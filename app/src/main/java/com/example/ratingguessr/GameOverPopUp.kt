package com.example.ratingguessr

import android.content.Context
import android.os.Bundle
import androidx.fragment.app.DialogFragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import androidx.core.content.edit

class GameOverPopUp : DialogFragment() {

    private lateinit var gameViewModel: GameViewModel

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_game_over_pop_up, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        gameViewModel = ViewModelProvider(requireActivity())[GameViewModel::class.java]
        val scoreTextView = view.findViewById<TextView>(R.id.SessionScore)
        scoreTextView.text = gameViewModel.getFormattedScore()

        val playAgainButton = view.findViewById<Button>(R.id.PlayAgainButton)
        val frontPageButton = view.findViewById<Button>(R.id.FrontPageButton)
        val playerNameInput = view.findViewById<EditText>(R.id.playerNameInput)
        val submitScoreButton = view.findViewById<Button>(R.id.submitScoreButton)
        val savedNameText = view.findViewById<TextView>(R.id.savedNameText)
        val savedNameContainer = view.findViewById<View>(R.id.savedNameContainer)
        val editNameButton = view.findViewById<Button>(R.id.editNameButton)

        // if a name has been added to sharedPreferences (which will always be most recent), dont prompt for name input
        val prefs = requireContext().getSharedPreferences("player_prefs", Context.MODE_PRIVATE)
        val recentName = prefs.getString("last_player_name", null)

        // sets visibility of views depending on if theres is a saved recent name or not:
        if (!recentName.isNullOrEmpty()) {
            savedNameContainer.visibility = View.VISIBLE
            playerNameInput.visibility = View.GONE
            savedNameText.text = recentName
        } else {
            savedNameContainer.visibility = View.GONE
            playerNameInput.visibility = View.VISIBLE
        }

        // edit name button functionality:
        editNameButton.setOnClickListener {
            savedNameContainer.visibility = View.GONE
            playerNameInput.visibility = View.VISIBLE
            playerNameInput.setText(recentName ?: "")
        }

        playAgainButton.setOnClickListener {
            gameViewModel.resetGame()
            findNavController().navigate(R.id.action_gameOverPopUp_to_gameFragment)
        }

        frontPageButton.setOnClickListener {
            findNavController().navigate(R.id.action_gameOverPopUp_to_introFragment)
        }

        // Submits score and prompts for name if none exists in shared preferences
        submitScoreButton.setOnClickListener {
            val enteredName = playerNameInput.text.toString().trim()
            val savedName = prefs.getString("last_player_name", "")?.trim()
            val playerName = if (enteredName.isNotEmpty()) enteredName else savedName ?: ""
            val score = gameViewModel.score.value ?: 0f

            if (playerName.isNotEmpty()) {
                prefs.edit {
                    putString("last_player_name", playerName)
                }

                val dbHelper = SimpleSQL(requireContext())

                dbHelper.insertScore(playerName, score)
                findNavController().navigate(R.id.action_gameOverPopUp_to_highScoreListFragment)
                } else {
                    playerNameInput.error = "Please enter your name"
                }
        }
    }

    override fun onStart() {
        super.onStart()
        dialog?.window?.setLayout(
            ViewGroup.LayoutParams.MATCH_PARENT,
            ViewGroup.LayoutParams.WRAP_CONTENT
        )
        dialog?.window?.setBackgroundDrawableResource(android.R.color.transparent)
        dialog?.setCanceledOnTouchOutside(false)
        dialog?.setCancelable(false)
    }

    companion object {
        fun newInstance(score: String) = GameOverPopUp().apply {
            arguments = Bundle().apply {
                putString("score", score)
            }
        }
    }
}