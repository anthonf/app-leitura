package com.example.ui.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.data.local.AppDatabase
import com.example.data.model.Book
import com.example.data.model.BookVersion
import com.example.data.model.Chapter
import com.example.data.model.CommunityFeedback
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
class BookViewModel(application: Application) : AndroidViewModel(application) {

    private val repository: BookRepository

    init {
        val database = AppDatabase.getDatabase(application)
        repository = BookRepository(database.bookDao())
        viewModelScope.launch {
            repository.seedInitialDataIfEmpty()
        }
    }

    val books: StateFlow<List<Book>> = repository.allBooks.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5000),
        initialValue = emptyList()
    )

    private val _selectedBookId = MutableStateFlow<Long?>(null)
    val selectedBookId: StateFlow<Long?> = _selectedBookId.asStateFlow()

    private val _selectedVersionId = MutableStateFlow<Long?>(null)
    val selectedVersionId: StateFlow<Long?> = _selectedVersionId.asStateFlow()

    val selectedBook: StateFlow<Book?> = _selectedBookId.flatMapLatest { id ->
        if (id != null) repository.getBook(id) else flowOf(null)
    }.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5000),
        initialValue = null
    )

    val bookVersions: StateFlow<List<BookVersion>> = _selectedBookId.flatMapLatest { id ->
        if (id != null) repository.getVersionsForBook(id) else flowOf(emptyList())
    }.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5000),
        initialValue = emptyList()
    )

    val chapters: StateFlow<List<Chapter>> = _selectedVersionId.flatMapLatest { versionId ->
        if (versionId != null) {
            repository.getChaptersForVersion(versionId)
        } else {
            flowOf(emptyList())
        }
    }.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5000),
        initialValue = emptyList()
    )

    val feedbacks: StateFlow<List<CommunityFeedback>> = _selectedBookId.flatMapLatest { id ->
        if (id != null) repository.getFeedbacksForBook(id) else flowOf(emptyList())
    }.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5000),
        initialValue = emptyList()
    )

    fun selectBook(bookId: Long) {
        _selectedBookId.value = bookId
        viewModelScope.launch {
            // Se já tem versões, seleciona a atual
            val versions = bookVersions.value
            val current = versions.find { it.isCurrent } ?: versions.firstOrNull()
            _selectedVersionId.value = current?.id
        }
    }

    fun selectVersion(versionId: Long) {
        _selectedVersionId.value = versionId
    }

    fun createBook(
        title: String,
        author: String,
        synopsis: String,
        genre: String,
        versionName: String,
        releaseNotes: String,
        coverColorHex: String
    ) {
        viewModelScope.launch {
            val bookId = repository.createBookWithInitialVersion(
                title = title.trim(),
                author = author.trim(),
                synopsis = synopsis.trim(),
                genre = genre.trim(),
                initialVersionName = versionName.trim().ifEmpty { "v1.0-draft" },
                initialReleaseNotes = releaseNotes.trim().ifEmpty { "Versão inicial submetida para revisão." },
                coverColorHex = coverColorHex
            )
            selectBook(bookId)
        }
    }

    fun createNewVersion(
        bookId: Long,
        versionName: String,
        releaseNotes: String,
        targetAudienceNote: String,
        copyChaptersFromCurrent: Boolean
    ) {
        viewModelScope.launch {
            val copyFromId = if (copyChaptersFromCurrent) _selectedVersionId.value else null
            val newVerId = repository.createNewVersion(
                bookId = bookId,
                versionName = versionName.trim(),
                releaseNotes = releaseNotes.trim(),
                targetAudienceNote = targetAudienceNote.trim(),
                copyChaptersFromVersionId = copyFromId,
                setAsCurrent = true
            )
            _selectedVersionId.value = newVerId
        }
    }

    fun switchActiveVersion(bookId: Long, versionId: Long) {
        viewModelScope.launch {
            repository.switchActiveVersion(bookId, versionId)
            _selectedVersionId.value = versionId
        }
    }

    fun addChapter(
        bookId: Long,
        versionId: Long,
        chapterNumber: Int,
        title: String,
        content: String,
        authorNotes: String
    ) {
        viewModelScope.launch {
            repository.addChapter(
                bookId = bookId,
                versionId = versionId,
                chapterNumber = chapterNumber,
                title = title.trim(),
                content = content.trim(),
                authorNotes = authorNotes.trim()
            )
        }
    }

    fun submitFeedback(
        bookId: Long,
        versionId: Long,
        reviewerName: String,
        rating: Int,
        feedbackText: String,
        pacing: String,
        depth: String,
        wouldRecommend: Boolean
    ) {
        viewModelScope.launch {
            repository.addCommunityFeedback(
                bookId = bookId,
                versionId = versionId,
                reviewerName = reviewerName.trim().ifEmpty { "Leitor Beta Anônimo" },
                rating = rating,
                feedbackText = feedbackText.trim(),
                pacingScore = pacing,
                characterDepth = depth,
                wouldRecommend = wouldRecommend
            )
        }
    }

    fun updateBookStatus(bookId: Long, status: String) {
        viewModelScope.launch {
            repository.updateBookStatus(bookId, status)
        }
    }
}
