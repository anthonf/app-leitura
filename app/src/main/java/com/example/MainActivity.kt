package com.example

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.BackHandler
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.ui.screens.book.BookDetailScreen
import com.example.ui.screens.book.BookListScreen
import com.example.ui.screens.reader.ReaderScreen
import com.example.ui.theme.VersoLivrosTheme
import com.example.ui.viewmodel.BookViewModel
import com.example.ui.viewmodel.ReaderViewModel

sealed interface Screen {
    data object BookList : Screen
    data class BookDetail(val bookId: Long) : Screen
    data class Reader(
        val chapterId: Long,
        val versionId: Long,
        val bookId: Long,
        val bookTitle: String,
        val versionName: String
    ) : Screen
}

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            VersoLivrosTheme {
                VersoLivrosApp()
            }
        }
    }
}

@Composable
fun VersoLivrosApp(
    bookViewModel: BookViewModel = viewModel(),
    readerViewModel: ReaderViewModel = viewModel()
) {
    var currentScreen by remember { mutableStateOf<Screen>(Screen.BookList) }

    Surface(modifier = Modifier.fillMaxSize()) {
        when (val screen = currentScreen) {
            is Screen.BookList -> {
                BookListScreen(
                    bookViewModel = bookViewModel,
                    onSelectBook = { bookId ->
                        currentScreen = Screen.BookDetail(bookId)
                    }
                )
            }

            is Screen.BookDetail -> {
                BackHandler {
                    currentScreen = Screen.BookList
                }
                BookDetailScreen(
                    bookViewModel = bookViewModel,
                    onNavigateBack = {
                        currentScreen = Screen.BookList
                    },
                    onOpenChapter = { chapterId, versionId, bookId, bookTitle, versionName ->
                        readerViewModel.loadChapter(chapterId, versionId, bookId)
                        currentScreen = Screen.Reader(
                            chapterId = chapterId,
                            versionId = versionId,
                            bookId = bookId,
                            bookTitle = bookTitle,
                            versionName = versionName
                        )
                    }
                )
            }

            is Screen.Reader -> {
                BackHandler {
                    currentScreen = Screen.BookDetail(screen.bookId)
                }
                ReaderScreen(
                    readerViewModel = readerViewModel,
                    bookTitle = screen.bookTitle,
                    versionName = screen.versionName,
                    onNavigateBack = {
                        currentScreen = Screen.BookDetail(screen.bookId)
                    }
                )
            }
        }
    }
}
