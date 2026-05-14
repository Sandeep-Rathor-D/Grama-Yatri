package com.mindmatrix.gramayatri.data.repository

import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.ValueEventListener
import com.google.firebase.database.ktx.database
import com.google.firebase.ktx.Firebase
import com.mindmatrix.gramayatri.data.model.BusRoute
import com.mindmatrix.gramayatri.data.model.CommunityAlert
import com.mindmatrix.gramayatri.data.model.PingEvent
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow

class FirebaseRepository {

    // ← ADD YOUR EXACT DATABASE URL HERE
    private val db = Firebase.database(
        "https://gramayatri-ba175-default-rtdb.asia-southeast1.firebasedatabase.app"
    ).reference

    fun getRoutes(): Flow<List<BusRoute>> = callbackFlow {
        val listener = object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                val routes = snapshot.children.mapNotNull {
                    it.getValue(BusRoute::class.java)
                }
                trySend(routes)
            }
            override fun onCancelled(error: DatabaseError) {
                close(error.toException())
            }
        }
        db.child("routes").addValueEventListener(listener)
        awaitClose { db.child("routes").removeEventListener(listener) }
    }

    fun getPingsForRoute(routeId: String): Flow<List<PingEvent>> = callbackFlow {
        val ref = db.child("pings").child(routeId)
        val listener = object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                val pings = snapshot.children.mapNotNull {
                    it.getValue(PingEvent::class.java)
                }.sortedByDescending { it.timestamp }
                trySend(pings)
            }
            override fun onCancelled(error: DatabaseError) {
                close(error.toException())
            }
        }
        ref.addValueEventListener(listener)
        awaitClose { ref.removeEventListener(listener) }
    }

    fun getAlertsForRoute(routeId: String): Flow<List<CommunityAlert>> = callbackFlow {
        val ref = db.child("alerts").child(routeId)
        val listener = object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                val alerts = snapshot.children.mapNotNull {
                    it.getValue(CommunityAlert::class.java)
                }.sortedByDescending { it.timestamp }
                trySend(alerts)
            }
            override fun onCancelled(error: DatabaseError) {
                close(error.toException())
            }
        }
        ref.addValueEventListener(listener)
        awaitClose { ref.removeEventListener(listener) }
    }

    suspend fun postPing(ping: PingEvent) {
        val key = db.child("pings").child(ping.routeId).push().key ?: return
        val pingWithId = ping.copy(id = key)
        db.child("pings").child(ping.routeId).child(key).setValue(pingWithId)
    }

    suspend fun postAlert(alert: CommunityAlert) {
        val key = db.child("alerts").child(alert.routeId).push().key ?: return
        val alertWithId = alert.copy(id = key)
        db.child("alerts").child(alert.routeId).child(key).setValue(alertWithId)
    }
}