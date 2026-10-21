#!/usr/bin/env python3
"""Render vector source previews with local librsvg/cairo; no Android/device claims."""
import argparse
import ctypes as C
import html
from pathlib import Path
import xml.etree.ElementTree as ET


def render(output):
    root = Path(__file__).resolve().parents[2]
    names = ['voidsys_google_calendar', 'voidsys_google_home', 'voidsys_notion']
    tiles = []
    android = '{http://schemas.android.com/apk/res/android}'
    for index, name in enumerate(names):
        vector = ET.parse(root / 'app/src/main/res/drawable' / (name + '.xml')).getroot()
        paths = []
        for path in vector.findall('path'):
            attrs = {'d': path.get(android + 'pathData'), 'fill': path.get(android + 'fillColor', 'none'),
                     'stroke': path.get(android + 'strokeColor', 'none'), 'stroke-width': path.get(android + 'strokeWidth', '0'),
                     'stroke-linecap': path.get(android + 'strokeLineCap', 'butt'), 'stroke-linejoin': path.get(android + 'strokeLineJoin', 'miter')}
            attrs = {k: ('none' if v.startswith('@') else v) for k, v in attrs.items()}
            paths.append('<path ' + ' '.join(f'{k}="{html.escape(v)}"' for k, v in attrs.items()) + '/>')
        for offset, size in [(16, 48), (84, 60)]:
            tiles.append(f'<svg x="{index * 180 + offset}" y="30" width="{size}" height="{size}" viewBox="0 0 108 108">' + ''.join(paths) + '</svg>')
        tiles.append(f'<text x="{index * 180 + 16}" y="120" fill="#D8D8D8" font-size="12">{name.removeprefix("voidsys_")}</text>')
    svg = ('<svg xmlns="http://www.w3.org/2000/svg" width="540" height="160"><rect width="540" height="160" fill="#0B0B0B"/>' + ''.join(tiles) + '</svg>').encode()
    rsvg = C.CDLL('librsvg-2.so.2')
    cairo = C.CDLL('libcairo.so.2')
    gobject = C.CDLL('libgobject-2.0.so.0')
    rsvg.rsvg_handle_new_from_data.argtypes = [C.c_char_p, C.c_size_t, C.c_void_p]
    rsvg.rsvg_handle_new_from_data.restype = C.c_void_p
    rsvg.rsvg_handle_render_cairo.argtypes = [C.c_void_p, C.c_void_p]
    rsvg.rsvg_handle_render_cairo.restype = C.c_int
    cairo.cairo_image_surface_create.argtypes = [C.c_int, C.c_int, C.c_int]
    cairo.cairo_image_surface_create.restype = C.c_void_p
    cairo.cairo_create.argtypes = [C.c_void_p]
    cairo.cairo_create.restype = C.c_void_p
    cairo.cairo_surface_write_to_png.argtypes = [C.c_void_p, C.c_char_p]
    cairo.cairo_surface_write_to_png.restype = C.c_int
    cairo.cairo_destroy.argtypes = [C.c_void_p]
    cairo.cairo_surface_destroy.argtypes = [C.c_void_p]
    gobject.g_object_unref.argtypes = [C.c_void_p]
    handle = rsvg.rsvg_handle_new_from_data(svg, len(svg), None)
    if not handle:
        raise RuntimeError('SVG source could not be loaded')
    surface = cairo.cairo_image_surface_create(0, 540, 160)
    context = cairo.cairo_create(surface)
    try:
        if not rsvg.rsvg_handle_render_cairo(handle, context):
            raise RuntimeError('SVG rendering failed')
        output.parent.mkdir(parents=True, exist_ok=True)
        if cairo.cairo_surface_write_to_png(surface, str(output).encode()):
            raise RuntimeError('PNG write failed')
    finally:
        cairo.cairo_destroy(context)
        cairo.cairo_surface_destroy(surface)
        gobject.g_object_unref(handle)


if __name__ == '__main__':
    parser = argparse.ArgumentParser(description=__doc__)
    parser.add_argument('output', type=Path)
    render(parser.parse_args().output)
