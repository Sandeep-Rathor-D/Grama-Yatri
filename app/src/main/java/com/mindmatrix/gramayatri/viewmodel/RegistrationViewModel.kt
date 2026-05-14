package com.mindmatrix.gramayatri.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.mindmatrix.gramayatri.data.local.UserPreferencesRepository
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

class RegistrationViewModel(application: Application) : AndroidViewModel(application) {

    private val userPrefsRepo = UserPreferencesRepository(application.applicationContext)

    val displayName: StateFlow<String> = userPrefsRepo.displayName
        .stateIn(
            scope        = viewModelScope,
            started      = SharingStarted.WhileSubscribed(5_000),
            initialValue = ""
        )

    fun saveDisplayName(name: String) {
        viewModelScope.launch {
            userPrefsRepo.saveDisplayName(name.trim())
        }
    }
}