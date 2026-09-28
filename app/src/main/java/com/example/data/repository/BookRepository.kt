package com.example.data.repository

import com.example.data.local.BookDao
import com.example.data.model.Book
import com.example.data.model.BookVersion
import com.example.data.model.Chapter
import com.example.data.model.ChapterComment
import com.example.data.model.CommunityFeedback
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.withContext

class BookRepository(private val bookDao: BookDao) {

    val allBooks: Flow<List<Book>> = bookDao.getAllBooks()

    fun getBook(bookId: Long): Flow<Book?> = bookDao.getBookById(bookId)

    fun getVersionsForBook(bookId: Long): Flow<List<BookVersion>> =
        bookDao.getVersionsForBook(bookId)

    fun getCurrentVersionForBook(bookId: Long): Flow<BookVersion?> =
        bookDao.getCurrentVersionForBook(bookId)

    fun getChaptersForVersion(versionId: Long): Flow<List<Chapter>> =
        bookDao.getChaptersForVersion(versionId)

    fun getChapter(chapterId: Long): Flow<Chapter?> =
        bookDao.getChapterById(chapterId)

    fun getCommentsForChapter(chapterId: Long): Flow<List<ChapterComment>> =
        bookDao.getCommentsForChapter(chapterId)

    fun getCommentsForBook(bookId: Long): Flow<List<ChapterComment>> =
        bookDao.getCommentsForBook(bookId)

    fun getFeedbacksForBook(bookId: Long): Flow<List<CommunityFeedback>> =
        bookDao.getFeedbacksForBook(bookId)

    suspend fun createBookWithInitialVersion(
        title: String,
        author: String,
        synopsis: String,
        genre: String,
        initialVersionName: String = "v1.0-draft",
        initialReleaseNotes: String = "Primeiro rascunho completo submetido para leitura beta.",
        coverColorHex: String = "#1E3A8A"
    ): Long = withContext(Dispatchers.IO) {
        val bookId = bookDao.insertBook(
            Book(
                title = title,
                author = author,
                synopsis = synopsis,
                genre = genre,
                coverColorHex = coverColorHex,
                currentVersionName = initialVersionName,
                status = "EM_REVISAO_BETA"
            )
        )

        val versionId = bookDao.insertVersion(
            BookVersion(
                bookId = bookId,
                versionName = initialVersionName,
                releaseNotes = initialReleaseNotes,
                isCurrent = true,
                targetAudienceNote = "Feedback geral sobre a ambientação e a apresentação dos personagens."
            )
        )

        // Criar primeiro capítulo de exemplo
        bookDao.insertChapter(
            Chapter(
                versionId = versionId,
                bookId = bookId,
                chapterNumber = 1,
                title = "Capítulo 1: O Começo",
                content = "O silêncio que antecedia o alvorecer era denso como neblina.\n\n" +
                        "Ele caminhou até o parapeito da torre e observou os contornos da cidade despertando lentamente. Havia algo estranho no ar naquele dia — uma quietude que não prometia paz, mas sim o prelúdio de uma tempestade iminente.\n\n" +
                        "Nas mãos, ele segurava a carta com o selo quebrado. As palavras ali contidas mudariam não apenas o seu destino, mas o de todos que jurara proteger.\n\n" +
                        "— O momento chegou — murmurou para si mesmo, antes de dar as costas à janela e descer as escadarias em espiral.",
                authorNotes = "Atenção leitores beta: este capítulo estabelece o mistério da carta. O ritmo parece adequado?"
            )
        )

        bookId
    }

    suspend fun createNewVersion(
        bookId: Long,
        versionName: String,
        releaseNotes: String,
        targetAudienceNote: String,
        copyChaptersFromVersionId: Long? = null,
        setAsCurrent: Boolean = true
    ): Long = withContext(Dispatchers.IO) {
        if (setAsCurrent) {
            bookDao.clearCurrentVersion(bookId)
        }

        val newVersionId = bookDao.insertVersion(
            BookVersion(
                bookId = bookId,
                versionName = versionName,
                releaseNotes = releaseNotes,
                targetAudienceNote = targetAudienceNote,
                isCurrent = setAsCurrent
            )
        )

        // Se solicitado, clona capítulos da versão anterior para a nova versão
        if (copyChaptersFromVersionId != null) {
            val previousChapters = bookDao.getChaptersForVersionSync(copyChaptersFromVersionId)
            val clonedChapters = previousChapters.map { ch ->
                ch.copy(
                    id = 0,
                    versionId = newVersionId,
                    createdAt = System.currentTimeMillis()
                )
            }
            bookDao.insertChapters(clonedChapters)
        }

        if (setAsCurrent) {
            val book = bookDao.getBookByIdSync(bookId)
            if (book != null) {
                bookDao.updateBook(
                    book.copy(
                        currentVersionName = versionName,
                        updatedAt = System.currentTimeMillis()
                    )
                )
            }
        }

        newVersionId
    }

    suspend fun switchActiveVersion(bookId: Long, versionId: Long) = withContext(Dispatchers.IO) {
        bookDao.clearCurrentVersion(bookId)
        bookDao.setVersionAsCurrent(versionId)
        val version = bookDao.getVersionByIdSync(versionId)
        val book = bookDao.getBookByIdSync(bookId)
        if (book != null && version != null) {
            bookDao.updateBook(
                book.copy(
                    currentVersionName = version.versionName,
                    updatedAt = System.currentTimeMillis()
                )
            )
        }
    }

    suspend fun addChapter(
        bookId: Long,
        versionId: Long,
        chapterNumber: Int,
        title: String,
        content: String,
        authorNotes: String
    ): Long = withContext(Dispatchers.IO) {
        bookDao.insertChapter(
            Chapter(
                bookId = bookId,
                versionId = versionId,
                chapterNumber = chapterNumber,
                title = title,
                content = content,
                authorNotes = authorNotes,
                orderIndex = chapterNumber
            )
        )
    }

    suspend fun updateChapter(chapter: Chapter) = withContext(Dispatchers.IO) {
        bookDao.updateChapter(chapter)
    }

    suspend fun deleteChapter(chapterId: Long) = withContext(Dispatchers.IO) {
        bookDao.deleteChapter(chapterId)
    }

    suspend fun addComment(
        chapterId: Long,
        versionId: Long,
        bookId: Long,
        authorName: String,
        content: String,
        commentType: String,
        paragraphIndex: Int? = null,
        paragraphPreview: String? = null
    ): Long = withContext(Dispatchers.IO) {
        bookDao.insertComment(
            ChapterComment(
                chapterId = chapterId,
                versionId = versionId,
                bookId = bookId,
                paragraphIndex = paragraphIndex,
                paragraphPreview = paragraphPreview,
                authorName = authorName,
                content = content,
                commentType = commentType
            )
        )
    }

    suspend fun likeComment(commentId: Long) = withContext(Dispatchers.IO) {
        bookDao.incrementCommentLikes(commentId)
    }

    suspend fun resolveComment(commentId: Long, isResolved: Boolean, reply: String?) = withContext(Dispatchers.IO) {
        bookDao.updateCommentResolution(commentId, isResolved, reply)
    }

    suspend fun addCommunityFeedback(
        bookId: Long,
        versionId: Long,
        reviewerName: String,
        rating: Int,
        feedbackText: String,
        pacingScore: String,
        characterDepth: String,
        wouldRecommend: Boolean
    ): Long = withContext(Dispatchers.IO) {
        bookDao.insertFeedback(
            CommunityFeedback(
                bookId = bookId,
                versionId = versionId,
                reviewerName = reviewerName,
                rating = rating,
                feedbackText = feedbackText,
                pacingScore = pacingScore,
                characterDepth = characterDepth,
                wouldRecommend = wouldRecommend
            )
        )
    }

    suspend fun updateBookStatus(bookId: Long, newStatus: String) = withContext(Dispatchers.IO) {
        val book = bookDao.getBookByIdSync(bookId)
        if (book != null) {
            bookDao.updateBook(book.copy(status = newStatus, updatedAt = System.currentTimeMillis()))
        }
    }

    suspend fun seedInitialDataIfEmpty() = withContext(Dispatchers.IO) {
        if (bookDao.getBookCount() > 0) return@withContext

        // Livro 1: Os Ecos de Titã (Ficção Científica com 2 versões e comentários)
        val book1Id = bookDao.insertBook(
            Book(
                title = "Os Ecos de Titã",
                author = "Helena Vasconcelos",
                synopsis = "No ano de 2188, uma colônia de mineração na lua de Saturno intercepta uma transmissão mecânica que antecede a própria civilização humana.",
                genre = "Ficção Científica, Cyberpunk, Suspense",
                coverColorHex = "#1E293B",
                currentVersionName = "v1.2-beta",
                status = "EM_REVISAO_BETA"
            )
        )

        // Versão 1.0 (antiga)
        val v10Id = bookDao.insertVersion(
            BookVersion(
                bookId = book1Id,
                versionName = "v1.0-alpha",
                releaseNotes = "Primeira versão de leitura para o grupo focal. Inclui os 2 primeiros capítulos e a introdução da estação orbital.",
                targetAudienceNote = "Feedback focado no rigor científico e introdução dos personagens.",
                isCurrent = false,
                createdAt = System.currentTimeMillis() - (14 * 24 * 3600 * 1000L)
            )
        )

        // Versão 1.2 (atual)
        val v12Id = bookDao.insertVersion(
            BookVersion(
                bookId = book1Id,
                versionName = "v1.2-beta",
                releaseNotes = "• Capítulo 1: Diálogos na cabine de descompressão reescritos para soar mais naturais após feedback dos leitores beta.\n• Capítulo 2: Novo trecho explicando o funcionamento do sinal de rádio de Titã.\n• Correção de inconsistências temporais no Capítulo 3.",
                targetAudienceNote = "Por favor, avaliem se a explicação do sinal ficou clara sem ficar cansativa.",
                isCurrent = true,
                createdAt = System.currentTimeMillis() - (2 * 24 * 3600 * 1000L)
            )
        )

        // Capítulos para v1.2
        val ch1Id = bookDao.insertChapter(
            Chapter(
                versionId = v12Id,
                bookId = book1Id,
                chapterNumber = 1,
                title = "Capítulo 1: O Silêncio de Metano",
                content = "A tempestade de metano líquido chicoteava os painéis solares da Estação Prometheus há quarenta e oito horas ininterruptas. Lá fora, a temperatura beirava cento e oitenta graus negativos sob um céu perpétuo cor de âmbar.\n\n" +
                        "Dra. Mariana Rios ajustou o fone de isolamento acústico. O zumbido dos reatores geotérmicos era uma constante reconfortante, mas os sensores subsuperficiais tinham acabado de registrar uma oscilação na frequência de 14.2 gigahertz.\n\n" +
                        "— Arthur, você está vendo isso na telemetria? — perguntou ela, sem tirar os olhos do monitor de fósforo verde.\n\n" +
                        "O engenheiro-chefe deu dois passos cautelosos até a bancada central, segurando uma caneca de alumínio com café reconstituído morno. Ele inclinou a cabeça e franziu a testa.\n\n" +
                        "— Isso não é estática ionosférica, Mariana. Estáticas não têm intervalo matemático exato de 3,14 segundos.\n\n" +
                        "Um arrepio frio percorreu a espinha da astrofísica. No manto congelado de Titã, a quilômetros de qualquer outra presença humana, algo estava contando em voz alta.",
                authorNotes = "Ajustei o diálogo entre Mariana e Arthur para ser mais direto e realista. Digam se a tensão inicial funciona bem!",
                orderIndex = 1
            )
        )

        val ch2Id = bookDao.insertChapter(
            Chapter(
                versionId = v12Id,
                bookId = book1Id,
                chapterNumber = 2,
                title = "Capítulo 2: Decodificação no Vácuo",
                content = "A sala de comunicações da Prometheus permaneceu em silêncio absoluto enquanto o computador quântico tentava correlacionar a sequência de pulsos com qualquer padrão conhecido nas bases da Aliança Terrestre.\n\n" +
                        "— Nada nos arquivos de pulsos magnéticos militares — anunciou Arthur, batendo os nós dos dedos no painel de titânio. — Também não é telemetria das antigas sondas Cassini ou Huygens.\n\n" +
                        "Mariana aproximou-se do gráfico esférico holográfico. Os pulsos não vinham da órbita. Eles vinham do subterrâneo mais profundo, do oceano salgado que se estendia a centenas de quilômetros sob a crosta de gelo de água.\n\n" +
                        "— O transmissor está no oceano de água líquida subterrâneo — sussurrou ela, com a voz embargada. — E a potência necessária para atravessar essa carapaça de gelo exigiria mais energia do que nossa estação inteira produz em um ano.\n\n" +
                        "Antes que Arthur pudesse responder, todas as luzes do módulo primário piscaram duas vezes e se estabilizaram em um tom carmesim de emergência.",
                authorNotes = "Este capítulo foi expandido nesta versão com a dedução sobre a origem oceânica subterrânea.",
                orderIndex = 2
            )
        )

        // Comentários dos leitores beta para o Capítulo 1
        bookDao.insertComment(
            ChapterComment(
                chapterId = ch1Id,
                versionId = v12Id,
                bookId = book1Id,
                paragraphIndex = 1,
                paragraphPreview = "Dra. Mariana Rios ajustou o fone de isolamento acústico...",
                authorName = "Carlos_Beta",
                content = "Excelente ambientação! A descrição do céu cor de âmbar e do metano transmite perfeitamente a hostilidade do planeta.",
                commentType = "PRAISE",
                likesCount = 5,
                isResolved = false,
                authorReply = "Muito obrigada, Carlos! Quis reforçar a sensação de isolamento desde a primeira página."
            )
        )

        bookDao.insertComment(
            ChapterComment(
                chapterId = ch1Id,
                versionId = v12Id,
                bookId = book1Id,
                paragraphIndex = 4,
                paragraphPreview = "— Isso não é estática ionosférica, Mariana...",
                authorName = "Sofia_Leitora",
                content = "A sugestão do intervalo de 3,14 segundos (Pi) ficou muito boa! Deu um gancho imediato de inteligência não-humana.",
                commentType = "SUGGESTION",
                likesCount = 8,
                isResolved = true,
                authorReply = "Incorporado na versão atual após sua sugestão na v1.0! Funcionou muito melhor."
            )
        )

        bookDao.insertComment(
            ChapterComment(
                chapterId = ch1Id,
                versionId = v12Id,
                bookId = book1Id,
                paragraphIndex = null,
                paragraphPreview = null,
                authorName = "Prof_Renato",
                content = "Capítulo no ritmo perfeito. Não perde tempo e joga o leitor direto na ação científica sem rodeios desnecessários.",
                commentType = "PACING",
                likesCount = 3,
                isResolved = false
            )
        )

        // Feedbacks da comunidade para o Livro 1
        bookDao.insertFeedback(
            CommunityFeedback(
                bookId = book1Id,
                versionId = v12Id,
                reviewerName = "Mariana L. (Beta Reader)",
                rating = 5,
                feedbackText = "A evolução da versão v1.0 para a v1.2 foi visível! Os diálogos agora fluem muito melhor e a tensão do mistério é irresistível. Mal posso esperar pelo capítulo 3.",
                pacingScore = "Ideal",
                characterDepth = "Excelente",
                wouldRecommend = true
            )
        )

        bookDao.insertFeedback(
            CommunityFeedback(
                bookId = book1Id,
                versionId = v12Id,
                reviewerName = "Eduardo Crítico",
                rating = 4,
                feedbackText = "Ficção científica dura com ótima precisão física. Apenas recomendo atentar para não deixar as explicações técnicas sobrecarregarem o leitor leigo mais adiante.",
                pacingScore = "Ideal",
                characterDepth = "Bom",
                wouldRecommend = true
            )
        )

        // Livro 2: A Guardiã dos Relógios Mortos (Fantasia Urbana)
        val book2Id = bookDao.insertBook(
            Book(
                title = "A Guardiã dos Relógios Mortos",
                author = "Vinicius Alencar",
                synopsis = "Em uma Praga alternativa onde o tempo pode ser engarrafado e roubado, uma relojoeira clandestina descobre que os últimos 7 minutos de um nobre assassinado continuam correndo.",
                genre = "Fantasia, Mistério, Aventura",
                coverColorHex = "#7C2D12",
                currentVersionName = "v1.0-draft",
                status = "EM_REVISAO_BETA"
            )
        )

        val v20Id = bookDao.insertVersion(
            BookVersion(
                bookId = book2Id,
                versionName = "v1.0-draft",
                releaseNotes = "Lançamento inicial para leitores beta da comunidade. Por favor deixem comentários sobre o sistema de magia temporal e a dinâmica da protagonista.",
                targetAudienceNote = "Foco em identificar se a mecânica dos minutos roubados ficou fácil de compreender.",
                isCurrent = true,
                createdAt = System.currentTimeMillis() - (5 * 24 * 3600 * 1000L)
            )
        )

        val ch2_1Id = bookDao.insertChapter(
            Chapter(
                versionId = v20Id,
                bookId = book2Id,
                chapterNumber = 1,
                title = "Capítulo 1: Engrenagens de Mercúrio",
                content = "Todo relógio que para no exato instante de uma morte violenta retém uma fração do fôlego do seu dono. Essa era a regra que o avô de Maya lhe ensinara antes de desaparecer nos nevoeiros do Rio Moldava.\n\n" +
                        "Na oficina escondida atrás da confeitaria da Rua das Lamparinas, Maya segurava uma pinça de latão com ponta de rubi. Diante dela repousava um relógio de bolso com caixa de prata fosca, trazido por um cliente encapuzado pouco antes da meia-noite.\n\n" +
                        "O ponteiro dos segundos tremia espasmodicamente no minuto cinquenta e três. Não avançava, nem retrocedia. Mas quando ela aproximou a orelha da caixa metálica, não ouviu o tique-taque mecânico.\n\n" +
                        "Ouviu um sussurro humano desesperado:\n\n" +
                        "— Ela escondeu a chave sob o altar de cinzas.\n\n" +
                        "Maya soltou a pinça sobre o veludo escuro. Aquele não era um relógio comum. Era um testamento roubado.",
                authorNotes = "Qual foi a impressão de vocês sobre a primeira frase de abertura? Chama a atenção?",
                orderIndex = 1
            )
        )

        bookDao.insertComment(
            ChapterComment(
                chapterId = ch2_1Id,
                versionId = v20Id,
                bookId = book2Id,
                paragraphIndex = 0,
                paragraphPreview = "Todo relógio que para no exato instante de uma morte violenta...",
                authorName = "Larissa_Livros",
                content = "A primeira frase é magnética! Dá vontade de devorar o livro na hora.",
                commentType = "PRAISE",
                likesCount = 7,
                isResolved = false
            )
        )

        bookDao.insertFeedback(
            CommunityFeedback(
                bookId = book2Id,
                versionId = v20Id,
                reviewerName = "Beto Fantasia",
                rating = 5,
                feedbackText = "Premissa muito original! O sistema de tempo aprisionado em relógios tem muito potencial. Aguardo ansiosamente os próximos capítulos da versão beta.",
                pacingScore = "Ideal",
                characterDepth = "Bom",
                wouldRecommend = true
            )
        )
    }
}
