package com.example.forocineast.model

import com.google.gson.annotations.SerializedName

/**
 * Representa una película individual traída de TMDB.
 */
data class Pelicula(
    @SerializedName("id") val idRemote: Int, // ID único en TMDB
    @SerializedName("title") val titulo: String,
    @SerializedName("overview") val sinopsis: String,
    @SerializedName("poster_path") val posterPath: String?,
    @SerializedName("backdrop_path") val bannerPath: String?,
    @SerializedName("vote_average") val calificacionPromedio: Double,
    @SerializedName("release_date") val fechaEstreno: String?
) {
    // Helper para obtener la URL completa del póster (formato vertical)
    fun getPosterUrl(): String {
        return if (!posterPath.isNullOrEmpty()) "https://image.tmdb.org/t/p/w500$posterPath" else ""
    }

    // Helper para obtener la URL completa del banner (formato horizontal)
    fun getBannerUrl(): String {
        return if (!bannerPath.isNullOrEmpty()) "https://image.tmdb.org/t/p/w780$bannerPath" else ""
    }
}