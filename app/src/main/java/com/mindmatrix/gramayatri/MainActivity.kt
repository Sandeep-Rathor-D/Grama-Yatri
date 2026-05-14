package com.mindmatrix.gramayatri

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.compose.rememberNavController
import com.mindmatrix.gramayatri.ui.screens.GramaYatriNavGraph
import com.mindmatrix.gramayatri.ui.theme.GramaYatriTheme
import com.mindmatrix.gramayatri.viewmodel.RegistrationViewModel

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            GramaYatriTheme {
                val registrationVm: RegistrationViewModel = viewModel()
                val displayName by registrationVm.displayName.collectAsState()
                val navController = rememberNavController()

                GramaYatriNavGraph(
                    navController  = navController,
                    hasDisplayName = displayName.isNotEmpty()
                )
            }
        }
    }
}