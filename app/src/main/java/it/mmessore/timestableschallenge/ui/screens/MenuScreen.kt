package it.mmessore.timestableschallenge.ui.screens

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import it.mmessore.timestableschallenge.R
import it.mmessore.timestableschallenge.ui.RoundButton

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
    val scrollState = rememberScrollState()

    LaunchedEffect(Unit) {
        viewModel.getRoundNum()
    }

    Column(
        modifier = modifier
            .verticalScroll(scrollState)
            .fillMaxSize()
            .padding(16.dp),
        verticalArrangement = Arrangement.Top
    ) {
        Text(
            text = stringResource(id = R.string.menu),
            style = MaterialTheme.typography.displayMedium,
            textAlign = TextAlign.Center,
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 16.dp)
        )
        GameMenu(
            onMenuButtonClick = onMenuButtonClick,
            hasPlayedRounds = hasPlayedRounds.value,
            modifier = Modifier
                .fillMaxWidth()
                .widthIn(max = 600.dp))
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

@Preview(showBackground = true)
@Composable
fun RoundButtonPreview() {
    MenuButton(onClick = {}, text = "New Game")
}

@Preview(showBackground = true, showSystemUi = true)
@Composable
fun MenuScreenPreview() {
    MenuScreen(onMenuButtonClick = {})
}