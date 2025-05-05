package com.example.ratingguessr

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModelProvider

class GameActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.gamemain)

        val viewModel = ViewModelProvider(this).get(GameViewModel::class.java)

        val fragment = HighScoreListFragment()

        val gameFragment = GameFragment()
    }
}