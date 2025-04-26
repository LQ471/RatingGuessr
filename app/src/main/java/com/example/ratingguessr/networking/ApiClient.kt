package com.example.ratingguessr.networking

import com.squareup.moshi.Moshi
import com.squareup.moshi.kotlin.reflect.KotlinJsonAdapterFactory
import okhttp3.OkHttpClient
import retrofit2.Call
import retrofit2.Retrofit
import retrofit2.converter.moshi.MoshiConverterFactory
import retrofit2.http.GET
import retrofit2.http.Query

object ApiClient {

    // Base URL for The Movie Database (TMDB) API
    private val BASE_URL = "https://api.themoviedb.org/3/"

    // Moshi is used to parse JSON responses into Kotlin objects
    private val moshi = Moshi.Builder().add(KotlinJsonAdapterFactory()).build()

    // TMDB API token used for authenticating requests
    val apiToken = "Bearer eyJhbGciOiJIUzI1NiJ9.eyJhdWQiOiJkOTg5YzE4MDA5YmQwMWJkOGYxZjFhMjA1MjJhMGIyNSIsIm5iZiI6MTc0NTIyNDU2Ny42NDksInN1YiI6IjY4MDYwMzc3Mjc2YmY2NGU0MWFhYmUyZSIsInNjb3BlcyI6WyJhcGlfcmVhZCJdLCJ2ZXJzaW9uIjoxfQ.-C7s0x0GhhDR1Sv-SRw1g63RhlnaEWjRW10uhV9yT04"

    // OkHttpClient with an interceptor to add authorization and content-type headers to every request
    val okHttpClient = OkHttpClient.Builder()
        .addInterceptor { chain ->
            val request = chain.request().newBuilder()
                .addHeader("Authorization", apiToken)
                .addHeader("accept", "application/json")
                .build()
            //Log.d("OkHttp", "Request URL: ${request.url}")
            chain.proceed(request)
        }
        .build()

    // Creating Retrofit instance
    private val retrofit: Retrofit by lazy {
        Retrofit.Builder()
            .baseUrl(BASE_URL)
            .client(okHttpClient)
            .addConverterFactory(MoshiConverterFactory.create(moshi))
            .build()
    }
    // Public API service interface implementation to be used by the rest of the app
    val apiService: ApiService by lazy {
        retrofit.create(ApiService::class.java)
    }
}

// The Api service interface defining how to query the API (which endpoint and which filters)
interface ApiService {
    @GET("discover/movie")
    fun discoverMovies(
        @Query("include_adult") includeAdult: Boolean = false,
        @Query("include_video") includeVideo: Boolean = false,
        @Query("language") language: String = "en-US",
        @Query("page") page: Int = 1, // Defaults to page 1. This will generally be overwritten by giving a page to the function
        @Query("vote_count.gte") voteCountGte: Int = 5000 // retrieves movies with > 5000 ratings to ensure some popularity
    ): Call<MovieResponse>
}