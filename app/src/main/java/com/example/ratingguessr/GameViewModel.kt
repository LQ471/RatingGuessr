package com.example.ratingguessr

import android.app.Application
import androidx.lifecycle.AndroidViewModel

// Has to be an AndroidViewModel to get the context for the SimpleSQL which requires activity context
class GameViewModel(application: Application) : AndroidViewModel(application) {

    private val dbHelper = SimpleSQL(application.applicationContext)

    init {
        // Fill DB with sample data when the ViewModel is created
        dbHelper.fillDB()
    }

    // Call this after game finishes
    // We might need a new fragment for the addScore popup?
    fun addScore(name: String, score: Int) {
        dbHelper.insertScore(name, score)
    }

    // Helper function to return topScores
    fun getTopScores(): List<HighScore> {
        return dbHelper.getTopScores()
    }
}
