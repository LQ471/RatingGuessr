package com.example.ratingguessr

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import androidx.fragment.app.Fragment

class IntroFragment : Fragment() {

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_intro, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val playButton = view.findViewById<Button>(R.id.play_button)
        val scoreButton = view.findViewById<Button>(R.id.highscores_button)

        playButton.setOnClickListener {
            (activity as? GameActivity)?.navigateToGameFragment()
        }

        scoreButton.setOnClickListener {
            (activity as? GameActivity)?.navigateToScoreFragment()
        }
    }
}
