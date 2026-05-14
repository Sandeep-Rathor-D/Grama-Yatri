package com.mindmatrix.gramayatri.data.model

data class BusRoute(
    val id: String = "",
    val name: String = "",
    val stops: List<Stop> = emptyList()
)