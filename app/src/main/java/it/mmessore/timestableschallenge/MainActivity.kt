package it.mmessore.timestableschallenge

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import it.mmessore.timestableschallenge.ui.screens.TimeTablesChallengeApp
import it.mmessore.timestableschallenge.ui.theme.AppTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            AppTheme {
                TimeTablesChallengeApp()
            }
        }
    }
}

