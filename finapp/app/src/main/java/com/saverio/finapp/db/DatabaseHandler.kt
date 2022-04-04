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
                "  `${COLUMN_CHAPTER_INDEX_SECTIONS}` INT NOT NULL KEY," +
                "  `${COLUMN_TITLE_SECTIONS}` TEXT NOT NULL," +
                "  `${COLUMN_AUTHOR_SECTIONS}` TEXT NOT NULL," +
                "  `${COLUMN_TEXT_SECTIONS}` TEXT NOT NULL"
        ")"
        database.execSQL(query)

        //Create "quizzes" table
        query = "CREATE TABLE `${TABLE_NAME_QUIZZES}` (" +
                "  `${COLUMN_ID_PK_QUIZZES}` INT NOT NULL PRIMARY KEY," +
                "  `${COLUMN_CHAPTER_INDEX_QUIZZES}` INT NOT NULL KEY," +
                "  `${COLUMN_QUESTION_QUIZZES}`  NOT NULL," +
                "  `${COLUMN_A_QUIZZES}` TEXT NOT NULL," +
                "  `${COLUMN_B_QUIZZES}` TEXT NOT NULL," +
                "  `${COLUMN_C_QUIZZES}` TEXT NOT NULL," +
                "  `${COLUMN_D_QUIZZES}` TEXT NOT NULL," +
                "  `${COLUMN_CORRECT_QUIZZES}` VARCHAR(5) NOT NULL" +
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
                "$COLUMN_CHAPTER_PK_CHAPTERS = ${chapter.chapter}",
                null
            ) //we need the primary key to update a record
        database.close()
        return success
    }

    fun deleteChapter(chapter: ChaptersModel): Int {
        val database = writableDatabase
        val contentValues = ContentValues()
        contentValues.put(COLUMN_CHAPTER_PK_CHAPTERS, chapter.chapter)
        contentValues.put(
            COLUMN_TITLE_CHAPTERS,
            chapter.title
        ) //this is not necessary because we need only the primary key

        val success = database.delete(
            TABLE_NAME_CHAPTERS,
            "$COLUMN_CHAPTER_PK_CHAPTERS = ${chapter.chapter}",
            null
        )
        database.close()
        return success
    }

    companion object {
        //general
        private val DATABASE_NAME = "QuizNuoto"
        private val DATABASE_VERSION = 2 //TODO: change this manually

        //chapters table
        public val TABLE_NAME_CHAPTERS = "chapters"
        public val COLUMN_CHAPTER_PK_CHAPTERS = "chapter"
        public val COLUMN_TITLE_CHAPTERS = "title"

        //sections table
        public val TABLE_NAME_SECTIONS = "chapters"
        public val COLUMN_SECTION_PK_SECTIONS = "section"
        public val COLUMN_CHAPTER_INDEX_SECTIONS = "chapter"
        public val COLUMN_TITLE_SECTIONS = "title"
        public val COLUMN_AUTHOR_SECTIONS = "author"
        public val COLUMN_TEXT_SECTIONS = "text"

        //quizzes table
        public val TABLE_NAME_QUIZZES = "chapters"
        public val COLUMN_ID_PK_QUIZZES = "id"
        public val COLUMN_CHAPTER_INDEX_QUIZZES = "chapter"
        public val COLUMN_QUESTION_QUIZZES = "question"
        public val COLUMN_A_QUIZZES = "A"
        public val COLUMN_B_QUIZZES = "B"
        public val COLUMN_C_QUIZZES = "C"
        public val COLUMN_D_QUIZZES = "D"
        public val COLUMN_CORRECT_QUIZZES = "correct"
    }
}