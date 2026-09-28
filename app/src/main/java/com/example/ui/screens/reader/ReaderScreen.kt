package com.example.ui.screens.reader

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.filled.ArrowForward
import androidx.compose.material.icons.filled.AddComment
import androidx.compose.material.icons.filled.ChatBubbleOutline
import androidx.compose.material.icons.filled.FormatSize
import androidx.compose.material.icons.filled.Info
import androidx.compose.material3.Badge
import androidx.compose.material3.BadgedBox
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.data.model.Chapter
import com.example.data.model.ReaderFontFamily
import com.example.data.model.ReaderSettings
import com.example.data.model.ReaderThemeMode
import com.example.ui.theme.ReaderDarkBg
import com.example.ui.theme.ReaderDarkText
import com.example.ui.theme.ReaderLightBg
import com.example.ui.theme.ReaderLightText
import com.example.ui.theme.ReaderOledBg
import com.example.ui.theme.ReaderOledText
import com.example.ui.theme.ReaderSepiaBg
import com.example.ui.theme.ReaderSepiaText
import com.example.ui.viewmodel.ReaderViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ReaderScreen(
    readerViewModel: ReaderViewModel,
    bookTitle: String,
    versionName: String,
    onNavigateBack: () -> Unit
) {
    val chapter by readerViewModel.currentChapter.collectAsStateWithLifecycle()
    val allChapters by readerViewModel.chaptersInVersion.collectAsStateWithLifecycle()
    val comments by readerViewModel.comments.collectAsStateWithLifecycle()
    val settings by readerViewModel.readerSettings.collectAsStateWithLifecycle()

    var showSettingsSheet by remember { mutableStateOf(false) }
    var showCommentsSheet by remember { mutableStateOf(false) }

    var selectedParagraphIndex by remember { mutableStateOf<Int?>(null) }
    var selectedParagraphPreview by remember { mutableStateOf<String?>(null) }

    // Color theme values
    val (bgColor, textColor, surfaceColor, accentColor) = when (settings.themeMode) {
        ReaderThemeMode.LIGHT -> Quad(ReaderLightBg, ReaderLightText, Color(0xFFF3F4F6), Color(0xFF1E3A8A))
        ReaderThemeMode.SEPIA -> Quad(ReaderSepiaBg, ReaderSepiaText, Color(0xFFEDE3D3), Color(0xFF854D0E))
        ReaderThemeMode.DARK -> Quad(ReaderDarkBg, ReaderDarkText, Color(0xFF27272A), Color(0xFF60A5FA))
        ReaderThemeMode.OLED -> Quad(ReaderOledBg, ReaderOledText, Color(0xFF18181B), Color(0xFF93C5FD))
    }

    val selectedFont = when (settings.fontFamily) {
        ReaderFontFamily.SERIF -> FontFamily.Serif
        ReaderFontFamily.SANS -> FontFamily.SansSerif
        ReaderFontFamily.MONOSPACE -> FontFamily.Monospace
    }

    val lineHeightSp = (settings.fontSizeSp * settings.lineSpacing.multiplier).sp
    val textAlign = if (settings.isJustified) TextAlign.Justify else TextAlign.Start

    val currentChapterIndex = remember(chapter, allChapters) {
        if (chapter != null) allChapters.indexOfFirst { it.id == chapter?.id } else 0
    }

    val paragraphs = remember(chapter?.content) {
        chapter?.content?.split("\n\n")?.filter { it.isNotBlank() } ?: emptyList()
    }

    val listState = rememberLazyListState()

    Scaffold(
        contentWindowInsets = WindowInsets(0, 0, 0, 0),
        topBar = {
            CenterAlignedTopAppBar(
                title = {
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Text(
                            text = chapter?.title ?: "Leitura",
                            style = MaterialTheme.typography.titleSmall,
                            fontWeight = FontWeight.Bold,
                            color = textColor,
                            maxLines = 1,
                            overflow = TextOverflow.Ellipsis
                        )
                        Text(
                            text = "$bookTitle • $versionName",
                            style = MaterialTheme.typography.labelSmall,
                            color = textColor.copy(alpha = 0.7f),
                            maxLines = 1,
                            overflow = TextOverflow.Ellipsis
                        )
                    }
                },
                navigationIcon = {
                    IconButton(
                        onClick = onNavigateBack,
                        modifier = Modifier.testTag("reader_back_button")
                    ) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = "Voltar",
                            tint = textColor
                        )
                    }
                },
                actions = {
                    // Quick Settings (Font / Theme / Night Mode)
                    IconButton(
                        onClick = { showSettingsSheet = true },
                        modifier = Modifier.testTag("reader_settings_button")
                    ) {
                        Icon(
                            imageVector = Icons.Default.FormatSize,
                            contentDescription = "Ajustar Fonte e Modo Noturno",
                            tint = textColor
                        )
                    }

                    // Comments button with badge
                    IconButton(
                        onClick = {
                            selectedParagraphIndex = null
                            selectedParagraphPreview = null
                            showCommentsSheet = true
                        },
                        modifier = Modifier.testTag("reader_comments_top_button")
                    ) {
                        BadgedBox(
                            badge = {
                                if (comments.isNotEmpty()) {
                                    Badge { Text("${comments.size}") }
                                }
                            }
                        ) {
                            Icon(
                                imageVector = Icons.Default.ChatBubbleOutline,
                                contentDescription = "Ver comentários do capítulo",
                                tint = textColor
                            )
                        }
                    }
                },
                colors = TopAppBarDefaults.centerAlignedTopAppBarColors(
                    containerColor = bgColor
                )
            )
        },
        bottomBar = {
            // Bottom navigation between chapters
            Surface(
                color = bgColor,
                tonalElevation = 2.dp,
                modifier = Modifier.fillMaxWidth()
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp, vertical = 12.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    IconButton(
                        onClick = { readerViewModel.goToPreviousChapter() },
                        enabled = currentChapterIndex > 0
                    ) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = "Capítulo anterior",
                            tint = if (currentChapterIndex > 0) textColor else textColor.copy(alpha = 0.3f)
                        )
                    }

                    Text(
                        text = if (allChapters.isNotEmpty()) "Capítulo ${currentChapterIndex + 1} de ${allChapters.size}" else "",
                        style = MaterialTheme.typography.bodySmall,
                        color = textColor.copy(alpha = 0.7f),
                        fontWeight = FontWeight.Medium
                    )

                    IconButton(
                        onClick = { readerViewModel.goToNextChapter() },
                        enabled = currentChapterIndex != -1 && currentChapterIndex < allChapters.size - 1
                    ) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.ArrowForward,
                            contentDescription = "Próximo capítulo",
                            tint = if (currentChapterIndex < allChapters.size - 1) textColor else textColor.copy(alpha = 0.3f)
                        )
                    }
                }
            }
        },
        floatingActionButton = {
            FloatingActionButton(
                onClick = {
                    showCommentsSheet = true
                },
                containerColor = accentColor,
                contentColor = Color.White,
                modifier = Modifier.testTag("reader_fab_comments")
            ) {
                Row(
                    modifier = Modifier.padding(horizontal = 12.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(
                        imageVector = Icons.Default.ChatBubbleOutline,
                        contentDescription = "Comentários do capítulo"
                    )
                    Spacer(modifier = Modifier.width(6.dp))
                    Text(
                        text = "${comments.size}",
                        fontWeight = FontWeight.Bold,
                        fontSize = 14.sp
                    )
                }
            }
        },
        containerColor = bgColor
    ) { paddingValues ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(bgColor)
                .padding(paddingValues)
        ) {
            LazyColumn(
                state = listState,
                contentPadding = PaddingValues(horizontal = 24.dp, vertical = 16.dp),
                verticalArrangement = Arrangement.spacedBy(16.dp),
                modifier = Modifier.fillMaxSize()
            ) {
                // Author Notes for beta readers
                if (!chapter?.authorNotes.isNullOrBlank()) {
                    item {
                        Card(
                            colors = CardDefaults.cardColors(
                                containerColor = surfaceColor
                            ),
                            shape = RoundedCornerShape(12.dp),
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(bottom = 12.dp)
                        ) {
                            Row(
                                modifier = Modifier.padding(14.dp),
                                verticalAlignment = Alignment.Top
                            ) {
                                Icon(
                                    imageVector = Icons.Default.Info,
                                    contentDescription = null,
                                    tint = accentColor,
                                    modifier = Modifier.size(20.dp)
                                )
                                Spacer(modifier = Modifier.width(10.dp))
                                Column {
                                    Text(
                                        text = "Solicitação do Autor aos Leitores Beta",
                                        style = MaterialTheme.typography.labelMedium,
                                        fontWeight = FontWeight.Bold,
                                        color = accentColor
                                    )
                                    Spacer(modifier = Modifier.height(4.dp))
                                    Text(
                                        text = chapter?.authorNotes ?: "",
                                        style = MaterialTheme.typography.bodySmall,
                                        color = textColor.copy(alpha = 0.9f)
                                    )
                                }
                            }
                        }
                    }
                }

                // Paragraphs with inline commentary support
                itemsIndexed(paragraphs) { index, paragraphText ->
                    val paragraphComments = comments.filter { it.paragraphIndex == index }
                    val isSelected = selectedParagraphIndex == index

                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clip(RoundedCornerShape(8.dp))
                            .background(
                                if (isSelected) surfaceColor
                                else Color.Transparent
                            )
                            .border(
                                width = if (isSelected) 1.5.dp else 0.dp,
                                color = if (isSelected) accentColor else Color.Transparent,
                                shape = RoundedCornerShape(8.dp)
                            )
                            .clickable {
                                if (isSelected) {
                                    selectedParagraphIndex = null
                                    selectedParagraphPreview = null
                                } else {
                                    selectedParagraphIndex = index
                                    selectedParagraphPreview = paragraphText.take(80) + if (paragraphText.length > 80) "..." else ""
                                }
                            }
                            .padding(horizontal = 8.dp, vertical = 6.dp)
                    ) {
                        Text(
                            text = paragraphText,
                            style = MaterialTheme.typography.bodyLarge.copy(
                                fontSize = settings.fontSizeSp.sp,
                                lineHeight = lineHeightSp,
                                fontFamily = selectedFont,
                                textAlign = textAlign,
                                color = textColor
                            )
                        )

                        // Inline Comments Badge & Action Row
                        if (paragraphComments.isNotEmpty() || isSelected) {
                            Spacer(modifier = Modifier.height(6.dp))
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                if (paragraphComments.isNotEmpty()) {
                                    Surface(
                                        color = accentColor.copy(alpha = 0.15f),
                                        shape = RoundedCornerShape(12.dp),
                                        modifier = Modifier.clickable {
                                            selectedParagraphIndex = index
                                            selectedParagraphPreview = paragraphText.take(80)
                                            showCommentsSheet = true
                                        }
                                    ) {
                                        Row(
                                            modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp),
                                            verticalAlignment = Alignment.CenterVertically
                                        ) {
                                            Icon(
                                                imageVector = Icons.Default.ChatBubbleOutline,
                                                contentDescription = null,
                                                tint = accentColor,
                                                modifier = Modifier.size(13.dp)
                                            )
                                            Spacer(modifier = Modifier.width(4.dp))
                                            Text(
                                                text = "${paragraphComments.size} ${if (paragraphComments.size == 1) "comentário" else "comentários"}",
                                                fontSize = 11.sp,
                                                fontWeight = FontWeight.Bold,
                                                color = accentColor
                                            )
                                        }
                                    }
                                } else {
                                    Spacer(modifier = Modifier.width(1.dp))
                                }

                                if (isSelected) {
                                    Surface(
                                        color = accentColor,
                                        shape = RoundedCornerShape(12.dp),
                                        modifier = Modifier.clickable {
                                            showCommentsSheet = true
                                        }
                                    ) {
                                        Row(
                                            modifier = Modifier.padding(horizontal = 10.dp, vertical = 4.dp),
                                            verticalAlignment = Alignment.CenterVertically
                                        ) {
                                            Icon(
                                                imageVector = Icons.Default.AddComment,
                                                contentDescription = null,
                                                tint = Color.White,
                                                modifier = Modifier.size(14.dp)
                                            )
                                            Spacer(modifier = Modifier.width(4.dp))
                                            Text(
                                                text = "Comentar este trecho",
                                                fontSize = 11.sp,
                                                fontWeight = FontWeight.Bold,
                                                color = Color.White
                                            )
                                        }
                                    }
                                }
                            }
                        }
                    }
                }

                // End of chapter spacer & mark
                item {
                    Spacer(modifier = Modifier.height(32.dp))
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(bottom = 60.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = "— Fim do ${chapter?.title ?: "Capítulo"} —",
                            style = MaterialTheme.typography.bodyMedium,
                            fontStyle = FontStyle.Italic,
                            color = textColor.copy(alpha = 0.5f)
                        )
                    }
                }
            }
        }
    }

    // Modal Sheet for Reader Settings (Night mode, fonts, sizes)
    if (showSettingsSheet) {
        ReaderSettingsSheet(
            settings = settings,
            onUpdateTheme = { readerViewModel.updateThemeMode(it) },
            onUpdateFontSize = { readerViewModel.updateFontSize(it) },
            onUpdateFontFamily = { readerViewModel.updateFontFamily(it) },
            onUpdateLineSpacing = { readerViewModel.updateLineSpacing(it) },
            onToggleJustification = { readerViewModel.toggleJustification() },
            onDismiss = { showSettingsSheet = false }
        )
    }

    // Modal Sheet for Chapter Comments
    if (showCommentsSheet) {
        ChapterCommentsSheet(
            chapterTitle = chapter?.title ?: "Capítulo",
            comments = comments,
            selectedParagraphIndex = selectedParagraphIndex,
            selectedParagraphPreview = selectedParagraphPreview,
            onClearSelectedParagraph = {
                selectedParagraphIndex = null
                selectedParagraphPreview = null
            },
            onAddComment = { name, text, type, pIndex, pPreview ->
                readerViewModel.addComment(name, text, type, pIndex, pPreview)
            },
            onLikeComment = { commentId ->
                readerViewModel.likeComment(commentId)
            },
            onResolveComment = { commentId, isResolved, reply ->
                readerViewModel.resolveComment(commentId, isResolved, reply)
            },
            onDismiss = { showCommentsSheet = false }
        )
    }
}

// Simple quad data holder for reader colors
private data class Quad<A, B, C, D>(val first: A, val second: B, val third: C, val fourth: D)
