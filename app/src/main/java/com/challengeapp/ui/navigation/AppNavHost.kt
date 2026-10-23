package com.challengeapp.ui.navigation

import androidx.compose.runtime.Composable
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.toRoute
import com.challengeapp.ui.form.FormScreen
import com.challengeapp.ui.image.ImageDetailScreen
import kotlinx.serialization.Serializable

@Serializable
object FormRoute

@Serializable
data class ImageRoute(val title: String, val src: String)

@Composable
fun AppNavHost() {
    val navController = rememberNavController()
    NavHost(navController = navController, startDestination = FormRoute) {
        composable<FormRoute> {
            FormScreen(onImageClick = { navController.navigate(ImageRoute(it.title, it.src)) })
        }
        composable<ImageRoute> { entry ->
            val route = entry.toRoute<ImageRoute>()
            ImageDetailScreen(title = route.title, src = route.src, onBack = navController::popBackStack)
        }
    }
}
