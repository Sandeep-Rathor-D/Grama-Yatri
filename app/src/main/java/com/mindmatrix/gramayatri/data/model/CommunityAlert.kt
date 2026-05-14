package com.mindmatrix.gramayatri.data.model

data class CommunityAlert(
    val id: String = "",
    val routeId: String = "",
    val reporterName: String = "",
    val message: String = "",
    val timestamp: Long = 0L
)