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
        query = "CREATE TABLE `${TABLE_NAME}` (" +
                "  `${COLUMN_CHAPTER}` INTEGER NOT NULL PRIMARY KEY," +
                "  `${COLUMN_TITLE}` TEXT NOT NULL" +
                ")"
        database.execSQL(query)
    }

    override fun onUpgrade(database: SQLiteDatabase, oldVersion: Int, newVersion: Int) {
        println("onUpgrade")
        database.execSQL("DROP TABLE IF EXISTS `${TABLE_NAME}`")
        onCreate(database)
    }


    fun addChapter(chapter: ChaptersModel): Long {
        val database = writableDatabase

        val contentValues = ContentValues()
        contentValues.put(COLUMN_CHAPTER, chapter.chapter)
        contentValues.put(COLUMN_TITLE, chapter.title)
        val success = database.insert(TABLE_NAME, null, contentValues)
        database.close()

        return success
    }

    @SuppressLint("Range")
    fun getChapters(): ArrayList<ChaptersModel> {
        val chaptersList = ArrayList<ChaptersModel>()
        val query = "SELECT * FROM `${TABLE_NAME}`"
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
                val chapter = cursor.getInt(cursor.getColumnIndex(COLUMN_CHAPTER))
                val title = cursor.getString(cursor.getColumnIndex(COLUMN_TITLE))

                val chapterToAdd = ChaptersModel(chapter = chapter, title = title)
                chaptersList.add(chapterToAdd)
            } while (cursor.moveToNext())
        }
        return chaptersList
    }

    @SuppressLint("Range")
    fun getChapter(chapter: Int): ChaptersModel {
        var chapterToReturn = ChaptersModel(chapter, null)
        val query = "SELECT * FROM `${TABLE_NAME}` WHERE `${COLUMN_CHAPTER}` = $chapter"
        val database = readableDatabase
        var cursor: Cursor? = null

        try {
            cursor = database.rawQuery(query, null)
        } catch (e: SQLException) {
            database.execSQL(query)
            return chapterToReturn
        }

        if (cursor.moveToFirst()) {
            val chapter = cursor.getInt(cursor.getColumnIndex(COLUMN_CHAPTER))
            val title = cursor.getString(cursor.getColumnIndex(COLUMN_TITLE))
            chapterToReturn.chapter = chapter
            chapterToReturn.title = title
        }
        return chapterToReturn
    }

    fun updateChapter(chapter: ChaptersModel): Int {
        val database = writableDatabase

        val contentValues = ContentValues()
        contentValues.put(COLUMN_CHAPTER, chapter.chapter)
        contentValues.put(COLUMN_TITLE, chapter.title)

        val success =
            database.update(
                TABLE_NAME,
                contentValues,
                "$COLUMN_CHAPTER = ${chapter.chapter}",
                null
            ) //we need the primary key to update a record
        database.close()
        return success
    }

    fun deleteChapter(chapter: ChaptersModel): Int {
        val database = writableDatabase
        val contentValues = ContentValues()
        contentValues.put(COLUMN_CHAPTER, chapter.chapter)
        contentValues.put(
            COLUMN_TITLE,
            chapter.title
        ) //this is not necessary because we need only the primary key

        val success = database.delete(TABLE_NAME, "$COLUMN_CHAPTER = ${chapter.chapter}", null)
        database.close()
        return success
    }

    companion object {
        //general
        private val DATABASE_NAME = "QuizNuoto"
        private val DATABASE_VERSION = 2 //TODO: change this manually

        //chapters table
        public val TABLE_NAME = "chapters"
        public val COLUMN_CHAPTER = "chapter"
        public val COLUMN_TITLE = "title"
    }
}