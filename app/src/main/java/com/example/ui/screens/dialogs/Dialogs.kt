package com.example.ui.screens.dialogs

import android.content.Context
import android.net.Uri
import android.provider.OpenableColumns
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
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
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Description
import androidx.compose.material.icons.filled.Star
import androidx.compose.material.icons.filled.UploadFile
import androidx.compose.material.icons.outlined.Star
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Checkbox
import androidx.compose.material3.FilterChip
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

@OptIn(ExperimentalLayoutApi::class)
@Composable
fun CreateBookDialog(
    onDismiss: () -> Unit,
    onConfirm: (
        title: String,
        author: String,
        synopsis: String,
        genre: String,
        versionName: String,
        releaseNotes: String,
        coverColorHex: String
    ) -> Unit
) {
    var title by remember { mutableStateOf("") }
    var author by remember { mutableStateOf("") }
    var synopsis by remember { mutableStateOf("") }
    var versionName by remember { mutableStateOf("v1.0-draft") }
    var releaseNotes by remember { mutableStateOf("Primeira versão para avaliação de ritmo e personagens.") }
    var selectedColor by remember { mutableStateOf("#1E3A8A") }

    val defaultGenres = listOf(
        "Ficção Científica", "Cyberpunk", "Distopia", "Fantasia", "Alta Fantasia",
        "Suspense", "Mistério", "Thriller", "Romance", "Terror", "Aventura",
        "Drama", "Histórico", "Policial", "Jovem Adulto", "Filosofia", "Não-Ficção"
    )
    val availableGenres = remember { mutableStateListOf(*defaultGenres.toTypedArray()) }
    val selectedGenres = remember { mutableStateListOf("Ficção Científica") }
    var newCustomGenre by remember { mutableStateOf("") }

    val colors = listOf("#1E3A8A", "#0F766E", "#7C2D12", "#581C87", "#334155", "#B45309")

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("Cadastrar Novo Livro para Revisão") },
        text = {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .verticalScroll(rememberScrollState()),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                OutlinedTextField(
                    value = title,
                    onValueChange = { title = it },
                    label = { Text("Título do Livro *") },
                    singleLine = true,
                    modifier = Modifier
                        .fillMaxWidth()
                        .testTag("book_title_input")
                )

                OutlinedTextField(
                    value = author,
                    onValueChange = { author = it },
                    label = { Text("Nome do Autor *") },
                    singleLine = true,
                    modifier = Modifier
                        .fillMaxWidth()
                        .testTag("book_author_input")
                )

                OutlinedTextField(
                    value = synopsis,
                    onValueChange = { synopsis = it },
                    label = { Text("Sinopse / Premissa") },
                    minLines = 3,
                    maxLines = 5,
                    modifier = Modifier.fillMaxWidth()
                )

                // Categorias Múltiplas
                Column(verticalArrangement = Arrangement.spacedBy(6.dp)) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = "Categorias / Gêneros (${selectedGenres.size} selecionados)",
                            style = MaterialTheme.typography.labelLarge,
                            fontWeight = FontWeight.Bold
                        )
                    }

                    Text(
                        text = "Toque para selecionar uma ou mais categorias:",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )

                    FlowRow(
                        horizontalArrangement = Arrangement.spacedBy(6.dp),
                        verticalArrangement = Arrangement.spacedBy(4.dp),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        availableGenres.forEach { genre ->
                            val isSelected = selectedGenres.contains(genre)
                            FilterChip(
                                selected = isSelected,
                                onClick = {
                                    if (isSelected) {
                                        if (selectedGenres.size > 1) {
                                            selectedGenres.remove(genre)
                                        }
                                    } else {
                                        selectedGenres.add(genre)
                                    }
                                },
                                label = { Text(genre, fontSize = 12.sp) }
                            )
                        }
                    }

                    // Adicionar Categoria Personalizada
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(top = 4.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        OutlinedTextField(
                            value = newCustomGenre,
                            onValueChange = { newCustomGenre = it },
                            placeholder = { Text("Adicionar outra categoria...") },
                            singleLine = true,
                            modifier = Modifier.weight(1f)
                        )
                        Spacer(modifier = Modifier.width(6.dp))
                        IconButton(
                            onClick = {
                                val trimmed = newCustomGenre.trim()
                                if (trimmed.isNotBlank() && !availableGenres.contains(trimmed)) {
                                    availableGenres.add(trimmed)
                                    selectedGenres.add(trimmed)
                                    newCustomGenre = ""
                                }
                            },
                            enabled = newCustomGenre.isNotBlank()
                        ) {
                            Icon(Icons.Default.Add, contentDescription = "Adicionar categoria")
                        }
                    }
                }

                Text("Cor da Capa", style = MaterialTheme.typography.labelLarge)
                Row(
                    horizontalArrangement = Arrangement.spacedBy(10.dp),
                    modifier = Modifier.padding(vertical = 4.dp)
                ) {
                    colors.forEach { hex ->
                        val color = Color(android.graphics.Color.parseColor(hex))
                        Box(
                            modifier = Modifier
                                .size(36.dp)
                                .clip(CircleShape)
                                .background(color)
                                .border(
                                    width = if (selectedColor == hex) 3.dp else 1.dp,
                                    color = if (selectedColor == hex) MaterialTheme.colorScheme.primary else Color.Transparent,
                                    shape = CircleShape
                                )
                                .clickable { selectedColor = hex }
                        )
                    }
                }

                OutlinedTextField(
                    value = versionName,
                    onValueChange = { versionName = it },
                    label = { Text("Identificador da Versão Inicial") },
                    placeholder = { Text("ex: v1.0-draft") },
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth()
                )

                OutlinedTextField(
                    value = releaseNotes,
                    onValueChange = { releaseNotes = it },
                    label = { Text("Notas de Versão / Changelog Inicial") },
                    minLines = 2,
                    modifier = Modifier.fillMaxWidth()
                )
            }
        },
        confirmButton = {
            Button(
                onClick = {
                    if (title.isNotBlank() && author.isNotBlank()) {
                        val finalGenre = if (selectedGenres.isNotEmpty()) {
                            selectedGenres.joinToString(", ")
                        } else {
                            "Geral"
                        }
                        onConfirm(
                            title,
                            author,
                            synopsis,
                            finalGenre,
                            versionName,
                            releaseNotes,
                            selectedColor
                        )
                        onDismiss()
                    }
                },
                enabled = title.isNotBlank() && author.isNotBlank() && selectedGenres.isNotEmpty(),
                modifier = Modifier.testTag("submit_create_book_button")
            ) {
                Text("Criar Livro")
            }
        },
        dismissButton = {
            OutlinedButton(onClick = onDismiss) {
                Text("Cancelar")
            }
        }
    )
}

@Composable
fun CreateVersionDialog(
    currentVersionName: String,
    onDismiss: () -> Unit,
    onConfirm: (
        versionName: String,
        releaseNotes: String,
        targetAudienceNote: String,
        copyChapters: Boolean
    ) -> Unit
) {
    var versionName by remember { mutableStateOf("") }
    var releaseNotes by remember { mutableStateOf("") }
    var targetAudienceNote by remember { mutableStateOf("") }
    var copyChapters by remember { mutableStateOf(true) }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("Lançar Nova Versão do Livro") },
        text = {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .verticalScroll(rememberScrollState()),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                Text(
                    text = "Versão atual: $currentVersionName",
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.primary
                )

                OutlinedTextField(
                    value = versionName,
                    onValueChange = { versionName = it },
                    label = { Text("Novo Número da Versão *") },
                    placeholder = { Text("ex: v1.1-beta, v2.0-rc") },
                    singleLine = true,
                    modifier = Modifier
                        .fillMaxWidth()
                        .testTag("new_version_name_input")
                )

                OutlinedTextField(
                    value = releaseNotes,
                    onValueChange = { releaseNotes = it },
                    label = { Text("Changelog / O que mudou nesta versão? *") },
                    placeholder = { Text("ex: Capítulo 2 reescrito com base no feedback; correções de ritmo...") },
                    minLines = 3,
                    maxLines = 6,
                    modifier = Modifier
                        .fillMaxWidth()
                        .testTag("version_release_notes_input")
                )

                OutlinedTextField(
                    value = targetAudienceNote,
                    onValueChange = { targetAudienceNote = it },
                    label = { Text("Instruções para os Leitores Beta") },
                    placeholder = { Text("ex: Por favor atentem ao desfecho do mistério...") },
                    minLines = 2,
                    modifier = Modifier.fillMaxWidth()
                )

                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(8.dp))
                        .clickable { copyChapters = !copyChapters }
                        .padding(vertical = 4.dp)
                ) {
                    Checkbox(
                        checked = copyChapters,
                        onCheckedChange = { copyChapters = it }
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = "Duplicar capítulos da versão atual para edição contínua",
                        style = MaterialTheme.typography.bodySmall
                    )
                }
            }
        },
        confirmButton = {
            Button(
                onClick = {
                    if (versionName.isNotBlank() && releaseNotes.isNotBlank()) {
                        onConfirm(versionName, releaseNotes, targetAudienceNote, copyChapters)
                        onDismiss()
                    }
                },
                enabled = versionName.isNotBlank() && releaseNotes.isNotBlank(),
                modifier = Modifier.testTag("confirm_new_version_button")
            ) {
                Text("Publicar Versão")
            }
        },
        dismissButton = {
            OutlinedButton(onClick = onDismiss) {
                Text("Cancelar")
            }
        }
    )
}

@Composable
fun AddChapterDialog(
    nextChapterNumber: Int,
    onDismiss: () -> Unit,
    onConfirm: (chapterNumber: Int, title: String, content: String, authorNotes: String) -> Unit
) {
    val context = LocalContext.current

    var chapterNumberText by remember { mutableStateOf(nextChapterNumber.toString()) }
    var title by remember { mutableStateOf("Capítulo $nextChapterNumber: ") }
    var content by remember { mutableStateOf("") }
    var authorNotes by remember { mutableStateOf("") }
    var uploadedFileName by remember { mutableStateOf<String?>(null) }
    var uploadStatusMessage by remember { mutableStateOf<String?>(null) }

    // File picker launcher for .txt, .md, document files
    val filePickerLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.GetContent()
    ) { uri: Uri? ->
        if (uri != null) {
            try {
                // Get filename
                val fileName = getFileNameFromUri(context, uri) ?: "capitulo.txt"
                uploadedFileName = fileName

                // Read file content
                context.contentResolver.openInputStream(uri)?.use { stream ->
                    val text = stream.bufferedReader().readText()
                    content = text

                    // Suggest clean title from filename if current title is default
                    val cleanName = fileName.substringBeforeLast(".")
                        .replace("_", " ")
                        .replace("-", " ")
                    if (title.isBlank() || title.startsWith("Capítulo $nextChapterNumber:")) {
                        title = "Capítulo $nextChapterNumber: $cleanName"
                    }

                    val wordCount = text.split("\\s+".toRegex()).filter { it.isNotBlank() }.size
                    uploadStatusMessage = "Arquivo \"$fileName\" carregado com sucesso ($wordCount palavras)."
                }
            } catch (e: Exception) {
                uploadStatusMessage = "Erro ao ler arquivo: ${e.localizedMessage}"
            }
        }
    }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(
                    imageVector = Icons.Default.UploadFile,
                    contentDescription = null,
                    tint = MaterialTheme.colorScheme.primary,
                    modifier = Modifier.size(24.dp)
                )
                Spacer(modifier = Modifier.width(8.dp))
                Text("Upload & Adicionar Capítulo")
            }
        },
        text = {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .verticalScroll(rememberScrollState()),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                // Prominent File Upload Section
                Card(
                    colors = CardDefaults.cardColors(
                        containerColor = MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.4f)
                    ),
                    shape = RoundedCornerShape(12.dp),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Column(
                        modifier = Modifier.padding(14.dp),
                        verticalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        Text(
                            text = "Upload de Arquivo de Texto",
                            style = MaterialTheme.typography.titleSmall,
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.primary
                        )
                        Text(
                            text = "Importe capítulos de arquivos .txt, .md ou texto salvos no seu aparelho.",
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )

                        Button(
                            onClick = {
                                filePickerLauncher.launch("*/*")
                            },
                            modifier = Modifier
                                .fillMaxWidth()
                                .testTag("button_upload_chapter_file")
                        ) {
                            Icon(Icons.Default.UploadFile, contentDescription = null)
                            Spacer(modifier = Modifier.width(8.dp))
                            Text(
                                if (uploadedFileName != null) "Trocar Arquivo Selecionado"
                                else "Selecionar Arquivo do Capítulo"
                            )
                        }

                        // Upload Status Banner
                        if (uploadStatusMessage != null) {
                            Surface(
                                color = MaterialTheme.colorScheme.surface,
                                shape = RoundedCornerShape(8.dp),
                                modifier = Modifier.fillMaxWidth()
                            ) {
                                Row(
                                    modifier = Modifier.padding(8.dp),
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Icon(
                                        imageVector = Icons.Default.CheckCircle,
                                        contentDescription = null,
                                        tint = Color(0xFF16A34A),
                                        modifier = Modifier.size(16.dp)
                                    )
                                    Spacer(modifier = Modifier.width(6.dp))
                                    Text(
                                        text = uploadStatusMessage ?: "",
                                        style = MaterialTheme.typography.bodySmall,
                                        color = MaterialTheme.colorScheme.onSurface,
                                        fontWeight = FontWeight.Medium
                                    )
                                }
                            }
                        }
                    }
                }

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    OutlinedTextField(
                        value = chapterNumberText,
                        onValueChange = { chapterNumberText = it },
                        label = { Text("Número") },
                        singleLine = true,
                        modifier = Modifier.width(90.dp)
                    )

                    OutlinedTextField(
                        value = title,
                        onValueChange = { title = it },
                        label = { Text("Título do Capítulo *") },
                        singleLine = true,
                        modifier = Modifier
                            .weight(1f)
                            .testTag("chapter_title_input")
                    )
                }

                OutlinedTextField(
                    value = authorNotes,
                    onValueChange = { authorNotes = it },
                    label = { Text("Nota do Autor para Leitores Beta") },
                    placeholder = { Text("ex: Avaliem a cena da tempestade e o ritmo dos diálogos...") },
                    minLines = 2,
                    modifier = Modifier.fillMaxWidth()
                )

                // Chapter content text field (filled automatically by upload, or editable)
                OutlinedTextField(
                    value = content,
                    onValueChange = { content = it },
                    label = {
                        Text(
                            if (content.isNotBlank()) "Texto do Capítulo (${content.split("\\s+".toRegex()).size} palavras)"
                            else "Texto do Capítulo (preenchido via upload ou digitado) *"
                        )
                    },
                    placeholder = { Text("Faça upload do arquivo acima ou cole o texto aqui...") },
                    minLines = 6,
                    maxLines = 10,
                    modifier = Modifier
                        .fillMaxWidth()
                        .testTag("chapter_content_input")
                )
            }
        },
        confirmButton = {
            Button(
                onClick = {
                    val num = chapterNumberText.toIntOrNull() ?: nextChapterNumber
                    if (title.isNotBlank() && content.isNotBlank()) {
                        onConfirm(num, title, content, authorNotes)
                        onDismiss()
                    }
                },
                enabled = title.isNotBlank() && content.isNotBlank(),
                modifier = Modifier.testTag("confirm_add_chapter_button")
            ) {
                Text("Salvar Capítulo")
            }
        },
        dismissButton = {
            OutlinedButton(onClick = onDismiss) {
                Text("Cancelar")
            }
        }
    )
}

// Helper to query file display name from Uri
private fun getFileNameFromUri(context: Context, uri: Uri): String? {
    var result: String? = null
    if (uri.scheme == "content") {
        val cursor = context.contentResolver.query(uri, null, null, null, null)
        cursor?.use {
            if (it.moveToFirst()) {
                val index = it.getColumnIndex(OpenableColumns.DISPLAY_NAME)
                if (index != -1) {
                    result = it.getString(index)
                }
            }
        }
    }
    if (result == null) {
        result = uri.path
        val cut = result?.lastIndexOf('/')
        if (cut != null && cut != -1) {
            result = result?.substring(cut + 1)
        }
    }
    return result
}

@Composable
fun AddFeedbackDialog(
    versionName: String,
    onDismiss: () -> Unit,
    onConfirm: (
        reviewerName: String,
        rating: Int,
        feedbackText: String,
        pacing: String,
        depth: String,
        wouldRecommend: Boolean
    ) -> Unit
) {
    var reviewerName by remember { mutableStateOf("") }
    var rating by remember { mutableIntStateOf(5) }
    var feedbackText by remember { mutableStateOf("") }
    var pacing by remember { mutableStateOf("Ideal") }
    var depth by remember { mutableStateOf("Bom") }
    var wouldRecommend by remember { mutableStateOf(true) }

    val pacingOptions = listOf("Lento", "Ideal", "Acelerado")
    val depthOptions = listOf("Raso", "Bom", "Excelente")

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("Feedback da Versão ($versionName)") },
        text = {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .verticalScroll(rememberScrollState()),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                OutlinedTextField(
                    value = reviewerName,
                    onValueChange = { reviewerName = it },
                    label = { Text("Seu Nome / Apelido de Leitor") },
                    placeholder = { Text("ex: Joana_Beta") },
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth()
                )

                Text("Avaliação Geral", style = MaterialTheme.typography.labelLarge)
                Row(
                    horizontalArrangement = Arrangement.spacedBy(6.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    for (i in 1..5) {
                        IconButton(
                            onClick = { rating = i },
                            modifier = Modifier.size(36.dp)
                        ) {
                            Icon(
                                imageVector = if (i <= rating) Icons.Filled.Star else Icons.Outlined.Star,
                                contentDescription = "$i estrelas",
                                tint = if (i <= rating) MaterialTheme.colorScheme.tertiary else MaterialTheme.colorScheme.outline
                            )
                        }
                    }
                    Spacer(modifier = Modifier.width(8.dp))
                    Text("$rating / 5", style = MaterialTheme.typography.titleMedium)
                }

                Text("Ritmo da Leitura", style = MaterialTheme.typography.labelMedium)
                Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    pacingOptions.forEach { opt ->
                        FilterChip(
                            selected = pacing == opt,
                            onClick = { pacing = opt },
                            label = { Text(opt) }
                        )
                    }
                }

                Text("Profundidade dos Personagens", style = MaterialTheme.typography.labelMedium)
                Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    depthOptions.forEach { opt ->
                        FilterChip(
                            selected = depth == opt,
                            onClick = { depth = opt },
                            label = { Text(opt) }
                        )
                    }
                }

                OutlinedTextField(
                    value = feedbackText,
                    onValueChange = { feedbackText = it },
                    label = { Text("Comentários detalhados *") },
                    placeholder = { Text("O que mais gostou? O que pode melhorar antes do lançamento?") },
                    minLines = 3,
                    modifier = Modifier
                        .fillMaxWidth()
                        .testTag("feedback_text_input")
                )

                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier
                        .fillMaxWidth()
                        .clickable { wouldRecommend = !wouldRecommend }
                        .padding(vertical = 4.dp)
                ) {
                    Checkbox(
                        checked = wouldRecommend,
                        onCheckedChange = { wouldRecommend = it }
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = "Recomendo a publicação desta obra",
                        style = MaterialTheme.typography.bodyMedium
                    )
                }
            }
        },
        confirmButton = {
            Button(
                onClick = {
                    if (feedbackText.isNotBlank()) {
                        onConfirm(reviewerName, rating, feedbackText, pacing, depth, wouldRecommend)
                        onDismiss()
                    }
                },
                enabled = feedbackText.isNotBlank(),
                modifier = Modifier.testTag("submit_feedback_button")
            ) {
                Text("Enviar Avaliação")
            }
        },
        dismissButton = {
            OutlinedButton(onClick = onDismiss) {
                Text("Cancelar")
            }
        }
    )
}
