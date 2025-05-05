package com.example.ratingguessr

import android.os.Bundle
import android.os.CountDownTimer
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.ImageButton
import android.widget.TextView
import androidx.core.content.ContextCompat
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.findNavController
import androidx.navigation.fragment.findNavController
import coil.load
import androidx.core.graphics.drawable.toDrawable
import androidx.lifecycle.lifecycleScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

/**
 * A simple [Fragment] subclass.
 * Use the [GameFragment.newInstance] factory method to
 * create an instance of this fragment.
 */
class GameFragment : Fragment() {

    private lateinit var gameViewModel: GameViewModel

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_game, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        gameViewModel = ViewModelProvider(requireActivity())[GameViewModel::class.java]

        val answerButton = view.findViewById<Button>(R.id.AnswerButton)
        val scoreTextView = view.findViewById<TextView>(R.id.SessionScore)
        val nextButton = view.findViewById<Button>(R.id.NextButton)
        val timeBar = view.findViewById<View>(R.id.timeBar)
        val exitButton = view.findViewById<View>(R.id.ExitButton)

        exitButton.setOnClickListener {
            gameViewModel.resetGame()
            view.findNavController().navigate(R.id.action_gameFragment_to_introFragment)
        }

        //  Layout elements for movie on left side
        val movie1ImageButton = view.findViewById<ImageButton>(R.id.imageButtonMovie1)
        val movie1TitleTextView = view.findViewById<TextView>(R.id.titleMovie1)
        val movie1YearTextView = view.findViewById<TextView>(R.id.releaseYearMovie1)
        val movie1RatingTextView = view.findViewById<TextView>(R.id.ratingMovie1)

        //  Layout elements for movie on right side
        val movie2ImageButton = view.findViewById<ImageButton>(R.id.imageButtonMovie2)
        val movie2TitleTextView = view.findViewById<TextView>(R.id.titleMovie2)
        val movie2YearTextView = view.findViewById<TextView>(R.id.releaseYearMovie2)
        val movie2RatingTextView = view.findViewById<TextView>(R.id.ratingMovie2)

        // Setting the layout through API calls in the ViewModel
        gameViewModel.moviePair.observe(viewLifecycleOwner) { (movie1, movie2) ->
            movie1ImageButton.load(movie1.fullPosterUrl)
            movie1TitleTextView.text = movie1.title
            movie1YearTextView.text = movie1.releaseYear
            movie1RatingTextView.text = movie1.voteAverageString

            movie2ImageButton.load(movie2.fullPosterUrl)
            movie2TitleTextView.text = movie2.title
            movie2YearTextView.text = movie2.releaseYear
            movie2RatingTextView.text = movie2.voteAverageString
        }

        gameViewModel.score.observe(viewLifecycleOwner) {
            scoreTextView.text = it.toString()
        }

        // Triggering fetching of the data in the viewModel
        if (gameViewModel.moviePair.value == null) {
            fetchMovies()
        }

        val redBorder = ContextCompat.getDrawable(requireContext(), R.drawable.border_red)
        val yellowBorder = ContextCompat.getDrawable(requireContext(), R.drawable.border_yellow)
        val originalBackground = ContextCompat.getColor(requireContext(), R.color.RatingGuessr_Blue).toDrawable()
        val redColor = ContextCompat.getColor(requireContext(), R.color.RatingGuessr_Red)

        // Updates UI based on correct/incorrect answer
        gameViewModel.answerResult.observe(viewLifecycleOwner) { result ->
            val selectedMovie = gameViewModel.selectedMovie.value

            when (result) {
                is GameViewModel.AnswerResult.Correct -> {
                    enableButton(nextButton)
                    when (selectedMovie) {
                        1 -> movie2RatingTextView.setTextColor(redColor)
                        2 -> movie1RatingTextView.setTextColor(redColor)
                    }
                }
                is GameViewModel.AnswerResult.Incorrect -> {
                        lifecycleScope.launch {
                            when (selectedMovie) {
                                1 -> {
                                    movie1RatingTextView.setTextColor(redColor)
                                    movie1ImageButton.background = redBorder
                                }
                                2 -> {
                                    movie2RatingTextView.setTextColor(redColor)
                                    movie2ImageButton.background = redBorder
                                }
                            }
                            // Delay set to allow the user to see ratings before the game over pop-up appears
                            delay(2000L)
                            gameViewModel.triggerGameOver()
                        }
                        disableButton(nextButton)
                }
                null -> {}
            }
        }

        gameViewModel.gameOver.observe(viewLifecycleOwner) { gameOver ->
            if (gameOver == true && gameViewModel.shouldNavigateToGameOver()) {
                findNavController().navigate(R.id.action_gameFragment_to_gameOverPopUp)
            }
        }

        // Initially, set Answer and Next buttons to be unavailable
        disableButton(answerButton)
        disableButton(nextButton)

        // Add border to chosen movie and make "Answer" button clickable
        movie1ImageButton.setOnClickListener {
            gameViewModel.setSelectedMovie(1)

            if (movie1ImageButton.background == yellowBorder) {
                movie1ImageButton.background = originalBackground
            } else {
                movie1ImageButton.background = yellowBorder
                movie2ImageButton.background = originalBackground
            }

            enableButton(answerButton)
        }

        // Add border to chosen movie and make "Answer" button clickable
        movie2ImageButton.setOnClickListener {
            gameViewModel.setSelectedMovie(2)

            if (movie2ImageButton.background == yellowBorder) {
                movie2ImageButton.background = originalBackground
            } else {
                movie2ImageButton.background = yellowBorder
                movie1ImageButton.background = originalBackground
            }

            enableButton(answerButton)
        }

        timeBar.post {
            timeBar.pivotX = 0f  // Left edge of the view
            timeBar.pivotY = timeBar.height / 2f  // Center vertically

            gameViewModel.startTimer(timeBar)  // start the timer *after* pivot is correctly set
        }

        answerButton.setOnClickListener {
            // Stop the timer when Answer is pressed
            gameViewModel.stopTimer()

            // Get the current progress (remaining time) and update the timeBar
            gameViewModel.getTimerProgress(timeBar)

            // Sets AnswerResult to Correct or Incorrect in ViewModel. Updates UI through observing this value.
            gameViewModel.evaluateAnswer()

            // Make ratings visible:
            movie1RatingTextView.visibility = View.VISIBLE
            movie2RatingTextView.visibility = View.VISIBLE
            movie1ImageButton.isClickable = false
            movie2ImageButton.isClickable = false

            // Disabled to only allow answering once
            disableButton(answerButton)
        }

        nextButton.setOnClickListener {
            // Reset the timer and start again
            gameViewModel.startTimer(timeBar)

            // Preparing for next movie.
            resetUI(movie1RatingTextView, movie2RatingTextView, movie1ImageButton, movie2ImageButton)

            // Disable buttons
            disableButton(nextButton)
            disableButton(answerButton)

            // Fetch the next movies to display:
            fetchMovies()

            // Reset answer-value for next movie-pair:
            gameViewModel.resetAnswer()
        }
    }

    private fun fetchMovies() {
        gameViewModel.loadTwoDistinctMovies()
    }

    private fun disableButton(button: Button) {
        button.alpha = 0.5f // 50% transparency
        button.isClickable = false
    }

    private fun enableButton(button: Button) {
        button.alpha = 1f // 100% opacity
        button.isClickable = true
    }

    private fun resetUI(movie1Rating:TextView, movie2Rating: TextView, movie1Button: ImageButton, movie2Button: ImageButton) {
        movie1Rating.visibility = View.INVISIBLE
        movie2Rating.visibility = View.INVISIBLE
        movie1Rating.setTextColor(ContextCompat.getColor(requireContext(), R.color.RatingGuessr_Yellow))
        movie2Rating.setTextColor(ContextCompat.getColor(requireContext(), R.color.RatingGuessr_Yellow))
        movie1Button.isClickable = true
        movie2Button.isClickable = true
        movie1Button.background = ContextCompat.getColor(requireContext(), R.color.RatingGuessr_Blue).toDrawable()
        movie2Button.background = ContextCompat.getColor(requireContext(), R.color.RatingGuessr_Blue).toDrawable()
    }
}