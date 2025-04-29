package com.example.ratingguessr

import android.os.Bundle
import android.os.CountDownTimer
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.TextView

private var score = 0
private var timer: CountDownTimer? = null
private val totalTime = 10000L // 10 seconds
private val interval = 30L // update every 100ms
private var remainingTime: Long = totalTime

/**
 * A simple [Fragment] subclass.
 * Use the [GameFragment.newInstance] factory method to
 * create an instance of this fragment.
 */
class GameFragment : Fragment() {

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_game, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val answerButton = view.findViewById<Button>(R.id.Answer)
        val scoreTextView = view.findViewById<TextView>(R.id.SessionScore)
        val nextButton = view.findViewById<Button>(R.id.NextButton)
        val timeBar = view.findViewById<View>(R.id.timeBar)
        val exitButton = view.findViewById<View>(R.id.ExitButton)

        exitButton.setOnClickListener {
            (activity as? GameActivity)?.navigateToIntroFragment()
        }

        // Initially, set Next button to be unavailable
        nextButton.alpha = 0.5f // 50% transparency
        nextButton.isClickable = false // Make it unclickable

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
            score += 1
            scoreTextView.text = score.toString()

            answerButton.alpha = 0.5f
            // Enable the Next button after answering
            nextButton.alpha = 1f // 100% opacity
            nextButton.isClickable = true // Make it clickable
        }

        nextButton.setOnClickListener {
            // Reset the timer and start again
            startTimer(timeBar)

            // Optionally, load the next question here if needed

            answerButton.alpha = 1f
            // Disable the Next button again after it's pressed
            nextButton.alpha = 0.5f // 50% transparency
            nextButton.isClickable = false // Make it unclickable
        }
    }

    private fun showGameOverPopUp() {
        val score = score.toString() // Get the score to pass to the pop-up fragment

        // Create a new instance of GameOverPopUp and pass the score
        val gameOverFragment = GameOverPopUp.newInstance(score)

        // Show the DialogFragment
        gameOverFragment.show(parentFragmentManager, "gameOverPopUp")
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
}