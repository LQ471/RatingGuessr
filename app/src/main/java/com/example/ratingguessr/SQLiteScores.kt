package com.example.ratingguessr

import android.content.ContentValues
import android.content.Context
import android.database.sqlite.SQLiteOpenHelper
import android.database.sqlite.SQLiteDatabase

data class HighScore(val name: String, val score: Int)

// SQLiteOpenHelper implementation for storing and retrieving high scores
class SimpleSQL(context: Context) : SQLiteOpenHelper(context, DATABASE_NAME, null, VERSION) {

    override fun onCreate(db: SQLiteDatabase) {
        db.execSQL(
            "CREATE TABLE $TABLE_NAME (" +
                    "$COLUMN_NAME TEXT," +
                    "$COLUMN_SCORE INTEGER" +
                    ");"
        )
    }

    override fun onUpgrade(db: SQLiteDatabase, oldVersion: Int, newVersion: Int) {}

    // Inserts a new name + score into the high score table
    // Here we could use execSQL and insert the params directly instead (see sqlite example file)
    fun insertScore(name: String, score: Int) {
        val db = writableDatabase
        val values = ContentValues().apply {
            put(COLUMN_NAME, name)
            put(COLUMN_SCORE, score)
        }
        db.insert(TABLE_NAME, null, values)
        db.close()
    }


    fun getTopScores(limit: Int = 5): List<HighScore> {
        val db = readableDatabase

        val cursor = db.query(
            TABLE_NAME,
            arrayOf(COLUMN_NAME, COLUMN_SCORE),
            null, null,
            null, null,
            "$COLUMN_SCORE DESC",
            limit.toString()
        )

        // gather names - scores from DB
        val scores = mutableListOf<HighScore>()
        while (cursor.moveToNext()) {
            val name = cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_NAME))
            val score = cursor.getInt(cursor.getColumnIndexOrThrow(COLUMN_SCORE))
            scores.add(HighScore(name, score))
        }

        cursor.close()
        db.close()
        return scores
    }

    fun fillDB() {
        insertScore("Jack", 1)
        insertScore("Jake", 2)
        insertScore("Jackie", 3)
        insertScore("Jacob", 4)
        insertScore("James", 5)
    }

    companion object {
        private const val VERSION = 1
        const val DATABASE_NAME = "highScores.db"
        const val TABLE_NAME = "HighScores"
        const val COLUMN_NAME = "Name"
        const val COLUMN_SCORE = "Score"
    }
}
