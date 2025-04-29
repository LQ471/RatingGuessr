package com.example.ratingguessr

import android.util.Log
import com.example.ratingguessr.networking.ApiClient
import com.example.ratingguessr.networking.Movie
import com.example.ratingguessr.networking.MovieResponse
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import kotlin.math.log

class MovieRepository {

    fun getRandomPopularMovie(
        onSuccess: (Movie) -> Unit,
        onError: (Throwable) -> Unit
    ) {
        // Randomizing result as much as possible (the API call returns 48 pages in total).
        // Todo: Find solution to avoid hardcoding the amount of pages.
        val randomPage = (1..48).random()

        val client = ApiClient.apiService.discoverMovies(page = randomPage)
        client.enqueue(object : Callback<MovieResponse> {
            override fun onResponse(
                call: Call<MovieResponse>,
                response: Response<MovieResponse>
            ) {
                if (response.isSuccessful) {
                    val movies = response.body()?.results ?: emptyList()
                    if (movies.isNotEmpty()) {
                        val randomMovie = movies.random()
                        Log.d("", randomMovie.title + " " + randomMovie.fullPosterUrl)
                        onSuccess(randomMovie)
                    } else {
                        onError(Throwable("No movies found on page $randomPage"))
                    }
                } else {
                    onError(Throwable("API Error"))
                }
            }

            override fun onFailure(call: Call<MovieResponse>, e: Throwable) {
                onError(e)
            }
        })
    }
}
