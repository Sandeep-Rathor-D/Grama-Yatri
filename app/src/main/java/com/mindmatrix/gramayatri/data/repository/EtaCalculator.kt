package com.mindmatrix.gramayatri.data.repository

import com.mindmatrix.gramayatri.data.model.PingEvent
import com.mindmatrix.gramayatri.data.model.Stop

object EtaCalculator {

    fun calculateEtas(
        stops: List<Stop>,
        latestPing: PingEvent?
    ): Map<String, String> {

        if (latestPing == null || stops.isEmpty()) return emptyMap()

        val pingStopIndex = stops.indexOfFirst { it.id == latestPing.stopId }
        if (pingStopIndex == -1) return emptyMap()

        val etaMap = mutableMapOf<String, String>()
        etaMap[stops[pingStopIndex].id] = "Bus is here"

        var cumulativeMinutes = 0L

        for (i in (pingStopIndex + 1)..stops.lastIndex) {
            cumulativeMinutes += stops[i].avgMinutesFromPrevious.toLong()
            val etaMillis = latestPing.timestamp + (cumulativeMinutes * 60L * 1000L)
            val minutesFromNow = (etaMillis - System.currentTimeMillis()) / 60_000L

            etaMap[stops[i].id] = when {
                minutesFromNow < 0L  -> "Likely passed"
                minutesFromNow == 0L -> "Arriving now"
                minutesFromNow < 60L -> "~${minutesFromNow} min"
                else -> {
                    val h = minutesFromNow / 60L
                    val m = minutesFromNow % 60L
                    "~${h}h ${m}m"
                }
            }
        }
        return etaMap
    }

    fun formatRelativeTime(timestamp: Long): String {
        val diffMin = (System.currentTimeMillis() - timestamp) / 60_000L
        return when {
            diffMin < 1L  -> "just now"
            diffMin < 60L -> "${diffMin} min ago"
            else          -> "${diffMin / 60L}h ago"
        }
    }
}