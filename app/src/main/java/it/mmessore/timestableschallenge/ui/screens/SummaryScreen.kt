package it.mmessore.timestableschallenge.ui.screens

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import it.mmessore.timestableschallenge.R
import it.mmessore.timestableschallenge.ui.RoundButton

@Composable
fun SummaryScreen(
    viewModel: RoundViewModel,
    onHomeButtonClick: () -> Unit = {},
    onRetryButtonClick: () -> Unit = {},
    modifier: Modifier = Modifier
) {
    val score = viewModel.score.value

    Column(
        modifier = modifier
            .fillMaxSize()
            .padding(16.dp),
        verticalArrangement = Arrangement.SpaceBetween
    ) {
        Row (
            horizontalArrangement = Arrangement.Center,
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier
                .padding(vertical = 16.dp)
                .fillMaxWidth()
        ) {
            Text(
                text = stringResource(R.string.final_score),
                style = MaterialTheme.typography.displayMedium,
                modifier = Modifier.padding(end = 8.dp)
            )
            Text(
                text = score.toString(),
                style = MaterialTheme.typography.displayLarge,
            )
        }
        Image(
            painter = painterResource(id = viewModel.getScoreImageId(score)),
            contentDescription = null,
            Modifier.clip(MaterialTheme.shapes.small)
        )
        Text (
            text = stringResource(id = viewModel.getScoreDescriptionId(score)),
            style = MaterialTheme.typography.titleLarge,
            textAlign = TextAlign.Center,
            modifier = Modifier.fillMaxWidth()
        )
        Row (
            horizontalArrangement = Arrangement.SpaceAround,
            modifier = Modifier
                .fillMaxWidth()
                .padding(8.dp)
        ){
            RoundButton(
                onClick = onHomeButtonClick,
                text = stringResource(id = R.string.home_button),
            )
            RoundButton(
                onClick = onRetryButtonClick,
                text = stringResource(id = R.string.retry_button),
            )
        }
    }
}