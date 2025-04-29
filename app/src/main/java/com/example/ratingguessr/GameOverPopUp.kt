package com.example.ratingguessr

import android.app.Dialog
import android.os.Bundle
import androidx.fragment.app.DialogFragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.TextView
import androidx.appcompat.app.AlertDialog

class GameOverPopUp : DialogFragment() {

    private var score: String? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
            score = it.getString("score")
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_game_over_pop_up, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val scoreTextView = view.findViewById<TextView>(R.id.SessionScore)
        scoreTextView.text = score  // Display the score passed from the GameFragment

        val playAgainButton = view.findViewById<Button>(R.id.PlayAgainButton)
        val frontPageButton = view.findViewById<Button>(R.id.FrontPageButton)

        // Play again button click listener
        playAgainButton.setOnClickListener {
            // Handle restart of the game
            restartGame()
        }

        // Front page button click listener
        frontPageButton.setOnClickListener {
            // Handle going back to the front page
            goToFrontPage()
        }
    }

    // Method to restart the game (replace this with actual restart logic)
    private fun restartGame() {
        dismiss()  // Dismiss the pop-up
        // You can restart the game here (e.g., reset score, restart timer, etc.)
    }

    // Method to navigate to the front page
    private fun goToFrontPage() {
        // Replace this with actual navigation logic to the front page
    }

    companion object {
        fun newInstance(score: String) = GameOverPopUp().apply {
            arguments = Bundle().apply {
                putString("score", score)
            }
        }
    }

    // Optionally, override onCreateDialog to customize the dialog appearance
    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        val builder = AlertDialog.Builder(requireActivity())
        builder.setView(requireActivity().layoutInflater.inflate(R.layout.fragment_game_over_pop_up, null))

        // Make the dialog non-cancelable (i.e., prevent closing when clicking on background)
        val dialog = builder.create()
        dialog.setCanceledOnTouchOutside(false) // This prevents the dialog from closing when background is clicked
        return dialog
    }
}