package com.example.ui.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.data.local.AppDatabase
import com.example.data.model.Chapter
import com.example.data.model.ChapterComment
import com.example.data.model.ReaderFontFamily
import com.example.data.model.ReaderLineSpacing
import com.example.data.model.ReaderSettings
import com.example.data.model.ReaderThemeMode
import com.example.data.repository.BookRepository
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

@OptIn(ExperimentalCoroutinesApi::class)
class ReaderViewModel(application: Application) : AndroidViewModel(application) {

    private val repository: BookRepository

    init {
        val database = AppDatabase.getDatabase(application)
        repository = BookRepository(database.bookDao())
    }

    private val _currentChapterId = MutableStateFlow<Long?>(null)
    val currentChapterId: StateFlow<Long?> = _currentChapterId.asStateFlow()

    private val _currentVersionId = MutableStateFlow<Long?>(null)
    val currentVersionId: StateFlow<Long?> = _currentVersionId.asStateFlow()

    private val _currentBookId = MutableStateFlow<Long?>(null)
    val currentBookId: StateFlow<Long?> = _currentBookId.asStateFlow()

    val currentChapter: StateFlow<Chapter?> = _currentChapterId.flatMapLatest { id ->
        if (id != null) repository.getChapter(id) else flowOf(null)
    }.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5000),
        initialValue = null
    )

    val chaptersInVersion: StateFlow<List<Chapter>> = _currentVersionId.flatMapLatest { verId ->
        if (verId != null) repository.getChaptersForVersion(verId) else flowOf(emptyList())
    }.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5000),
        initialValue = emptyList()
    )

    val comments: StateFlow<List<ChapterComment>> = _currentChapterId.flatMapLatest { chId ->
        if (chId != null) repository.getCommentsForChapter(chId) else flowOf(emptyList())
    }.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5000),
        initialValue = emptyList()
    )

    private val _readerSettings = MutableStateFlow(
        ReaderSettings(
            themeMode = ReaderThemeMode.SEPIA,
            fontSizeSp = 18,
            fontFamily = ReaderFontFamily.SERIF,
            lineSpacing = ReaderLineSpacing.NORMAL,
            isJustified = true
        )
    )
    val readerSettings: StateFlow<ReaderSettings> = _readerSettings.asStateFlow()

    fun loadChapter(chapterId: Long, versionId: Long, bookId: Long) {
        _currentChapterId.value = chapterId
        _currentVersionId.value = versionId
        _currentBookId.value = bookId
    }

    fun goToNextChapter() {
        val list = chaptersInVersion.value
        val cur = currentChapter.value ?: return
        val currentIndex = list.indexOfFirst { it.id == cur.id }
        if (currentIndex != -1 && currentIndex < list.size - 1) {
            val next = list[currentIndex + 1]
            _currentChapterId.value = next.id
        }
    }

    fun goToPreviousChapter() {
        val list = chaptersInVersion.value
        val cur = currentChapter.value ?: return
        val currentIndex = list.indexOfFirst { it.id == cur.id }
        if (currentIndex > 0) {
            val prev = list[currentIndex - 1]
            _currentChapterId.value = prev.id
        }
    }

    fun updateThemeMode(mode: ReaderThemeMode) {
        _readerSettings.value = _readerSettings.value.copy(themeMode = mode)
    }

    fun updateFontSize(newSize: Int) {
        val clamped = newSize.coerceIn(13, 30)
        _readerSettings.value = _readerSettings.value.copy(fontSizeSp = clamped)
    }

    fun updateFontFamily(font: ReaderFontFamily) {
        _readerSettings.value = _readerSettings.value.copy(fontFamily = font)
    }

    fun updateLineSpacing(spacing: ReaderLineSpacing) {
        _readerSettings.value = _readerSettings.value.copy(lineSpacing = spacing)
    }

    fun toggleJustification() {
        _readerSettings.value = _readerSettings.value.copy(
            isJustified = !_readerSettings.value.isJustified
        )
    }

    fun addComment(
        authorName: String,
        content: String,
        commentType: String,
        paragraphIndex: Int? = null,
        paragraphPreview: String? = null
    ) {
        val chId = _currentChapterId.value ?: return
        val verId = _currentVersionId.value ?: return
        val bId = _currentBookId.value ?: return

        viewModelScope.launch {
            repository.addComment(
                chapterId = chId,
                versionId = verId,
                bookId = bId,
                authorName = authorName.trim().ifEmpty { "Leitor Beta" },
                content = content.trim(),
                commentType = commentType,
                paragraphIndex = paragraphIndex,
                paragraphPreview = paragraphPreview
            )
        }
    }

    fun likeComment(commentId: Long) {
        viewModelScope.launch {
            repository.likeComment(commentId)
        }
    }

    fun resolveComment(commentId: Long, isResolved: Boolean, authorReply: String?) {
        viewModelScope.launch {
            repository.resolveComment(commentId, isResolved, authorReply?.trim())
        }
    }
}
