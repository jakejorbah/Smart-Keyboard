package com.example.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.FavoriteBorder
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
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
import com.example.data.KeyboardPreferences
import com.example.data.model.KeyboardSettings
import com.example.data.model.KeyboardTheme
import com.example.ui.theme.ElectricBlue
import com.example.ui.theme.GlassSuccess

@Composable
fun ThemesScreen(
    preferences: KeyboardPreferences,
    settings: KeyboardSettings
) {
    var favoriteThemeIds by remember { mutableStateOf(setOf(KeyboardTheme.CrystalClear.id, KeyboardTheme.IosLightGlass.id)) }

    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        item {
            Column {
                Text(
                    text = "Themes & Styles",
                    fontSize = 28.sp,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.onBackground
                )
                Text(
                    text = "Choose from 10 handcrafted translucent glass styles",
                    fontSize = 13.sp,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        }

        items(KeyboardTheme.AllThemes, key = { it.id }) { theme ->
            val isApplied = theme.id == settings.themeId
            val isFavorite = favoriteThemeIds.contains(theme.id)

            ThemeCard(
                theme = theme,
                isApplied = isApplied,
                isFavorite = isFavorite,
                onApply = { preferences.selectTheme(theme.id) },
                onToggleFavorite = {
                    favoriteThemeIds = if (isFavorite) {
                        favoriteThemeIds - theme.id
                    } else {
                        favoriteThemeIds + theme.id
                    }
                }
            )
        }

        item {
            Spacer(modifier = Modifier.height(20.dp))
        }
    }
}

@Composable
private fun ThemeCard(
    theme: KeyboardTheme,
    isApplied: Boolean,
    isFavorite: Boolean,
    onApply: () -> Unit,
    onToggleFavorite: () -> Unit
) {
    val shape = RoundedCornerShape(20.dp)

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .border(
                width = if (isApplied) 2.dp else 1.dp,
                color = if (isApplied) ElectricBlue else MaterialTheme.colorScheme.outline.copy(alpha = 0.2f),
                shape = shape
            ),
        shape = shape,
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.45f))
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp)
        ) {
            // Top Row: Theme Name, Pill & Favorite
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    Box(
                        modifier = Modifier
                            .size(16.dp)
                            .clip(CircleShape)
                            .background(theme.accentColor)
                    )

                    Text(
                        text = theme.name,
                        fontSize = 17.sp,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.onSurface
                    )

                    Box(
                        modifier = Modifier
                            .clip(RoundedCornerShape(6.dp))
                            .background(MaterialTheme.colorScheme.surface)
                            .padding(horizontal = 6.dp, vertical = 2.dp)
                    ) {
                        Text(
                            text = if (theme.isDark) "Dark" else "Light",
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Medium,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                }

                IconButton(onClick = onToggleFavorite, modifier = Modifier.size(28.dp)) {
                    Icon(
                        imageVector = if (isFavorite) Icons.Default.Favorite else Icons.Default.FavoriteBorder,
                        contentDescription = "Favorite",
                        tint = if (isFavorite) Color(0xFFF43F5E) else MaterialTheme.colorScheme.onSurfaceVariant,
                        modifier = Modifier.size(18.dp)
                    )
                }
            }

            Spacer(modifier = Modifier.height(4.dp))

            Text(
                text = theme.description,
                fontSize = 12.sp,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                lineHeight = 16.sp
            )

            Spacer(modifier = Modifier.height(14.dp))

            // Realistic Mini Swatch Preview Deck
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(72.dp)
                    .clip(RoundedCornerShape(12.dp))
                    .background(
                        Brush.linearGradient(
                            listOf(
                                theme.keyboardBgColor.copy(alpha = theme.keyboardOpacity),
                                theme.keyboardBgColor.copy(alpha = (theme.keyboardOpacity * 1.3f).coerceAtMost(1f))
                            )
                        )
                    )
                    .border(1.dp, theme.borderColor.copy(alpha = 0.4f), RoundedCornerShape(12.dp))
                    .padding(8.dp),
                contentAlignment = Alignment.Center
            ) {
                Column(
                    verticalArrangement = Arrangement.spacedBy(4.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    // Row 1 of mini keys
                    Row(
                        horizontalArrangement = Arrangement.spacedBy(4.dp)
                    ) {
                        listOf("Q", "W", "E", "R", "T", "Y", "U").forEach { key ->
                            MiniKey(
                                text = key,
                                bg = theme.keyBgColor.copy(alpha = theme.keycapOpacity),
                                fg = theme.keyTextColor,
                                border = theme.borderColor.copy(alpha = 0.5f)
                            )
                        }
                    }

                    // Row 2 of mini keys
                    Row(
                        horizontalArrangement = Arrangement.spacedBy(4.dp)
                    ) {
                        MiniKey(
                            text = "Shift",
                            bg = theme.keySpecialBgColor.copy(alpha = 0.7f),
                            fg = theme.keySpecialTextColor,
                            border = theme.borderColor.copy(alpha = 0.5f),
                            isWide = true
                        )
                        listOf("A", "S", "D", "F", "G").forEach { key ->
                            MiniKey(
                                text = key,
                                bg = theme.keyBgColor.copy(alpha = theme.keycapOpacity),
                                fg = theme.keyTextColor,
                                border = theme.borderColor.copy(alpha = 0.5f)
                            )
                        }
                        MiniKey(
                            text = "⌫",
                            bg = theme.keySpecialBgColor.copy(alpha = 0.7f),
                            fg = theme.keySpecialTextColor,
                            border = theme.borderColor.copy(alpha = 0.5f),
                            isWide = true
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(12.dp))

            // Bottom action row: Opacity indicators & Apply button
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "Opacity: ${(theme.keyboardOpacity * 100).toInt()}% • Keycap: ${(theme.keycapOpacity * 100).toInt()}%",
                    fontSize = 11.sp,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )

                if (isApplied) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(4.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Default.Check,
                            contentDescription = null,
                            tint = GlassSuccess,
                            modifier = Modifier.size(16.dp)
                        )
                        Text(
                            text = "Applied",
                            fontSize = 13.sp,
                            fontWeight = FontWeight.Bold,
                            color = GlassSuccess
                        )
                    }
                } else {
                    Button(
                        onClick = onApply,
                        shape = RoundedCornerShape(10.dp),
                        colors = ButtonDefaults.buttonColors(containerColor = ElectricBlue),
                        contentPadding = PaddingValues(horizontal = 16.dp, vertical = 6.dp)
                    ) {
                        Text("Apply", fontSize = 12.sp)
                    }
                }
            }
        }
    }
}

@Composable
private fun MiniKey(
    text: String,
    bg: Color,
    fg: Color,
    border: Color,
    isWide: Boolean = false
) {
    Box(
        modifier = Modifier
            .width(if (isWide) 34.dp else 22.dp)
            .height(22.dp)
            .clip(RoundedCornerShape(4.dp))
            .background(bg)
            .border(0.5.dp, border, RoundedCornerShape(4.dp)),
        contentAlignment = Alignment.Center
    ) {
        Text(
            text = text,
            color = fg,
            fontSize = 9.sp,
            fontWeight = FontWeight.Medium
        )
    }
}
