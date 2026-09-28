package com.example.data.local

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import com.example.data.model.Book
import com.example.data.model.BookVersion
import com.example.data.model.Chapter
import com.example.data.model.ChapterComment
import com.example.data.model.CommunityFeedback
import kotlinx.coroutines.flow.Flow

@Dao
interface BookDao {

    // --- Books ---
    @Query("SELECT * FROM books ORDER BY updatedAt DESC")
    fun getAllBooks(): Flow<List<Book>>

    @Query("SELECT * FROM books WHERE id = :bookId")
    fun getBookById(bookId: Long): Flow<Book?>

    @Query("SELECT * FROM books WHERE id = :bookId")
    suspend fun getBookByIdSync(bookId: Long): Book?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertBook(book: Book): Long

    @Update
    suspend fun updateBook(book: Book)

    @Query("DELETE FROM books WHERE id = :bookId")
    suspend fun deleteBook(bookId: Long)

    @Query("SELECT COUNT(*) FROM books")
    suspend fun getBookCount(): Int

    // --- Versions ---
    @Query("SELECT * FROM book_versions WHERE bookId = :bookId ORDER BY createdAt DESC")
    fun getVersionsForBook(bookId: Long): Flow<List<BookVersion>>

    @Query("SELECT * FROM book_versions WHERE bookId = :bookId ORDER BY createdAt DESC")
    suspend fun getVersionsForBookSync(bookId: Long): List<BookVersion>

    @Query("SELECT * FROM book_versions WHERE id = :versionId")
    fun getVersionById(versionId: Long): Flow<BookVersion?>

    @Query("SELECT * FROM book_versions WHERE id = :versionId")
    suspend fun getVersionByIdSync(versionId: Long): BookVersion?

    @Query("SELECT * FROM book_versions WHERE bookId = :bookId AND isCurrent = 1 LIMIT 1")
    fun getCurrentVersionForBook(bookId: Long): Flow<BookVersion?>

    @Query("SELECT * FROM book_versions WHERE bookId = :bookId AND isCurrent = 1 LIMIT 1")
    suspend fun getCurrentVersionForBookSync(bookId: Long): BookVersion?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertVersion(version: BookVersion): Long

    @Update
    suspend fun updateVersion(version: BookVersion)

    @Query("UPDATE book_versions SET isCurrent = 0 WHERE bookId = :bookId")
    suspend fun clearCurrentVersion(bookId: Long)

    @Query("UPDATE book_versions SET isCurrent = 1 WHERE id = :versionId")
    suspend fun setVersionAsCurrent(versionId: Long)

    @Query("DELETE FROM book_versions WHERE id = :versionId")
    suspend fun deleteVersion(versionId: Long)

    // --- Chapters ---
    @Query("SELECT * FROM chapters WHERE versionId = :versionId ORDER BY orderIndex ASC, chapterNumber ASC")
    fun getChaptersForVersion(versionId: Long): Flow<List<Chapter>>

    @Query("SELECT * FROM chapters WHERE versionId = :versionId ORDER BY orderIndex ASC, chapterNumber ASC")
    suspend fun getChaptersForVersionSync(versionId: Long): List<Chapter>

    @Query("SELECT * FROM chapters WHERE id = :chapterId")
    fun getChapterById(chapterId: Long): Flow<Chapter?>

    @Query("SELECT * FROM chapters WHERE id = :chapterId")
    suspend fun getChapterByIdSync(chapterId: Long): Chapter?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertChapter(chapter: Chapter): Long

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertChapters(chapters: List<Chapter>)

    @Update
    suspend fun updateChapter(chapter: Chapter)

    @Query("DELETE FROM chapters WHERE id = :chapterId")
    suspend fun deleteChapter(chapterId: Long)

    // --- Chapter Comments ---
    @Query("SELECT * FROM chapter_comments WHERE chapterId = :chapterId ORDER BY createdAt DESC")
    fun getCommentsForChapter(chapterId: Long): Flow<List<ChapterComment>>

    @Query("SELECT * FROM chapter_comments WHERE versionId = :versionId ORDER BY createdAt DESC")
    fun getCommentsForVersion(versionId: Long): Flow<List<ChapterComment>>

    @Query("SELECT * FROM chapter_comments WHERE bookId = :bookId ORDER BY createdAt DESC")
    fun getCommentsForBook(bookId: Long): Flow<List<ChapterComment>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertComment(comment: ChapterComment): Long

    @Update
    suspend fun updateComment(comment: ChapterComment)

    @Query("UPDATE chapter_comments SET likesCount = likesCount + 1 WHERE id = :commentId")
    suspend fun incrementCommentLikes(commentId: Long)

    @Query("UPDATE chapter_comments SET isResolved = :isResolved, authorReply = :authorReply WHERE id = :commentId")
    suspend fun updateCommentResolution(commentId: Long, isResolved: Boolean, authorReply: String?)

    @Query("DELETE FROM chapter_comments WHERE id = :commentId")
    suspend fun deleteComment(commentId: Long)

    // --- Community Feedback ---
    @Query("SELECT * FROM community_feedbacks WHERE bookId = :bookId ORDER BY createdAt DESC")
    fun getFeedbacksForBook(bookId: Long): Flow<List<CommunityFeedback>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertFeedback(feedback: CommunityFeedback): Long

    @Query("DELETE FROM community_feedbacks WHERE id = :feedbackId")
    suspend fun deleteFeedback(feedbackId: Long)
}
