package com.example.forocineast.model

import com.google.gson.annotations.SerializedName

data class Pelicula(
    @SerializedName("id") val idRemote: Int = 0,

    // --- CAMPOS PARA CINE ---
    @SerializedName("title") val title: String? = null,
    @SerializedName("release_date") val releaseDate: String? = null,

    // --- CAMPOS PARA TV (SERIES) ---
    @SerializedName("name") val name: String? = null,
    @SerializedName("first_air_date") val firstAirDate: String? = null,

    // --- CAMPOS COMUNES ---
    @SerializedName("overview") val sinopsis: String? = "Sin sinopsis disponible.",
    @SerializedName("poster_path") val posterPath: String? = null,
    @SerializedName("backdrop_path") val backdropPath: String? = null,
    @SerializedName("vote_average") val calificacionPromedio: Double = 0.0
) {
    // Propiedad computada para unificar Título (Cine o TV)
    val titulo: String
        get() = title ?: name ?: "Título Desconocido"

    // Propiedad computada para unificar Año
    val anio: String?
        get() = releaseDate ?: firstAirDate

    fun getPosterUrl(): String {
        val baseUrl = "https://image.tmdb.org/t/p/w500"
        return if (!posterPath.isNullOrEmpty()) {
            "$baseUrl$posterPath"
        } else {
            "https://via.placeholder.com/300x450.png?text=No+Poster"
        }
    }

    fun getBannerUrl(): String {
        val baseUrl = "https://image.tmdb.org/t/p/w780"
        return if (!backdropPath.isNullOrEmpty()) {
            "$baseUrl$backdropPath"
        } else {
            getPosterUrl()
        }
    }
}