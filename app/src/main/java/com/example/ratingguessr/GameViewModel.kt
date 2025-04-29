package com.example.ratingguessr

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.example.ratingguessr.networking.Movie
import android.util.Log

// Has to be an AndroidViewModel to get the context for the SimpleSQL which requires activity context
class GameViewModel(application: Application) : AndroidViewModel(application) {

    private val movieRepository = MovieRepository()
    private val dbHelper = SimpleSQL(application.applicationContext)

    private val _moviePair = MutableLiveData<Pair<Movie, Movie>>()
    val moviePair: LiveData<Pair<Movie, Movie>> = _moviePair

    init {
        // Fill DB with sample data when the ViewModel is created
        dbHelper.fillDB()
    }

    fun loadTwoDistinctMovies(retryCount: Int = 0) {
        val maxRetries = 5
        if (retryCount == maxRetries) return

        movieRepository.getRandomPopularMovie(
            onSuccess = { movie1 ->
                movieRepository.getRandomPopularMovie(
                    onSuccess = { movie2 ->
                        // Ensuring the two movies do not have the same title or the same rating
                        if (movie1.title == movie2.title || movie1.voteAverageString == movie2.voteAverageString) {
                            loadTwoDistinctMovies(retryCount + 1)
                        } else {
                            _moviePair.postValue(Pair(movie1, movie2))
                        }
                    },
                    onError = { Log.e("GameViewModel", "Fetching Movie 2 failed")}
                )
            },
            onError = { Log.e("GameViewModel", "Fetching Movie 2 failed") }
        )
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
