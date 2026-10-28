package com.example.ui.components

import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.togetherWith
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.filled.ArrowForward
import androidx.compose.material.icons.automirrored.filled.Backspace
import androidx.compose.material.icons.automirrored.filled.KeyboardReturn
import androidx.compose.material.icons.automirrored.filled.Send
import androidx.compose.material.icons.filled.ArrowUpward
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.ChevronLeft
import androidx.compose.material.icons.filled.ChevronRight
import androidx.compose.material.icons.filled.Fullscreen
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableLongStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.model.ClipboardSnippet
import com.example.data.model.KeyboardMode
import com.example.data.model.KeyboardSettings
import com.example.data.model.KeyboardTheme
import com.example.data.model.OneHandedMode
import com.example.data.model.TextShortcut
import com.example.engine.TypingEngine

enum class ShiftState {
    OFF,
    ONCE,
    CAPS_LOCK
}

@Composable
fun KeyboardLayout(
    settings: KeyboardSettings,
    shortcuts: List<TextShortcut> = emptyList(),
    snippets: List<ClipboardSnippet> = emptyList(),
    templates: List<com.example.data.model.MessageTemplate> = emptyList(),
    customWords: Set<String> = emptySet(),
    currentTextBeforeCursor: String = "",
    actionLabel: String = "Done",
    onTextInput: (String) -> Unit,
    onDelete: () -> Unit,
    onEnter: () -> Unit,
    onOpenSettings: () -> Unit,
    onOpenMessagePlanner: () -> Unit = {},
    onAddSnippet: (String) -> Unit = {},
    onDeleteSnippet: (String) -> Unit = {},
    onUpdateOneHandedMode: (OneHandedMode) -> Unit = {},
    onCycleTheme: () -> Unit = {},
    onVoiceInput: (() -> Unit)? = null
) {
    val theme = KeyboardTheme.getById(settings.themeId)
    val typingEngine = remember { TypingEngine() }

    var currentMode by remember { mutableStateOf(KeyboardMode.ALPHABET) }
    var shiftState by remember { mutableStateOf(ShiftState.OFF) }
    var lastShiftTapTime by remember { mutableLongStateOf(0L) }
    var lastSpaceTapTime by remember { mutableLongStateOf(0L) }

    // Auto-capitalize on initial load or cursor update
    LaunchedEffect(currentTextBeforeCursor, settings.autoCapitalization) {
        if (settings.autoCapitalization && shiftState == ShiftState.OFF) {
            if (typingEngine.shouldAutoCapitalize(currentTextBeforeCursor)) {
                shiftState = ShiftState.ONCE
            }
        }
    }

    val suggestions = remember(currentTextBeforeCursor, settings.suggestionsEnabled, shortcuts, customWords) {
        typingEngine.getSuggestions(
            textBeforeCursor = currentTextBeforeCursor,
            shortcuts = shortcuts,
            customWords = customWords,
            suggestionsEnabled = settings.suggestionsEnabled
        )
    }

    val keyHeight = (46.dp * settings.keyboardHeightFactor).coerceIn(38.dp, 60.dp)
    val cornerRadiusDp = settings.cornerRadius.dp
    val spacingDp = settings.keySpacing.dp
    val borderWidthDp = settings.borderWidth.dp
    val borderAlpha = settings.borderOpacity

    val actualKeyboardBgColor = theme.keyboardBgColor.copy(
        alpha = settings.keyboardOpacity.coerceIn(0.05f, 0.98f)
    )
    val actualBorderColor = theme.borderColor.copy(
        alpha = borderAlpha.coerceIn(0.05f, 1f)
    )

    fun handleKeyInput(char: String) {
        val output = when (shiftState) {
            ShiftState.CAPS_LOCK, ShiftState.ONCE -> char.uppercase()
            ShiftState.OFF -> char.lowercase()
        }
        onTextInput(output)
        if (shiftState == ShiftState.ONCE) {
            shiftState = ShiftState.OFF
        }
    }

    fun handleSpaceInput() {
        val now = System.currentTimeMillis()
        if (settings.doubleSpacePeriod && (now - lastSpaceTapTime < 450)) {
            // Replace previous space with ". "
            onDelete()
            onTextInput(". ")
            if (settings.autoCapitalization) {
                shiftState = ShiftState.ONCE
            }
            lastSpaceTapTime = 0L
        } else {
            onTextInput(" ")
            lastSpaceTapTime = now
        }
    }

    fun handleShiftTap() {
        val now = System.currentTimeMillis()
        shiftState = when (shiftState) {
            ShiftState.OFF -> ShiftState.ONCE
            ShiftState.ONCE -> {
                if (now - lastShiftTapTime < 400) {
                    ShiftState.CAPS_LOCK
                } else {
                    ShiftState.OFF
                }
            }
            ShiftState.CAPS_LOCK -> ShiftState.OFF
        }
        lastShiftTapTime = now
    }

    fun handleSuggestionSelection(suggestion: String) {
        // Delete current prefix before committing suggestion
        val trimmed = currentTextBeforeCursor
        val wordMatch = Regex("([a-zA-Z0-9'-]+)$").find(trimmed)
        val prefixLen = wordMatch?.value?.length ?: 0
        repeat(prefixLen) {
            onDelete()
        }
        onTextInput("$suggestion ")
        if (settings.autoCapitalization && typingEngine.shouldAutoCapitalize(suggestion)) {
            shiftState = ShiftState.ONCE
        } else if (shiftState == ShiftState.ONCE) {
            shiftState = ShiftState.OFF
        }
    }

    val blurFactor = (settings.blurIntensity / 100f).coerceIn(0f, 1f)
    val hasTint = settings.backgroundTintColor != 0L
    val tintColor = if (hasTint) {
        Color(settings.backgroundTintColor).copy(alpha = settings.tintOpacity.coerceIn(0.05f, 0.95f))
    } else {
        Color.Transparent
    }

    // Keyboard container with frosted glass gradient and tint
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .background(
                Brush.verticalGradient(
                    colors = if (hasTint) {
                        listOf(
                            actualKeyboardBgColor.copy(alpha = (actualKeyboardBgColor.alpha * (0.75f + 0.25f * blurFactor)).coerceAtMost(1f)),
                            tintColor,
                            actualKeyboardBgColor.copy(alpha = (actualKeyboardBgColor.alpha * (0.95f + 0.05f * blurFactor)).coerceAtMost(1f))
                        )
                    } else {
                        listOf(
                            actualKeyboardBgColor.copy(alpha = (actualKeyboardBgColor.alpha * (0.75f + 0.25f * blurFactor)).coerceAtMost(1f)),
                            actualKeyboardBgColor.copy(alpha = (actualKeyboardBgColor.alpha * (1.10f + 0.10f * blurFactor)).coerceAtMost(1f))
                        )
                    }
                )
            )
            .border(
                BorderStroke(
                    borderWidthDp,
                    Brush.verticalGradient(
                        colors = listOf(
                            actualBorderColor.copy(alpha = (actualBorderColor.alpha * 1.2f).coerceAtMost(1f)),
                            actualBorderColor.copy(alpha = (actualBorderColor.alpha * 0.4f).coerceAtLeast(0.05f))
                        )
                    )
                )
            )
            .padding(horizontal = 4.dp, vertical = 2.dp)
    ) {
        Column(
            modifier = Modifier.fillMaxWidth(),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            // Suggestion Strip
            SuggestionStrip(
                suggestions = suggestions,
                theme = theme,
                onSelectSuggestion = { handleSuggestionSelection(it) },
                onOpenSettings = onOpenSettings,
                onOpenEmoji = { currentMode = if (currentMode == KeyboardMode.EMOJI) KeyboardMode.ALPHABET else KeyboardMode.EMOJI },
                onOpenClipboard = { currentMode = if (currentMode == KeyboardMode.CLIPBOARD) KeyboardMode.ALPHABET else KeyboardMode.CLIPBOARD },
                onOpenTemplates = { currentMode = if (currentMode == KeyboardMode.TEMPLATES) KeyboardMode.ALPHABET else KeyboardMode.TEMPLATES },
                onToggleOneHanded = {
                    val next = when (settings.oneHandedMode) {
                        OneHandedMode.NONE -> OneHandedMode.RIGHT
                        OneHandedMode.RIGHT -> OneHandedMode.LEFT
                        OneHandedMode.LEFT -> OneHandedMode.NONE
                    }
                    onUpdateOneHandedMode(next)
                },
                onCycleTheme = onCycleTheme,
                onVoiceInput = onVoiceInput
            )

            // Main Keyboard Deck with One-Handed Mode support
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically
            ) {
                // Left margin / controller for One-Handed mode
                if (settings.oneHandedMode == OneHandedMode.RIGHT) {
                    Column(
                        modifier = Modifier
                            .width(52.dp)
                            .padding(end = 4.dp),
                        verticalArrangement = Arrangement.spacedBy(8.dp),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        IconButton(
                            onClick = { onUpdateOneHandedMode(OneHandedMode.LEFT) },
                            modifier = Modifier.size(36.dp)
                        ) {
                            Icon(
                                imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                                contentDescription = "Switch to Left",
                                tint = theme.keyTextColor.copy(alpha = 0.7f)
                            )
                        }
                        IconButton(
                            onClick = { onUpdateOneHandedMode(OneHandedMode.NONE) },
                            modifier = Modifier.size(36.dp)
                        ) {
                            Icon(
                                imageVector = Icons.Default.Fullscreen,
                                contentDescription = "Full width",
                                tint = theme.keyTextColor.copy(alpha = 0.7f)
                            )
                        }
                    }
                }

                // Keyboard Content Area
                Box(modifier = Modifier.weight(1f)) {
                    AnimatedContent(
                        targetState = currentMode,
                        transitionSpec = {
                            fadeIn(animationSpec = tween(180)) togetherWith fadeOut(animationSpec = tween(150))
                        },
                        label = "keyboardMode"
                    ) { mode ->
                        when (mode) {
                            KeyboardMode.ALPHABET -> {
                                AlphabetDeck(
                                    settings = settings,
                                    theme = theme,
                                    shiftState = shiftState,
                                    keyHeight = keyHeight,
                                    cornerRadius = cornerRadiusDp,
                                    spacing = spacingDp,
                                    borderWidth = borderWidthDp,
                                    actionLabel = actionLabel,
                                    onChar = { handleKeyInput(it) },
                                    onShift = { handleShiftTap() },
                                    onBackspace = onDelete,
                                    onSpace = { handleSpaceInput() },
                                    onEnter = onEnter,
                                    onSwitchToSymbols = { currentMode = KeyboardMode.SYMBOLS_1 },
                                    onSwitchToEmoji = { currentMode = KeyboardMode.EMOJI }
                                )
                            }
                            KeyboardMode.SYMBOLS_1 -> {
                                SymbolsDeck(
                                    page = 1,
                                    settings = settings,
                                    theme = theme,
                                    keyHeight = keyHeight,
                                    cornerRadius = cornerRadiusDp,
                                    spacing = spacingDp,
                                    borderWidth = borderWidthDp,
                                    actionLabel = actionLabel,
                                    onChar = { onTextInput(it) },
                                    onBackspace = onDelete,
                                    onSpace = { handleSpaceInput() },
                                    onEnter = onEnter,
                                    onSwitchToAlpha = { currentMode = KeyboardMode.ALPHABET },
                                    onSwitchSymbolPage = { currentMode = KeyboardMode.SYMBOLS_2 },
                                    onSwitchToEmoji = { currentMode = KeyboardMode.EMOJI }
                                )
                            }
                            KeyboardMode.SYMBOLS_2 -> {
                                SymbolsDeck(
                                    page = 2,
                                    settings = settings,
                                    theme = theme,
                                    keyHeight = keyHeight,
                                    cornerRadius = cornerRadiusDp,
                                    spacing = spacingDp,
                                    borderWidth = borderWidthDp,
                                    actionLabel = actionLabel,
                                    onChar = { onTextInput(it) },
                                    onBackspace = onDelete,
                                    onSpace = { handleSpaceInput() },
                                    onEnter = onEnter,
                                    onSwitchToAlpha = { currentMode = KeyboardMode.ALPHABET },
                                    onSwitchSymbolPage = { currentMode = KeyboardMode.SYMBOLS_1 },
                                    onSwitchToEmoji = { currentMode = KeyboardMode.EMOJI }
                                )
                            }
                            KeyboardMode.EMOJI -> {
                                EmojiPickerView(
                                    theme = theme,
                                    onEmojiSelected = { onTextInput(it) },
                                    onDelete = onDelete,
                                    onCloseEmoji = { currentMode = KeyboardMode.ALPHABET }
                                )
                            }
                            KeyboardMode.CLIPBOARD -> {
                                ClipboardPickerView(
                                    theme = theme,
                                    snippets = snippets,
                                    onSnippetSelected = { onTextInput(it) },
                                    onAddSnippet = onAddSnippet,
                                    onDeleteSnippet = onDeleteSnippet,
                                    onCloseClipboard = { currentMode = KeyboardMode.ALPHABET }
                                )
                            }
                            KeyboardMode.TEMPLATES -> {
                                TemplatePickerView(
                                    theme = theme,
                                    templates = templates,
                                    onTemplateSelected = { onTextInput(it) },
                                    onOpenMessagePlanner = onOpenMessagePlanner,
                                    onClose = { currentMode = KeyboardMode.ALPHABET }
                                )
                            }
                        }
                    }
                }

                // Right margin / controller for One-Handed mode
                if (settings.oneHandedMode == OneHandedMode.LEFT) {
                    Column(
                        modifier = Modifier
                            .width(52.dp)
                            .padding(start = 4.dp),
                        verticalArrangement = Arrangement.spacedBy(8.dp),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        IconButton(
                            onClick = { onUpdateOneHandedMode(OneHandedMode.RIGHT) },
                            modifier = Modifier.size(36.dp)
                        ) {
                            Icon(
                                imageVector = Icons.AutoMirrored.Filled.ArrowForward,
                                contentDescription = "Switch to Right",
                                tint = theme.keyTextColor.copy(alpha = 0.7f)
                            )
                        }
                        IconButton(
                            onClick = { onUpdateOneHandedMode(OneHandedMode.NONE) },
                            modifier = Modifier.size(36.dp)
                        ) {
                            Icon(
                                imageVector = Icons.Default.Fullscreen,
                                contentDescription = "Full width",
                                tint = theme.keyTextColor.copy(alpha = 0.7f)
                            )
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun AlphabetDeck(
    settings: KeyboardSettings,
    theme: KeyboardTheme,
    shiftState: ShiftState,
    keyHeight: androidx.compose.ui.unit.Dp,
    cornerRadius: androidx.compose.ui.unit.Dp,
    spacing: androidx.compose.ui.unit.Dp,
    borderWidth: androidx.compose.ui.unit.Dp,
    actionLabel: String,
    onChar: (String) -> Unit,
    onShift: () -> Unit,
    onBackspace: () -> Unit,
    onSpace: () -> Unit,
    onEnter: () -> Unit,
    onSwitchToSymbols: () -> Unit,
    onSwitchToEmoji: () -> Unit
) {
    val isShifted = shiftState != ShiftState.OFF
    val row1 = listOf("Q", "W", "E", "R", "T", "Y", "U", "I", "O", "P")
    val row1Sec = listOf("1", "2", "3", "4", "5", "6", "7", "8", "9", "0")

    val row2 = listOf("A", "S", "D", "F", "G", "H", "J", "K", "L")
    val row2Sec = listOf("@", "#", "$", "%", "&", "*", "-", "+", "=")

    val row3 = listOf("Z", "X", "C", "V", "B", "N", "M")
    val row3Sec = listOf("(", ")", "'", "\"", "/", ":", ";")

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 4.dp),
        verticalArrangement = Arrangement.spacedBy(spacing)
    ) {
        // Optional Dedicated Number Row
        if (settings.numberRowEnabled) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(spacing)
            ) {
                row1Sec.forEach { digit ->
                    GlassKey(
                        modifier = Modifier.weight(1f),
                        primaryText = digit,
                        keyBgColor = theme.keyBgColor,
                        keyTextColor = theme.keyTextColor,
                        borderColor = theme.borderColor,
                        borderWidth = borderWidth,
                        cornerRadius = cornerRadius,
                        height = keyHeight * 0.85f,
                        keycapOpacity = settings.keycapOpacity,
                        hapticsEnabled = settings.hapticFeedback,
                        onTap = { onChar(digit) }
                    )
                }
            }
        }

        // Row 1 (QWERTY)
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(spacing)
        ) {
            row1.forEachIndexed { i, char ->
                val display = if (isShifted) char.uppercase() else char.lowercase()
                GlassKey(
                    modifier = Modifier.weight(1f),
                    primaryText = display,
                    secondaryText = row1Sec[i],
                    keyBgColor = theme.keyBgColor,
                    keyTextColor = theme.keyTextColor,
                    borderColor = theme.borderColor,
                    borderWidth = borderWidth,
                    cornerRadius = cornerRadius,
                    height = keyHeight,
                    keycapOpacity = settings.keycapOpacity,
                    hapticsEnabled = settings.hapticFeedback,
                    onTap = { onChar(display) },
                    onLongPress = { onChar(row1Sec[i]) }
                )
            }
        }

        // Row 2 (ASDFGHJKL)
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 14.dp),
            horizontalArrangement = Arrangement.spacedBy(spacing)
        ) {
            row2.forEachIndexed { i, char ->
                val display = if (isShifted) char.uppercase() else char.lowercase()
                GlassKey(
                    modifier = Modifier.weight(1f),
                    primaryText = display,
                    secondaryText = row2Sec[i],
                    keyBgColor = theme.keyBgColor,
                    keyTextColor = theme.keyTextColor,
                    borderColor = theme.borderColor,
                    borderWidth = borderWidth,
                    cornerRadius = cornerRadius,
                    height = keyHeight,
                    keycapOpacity = settings.keycapOpacity,
                    hapticsEnabled = settings.hapticFeedback,
                    onTap = { onChar(display) },
                    onLongPress = { onChar(row2Sec[i]) }
                )
            }
        }

        // Row 3 (Shift, ZXCVBNM, Backspace)
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(spacing)
        ) {
            // Shift Key
            val shiftBg = if (shiftState != ShiftState.OFF) theme.accentColor else theme.keySpecialBgColor
            val shiftTextColor = if (shiftState != ShiftState.OFF) Color.White else theme.keySpecialTextColor
            GlassKey(
                modifier = Modifier.weight(1.5f),
                icon = {
                    Icon(
                        imageVector = Icons.Default.ArrowUpward,
                        contentDescription = "Shift",
                        tint = shiftTextColor,
                        modifier = Modifier.size(19.dp)
                    )
                },
                isSpecial = true,
                keyBgColor = shiftBg,
                keyTextColor = shiftTextColor,
                borderColor = theme.borderColor,
                borderWidth = borderWidth,
                cornerRadius = cornerRadius,
                height = keyHeight,
                keycapOpacity = if (shiftState != ShiftState.OFF) 0.85f else settings.keycapOpacity,
                hapticsEnabled = settings.hapticFeedback,
                onTap = onShift
            )

            // Letters
            row3.forEachIndexed { i, char ->
                val display = if (isShifted) char.uppercase() else char.lowercase()
                GlassKey(
                    modifier = Modifier.weight(1f),
                    primaryText = display,
                    secondaryText = row3Sec[i],
                    keyBgColor = theme.keyBgColor,
                    keyTextColor = theme.keyTextColor,
                    borderColor = theme.borderColor,
                    borderWidth = borderWidth,
                    cornerRadius = cornerRadius,
                    height = keyHeight,
                    keycapOpacity = settings.keycapOpacity,
                    hapticsEnabled = settings.hapticFeedback,
                    onTap = { onChar(display) },
                    onLongPress = { onChar(row3Sec[i]) }
                )
            }

            // Backspace Key (with auto-repeat on hold!)
            GlassKey(
                modifier = Modifier.weight(1.5f),
                icon = {
                    Icon(
                        imageVector = Icons.AutoMirrored.Filled.Backspace,
                        contentDescription = "Delete",
                        tint = theme.keySpecialTextColor,
                        modifier = Modifier.size(19.dp)
                    )
                },
                isSpecial = true,
                keyBgColor = theme.keySpecialBgColor,
                keyTextColor = theme.keySpecialTextColor,
                borderColor = theme.borderColor,
                borderWidth = borderWidth,
                cornerRadius = cornerRadius,
                height = keyHeight,
                keycapOpacity = settings.keycapOpacity,
                hapticsEnabled = settings.hapticFeedback,
                repeatOnLongPress = true,
                onTap = onBackspace
            )
        }

        // Row 4 (Symbols, Emoji, Comma, Space, Period, Enter/Action)
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(spacing)
        ) {
            // ?123 switch
            GlassKey(
                modifier = Modifier.weight(1.3f),
                primaryText = "?123",
                isSpecial = true,
                keyBgColor = theme.keySpecialBgColor,
                keyTextColor = theme.keySpecialTextColor,
                borderColor = theme.borderColor,
                borderWidth = borderWidth,
                cornerRadius = cornerRadius,
                height = keyHeight,
                keycapOpacity = settings.keycapOpacity,
                hapticsEnabled = settings.hapticFeedback,
                onTap = onSwitchToSymbols
            )

            // Quick Emoji
            GlassKey(
                modifier = Modifier.weight(1f),
                primaryText = "😀",
                isSpecial = true,
                keyBgColor = theme.keySpecialBgColor,
                keyTextColor = theme.keySpecialTextColor,
                borderColor = theme.borderColor,
                borderWidth = borderWidth,
                cornerRadius = cornerRadius,
                height = keyHeight,
                keycapOpacity = settings.keycapOpacity,
                hapticsEnabled = settings.hapticFeedback,
                onTap = onSwitchToEmoji
            )

            // Comma
            GlassKey(
                modifier = Modifier.weight(0.9f),
                primaryText = ",",
                keyBgColor = theme.keyBgColor,
                keyTextColor = theme.keyTextColor,
                borderColor = theme.borderColor,
                borderWidth = borderWidth,
                cornerRadius = cornerRadius,
                height = keyHeight,
                keycapOpacity = settings.keycapOpacity,
                hapticsEnabled = settings.hapticFeedback,
                onTap = { onChar(",") }
            )

            // Spacebar
            GlassKey(
                modifier = Modifier.weight(3.6f),
                primaryText = "Crystal",
                keyBgColor = theme.keyBgColor,
                keyTextColor = theme.keyTextColor.copy(alpha = 0.65f),
                borderColor = theme.borderColor,
                borderWidth = borderWidth,
                cornerRadius = cornerRadius,
                height = keyHeight,
                keycapOpacity = settings.keycapOpacity,
                hapticsEnabled = settings.hapticFeedback,
                onTap = onSpace
            )

            // Period
            GlassKey(
                modifier = Modifier.weight(0.9f),
                primaryText = ".",
                keyBgColor = theme.keyBgColor,
                keyTextColor = theme.keyTextColor,
                borderColor = theme.borderColor,
                borderWidth = borderWidth,
                cornerRadius = cornerRadius,
                height = keyHeight,
                keycapOpacity = settings.keycapOpacity,
                hapticsEnabled = settings.hapticFeedback,
                onTap = { onChar(".") }
            )

            // Enter / Action Key
            GlassKey(
                modifier = Modifier.weight(1.5f),
                icon = {
                    Icon(
                        imageVector = when (actionLabel.lowercase()) {
                            "send" -> Icons.AutoMirrored.Filled.Send
                            "search" -> Icons.Default.Search
                            else -> Icons.AutoMirrored.Filled.KeyboardReturn
                        },
                        contentDescription = actionLabel,
                        tint = Color.White,
                        modifier = Modifier.size(19.dp)
                    )
                },
                isSpecial = true,
                keyBgColor = theme.accentColor,
                keyTextColor = Color.White,
                borderColor = theme.borderColor,
                borderWidth = borderWidth,
                cornerRadius = cornerRadius,
                height = keyHeight,
                keycapOpacity = 0.85f,
                hapticsEnabled = settings.hapticFeedback,
                onTap = onEnter
            )
        }
    }
}

@Composable
private fun SymbolsDeck(
    page: Int,
    settings: KeyboardSettings,
    theme: KeyboardTheme,
    keyHeight: androidx.compose.ui.unit.Dp,
    cornerRadius: androidx.compose.ui.unit.Dp,
    spacing: androidx.compose.ui.unit.Dp,
    borderWidth: androidx.compose.ui.unit.Dp,
    actionLabel: String,
    onChar: (String) -> Unit,
    onBackspace: () -> Unit,
    onSpace: () -> Unit,
    onEnter: () -> Unit,
    onSwitchToAlpha: () -> Unit,
    onSwitchSymbolPage: () -> Unit,
    onSwitchToEmoji: () -> Unit
) {
    val numberRow = listOf("1", "2", "3", "4", "5", "6", "7", "8", "9", "0")

    val p1Row1 = listOf("[", "]", "{", "}", "#", "%", "^", "*", "+", "=")
    val p1Row2 = listOf("_", "\\", "|", "~", "<", ">", "$", "€", "£", "¥")
    val p1Row3 = listOf("•", ".", ",", "?", "!", "'", "\"", ":", ";")

    val p2Row1 = listOf("~", "`", "|", "^", "%", "*", "+", "=", "{", "}")
    val p2Row2 = listOf("[", "]", "\\", "<", ">", "€", "£", "¥", "₽", "#")
    val p2Row3 = listOf("_", "§", "©", "®", "™", "°", "•", "·", "¿", "¡")

    val activeRow1 = if (page == 1) p1Row1 else p2Row1
    val activeRow2 = if (page == 1) p1Row2 else p2Row2
    val activeRow3 = if (page == 1) p1Row3 else p2Row3

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 4.dp),
        verticalArrangement = Arrangement.spacedBy(spacing)
    ) {
        // Number Row
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(spacing)
        ) {
            numberRow.forEach { digit ->
                GlassKey(
                    modifier = Modifier.weight(1f),
                    primaryText = digit,
                    keyBgColor = theme.keyBgColor,
                    keyTextColor = theme.keyTextColor,
                    borderColor = theme.borderColor,
                    borderWidth = borderWidth,
                    cornerRadius = cornerRadius,
                    height = keyHeight,
                    keycapOpacity = settings.keycapOpacity,
                    hapticsEnabled = settings.hapticFeedback,
                    onTap = { onChar(digit) }
                )
            }
        }

        // Row 1
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(spacing)
        ) {
            activeRow1.forEach { sym ->
                GlassKey(
                    modifier = Modifier.weight(1f),
                    primaryText = sym,
                    keyBgColor = theme.keyBgColor,
                    keyTextColor = theme.keyTextColor,
                    borderColor = theme.borderColor,
                    borderWidth = borderWidth,
                    cornerRadius = cornerRadius,
                    height = keyHeight,
                    keycapOpacity = settings.keycapOpacity,
                    hapticsEnabled = settings.hapticFeedback,
                    onTap = { onChar(sym) }
                )
            }
        }

        // Row 2
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(spacing)
        ) {
            activeRow2.forEach { sym ->
                GlassKey(
                    modifier = Modifier.weight(1f),
                    primaryText = sym,
                    keyBgColor = theme.keyBgColor,
                    keyTextColor = theme.keyTextColor,
                    borderColor = theme.borderColor,
                    borderWidth = borderWidth,
                    cornerRadius = cornerRadius,
                    height = keyHeight,
                    keycapOpacity = settings.keycapOpacity,
                    hapticsEnabled = settings.hapticFeedback,
                    onTap = { onChar(sym) }
                )
            }
        }

        // Row 3 (Page switch, symbols, backspace)
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(spacing)
        ) {
            // Page toggle (1/2 or 2/2)
            GlassKey(
                modifier = Modifier.weight(1.5f),
                primaryText = if (page == 1) "1/2" else "2/2",
                isSpecial = true,
                keyBgColor = theme.keySpecialBgColor,
                keyTextColor = theme.keySpecialTextColor,
                borderColor = theme.borderColor,
                borderWidth = borderWidth,
                cornerRadius = cornerRadius,
                height = keyHeight,
                keycapOpacity = settings.keycapOpacity,
                hapticsEnabled = settings.hapticFeedback,
                onTap = onSwitchSymbolPage
            )

            activeRow3.forEach { sym ->
                GlassKey(
                    modifier = Modifier.weight(1f),
                    primaryText = sym,
                    keyBgColor = theme.keyBgColor,
                    keyTextColor = theme.keyTextColor,
                    borderColor = theme.borderColor,
                    borderWidth = borderWidth,
                    cornerRadius = cornerRadius,
                    height = keyHeight,
                    keycapOpacity = settings.keycapOpacity,
                    hapticsEnabled = settings.hapticFeedback,
                    onTap = { onChar(sym) }
                )
            }

            // Backspace
            GlassKey(
                modifier = Modifier.weight(1.5f),
                icon = {
                    Icon(
                        imageVector = Icons.AutoMirrored.Filled.Backspace,
                        contentDescription = "Delete",
                        tint = theme.keySpecialTextColor,
                        modifier = Modifier.size(19.dp)
                    )
                },
                isSpecial = true,
                keyBgColor = theme.keySpecialBgColor,
                keyTextColor = theme.keySpecialTextColor,
                borderColor = theme.borderColor,
                borderWidth = borderWidth,
                cornerRadius = cornerRadius,
                height = keyHeight,
                keycapOpacity = settings.keycapOpacity,
                hapticsEnabled = settings.hapticFeedback,
                repeatOnLongPress = true,
                onTap = onBackspace
            )
        }

        // Row 4 (ABC, Emoji, Space, Enter)
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(spacing)
        ) {
            // Switch back to ABC
            GlassKey(
                modifier = Modifier.weight(1.5f),
                primaryText = "ABC",
                isSpecial = true,
                keyBgColor = theme.keySpecialBgColor,
                keyTextColor = theme.keySpecialTextColor,
                borderColor = theme.borderColor,
                borderWidth = borderWidth,
                cornerRadius = cornerRadius,
                height = keyHeight,
                keycapOpacity = settings.keycapOpacity,
                hapticsEnabled = settings.hapticFeedback,
                onTap = onSwitchToAlpha
            )

            // Emoji
            GlassKey(
                modifier = Modifier.weight(1f),
                primaryText = "😀",
                isSpecial = true,
                keyBgColor = theme.keySpecialBgColor,
                keyTextColor = theme.keySpecialTextColor,
                borderColor = theme.borderColor,
                borderWidth = borderWidth,
                cornerRadius = cornerRadius,
                height = keyHeight,
                keycapOpacity = settings.keycapOpacity,
                hapticsEnabled = settings.hapticFeedback,
                onTap = onSwitchToEmoji
            )

            // Spacebar
            GlassKey(
                modifier = Modifier.weight(4.5f),
                primaryText = "space",
                keyBgColor = theme.keyBgColor,
                keyTextColor = theme.keyTextColor.copy(alpha = 0.65f),
                borderColor = theme.borderColor,
                borderWidth = borderWidth,
                cornerRadius = cornerRadius,
                height = keyHeight,
                keycapOpacity = settings.keycapOpacity,
                hapticsEnabled = settings.hapticFeedback,
                onTap = onSpace
            )

            // Enter
            GlassKey(
                modifier = Modifier.weight(1.5f),
                icon = {
                    Icon(
                        imageVector = Icons.AutoMirrored.Filled.KeyboardReturn,
                        contentDescription = actionLabel,
                        tint = Color.White,
                        modifier = Modifier.size(19.dp)
                    )
                },
                isSpecial = true,
                keyBgColor = theme.accentColor,
                keyTextColor = Color.White,
                borderColor = theme.borderColor,
                borderWidth = borderWidth,
                cornerRadius = cornerRadius,
                height = keyHeight,
                keycapOpacity = 0.85f,
                hapticsEnabled = settings.hapticFeedback,
                onTap = onEnter
            )
        }
    }
}
