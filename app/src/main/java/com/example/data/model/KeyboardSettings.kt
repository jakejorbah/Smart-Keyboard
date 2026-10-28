package com.example.data.model

enum class KeyboardMode {
    ALPHABET,
    SYMBOLS_1,
    SYMBOLS_2,
    EMOJI,
    CLIPBOARD,
    TEMPLATES
}

enum class OneHandedMode {
    NONE,
    LEFT,
    RIGHT
}

data class TintPreset(
    val id: String,
    val name: String,
    val colorValue: Long
)

val BackgroundTintPresets = listOf(
    TintPreset("clear", "Clear Glass", 0x00000000L),
    TintPreset("ice", "Electric Ice", 0xFF0284C7L),
    TintPreset("cyan", "Neon Cyan", 0xFF06B6D4L),
    TintPreset("amethyst", "Amethyst", 0xFF7C3AEDL),
    TintPreset("rose", "Rose Quartz", 0xFFE11D48L),
    TintPreset("emerald", "Emerald", 0xFF059669L),
    TintPreset("amber", "Warm Amber", 0xFFD97706L),
    TintPreset("slate", "Smoke Slate", 0xFF334155L),
    TintPreset("midnight", "Midnight Black", 0xFF0B1120L)
)

data class KeyboardSettings(
    val themeId: String = KeyboardTheme.CrystalClear.id,
    val keyboardOpacity: Float = 0.55f,
    val keycapOpacity: Float = 0.50f,
    val cornerRadius: Int = 10,
    val keySpacing: Int = 5,
    val keyboardHeightFactor: Float = 1.0f,
    val borderWidth: Float = 1.0f,
    val borderOpacity: Float = 0.45f,
    val blurIntensity: Int = 60,
    val backgroundTintColor: Long = 0x00000000L,
    val tintOpacity: Float = 0.35f,
    val keypressAnimationEnabled: Boolean = true,
    val respectReducedMotion: Boolean = true,
    val hapticFeedback: Boolean = true,
    val hapticIntensity: Float = 1.0f,
    val soundFeedback: Boolean = false,
    val autoCapitalization: Boolean = true,
    val doubleSpacePeriod: Boolean = true,
    val suggestionsEnabled: Boolean = true,
    val autoCorrectEnabled: Boolean = true,
    val numberRowEnabled: Boolean = false,
    val oneHandedMode: OneHandedMode = OneHandedMode.NONE,
    val activeLanguage: String = "English (US)",
    val customTintColor: Long = 0xFF0F172AL,
    val customKeyColor: Long = 0xFF1E293BL,
    val customTextColor: Long = 0xFFFFFFFFL
)
