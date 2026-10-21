#!/usr/bin/env python3
"""Inspect ELF LOAD and packaged ZIP alignment without executing foreign binaries."""
import argparse
import json
from pathlib import Path
import struct
import zipfile

ROOT = Path(__file__).resolve().parents[2]


def elf_load_alignments(data):
    if data[:4] != b'\x7fELF':
        raise ValueError('Not ELF')
    endian = '<' if data[5] == 1 else '>'
    is64 = data[4] == 2
    phoff = struct.unpack_from(endian + ('Q' if is64 else 'I'), data, 32 if is64 else 28)[0]
    size, count = struct.unpack_from(endian + 'HH', data, 54 if is64 else 42)
    aligns = []
    for i in range(count):
        offset = phoff + size * i
        if struct.unpack_from(endian + 'I', data, offset)[0] == 1:
            aligns.append(struct.unpack_from(endian + ('Q' if is64 else 'I'), data, offset + (48 if is64 else 28))[0])
    return aligns


def audit(apk=None):
    report = {'source_libraries': {}, 'apk': None}
    for path in sorted((ROOT / 'app/src/main/jniLibs').rglob('*.so')):
        report['source_libraries'][str(path.relative_to(ROOT))] = elf_load_alignments(path.read_bytes())
    if apk:
        packaged = {}
        with zipfile.ZipFile(apk) as archive, apk.open('rb') as raw:
            for info in archive.infolist():
                if not info.filename.endswith('.so'):
                    continue
                raw.seek(info.header_offset + 26)
                name, extra = struct.unpack('<HH', raw.read(4))
                start = info.header_offset + 30 + name + extra
                packaged[info.filename] = {'elf_load_alignments': elf_load_alignments(archive.read(info)),
                                           'stored': info.compress_type == zipfile.ZIP_STORED,
                                           'data_offset': start,
                                           'zip_16kb_aligned': start % 16384 == 0}
        report['apk'] = {'name': apk.name, 'bytes': apk.stat().st_size, 'libraries': packaged}
    report['passes_source_16kb_elf_alignment'] = all(
        values and min(values) >= 16384 for path, values in report['source_libraries'].items()
        if '/arm64-v8a/' in path or '/x86_64/' in path)
    return report


if __name__ == '__main__':
    parser = argparse.ArgumentParser(description=__doc__)
    parser.add_argument('--apk', type=Path)
    args = parser.parse_args()
    result = audit(args.apk)
    print(json.dumps(result, indent=2))
    raise SystemExit(0 if result['passes_source_16kb_elf_alignment'] else 1)
