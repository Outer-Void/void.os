package com.outervoid.axiom.settings

import android.graphics.Typeface
import androidx.compose.ui.text.font.FontFamily

data class FontOption(
    val key: String,
    val displayName: String,
    val systemFamilyName: String,
    val category: FontCategory
) {
    fun fontFamily(): FontFamily {
        val familyName = if (systemFamilyName.isBlank()) "monospace" else systemFamilyName
        return FontFamily(Typeface.create(familyName, Typeface.NORMAL) ?: Typeface.MONOSPACE)
    }
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
        FontOption("jetbrains_mono", "JetBrains Mono", "JetBrains Mono", FontCategory.CoreAxiom),
        FontOption("space_mono", "Space Mono", "Space Mono", FontCategory.CoreAxiom),
        FontOption("ibm_plex_mono", "IBM Plex Mono", "IBM Plex Mono", FontCategory.CoreAxiom),
        FontOption("source_code_pro", "Source Code Pro", "Source Code Pro", FontCategory.CoreAxiom),
        FontOption("fira_code", "Fira Code", "Fira Code", FontCategory.CoreAxiom),
        FontOption("roboto_mono", "Roboto Mono", "Roboto Mono", FontCategory.CoreAxiom),
        FontOption("ubuntu_mono", "Ubuntu Mono", "Ubuntu Mono", FontCategory.CoreAxiom),
        FontOption("hack", "Hack", "Hack", FontCategory.CoreAxiom),
        FontOption("cascadia_code", "Cascadia Code", "Cascadia Code", FontCategory.CoreAxiom),
        FontOption("monaspace", "Monaspace", "Monaspace", FontCategory.CoreAxiom),
        FontOption("inconsolata", "Inconsolata", "Inconsolata", FontCategory.CoreAxiom),
        FontOption("jetbrainsmono_nerd", "JetBrainsMono Nerd Font", "JetBrainsMono Nerd Font", FontCategory.NerdFont),
        FontOption("firacode_nerd", "FiraCode Nerd Font", "FiraCode Nerd Font", FontCategory.NerdFont),
        FontOption("hack_nerd", "Hack Nerd Font", "Hack Nerd Font", FontCategory.NerdFont),
        FontOption("meslolgs_nerd", "MesloLGS Nerd Font", "MesloLGS Nerd Font", FontCategory.NerdFont),
        FontOption("caskaydiacove_nerd", "CaskaydiaCove Nerd Font", "CaskaydiaCove Nerd Font", FontCategory.NerdFont),
        FontOption("mononoki_nerd", "Mononoki Nerd Font", "Mononoki Nerd Font", FontCategory.NerdFont),
        FontOption("iosevka_nerd", "Iosevka Nerd Font", "Iosevka Nerd Font", FontCategory.NerdFont),
        FontOption("victormono_nerd", "VictorMono Nerd Font", "VictorMono Nerd Font", FontCategory.NerdFont),
        FontOption("saucecodepro_nerd", "SauceCodePro Nerd Font", "SauceCodePro Nerd Font", FontCategory.NerdFont),
        FontOption("ubuntumono_nerd", "UbuntuMono Nerd Font", "UbuntuMono Nerd Font", FontCategory.NerdFont),
        FontOption("roboto", "Roboto", "Roboto", FontCategory.Sans),
        FontOption("inter", "Inter", "Inter", FontCategory.Sans),
        FontOption("open_sans", "Open Sans", "Open Sans", FontCategory.Sans),
        FontOption("noto_sans", "Noto Sans", "Noto Sans", FontCategory.Sans),
        FontOption("montserrat", "Montserrat", "Montserrat", FontCategory.Sans),
        FontOption("poppins", "Poppins", "Poppins", FontCategory.Sans),
        FontOption("lato", "Lato", "Lato", FontCategory.Sans),
        FontOption("source_sans_3", "Source Sans 3", "Source Sans 3", FontCategory.Sans),
        FontOption("work_sans", "Work Sans", "Work Sans", FontCategory.Sans),
        FontOption("manrope", "Manrope", "Manrope", FontCategory.Sans),
        FontOption("noto_serif", "Noto Serif", "Noto Serif", FontCategory.Serif),
        FontOption("merriweather", "Merriweather", "Merriweather", FontCategory.Serif),
        FontOption("playfair_display", "Playfair Display", "Playfair Display", FontCategory.Serif),
        FontOption("libre_baskerville", "Libre Baskerville", "Libre Baskerville", FontCategory.Serif),
        FontOption("source_serif_4", "Source Serif 4", "Source Serif 4", FontCategory.Serif),
        FontOption("orbitron", "Orbitron", "Orbitron", FontCategory.Display),
        FontOption("share_tech_mono", "Share Tech Mono", "Share Tech Mono", FontCategory.Display),
        FontOption("exo_2", "Exo 2", "Exo 2", FontCategory.Display),
        FontOption("rajdhani", "Rajdhani", "Rajdhani", FontCategory.Display),
        FontOption("oxanium", "Oxanium", "Oxanium", FontCategory.Display)
    )

    fun byKey(key: String): FontOption = All.firstOrNull { it.key == key } ?: All.first()
}
