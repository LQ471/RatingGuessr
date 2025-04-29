package com.example.ratingguessr

import android.os.Bundle
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModelProvider

class GameActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.gamemain)

        if (savedInstanceState == null) {
            supportFragmentManager.beginTransaction()
                .replace(R.id.main_fragment, IntroFragment())
                .commit()
        }

        val viewModel = ViewModelProvider(this).get(GameViewModel::class.java)

        val fragment = HighScoreListFragment()

        /** supportFragmentManager.beginTransaction()
        .replace(R.id.main_fragment, fragment)
        .addToBackStack(null)
        .commit() **/

        val gameFragment = GameFragment()
    }

    /** fragment navigation functions
     *
     */

    fun navigateToGameFragment() {
        val gameFragment = GameFragment()

        supportFragmentManager.beginTransaction()
            .replace(R.id.main_fragment, gameFragment)
            .addToBackStack(null)
            .commit()
    }

    fun navigateToScoreFragment() {
        val scoreFragment = HighScoreListFragment()

        supportFragmentManager.beginTransaction()
            .replace(R.id.main_fragment, scoreFragment)
            .addToBackStack(null)
            .commit()
    }

    fun navigateToIntroFragment() {
        val introFragment = IntroFragment()

        supportFragmentManager.beginTransaction()
            .replace(R.id.main_fragment, introFragment)
            .addToBackStack(null)
            .commit()
    }
}