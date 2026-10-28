package com.example.ui.components

import android.provider.Settings
import android.view.HapticFeedbackConstants
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.spring
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalView
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

@Composable
fun GlassKey(
    modifier: Modifier = Modifier,
    primaryText: String? = null,
    secondaryText: String? = null,
    icon: (@Composable () -> Unit)? = null,
    isSpecial: Boolean = false,
    keyBgColor: Color,
    keyTextColor: Color,
    borderColor: Color,
    borderWidth: Dp = 1.dp,
    cornerRadius: Dp = 8.dp,
    height: Dp = 48.dp,
    keycapOpacity: Float = 0.6f,
    hapticsEnabled: Boolean = true,
    animationEnabled: Boolean = true,
    respectReducedMotion: Boolean = true,
    repeatOnLongPress: Boolean = false,
    onTap: () -> Unit,
    onLongPress: (() -> Unit)? = null
) {
    val context = LocalContext.current
    val view = LocalView.current
    var isPressed by remember { mutableStateOf(false) }
    val scope = rememberCoroutineScope()
    var repeatJob by remember { mutableStateOf<Job?>(null) }

    // Check system-level reduced motion accessibility setting
    val isSystemReducedMotion = remember(respectReducedMotion) {
        if (!respectReducedMotion) false
        else try {
            Settings.Global.getFloat(context.contentResolver, Settings.Global.ANIMATOR_DURATION_SCALE, 1f) == 0f
        } catch (e: Exception) {
            false
        }
    }

    val shouldAnimate = animationEnabled && !isSystemReducedMotion

    val scale by animateFloatAsState(
        targetValue = if (isPressed && shouldAnimate) 0.92f else 1f,
        animationSpec = spring(dampingRatio = 0.65f, stiffness = 700f),
        label = "keyScale"
    )

    val shape = RoundedCornerShape(cornerRadius)

    val actualBgColor = if (isPressed) {
        keyBgColor.copy(alpha = (keycapOpacity * 1.4f).coerceAtMost(0.95f))
    } else {
        keyBgColor.copy(alpha = keycapOpacity.coerceIn(0.05f, 0.95f))
    }

    val actualBorderColor = if (isPressed) {
        borderColor.copy(alpha = 0.85f)
    } else {
        borderColor
    }

    fun triggerHaptic() {
        if (hapticsEnabled) {
            try {
                view.performHapticFeedback(HapticFeedbackConstants.KEYBOARD_TAP)
            } catch (e: Exception) {
                // Ignore if device vibrator unavailable
            }
        }
    }

    Box(
        modifier = modifier
            .height(height)
            .scale(scale)
            .clip(shape)
            .background(
                Brush.verticalGradient(
                    colors = listOf(
                        actualBgColor.copy(alpha = (actualBgColor.alpha * 1.15f).coerceAtMost(1f)),
                        actualBgColor.copy(alpha = (actualBgColor.alpha * 0.85f).coerceAtLeast(0.05f))
                    )
                )
            )
            .border(
                BorderStroke(borderWidth, actualBorderColor),
                shape
            )
            .pointerInput(repeatOnLongPress, hapticsEnabled) {
                detectTapGestures(
                    onPress = {
                        isPressed = true
                        triggerHaptic()
                        if (repeatOnLongPress) {
                            repeatJob = scope.launch {
                                onTap()
                                delay(400) // Initial delay before repeat
                                while (isPressed) {
                                    triggerHaptic()
                                    onTap()
                                    delay(75) // Fast repeat
                                }
                            }
                        }
                        tryAwaitRelease()
                        isPressed = false
                        repeatJob?.cancel()
                        repeatJob = null
                    },
                    onTap = {
                        if (!repeatOnLongPress) {
                            onTap()
                        }
                    },
                    onLongPress = {
                        if (!repeatOnLongPress && onLongPress != null) {
                            try {
                                view.performHapticFeedback(HapticFeedbackConstants.LONG_PRESS)
                            } catch (e: Exception) { }
                            onLongPress()
                        }
                    }
                )
            },
        contentAlignment = Alignment.Center
    ) {
        if (secondaryText != null) {
            Text(
                text = secondaryText,
                color = keyTextColor.copy(alpha = 0.50f),
                fontSize = 9.sp,
                fontWeight = FontWeight.Normal,
                modifier = Modifier
                    .align(Alignment.TopEnd)
                    .padding(top = 2.dp, end = 4.dp)
            )
        }

        if (icon != null) {
            icon()
        } else if (primaryText != null) {
            Text(
                text = primaryText,
                color = keyTextColor,
                fontSize = if (primaryText.length > 2) 13.sp else 19.sp,
                fontWeight = if (isSpecial) FontWeight.SemiBold else FontWeight.Medium,
                textAlign = TextAlign.Center
            )
        }
    }
}
