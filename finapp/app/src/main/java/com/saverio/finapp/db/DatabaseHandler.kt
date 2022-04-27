package com.saverio.finapp.db

import android.annotation.SuppressLint
import android.content.ContentValues
import android.content.Context
import android.database.Cursor
import android.database.SQLException
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteOpenHelper

class DatabaseHandler(context: Context) :
    SQLiteOpenHelper(context, DATABASE_NAME, null, DATABASE_VERSION) {
    override fun onCreate(database: SQLiteDatabase) {
        //println("onCreate")
        var query = ""
        //Create "chapters" table
        query = "CREATE TABLE `${TABLE_NAME_CHAPTERS}` (" +
                "  `${COLUMN_CHAPTER_PK_CHAPTERS}` INTEGER NOT NULL PRIMARY KEY," +
                "  `${COLUMN_TITLE_CHAPTERS}` TEXT NOT NULL" +
                ")"
        database.execSQL(query)

        //Create "sections" table
        query = "CREATE TABLE `${TABLE_NAME_SECTIONS}` (" +
                "  `${COLUMN_SECTION_PK_SECTIONS}` VARCHAR(10) NOT NULL PRIMARY KEY," +
                "  `${COLUMN_CHAPTER_INDEX_SECTIONS}` INT NOT NULL," +
                "  `${COLUMN_TITLE_SECTIONS}` TEXT NOT NULL," +
                "  `${COLUMN_AUTHOR_SECTIONS}` TEXT NOT NULL," +
                "  `${COLUMN_TEXT_SECTIONS}` TEXT NOT NULL" +
                ")"
        database.execSQL(query)

        //Create "quizzes" table
        query = "CREATE TABLE `${TABLE_NAME_QUIZZES}` (" +
                "  `${COLUMN_ID_PK_QUIZZES}` INT NOT NULL PRIMARY KEY," +
                "  `${COLUMN_CHAPTER_INDEX_QUIZZES}` INT NOT NULL," +
                "  `${COLUMN_SECTION_INDEX_QUIZZES}` VARCHAR(10) NOT NULL," +
                "  `${COLUMN_QUESTION_QUIZZES}` TEXT NOT NULL," +
                "  `${COLUMN_A_QUIZZES}` TEXT NOT NULL," +
                "  `${COLUMN_B_QUIZZES}` TEXT NOT NULL," +
                "  `${COLUMN_C_QUIZZES}` TEXT NOT NULL," +
                "  `${COLUMN_D_QUIZZES}` TEXT NOT NULL," +
                "  `${COLUMN_CORRECT_QUIZZES}` VARCHAR(5) NOT NULL" +
                ")"
        database.execSQL(query)

        //Create "news" table
        query = "CREATE TABLE `${TABLE_NAME_NEWS}` (" +
                "  `${COLUMN_ID_PK_NEWS}` INT NOT NULL PRIMARY KEY," +
                "  `${COLUMN_TYPE_NEWS}` INT NOT NULL," +
                "  `${COLUMN_DATE_NEWS}` VARCHAR(50) NOT NULL," +
                "  `${COLUMN_TITLE_NEWS}` TEXT NOT NULL," +
                "  `${COLUMN_IMAGE_NEWS}` TEXT NOT NULL," +
                "  `${COLUMN_LINK_NEWS}` TEXT NOT NULL," +
                "  `${COLUMN_TEXT_NEWS}` TEXT NOT NULL" +
                ")"
        database.execSQL(query)

        //Create "statistics" table
        query = "CREATE TABLE `${TABLE_NAME_STATISTICS}` (" +
                "  `${COLUMN_ID_PK_STATISTICS}` INT NOT NULL PRIMARY KEY," +
                "  `${COLUMN_TYPE_STATISTICS}` INT NOT NULL," +
                "  `${COLUMN_DATETIME_STATISTICS}` TEXT NOT NULL," +
                "  `${COLUMN_CORRECT_ANSWER_STATISTICS}` TEXT NOT NULL," +
                "  `${COLUMN_USER_ANSWER_STATISTICS}` TEXT NOT NULL," +
                "  `${COLUMN_QUESTION_ID_STATISTICS}` TEXT NOT NULL," +
                "  `${COLUMN_MILLISECONDS_STATISTICS}` INT NOT NULL" +
                ")"
        database.execSQL(query)
    }

    override fun onUpgrade(database: SQLiteDatabase, oldVersion: Int, newVersion: Int) {
        //println("onUpgrade")
        database.execSQL("DROP TABLE IF EXISTS `${TABLE_NAME_CHAPTERS}`")
        database.execSQL("DROP TABLE IF EXISTS `${TABLE_NAME_SECTIONS}`")
        database.execSQL("DROP TABLE IF EXISTS `${TABLE_NAME_QUIZZES}`")
        database.execSQL("DROP TABLE IF EXISTS `${TABLE_NAME_NEWS}`")
        database.execSQL("DROP TABLE IF EXISTS `${TABLE_NAME_STATISTICS}`")
        onCreate(database)
    }


    //Chapters
    fun addChapter(chapter: ChaptersModel): Long {
        val database = writableDatabase

        val contentValues = ContentValues()
        contentValues.put(COLUMN_CHAPTER_PK_CHAPTERS, chapter.chapter)
        contentValues.put(COLUMN_TITLE_CHAPTERS, chapter.title)
        val success = database.insert(TABLE_NAME_CHAPTERS, null, contentValues)
        database.close()

        return success
    }

    @SuppressLint("Range")
    fun getChapters(): ArrayList<ChaptersModel> {
        val chaptersList = ArrayList<ChaptersModel>()
        val query = "SELECT * FROM `${TABLE_NAME_CHAPTERS}`"
        val database = readableDatabase
        var cursor: Cursor? = null

        try {
            cursor = database.rawQuery(query, null)
        } catch (e: SQLException) {
            database.execSQL(query)
            return ArrayList()
        }

        if (cursor.moveToFirst()) {
            do {
                val chapter = cursor.getInt(cursor.getColumnIndex(COLUMN_CHAPTER_PK_CHAPTERS))
                val title = cursor.getString(cursor.getColumnIndex(COLUMN_TITLE_CHAPTERS))

                val chapterToAdd = ChaptersModel(chapter = chapter, title = title)
                chaptersList.add(chapterToAdd)
            } while (cursor.moveToNext())
        }
        return chaptersList
    }

    @SuppressLint("Range")
    fun getChapter(chapter: Int): ChaptersModel {
        var chapterToReturn = ChaptersModel(chapter, null)
        val query =
            "SELECT * FROM `${TABLE_NAME_CHAPTERS}` WHERE `${COLUMN_CHAPTER_PK_CHAPTERS}` = $chapter"
        val database = readableDatabase
        var cursor: Cursor? = null

        try {
            cursor = database.rawQuery(query, null)
        } catch (e: SQLException) {
            database.execSQL(query)
            return chapterToReturn
        }

        if (cursor.moveToFirst()) {
            val chapter = cursor.getInt(cursor.getColumnIndex(COLUMN_CHAPTER_PK_CHAPTERS))
            val title = cursor.getString(cursor.getColumnIndex(COLUMN_TITLE_CHAPTERS))
            chapterToReturn.chapter = chapter
            chapterToReturn.title = title
        }
        return chapterToReturn
    }

    fun updateChapter(chapter: ChaptersModel): Int {
        val database = writableDatabase

        val contentValues = ContentValues()
        contentValues.put(COLUMN_CHAPTER_PK_CHAPTERS, chapter.chapter)
        contentValues.put(COLUMN_TITLE_CHAPTERS, chapter.title)

        val success =
            database.update(
                TABLE_NAME_CHAPTERS,
                contentValues,
                "$COLUMN_CHAPTER_PK_CHAPTERS = '${chapter.chapter}'",
                null
            ) //we need the primary key to update a record
        database.close()
        return success
    }

    fun deleteChapter(chapter: ChaptersModel): Int {
        val database = writableDatabase
        val contentValues = ContentValues()
        contentValues.put(COLUMN_CHAPTER_PK_CHAPTERS, chapter.chapter)
        //this is not necessary because we need only the primary key

        val success = database.delete(
            TABLE_NAME_CHAPTERS,
            "$COLUMN_CHAPTER_PK_CHAPTERS = '${chapter.chapter}'",
            null
        )
        database.close()
        return success
    }
    //End || Chapters

    //Sections
    fun addSection(section: SectionsModel): Long {
        val database = writableDatabase

        val contentValues = ContentValues()
        contentValues.put(COLUMN_SECTION_PK_SECTIONS, section.section)
        contentValues.put(COLUMN_CHAPTER_INDEX_SECTIONS, section.chapter)
        contentValues.put(COLUMN_TITLE_SECTIONS, section.title)
        contentValues.put(COLUMN_AUTHOR_SECTIONS, section.author)
        contentValues.put(COLUMN_TEXT_SECTIONS, section.text)
        val success = database.insert(TABLE_NAME_SECTIONS, null, contentValues)
        database.close()

        return success
    }

    @SuppressLint("Range")
    fun getSections(chapter: Int? = null): ArrayList<SectionsModel> {
        val sectionsList = ArrayList<SectionsModel>()
        var query = ""
        if (chapter != null) {
            query = "SELECT * FROM `${TABLE_NAME_SECTIONS}` WHERE `chapter` = '${chapter}'"
        } else {
            query = "SELECT * FROM `${TABLE_NAME_SECTIONS}`"
        }
        val database = readableDatabase
        var cursor: Cursor? = null

        try {
            cursor = database.rawQuery(query, null)
        } catch (e: SQLException) {
            database.execSQL(query)
            return ArrayList()
        }

        if (cursor.moveToFirst()) {
            do {
                val section = cursor.getString(cursor.getColumnIndex(COLUMN_SECTION_PK_SECTIONS))
                val chapter = cursor.getInt(cursor.getColumnIndex(COLUMN_CHAPTER_INDEX_SECTIONS))
                val title = cursor.getString(cursor.getColumnIndex(COLUMN_TITLE_SECTIONS))
                val author = cursor.getString(cursor.getColumnIndex(COLUMN_AUTHOR_SECTIONS))
                val text = cursor.getString(cursor.getColumnIndex(COLUMN_TEXT_SECTIONS))

                val sectionToAdd = SectionsModel(
                    section = section,
                    chapter = chapter,
                    title = title,
                    author = author,
                    text = text
                )
                sectionsList.add(sectionToAdd)
            } while (cursor.moveToNext())
        }
        return sectionsList
    }

    @SuppressLint("Range")
    fun getSection(section: String): SectionsModel {
        var sectionToReturn = SectionsModel(section, null, null, null, null)
        val query =
            "SELECT * FROM `${TABLE_NAME_SECTIONS}` WHERE `${COLUMN_SECTION_PK_SECTIONS}` = '$section'"
        val database = readableDatabase
        var cursor: Cursor? = null

        try {
            cursor = database.rawQuery(query, null)
        } catch (e: SQLException) {
            database.execSQL(query)
            return sectionToReturn
        }

        if (cursor.moveToFirst()) {
            val section = cursor.getString(cursor.getColumnIndex(COLUMN_SECTION_PK_SECTIONS))
            val chapter = cursor.getInt(cursor.getColumnIndex(COLUMN_CHAPTER_INDEX_SECTIONS))
            val title = cursor.getString(cursor.getColumnIndex(COLUMN_TITLE_SECTIONS))
            val author = cursor.getString(cursor.getColumnIndex(COLUMN_AUTHOR_SECTIONS))
            val text = cursor.getString(cursor.getColumnIndex(COLUMN_TEXT_SECTIONS))
            sectionToReturn.section = section
            sectionToReturn.chapter = chapter
            sectionToReturn.title = title
            sectionToReturn.author = author
            sectionToReturn.text = text
        }
        return sectionToReturn
    }

    fun updateSection(section: SectionsModel): Int {
        val database = writableDatabase

        val contentValues = ContentValues()
        contentValues.put(COLUMN_SECTION_PK_SECTIONS, section.section)
        contentValues.put(COLUMN_CHAPTER_INDEX_SECTIONS, section.chapter)
        contentValues.put(COLUMN_TITLE_SECTIONS, section.title)
        contentValues.put(COLUMN_AUTHOR_SECTIONS, section.author)
        contentValues.put(COLUMN_TEXT_SECTIONS, section.text)

        val success =
            database.update(
                TABLE_NAME_SECTIONS,
                contentValues,
                "$COLUMN_SECTION_PK_SECTIONS = '${section.section}'",
                null
            ) //we need the primary key to update a record
        database.close()
        return success
    }

    fun deleteSection(section: SectionsModel): Int {
        val database = writableDatabase
        val contentValues = ContentValues()
        contentValues.put(COLUMN_SECTION_PK_SECTIONS, section.section)
        //this is not necessary because we need only the primary key

        val success = database.delete(
            TABLE_NAME_SECTIONS,
            "$COLUMN_SECTION_PK_SECTIONS = '${section.section}'",
            null
        )
        database.close()
        return success
    }
    //End || Sections


    //Search in the Theory
    @SuppressLint("Range")
    fun searchInTheory(
        chapter: Int? = null,
        section: String? = null,
        expression: String
    ): ArrayList<SectionsModel> {
        val sectionsList = ArrayList<SectionsModel>()
        var query = ""
        val expressionToUse = expression.replace("'", " ").replace(" ", "%")
        if (chapter != null && section == null) {
            //in a specific chapter
            query =
                "SELECT * FROM `${TABLE_NAME_SECTIONS}` WHERE `chapter` = '${chapter}' AND `text` LIKE '%${expressionToUse}%' OR `title` LIKE '%${expressionToUse}%'"
        } else if (section != null) {
            //in a specific section
            query =
                "SELECT * FROM `${TABLE_NAME_SECTIONS}` WHERE `section` = '${section}' AND `text` LIKE '%${expressionToUse}%' OR `title` LIKE '%${expressionToUse}%'"
        } else {
            //in general
            query =
                "SELECT * FROM `${TABLE_NAME_SECTIONS}` WHERE `text` LIKE '%${expressionToUse}%' OR `title` LIKE '%${expressionToUse}%'"
        }
        val database = readableDatabase
        var cursor: Cursor? = null

        try {
            cursor = database.rawQuery(query, null)
        } catch (e: SQLException) {
            database.execSQL(query)
            return ArrayList()
        }

        if (cursor.moveToFirst()) {
            do {
                val section = cursor.getString(cursor.getColumnIndex(COLUMN_SECTION_PK_SECTIONS))
                val chapter = cursor.getInt(cursor.getColumnIndex(COLUMN_CHAPTER_INDEX_SECTIONS))
                val title = cursor.getString(cursor.getColumnIndex(COLUMN_TITLE_SECTIONS))
                val author = cursor.getString(cursor.getColumnIndex(COLUMN_AUTHOR_SECTIONS))
                val text = cursor.getString(cursor.getColumnIndex(COLUMN_TEXT_SECTIONS))

                val sectionToAdd = SectionsModel(
                    section = section,
                    chapter = chapter,
                    title = title,
                    author = author,
                    text = text
                )
                sectionsList.add(sectionToAdd)
            } while (cursor.moveToNext())
        }
        return sectionsList
    }

    //Quizzes
    fun addQuiz(quiz: QuizzesModel): Long {
        val database = writableDatabase

        val contentValues = ContentValues()
        contentValues.put(COLUMN_ID_PK_QUIZZES, quiz.id)
        contentValues.put(COLUMN_CHAPTER_INDEX_QUIZZES, quiz.chapter)
        contentValues.put(COLUMN_SECTION_INDEX_QUIZZES, quiz.section)
        contentValues.put(COLUMN_QUESTION_QUIZZES, quiz.question)
        contentValues.put(COLUMN_A_QUIZZES, quiz.A)
        contentValues.put(COLUMN_B_QUIZZES, quiz.B)
        contentValues.put(COLUMN_C_QUIZZES, quiz.C)
        contentValues.put(COLUMN_D_QUIZZES, quiz.D)
        contentValues.put(COLUMN_CORRECT_QUIZZES, quiz.correct)
        val success = database.insert(TABLE_NAME_QUIZZES, null, contentValues)
        database.close()

        return success
    }

    @SuppressLint("Range")
    fun getQuizzes(): ArrayList<QuizzesModel> {
        val quizzesList = ArrayList<QuizzesModel>()
        val query = "SELECT * FROM `${TABLE_NAME_QUIZZES}`"
        val database = readableDatabase
        var cursor: Cursor? = null

        try {
            cursor = database.rawQuery(query, null)
        } catch (e: SQLException) {
            database.execSQL(query)
            return ArrayList()
        }

        if (cursor.moveToFirst()) {
            do {
                val id = cursor.getInt(cursor.getColumnIndex(COLUMN_ID_PK_QUIZZES))
                val chapter = cursor.getInt(cursor.getColumnIndex(COLUMN_CHAPTER_INDEX_QUIZZES))
                val section = cursor.getString(cursor.getColumnIndex(COLUMN_SECTION_INDEX_QUIZZES))
                val question = cursor.getString(cursor.getColumnIndex(COLUMN_QUESTION_QUIZZES))
                val A = cursor.getString(cursor.getColumnIndex(COLUMN_A_QUIZZES))
                val B = cursor.getString(cursor.getColumnIndex(COLUMN_B_QUIZZES))
                val C = cursor.getString(cursor.getColumnIndex(COLUMN_C_QUIZZES))
                val D = cursor.getString(cursor.getColumnIndex(COLUMN_D_QUIZZES))
                val correct = cursor.getString(cursor.getColumnIndex(COLUMN_CORRECT_QUIZZES))

                val quizToAdd = QuizzesModel(
                    id = id,
                    chapter = chapter,
                    section = section,
                    question = question,
                    A = A,
                    B = B,
                    C = C,
                    D = D,
                    correct = correct
                )
                quizzesList.add(quizToAdd)
            } while (cursor.moveToNext())
        }
        return quizzesList
    }

    @SuppressLint("Range")
    fun getQuizzes(chapter: Int?): ArrayList<QuizzesModel> {
        val quizzesList = ArrayList<QuizzesModel>()
        var query = ""
        if (chapter == null) query = "SELECT * FROM `${TABLE_NAME_QUIZZES}`"
        else query = "SELECT * FROM `${TABLE_NAME_QUIZZES}` WHERE `chapter` = '${chapter}'"
        val database = readableDatabase
        var cursor: Cursor? = null

        try {
            cursor = database.rawQuery(query, null)
        } catch (e: SQLException) {
            database.execSQL(query)
            return ArrayList()
        }

        if (cursor.moveToFirst()) {
            do {
                val id = cursor.getInt(cursor.getColumnIndex(COLUMN_ID_PK_QUIZZES))
                val chapter = cursor.getInt(cursor.getColumnIndex(COLUMN_CHAPTER_INDEX_QUIZZES))
                val section = cursor.getString(cursor.getColumnIndex(COLUMN_SECTION_INDEX_QUIZZES))
                val question = cursor.getString(cursor.getColumnIndex(COLUMN_QUESTION_QUIZZES))
                val A = cursor.getString(cursor.getColumnIndex(COLUMN_A_QUIZZES))
                val B = cursor.getString(cursor.getColumnIndex(COLUMN_B_QUIZZES))
                val C = cursor.getString(cursor.getColumnIndex(COLUMN_C_QUIZZES))
                val D = cursor.getString(cursor.getColumnIndex(COLUMN_D_QUIZZES))
                val correct = cursor.getString(cursor.getColumnIndex(COLUMN_CORRECT_QUIZZES))

                val quizToAdd = QuizzesModel(
                    id = id,
                    chapter = chapter,
                    section = section,
                    question = question,
                    A = A,
                    B = B,
                    C = C,
                    D = D,
                    correct = correct
                )
                quizzesList.add(quizToAdd)
            } while (cursor.moveToNext())
        }
        return quizzesList
    }

    @SuppressLint("Range")
    fun getQuizzesRandom(limit: Int = 50): ArrayList<QuizzesModel> {
        val quizzesList = ArrayList<QuizzesModel>()
        val query = "SELECT * FROM `${TABLE_NAME_QUIZZES}` ORDER BY RANDOM() LIMIT $limit"
        val database = readableDatabase
        var cursor: Cursor? = null

        try {
            cursor = database.rawQuery(query, null)
        } catch (e: SQLException) {
            database.execSQL(query)
            return ArrayList()
        }

        if (cursor.moveToFirst()) {
            do {
                val id = cursor.getInt(cursor.getColumnIndex(COLUMN_ID_PK_QUIZZES))
                val chapter = cursor.getInt(cursor.getColumnIndex(COLUMN_CHAPTER_INDEX_QUIZZES))
                val section = cursor.getString(cursor.getColumnIndex(COLUMN_SECTION_INDEX_QUIZZES))
                val question = cursor.getString(cursor.getColumnIndex(COLUMN_QUESTION_QUIZZES))
                val A = cursor.getString(cursor.getColumnIndex(COLUMN_A_QUIZZES))
                val B = cursor.getString(cursor.getColumnIndex(COLUMN_B_QUIZZES))
                val C = cursor.getString(cursor.getColumnIndex(COLUMN_C_QUIZZES))
                val D = cursor.getString(cursor.getColumnIndex(COLUMN_D_QUIZZES))
                val correct = cursor.getString(cursor.getColumnIndex(COLUMN_CORRECT_QUIZZES))

                val quizToAdd = QuizzesModel(
                    id = id,
                    chapter = chapter,
                    section = section,
                    question = question,
                    A = A,
                    B = B,
                    C = C,
                    D = D,
                    correct = correct
                )
                quizzesList.add(quizToAdd)
            } while (cursor.moveToNext())
        }
        return quizzesList
    }

    @SuppressLint("Range")
    fun getQuiz(id: Int): QuizzesModel {
        var quizToReturn = QuizzesModel(id, null, null, null, null, null, null, null, null)
        val query =
            "SELECT * FROM `${TABLE_NAME_QUIZZES}` WHERE `${COLUMN_ID_PK_QUIZZES}` = $id"
        val database = readableDatabase
        var cursor: Cursor? = null

        try {
            cursor = database.rawQuery(query, null)
        } catch (e: SQLException) {
            database.execSQL(query)
            return quizToReturn
        }

        if (cursor.moveToFirst()) {
            val id = cursor.getInt(cursor.getColumnIndex(COLUMN_ID_PK_QUIZZES))
            val chapter = cursor.getInt(cursor.getColumnIndex(COLUMN_CHAPTER_INDEX_QUIZZES))
            val section = cursor.getString(cursor.getColumnIndex(COLUMN_SECTION_INDEX_QUIZZES))
            val question = cursor.getString(cursor.getColumnIndex(COLUMN_QUESTION_QUIZZES))
            val A = cursor.getString(cursor.getColumnIndex(COLUMN_A_QUIZZES))
            val B = cursor.getString(cursor.getColumnIndex(COLUMN_B_QUIZZES))
            val C = cursor.getString(cursor.getColumnIndex(COLUMN_C_QUIZZES))
            val D = cursor.getString(cursor.getColumnIndex(COLUMN_D_QUIZZES))
            val correct = cursor.getString(cursor.getColumnIndex(COLUMN_CORRECT_QUIZZES))
            quizToReturn.id = id
            quizToReturn.chapter = chapter
            quizToReturn.section = section
            quizToReturn.question = question
            quizToReturn.A = A
            quizToReturn.B = B
            quizToReturn.C = C
            quizToReturn.D = D
            quizToReturn.correct = correct
        }
        return quizToReturn
    }

    fun updateQuiz(quiz: QuizzesModel): Int {
        val database = writableDatabase

        val contentValues = ContentValues()
        contentValues.put(COLUMN_ID_PK_QUIZZES, quiz.id)
        contentValues.put(COLUMN_CHAPTER_INDEX_QUIZZES, quiz.chapter)
        contentValues.put(COLUMN_SECTION_INDEX_QUIZZES, quiz.section)
        contentValues.put(COLUMN_QUESTION_QUIZZES, quiz.question)
        contentValues.put(COLUMN_A_QUIZZES, quiz.A)
        contentValues.put(COLUMN_B_QUIZZES, quiz.B)
        contentValues.put(COLUMN_C_QUIZZES, quiz.C)
        contentValues.put(COLUMN_D_QUIZZES, quiz.D)
        contentValues.put(COLUMN_CORRECT_QUIZZES, quiz.correct)

        val success =
            database.update(
                TABLE_NAME_QUIZZES,
                contentValues,
                "$COLUMN_ID_PK_QUIZZES = '${quiz.id}'",
                null
            ) //we need the primary key to update a record
        database.close()
        return success
    }

    fun deleteQuiz(quiz: QuizzesModel): Int {
        val database = writableDatabase
        val contentValues = ContentValues()
        contentValues.put(COLUMN_ID_PK_QUIZZES, quiz.id)
        //this is not necessary because we need only the primary key

        val success = database.delete(
            TABLE_NAME_QUIZZES,
            "$COLUMN_ID_PK_QUIZZES = '${quiz.id}'",
            null
        )
        database.close()
        return success
    }
    //End || Quizzes


    //News
    fun addNews(news: NewsModel): Long {
        val database = writableDatabase

        val contentValues = ContentValues()
        contentValues.put(COLUMN_ID_PK_NEWS, news.id)
        contentValues.put(COLUMN_TYPE_NEWS, news.type)
        contentValues.put(COLUMN_DATE_NEWS, news.date)
        if (news.title == null) contentValues.put(COLUMN_TITLE_NEWS, "NULL")
        else contentValues.put(COLUMN_TITLE_NEWS, news.title)
        if (news.image == null) contentValues.put(COLUMN_IMAGE_NEWS, "NULL")
        else contentValues.put(COLUMN_IMAGE_NEWS, news.image)
        if (news.link == null) contentValues.put(COLUMN_LINK_NEWS, "NULL")
        else contentValues.put(COLUMN_LINK_NEWS, news.link)
        contentValues.put(COLUMN_TEXT_NEWS, news.text)
        val success = database.insert(TABLE_NAME_NEWS, null, contentValues)
        database.close()

        return success
    }

    @SuppressLint("Range")
    fun getNews(): ArrayList<NewsModel> {
        val newsList = ArrayList<NewsModel>()
        val query =
            "SELECT * FROM `${TABLE_NAME_NEWS}` ORDER BY `${COLUMN_DATE_NEWS}` DESC, `${COLUMN_ID_PK_NEWS}` DESC"
        val database = readableDatabase
        var cursor: Cursor? = null

        try {
            cursor = database.rawQuery(query, null)
        } catch (e: SQLException) {
            database.execSQL(query)
            return ArrayList()
        }

        if (cursor.moveToFirst()) {
            do {
                val id = cursor.getInt(cursor.getColumnIndex(COLUMN_ID_PK_NEWS))
                val type = cursor.getInt(cursor.getColumnIndex(COLUMN_TYPE_NEWS))
                val date = cursor.getString(cursor.getColumnIndex(COLUMN_DATE_NEWS))
                val title = cursor.getString(cursor.getColumnIndex(COLUMN_TITLE_NEWS))
                val image = cursor.getString(cursor.getColumnIndex(COLUMN_IMAGE_NEWS))
                val link = cursor.getString(cursor.getColumnIndex(COLUMN_LINK_NEWS))
                val text = cursor.getString(cursor.getColumnIndex(COLUMN_TEXT_NEWS))

                val newsToAdd = NewsModel(
                    id = id,
                    type = type,
                    date = date,
                    title = title,
                    image = image,
                    link = link,
                    text = text
                )
                newsList.add(newsToAdd)
            } while (cursor.moveToNext())
        }
        return newsList
    }

    @SuppressLint("Range")
    fun getNews(id: Int): NewsModel {
        var newsToReturn = NewsModel(id, 0, "", null, null, null, "")
        val query =
            "SELECT * FROM `${TABLE_NAME_NEWS}` WHERE `${COLUMN_ID_PK_NEWS}` = $id ORDER BY `${COLUMN_DATE_NEWS}` DESC, `${COLUMN_ID_PK_NEWS}` DESC"
        val database = readableDatabase
        var cursor: Cursor? = null

        try {
            cursor = database.rawQuery(query, null)
        } catch (e: SQLException) {
            database.execSQL(query)
            return newsToReturn
        }

        if (cursor.moveToFirst()) {
            val id = cursor.getInt(cursor.getColumnIndex(COLUMN_ID_PK_NEWS))
            val type = cursor.getInt(cursor.getColumnIndex(COLUMN_TYPE_NEWS))
            val date = cursor.getString(cursor.getColumnIndex(COLUMN_DATE_NEWS))
            val title = cursor.getString(cursor.getColumnIndex(COLUMN_TITLE_NEWS))
            val image = cursor.getString(cursor.getColumnIndex(COLUMN_IMAGE_NEWS))
            val link = cursor.getString(cursor.getColumnIndex(COLUMN_LINK_NEWS))
            val text = cursor.getString(cursor.getColumnIndex(COLUMN_TEXT_NEWS))
            newsToReturn.id = id
            newsToReturn.type = type
            newsToReturn.date = date
            newsToReturn.title = title
            newsToReturn.image = image
            newsToReturn.link = link
            newsToReturn.text = text
        }
        return newsToReturn
    }

    @SuppressLint("Range")
    fun checkNews(id: Int): Boolean {
        var returnValue = false
        val query =
            "SELECT * FROM `${TABLE_NAME_NEWS}` WHERE `${COLUMN_ID_PK_NEWS}` = $id"
        val database = readableDatabase
        var cursor: Cursor? = null

        try {
            cursor = database.rawQuery(query, null)
        } catch (e: SQLException) {
            database.execSQL(query)
            return returnValue
        }

        if (cursor.moveToFirst()) {
            returnValue = true
        }
        return returnValue
    }

    fun updateNews(news: NewsModel): Int {
        val database = writableDatabase

        val contentValues = ContentValues()
        contentValues.put(COLUMN_ID_PK_NEWS, news.id)
        contentValues.put(COLUMN_TYPE_NEWS, news.type)
        contentValues.put(COLUMN_DATE_NEWS, news.date)
        if (news.title == null) contentValues.put(COLUMN_TITLE_NEWS, "NULL")
        else contentValues.put(COLUMN_TITLE_NEWS, news.title)
        if (news.image == null) contentValues.put(COLUMN_IMAGE_NEWS, "NULL")
        else contentValues.put(COLUMN_IMAGE_NEWS, news.image)
        if (news.link == null) contentValues.put(COLUMN_LINK_NEWS, "NULL")
        else contentValues.put(COLUMN_LINK_NEWS, news.link)
        contentValues.put(COLUMN_TEXT_NEWS, news.text)

        val success = database.update(
            TABLE_NAME_NEWS,
            contentValues,
            "$COLUMN_ID_PK_NEWS = '${news.id}'",
            null
        ) //we need the primary key to update a record
        database.close()
        return success
    }

    fun deleteNews(news: NewsModel): Int {
        val database = writableDatabase
        val contentValues = ContentValues()
        contentValues.put(COLUMN_ID_PK_NEWS, news.id)
        //this is not necessary because we need only the primary key

        val success = database.delete(
            TABLE_NAME_NEWS,
            "$COLUMN_ID_PK_NEWS = '${news.id}'",
            null
        )
        database.close()
        return success
    }
    //End || News

    //Statistics
    fun addStatistics(statistics: StatisticsModel): Long {
        val database = writableDatabase

        val contentValues = ContentValues()
        contentValues.put(COLUMN_ID_PK_STATISTICS, statistics.id)
        contentValues.put(COLUMN_TYPE_STATISTICS, statistics.type)
        contentValues.put(COLUMN_DATETIME_STATISTICS, statistics.datetime)
        if (statistics.correct_answer == null)
            contentValues.put(COLUMN_CORRECT_ANSWER_STATISTICS, "NULL")
        else contentValues.put(COLUMN_CORRECT_ANSWER_STATISTICS, statistics.correct_answer)
        if (statistics.user_answer == null) contentValues.put(COLUMN_USER_ANSWER_STATISTICS, "NULL")
        else contentValues.put(COLUMN_USER_ANSWER_STATISTICS, statistics.user_answer)
        contentValues.put(COLUMN_QUESTION_ID_STATISTICS, statistics.question_id)
        contentValues.put(COLUMN_MILLISECONDS_STATISTICS, statistics.milliseconds)
        val success = database.insert(TABLE_NAME_STATISTICS, null, contentValues)
        database.close()

        return success
    }

    @SuppressLint("Range")
    fun getNewIdStatistics(): Int {
        //get a new unique id for statistics (based to the last one created)
        var valueToReturn = 0
        val query =
            "SELECT * FROM `${TABLE_NAME_STATISTICS}` ORDER BY `${COLUMN_ID_PK_STATISTICS}` DESC LIMIT 1"
        val database = readableDatabase
        var cursor: Cursor? = null

        try {
            cursor = database.rawQuery(query, null)
        } catch (e: SQLException) {
            database.execSQL(query)
            return valueToReturn
        }

        if (cursor.moveToFirst()) {
            valueToReturn = cursor.getInt(cursor.getColumnIndex(COLUMN_ID_PK_STATISTICS)) + 1
        }
        return valueToReturn
    }

    @SuppressLint("Range")
    fun getStatistics(
        question_id: Int? = null,
        type: Int? = null,
        datetime: String? = null
    ): ArrayList<StatisticsModel> {
        //get all statistics data filtered by some parameters (question_id and/or type and/or datetime)
        val statisticsList = ArrayList<StatisticsModel>()
        var details = ""
        if (question_id != null) details += "`${COLUMN_QUESTION_ID_STATISTICS}` = '${question_id}'"
        if (question_id != null && (type != null || datetime != null)) details += " AND "
        if (type != null) details += "`${COLUMN_TYPE_STATISTICS}` = '${type}'"
        if (type != null && datetime != null) details += " AND "
        if (datetime != null) details += "`${COLUMN_DATETIME_STATISTICS}` = '${datetime}'"
        var query = ""
        if (question_id == null && type == null && datetime == null) query =
            "SELECT * FROM `${TABLE_NAME_STATISTICS}` ORDER BY `${COLUMN_DATETIME_STATISTICS}` DESC"
        else query =
            "SELECT * FROM `${TABLE_NAME_STATISTICS}` WHERE $details ORDER BY `${COLUMN_DATETIME_STATISTICS}` DESC"

        //println(query)

        val database = readableDatabase
        var cursor: Cursor? = null

        try {
            cursor = database.rawQuery(query, null)
        } catch (e: SQLException) {
            database.execSQL(query)
            return ArrayList()
        }

        if (cursor.moveToFirst()) {
            do {
                val id = cursor.getInt(cursor.getColumnIndex(COLUMN_ID_PK_STATISTICS))
                val type = cursor.getInt(cursor.getColumnIndex(COLUMN_TYPE_STATISTICS))
                val datetime = cursor.getString(cursor.getColumnIndex(COLUMN_DATETIME_STATISTICS))
                val questionId =
                    cursor.getInt(cursor.getColumnIndex(COLUMN_QUESTION_ID_STATISTICS))
                val correctAnswer =
                    cursor.getString(cursor.getColumnIndex(COLUMN_CORRECT_ANSWER_STATISTICS))
                val userAnswer =
                    cursor.getString(cursor.getColumnIndex(COLUMN_USER_ANSWER_STATISTICS))
                val milliseconds =
                    cursor.getInt(cursor.getColumnIndex(COLUMN_MILLISECONDS_STATISTICS))

                val statisticsToAdd = StatisticsModel(
                    id = id,
                    type = type,
                    datetime = datetime,
                    question_id = questionId,
                    correct_answer = correctAnswer,
                    user_answer = userAnswer,
                    milliseconds = milliseconds
                )
                statisticsList.add(statisticsToAdd)
            } while (cursor.moveToNext())
        }
        return statisticsList
    }

    @SuppressLint("Range")
    fun getWrongCorrectSkippedAnswersStatistics(
        datetime: String? = null,
        filter: String = "ws"
    ): ArrayList<StatisticsModel> {
        //get all wrong answers of a specific simulation
        //filter can be {w|c|s|ws} -> w:wrong, c:correct, s:skipped, ws:wrong and skipped || other values will be treated as "ws"
        val statisticsList = ArrayList<StatisticsModel>()
        var query = ""
        var details = ""
        var details2 = ""
        if (datetime != null) {
            details = "`${COLUMN_DATETIME_STATISTICS}` = '${datetime}' AND "
            details2 = " ORDER BY `${COLUMN_DATETIME_STATISTICS}` DESC"
        }
        if (filter == "c") {
            //c:correct
            query =
                "SELECT * FROM `${TABLE_NAME_STATISTICS}` WHERE $details`${COLUMN_TYPE_STATISTICS}` = 1 AND `${COLUMN_USER_ANSWER_STATISTICS}` = `${COLUMN_CORRECT_ANSWER_STATISTICS}`$details2"
        } else if (filter == "s") {
            //s:skipped
            query =
                "SELECT * FROM `${TABLE_NAME_STATISTICS}` WHERE $details`${COLUMN_TYPE_STATISTICS}` = 1 AND `${COLUMN_USER_ANSWER_STATISTICS}` = ''$details2"
        } else {
            //w:wrong or ws:wrong and skipped (or other values)
            var details1 = ""
            if (filter == "w") {
                //w:wrong (only)
                details1 += "AND NOT `${COLUMN_USER_ANSWER_STATISTICS}` = ''"
            }
            query =
                "SELECT * FROM `${TABLE_NAME_STATISTICS}` WHERE $details`${COLUMN_TYPE_STATISTICS}` = 1 $details1 AND NOT `${COLUMN_USER_ANSWER_STATISTICS}` = `${COLUMN_CORRECT_ANSWER_STATISTICS}`$details2"
        }

        val database = readableDatabase
        var cursor: Cursor? = null

        try {
            cursor = database.rawQuery(query, null)
        } catch (e: SQLException) {
            database.execSQL(query)
            return ArrayList()
        }

        if (cursor.moveToFirst()) {
            do {
                val id = cursor.getInt(cursor.getColumnIndex(COLUMN_ID_PK_STATISTICS))
                val type = cursor.getInt(cursor.getColumnIndex(COLUMN_TYPE_STATISTICS))
                val datetime = cursor.getString(cursor.getColumnIndex(COLUMN_DATETIME_STATISTICS))
                val questionId =
                    cursor.getInt(cursor.getColumnIndex(COLUMN_QUESTION_ID_STATISTICS))
                val correctAnswer =
                    cursor.getString(cursor.getColumnIndex(COLUMN_CORRECT_ANSWER_STATISTICS))
                val userAnswer =
                    cursor.getString(cursor.getColumnIndex(COLUMN_USER_ANSWER_STATISTICS))
                val milliseconds =
                    cursor.getInt(cursor.getColumnIndex(COLUMN_MILLISECONDS_STATISTICS))

                val statisticsToAdd = StatisticsModel(
                    id = id,
                    type = type,
                    datetime = datetime,
                    question_id = questionId,
                    correct_answer = correctAnswer,
                    user_answer = userAnswer,
                    milliseconds = milliseconds
                )
                statisticsList.add(statisticsToAdd)
            } while (cursor.moveToNext())
        }
        return statisticsList
    }

    @SuppressLint("Range")
    fun getMistakesStatistics(): ArrayList<StatisticsModel> {
        //return (DISTINCT) mistakes quiz
        val statisticsList = ArrayList<StatisticsModel>()
        val query =
            "SELECT DISTINCT `${COLUMN_QUESTION_ID_STATISTICS}` FROM `${TABLE_NAME_STATISTICS}` WHERE NOT `${COLUMN_CORRECT_ANSWER_STATISTICS}` = `${COLUMN_USER_ANSWER_STATISTICS}` ORDER BY `${COLUMN_DATETIME_STATISTICS}` DESC"

        //println(query)

        val database = readableDatabase
        var cursor: Cursor? = null

        try {
            cursor = database.rawQuery(query, null)
        } catch (e: SQLException) {
            database.execSQL(query)
            return ArrayList()
        }

        if (cursor.moveToFirst()) {
            do {
                val statisticsToAdd = getOneStatisticByQuestionId(
                    cursor.getInt(cursor.getColumnIndex(COLUMN_QUESTION_ID_STATISTICS))
                )
                statisticsList.add(statisticsToAdd)
            } while (cursor.moveToNext())
        }
        return statisticsList
    }

    @SuppressLint("Range")
    fun getOneStatisticByQuestionId(question_id: Int): StatisticsModel {
        var statisticsToReturn = StatisticsModel(-1, 0, question_id, "", null, null, -1)
        val query =
            "SELECT * FROM `${TABLE_NAME_STATISTICS}` WHERE `${COLUMN_QUESTION_ID_STATISTICS}` = $question_id ORDER BY `${COLUMN_DATETIME_STATISTICS}` DESC LIMIT 1"
        val database = readableDatabase
        var cursor: Cursor? = null

        try {
            cursor = database.rawQuery(query, null)
        } catch (e: SQLException) {
            database.execSQL(query)
            return statisticsToReturn
        }

        if (cursor.moveToFirst()) {
            val id = cursor.getInt(cursor.getColumnIndex(COLUMN_ID_PK_STATISTICS))
            val type = cursor.getInt(cursor.getColumnIndex(COLUMN_TYPE_STATISTICS))
            val datetime = cursor.getString(cursor.getColumnIndex(COLUMN_DATETIME_STATISTICS))
            val questionId = cursor.getInt(cursor.getColumnIndex(COLUMN_QUESTION_ID_STATISTICS))
            val correctAnswer =
                cursor.getString(cursor.getColumnIndex(COLUMN_CORRECT_ANSWER_STATISTICS))
            val userAnswer = cursor.getString(cursor.getColumnIndex(COLUMN_USER_ANSWER_STATISTICS))
            val milliseconds = cursor.getInt(cursor.getColumnIndex(COLUMN_MILLISECONDS_STATISTICS))

            statisticsToReturn.id = id
            statisticsToReturn.type = type
            statisticsToReturn.datetime = datetime
            statisticsToReturn.question_id = questionId
            statisticsToReturn.correct_answer = correctAnswer
            statisticsToReturn.user_answer = userAnswer
            statisticsToReturn.milliseconds = milliseconds
        }
        return statisticsToReturn
    }

    @SuppressLint("Range")
    fun getAllSimulations(): ArrayList<String> {
        var arrayToReturn = ArrayList<String>()
        val query =
            "SELECT DISTINCT `${COLUMN_DATETIME_STATISTICS}` FROM `${TABLE_NAME_STATISTICS}` WHERE `${COLUMN_TYPE_STATISTICS}` = 1 ORDER BY `${COLUMN_DATETIME_STATISTICS}` DESC"
        val database = readableDatabase
        var cursor: Cursor? = null

        try {
            cursor = database.rawQuery(query, null)
        } catch (e: SQLException) {
            database.execSQL(query)
            return arrayToReturn
        }

        if (cursor.moveToFirst()) {
            do {
                val datetime = cursor.getString(cursor.getColumnIndex(COLUMN_DATETIME_STATISTICS))
                //println(datetime)
                arrayToReturn.add(datetime)
            } while (cursor.moveToNext())
        }
        return arrayToReturn
    }

    @SuppressLint("Range")
    fun checkStatistics(question: Int, type: Int): Boolean {
        var returnValue = false
        val query =
            "SELECT * FROM `${TABLE_NAME_STATISTICS}` WHERE `${COLUMN_QUESTION_ID_STATISTICS}` = $question AND `${COLUMN_TYPE_STATISTICS}` = $type"

        val database = readableDatabase
        var cursor: Cursor? = null

        try {
            cursor = database.rawQuery(query, null)
        } catch (e: SQLException) {
            database.execSQL(query)
            return returnValue
        }

        if (cursor.moveToFirst()) {
            returnValue = true
        }
        return returnValue
    }

    fun updateStatistics(statistics: StatisticsModel): Int {
        //update an existing statistics using it's primary key (id)
        val database = writableDatabase

        val contentValues = ContentValues()
        contentValues.put(COLUMN_ID_PK_STATISTICS, statistics.id)
        contentValues.put(COLUMN_TYPE_STATISTICS, statistics.type)
        contentValues.put(COLUMN_DATETIME_STATISTICS, statistics.datetime)
        if (statistics.correct_answer == null)
            contentValues.put(COLUMN_CORRECT_ANSWER_STATISTICS, "NULL")
        else contentValues.put(COLUMN_CORRECT_ANSWER_STATISTICS, statistics.correct_answer)
        if (statistics.user_answer == null) contentValues.put(COLUMN_USER_ANSWER_STATISTICS, "NULL")
        else contentValues.put(COLUMN_USER_ANSWER_STATISTICS, statistics.user_answer)
        contentValues.put(COLUMN_QUESTION_ID_STATISTICS, statistics.question_id)
        contentValues.put(COLUMN_MILLISECONDS_STATISTICS, statistics.milliseconds)
        val success = database.update(
            TABLE_NAME_STATISTICS,
            contentValues,
            "$COLUMN_ID_PK_STATISTICS = '${statistics.id}'",
            null
        ) //we need the primary key to update a record
        database.close()

        return success
    }

    fun deleteAllStatistics() {
        val database = writableDatabase

        val success = database.delete(
            TABLE_NAME_STATISTICS,
            null,
            null
        )
        database.close()
    }
    //End || Statistics

    companion object {
        //general
        private val DATABASE_NAME = "QuizNuoto"
        private val DATABASE_VERSION = 12 //TODO: change this manually

        //chapters table
        val TABLE_NAME_CHAPTERS = "chapters"
        val COLUMN_CHAPTER_PK_CHAPTERS = "chapter"
        val COLUMN_TITLE_CHAPTERS = "title"

        //sections table
        val TABLE_NAME_SECTIONS = "sections"
        val COLUMN_SECTION_PK_SECTIONS = "section"
        val COLUMN_CHAPTER_INDEX_SECTIONS = "chapter"
        val COLUMN_TITLE_SECTIONS = "title"
        val COLUMN_AUTHOR_SECTIONS = "author"
        val COLUMN_TEXT_SECTIONS = "text"

        //quizzes table
        val TABLE_NAME_QUIZZES = "quizzes"
        val COLUMN_ID_PK_QUIZZES = "id"
        val COLUMN_CHAPTER_INDEX_QUIZZES = "chapter"
        val COLUMN_SECTION_INDEX_QUIZZES = "section"
        val COLUMN_QUESTION_QUIZZES = "question"
        val COLUMN_A_QUIZZES = "A"
        val COLUMN_B_QUIZZES = "B"
        val COLUMN_C_QUIZZES = "C"
        val COLUMN_D_QUIZZES = "D"
        val COLUMN_CORRECT_QUIZZES = "correct"

        //news table
        val TABLE_NAME_NEWS = "news"
        val COLUMN_ID_PK_NEWS = "id"
        val COLUMN_TYPE_NEWS = "type"
        val COLUMN_DATE_NEWS = "date"
        val COLUMN_TITLE_NEWS = "title"
        val COLUMN_IMAGE_NEWS = "image"
        val COLUMN_LINK_NEWS = "link"
        val COLUMN_TEXT_NEWS = "text"

        //statistics table
        val TABLE_NAME_STATISTICS = "statistics"
        val COLUMN_ID_PK_STATISTICS = "id"
        val COLUMN_TYPE_STATISTICS = "type"
        val COLUMN_CORRECT_ANSWER_STATISTICS = "correct_answer"
        val COLUMN_USER_ANSWER_STATISTICS = "user_answer"
        val COLUMN_DATETIME_STATISTICS = "datetime"
        val COLUMN_QUESTION_ID_STATISTICS = "question"
        val COLUMN_MILLISECONDS_STATISTICS = "milliseconds"
    }
}