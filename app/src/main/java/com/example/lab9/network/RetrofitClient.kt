package com.example.lab9.network

import com.example.lab9.models.Post
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.http.GET
import retrofit2.http.Path
import retrofit2.http.Query

interface ApiService {
    // Отримуємо всі пости без обмеження
    @GET("posts")
    suspend fun getPosts(): Response<List<Post>>  // Змінено: без параметра _limit

    @GET("posts/{id}")
    suspend fun getPost(@Path("id") postId: Int): Response<Post>
}


object RetrofitClient {
    private const val BASE_URL = "https://jsonplaceholder.typicode.com/"

    val instance: ApiService by lazy {
        Retrofit.Builder()
            .baseUrl(BASE_URL)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
            .create(ApiService::class.java)
    }
}
