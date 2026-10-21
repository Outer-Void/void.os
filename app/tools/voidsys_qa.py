#!/usr/bin/env python3
"""Reproducible VOID.SYS static QA; no third-party dependencies. Pixel metrics are heuristics."""
import argparse
import os
from collections import Counter, defaultdict
import hashlib
import html
import json
from pathlib import Path
import re
import struct
import zlib
import xml.etree.ElementTree as ET

ROOT = Path(__file__).resolve().parents[2]
MAIN = ROOT / 'app/src/main'


def png_pixels(path):
    data = path.read_bytes()
    width, height, depth, color = struct.unpack('>IIBB', data[16:26])
    if depth != 8 or color not in (2, 6):
        return width, height, None
    compressed = bytearray()
    pos = 8
    while pos < len(data):
        size = struct.unpack('>I', data[pos:pos + 4])[0]
        if data[pos + 4:pos + 8] == b'IDAT':
            compressed.extend(data[pos + 8:pos + 8 + size])
        pos += size + 12
    raw = zlib.decompress(compressed)
    channels = 4 if color == 6 else 3
    stride = width * channels
    previous = bytearray(stride)
    pixels = []
    offset = 0
    for _ in range(height):
        kind = raw[offset]
        offset += 1
        row = bytearray(raw[offset:offset + stride])
        offset += stride
        for i in range(stride):
            a = row[i - channels] if i >= channels else 0
            b = previous[i]
            c = previous[i - channels] if i >= channels else 0
            if kind == 1:
                predictor = a
            elif kind == 2:
                predictor = b
            elif kind == 3:
                predictor = (a + b) // 2
            elif kind == 4:
                p = a + b - c
                ds = (abs(p - a), abs(p - b), abs(p - c))
                predictor = (a, b, c)[ds.index(min(ds))]
            elif kind == 0:
                predictor = 0
            else:
                raise ValueError(f'Unsupported PNG filter {kind}')
            row[i] = (row[i] + predictor) & 255
        pixels.extend(tuple(row[i:i + channels]) if channels == 4 else (*row[i:i + 3], 255)
                      for i in range(0, stride, channels))
        previous = row
    return width, height, pixels


def pixel_metrics(path):
    width, height, pixels = png_pixels(path)
    if pixels is None:
        return {'size': [width, height], 'pixel_analysis': 'unsupported PNG color/depth'}
    visible = [(i % width, i // width) for i, p in enumerate(pixels) if p[3] >= 32]
    # Bright glyph bounds separate pale linework from opaque near-black tile backgrounds.
    glyph = [(i % width, i // width) for i, p in enumerate(pixels)
             if p[3] >= 128 and max(p[:3]) >= 120]
    result = {'size': [width, height], 'transparent_fraction': round(sum(p[3] < 32 for p in pixels) / len(pixels), 4)}
    for name, coords in [('alpha', visible), ('bright_glyph', glyph)]:
        if coords:
            xs, ys = zip(*coords)
            result[name + '_bounds'] = [min(xs), min(ys), max(xs), max(ys)]
            result[name + '_centroid_offset'] = [round((sum(xs) / len(xs) + .5) / width - .5, 4),
                                                 round((sum(ys) / len(ys) + .5) / height - .5, 4)]
    result['bright_fraction'] = round(len(glyph) / len(pixels), 4)
    return result


def audit(output, resource_dump=None):
    output.mkdir(parents=True, exist_ok=True)
    duplicates = []
    def pairs(items):
        result = {}
        for key, value in items:
            if key in result:
                duplicates.append(key)
            result[key] = value
        return result
    mappings = json.loads((MAIN / 'assets/voidsys_icon_map.json').read_text(), object_pairs_hook=pairs)
    mappings = {k: v if isinstance(v, str) else v['drawable'] for k, v in mappings.items()}
    generic = set(json.loads((MAIN / 'assets/voidsys_generic_drawables.json').read_text()))
    resources = defaultdict(list)
    for path in sorted((MAIN / 'res').glob('drawable*/*')):
        if path.suffix in ('.png', '.xml', '.webp', '.jpg'):
            resources[path.stem].append(path)
    targets = set(mappings.values())
    keep = ET.parse(MAIN / 'res/raw/voidsys_keep.xml').getroot().attrib.get('{http://schemas.android.com/tools}keep', '')
    kept = set(re.findall(r'@drawable/([a-z0-9_]+)', keep))
    hashes = defaultdict(list)
    metrics = {}
    metric_cache = {}
    density_issues = []
    density_scale = {'mdpi': 1, 'hdpi': 1.5, 'xhdpi': 2, 'xxhdpi': 3, 'xxxhdpi': 4}
    for name, paths in sorted(resources.items()):
        normalized_sizes = set()
        for path in paths:
            if path.suffix != '.png':
                continue
            width, height = struct.unpack('>II', path.read_bytes()[16:24])
            scale = density_scale.get(path.parent.name.removeprefix('drawable-'))
            if scale:
                normalized_sizes.add((round(width / scale, 1), round(height / scale, 1)))
        if len(normalized_sizes) > 1:
            density_issues.append({'name': name, 'logical_sizes': sorted(normalized_sizes)})
        pngs = [p for p in paths if p.suffix == '.png']
        if pngs:
            preferred = next((p for p in pngs if p.parent.name == 'drawable-xxxhdpi'), pngs[-1])
            digest = hashlib.sha256(preferred.read_bytes()).hexdigest()
            hashes[digest].append(name)
            if digest not in metric_cache:
                metric_cache[digest] = pixel_metrics(preferred)
            metrics[name] = metric_cache[digest]
    source = '\n'.join(p.read_text() for p in MAIN.rglob('*.kt'))
    xml = '\n'.join(p.read_text() for p in (MAIN / 'res').rglob('*.xml'))
    unused = sorted(name for name in resources if name not in targets and
                    not re.search(r'(?:R\.drawable\.|@drawable/)' + re.escape(name) + r'\b', source + xml))
    # Normalized component aliases must agree. Package defaults may intentionally differ.
    components = defaultdict(set)
    for key, value in mappings.items():
        key = key.removeprefix('ComponentInfo{').removesuffix('}')
        if '/' in key:
            pkg, activity = key.split('/', 1)
            components[pkg + '/' + (pkg + activity if activity.startswith('.') else activity)].add(value)
    shrunk_missing = None
    if resource_dump is not None:
        dumped_resources = resource_dump.read_text()
        shrunk_missing = sorted(name for name in targets - generic
                                if not re.search(r'drawable/' + re.escape(name) + r'\b', dumped_resources))
    report = {
        'missing_shrunk_direct_targets': shrunk_missing,
        'map_entries': len(mappings), 'mapped_drawables': len(targets), 'resource_names': len(resources),
        'duplicate_json_keys': duplicates,
        'invalid_resource_names': sorted(n for n in targets | set(resources) if not re.fullmatch('[a-z][a-z0-9_]*', n)),
        'missing_mapped_drawables': sorted(targets - resources.keys()),
        'unkept_direct_targets': sorted(targets - generic - kept),
        'conflicting_component_aliases': {k: sorted(v) for k, v in components.items() if len(v) > 1},
        'identical_artwork_groups': [v for v in hashes.values() if len(v) > 1],
        'direct_artwork_collisions': [sorted(set(v) & (targets - generic)) for v in hashes.values()
                                      if len(set(v) & (targets - generic)) > 1],
        'generic_targets': len(targets & generic), 'unused_candidates': unused,
        'generic_list_only': sorted(generic - targets), 'density_inconsistencies': density_issues,
        'pixel_metrics': metrics,
        'limits': 'Source QA only. Identical art may be intentional. Centroids/brightness are review hints, not optical or WCAG certification. No runtime screenshots.'
    }
    (output / 'voidsys-inventory.json').write_text(json.dumps(report, indent=2) + '\n')
    tiles = []
    for name in sorted(targets):
        paths = resources.get(name, [])
        path = next((p for p in paths if p.parent.name == 'drawable-xxxhdpi' and p.suffix == '.png'), None)
        if path is None:
            vector_path = next((p for p in paths if p.suffix == '.xml'), None)
            if vector_path is not None:
                vector = ET.parse(vector_path).getroot()
                attr = '{http://schemas.android.com/apk/res/android}'
                if vector.tag == 'vector':
                    svg_paths = []
                    for element in vector.findall('path'):
                        attrs = {'d': element.get(attr + 'pathData', ''),
                                 'fill': element.get(attr + 'fillColor', 'none'),
                                 'stroke': element.get(attr + 'strokeColor', 'none'),
                                 'stroke-width': element.get(attr + 'strokeWidth', '0'),
                                 'stroke-linecap': element.get(attr + 'strokeLineCap', 'butt'),
                                 'stroke-linejoin': element.get(attr + 'strokeLineJoin', 'miter')}
                        if attrs['fill'].startswith('@'):
                            attrs['fill'] = 'none'
                        svg_paths.append('<path ' + ' '.join(k + '="' + html.escape(v, quote=True) + '"' for k, v in attrs.items()) + '/>')
                    svg = '<svg xmlns="http://www.w3.org/2000/svg" viewBox="0 0 ' + vector.get(attr + 'viewportWidth', '108') + ' ' + vector.get(attr + 'viewportHeight', '108') + '">' + ''.join(svg_paths) + '</svg>'
                    import urllib.parse
                    data = 'data:image/svg+xml,' + urllib.parse.quote(svg)
                    tiles.append(f'<figure><img width="48" height="48" src="{data}"><img width="60" height="60" src="{data}"><figcaption>{html.escape(name)}<br>DIRECT VECTOR</figcaption></figure>')
            continue
        data = html.escape(os.path.relpath(path, output), quote=True)
        label = html.escape(name)
        tiles.append(f'<figure><div><img width="48" height="48" src="{data}"><img width="60" height="60" src="{data}"></div><figcaption>{label}<br>{"GENERATED AT RUNTIME" if name in generic else "DIRECT ART"}</figcaption></figure>')
    (output / 'voidsys-contact-sheet.html').write_text('''<!doctype html><meta charset="utf-8"><title>VOID.SYS source art review</title>
<style>body{background:#0b0b0b;color:#d8d8d8;font:13px sans-serif}main{display:grid;grid-template-columns:repeat(auto-fill,minmax(180px,1fr));gap:12px}figure{margin:0;padding:16px;background:#191919;overflow-wrap:anywhere}img{margin:6px;image-rendering:auto}figcaption{margin-top:8px}</style>
<h1>VOID.SYS source artwork: 48 / 60 CSS pixels</h1><p>Source assets, not device screenshots. Generic PNGs are replaced by runtime category/monogram glyphs; review them on-device separately.</p><main>''' + '\n'.join(tiles) + '</main>\n')
    fatal = any(report[k] for k in ['duplicate_json_keys', 'invalid_resource_names', 'missing_mapped_drawables', 'unkept_direct_targets', 'conflicting_component_aliases', 'missing_shrunk_direct_targets'])
    print(json.dumps({k: (len(v) if isinstance(v, (dict, list)) else v) for k, v in report.items() if k != 'pixel_metrics'}, indent=2))
    return 1 if fatal else 0


if __name__ == '__main__':
    parser = argparse.ArgumentParser(description=__doc__)
    parser.add_argument('--resource-dump', type=Path, help='aapt2 dump resources from the actual shrunk release APK')
    parser.add_argument('--output', type=Path, default=ROOT / 'docs/audits/artifacts')
    args = parser.parse_args()
    raise SystemExit(audit(args.output, args.resource_dump))
