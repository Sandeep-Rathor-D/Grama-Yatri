package com.mindmatrix.gramayatri.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavHostController
import com.mindmatrix.gramayatri.ui.theme.*
import com.mindmatrix.gramayatri.viewmodel.RegistrationViewModel

@Composable
fun RegistrationScreen(
    navController : NavHostController,
    vm            : RegistrationViewModel = viewModel()
) {
    var nameInput    by remember { mutableStateOf("") }
    val focusManager  = LocalFocusManager.current
    val isValid       = nameInput.trim().length >= 2

    fun proceed() {
        if (isValid) {
            vm.saveDisplayName(nameInput.trim())
            navController.navigate(Screen.RouteSelection.route) {
                popUpTo(Screen.Registration.route) { inclusive = true }
            }
        }
    }

    Column(
        modifier              = Modifier
            .fillMaxSize()
            .background(GramaWhite)
            .padding(32.dp),
        verticalArrangement   = Arrangement.Center,
        horizontalAlignment   = Alignment.CenterHorizontally
    ) {
        Surface(
            shape    = RoundedCornerShape(24.dp),
            color    = GramaGreen,
            modifier = Modifier.size(90.dp)
        ) {
            Box(contentAlignment = Alignment.Center) {
                Text(text = "🚌", fontSize = 42.sp)
            }
        }

        Spacer(modifier = Modifier.height(24.dp))

        Text(
            text       = "Grama-Yatri",
            fontSize   = 28.sp,
            fontWeight = FontWeight.Bold,
            color      = GramaGreen
        )

        Text(
            text      = "Community Bus Tracker",
            fontSize  = 14.sp,
            color     = GramaGrey,
            textAlign = TextAlign.Center
        )

        Spacer(modifier = Modifier.height(48.dp))

        Text(
            text       = "Enter your name to get started",
            fontSize   = 16.sp,
            fontWeight = FontWeight.Medium,
            color      = GramaBlack
        )

        Spacer(modifier = Modifier.height(16.dp))

        OutlinedTextField(
            value         = nameInput,
            onValueChange = { nameInput = it },
            label         = { Text("Your Name  e.g. Ravi") },
            singleLine    = true,
            isError       = nameInput.isNotEmpty() && !isValid,
            modifier      = Modifier.fillMaxWidth(),
            keyboardOptions = KeyboardOptions(imeAction = ImeAction.Done),
            keyboardActions = KeyboardActions(onDone = {
                focusManager.clearFocus()
                proceed()
            }),
            colors = OutlinedTextFieldDefaults.colors(
                focusedBorderColor = GramaGreen,
                focusedLabelColor  = GramaGreen,
                cursorColor        = GramaGreen
            )
        )

        if (nameInput.isNotEmpty() && !isValid) {
            Text(
                text     = "Name must be at least 2 characters",
                color    = MaterialTheme.colorScheme.error,
                fontSize = 12.sp,
                modifier = Modifier.padding(top = 4.dp)
            )
        }

        Spacer(modifier = Modifier.height(32.dp))

        Button(
            onClick  = { proceed() },
            enabled  = isValid,
            modifier = Modifier
                .fillMaxWidth()
                .height(52.dp),
            shape  = RoundedCornerShape(12.dp),
            colors = ButtonDefaults.buttonColors(
                containerColor         = GramaGreen,
                disabledContainerColor = GramaGrey.copy(alpha = 0.4f)
            )
        ) {
            Text(
                text     = "Continue  →",
                fontSize = 16.sp,
                color    = GramaWhite
            )
        }

        Spacer(modifier = Modifier.height(24.dp))

        Text(
            text      = "No email or password needed.\nYour name is saved only on this device.",
            fontSize  = 12.sp,
            color     = GramaGrey,
            textAlign = TextAlign.Center
        )
    }
}