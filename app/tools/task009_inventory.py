#!/usr/bin/env python3
"""Reconcile tracked live files and every drawable/mipmap variant without claiming visual QA."""
import hashlib
import json
import re
import struct
import subprocess
from collections import Counter, defaultdict
from pathlib import Path
import xml.etree.ElementTree as ET

ROOT = Path(__file__).resolve().parents[2]
ANDROID = '{http://schemas.android.com/apk/res/android}'


def asset_record(path, root, mappings):
    data = path.read_bytes()
    record = {
        'identifier': str(path.relative_to(root)), 'asset_path': str(path.relative_to(root)),
        'resource_name': path.stem, 'density': path.parent.name,
        'dimensions': None, 'alpha': 'unknown', 'kind': path.suffix[1:],
        'content_hash': hashlib.sha256(data).hexdigest(),
        'associated_packages': sorted(mappings.get(path.stem, [])),
        'mapping_count': len(mappings.get(path.stem, [])),
        'existing_condition': 'bundled source; not individually visually reviewed in Task 009',
        'required_improvement': 'individual recognition, optical size, contrast and runtime review',
        'changes_made': [], 'tests_performed': ['source metadata/hash extraction'],
        'qa_status': 'unverified', 'upgrade_status': 'not_changed_in_task_009',
        'verification_status': 'metadata_only', 'visual_review_status': 'pending',
        'remaining_problems': ['individual artwork acceptance pending'], 'completion_status': 'pending'
    }
    if path.suffix == '.png':
        record['dimensions'] = list(struct.unpack('>II', data[16:24]))
        record['alpha'] = 'channel' if data[25] in (4, 6) else 'tRNS' if b'tRNS' in data else 'none'
    elif path.suffix == '.xml':
        element = ET.fromstring(data)
        record['kind'] = element.tag
        record['dimensions'] = [element.get(ANDROID + key) for key in ('width', 'height')]
        record['alpha'] = 'defined by drawable composition; runtime inspection pending'
        record['drawable_references'] = re.findall(rb'@(?:drawable|mipmap)/([a-z0-9_]+)', data)
        record['drawable_references'] = [x.decode() for x in record['drawable_references']]
    return record


def main():
    paths = subprocess.check_output(['git', 'ls-files', '-z'], cwd=ROOT).decode().split('\0')
    paths = [p for p in paths if p]
    mappings = json.loads((ROOT / 'app/src/main/assets/voidsys_icon_map.json').read_text())
    generic = set(json.loads((ROOT / 'app/src/main/assets/voidsys_generic_drawables.json').read_text()))
    by_resource = defaultdict(list)
    for key, value in mappings.items():
        by_resource[value if isinstance(value, str) else value['drawable']].append(key)
    assets = [asset_record(p, ROOT, by_resource) for p in sorted((ROOT / 'app/src/main/res').glob('*/*'))
              if p.suffix in ('.png', '.xml', '.webp', '.jpg') and p.parent.name.startswith(('drawable', 'mipmap'))]
    reference_source = '\n'.join(p.read_text() for p in (ROOT / 'app/src/main').rglob('*') if p.suffix in ('.kt', '.xml'))
    groups = defaultdict(list)
    for asset in assets:
        name = asset['resource_name']
        asset['usage'] = 'mapped' if asset['mapping_count'] else 'explicit_reference' if re.search(r'(?:R\.(?:drawable|mipmap)\.|@(?:drawable|mipmap)/)' + re.escape(name) + r'\b', reference_source) else 'unused_candidate_requires_dynamic_lookup_review'
        asset['runtime_rendering'] = 'generated_category_and_identity' if name in generic else 'bundled_art'
        groups[asset['content_hash']].append(asset['identifier'])
    for asset in assets:
        duplicates = groups[asset['content_hash']]
        asset['duplicate_group'] = asset['content_hash'] if len(duplicates) > 1 else None
    source = []
    for relative in paths:
        p = ROOT / relative
        if p.suffix not in ('.kt', '.java'):
            continue
        text = p.read_text()
        source.append({'path': relative, 'sha256': hashlib.sha256(p.read_bytes()).hexdigest(),
                       'declarations': re.findall(r'\b(?:class|object|enum class|interface|fun)\s+(\w+)', text),
                       'control_sites': [{'line': i, 'text': line.strip()} for i, line in enumerate(text.splitlines(), 1)
                                         if re.search(r'\bonClick\b|\bclickable\b|\bcombinedClickable\b|Bounds\.contains|isKeyJustPressed', line)],
                       'review_status': 'source_indexed; behavioral acceptance pending'})
    launcher_state = (ROOT / 'app/src/main/java/com/outervoid/axiom/ui/state/LauncherState.kt').read_text()
    routes = re.findall(r'^    (\w+),?$', launcher_state, re.M)
    games = sorted(p.stem for p in (ROOT / 'app/src/main/java/com/outervoid/voidgame/screens').glob('*.kt'))
    features = []
    for name in routes + games + ['VOID.SYS', 'Widgets', 'PackageInventory', 'ReleaseAuthorization', 'Native16KB', 'Audio']:
        features.append({'identifier': name, 'existing_condition': 'implemented but unverified',
                         'required_improvement': 'source review and relevant automated/device acceptance',
                         'changes_made': [], 'tests_performed': [], 'visual_review_status': 'pending',
                         'remaining_problems': ['Task 009 acceptance not established'], 'completion_status': 'unverified'})
    for feature in features:
        if feature['identifier'] == 'ReleaseAuthorization':
            feature.update(existing_condition='partial; production verification missing; fail closed', completion_status='blocked')
        if feature['identifier'] == 'Native16KB':
            feature.update(existing_condition='broken: source 64-bit ELF LOAD alignment 4096 (Task 008)', completion_status='blocked')
        if feature['identifier'] == 'Audio':
            feature.update(existing_condition='not implemented; no game audio asset/system found', completion_status='missing')
    inventory = {'baseline': 'e78e28f', 'tracked_file_count': len(paths), 'tracked_files': paths,
                 'source_files': source, 'launcher_routes': routes, 'game_screens': games,
                 'asset_counts': dict(Counter(a['kind'] for a in assets)),
                 'limits': 'Declaration/control index is discovery, not exhaustive behavior verification. Assets include all densities, unused candidates and launcher mipmaps.'}
    for path, data in [('reports/voidsys/icon-inventory.json', {'assets': assets, 'duplicate_groups': {key: values for key, values in groups.items() if len(values) > 1}}),
                       ('reports/task-009/repository-inventory.json', inventory),
                       ('reports/task-009/completion.json', {'features': features})]:
        output = ROOT / path
        output.parent.mkdir(parents=True, exist_ok=True)
        if path.endswith('completion.json') and output.exists():
            existing = {x['identifier']: x for x in json.loads(output.read_text())['features']}
            data['features'] = [existing.get(x['identifier'], x) for x in features]
        if path.endswith('icon-inventory.json'):
            output.write_text('{\n  "assets": [\n' + ',\n'.join(json.dumps(a, sort_keys=True) for a in assets) + '\n  ],\n  "duplicate_groups": ' + json.dumps(data['duplicate_groups'], sort_keys=True) + '\n}\n')
        else:
            output.write_text(json.dumps(data, indent=2) + '\n')
    print(json.dumps({'tracked_files': len(paths), 'source_files': len(source), 'assets': len(assets),
                      'asset_counts': inventory['asset_counts'], 'launcher_routes': len(routes), 'game_screens': len(games)}))


if __name__ == '__main__':
    main()
