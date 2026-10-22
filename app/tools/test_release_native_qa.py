import io
import struct
import tempfile
import unittest
import zipfile
from pathlib import Path
from unittest.mock import patch
import release_native_qa as qa


def elf(alignment=16384, file_offset=0, virtual=0, machine=183):
    data = bytearray(120)
    data[:6] = b'\x7fELF\x02\x01'
    struct.pack_into('<H', data, 18, machine)
    struct.pack_into('<Q', data, 32, 64)
    struct.pack_into('<HH', data, 54, 56, 1)
    struct.pack_into('<IIQQQQQQ', data, 64, 1, 5, file_offset, virtual, 0, 1, 1, alignment)
    return bytes(data)


class NativeAuditTest(unittest.TestCase):
    def test_load_geometry_distinguishes_alignment_from_congruence(self):
        self.assertTrue(qa.aligned(qa.elf_info(elf())))
        self.assertFalse(qa.aligned(qa.elf_info(elf(4096))))
        self.assertFalse(qa.aligned(qa.elf_info(elf(20000))))
        self.assertFalse(qa.aligned(qa.elf_info(elf(16384, 0, 4096))))
        self.assertEqual('aarch64', qa.elf_info(elf())['architecture'])

    def test_missing_native_inputs_do_not_vacuously_pass(self):
        with tempfile.TemporaryDirectory() as directory, patch.object(qa, 'ROOT', Path(directory)):
            self.assertFalse(qa.audit(native_dir=Path(directory))['passes'])

    def test_apk_misalignment_fails_even_when_source_is_aligned(self):
        with tempfile.TemporaryDirectory() as directory, patch.object(qa, 'ROOT', Path(directory)):
            root = Path(directory)
            native = root / 'natives'
            for abi, machine in [('arm64-v8a', 183), ('x86_64', 62)]:
                path = native / abi / 'libfixture.so'
                path.parent.mkdir(parents=True)
                path.write_bytes(elf(machine=machine))
            apk = root / 'fixture.apk'
            with zipfile.ZipFile(apk, 'w') as archive:
                for path in native.rglob('*.so'):
                    archive.writestr('lib/' + str(path.relative_to(native)), path.read_bytes())
            report = qa.audit(apk, native)
            self.assertTrue(report['passes_source_16kb_elf_alignment'])
            self.assertFalse(report['passes_apk_16kb_alignment'])
            self.assertFalse(report['passes'])

    def test_truncated_or_invalid_elf_is_rejected(self):
        with self.assertRaises(ValueError): qa.elf_info(b'not elf')
        with self.assertRaises(ValueError): qa.elf_info(elf()[:100])
