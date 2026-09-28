package com.example.ui.screens.book

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
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
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.filled.MenuBook
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.AutoAwesome
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.History
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.RateReview
import androidx.compose.material.icons.filled.Star
import androidx.compose.material.icons.filled.UploadFile
import androidx.compose.material.icons.outlined.Star
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FilterChip
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.ScrollableTabRow
import androidx.compose.material3.Surface
import androidx.compose.material3.Tab
import androidx.compose.material3.TabRow
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.data.model.Book
import com.example.data.model.BookVersion
import com.example.data.model.Chapter
import com.example.data.model.CommunityFeedback
import com.example.ui.screens.dialogs.AddChapterDialog
import com.example.ui.screens.dialogs.AddFeedbackDialog
import com.example.ui.screens.dialogs.CreateVersionDialog
import com.example.ui.viewmodel.BookViewModel
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun BookDetailScreen(
    bookViewModel: BookViewModel,
    onNavigateBack: () -> Unit,
    onOpenChapter: (chapterId: Long, versionId: Long, bookId: Long, bookTitle: String, versionName: String) -> Unit
) {
    val book by bookViewModel.selectedBook.collectAsStateWithLifecycle()
    val versions by bookViewModel.bookVersions.collectAsStateWithLifecycle()
    val selectedVersionId by bookViewModel.selectedVersionId.collectAsStateWithLifecycle()
    val chapters by bookViewModel.chapters.collectAsStateWithLifecycle()
    val feedbacks by bookViewModel.feedbacks.collectAsStateWithLifecycle()

    var selectedTabIndex by remember { mutableIntStateOf(0) }

    var showNewVersionDialog by remember { mutableStateOf(false) }
    var showAddChapterDialog by remember { mutableStateOf(false) }
    var showFeedbackDialog by remember { mutableStateOf(false) }

    val currentVersion = versions.find { it.id == selectedVersionId }
        ?: versions.find { it.isCurrent }
        ?: versions.firstOrNull()

    if (book == null) {
        Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
            Text("Nenhum livro selecionado.")
        }
        return
    }

    val currentBook = book!!

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        text = currentBook.title,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis,
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold
                    )
                },
                navigationIcon = {
                    IconButton(
                        onClick = onNavigateBack,
                        modifier = Modifier.testTag("book_detail_back_button")
                    ) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = "Voltar"
                        )
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.surface
                )
            )
        },
        floatingActionButton = {
            when (selectedTabIndex) {
                0 -> { // Capítulos tab
                    FloatingActionButton(
                        onClick = { showAddChapterDialog = true },
                        modifier = Modifier.testTag("fab_add_chapter")
                    ) {
                        Row(
                            modifier = Modifier.padding(horizontal = 14.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Icon(Icons.Default.UploadFile, contentDescription = "Upload de Capítulo")
                            Spacer(modifier = Modifier.width(6.dp))
                            Text("Upload de Capítulo", fontWeight = FontWeight.SemiBold)
                        }
                    }
                }
                1 -> { // Versões tab
                    FloatingActionButton(
                        onClick = { showNewVersionDialog = true },
                        modifier = Modifier.testTag("fab_create_version")
                    ) {
                        Row(
                            modifier = Modifier.padding(horizontal = 14.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Icon(Icons.Default.AutoAwesome, contentDescription = "Nova Versão")
                            Spacer(modifier = Modifier.width(6.dp))
                            Text("Nova Versão", fontWeight = FontWeight.SemiBold)
                        }
                    }
                }
                2 -> { // Feedback tab
                    FloatingActionButton(
                        onClick = { showFeedbackDialog = true },
                        modifier = Modifier.testTag("fab_add_feedback")
                    ) {
                        Row(
                            modifier = Modifier.padding(horizontal = 14.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Icon(Icons.Default.RateReview, contentDescription = "Deixar Feedback")
                            Spacer(modifier = Modifier.width(6.dp))
                            Text("Avaliar Versão", fontWeight = FontWeight.SemiBold)
                        }
                    }
                }
            }
        }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
        ) {
            // Book Header Info Card
            BookHeaderBanner(
                book = currentBook,
                currentVersionName = currentVersion?.versionName ?: currentBook.currentVersionName,
                onStatusChange = { newStatus ->
                    bookViewModel.updateBookStatus(currentBook.id, newStatus)
                }
            )

            // Tabs
            TabRow(
                selectedTabIndex = selectedTabIndex,
                containerColor = MaterialTheme.colorScheme.surface
            ) {
                Tab(
                    selected = selectedTabIndex == 0,
                    onClick = { selectedTabIndex = 0 },
                    text = { Text("Capítulos (${chapters.size})") },
                    icon = { Icon(Icons.AutoMirrored.Filled.MenuBook, contentDescription = null, modifier = Modifier.size(18.dp)) }
                )
                Tab(
                    selected = selectedTabIndex == 1,
                    onClick = { selectedTabIndex = 1 },
                    text = { Text("Versões (${versions.size})") },
                    icon = { Icon(Icons.Default.History, contentDescription = null, modifier = Modifier.size(18.dp)) }
                )
                Tab(
                    selected = selectedTabIndex == 2,
                    onClick = { selectedTabIndex = 2 },
                    text = { Text("Feedback (${feedbacks.size})") },
                    icon = { Icon(Icons.Default.RateReview, contentDescription = null, modifier = Modifier.size(18.dp)) }
                )
            }

            // Tab Content
            when (selectedTabIndex) {
                0 -> ChaptersTabContent(
                    chapters = chapters,
                    versions = versions,
                    selectedVersion = currentVersion,
                    onSelectVersion = { versionId ->
                        bookViewModel.selectVersion(versionId)
                    },
                    onOpenChapter = { chapter ->
                        onOpenChapter(
                            chapter.id,
                            chapter.versionId,
                            currentBook.id,
                            currentBook.title,
                            currentVersion?.versionName ?: currentBook.currentVersionName
                        )
                    }
                )
                1 -> VersionsTabContent(
                    versions = versions,
                    selectedVersionId = currentVersion?.id,
                    onSelectVersionToView = { versionId ->
                        bookViewModel.selectVersion(versionId)
                        selectedTabIndex = 0 // Switch to chapters tab to view this version's content
                    },
                    onSetAsActiveVersion = { versionId ->
                        bookViewModel.switchActiveVersion(currentBook.id, versionId)
                    }
                )
                2 -> FeedbacksTabContent(
                    feedbacks = feedbacks,
                    versionName = currentVersion?.versionName ?: currentBook.currentVersionName,
                    onOpenFeedbackDialog = { showFeedbackDialog = true }
                )
            }
        }
    }

    // Dialogs
    if (showNewVersionDialog && currentVersion != null) {
        CreateVersionDialog(
            currentVersionName = currentVersion.versionName,
            onDismiss = { showNewVersionDialog = false },
            onConfirm = { name, releaseNotes, targetNote, copyChapters ->
                bookViewModel.createNewVersion(
                    bookId = currentBook.id,
                    versionName = name,
                    releaseNotes = releaseNotes,
                    targetAudienceNote = targetNote,
                    copyChaptersFromCurrent = copyChapters
                )
            }
        )
    }

    if (showAddChapterDialog && currentVersion != null) {
        AddChapterDialog(
            nextChapterNumber = chapters.size + 1,
            onDismiss = { showAddChapterDialog = false },
            onConfirm = { num, title, content, authorNotes ->
                bookViewModel.addChapter(
                    bookId = currentBook.id,
                    versionId = currentVersion.id,
                    chapterNumber = num,
                    title = title,
                    content = content,
                    authorNotes = authorNotes
                )
            }
        )
    }

    if (showFeedbackDialog && currentVersion != null) {
        AddFeedbackDialog(
            versionName = currentVersion.versionName,
            onDismiss = { showFeedbackDialog = false },
            onConfirm = { name, rating, text, pacing, depth, wouldRecommend ->
                bookViewModel.submitFeedback(
                    bookId = currentBook.id,
                    versionId = currentVersion.id,
                    reviewerName = name,
                    rating = rating,
                    feedbackText = text,
                    pacing = pacing,
                    depth = depth,
                    wouldRecommend = wouldRecommend
                )
            }
        )
    }
}

@OptIn(ExperimentalLayoutApi::class)
@Composable
private fun BookHeaderBanner(
    book: Book,
    currentVersionName: String,
    onStatusChange: (String) -> Unit
) {
    var statusMenuExpanded by remember { mutableStateOf(false) }

    val statusLabel = when (book.status) {
        "EM_REVISAO_BETA" -> "Em Revisão Beta"
        "RASCUNHO" -> "Rascunho Inicial"
        "PRONTO_PARA_LANCAMENTO" -> "Pronto p/ Lançamento"
        else -> book.status
    }

    val (statusColor, statusBg) = when (book.status) {
        "EM_REVISAO_BETA" -> Color(0xFFD97706) to Color(0xFFFEF3C7)
        "RASCUNHO" -> Color(0xFF475569) to Color(0xFFE2E8F0)
        "PRONTO_PARA_LANCAMENTO" -> Color(0xFF16A34A) to Color(0xFFDCFCE7)
        else -> MaterialTheme.colorScheme.primary to MaterialTheme.colorScheme.primaryContainer
    }

    Surface(
        color = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f),
        modifier = Modifier.fillMaxWidth()
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                // Version badge
                Surface(
                    color = MaterialTheme.colorScheme.tertiaryContainer,
                    shape = RoundedCornerShape(4.dp)
                ) {
                    Text(
                        text = "Versão: $currentVersionName",
                        style = MaterialTheme.typography.labelSmall,
                        color = MaterialTheme.colorScheme.onTertiaryContainer,
                        fontWeight = FontWeight.Bold,
                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 3.dp)
                    )
                }

                // Status chip with dropdown to change
                Box {
                    Surface(
                        color = statusBg,
                        shape = RoundedCornerShape(12.dp),
                        modifier = Modifier
                            .clickable { statusMenuExpanded = true }
                            .padding(2.dp)
                    ) {
                        Row(
                            modifier = Modifier.padding(horizontal = 10.dp, vertical = 4.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(
                                text = statusLabel,
                                color = statusColor,
                                fontSize = 11.sp,
                                fontWeight = FontWeight.Bold
                            )
                        }
                    }

                    DropdownMenu(
                        expanded = statusMenuExpanded,
                        onDismissRequest = { statusMenuExpanded = false }
                    ) {
                        DropdownMenuItem(
                            text = { Text("Em Revisão Beta") },
                            onClick = {
                                onStatusChange("EM_REVISAO_BETA")
                                statusMenuExpanded = false
                            }
                        )
                        DropdownMenuItem(
                            text = { Text("Rascunho Inicial") },
                            onClick = {
                                onStatusChange("RASCUNHO")
                                statusMenuExpanded = false
                            }
                        )
                        DropdownMenuItem(
                            text = { Text("Pronto para Lançamento") },
                            onClick = {
                                onStatusChange("PRONTO_PARA_LANCAMENTO")
                                statusMenuExpanded = false
                            }
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(8.dp))

            // Multiple categories tags
            FlowRow(
                horizontalArrangement = Arrangement.spacedBy(6.dp),
                verticalArrangement = Arrangement.spacedBy(4.dp),
                modifier = Modifier.fillMaxWidth()
            ) {
                book.genreList.forEach { genre ->
                    Surface(
                        color = MaterialTheme.colorScheme.primaryContainer,
                        shape = RoundedCornerShape(4.dp)
                    ) {
                        Text(
                            text = genre,
                            style = MaterialTheme.typography.labelSmall,
                            color = MaterialTheme.colorScheme.onPrimaryContainer,
                            fontWeight = FontWeight.Bold,
                            modifier = Modifier.padding(horizontal = 8.dp, vertical = 3.dp)
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(8.dp))

            Text(
                text = "Autor(a): ${book.author}",
                style = MaterialTheme.typography.bodyMedium,
                fontWeight = FontWeight.Medium,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )

            if (book.synopsis.isNotBlank()) {
                Spacer(modifier = Modifier.height(6.dp))
                Text(
                    text = book.synopsis,
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.85f),
                    maxLines = 3,
                    overflow = TextOverflow.Ellipsis
                )
            }
        }
    }
}

@Composable
private fun ChaptersTabContent(
    chapters: List<Chapter>,
    versions: List<BookVersion>,
    selectedVersion: BookVersion?,
    onSelectVersion: (Long) -> Unit,
    onOpenChapter: (Chapter) -> Unit
) {
    var versionDropdownExpanded by remember { mutableStateOf(false) }

    Column(modifier = Modifier.fillMaxSize()) {
        // Version selector sub-bar
        Surface(
            color = MaterialTheme.colorScheme.surface,
            tonalElevation = 1.dp,
            modifier = Modifier.fillMaxWidth()
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp, vertical = 10.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "Exibindo versão:",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )

                Box {
                    OutlinedButton(
                        onClick = { versionDropdownExpanded = true }
                    ) {
                        Text(
                            text = selectedVersion?.versionName ?: "Selecionar Versão",
                            fontWeight = FontWeight.Bold
                        )
                    }

                    DropdownMenu(
                        expanded = versionDropdownExpanded,
                        onDismissRequest = { versionDropdownExpanded = false }
                    ) {
                        versions.forEach { ver ->
                            DropdownMenuItem(
                                text = {
                                    Row(verticalAlignment = Alignment.CenterVertically) {
                                        Text(ver.versionName)
                                        if (ver.isCurrent) {
                                            Spacer(modifier = Modifier.width(6.dp))
                                            Surface(
                                                color = Color(0xFFDCFCE7),
                                                shape = RoundedCornerShape(4.dp)
                                            ) {
                                                Text(
                                                    "ATUAL",
                                                    fontSize = 9.sp,
                                                    fontWeight = FontWeight.Bold,
                                                    color = Color(0xFF16A34A),
                                                    modifier = Modifier.padding(horizontal = 4.dp, vertical = 1.dp)
                                                )
                                            }
                                        }
                                    }
                                },
                                onClick = {
                                    onSelectVersion(ver.id)
                                    versionDropdownExpanded = false
                                }
                            )
                        }
                    }
                }
            }
        }

        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = 16.dp, vertical = 12.dp),
            verticalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            if (chapters.isEmpty()) {
                item {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 50.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        Column(horizontalAlignment = Alignment.CenterHorizontally) {
                            Icon(
                                imageVector = Icons.AutoMirrored.Filled.MenuBook,
                                contentDescription = null,
                                tint = MaterialTheme.colorScheme.outline,
                                modifier = Modifier.size(48.dp)
                            )
                            Spacer(modifier = Modifier.height(10.dp))
                            Text(
                                text = "Nenhum capítulo cadastrado nesta versão.",
                                style = MaterialTheme.typography.bodyMedium,
                                color = MaterialTheme.colorScheme.outline
                            )
                            Text(
                                text = "Toque no botão 'Upload de Capítulo' abaixo para carregar um arquivo .txt ou .md.",
                                style = MaterialTheme.typography.bodySmall,
                                color = MaterialTheme.colorScheme.outline
                            )
                        }
                    }
                }
            } else {
                items(chapters, key = { it.id }) { chapter ->
                    Card(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clickable { onOpenChapter(chapter) }
                            .testTag("chapter_item_${chapter.id}"),
                        colors = CardDefaults.cardColors(
                            containerColor = MaterialTheme.colorScheme.surface
                        ),
                        border = androidx.compose.foundation.BorderStroke(
                            1.dp,
                            MaterialTheme.colorScheme.outlineVariant
                        ),
                        shape = RoundedCornerShape(12.dp)
                    ) {
                        Column(modifier = Modifier.padding(16.dp)) {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Text(
                                    text = chapter.title,
                                    style = MaterialTheme.typography.titleSmall,
                                    fontWeight = FontWeight.Bold
                                )

                                Button(
                                    onClick = { onOpenChapter(chapter) },
                                    modifier = Modifier.testTag("read_chapter_button_${chapter.id}")
                                ) {
                                    Text("Ler")
                                }
                            }

                            Spacer(modifier = Modifier.height(6.dp))
                            Text(
                                text = chapter.content.take(160) + if (chapter.content.length > 160) "..." else "",
                                style = MaterialTheme.typography.bodySmall,
                                color = MaterialTheme.colorScheme.onSurfaceVariant,
                                maxLines = 2,
                                overflow = TextOverflow.Ellipsis
                            )

                            if (chapter.authorNotes.isNotBlank()) {
                                Spacer(modifier = Modifier.height(8.dp))
                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    Icon(
                                        imageVector = Icons.Default.Info,
                                        contentDescription = null,
                                        tint = MaterialTheme.colorScheme.tertiary,
                                        modifier = Modifier.size(14.dp)
                                    )
                                    Spacer(modifier = Modifier.width(4.dp))
                                    Text(
                                        text = "Nota aos betas: ${chapter.authorNotes}",
                                        style = MaterialTheme.typography.labelSmall,
                                        fontStyle = FontStyle.Italic,
                                        color = MaterialTheme.colorScheme.tertiary,
                                        maxLines = 1,
                                        overflow = TextOverflow.Ellipsis
                                    )
                                }
                            }
                        }
                    }
                }
            }

            // Bottom padding for FAB
            item { Spacer(modifier = Modifier.height(80.dp)) }
        }
    }
}

@Composable
private fun VersionsTabContent(
    versions: List<BookVersion>,
    selectedVersionId: Long?,
    onSelectVersionToView: (Long) -> Unit,
    onSetAsActiveVersion: (Long) -> Unit
) {
    val dateFormatter = remember { SimpleDateFormat("dd/MM/yyyy", Locale.getDefault()) }

    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .padding(horizontal = 16.dp, vertical = 12.dp),
        verticalArrangement = Arrangement.spacedBy(14.dp)
    ) {
        items(versions, key = { it.id }) { ver ->
            val isCurrent = ver.isCurrent
            val formattedDate = dateFormatter.format(Date(ver.createdAt))

            Card(
                colors = CardDefaults.cardColors(
                    containerColor = if (isCurrent) MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.25f)
                    else MaterialTheme.colorScheme.surface
                ),
                border = androidx.compose.foundation.BorderStroke(
                    width = if (isCurrent) 2.dp else 1.dp,
                    color = if (isCurrent) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.outlineVariant
                ),
                shape = RoundedCornerShape(12.dp),
                modifier = Modifier
                    .fillMaxWidth()
                    .testTag("version_item_${ver.versionName}")
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    // Header: Version Name, Current badge, Date
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Text(
                                text = ver.versionName,
                                style = MaterialTheme.typography.titleMedium,
                                fontWeight = FontWeight.Bold,
                                color = if (isCurrent) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.onSurface
                            )

                            if (isCurrent) {
                                Spacer(modifier = Modifier.width(8.dp))
                                Surface(
                                    color = Color(0xFFDCFCE7),
                                    shape = RoundedCornerShape(4.dp)
                                ) {
                                    Text(
                                        text = "VERSÃO ATIVA",
                                        color = Color(0xFF16A34A),
                                        fontSize = 10.sp,
                                        fontWeight = FontWeight.Bold,
                                        modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                                    )
                                }
                            }
                        }

                        Text(
                            text = formattedDate,
                            style = MaterialTheme.typography.labelSmall,
                            color = MaterialTheme.colorScheme.outline
                        )
                    }

                    Spacer(modifier = Modifier.height(10.dp))

                    // Changelog / Release notes
                    Text(
                        text = "Changelog / O que mudou:",
                        style = MaterialTheme.typography.labelMedium,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.onSurface
                    )
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(
                        text = ver.releaseNotes,
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )

                    // Target audience note if present
                    if (ver.targetAudienceNote.isNotBlank()) {
                        Spacer(modifier = Modifier.height(8.dp))
                        Surface(
                            color = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.6f),
                            shape = RoundedCornerShape(6.dp),
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Text(
                                text = "Instrução aos Leitores: ${ver.targetAudienceNote}",
                                style = MaterialTheme.typography.bodySmall,
                                fontStyle = FontStyle.Italic,
                                modifier = Modifier.padding(8.dp)
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(12.dp))

                    // Action buttons
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.End,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        if (!isCurrent) {
                            OutlinedButton(
                                onClick = { onSetAsActiveVersion(ver.id) }
                            ) {
                                Text("Tornar Ativa")
                            }
                            Spacer(modifier = Modifier.width(8.dp))
                        }

                        Button(
                            onClick = { onSelectVersionToView(ver.id) }
                        ) {
                            Text("Ver Capítulos")
                        }
                    }
                }
            }
        }

        item { Spacer(modifier = Modifier.height(80.dp)) }
    }
}

@Composable
private fun FeedbacksTabContent(
    feedbacks: List<CommunityFeedback>,
    versionName: String,
    onOpenFeedbackDialog: () -> Unit
) {
    val averageRating = remember(feedbacks) {
        if (feedbacks.isEmpty()) 0.0
        else feedbacks.map { it.rating }.average()
    }

    val recommendPercentage = remember(feedbacks) {
        if (feedbacks.isEmpty()) 0
        else ((feedbacks.count { it.wouldRecommend }.toDouble() / feedbacks.size) * 100).toInt()
    }

    val dateFormatter = remember { SimpleDateFormat("dd/MM/yyyy", Locale.getDefault()) }

    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .padding(horizontal = 16.dp, vertical = 12.dp),
        verticalArrangement = Arrangement.spacedBy(14.dp)
    ) {
        // Feedback summary statistics card
        item {
            Card(
                colors = CardDefaults.cardColors(
                    containerColor = MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.3f)
                ),
                shape = RoundedCornerShape(12.dp),
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Text(
                        text = "Termômetro da Comunidade Beta",
                        style = MaterialTheme.typography.titleSmall,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.primary
                    )
                    Spacer(modifier = Modifier.height(10.dp))

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceAround
                    ) {
                        Column(horizontalAlignment = Alignment.CenterHorizontally) {
                            Text(
                                text = if (feedbacks.isEmpty()) "—" else String.format(Locale.getDefault(), "%.1f", averageRating),
                                style = MaterialTheme.typography.headlineMedium,
                                fontWeight = FontWeight.Bold,
                                color = MaterialTheme.colorScheme.primary
                            )
                            Row {
                                for (i in 1..5) {
                                    Icon(
                                        imageVector = if (i <= averageRating.toInt()) Icons.Filled.Star else Icons.Outlined.Star,
                                        contentDescription = null,
                                        tint = MaterialTheme.colorScheme.tertiary,
                                        modifier = Modifier.size(14.dp)
                                    )
                                }
                            }
                            Text(
                                text = "Média de Avaliação",
                                style = MaterialTheme.typography.labelSmall,
                                color = MaterialTheme.colorScheme.outline
                            )
                        }

                        Column(horizontalAlignment = Alignment.CenterHorizontally) {
                            Text(
                                text = if (feedbacks.isEmpty()) "—" else "$recommendPercentage%",
                                style = MaterialTheme.typography.headlineMedium,
                                fontWeight = FontWeight.Bold,
                                color = Color(0xFF16A34A)
                            )
                            Text(
                                text = "Aprovação p/ Lançamento",
                                style = MaterialTheme.typography.labelSmall,
                                color = MaterialTheme.colorScheme.outline
                            )
                        }

                        Column(horizontalAlignment = Alignment.CenterHorizontally) {
                            Text(
                                text = "${feedbacks.size}",
                                style = MaterialTheme.typography.headlineMedium,
                                fontWeight = FontWeight.Bold,
                                color = MaterialTheme.colorScheme.onSurface
                            )
                            Text(
                                text = "Leitores Avaliadores",
                                style = MaterialTheme.typography.labelSmall,
                                color = MaterialTheme.colorScheme.outline
                            )
                        }
                    }
                }
            }
        }

        // List of feedbacks
        if (feedbacks.isEmpty()) {
            item {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 40.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Icon(
                            imageVector = Icons.Default.RateReview,
                            contentDescription = null,
                            tint = MaterialTheme.colorScheme.outline,
                            modifier = Modifier.size(40.dp)
                        )
                        Spacer(modifier = Modifier.height(8.dp))
                        Text(
                            text = "Ainda não há avaliações para esta obra.",
                            style = MaterialTheme.typography.bodyMedium,
                            color = MaterialTheme.colorScheme.outline
                        )
                        Spacer(modifier = Modifier.height(6.dp))
                        Button(onClick = onOpenFeedbackDialog) {
                            Text("Seja o primeiro a avaliar")
                        }
                    }
                }
            }
        } else {
            items(feedbacks, key = { it.id }) { fb ->
                Card(
                    colors = CardDefaults.cardColors(
                        containerColor = MaterialTheme.colorScheme.surface
                    ),
                    border = androidx.compose.foundation.BorderStroke(
                        1.dp,
                        MaterialTheme.colorScheme.outlineVariant
                    ),
                    shape = RoundedCornerShape(12.dp),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Column(modifier = Modifier.padding(14.dp)) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(
                                text = fb.reviewerName,
                                fontWeight = FontWeight.Bold,
                                style = MaterialTheme.typography.bodyMedium
                            )

                            Row {
                                for (i in 1..5) {
                                    Icon(
                                        imageVector = if (i <= fb.rating) Icons.Filled.Star else Icons.Outlined.Star,
                                        contentDescription = null,
                                        tint = MaterialTheme.colorScheme.tertiary,
                                        modifier = Modifier.size(16.dp)
                                    )
                                }
                            }
                        }

                        Spacer(modifier = Modifier.height(6.dp))

                        Row(
                            horizontalArrangement = Arrangement.spacedBy(8.dp),
                            modifier = Modifier.padding(vertical = 2.dp)
                        ) {
                            Surface(
                                color = MaterialTheme.colorScheme.surfaceVariant,
                                shape = RoundedCornerShape(4.dp)
                            ) {
                                Text(
                                    text = "Ritmo: ${fb.pacingScore}",
                                    fontSize = 11.sp,
                                    modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                                )
                            }

                            Surface(
                                color = MaterialTheme.colorScheme.surfaceVariant,
                                shape = RoundedCornerShape(4.dp)
                            ) {
                                Text(
                                    text = "Personagens: ${fb.characterDepth}",
                                    fontSize = 11.sp,
                                    modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                                )
                            }

                            if (fb.wouldRecommend) {
                                Surface(
                                    color = Color(0xFFDCFCE7),
                                    shape = RoundedCornerShape(4.dp)
                                ) {
                                    Text(
                                        text = "Recomenda ✓",
                                        color = Color(0xFF16A34A),
                                        fontSize = 11.sp,
                                        fontWeight = FontWeight.Bold,
                                        modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                                    )
                                }
                            }
                        }

                        Spacer(modifier = Modifier.height(8.dp))
                        Text(
                            text = fb.feedbackText,
                            style = MaterialTheme.typography.bodyMedium
                        )

                        Spacer(modifier = Modifier.height(6.dp))
                        Text(
                            text = dateFormatter.format(Date(fb.createdAt)),
                            style = MaterialTheme.typography.labelSmall,
                            color = MaterialTheme.colorScheme.outline
                        )
                    }
                }
            }
        }

        item { Spacer(modifier = Modifier.height(80.dp)) }
    }
}
