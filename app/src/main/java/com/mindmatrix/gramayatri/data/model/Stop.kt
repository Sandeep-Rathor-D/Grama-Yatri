package com.mindmatrix.gramayatri.data.model

data class Stop(
    val id: String = "",
    val name: String = "",
    val order: Int = 0,
    val avgMinutesFromPrevious: Int = 0
)