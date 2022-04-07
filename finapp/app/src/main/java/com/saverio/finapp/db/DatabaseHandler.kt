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
        println("onCreate")
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
    }

    override fun onUpgrade(database: SQLiteDatabase, oldVersion: Int, newVersion: Int) {
        println("onUpgrade")
        database.execSQL("DROP TABLE IF EXISTS `${TABLE_NAME_CHAPTERS}`")
        database.execSQL("DROP TABLE IF EXISTS `${TABLE_NAME_SECTIONS}`")
        database.execSQL("DROP TABLE IF EXISTS `${TABLE_NAME_QUIZZES}`")
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
                val question = cursor.getString(cursor.getColumnIndex(COLUMN_QUESTION_QUIZZES))
                val A = cursor.getString(cursor.getColumnIndex(COLUMN_A_QUIZZES))
                val B = cursor.getString(cursor.getColumnIndex(COLUMN_B_QUIZZES))
                val C = cursor.getString(cursor.getColumnIndex(COLUMN_C_QUIZZES))
                val D = cursor.getString(cursor.getColumnIndex(COLUMN_D_QUIZZES))
                val correct = cursor.getString(cursor.getColumnIndex(COLUMN_CORRECT_QUIZZES))

                val quizToAdd = QuizzesModel(
                    id = id,
                    chapter = chapter,
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
        var quizToReturn = QuizzesModel(id, null, null, null, null, null, null, null)
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
            val question = cursor.getString(cursor.getColumnIndex(COLUMN_QUESTION_QUIZZES))
            val A = cursor.getString(cursor.getColumnIndex(COLUMN_A_QUIZZES))
            val B = cursor.getString(cursor.getColumnIndex(COLUMN_B_QUIZZES))
            val C = cursor.getString(cursor.getColumnIndex(COLUMN_C_QUIZZES))
            val D = cursor.getString(cursor.getColumnIndex(COLUMN_D_QUIZZES))
            val correct = cursor.getString(cursor.getColumnIndex(COLUMN_CORRECT_QUIZZES))
            quizToReturn.id = id
            quizToReturn.chapter = chapter
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

    companion object {
        //general
        private val DATABASE_NAME = "QuizNuoto"
        private val DATABASE_VERSION = 5 //TODO: change this manually

        //chapters table
        public val TABLE_NAME_CHAPTERS = "chapters"
        public val COLUMN_CHAPTER_PK_CHAPTERS = "chapter"
        public val COLUMN_TITLE_CHAPTERS = "title"

        //sections table
        public val TABLE_NAME_SECTIONS = "sections"
        public val COLUMN_SECTION_PK_SECTIONS = "section"
        public val COLUMN_CHAPTER_INDEX_SECTIONS = "chapter"
        public val COLUMN_TITLE_SECTIONS = "title"
        public val COLUMN_AUTHOR_SECTIONS = "author"
        public val COLUMN_TEXT_SECTIONS = "text"

        //quizzes table
        public val TABLE_NAME_QUIZZES = "quizzes"
        public val COLUMN_ID_PK_QUIZZES = "id"
        public val COLUMN_CHAPTER_INDEX_QUIZZES = "chapter"
        public val COLUMN_QUESTION_QUIZZES = "question"
        public val COLUMN_A_QUIZZES = "A"
        public val COLUMN_B_QUIZZES = "B"
        public val COLUMN_C_QUIZZES = "C"
        public val COLUMN_D_QUIZZES = "D"
        public val COLUMN_CORRECT_QUIZZES = "correct"

        //news table
        public val TABLE_NAME_NEWS = "news"
        public val COLUMN_ID_PK_NEWS = "id"
        public val COLUMN_TYPE_NEWS = "type"
        public val COLUMN_DATE_NEWS = "date"
        public val COLUMN_TITLE_NEWS = "title"
        public val COLUMN_IMAGE_NEWS = "image"
        public val COLUMN_LINK_NEWS = "link"
        public val COLUMN_TEXT_NEWS = "text"
    }
}