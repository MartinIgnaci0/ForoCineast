package com.example.forocineast.model

import com.google.gson.annotations.SerializedName

data class PeliculaResponse(
    @SerializedName("results") val resultados: List<Pelicula>?
)