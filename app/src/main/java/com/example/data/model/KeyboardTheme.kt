package com.example.data.model

import androidx.compose.ui.graphics.Color

data class KeyboardTheme(
    val id: String,
    val name: String,
    val description: String,
    val isDark: Boolean,
    val keyboardBgColor: Color,
    val keyBgColor: Color,
    val keyTextColor: Color,
    val keySpecialBgColor: Color,
    val keySpecialTextColor: Color,
    val accentColor: Color,
    val borderColor: Color,
    val keyboardOpacity: Float,
    val keycapOpacity: Float
) {
    companion object {
        val CrystalClear = KeyboardTheme(
            id = "crystal_clear",
            name = "Crystal Clear",
            description = "Ultra-translucent ice glass with luminous electric blue accents",
            isDark = true,
            keyboardBgColor = Color(0xFF0F172A),
            keyBgColor = Color(0xFF38BDF8),
            keyTextColor = Color.White,
            keySpecialBgColor = Color(0xFF0284C7),
            keySpecialTextColor = Color.White,
            accentColor = Color(0xFF38BDF8),
            borderColor = Color(0x66FFFFFF),
            keyboardOpacity = 0.35f,
            keycapOpacity = 0.40f
        )

        val IosLightGlass = KeyboardTheme(
            id = "ios_light_glass",
            name = "iOS Light Glass",
            description = "Apple iOS 18 frosted translucent glass with clean graphite typography",
            isDark = false,
            keyboardBgColor = Color(0xFFD1D5DB),
            keyBgColor = Color.White,
            keyTextColor = Color(0xFF111827),
            keySpecialBgColor = Color(0xFFCBD5E1),
            keySpecialTextColor = Color(0xFF1E293B),
            accentColor = Color(0xFF007AFF),
            borderColor = Color(0x33000000),
            keyboardOpacity = 0.70f,
            keycapOpacity = 0.85f
        )

        val MidnightGlass = KeyboardTheme(
            id = "midnight_glass",
            name = "Midnight Glass",
            description = "Deep translucent indigo-navy with neon cyber cyan highlights",
            isDark = true,
            keyboardBgColor = Color(0xFF0B1120),
            keyBgColor = Color(0xFF1E293B),
            keyTextColor = Color(0xFFF1F5F9),
            keySpecialBgColor = Color(0xFF334155),
            keySpecialTextColor = Color(0xFF38BDF8),
            accentColor = Color(0xFF38BDF8),
            borderColor = Color(0x4038BDF8),
            keyboardOpacity = 0.75f,
            keycapOpacity = 0.65f
        )

        val FrostedWhite = KeyboardTheme(
            id = "frosted_white",
            name = "Frosted White",
            description = "Silky alabaster frosted glass with high-contrast slate keys",
            isDark = false,
            keyboardBgColor = Color(0xFFF1F5F9),
            keyBgColor = Color(0xFFFFFFFF),
            keyTextColor = Color(0xFF0F172A),
            keySpecialBgColor = Color(0xFFE2E8F0),
            keySpecialTextColor = Color(0xFF334155),
            accentColor = Color(0xFF2563EB),
            borderColor = Color(0x44CBD5E1),
            keyboardOpacity = 0.80f,
            keycapOpacity = 0.75f
        )

        val Obsidian = KeyboardTheme(
            id = "obsidian",
            name = "Obsidian AMOLED",
            description = "Smoky crystalline glass over deep true black with stark white text",
            isDark = true,
            keyboardBgColor = Color(0xFF050508),
            keyBgColor = Color(0xFF181820),
            keyTextColor = Color.White,
            keySpecialBgColor = Color(0xFF272732),
            keySpecialTextColor = Color(0xFFE2E8F0),
            accentColor = Color(0xFF6366F1),
            borderColor = Color(0x2EFFFFFF),
            keyboardOpacity = 0.85f,
            keycapOpacity = 0.60f
        )

        val LavenderMist = KeyboardTheme(
            id = "lavender_mist",
            name = "Lavender Mist",
            description = "Dreamy lilac translucent glass with soft purple illumination",
            isDark = true,
            keyboardBgColor = Color(0xFF1E1338),
            keyBgColor = Color(0xFF3B2668),
            keyTextColor = Color(0xFFF5F3FF),
            keySpecialBgColor = Color(0xFF563696),
            keySpecialTextColor = Color(0xFFDDD6FE),
            accentColor = Color(0xFFA855F7),
            borderColor = Color(0x40C084FC),
            keyboardOpacity = 0.65f,
            keycapOpacity = 0.55f
        )

        val OceanBlue = KeyboardTheme(
            id = "ocean_blue",
            name = "Ocean Blue",
            description = "Aquatic deep sea glass with crystalline azure keys",
            isDark = true,
            keyboardBgColor = Color(0xFF082F49),
            keyBgColor = Color(0xFF0C4A6E),
            keyTextColor = Color(0xFFF0F9FF),
            keySpecialBgColor = Color(0xFF0369A1),
            keySpecialTextColor = Color(0xFFBAE6FD),
            accentColor = Color(0xFF38BDF8),
            borderColor = Color(0x407DD3FC),
            keyboardOpacity = 0.70f,
            keycapOpacity = 0.55f
        )

        val Aurora = KeyboardTheme(
            id = "aurora",
            name = "Aurora Borealis",
            description = "Iridescent polar lights with emerald and amethyst glass facets",
            isDark = true,
            keyboardBgColor = Color(0xFF062024),
            keyBgColor = Color(0xFF0F3B40),
            keyTextColor = Color(0xFFECFDF5),
            keySpecialBgColor = Color(0xFF1E4E4E),
            keySpecialTextColor = Color(0xFF6EE7B7),
            accentColor = Color(0xFF34D399),
            borderColor = Color(0x4434D399),
            keyboardOpacity = 0.70f,
            keycapOpacity = 0.55f
        )

        val MinimalBlack = KeyboardTheme(
            id = "minimal_black",
            name = "Minimal Slate",
            description = "Subtle monochromatic slate glass with understated elegance",
            isDark = true,
            keyboardBgColor = Color(0xFF1E293B),
            keyBgColor = Color(0xFF334155),
            keyTextColor = Color(0xFFF8FAFC),
            keySpecialBgColor = Color(0xFF475569),
            keySpecialTextColor = Color(0xFFCBD5E1),
            accentColor = Color(0xFF94A3B8),
            borderColor = Color(0x33FFFFFF),
            keyboardOpacity = 0.75f,
            keycapOpacity = 0.65f
        )

        val Custom = KeyboardTheme(
            id = "custom",
            name = "Custom Glass",
            description = "Fully personalized colors, transparency, and glass sheen",
            isDark = true,
            keyboardBgColor = Color(0xFF111827),
            keyBgColor = Color(0xFF1F2937),
            keyTextColor = Color.White,
            keySpecialBgColor = Color(0xFF374151),
            keySpecialTextColor = Color(0xFF93C5FD),
            accentColor = Color(0xFF3B82F6),
            borderColor = Color(0x40FFFFFF),
            keyboardOpacity = 0.65f,
            keycapOpacity = 0.55f
        )

        val AllThemes = listOf(
            CrystalClear,
            IosLightGlass,
            MidnightGlass,
            FrostedWhite,
            Obsidian,
            LavenderMist,
            OceanBlue,
            Aurora,
            MinimalBlack,
            Custom
        )

        fun getById(id: String): KeyboardTheme {
            return AllThemes.find { it.id == id } ?: CrystalClear
        }
    }
}
