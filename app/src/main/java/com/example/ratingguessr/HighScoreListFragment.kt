package com.example.ratingguessr

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.LinearLayout
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider

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
                text = "${index + 1}. ${score.name}: ${score.score}"
                textSize = 16f
            }
            scoreListLayout.addView(scoreView)
        }

        val playAgainButton = view.findViewById<Button>(R.id.PlayAgainButton)
        val homepageButton = view.findViewById<Button>(R.id.ScoresToIntroButton)

        playAgainButton.setOnClickListener {
            (activity as? GameActivity)?.navigateToGameFragment()
        }

        homepageButton.setOnClickListener {
            (activity as? GameActivity)?.navigateToIntroFragment()
        }
    }
}

