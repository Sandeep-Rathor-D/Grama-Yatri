package com.mindmatrix.gramayatri.data.model

data class PingEvent(
    val id: String = "",
    val routeId: String = "",
    val stopId: String = "",
    val reporterName: String = "",
    val pingType: String = "",
    val timestamp: Long = 0L
) {
    companion object {
        const val TYPE_ON_BUS = "ON_BUS"
        const val TYPE_PASSED_STOP = "PASSED_STOP"
    }
}