package com.example.ui.screens.reader

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
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
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Comment
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.Reply
import androidx.compose.material.icons.filled.Send
import androidx.compose.material.icons.outlined.CheckCircle
import androidx.compose.material.icons.outlined.FavoriteBorder
import androidx.compose.material3.Button
import androidx.compose.material3.Divider
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FilterChip
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.rememberModalBottomSheetState
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
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.model.ChapterComment
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

@OptIn(ExperimentalMaterial3Api::class, ExperimentalLayoutApi::class)
@Composable
fun ChapterCommentsSheet(
    chapterTitle: String,
    comments: List<ChapterComment>,
    selectedParagraphIndex: Int?,
    selectedParagraphPreview: String?,
    onClearSelectedParagraph: () -> Unit,
    onAddComment: (
        authorName: String,
        content: String,
        commentType: String,
        paragraphIndex: Int?,
        paragraphPreview: String?
    ) -> Unit,
    onLikeComment: (Long) -> Unit,
    onResolveComment: (commentId: Long, isResolved: Boolean, reply: String?) -> Unit,
    onDismiss: () -> Unit
) {
    val sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)

    var selectedFilter by remember { mutableStateOf("TODOS") }
    var authorNameInput by remember { mutableStateOf("") }
    var commentTextInput by remember { mutableStateOf("") }
    var selectedCommentType by remember { mutableStateOf("SUGGESTION") }

    var replyingToCommentId by remember { mutableStateOf<Long?>(null) }
    var authorReplyInput by remember { mutableStateOf("") }

    val filterOptions = listOf(
        "TODOS" to "Todos",
        "SUGGESTION" to "Sugestões",
        "GRAMMAR" to "Gramática",
        "PRAISE" to "Elogios",
        "PACING" to "Ritmo",
        "QUESTION" to "Dúvidas"
    )

    val filteredComments = remember(comments, selectedFilter) {
        if (selectedFilter == "TODOS") comments
        else comments.filter { it.commentType == selectedFilter }
    }

    ModalBottomSheet(
        onDismissRequest = onDismiss,
        sheetState = sheetState,
        modifier = Modifier
            .fillMaxHeight(0.9f)
            .testTag("chapter_comments_sheet")
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 20.dp)
        ) {
            // Header
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column {
                    Text(
                        text = "Comentários do Capítulo",
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold
                    )
                    Text(
                        text = chapterTitle,
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis
                    )
                }
                Text(
                    text = "${comments.size} comentários",
                    style = MaterialTheme.typography.labelMedium,
                    color = MaterialTheme.colorScheme.primary,
                    fontWeight = FontWeight.SemiBold
                )
            }

            Spacer(modifier = Modifier.height(12.dp))

            // Paragraph focus banner if selected
            if (selectedParagraphIndex != null && selectedParagraphPreview != null) {
                Surface(
                    color = MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.4f),
                    shape = RoundedCornerShape(8.dp),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(10.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Column(modifier = Modifier.weight(1f)) {
                            Text(
                                text = "Comentando no Parágrafo ${selectedParagraphIndex + 1}:",
                                style = MaterialTheme.typography.labelSmall,
                                fontWeight = FontWeight.Bold,
                                color = MaterialTheme.colorScheme.primary
                            )
                            Text(
                                text = "“${selectedParagraphPreview}”",
                                style = MaterialTheme.typography.bodySmall,
                                fontStyle = FontStyle.Italic,
                                maxLines = 2,
                                overflow = TextOverflow.Ellipsis
                            )
                        }
                        IconButton(onClick = onClearSelectedParagraph) {
                            Icon(
                                imageVector = Icons.Default.Close,
                                contentDescription = "Remover seleção do parágrafo",
                                modifier = Modifier.size(18.dp)
                            )
                        }
                    }
                }
                Spacer(modifier = Modifier.height(8.dp))
            }

            // Filters
            FlowRow(
                horizontalArrangement = Arrangement.spacedBy(6.dp),
                modifier = Modifier.fillMaxWidth()
            ) {
                filterOptions.forEach { (key, label) ->
                    FilterChip(
                        selected = selectedFilter == key,
                        onClick = { selectedFilter = key },
                        label = { Text(label, fontSize = 12.sp) }
                    )
                }
            }

            Spacer(modifier = Modifier.height(10.dp))

            // Comments List
            LazyColumn(
                modifier = Modifier
                    .weight(1f)
                    .fillMaxWidth(),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                if (filteredComments.isEmpty()) {
                    item {
                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(vertical = 40.dp),
                            contentAlignment = Alignment.Center
                        ) {
                            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                                Icon(
                                    imageVector = Icons.Default.Comment,
                                    contentDescription = null,
                                    tint = MaterialTheme.colorScheme.outline,
                                    modifier = Modifier.size(40.dp)
                                )
                                Spacer(modifier = Modifier.height(8.dp))
                                Text(
                                    text = "Nenhum comentário nesta categoria ainda.",
                                    style = MaterialTheme.typography.bodyMedium,
                                    color = MaterialTheme.colorScheme.outline
                                )
                                Text(
                                    text = "Seja o primeiro a deixar feedback ao autor!",
                                    style = MaterialTheme.typography.bodySmall,
                                    color = MaterialTheme.colorScheme.outline
                                )
                            }
                        }
                    }
                } else {
                    items(filteredComments, key = { it.id }) { comment ->
                        CommentItemCard(
                            comment = comment,
                            onLike = { onLikeComment(comment.id) },
                            onToggleResolve = {
                                onResolveComment(comment.id, !comment.isResolved, comment.authorReply)
                            },
                            isReplying = replyingToCommentId == comment.id,
                            replyInput = authorReplyInput,
                            onReplyInputChange = { authorReplyInput = it },
                            onStartReply = {
                                replyingToCommentId = comment.id
                                authorReplyInput = comment.authorReply ?: ""
                            },
                            onCancelReply = { replyingToCommentId = null },
                            onSubmitReply = {
                                onResolveComment(comment.id, true, authorReplyInput)
                                replyingToCommentId = null
                            }
                        )
                    }
                }
            }

            Divider(modifier = Modifier.padding(vertical = 8.dp))

            // Add Comment Input Form
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(bottom = 16.dp),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    OutlinedTextField(
                        value = authorNameInput,
                        onValueChange = { authorNameInput = it },
                        placeholder = { Text("Seu nome / apelido") },
                        singleLine = true,
                        modifier = Modifier
                            .weight(1f)
                            .testTag("comment_author_name_input")
                    )

                    // Type Selector chip
                    CommentTypeSelectorDropdown(
                        selectedType = selectedCommentType,
                        onSelectType = { selectedCommentType = it }
                    )
                }

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    OutlinedTextField(
                        value = commentTextInput,
                        onValueChange = { commentTextInput = it },
                        placeholder = {
                            Text(
                                if (selectedParagraphIndex != null)
                                    "Comentário sobre o parágrafo selecionado..."
                                else
                                    "Comente sobre o capítulo ou faça uma sugestão..."
                            )
                        },
                        minLines = 2,
                        maxLines = 4,
                        modifier = Modifier
                            .weight(1f)
                            .testTag("comment_text_input")
                    )

                    Spacer(modifier = Modifier.width(8.dp))

                    IconButton(
                        onClick = {
                            if (commentTextInput.isNotBlank()) {
                                onAddComment(
                                    authorNameInput.ifBlank { "Leitor Beta" },
                                    commentTextInput,
                                    selectedCommentType,
                                    selectedParagraphIndex,
                                    selectedParagraphPreview
                                )
                                commentTextInput = ""
                                onClearSelectedParagraph()
                            }
                        },
                        enabled = commentTextInput.isNotBlank(),
                        modifier = Modifier
                            .size(48.dp)
                            .clip(CircleShape)
                            .background(
                                if (commentTextInput.isNotBlank()) MaterialTheme.colorScheme.primary
                                else MaterialTheme.colorScheme.surfaceVariant
                            )
                            .testTag("send_comment_button")
                    ) {
                        Icon(
                            imageVector = Icons.Default.Send,
                            contentDescription = "Enviar comentário",
                            tint = if (commentTextInput.isNotBlank()) MaterialTheme.colorScheme.onPrimary
                            else MaterialTheme.colorScheme.outline
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun CommentItemCard(
    comment: ChapterComment,
    onLike: () -> Unit,
    onToggleResolve: () -> Unit,
    isReplying: Boolean,
    replyInput: String,
    onReplyInputChange: (String) -> Unit,
    onStartReply: () -> Unit,
    onCancelReply: () -> Unit,
    onSubmitReply: () -> Unit
) {
    val (typeLabel, typeColor, typeBg) = when (comment.commentType) {
        "SUGGESTION" -> Triple("Sugestão", Color(0xFFD97706), Color(0xFFFEF3C7))
        "PRAISE" -> Triple("Elogio", Color(0xFF15803D), Color(0xFFDCFCE7))
        "GRAMMAR" -> Triple("Gramática", Color(0xFF2563EB), Color(0xFFDBEAFE))
        "PACING" -> Triple("Ritmo", Color(0xFF7C3AED), Color(0xFFEDE9FE))
        "QUESTION" -> Triple("Dúvida", Color(0xFF0D9488), Color(0xFFCCFBF1))
        else -> Triple("Geral", MaterialTheme.colorScheme.primary, MaterialTheme.colorScheme.primaryContainer)
    }

    val dateFormatter = remember { SimpleDateFormat("dd/MM/yyyy HH:mm", Locale.getDefault()) }
    val formattedDate = remember(comment.createdAt) { dateFormatter.format(Date(comment.createdAt)) }

    Surface(
        color = MaterialTheme.colorScheme.surface,
        tonalElevation = 1.dp,
        shape = RoundedCornerShape(12.dp),
        border = androidx.compose.foundation.BorderStroke(
            1.dp,
            if (comment.isResolved) Color(0xFF86EFAC) else MaterialTheme.colorScheme.outlineVariant
        ),
        modifier = Modifier.fillMaxWidth()
    ) {
        Column(modifier = Modifier.padding(12.dp)) {
            // Header: Author, Badge, Date
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Text(
                        text = comment.authorName,
                        fontWeight = FontWeight.Bold,
                        style = MaterialTheme.typography.bodyMedium
                    )

                    Box(
                        modifier = Modifier
                            .clip(RoundedCornerShape(4.dp))
                            .background(typeBg)
                            .padding(horizontal = 6.dp, vertical = 2.dp)
                    ) {
                        Text(
                            text = typeLabel,
                            color = typeColor,
                            fontSize = 10.sp,
                            fontWeight = FontWeight.Bold
                        )
                    }
                }

                Text(
                    text = formattedDate,
                    style = MaterialTheme.typography.labelSmall,
                    color = MaterialTheme.colorScheme.outline
                )
            }

            // Paragraph context snippet if attached to specific paragraph
            if (comment.paragraphPreview != null) {
                Spacer(modifier = Modifier.height(6.dp))
                Surface(
                    color = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f),
                    shape = RoundedCornerShape(6.dp),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text(
                        text = "Parágrafo ${(comment.paragraphIndex ?: 0) + 1}: “${comment.paragraphPreview}”",
                        style = MaterialTheme.typography.bodySmall,
                        fontStyle = FontStyle.Italic,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                        maxLines = 2,
                        overflow = TextOverflow.Ellipsis,
                        modifier = Modifier.padding(6.dp)
                    )
                }
            }

            // Comment text
            Spacer(modifier = Modifier.height(8.dp))
            Text(
                text = comment.content,
                style = MaterialTheme.typography.bodyMedium
            )

            // Author reply if present
            if (!comment.authorReply.isNullOrBlank()) {
                Spacer(modifier = Modifier.height(8.dp))
                Surface(
                    color = MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.3f),
                    shape = RoundedCornerShape(8.dp),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Column(modifier = Modifier.padding(8.dp)) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(
                                imageVector = Icons.Default.Reply,
                                contentDescription = null,
                                tint = MaterialTheme.colorScheme.primary,
                                modifier = Modifier.size(14.dp)
                            )
                            Spacer(modifier = Modifier.width(4.dp))
                            Text(
                                text = "Resposta do Autor:",
                                style = MaterialTheme.typography.labelSmall,
                                fontWeight = FontWeight.Bold,
                                color = MaterialTheme.colorScheme.primary
                            )
                        }
                        Text(
                            text = comment.authorReply,
                            style = MaterialTheme.typography.bodySmall
                        )
                    }
                }
            }

            // Inline Reply Box for Author
            if (isReplying) {
                Spacer(modifier = Modifier.height(8.dp))
                OutlinedTextField(
                    value = replyInput,
                    onValueChange = onReplyInputChange,
                    label = { Text("Sua resposta de autor / nota de revisão") },
                    placeholder = { Text("ex: Excelente apontamento, alterado no rascunho!") },
                    minLines = 2,
                    modifier = Modifier.fillMaxWidth()
                )
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(top = 6.dp),
                    horizontalArrangement = Arrangement.End
                ) {
                    OutlinedButton(onClick = onCancelReply) {
                        Text("Cancelar")
                    }
                    Spacer(modifier = Modifier.width(8.dp))
                    Button(onClick = onSubmitReply, enabled = replyInput.isNotBlank()) {
                        Text("Salvar Resposta")
                    }
                }
            }

            // Action row: Like, Status, Reply button
            Spacer(modifier = Modifier.height(10.dp))
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    IconButton(
                        onClick = onLike,
                        modifier = Modifier.size(32.dp)
                    ) {
                        Icon(
                            imageVector = if (comment.likesCount > 0) Icons.Filled.Favorite else Icons.Outlined.FavoriteBorder,
                            contentDescription = "Curtir comentário",
                            tint = if (comment.likesCount > 0) Color(0xFFEF4444) else MaterialTheme.colorScheme.outline,
                            modifier = Modifier.size(18.dp)
                        )
                    }
                    Text(
                        text = "${comment.likesCount}",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )

                    Spacer(modifier = Modifier.width(16.dp))

                    Text(
                        text = if (isReplying) "Respondendo..." else "Responder",
                        style = MaterialTheme.typography.labelMedium,
                        color = MaterialTheme.colorScheme.primary,
                        modifier = Modifier
                            .clickable(onClick = onStartReply)
                            .padding(4.dp)
                    )
                }

                // Resolved status pill
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier
                        .clip(RoundedCornerShape(12.dp))
                        .background(
                            if (comment.isResolved) Color(0xFFDCFCE7) else MaterialTheme.colorScheme.surfaceVariant
                        )
                        .clickable(onClick = onToggleResolve)
                        .padding(horizontal = 8.dp, vertical = 4.dp)
                ) {
                    Icon(
                        imageVector = if (comment.isResolved) Icons.Filled.CheckCircle else Icons.Outlined.CheckCircle,
                        contentDescription = null,
                        tint = if (comment.isResolved) Color(0xFF16A34A) else MaterialTheme.colorScheme.outline,
                        modifier = Modifier.size(14.dp)
                    )
                    Spacer(modifier = Modifier.width(4.dp))
                    Text(
                        text = if (comment.isResolved) "Incorporado" else "Pendente",
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Medium,
                        color = if (comment.isResolved) Color(0xFF16A34A) else MaterialTheme.colorScheme.outline
                    )
                }
            }
        }
    }
}

@Composable
private fun CommentTypeSelectorDropdown(
    selectedType: String,
    onSelectType: (String) -> Unit
) {
    var expanded by remember { mutableStateOf(false) }

    val options = listOf(
        "SUGGESTION" to "Sugestão",
        "PRAISE" to "Elogio",
        "GRAMMAR" to "Gramática",
        "PACING" to "Ritmo",
        "QUESTION" to "Dúvida"
    )

    val currentLabel = options.find { it.first == selectedType }?.second ?: "Tipo"

    Box {
        Surface(
            shape = RoundedCornerShape(8.dp),
            color = MaterialTheme.colorScheme.secondaryContainer,
            modifier = Modifier
                .clickable { expanded = !expanded }
                .padding(horizontal = 10.dp, vertical = 10.dp)
        ) {
            Text(
                text = currentLabel,
                style = MaterialTheme.typography.labelMedium,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.onSecondaryContainer
            )
        }

        androidx.compose.material3.DropdownMenu(
            expanded = expanded,
            onDismissRequest = { expanded = false }
        ) {
            options.forEach { (key, label) ->
                androidx.compose.material3.DropdownMenuItem(
                    text = { Text(label) },
                    onClick = {
                        onSelectType(key)
                        expanded = false
                    }
                )
            }
        }
    }
}
