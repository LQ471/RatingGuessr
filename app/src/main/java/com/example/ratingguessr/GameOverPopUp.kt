package com.example.ratingguessr

import android.os.Bundle
import androidx.fragment.app.DialogFragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.TextView
import androidx.navigation.findNavController
import androidx.navigation.fragment.findNavController

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
        return inflater.inflate(R.layout.fragment_game_over_pop_up, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val scoreTextView = view.findViewById<TextView>(R.id.SessionScore)
        scoreTextView.text = score

        val playAgainButton = view.findViewById<Button>(R.id.PlayAgainButton)
        val frontPageButton = view.findViewById<Button>(R.id.FrontPageButton)

        playAgainButton.setOnClickListener {
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