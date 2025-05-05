package com.example.ratingguessr

import android.app.Application
import android.os.CountDownTimer
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.example.ratingguessr.networking.Movie
import android.util.Log
import android.view.View

// Has to be an AndroidViewModel to get the context for the SimpleSQL which requires activity context
class GameViewModel(application: Application) : AndroidViewModel(application) {

    private var timer: CountDownTimer? = null
    private val totalTime = 10000L // 10 seconds
    private val interval = 30L // update every 100ms
    private var remainingTime: Long = totalTime

    // Class to handle evaluation
    sealed class AnswerResult {
        data object Correct : AnswerResult()
        data object Incorrect : AnswerResult()
    }

    private val _answerResult = MutableLiveData<AnswerResult?>()
    val answerResult: LiveData<AnswerResult?> = _answerResult

    private val movieRepository = MovieRepository()
    private val dbHelper = SimpleSQL(application.applicationContext)

    private var _moviePair = MutableLiveData<Pair<Movie, Movie>?>()
    val moviePair: LiveData<Pair<Movie, Movie>?> = _moviePair

    private val _score = MutableLiveData<Float>(0f)
    val score: LiveData<Float> = _score

    private val _winningMovie = MutableLiveData<Int>()

    private val _selectedMovie = MutableLiveData<Int?>()
    val selectedMovie: MutableLiveData<Int?> get() = _selectedMovie

    private val _gameOver = MutableLiveData<Boolean>()
    val gameOver: LiveData<Boolean> get() = _gameOver

    private val _hasNavigatedToGameOver = MutableLiveData<Boolean>()

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

    fun evaluateAnswer(timeBonus: Float = 0f) {
        if (_selectedMovie.value == _winningMovie.value) {
            addToScore(1f + timeBonus)
            _answerResult.value = AnswerResult.Correct
        } else {
            _answerResult.value = AnswerResult.Incorrect
        }
    }

    fun setSelectedMovie(index: Int) {
        _selectedMovie.value = index
    }

    fun addToScore(base: Float = 1f) {
        _score.value = (_score.value ?: 0f) + base
    }

    fun resetScore() {
        _score.value = 0f
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

    private fun resetGameOverNavigation() {
        _hasNavigatedToGameOver.value = false
    }

    fun resetGame() {
        resetAnswer()
        resetScore()
        resetGameOverNavigation()
        _gameOver.value = false
        _selectedMovie.value = null
        timer?.cancel()
        _moviePair.value = null
    }

    fun startTimer(timeBar: View) {
        timer?.cancel() // Cancel existing timer if any

        timer = object : CountDownTimer(totalTime, interval) {
            override fun onTick(millisUntilFinished: Long) {
                remainingTime = millisUntilFinished // Store the remaining time

                val progress = millisUntilFinished.toFloat() / totalTime
                timeBar.scaleX = progress // Shrink horizontally
            }

            override fun onFinish() {
                timeBar.scaleX = 0f
                triggerGameOver()
            }
        }.start()
    }

    fun stopTimer() {
        timer?.cancel()
    }

    fun getTimerFraction(): Float {
        return (remainingTime.toFloat() / totalTime).coerceIn(0f, 1f)
    }

    fun getFormattedScore(): String {
        return String.format("%.2f", _score.value ?: 0f)
    }
}
