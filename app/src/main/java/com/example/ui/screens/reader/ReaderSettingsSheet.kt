package com.example.ui.screens.reader

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
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.FormatAlignLeft
import androidx.compose.material.icons.filled.FormatAlignCenter
import androidx.compose.material.icons.filled.FormatAlignJustify
import androidx.compose.material.icons.filled.FormatSize
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FilterChip
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.IconButtonDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.Slider
import androidx.compose.material3.Text
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.model.ReaderFontFamily
import com.example.data.model.ReaderLineSpacing
import com.example.data.model.ReaderSettings
import com.example.data.model.ReaderThemeMode

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ReaderSettingsSheet(
    settings: ReaderSettings,
    onUpdateTheme: (ReaderThemeMode) -> Unit,
    onUpdateFontSize: (Int) -> Unit,
    onUpdateFontFamily: (ReaderFontFamily) -> Unit,
    onUpdateLineSpacing: (ReaderLineSpacing) -> Unit,
    onToggleJustification: () -> Unit,
    onDismiss: () -> Unit
) {
    val sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)

    ModalBottomSheet(
        onDismissRequest = onDismiss,
        sheetState = sheetState,
        modifier = Modifier.testTag("reader_settings_sheet")
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 24.dp)
                .padding(bottom = 32.dp),
            verticalArrangement = Arrangement.spacedBy(20.dp)
        ) {
            Text(
                text = "Conforto Visual & Tipografia",
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold
            )

            // 1. Temas de Leitura / Modo Noturno
            Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                Text(
                    text = "Tema de Leitura",
                    style = MaterialTheme.typography.labelLarge,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    ThemeOptionButton(
                        title = "Claro",
                        bgColor = Color(0xFFFBFBFB),
                        textColor = Color(0xFF1F2937),
                        isSelected = settings.themeMode == ReaderThemeMode.LIGHT,
                        onClick = { onUpdateTheme(ReaderThemeMode.LIGHT) },
                        modifier = Modifier.weight(1f)
                    )

                    ThemeOptionButton(
                        title = "Sépia",
                        bgColor = Color(0xFFF6F0E6),
                        textColor = Color(0xFF2E2419),
                        isSelected = settings.themeMode == ReaderThemeMode.SEPIA,
                        onClick = { onUpdateTheme(ReaderThemeMode.SEPIA) },
                        modifier = Modifier.weight(1f)
                    )

                    ThemeOptionButton(
                        title = "Noturno",
                        bgColor = Color(0xFF18181B),
                        textColor = Color(0xFFE4E4E7),
                        isSelected = settings.themeMode == ReaderThemeMode.DARK,
                        onClick = { onUpdateTheme(ReaderThemeMode.DARK) },
                        modifier = Modifier.weight(1f)
                    )

                    ThemeOptionButton(
                        title = "OLED",
                        bgColor = Color(0xFF000000),
                        textColor = Color(0xFFCCCCCC),
                        isSelected = settings.themeMode == ReaderThemeMode.OLED,
                        onClick = { onUpdateTheme(ReaderThemeMode.OLED) },
                        modifier = Modifier.weight(1f)
                    )
                }
            }

            // 2. Tamanho da Fonte
            Column(verticalArrangement = Arrangement.spacedBy(6.dp)) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "Tamanho do Texto",
                        style = MaterialTheme.typography.labelLarge,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                    Text(
                        text = "${settings.fontSizeSp} sp",
                        style = MaterialTheme.typography.labelLarge,
                        fontWeight = FontWeight.Bold
                    )
                }

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    IconButton(
                        onClick = { onUpdateFontSize(settings.fontSizeSp - 1) },
                        enabled = settings.fontSizeSp > 13
                    ) {
                        Text("A-", fontWeight = FontWeight.Bold, fontSize = 14.sp)
                    }

                    Slider(
                        value = settings.fontSizeSp.toFloat(),
                        onValueChange = { onUpdateFontSize(it.toInt()) },
                        valueRange = 13f..28f,
                        steps = 14,
                        modifier = Modifier
                            .weight(1f)
                            .testTag("font_size_slider")
                    )

                    IconButton(
                        onClick = { onUpdateFontSize(settings.fontSizeSp + 1) },
                        enabled = settings.fontSizeSp < 28
                    ) {
                        Text("A+", fontWeight = FontWeight.Bold, fontSize = 20.sp)
                    }
                }
            }

            // 3. Família da Fonte
            Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                Text(
                    text = "Estilo de Fonte",
                    style = MaterialTheme.typography.labelLarge,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    FontOptionChip(
                        label = "Serifada",
                        fontFamily = FontFamily.Serif,
                        isSelected = settings.fontFamily == ReaderFontFamily.SERIF,
                        onClick = { onUpdateFontFamily(ReaderFontFamily.SERIF) },
                        modifier = Modifier.weight(1f)
                    )

                    FontOptionChip(
                        label = "Sem Serifa",
                        fontFamily = FontFamily.SansSerif,
                        isSelected = settings.fontFamily == ReaderFontFamily.SANS,
                        onClick = { onUpdateFontFamily(ReaderFontFamily.SANS) },
                        modifier = Modifier.weight(1f)
                    )

                    FontOptionChip(
                        label = "Monospace",
                        fontFamily = FontFamily.Monospace,
                        isSelected = settings.fontFamily == ReaderFontFamily.MONOSPACE,
                        onClick = { onUpdateFontFamily(ReaderFontFamily.MONOSPACE) },
                        modifier = Modifier.weight(1f)
                    )
                }
            }

            // 4. Espaçamento entre Linhas & Alinhamento
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = "Espaçamento",
                        style = MaterialTheme.typography.labelLarge,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                    Spacer(modifier = Modifier.height(6.dp))
                    Row(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                        ReaderLineSpacing.values().forEach { spacing ->
                            FilterChip(
                                selected = settings.lineSpacing == spacing,
                                onClick = { onUpdateLineSpacing(spacing) },
                                label = { Text(spacing.label, fontSize = 12.sp) }
                            )
                        }
                    }
                }

                Spacer(modifier = Modifier.width(12.dp))

                Column(horizontalAlignment = Alignment.End) {
                    Text(
                        text = "Alinhamento",
                        style = MaterialTheme.typography.labelLarge,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                    Spacer(modifier = Modifier.height(6.dp))
                    FilterChip(
                        selected = settings.isJustified,
                        onClick = onToggleJustification,
                        label = {
                            Text(if (settings.isJustified) "Justificado" else "À Esquerda")
                        },
                        leadingIcon = {
                            Icon(
                                imageVector = if (settings.isJustified) Icons.Filled.FormatAlignJustify else Icons.AutoMirrored.Filled.FormatAlignLeft,
                                contentDescription = "Alinhamento",
                                modifier = Modifier.size(16.dp)
                            )
                        }
                    )
                }
            }
        }
    }
}

@Composable
private fun ThemeOptionButton(
    title: String,
    bgColor: Color,
    textColor: Color,
    isSelected: Boolean,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Box(
        modifier = modifier
            .height(54.dp)
            .clip(RoundedCornerShape(10.dp))
            .background(bgColor)
            .border(
                width = if (isSelected) 2.5.dp else 1.dp,
                color = if (isSelected) MaterialTheme.colorScheme.primary else Color(0x33888888),
                shape = RoundedCornerShape(10.dp)
            )
            .clickable(onClick = onClick),
        contentAlignment = Alignment.Center
    ) {
        Column(horizontalAlignment = Alignment.CenterHorizontally) {
            Text(
                text = "Aa",
                color = textColor,
                fontSize = 16.sp,
                fontWeight = FontWeight.Bold
            )
            Text(
                text = title,
                color = textColor.copy(alpha = 0.8f),
                fontSize = 11.sp
            )
        }
    }
}

@Composable
private fun FontOptionChip(
    label: String,
    fontFamily: FontFamily,
    isSelected: Boolean,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Box(
        modifier = modifier
            .height(44.dp)
            .clip(RoundedCornerShape(8.dp))
            .background(
                if (isSelected) MaterialTheme.colorScheme.primaryContainer
                else MaterialTheme.colorScheme.surfaceVariant
            )
            .border(
                width = if (isSelected) 1.5.dp else 0.5.dp,
                color = if (isSelected) MaterialTheme.colorScheme.primary else Color.Transparent,
                shape = RoundedCornerShape(8.dp)
            )
            .clickable(onClick = onClick),
        contentAlignment = Alignment.Center
    ) {
        Text(
            text = label,
            fontFamily = fontFamily,
            fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal,
            color = if (isSelected) MaterialTheme.colorScheme.onPrimaryContainer
            else MaterialTheme.colorScheme.onSurfaceVariant,
            fontSize = 13.sp
        )
    }
}
