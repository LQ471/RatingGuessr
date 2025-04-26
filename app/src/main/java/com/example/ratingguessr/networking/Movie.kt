package com.example.ratingguessr.networking

import com.squareup.moshi.Json

// Data class representing a single movie object returned by the TMDB API
// We only keep relevant data: title, movie poster, date of release and the rating (vote_average).
data class Movie(
    @Json(name="title")
    val title: String,
    @Json(name="poster_path") // Base path: https://image.tmdb.org/t/p/w500/
    val posterPath: String,
    @Json(name="release_date")
    val releaseDate: String,
    @Json(name="vote_average")
    val voteAverage: Float
) {
    // URL is completed by adding the base path before the retrieved URL-ending.
    val fullPosterUrl: String
        get() = posterPath.let { "https://image.tmdb.org/t/p/w500$it" }
    // Release year is extracted from release_date.
    val releaseYear: String
        get() = releaseDate.let { releaseDate.substring(0, 4) }
}

data class MovieResponse(@Json(name="results")
val results : List<Movie>)