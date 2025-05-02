package com.example.ratingguessr

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.example.ratingguessr.networking.Movie
import android.util.Log

// Has to be an AndroidViewModel to get the context for the SimpleSQL which requires activity context
class GameViewModel(application: Application) : AndroidViewModel(application) {

    // Class to handle evaluation
    sealed class AnswerResult {
        data object Correct : AnswerResult()
        data object Incorrect : AnswerResult()
    }

    private val _answerResult = MutableLiveData<AnswerResult?>()
    val answerResult: LiveData<AnswerResult?> = _answerResult

    private val movieRepository = MovieRepository()
    private val dbHelper = SimpleSQL(application.applicationContext)

    private val _moviePair = MutableLiveData<Pair<Movie, Movie>>()
    val moviePair: LiveData<Pair<Movie, Movie>> = _moviePair

    private val _score = MutableLiveData(0)
    val score: LiveData<Int> = _score

    private val _winningMovie = MutableLiveData<Int>()

    private val _selectedMovie = MutableLiveData<Int>()
    val selectedMovie: LiveData<Int> get() = _selectedMovie

    private val _gameOver = MutableLiveData<Boolean>()
    val gameOver: LiveData<Boolean> get() = _gameOver

    private val _hasNavigatedToGameOver = MutableLiveData<Boolean>()
    val hasNavigatedToGameOver: LiveData<Boolean> get() = _hasNavigatedToGameOver

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
                            _winningMovie.value = determineWinningMovie(movie1, movie2)
                        }
                    },
                    onError = { Log.e("GameViewModel", "Fetching Movie 2 failed")}
                )
            },
            onError = { Log.e("GameViewModel", "Fetching Movie 2 failed") }
        )

    }

    private fun determineWinningMovie(movie1: Movie, movie2: Movie): Int {
        return if (movie1.voteAverage > movie2.voteAverage) 1 else 2
    }

    fun evaluateAnswer() {
        if (_selectedMovie.value == _winningMovie.value) {
            addToScore()
            _answerResult.value = AnswerResult.Correct
        } else {
            _gameOver.value = true
            _answerResult.value = AnswerResult.Incorrect
        }
    }

    fun setSelectedMovie(index: Int) {
        _selectedMovie.value = index
    }

    fun addToScore() {
        _score.value = (_score.value ?: 0) + 1
    }

    fun resetScore() {
        _score.value = 0
    }

    fun resetAnswer() {
        _answerResult.value = null
    }

    fun shouldNavigateToGameOver(): Boolean {
        if (_hasNavigatedToGameOver.value == true) return false
        _hasNavigatedToGameOver.value = true
        return true
    }

    fun triggerGameOver() {
        _gameOver.value = true
    }

    fun resetGameOver() {
        _gameOver.value = false
    }

    private fun resetGameOverNavigation() {
        _hasNavigatedToGameOver.value = false
    }

    fun resetGame() {
        resetAnswer()
        resetScore()
        resetGameOverNavigation()
        _gameOver.value = false
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
