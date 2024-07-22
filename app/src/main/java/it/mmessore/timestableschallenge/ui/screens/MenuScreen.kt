package it.mmessore.timestableschallenge.ui.screens

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.widthIn
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import it.mmessore.timestableschallenge.R
import it.mmessore.timestableschallenge.ui.RoundButton
import it.mmessore.timestableschallenge.ui.ScreenContainer

enum class MenuAction {
    NEW_GAME,
    LAST_GAME,
    SHARE_GAME,
    YOUR_SCORES,
    SETTINGS
}
@Composable
fun MenuScreen(
    onMenuButtonClick: (MenuAction) -> Unit,
    viewModel: MenuViewModel = hiltViewModel(),
    modifier: Modifier = Modifier
) {
    val hasPlayedRounds = viewModel.hasPlayedRounds.collectAsState()

    LaunchedEffect(Unit) {
        viewModel.getRoundNum()
    }

    ScreenContainer(
        modifier = modifier,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        GameMenu(
            onMenuButtonClick = onMenuButtonClick,
            hasPlayedRounds = hasPlayedRounds.value,
            modifier = Modifier.widthIn(max = 600.dp)
        )
    }
}

@Composable
fun GameMenu(
    onMenuButtonClick: (MenuAction) -> Unit,
    hasPlayedRounds: Boolean = true,
    modifier: Modifier = Modifier)
{
    Column(
        modifier = modifier,
        verticalArrangement = Arrangement.SpaceBetween
    ) {
        MenuButton(onClick = { onMenuButtonClick(MenuAction.NEW_GAME) }, text = stringResource(R.string.menu_start_new_game) )
        if (hasPlayedRounds)
            MenuButton(onClick = { onMenuButtonClick(MenuAction.LAST_GAME) }, text = stringResource(R.string.menu_play_last_game))
        MenuButton(onClick = { onMenuButtonClick(MenuAction.SHARE_GAME) }, text = stringResource(R.string.menu_share_new_game) )
        if (hasPlayedRounds)
            MenuButton(onClick = { onMenuButtonClick(MenuAction.YOUR_SCORES) }, text = stringResource(R.string.menu_your_scores) )
        MenuButton(onClick = { onMenuButtonClick(MenuAction.SETTINGS) }, text = stringResource(R.string.menu_settings) )
    }
}

@Composable
fun MenuButton(
    onClick: () -> Unit,
    text: String,
    enabled: Boolean = true,
    modifier: Modifier = Modifier
        .padding(16.dp)
        .fillMaxWidth()
){
    RoundButton(
        onClick = onClick,
        text = text,
        modifier = modifier,
        enabled = enabled,
        uppercase = false
    )
}

