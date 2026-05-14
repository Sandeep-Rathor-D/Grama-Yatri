package com.mindmatrix.gramayatri.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.google.firebase.ktx.Firebase
import com.google.firebase.database.ktx.database
import com.mindmatrix.gramayatri.data.local.UserPreferencesRepository
import com.mindmatrix.gramayatri.data.model.BusRoute
import com.mindmatrix.gramayatri.data.model.CommunityAlert
import com.mindmatrix.gramayatri.data.model.PingEvent
import com.mindmatrix.gramayatri.data.model.Stop
import com.mindmatrix.gramayatri.data.repository.EtaCalculator
import com.mindmatrix.gramayatri.data.repository.FirebaseRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

data class RouteUiState(
    val routeName    : String               = "",
    val stops        : List<Stop>           = emptyList(),
    val latestPing   : PingEvent?           = null,
    val etaMap       : Map<String, String>  = emptyMap(),
    val activeAlerts : List<CommunityAlert> = emptyList(),
    val isLoading    : Boolean              = true,
    val errorMessage : String?              = null
)

class RouteViewModel(application: Application) : AndroidViewModel(application) {

    private val firebaseRepo  = FirebaseRepository()
    private val userPrefsRepo = UserPreferencesRepository(application.applicationContext)

    private val _uiState = MutableStateFlow(RouteUiState())
    val uiState: StateFlow<RouteUiState> = _uiState.asStateFlow()

    private val _selectedStopId = MutableStateFlow("")
    val selectedStopId: StateFlow<String> = _selectedStopId.asStateFlow()

    fun setSelectedStop(stopId: String) {
        _selectedStopId.value = stopId
    }

    fun loadRoute(route: BusRoute) {
        _uiState.update {
            it.copy(
                routeName = route.name,
                stops     = route.stops.sortedBy { s -> s.order },
                isLoading = false
            )
        }
        if (route.stops.isNotEmpty()) {
            _selectedStopId.value = route.stops.sortedBy { it.order }.first().id
        }
        viewModelScope.launch {
            launch {
                firebaseRepo.getPingsForRoute(route.id).collect { pings ->
                    val latestPing = pings.firstOrNull()
                    val newEtas    = EtaCalculator.calculateEtas(
                        _uiState.value.stops, latestPing
                    )
                    _uiState.update {
                        it.copy(latestPing = latestPing, etaMap = newEtas)
                    }
                }
            }
            launch {
                firebaseRepo.getAlertsForRoute(route.id).collect { alerts ->
                    _uiState.update { it.copy(activeAlerts = alerts) }
                }
            }
        }
    }

    fun postPing(pingType: String, routeId: String) {
        viewModelScope.launch {
            val displayName = userPrefsRepo.displayName.first()
            val stopId      = _selectedStopId.value.ifEmpty {
                _uiState.value.stops.firstOrNull()?.id ?: return@launch
            }
            val ping = PingEvent(
                routeId      = routeId,
                stopId       = stopId,
                reporterName = displayName.ifEmpty { "Anonymous" },
                pingType     = pingType,
                timestamp    = System.currentTimeMillis()
            )
            firebaseRepo.postPing(ping)
        }
    }

    fun postAlert(message: String, routeId: String) {
        viewModelScope.launch {
            val displayName = userPrefsRepo.displayName.first()
            val alert = CommunityAlert(
                routeId      = routeId,
                reporterName = displayName.ifEmpty { "Anonymous" },
                message      = message,
                timestamp    = System.currentTimeMillis()
            )
            val db = Firebase.database(
                "https://gramayatri-ba175-default-rtdb.asia-southeast1.firebasedatabase.app"
            ).reference
            val key = db.child("alerts").child(routeId).push().key ?: return@launch
            val alertWithId = alert.copy(id = key)
            db.child("alerts").child(routeId).child(key).setValue(alertWithId)
        }
    }
}