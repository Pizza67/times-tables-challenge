package it.mmessore.timestableschallenge

import android.net.Uri
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import dagger.hilt.android.AndroidEntryPoint
import it.mmessore.timestableschallenge.data.persistency.Constants
import it.mmessore.timestableschallenge.ui.screens.AppRootScreen
import it.mmessore.timestableschallenge.ui.theme.AppTheme

@AndroidEntryPoint
class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            val data: Uri? = intent?.data
            var roundId: String? = null
            if (data?.scheme == Constants.CUSTOM_URI_SCHEME) {
                roundId = data.getQueryParameter("roundId")
            }
            AppTheme {
                AppRootScreen(challengeId = roundId)
            }
        }
    }
}

