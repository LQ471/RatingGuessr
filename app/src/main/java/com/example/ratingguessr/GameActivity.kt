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

        /** supportFragmentManager.beginTransaction()
            .replace(R.id.main_fragment, fragment)
            .addToBackStack(null)
            .commit() **/

        val gameFragment = GameFragment()

        supportFragmentManager.beginTransaction()
            .replace(R.id.main_fragment, gameFragment)
            .addToBackStack(null)
            .commit()
    }
}