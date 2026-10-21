"""Small PNG fixtures validate filter reversal and optical metric bounds."""
import struct
import tempfile
import unittest
import zlib
from pathlib import Path
from voidsys_qa import png_pixels, pixel_metrics


def chunk(kind, data):
    return struct.pack('>I', len(data)) + kind + data + struct.pack('>I', zlib.crc32(kind + data))


class PngMetricsTest(unittest.TestCase):
    def test_filters_preserve_transparency_and_glyph_centroid(self):
        # Two white RGBA pixels separated by a transparent pixel, centered in a 3x1 image.
        row = bytes([255, 255, 255, 255, 0, 0, 0, 0, 255, 255, 255, 255])
        for kind in range(5):
            filtered = bytearray()
            for i, value in enumerate(row):
                left = row[i - 4] if i >= 4 else 0
                predictor = left if kind in (1, 4) else left // 2 if kind == 3 else 0
                filtered.append((value - predictor) & 255)
            png = b'\x89PNG\r\n\x1a\n' + chunk(b'IHDR', struct.pack('>IIBBBBB', 3, 1, 8, 6, 0, 0, 0))
            png += chunk(b'IDAT', zlib.compress(bytes([kind]) + filtered)) + chunk(b'IEND', b'')
            with tempfile.TemporaryDirectory() as directory:
                path = Path(directory) / 'fixture.png'
                path.write_bytes(png)
                self.assertEqual((255, 255, 255, 255), png_pixels(path)[2][0])
                self.assertEqual((0, 0, 0, 0), png_pixels(path)[2][1])
                self.assertEqual([0, 0, 2, 0], pixel_metrics(path)['bright_glyph_bounds'])
                self.assertAlmostEqual(.3333, pixel_metrics(path)['transparent_fraction'])


if __name__ == '__main__':
    unittest.main()
