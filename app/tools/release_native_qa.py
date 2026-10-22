#!/usr/bin/env python3
"""Inspect ELF geometry and APK ZIP alignment without executing foreign binaries."""
import argparse
import hashlib
import json
from pathlib import Path
import struct
import zipfile

ROOT = Path(__file__).resolve().parents[2]


def elf_info(data):
    if data[:4] != b'\x7fELF' or data[4] not in (1, 2) or data[5] not in (1, 2):
        raise ValueError('Unsupported ELF')
    endian = '<' if data[5] == 1 else '>'
    is64 = data[4] == 2
    machine = struct.unpack_from(endian + 'H', data, 18)[0]
    phoff = struct.unpack_from(endian + ('Q' if is64 else 'I'), data, 32 if is64 else 28)[0]
    size, count = struct.unpack_from(endian + 'HH', data, 54 if is64 else 42)
    if size < (56 if is64 else 32) or not count or phoff + size * count > len(data):
        raise ValueError('Invalid ELF program headers')
    segments = []
    for i in range(count):
        offset = phoff + size * i
        if struct.unpack_from(endian + 'I', data, offset)[0] == 1:
            file_offset, virtual = struct.unpack_from(endian + ('QQ' if is64 else 'II'), data, offset + (8 if is64 else 4))
            alignment = struct.unpack_from(endian + ('Q' if is64 else 'I'), data, offset + (48 if is64 else 28))[0]
            segments.append({'offset': file_offset, 'virtual_address': virtual, 'alignment': alignment,
                             'congruent_16kb': file_offset % 16384 == virtual % 16384})
    return {'bits': 64 if is64 else 32, 'machine': machine,
            'architecture': {183: 'aarch64', 62: 'x86_64', 40: 'arm', 3: 'x86'}.get(machine, 'unknown'),
            'load_segments': segments, 'sha256': hashlib.sha256(data).hexdigest()}


def elf_load_alignments(data):
    return [s['alignment'] for s in elf_info(data)['load_segments']]


def aligned(info):
    return bool(info['load_segments']) and all(s['alignment'] >= 16384 and s['alignment'] & (s['alignment'] - 1) == 0 and s['congruent_16kb'] for s in info['load_segments'])


def audit(apk=None, native_dir=None):
    native_dir = native_dir or ROOT / 'app/build/generated/gdxNatives'
    report = {'native_origin': 'com.badlogicgames.gdx:gdx-platform; version in app/build.gradle.kts',
              'source_libraries': {}, 'apk': None}
    paths = sorted(set(native_dir.rglob('*.so')) | set((ROOT / 'app/src/main/jniLibs').rglob('*.so')))
    for path in paths:
        try:
            key = str(path.relative_to(ROOT))
        except ValueError:
            key = str(path)
        report['source_libraries'][key] = elf_info(path.read_bytes())
    required = {'aarch64', 'x86_64'}
    source64 = [info for info in report['source_libraries'].values() if info['bits'] == 64]
    report['passes_source_16kb_elf_alignment'] = (required <= {i['architecture'] for i in source64}
                                                 and all(aligned(i) for i in source64))
    report['passes_apk_16kb_alignment'] = None
    if apk:
        packaged = {}
        with zipfile.ZipFile(apk) as archive, apk.open('rb') as raw:
            for entry in archive.infolist():
                if not entry.filename.endswith('.so'):
                    continue
                raw.seek(entry.header_offset + 26)
                name, extra = struct.unpack('<HH', raw.read(4))
                start = entry.header_offset + 30 + name + extra
                packaged[entry.filename] = {'elf': elf_info(archive.read(entry)),
                                           'stored': entry.compress_type == zipfile.ZIP_STORED,
                                           'data_offset': start, 'zip_16kb_aligned': start % 16384 == 0}
        report['apk'] = {'name': apk.name, 'bytes': apk.stat().st_size, 'libraries': packaged}
        packaged64 = [p for p in packaged.values() if p['elf']['bits'] == 64]
        report['passes_apk_16kb_alignment'] = (required <= {p['elf']['architecture'] for p in packaged64}
            and all(aligned(p['elf']) and (not p['stored'] or p['zip_16kb_aligned']) for p in packaged64))
    report['passes'] = report['passes_source_16kb_elf_alignment'] and report['passes_apk_16kb_alignment'] is not False
    return report


if __name__ == '__main__':
    parser = argparse.ArgumentParser(description=__doc__)
    parser.add_argument('--apk', type=Path)
    parser.add_argument('--native-dir', type=Path)
    args = parser.parse_args()
    result = audit(args.apk, args.native_dir)
    print(json.dumps(result, indent=2))
    raise SystemExit(0 if result['passes'] else 1)
