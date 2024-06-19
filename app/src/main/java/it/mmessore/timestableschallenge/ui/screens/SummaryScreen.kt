package it.mmessore.timestableschallenge.ui.screens

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import it.mmessore.timestableschallenge.R
import it.mmessore.timestableschallenge.ui.RoundButton

@Composable
fun SummaryScreen(
    roundId: String,
    viewModel: SummaryViewModel = hiltViewModel(),
    onMenuButtonClick: () -> Unit = {},
    modifier: Modifier = Modifier
) {
    val roundInfo = viewModel.roundInfo.collectAsStateWithLifecycle()

    LaunchedEffect(roundId) {
        viewModel.fetchRoundInfo(roundId)
    }

    Column(
        modifier = modifier
            .fillMaxSize()
            .padding(16.dp),
        verticalArrangement = Arrangement.SpaceBetween
    ) {
        Column (
            verticalArrangement = Arrangement.Center,
            modifier = Modifier.padding(vertical = 16.dp)
        ){
            Row(
                horizontalArrangement = Arrangement.Center,
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.fillMaxWidth()
            ) {
                Text(
                    text = stringResource(R.string.final_score),
                    style = MaterialTheme.typography.displayMedium,
                    modifier = Modifier.padding(end = 8.dp)
                )
                Text(
                    text = roundInfo.value.score.toString(),
                    style = MaterialTheme.typography.displayLarge,
                )
            }
            Spacer(modifier = Modifier.height(8.dp))
            Row(
                horizontalArrangement = Arrangement.Center,
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.fillMaxWidth()
            ) {
                Text(
                    text = stringResource(R.string.your_level),
                    style = MaterialTheme.typography.headlineMedium,
                    modifier = Modifier.padding(end = 8.dp)
                )
                Text(
                    text = stringResource(roundInfo.value.level.name),
                    style = MaterialTheme.typography.headlineLarge,
                )
            }
        }
        Image(
            painter = painterResource(id = roundInfo.value.level.image),
            contentDescription = null,
            Modifier.clip(MaterialTheme.shapes.small)
        )
        Row (
            horizontalArrangement = Arrangement.Center,
            modifier = Modifier.fillMaxWidth()
        ){
            Text (
                text = stringResource(id = roundInfo.value.level.description),
                style = MaterialTheme.typography.titleLarge,
                textAlign = TextAlign.Center,
            )
        }
        RoundButton(
            onClick = onMenuButtonClick,
            text = stringResource(id = R.string.menu),
            modifier = Modifier
                .padding(vertical = 16.dp)
                .align(alignment = Alignment.CenterHorizontally)
            )
    }
}