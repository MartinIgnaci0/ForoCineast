package com.example.forocineast.data.remote

import okhttp3.OkHttpClient
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

object ExternalRetrofitInstance {
    private const val BASE_URL = "https://api.themoviedb.org/3/"

    // TU API KEY (Corta, 32 caracteres)
    private const val API_KEY = "c3568d65b0fc1de2bee609a24087cabe"

    // Cliente HTTP que añade la API KEY como parámetro (?api_key=...) a todas las peticiones
    private val client = OkHttpClient.Builder()
        .addInterceptor { chain ->
            val original = chain.request()
            val originalHttpUrl = original.url

            // Agregamos ?api_key=TU_CLAVE a la URL
            val url = originalHttpUrl.newBuilder()
                .addQueryParameter("api_key", API_KEY)
                .build()

            val request = original.newBuilder()
                .url(url)
                .build()
            chain.proceed(request)
        }
        .build()

    val api: ExternalApiService by lazy {
        Retrofit.Builder()
            .baseUrl(BASE_URL)
            .client(client)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
            .create(ExternalApiService::class.java)
    }
}