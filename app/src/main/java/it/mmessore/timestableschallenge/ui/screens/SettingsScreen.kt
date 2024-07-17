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
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.rounded.KeyboardArrowRight
import androidx.compose.material3.Card
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.RadioButton
import androidx.compose.material3.Surface
import androidx.compose.material3.Switch
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.State
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.painter.Painter
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import it.mmessore.timestableschallenge.R
import it.mmessore.timestableschallenge.data.persistency.AppPreferences.AppThemeStyle
import it.mmessore.timestableschallenge.ui.ClickableTextWithUrl
import it.mmessore.timestableschallenge.ui.DialogScaffold
import it.mmessore.timestableschallenge.ui.SFXDialog
import it.mmessore.timestableschallenge.utils.getActivity
import it.mmessore.timestableschallenge.utils.getAppVersion
import it.mmessore.timestableschallenge.utils.getAppVersionCode

@Composable
fun SettingsScreen(
    viewmodel: SettingsViewModel = hiltViewModel(),
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current
    val activity = context.getActivity()

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
            SettingsClickableComp(
                icon = painterResource(id = R.drawable.palette_24),
                name = R.string.settings_theme,
                desc = R.string.settings_theme_desc,
                selectedValue = viewmodel.themeStyle.collectAsState().value,
                onValueSaved = {
                    viewmodel.saveTheme(it)
                    activity?.recreate() },
                dialogContent = { selectedTheme, onThemeSelected ->
                    ThemeDialog(selectedTheme, onThemeSelected)
                }
            )
        }

        SettingsGroup(name = R.string.settings_support_group) {
            SettingsClickableComp(
                icon = painterResource(id = R.drawable.info_outline_24),
                dialogImage = painterResource(id = R.drawable.app_icon),
                name = R.string.settings_about,
                desc = R.string.settings_about_desc,
                selectedValue = Unit,
                dialogContent = { _, _ ->
                    AboutDialog()
                }
            )
        }
    }
}

@Composable
fun ThemeDialog(
    selectedTheme: AppThemeStyle,
    onThemeSelected: (AppThemeStyle) -> Unit,
    modifier: Modifier = Modifier
)  {
    Column(
        modifier = modifier
            .fillMaxWidth()
            .padding(16.dp)
    ) {
        Text(
            text = stringResource(id = R.string.settings_theme),
            style = MaterialTheme.typography.titleLarge,
            fontWeight = FontWeight.Bold,
            textAlign = TextAlign.Center,
            modifier = Modifier.fillMaxWidth()
        )
        Spacer(modifier = Modifier.height(16.dp))
        Column {
            AppThemeStyle.entries.forEach { theme ->
                Row(verticalAlignment = Alignment.CenterVertically) {
                    RadioButton(
                        selected = selectedTheme == theme,
                        onClick = { onThemeSelected(theme) }
                    )
                    Text(text = stringResource(id = theme.label))
                }
            }
        }
    }
}

@Composable
fun AboutDialog(modifier: Modifier = Modifier) {
    val context = LocalContext.current
    Column(modifier = modifier
        .fillMaxWidth()
        .padding(16.dp)) {
        Text(
            text = stringResource(id = R.string.app_name),
            style = MaterialTheme.typography.titleLarge,
            fontWeight = FontWeight.Bold,
            textAlign = TextAlign.Center,
            modifier = Modifier.fillMaxWidth()
        )
        Spacer(modifier = Modifier.height(16.dp))
        Text(
            text = stringResource(
                id = R.string.about_text_version,
                getAppVersion(context),
                getAppVersionCode(context)
            ),
            color = MaterialTheme.colorScheme.onSurface
        )
        Spacer(modifier = Modifier.height(8.dp))
        ClickableTextWithUrl(
            text = stringResource(
                id = R.string.about_text_feedback,
                getAppVersion(context),
                getAppVersionCode(context)
            ),
            textUrl = "GitHub",
            url = "https://github.com/Pizza67/times-tables-challenge"
        )
    }
}

/*
    Credits to Tomáš Repčík
    https://tomas-repcik.medium.com/making-extensible-settings-screen-in-jetpack-compose-from-scratch-2558170dd24d
 */
@Composable
fun SettingsGroup(
    @StringRes name: Int,
    // to accept only composables compatible with column
    content: @Composable ColumnScope.() -> Unit
){
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
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 8.dp),
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
                Column(modifier = Modifier
                    .padding(horizontal = 8.dp)
                    .weight(1f)) {
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

@Composable
fun <T> SettingsClickableComp(
    icon: Painter,
    dialogImage: Painter? = null,
    @StringRes name: Int,
    @StringRes desc: Int,
    selectedValue: T,
    onValueSaved: (T) -> Unit = {},
    dialogContent: @Composable (T, (T) -> Unit) -> Unit
) {
    var isDialogShown by remember { mutableStateOf(false) }
    var selectedItem by remember { mutableStateOf(selectedValue) }

    SFXDialog(
        showDialog = isDialogShown,
        onDismissRequest = { isDialogShown = false }
    ) {
        DialogScaffold(
            dialogImage,
            onCloseButtonClick = {
                isDialogShown = false
                onValueSaved(selectedItem) },
            content = {
                dialogContent(selectedItem) { newValue ->
                    selectedItem = newValue
                }
            }
        )
    }

    Surface(
        color = Color.Transparent,
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 8.dp),
        onClick = { isDialogShown = true },
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
                Column(modifier = Modifier
                    .padding(horizontal = 8.dp)
                    .weight(1f)) {
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
                Icon(
                    Icons.AutoMirrored.Rounded.KeyboardArrowRight,
                    tint = MaterialTheme.colorScheme.surfaceTint,
                    contentDescription = stringResource(id = R.string.ic_arrow_forward)
                )
            }
            Spacer(modifier = Modifier.height(16.dp))
            HorizontalDivider(color = MaterialTheme.colorScheme.onSurface)
        }
    }
}