package it.mmessore.timestableschallenge

import android.net.Uri
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.runtime.snapshotFlow
import dagger.hilt.android.AndroidEntryPoint
import it.mmessore.timestableschallenge.data.persistency.AppPreferences
import it.mmessore.timestableschallenge.data.persistency.Constants
import it.mmessore.timestableschallenge.ui.screens.AppRootScreen
import it.mmessore.timestableschallenge.ui.theme.AppTheme
import it.mmessore.timestableschallenge.utils.isSystemInDarkTheme
import kotlinx.coroutines.flow.MutableStateFlow
import javax.inject.Inject

@AndroidEntryPoint
class MainActivity : ComponentActivity() {
    @Inject lateinit var constants: Constants
    @Inject lateinit var appPreferences: AppPreferences

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            val data: Uri? = intent?.data
            var roundId: String? = null
            if (data?.scheme == constants.CUSTOM_URI_SCHEME) {
                roundId = data.getQueryParameter(constants.QUERY_PARAM_ROUND_ID)
            }

            val themeStyleFlow = remember { MutableStateFlow(appPreferences.themeStyle) }

            LaunchedEffect(Unit) {
                snapshotFlow { appPreferences.themeStyle }
                    .collect { themeStyleFlow.value = it }
            }

            val themeStyle by themeStyleFlow.collectAsState()
            AppTheme (darkTheme = when (themeStyle) {
                AppPreferences.AppThemeStyle.LIGHT -> false
                AppPreferences.AppThemeStyle.DARK -> true
                else -> isSystemInDarkTheme(this)
            }){
                AppRootScreen(challengeId = roundId)
            }
        }
    }
}

