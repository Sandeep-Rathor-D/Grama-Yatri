package com.mindmatrix.gramayatri.ui.screens

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.google.firebase.database.ktx.database
import com.google.firebase.ktx.Firebase
import com.mindmatrix.gramayatri.data.model.BusRoute
import com.mindmatrix.gramayatri.data.model.CommunityAlert
import com.mindmatrix.gramayatri.data.model.PingEvent
import com.mindmatrix.gramayatri.data.model.Stop
import com.mindmatrix.gramayatri.data.repository.EtaCalculator
import com.mindmatrix.gramayatri.ui.theme.*
import com.mindmatrix.gramayatri.viewmodel.RouteViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun RouteViewScreen(
    routeId  : String,
    routeName: String,
    onBack   : () -> Unit,
    vm       : RouteViewModel = viewModel()
) {
    val uiState        by vm.uiState.collectAsState()
    val selectedStopId by vm.selectedStopId.collectAsState()
    var showAlertDialog by remember { mutableStateOf(false) }
    var alertText       by remember { mutableStateOf("") }

    LaunchedEffect(routeId) {
        val ref = Firebase.database(
            "https://gramayatri-ba175-default-rtdb.asia-southeast1.firebasedatabase.app"
        ).reference.child("routes").child(routeId)
        ref.get().addOnSuccessListener { snapshot ->
            val route = snapshot.getValue(BusRoute::class.java)
            if (route != null) vm.loadRoute(route)
        }
    }



    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Column {
                        Text(
                            text       = routeName,
                            color      = GramaWhite,
                            fontSize   = 16.sp,
                            fontWeight = FontWeight.Bold,
                            maxLines   = 1
                        )
                        Text(
                            text     = "${uiState.stops.size} stops",
                            color    = GramaWhite.copy(alpha = 0.8f),
                            fontSize = 12.sp
                        )
                    }
                },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(
                            Icons.Default.ArrowBack,
                            contentDescription = "Back",
                            tint               = GramaWhite
                        )
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = GramaGreen
                )
            )
        }
    ) { padding ->

        if (uiState.isLoading) {
            Box(
                modifier         = Modifier.fillMaxSize().padding(padding),
                contentAlignment = Alignment.Center
            ) {
                CircularProgressIndicator(color = GramaGreen)
            }
            return@Scaffold
        }

        LazyColumn(
            modifier       = Modifier.fillMaxSize().padding(padding),
            contentPadding = PaddingValues(bottom = 24.dp)
        ) {
            if (uiState.activeAlerts.isNotEmpty()) {
                item {
                    AlertBannerCard(alert = uiState.activeAlerts.first())
                }
            }

            item {
                StopSelectorRow(
                    stops          = uiState.stops,
                    selectedStopId = selectedStopId,
                    onStopSelected = { vm.setSelectedStop(it) }
                )
            }

            item {
                PingButtonsCard(
                    onBoardingPing = { vm.postPing(PingEvent.TYPE_ON_BUS, routeId) },
                    onPassedPing   = { vm.postPing(PingEvent.TYPE_PASSED_STOP, routeId) },
                    onPostAlert    = { showAlertDialog = true },
                    latestPing     = uiState.latestPing
                )
            }

            item {
                Text(
                    text       = "Route Timeline",
                    fontSize   = 14.sp,
                    color      = GramaGrey,
                    fontWeight = FontWeight.Medium,
                    modifier   = Modifier.padding(
                        horizontal = 16.dp,
                        vertical   = 8.dp
                    )
                )
            }

            itemsIndexed(uiState.stops) { index, stop ->
                StopTimelineItem(
                    stop               = stop,
                    eta                = uiState.etaMap[stop.id],
                    isLastStop         = index == uiState.stops.lastIndex,
                    isSelected         = stop.id == selectedStopId,
                    latestPingReporter = if (uiState.latestPing?.stopId == stop.id)
                        uiState.latestPing?.reporterName else null,
                    onClick            = { vm.setSelectedStop(stop.id) }
                )
            }
        }
    }

    if (showAlertDialog) {
        AlertDialog(
            onDismissRequest = { showAlertDialog = false },
            icon             = {
                Icon(Icons.Default.Warning, null, tint = GramaAmber)
            },
            title = { Text("Post Community Alert") },
            text  = {
                OutlinedTextField(
                    value         = alertText,
                    onValueChange = { alertText = it },
                    label         = { Text("e.g. Morning bus cancelled today") },
                    maxLines      = 3,
                    modifier      = Modifier.fillMaxWidth(),
                    colors        = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = GramaAmber,
                        focusedLabelColor  = GramaAmber
                    )
                )
            },
            confirmButton = {
                Button(
                    onClick = {
                        if (alertText.isNotBlank()) {
                            vm.postAlert(alertText.trim(), routeId)
                            alertText       = ""
                            showAlertDialog = false
                        }
                    },
                    colors = ButtonDefaults.buttonColors(
                        containerColor = GramaAmber
                    )
                ) {
                    Text("Post Alert", color = GramaWhite)
                }
            },
            dismissButton = {
                TextButton(onClick = { showAlertDialog = false }) {
                    Text("Cancel", color = GramaGrey)
                }
            }
        )
    }
}

@Composable
fun AlertBannerCard(alert: CommunityAlert) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 8.dp),
        shape  = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(containerColor = GramaAmber)
    ) {
        Row(
            modifier          = Modifier.padding(12.dp),
            verticalAlignment = Alignment.Top
        ) {
            Icon(
                imageVector        = Icons.Default.Warning,
                contentDescription = null,
                tint               = GramaWhite,
                modifier           = Modifier.size(20.dp)
            )
            Spacer(modifier = Modifier.width(8.dp))
            Column {
                Text(
                    text       = alert.message,
                    color      = GramaWhite,
                    fontSize   = 14.sp,
                    fontWeight = FontWeight.SemiBold
                )
                Spacer(modifier = Modifier.height(2.dp))
                Text(
                    text     = "By ${alert.reporterName} • " +
                            EtaCalculator.formatRelativeTime(alert.timestamp),
                    color    = GramaWhite.copy(alpha = 0.85f),
                    fontSize = 12.sp
                )
            }
        }
    }
}

@Composable
fun StopSelectorRow(
    stops          : List<Stop>,
    selectedStopId : String,
    onStopSelected : (String) -> Unit
) {
    var expanded by remember { mutableStateOf(false) }
    val selectedStop = stops.find { it.id == selectedStopId }

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 8.dp),
        shape  = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(containerColor = GramaGreenPale),
        border = BorderStroke(1.dp, GramaGreenLight)
    ) {
        Column(modifier = Modifier.padding(12.dp)) {
            Text(
                text       = "Your current stop",
                fontSize   = 12.sp,
                color      = GramaGrey,
                fontWeight = FontWeight.Medium
            )
            Spacer(modifier = Modifier.height(8.dp))
            OutlinedButton(
                onClick  = { expanded = true },
                modifier = Modifier.fillMaxWidth(),
                border   = BorderStroke(1.dp, GramaGreen),
                shape    = RoundedCornerShape(8.dp)
            ) {
                Icon(
                    Icons.Default.LocationOn,
                    null,
                    tint     = GramaGreen,
                    modifier = Modifier.size(16.dp)
                )
                Spacer(modifier = Modifier.width(8.dp))
                Text(
                    text     = selectedStop?.name ?: "Select stop",
                    color    = GramaGreen,
                    fontSize = 14.sp,
                    modifier = Modifier.weight(1f)
                )
                Icon(Icons.Default.ArrowDropDown, null, tint = GramaGreen)
            }
            DropdownMenu(
                expanded         = expanded,
                onDismissRequest = { expanded = false }
            ) {
                stops.forEach { stop ->
                    DropdownMenuItem(
                        text    = { Text(stop.name, fontSize = 14.sp) },
                        onClick = {
                            onStopSelected(stop.id)
                            expanded = false
                        }
                    )
                }
            }
        }
    }
}

@Composable
fun PingButtonsCard(
    onBoardingPing : () -> Unit,
    onPassedPing   : () -> Unit,
    onPostAlert    : () -> Unit,
    latestPing     : PingEvent?
) {
    Card(
        modifier  = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 4.dp),
        shape     = RoundedCornerShape(12.dp),
        colors    = CardDefaults.cardColors(containerColor = GramaWhite),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Text(
                text       = "Report Bus Location",
                fontSize   = 14.sp,
                fontWeight = FontWeight.SemiBold,
                color      = GramaGreen
            )
            if (latestPing != null) {
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text     = "Last report by ${latestPing.reporterName} • " +
                            EtaCalculator.formatRelativeTime(latestPing.timestamp),
                    fontSize = 12.sp,
                    color    = GramaGrey
                )
            }
            Spacer(modifier = Modifier.height(12.dp))
            Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                Button(
                    onClick  = onBoardingPing,
                    modifier = Modifier.weight(1f),
                    shape    = RoundedCornerShape(10.dp),
                    colors   = ButtonDefaults.buttonColors(
                        containerColor = GramaGreen
                    )
                ) {
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Text("🚌", fontSize = 18.sp)
                        Text(
                            text     = "I'm on Bus",
                            fontSize = 11.sp,
                            color    = GramaWhite
                        )
                    }
                }
                OutlinedButton(
                    onClick  = onPassedPing,
                    modifier = Modifier.weight(1f),
                    shape    = RoundedCornerShape(10.dp),
                    border   = BorderStroke(1.5.dp, GramaGreen)
                ) {
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Text("👁", fontSize = 18.sp)
                        Text(
                            text     = "Bus Passed Me",
                            fontSize = 11.sp,
                            color    = GramaGreen
                        )
                    }
                }
            }
            Spacer(modifier = Modifier.height(8.dp))
            OutlinedButton(
                onClick  = onPostAlert,
                modifier = Modifier.fillMaxWidth(),
                shape    = RoundedCornerShape(10.dp),
                border   = BorderStroke(1.5.dp, GramaAmber)
            ) {
                Icon(
                    Icons.Default.Warning,
                    null,
                    tint     = GramaAmber,
                    modifier = Modifier.size(16.dp)
                )
                Spacer(modifier = Modifier.width(8.dp))
                Text(
                    "Post Community Alert",
                    color    = GramaAmber,
                    fontSize = 13.sp
                )
            }
        }
    }
}

@Composable
fun StopTimelineItem(
    stop               : Stop,
    eta                : String?,
    isLastStop         : Boolean,
    isSelected         : Boolean,
    latestPingReporter : String?,
    onClick            : () -> Unit
) {
    val isBusHere = eta == "Bus is here"
    val hasEta    = eta != null
    val dotColor  = when {
        isBusHere -> GramaAmber
        hasEta    -> GramaGreen
        else      -> GramaGrey.copy(alpha = 0.4f)
    }

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onClick() }
            .background(
                if (isSelected) GramaGreenPale else Color.Transparent
            )
            .padding(horizontal = 16.dp)
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            modifier            = Modifier.width(28.dp)
        ) {
            Spacer(modifier = Modifier.height(14.dp))
            Box(
                modifier = Modifier
                    .size(if (isBusHere) 18.dp else 14.dp)
                    .clip(CircleShape)
                    .background(dotColor)
            )
            if (!isLastStop) {
                Box(
                    modifier = Modifier
                        .width(2.dp)
                        .height(56.dp)
                        .background(
                            if (hasEta) GramaGreenLight
                            else GramaGrey.copy(alpha = 0.3f)
                        )
                )
            }
        }

        Spacer(modifier = Modifier.width(16.dp))

        Column(
            modifier = Modifier
                .weight(1f)
                .padding(top = 8.dp, bottom = 8.dp)
        ) {
            Text(
                text       = stop.name,
                fontSize   = 15.sp,
                fontWeight = if (isBusHere || isSelected)
                    FontWeight.Bold else FontWeight.Normal,
                color      = if (isBusHere) GramaAmber else GramaBlack
            )
            if (eta != null) {
                Spacer(modifier = Modifier.height(2.dp))
                Surface(
                    shape = RoundedCornerShape(6.dp),
                    color = when {
                        isBusHere              -> GramaAmber.copy(alpha = 0.15f)
                        eta == "Likely passed" -> GramaGrey.copy(alpha = 0.1f)
                        else                   -> GramaGreenPale
                    }
                ) {
                    Text(
                        text       = eta,
                        fontSize   = 13.sp,
                        color      = when {
                            isBusHere              -> GramaAmber
                            eta == "Likely passed" -> GramaGrey
                            else                   -> GramaGreen
                        },
                        fontWeight = FontWeight.Medium,
                        modifier   = Modifier.padding(
                            horizontal = 8.dp,
                            vertical   = 3.dp
                        )
                    )
                }
            }
            if (latestPingReporter != null) {
                Spacer(modifier = Modifier.height(2.dp))
                Text(
                    text     = "📍 Reported by $latestPingReporter",
                    fontSize = 12.sp,
                    color    = GramaGrey
                )
            }
        }

        if (isBusHere) {
            Text(
                text     = "🚌",
                fontSize = 22.sp,
                modifier = Modifier.align(Alignment.CenterVertically)
            )
        }
    }
}