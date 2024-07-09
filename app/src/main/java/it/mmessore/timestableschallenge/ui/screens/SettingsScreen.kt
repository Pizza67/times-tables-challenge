package it.mmessore.timestableschallenge.ui.screens

import androidx.annotation.StringRes
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Card
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Switch
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.State
import androidx.compose.runtime.collectAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.painter.Painter
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import it.mmessore.timestableschallenge.R
import it.mmessore.timestableschallenge.ui.CommonScaffold

@Composable
fun SettingsScreen(
    viewmodel: SettingsViewModel = hiltViewModel(),
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier
            .verticalScroll(rememberScrollState())
            .fillMaxSize()
            .padding(16.dp),
        verticalArrangement = Arrangement.SpaceBetween
    ) {
        SettingsGroup(name = R.string.settings_game_group) {
            SettingsSwitchComp(
                icon = painterResource(id = R.drawable.filter_9_plus_24),
                name = R.string.settings_extended_mode,
                desc = R.string.settings_extended_mode_desc,
                state = viewmodel.swExtendedMode.collectAsState(),
                onClick = { viewmodel.toggleExtendedMode() }
            )
            SettingsSwitchComp(
                icon = painterResource(id = R.drawable.trending_down_24),
                name = R.string.settings_overwrite_best_scores,
                desc = R.string.settings_overwrite_best_scores_desc,
                state = viewmodel.swOverwriteBestScores.collectAsState(),
                onClick = { viewmodel.toggleOverwriteBestScores() }
            )
            SettingsSwitchComp(
                icon = painterResource(id = R.drawable.time_left_24),
                name = R.string.settings_use_time_left,
                desc = R.string.settings_use_time_left_desc,
                state = viewmodel.swUseTimeLeft.collectAsState(),
                onClick = { viewmodel.toggleUseTimeLeft() }
            )
        }

        SettingsGroup(name = R.string.settings_look_and_feel_group) {
            SettingsSwitchComp(
                icon = painterResource(id = R.drawable.music_note_24),
                name = R.string.settings_play_sounds,
                desc = R.string.settings_play_sounds_desc,
                state = viewmodel.swPlaySounds.collectAsState(),
                onClick = { viewmodel.togglePlaySounds() }
            )
        }
    }
}

@Composable
fun SettingsGroup(
    @StringRes name: Int,
    // to accept only composables compatible with column
    content: @Composable ColumnScope.() -> Unit ){
    Column(modifier = Modifier.padding(vertical = 8.dp)) {
        Text(
            text = stringResource(id = name),
            style = MaterialTheme.typography.titleLarge,
        )
        Spacer(modifier = Modifier.height(8.dp))
        Card(
            modifier = Modifier.fillMaxWidth()
        ) {
            Column (modifier = Modifier.padding(16.dp)){
                content()
            }
        }
    }
}

@Composable
fun SettingsSwitchComp(
    icon: Painter,
    @StringRes name: Int,
    @StringRes desc: Int,
    state: State<Boolean>,
    onClick: () -> Unit
) {
    Surface(
        color = Color.Transparent,
        modifier = Modifier.fillMaxWidth().padding(vertical = 8.dp),
        onClick = onClick,
    ) {
        Column {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Icon(
                    painter = icon,
                    tint = MaterialTheme.colorScheme.primary,
                    contentDescription = stringResource(id = name),
                    modifier = Modifier.size(32.dp)
                )
                Spacer(modifier = Modifier.width(8.dp))
                Column(modifier = Modifier.padding(horizontal = 8.dp).weight(1f)) {
                    Text(
                        text = stringResource(id = name),
                        style = MaterialTheme.typography.bodyLarge,
                        textAlign = TextAlign.Start,
                    )
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(
                        text = stringResource(id = desc),
                        style = MaterialTheme.typography.labelMedium,
                        textAlign = TextAlign.Start,
                    )
                }
                Spacer(modifier = Modifier.width(8.dp))
                Switch(
                    checked = state.value,
                    onCheckedChange = { onClick() }
                )
            }
            Spacer(modifier = Modifier.height(16.dp))
            HorizontalDivider(color = MaterialTheme.colorScheme.onSurface)
        }
    }
}