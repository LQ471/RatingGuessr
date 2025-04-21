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

import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView


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
        val scores = viewModel.highScores

        scores.forEachIndexed { index, score ->
            val scoreView = TextView(requireContext()).apply {
                text = "${index + 1}. $score"
                textSize = 16f
                setPadding(8, 4, 8, 4)
            }
            scoreListLayout.addView(scoreView)
        }

        view.findViewById<Button>(R.id.PlayAgainButton)?.setOnClickListener {
            val intent = Intent(requireContext(), IntroActivity::class.java)
            startActivity(intent)
        }
    }
}

