package com.example.ratingguessr

import android.content.Intent
import android.graphics.Typeface
import android.os.Bundle
import android.view.Gravity
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.LinearLayout
import android.widget.TextView
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.findNavController

class HighScoreListFragment : Fragment() {

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        return inflater.inflate(R.layout.fragment_scorelist, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val scoreListLayout = view.findViewById<LinearLayout>(R.id.highScoreList)

        val viewModel = ViewModelProvider(requireActivity())[GameViewModel::class.java]
        val db = SimpleSQL(requireContext())
        val scores = db.getTopScores()


        scores.forEachIndexed { index, score ->
            val scoreView = TextView(requireContext()).apply {
                text = "${index + 1}. ${score.name}: ${String.format("%.2f", score.score)}"
                textSize = 20f
                setTextColor(ContextCompat.getColor(requireContext(), R.color.RatingGuessr_Yellow))
                gravity = Gravity.CENTER
                setTypeface(typeface, Typeface.BOLD)

                val params = LinearLayout.LayoutParams(
                    LinearLayout.LayoutParams.MATCH_PARENT,
                    LinearLayout.LayoutParams.WRAP_CONTENT
                )
                params.setMargins(0, 0, 0, 16) // left, top, right, bottom
                layoutParams = params
            }
            scoreListLayout.addView(scoreView)
        }

        val playAgainButton = view.findViewById<Button>(R.id.PlayAgainButton)
        val homepageButton = view.findViewById<Button>(R.id.ScoresToIntroButton)


        playAgainButton.setOnClickListener {
            //(activity as? GameActivity)?.navigateToGameFragment()
            viewModel.resetGame()
            view.findNavController().navigate(R.id.action_highScoreListFragment_to_gameFragment)
        }

        homepageButton.setOnClickListener {
            view.findNavController().navigate(R.id.action_highScoreListFragment_to_introFragment)
        }
    }
}

