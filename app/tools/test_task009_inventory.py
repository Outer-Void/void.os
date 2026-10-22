"""Source metadata must distinguish real image dimensions from vector dimensions."""
import tempfile
import unittest
from pathlib import Path
from task009_inventory import asset_record
from test_voidsys_qa import chunk
import struct
import zlib


class AssetInventoryTest(unittest.TestCase):
    def test_png_metadata_and_mapping_aliases(self):
        with tempfile.TemporaryDirectory() as directory:
            root = Path(directory)
            path = root / 'drawable-hdpi' / 'fixture.png'
            path.parent.mkdir()
            path.write_bytes(b'\x89PNG\r\n\x1a\n' + chunk(b'IHDR', struct.pack('>IIBBBBB', 1, 1, 8, 6, 0, 0, 0))
                             + chunk(b'IDAT', zlib.compress(b'\0\xff\xff\xff\xff')) + chunk(b'IEND', b''))
            asset = asset_record(path, root, {'fixture': ['org.example', 'org.example/.Main']})
            self.assertEqual([1, 1], asset['dimensions'])
            self.assertEqual('channel', asset['alpha'])
            self.assertEqual(2, asset['mapping_count'])
            self.assertEqual('pending', asset['visual_review_status'])
            self.assertEqual(64, len(asset['content_hash']))

    def test_adaptive_references_are_preserved_without_false_visual_approval(self):
        with tempfile.TemporaryDirectory() as directory:
            root = Path(directory)
            path = root / 'icon.xml'
            path.write_text('<adaptive-icon xmlns:android="http://schemas.android.com/apk/res/android">'
                            '<foreground android:drawable="@drawable/foreground"/>'
                            '<background android:drawable="@mipmap/background"/></adaptive-icon>')
            asset = asset_record(path, root, {})
            self.assertEqual('adaptive-icon', asset['kind'])
            self.assertEqual(['foreground', 'background'], asset['drawable_references'])
            self.assertEqual('metadata_only', asset['verification_status'])
