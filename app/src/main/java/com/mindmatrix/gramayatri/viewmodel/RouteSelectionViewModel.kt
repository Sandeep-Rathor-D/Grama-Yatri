package com.mindmatrix.gramayatri.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.mindmatrix.gramayatri.data.model.BusRoute
import com.mindmatrix.gramayatri.data.repository.FirebaseRepository
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.stateIn

class RouteSelectionViewModel : ViewModel() {

    private val firebaseRepo = FirebaseRepository()

    val routes: StateFlow<List<BusRoute>> = firebaseRepo.getRoutes()
        .stateIn(
            scope        = viewModelScope,
            started      = SharingStarted.WhileSubscribed(5_000),
            initialValue = emptyList()
        )
}