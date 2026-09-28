package com.example.data.model

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import androidx.room.PrimaryKey

@Entity(tableName = "books")
data class Book(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val title: String,
    val author: String,
    val synopsis: String,
    val genre: String,
    val coverColorHex: String = "#1E3A8A",
    val currentVersionName: String = "v1.0-draft",
    val status: String = "EM_REVISAO_BETA", // EM_REVISAO_BETA, RASCUNHO, PRONTO_PARA_LANCAMENTO
    val createdAt: Long = System.currentTimeMillis(),
    val updatedAt: Long = System.currentTimeMillis()
) {
    val genreList: List<String>
        get() = genre.split(",").map { it.trim() }.filter { it.isNotEmpty() }
}

@Entity(
    tableName = "book_versions",
    indices = [Index(value = ["bookId"])]
)
data class BookVersion(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val bookId: Long,
    val versionName: String, // ex: v1.0-alpha, v1.1-beta, v2.0-rc
    val releaseNotes: String, // Changelog: o que mudou nesta versão
    val targetAudienceNote: String = "",
    val isCurrent: Boolean = false,
    val createdAt: Long = System.currentTimeMillis()
)

@Entity(
    tableName = "chapters",
    indices = [Index(value = ["versionId"]), Index(value = ["bookId"])]
)
data class Chapter(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val versionId: Long,
    val bookId: Long,
    val chapterNumber: Int,
    val title: String,
    val content: String,
    val authorNotes: String = "", // Notas do autor para o leitor beta
    val orderIndex: Int = 0,
    val createdAt: Long = System.currentTimeMillis()
)

@Entity(
    tableName = "chapter_comments",
    indices = [Index(value = ["chapterId"]), Index(value = ["versionId"])]
)
data class ChapterComment(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val chapterId: Long,
    val versionId: Long,
    val bookId: Long,
    val paragraphIndex: Int? = null, // null = comentário geral no capítulo, ou índice do parágrafo
    val paragraphPreview: String? = null, // Trecho do parágrafo destacado
    val authorName: String,
    val content: String,
    val commentType: String = "SUGGESTION", // SUGGESTION, PRAISE, GRAMMAR, QUESTION, PACING
    val likesCount: Int = 0,
    val isResolved: Boolean = false,
    val authorReply: String? = null,
    val createdAt: Long = System.currentTimeMillis()
)

@Entity(
    tableName = "community_feedbacks",
    indices = [Index(value = ["bookId"]), Index(value = ["versionId"])]
)
data class CommunityFeedback(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val bookId: Long,
    val versionId: Long,
    val reviewerName: String,
    val rating: Int, // 1 a 5
    val feedbackText: String,
    val pacingScore: String = "Ideal", // Lento, Ideal, Acelerado
    val characterDepth: String = "Bom", // Raso, Bom, Excelente
    val wouldRecommend: Boolean = true,
    val createdAt: Long = System.currentTimeMillis()
)

enum class ReaderThemeMode {
    LIGHT,   // Fundo branco suave, texto carvão
    SEPIA,   // Fundo papel pergaminho #F5EFEB, texto sépia escuro #2B2118
    DARK,    // Fundo cinza escuro #1A1A1A, texto cinza claro #E0E0E0
    OLED     // Fundo preto puro #000000, texto cinza suave #CCCCCC
}

enum class ReaderFontFamily {
    SERIF,     // Serifada clássica de livro (Georgia/Serif)
    SANS,      // Sem serifa limpa (Sans-serif)
    MONOSPACE  // Estilo máquina de escrever / rascunho de revisão (Monospace)
}

enum class ReaderLineSpacing(val multiplier: Float, val label: String) {
    COMPACT(1.3f, "Compacto"),
    NORMAL(1.6f, "Normal"),
    RELAXED(1.9f, "Espaçoso")
}

data class ReaderSettings(
    val themeMode: ReaderThemeMode = ReaderThemeMode.SEPIA,
    val fontSizeSp: Int = 18,
    val fontFamily: ReaderFontFamily = ReaderFontFamily.SERIF,
    val lineSpacing: ReaderLineSpacing = ReaderLineSpacing.NORMAL,
    val isJustified: Boolean = true
)
