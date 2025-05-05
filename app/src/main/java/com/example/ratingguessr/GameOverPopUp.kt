package com.example.ratingguessr

import android.os.Bundle
import androidx.fragment.app.DialogFragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.TextView
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController

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

        playAgainButton.setOnClickListener {
            gameViewModel.resetGame()
            findNavController().navigate(R.id.action_gameOverPopUp_to_gameFragment)
        }

        frontPageButton.setOnClickListener {
            findNavController().navigate(R.id.action_gameOverPopUp_to_introFragment)
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