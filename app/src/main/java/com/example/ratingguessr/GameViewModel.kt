package com.example.ratingguessr

import androidx.lifecycle.ViewModel

class GameViewModel : ViewModel() {
    private val _highScores = mutableListOf<Int>()

    val highScores: List<Int>
        get() = _highScores

    // temp function for testing scorelist.
    fun fillScores() {
        _highScores.add(1)
        _highScores.add(2)
        _highScores.add(3)
        _highScores.add(4)
        updateScores(5)
    }

    fun updateScores(newScore: Int) {
        _highScores.add(newScore)

        _highScores.sortDescending()

        if (_highScores.size > 5) {
            _highScores.removeAt(_highScores.lastIndex)
        }
    }
}
