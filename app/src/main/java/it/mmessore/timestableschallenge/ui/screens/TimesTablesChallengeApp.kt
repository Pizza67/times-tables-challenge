package it.mmessore.timestableschallenge.ui.screens

import android.widget.Toast
import androidx.activity.compose.BackHandler
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import it.mmessore.timestableschallenge.R

enum class AppScreen() {
    Home, Round, Summary
}

@Composable
fun TimeTablesChallengeApp(
    viewModel: RoundViewModel = viewModel(),
    navController: NavHostController = rememberNavController()
) {
    val context = LocalContext.current

    Scaffold (modifier = Modifier.fillMaxSize()) { innerPadding ->
        NavHost(
            navController = navController,
            startDestination = AppScreen.Home.name,
            modifier = Modifier.padding(innerPadding)
        ) {
            composable(route = AppScreen.Home.name) {
                HomeScreen(
                    onStartButtonClick = {
                        viewModel.resetRound()
                        navController.navigate(AppScreen.Round.name)
                })
            }

            composable(route = AppScreen.Round.name) {
                BackHandler {
                    Toast.makeText(context, context.getString(R.string.back_not_allowed), Toast.LENGTH_SHORT).show()
                }
                RoundScreen(
                    viewModel = viewModel,
                    onRoundFinished = {
                        navController.navigate(AppScreen.Summary.name) {
                            popUpTo(AppScreen.Round.name) { inclusive = true }
                        }
                    })
            }

            composable(route = AppScreen.Summary.name) {
                SummaryScreen(
                    viewModel,
                    onHomeButtonClick = { navController.navigate(AppScreen.Home.name) },
                    onRetryButtonClick = {
                        navController.navigate(AppScreen.Round.name)
                        viewModel.resetRound(newRound = false)
                    }
                )
            }
        }
    }
}