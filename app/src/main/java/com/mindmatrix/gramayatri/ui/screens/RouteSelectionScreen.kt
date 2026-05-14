package com.mindmatrix.gramayatri.ui.screens

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.ArrowForwardIos
import androidx.compose.material.icons.filled.DirectionsBus
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavHostController
import com.mindmatrix.gramayatri.data.model.BusRoute
import com.mindmatrix.gramayatri.ui.theme.*
import com.mindmatrix.gramayatri.viewmodel.RouteSelectionViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun RouteSelectionScreen(
    navController : NavHostController,
    vm            : RouteSelectionViewModel = viewModel()
) {
    val routes by vm.routes.collectAsState()

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        "Select Your Route",
                        color      = GramaWhite,
                        fontWeight = FontWeight.Bold
                    )
                },
                navigationIcon = {
                    IconButton(onClick = {
                        navController.navigate(Screen.Registration.route) {
                            popUpTo(Screen.RouteSelection.route) { inclusive = true }
                        }
                    }) {
                        Icon(
                            Icons.Default.ArrowBack,
                            contentDescription = "Back",
                            tint = GramaWhite
                        )
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = GramaGreen
                )
            )
        }
    ) { padding ->
        if (routes.isEmpty()) {
            Box(
                modifier         = Modifier.fillMaxSize().padding(padding),
                contentAlignment = Alignment.Center
            ) {
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    CircularProgressIndicator(color = GramaGreen)
                    Spacer(modifier = Modifier.height(16.dp))
                    Text("Loading routes...", color = GramaGrey, fontSize = 14.sp)
                }
            }
        } else {
            LazyColumn(
                modifier            = Modifier.fillMaxSize().padding(padding),
                contentPadding      = PaddingValues(16.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                item {
                    Text(
                        text     = "Available Bus Routes",
                        fontSize = 14.sp,
                        color    = GramaGrey,
                        modifier = Modifier.padding(bottom = 4.dp)
                    )
                }
                items(routes) { route ->
                    RouteCard(
                        route   = route,
                        onClick = {
                            navController.navigate(
                                Screen.RouteView.createRoute(route.id, route.name)
                            )
                        }
                    )
                }
            }
        }
    }
}

@Composable
fun RouteCard(route: BusRoute, onClick: () -> Unit) {
    Card(
        modifier  = Modifier.fillMaxWidth().clickable { onClick() },
        shape     = RoundedCornerShape(12.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
        border    = BorderStroke(1.dp, GramaGreenLight),
        colors    = CardDefaults.cardColors(containerColor = GramaWhite)
    ) {
        Row(
            modifier          = Modifier.padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Surface(
                shape    = RoundedCornerShape(8.dp),
                color    = GramaGreenPale,
                modifier = Modifier.size(48.dp)
            ) {
                Box(contentAlignment = Alignment.Center) {
                    Icon(
                        imageVector        = Icons.Default.DirectionsBus,
                        contentDescription = null,
                        tint               = GramaGreen,
                        modifier           = Modifier.size(28.dp)
                    )
                }
            }
            Spacer(modifier = Modifier.width(16.dp))
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text       = route.name,
                    fontSize   = 15.sp,
                    fontWeight = FontWeight.SemiBold,
                    color      = GramaBlack
                )
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text     = "${route.stops.size} stops",
                    fontSize = 13.sp,
                    color    = GramaGrey
                )
            }
            Icon(
                imageVector        = Icons.Default.ArrowForwardIos,
                contentDescription = null,
                tint               = GramaGrey,
                modifier           = Modifier.size(16.dp)
            )
        }
    }
}