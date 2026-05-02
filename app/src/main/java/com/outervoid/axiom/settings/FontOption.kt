package com.outervoid.axiom.settings

import androidx.compose.ui.text.font.Font
import androidx.compose.ui.text.font.FontFamily
import com.outervoid.axiom.R

data class FontOption(
    val key: String,
    val displayName: String,
    val category: FontCategory,
    val fontResId: Int
) {
    fun fontFamily(): FontFamily = FontFamily(Font(fontResId))
}

enum class FontCategory {
    CoreAxiom,
    NerdFont,
    Sans,
    Serif,
    Display
}

object FontOptions {
    val All = listOf(
        FontOption("jetbrains_mono", "JetBrains Mono", FontCategory.CoreAxiom, R.font.jetbrains_mono),
        FontOption("space_mono", "Space Mono", FontCategory.CoreAxiom, R.font.space_mono),
        FontOption("ibm_plex_mono", "IBM Plex Mono", FontCategory.CoreAxiom, R.font.ibm_plex_mono),
        FontOption("source_code_pro", "Source Code Pro", FontCategory.CoreAxiom, R.font.source_code_pro),
        FontOption("fira_code", "Fira Code", FontCategory.CoreAxiom, R.font.fira_code),
        FontOption("roboto_mono", "Roboto Mono", FontCategory.CoreAxiom, R.font.roboto_mono),
        FontOption("ubuntu_mono", "Ubuntu Mono", FontCategory.CoreAxiom, R.font.ubuntu_mono),
        FontOption("hack", "Hack", FontCategory.CoreAxiom, R.font.hack),
        FontOption("cascadia_code", "Cascadia Code", FontCategory.CoreAxiom, R.font.cascadia_code),
        FontOption("monaspace", "Monaspace", FontCategory.CoreAxiom, R.font.monaspace),
        FontOption("inconsolata", "Inconsolata", FontCategory.CoreAxiom, R.font.inconsolata),
        FontOption("jetbrainsmono_nerd", "JetBrainsMono Nerd Font", FontCategory.NerdFont, R.font.jetbrainsmono_nerd),
        FontOption("firacode_nerd", "FiraCode Nerd Font", FontCategory.NerdFont, R.font.firacode_nerd),
        FontOption("hack_nerd", "Hack Nerd Font", FontCategory.NerdFont, R.font.hack_nerd),
        FontOption("meslolgs_nerd", "MesloLGS Nerd Font", FontCategory.NerdFont, R.font.meslolgs_nerd),
        FontOption("caskaydiacove_nerd", "CaskaydiaCove Nerd Font", FontCategory.NerdFont, R.font.caskaydiacove_nerd),
        FontOption("mononoki_nerd", "Mononoki Nerd Font", FontCategory.NerdFont, R.font.mononoki_nerd),
        FontOption("iosevka_nerd", "Iosevka Nerd Font", FontCategory.NerdFont, R.font.iosevka_nerd),
        FontOption("victormono_nerd", "VictorMono Nerd Font", FontCategory.NerdFont, R.font.victormono_nerd),
        FontOption("saucecodepro_nerd", "SauceCodePro Nerd Font", FontCategory.NerdFont, R.font.saucecodepro_nerd),
        FontOption("ubuntumono_nerd", "UbuntuMono Nerd Font", FontCategory.NerdFont, R.font.ubuntumono_nerd),
        FontOption("roboto", "Roboto", FontCategory.Sans, R.font.roboto),
        FontOption("inter", "Inter", FontCategory.Sans, R.font.inter),
        FontOption("open_sans", "Open Sans", FontCategory.Sans, R.font.open_sans),
        FontOption("noto_sans", "Noto Sans", FontCategory.Sans, R.font.noto_sans),
        FontOption("montserrat", "Montserrat", FontCategory.Sans, R.font.montserrat),
        FontOption("poppins", "Poppins", FontCategory.Sans, R.font.poppins),
        FontOption("lato", "Lato", FontCategory.Sans, R.font.lato),
        FontOption("source_sans_3", "Source Sans 3", FontCategory.Sans, R.font.source_sans_3),
        FontOption("work_sans", "Work Sans", FontCategory.Sans, R.font.work_sans),
        FontOption("manrope", "Manrope", FontCategory.Sans, R.font.manrope),
        FontOption("noto_serif", "Noto Serif", FontCategory.Serif, R.font.noto_serif),
        FontOption("merriweather", "Merriweather", FontCategory.Serif, R.font.merriweather),
        FontOption("playfair_display", "Playfair Display", FontCategory.Serif, R.font.playfair_display),
        FontOption("libre_baskerville", "Libre Baskerville", FontCategory.Serif, R.font.libre_baskerville),
        FontOption("source_serif_4", "Source Serif 4", FontCategory.Serif, R.font.source_serif_4),
        FontOption("orbitron", "Orbitron", FontCategory.Display, R.font.orbitron),
        FontOption("share_tech_mono", "Share Tech Mono", FontCategory.Display, R.font.share_tech_mono),
        FontOption("exo_2", "Exo 2", FontCategory.Display, R.font.exo_2),
        FontOption("rajdhani", "Rajdhani", FontCategory.Display, R.font.rajdhani),
        FontOption("oxanium", "Oxanium", FontCategory.Display, R.font.oxanium)
    )

    fun byKey(key: String): FontOption = All.firstOrNull { it.key == key } ?: All.first()
}
