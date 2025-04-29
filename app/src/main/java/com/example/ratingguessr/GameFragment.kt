package com.example.ratingguessr

import android.os.Bundle
import android.os.CountDownTimer
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

private var timer: CountDownTimer? = null
private val totalTime = 10000L // 10 seconds
private val interval = 30L // update every 100ms
private var remainingTime: Long = totalTime
private var winningMovie = -1
private var selectedMovie = -1
private var hasNavigatedToGameOver = false

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
            // (activity as? GameActivity)?.navigateToIntroFragment()
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

            winningMovie = if (movie1.voteAverage > movie2.voteAverage) 1 else 2
        }

        gameViewModel.score.observe(viewLifecycleOwner) {
            scoreTextView.text = it.toString()
        }

        // Triggering fetching of the data in the viewModel
        fetchMovies()

        val yellowBorder = ContextCompat.getDrawable(requireContext(), R.drawable.border_yellow)
        val originalBackground = ContextCompat.getColor(requireContext(), R.color.RatingGuessr_Blue).toDrawable()

        // Initially, set Answer and Next buttons to be unavailable
        disableButton(answerButton)
        disableButton(nextButton)

        // Add border to chosen movie and make "Answer" button clickable
        movie1ImageButton.setOnClickListener {
            selectedMovie = 1

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
            selectedMovie = 2

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

            startTimer(timeBar)  // start the timer *after* pivot is correctly set
        }

        answerButton.setOnClickListener {
            // Stop the timer when Answer is pressed
            timer?.cancel() // Cancel the running timer

            // Get the current progress (remaining time) and update the timeBar
            val progress = (remainingTime.toFloat() / totalTime)
            timeBar.scaleX = progress // Adjust scale based on the time left

            // Increase score when Answer is pressed
            if (selectedMovie == winningMovie) {
                gameViewModel.addToScore()
                enableButton(nextButton)
            }
            else {
                lifecycleScope.launch {
                    val redColor =
                        ContextCompat.getColor(requireContext(), R.color.RatingGuessr_Red)
                    if (selectedMovie == 1) {
                        movie1RatingTextView.setTextColor(redColor)
                        movie1ImageButton.background =
                            ContextCompat.getDrawable(requireContext(), R.drawable.border_red)
                    } else if (selectedMovie == 2) {
                        movie2RatingTextView.setTextColor(redColor)
                        movie2ImageButton.background =
                            ContextCompat.getDrawable(requireContext(), R.drawable.border_red)
                    }

                    // Delay set to allow the user to see ratings before the game over pop-up appears
                    delay(2000L)
                    showGameOverPopUp()
                }
                disableButton(nextButton)
            }

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
            startTimer(timeBar)

            // Preparing for next movie.
            movie1RatingTextView.visibility = View.INVISIBLE
            movie2RatingTextView.visibility = View.INVISIBLE
            movie1ImageButton.isClickable = true
            movie2ImageButton.isClickable = true
            movie1ImageButton.background = originalBackground
            movie2ImageButton.background = originalBackground

            // Disable buttons
            disableButton(nextButton)
            disableButton(answerButton)

            // Fetch the next movies to display:
            fetchMovies()
        }
    }

    // Reset setup if fragment is destroyed
    override fun onDestroyView() {
        super.onDestroyView()
        gameViewModel.resetScore()
        hasNavigatedToGameOver = false
    }

    private fun showGameOverPopUp() {
        if (hasNavigatedToGameOver) return
        hasNavigatedToGameOver = true

        // Preventing the timer from triggering another call to showGameOverPopUp() when it runs out
        timer?.cancel()

        findNavController().navigate(R.id.action_gameFragment_to_gameOverPopUp)
    }

    private fun startTimer(timeBar: View) {
        timer?.cancel() // Cancel existing timer if any

        timer = object : CountDownTimer(totalTime, interval) {
            override fun onTick(millisUntilFinished: Long) {
                remainingTime = millisUntilFinished // Store the remaining time

                val progress = millisUntilFinished.toFloat() / totalTime
                timeBar.scaleX = progress // Shrink horizontally
            }

            override fun onFinish() {
                timeBar.scaleX = 0f
                showGameOverPopUp()
            }
        }.start()
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

}