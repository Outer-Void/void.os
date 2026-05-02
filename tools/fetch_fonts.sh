#!/usr/bin/env bash
set -euo pipefail

ROOT="$(cd "$(dirname "${BASH_SOURCE[0]}")/.." && pwd)"
FONT_DIR="$ROOT/app/src/main/res/font"
TMP_DIR="$(mktemp -d)"
trap 'rm -rf "$TMP_DIR"' EXIT

mkdir -p "$FONT_DIR"

fetch() {
  local url="$1"
  local out="$2"
  if [[ -s "$FONT_DIR/$out" ]]; then
    return
  fi
  echo "Fetching $out"
  curl -fL --retry 3 --retry-delay 2 "$url" -o "$FONT_DIR/$out"
}

fetch_google() {
  local family_dir="$1"
  local name_regex="$2"
  local out="$3"
  if [[ -s "$FONT_DIR/$out" ]]; then
    return
  fi
  local api="https://api.github.com/repos/google/fonts/contents/$family_dir?ref=main"
  local url
  url="$(curl -fsSL "$api" | python3 -c '
import json, re, sys
pattern = re.compile(sys.argv[1])
for item in json.load(sys.stdin):
    name = item.get("name", "")
    if pattern.fullmatch(name):
        print(item["download_url"])
        break
' "$name_regex")"
  if [[ -z "$url" ]]; then
    echo "Missing $name_regex in $family_dir" >&2
    exit 1
  fi
  fetch "$url" "$out"
}

fetch_zip_font() {
  local url="$1"
  local pattern="$2"
  local out="$3"
  if [[ -s "$FONT_DIR/$out" ]]; then
    return
  fi
  local zip="$TMP_DIR/${out%.ttf}.zip"
  local extract="$TMP_DIR/${out%.ttf}"
  mkdir -p "$extract"
  echo "Fetching $out"
  curl -fL --retry 3 --retry-delay 2 "$url" -o "$zip"
  unzip -q "$zip" -d "$extract"
  local source
  source="$(find "$extract" -type f -name "$pattern" | sort | head -n 1)"
  if [[ -z "$source" ]]; then
    echo "Missing $pattern in $url" >&2
    exit 1
  fi
  cp "$source" "$FONT_DIR/$out"
}

NERD_RELEASE="https://github.com/ryanoasis/nerd-fonts/releases/download/v3.3.0"

# Core AXIOM / Google Fonts
fetch_google "ofl/jetbrainsmono" "JetBrainsMono\\[wght\\]\\.ttf" "jetbrains_mono.ttf"
fetch_google "ofl/spacemono" "SpaceMono-Regular\\.ttf" "space_mono.ttf"
fetch_google "ofl/ibmplexmono" "IBMPlexMono-Regular\\.ttf" "ibm_plex_mono.ttf"
fetch_google "ofl/sourcecodepro" "SourceCodePro\\[wght\\]\\.ttf" "source_code_pro.ttf"
fetch_google "ofl/firacode" "FiraCode\\[wght\\]\\.ttf" "fira_code.ttf"
fetch_google "ofl/robotomono" "RobotoMono\\[wght\\]\\.ttf" "roboto_mono.ttf"
fetch_zip_font "$NERD_RELEASE/UbuntuMono.zip" "UbuntuMonoNerdFont-Regular.ttf" "ubuntu_mono.ttf"
fetch_google "ofl/inconsolata" "Inconsolata\\[wdth,wght\\]\\.ttf" "inconsolata.ttf"

# Open-source fonts not hosted in google/fonts under the exact family.
fetch_zip_font "$NERD_RELEASE/Hack.zip" "HackNerdFont-Regular.ttf" "hack.ttf"
fetch_zip_font "$NERD_RELEASE/CascadiaCode.zip" "CaskaydiaCoveNerdFont-Regular.ttf" "cascadia_code.ttf"
fetch_zip_font "$NERD_RELEASE/Monaspace.zip" "MonaspiceRnNerdFont-Regular.otf" "monaspace.otf"

# Nerd Font variants from official Nerd Fonts releases.
fetch_zip_font "$NERD_RELEASE/JetBrainsMono.zip" "JetBrainsMonoNerdFont-Regular.ttf" "jetbrainsmono_nerd.ttf"
fetch_zip_font "$NERD_RELEASE/FiraCode.zip" "FiraCodeNerdFont-Regular.ttf" "firacode_nerd.ttf"
fetch_zip_font "$NERD_RELEASE/Hack.zip" "HackNerdFont-Regular.ttf" "hack_nerd.ttf"
fetch_zip_font "$NERD_RELEASE/Meslo.zip" "MesloLGSNerdFont-Regular.ttf" "meslolgs_nerd.ttf"
fetch_zip_font "$NERD_RELEASE/CascadiaCode.zip" "CaskaydiaCoveNerdFont-Regular.ttf" "caskaydiacove_nerd.ttf"
fetch_zip_font "$NERD_RELEASE/Mononoki.zip" "MononokiNerdFont-Regular.ttf" "mononoki_nerd.ttf"
fetch_zip_font "$NERD_RELEASE/Iosevka.zip" "IosevkaNerdFont-Regular.ttf" "iosevka_nerd.ttf"
fetch_zip_font "$NERD_RELEASE/VictorMono.zip" "VictorMonoNerdFont-Regular.ttf" "victormono_nerd.ttf"
fetch_zip_font "$NERD_RELEASE/SourceCodePro.zip" "SauceCodeProNerdFont-Regular.ttf" "saucecodepro_nerd.ttf"
fetch_zip_font "$NERD_RELEASE/UbuntuMono.zip" "UbuntuMonoNerdFont-Regular.ttf" "ubuntumono_nerd.ttf"

# Popular sans / serif / display from Google Fonts.
fetch_google "ofl/roboto" "Roboto\\[wdth,wght\\]\\.ttf" "roboto.ttf"
fetch_google "ofl/inter" "Inter\\[opsz,wght\\]\\.ttf" "inter.ttf"
fetch_google "ofl/opensans" "OpenSans\\[wdth,wght\\]\\.ttf" "open_sans.ttf"
fetch_google "ofl/notosans" "NotoSans\\[wdth,wght\\]\\.ttf" "noto_sans.ttf"
fetch_google "ofl/montserrat" "Montserrat\\[wght\\]\\.ttf" "montserrat.ttf"
fetch_google "ofl/poppins" "Poppins-Regular\\.ttf" "poppins.ttf"
fetch_google "ofl/lato" "Lato-Regular\\.ttf" "lato.ttf"
fetch_google "ofl/sourcesans3" "SourceSans3\\[wght\\]\\.ttf" "source_sans_3.ttf"
fetch_google "ofl/worksans" "WorkSans\\[wght\\]\\.ttf" "work_sans.ttf"
fetch_google "ofl/manrope" "Manrope\\[wght\\]\\.ttf" "manrope.ttf"
fetch_google "ofl/notoserif" "NotoSerif\\[wdth,wght\\]\\.ttf" "noto_serif.ttf"
fetch_google "ofl/merriweather" "Merriweather\\[opsz,wdth,wght\\]\\.ttf" "merriweather.ttf"
fetch_google "ofl/playfairdisplay" "PlayfairDisplay\\[wght\\]\\.ttf" "playfair_display.ttf"
fetch_google "ofl/librebaskerville" "LibreBaskerville\\[wght\\]\\.ttf" "libre_baskerville.ttf"
fetch_google "ofl/sourceserif4" "SourceSerif4\\[opsz,wght\\]\\.ttf" "source_serif_4.ttf"
fetch_google "ofl/orbitron" "Orbitron\\[wght\\]\\.ttf" "orbitron.ttf"
fetch_google "ofl/sharetechmono" "ShareTechMono-Regular\\.ttf" "share_tech_mono.ttf"
fetch_google "ofl/exo2" "Exo2\\[wght\\]\\.ttf" "exo_2.ttf"
fetch_google "ofl/rajdhani" "Rajdhani-Regular\\.ttf" "rajdhani.ttf"
fetch_google "ofl/oxanium" "Oxanium\\[wght\\]\\.ttf" "oxanium.ttf"

echo "Font fetch complete: $(find "$FONT_DIR" -maxdepth 1 \( -name '*.ttf' -o -name '*.otf' \) | wc -l) files"
