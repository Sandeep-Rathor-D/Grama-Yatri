package com.mindmatrix.gramayatri.ui.screens

import androidx.compose.runtime.Composable
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.navArgument

sealed class Screen(val route: String) {
    object Registration   : Screen("registration")
    object RouteSelection : Screen("route_selection")
    object RouteView      : Screen("route_view/{routeId}/{routeName}") {
        fun createRoute(routeId: String, routeName: String) =
            "route_view/$routeId/${routeName.replace("/", "|")}"
    }
}

@Composable
fun GramaYatriNavGraph(
    navController  : NavHostController,
    hasDisplayName : Boolean
) {
    NavHost(
        navController    = navController,
        startDestination = if (hasDisplayName)
            Screen.RouteSelection.route
        else
            Screen.Registration.route
    ) {
        composable(Screen.Registration.route) {
            RegistrationScreen(navController = navController)
        }
        composable(Screen.RouteSelection.route) {
            RouteSelectionScreen(navController = navController)
        }
        composable(
            route     = Screen.RouteView.route,
            arguments = listOf(
                navArgument("routeId")   { type = NavType.StringType },
                navArgument("routeName") { type = NavType.StringType }
            )
        ) { backStackEntry ->
            val routeId   = backStackEntry.arguments?.getString("routeId")   ?: ""
            val routeName = backStackEntry.arguments?.getString("routeName")  ?: ""
            RouteViewScreen(
                routeId   = routeId,
                routeName = routeName.replace("|", "/"),
                onBack    = { navController.popBackStack() }
            )
        }
    }
}