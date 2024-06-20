package it.mmessore.timestableschallenge.ui.screens

import android.util.Log
import androidx.annotation.DrawableRes
import androidx.annotation.StringRes
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Card
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import it.mmessore.timestableschallenge.R
import it.mmessore.timestableschallenge.data.Levels
import it.mmessore.timestableschallenge.ui.theme.AppTheme
import it.mmessore.timestableschallenge.utils.formatTimestamp
import java.text.NumberFormat
import java.util.Locale

@Composable
fun StatsScreen(
    viewModel: StatsViewModel = hiltViewModel(),
    modifier: Modifier = Modifier
){
    val scrollState = rememberScrollState()
    Column(
        modifier = modifier
            .verticalScroll(scrollState)
            .fillMaxSize()
            .padding(16.dp),
        verticalArrangement = Arrangement.Top
    ) {
        Text(
            text = stringResource(id = R.string.menu_your_scores),
            style = MaterialTheme.typography.displayMedium,
            textAlign = TextAlign.Center,
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 16.dp)
        )
        CurrentRank(0)
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 8.dp)
        ) {
            Image(
                painter = painterResource(R.drawable.img_rank_high),
                contentDescription = null,
                Modifier
                    .clip(MaterialTheme.shapes.small)
                    .weight(1f)
            )
            Column (
                verticalArrangement = Arrangement.Center,
                modifier = Modifier.padding(start = 16.dp)
            ) {
                Stat(stringResource(
                    id = R.string.stat_num_rounds),
                    formatNumber(149.toDouble()),
                    Modifier.padding(vertical = 8.dp)
                )
                Stat(stringResource(
                    id = R.string.stat_total_score),
                    formatNumber(15259.toDouble()),
                    Modifier.padding(vertical = 8.dp)
                )
                Stat(stringResource(
                    id = R.string.stat_avg_score),
                    formatNumber(15.3),
                    Modifier.padding(vertical = 8.dp)
                )
            }
        }
        Text(
            text = "Best round:",
            style = MaterialTheme.typography.bodyLarge
        )
        StatsRoundCard(
            level = stringResource(id = Levels.list[4].name),
            image = Levels.list[4].image,
            timeLeft = 345/1000
        )
        Spacer(modifier = Modifier.height(8.dp))
        Text(
            text = "Try to improve this score:",
            style = MaterialTheme.typography.bodyLarge
        )
        StatsRoundCard(
            level = stringResource(id = Levels.list[1].name),
            image = Levels.list[1].image,
            score = 12,
            timestamp = System.currentTimeMillis() - 100000000,
            onReplayButtonClick = { Log.d("StatsScreen", "Replay your worst round") }
        )
    }
}

@Composable
private fun StatsRoundCard(
    level: String = "Cosmic Legend",
    @DrawableRes image: Int = R.drawable.img_score_max,
    score: Int = 20,
    timestamp: Long = System.currentTimeMillis(),
    timeLeft: Long = 0,
    onReplayButtonClick: (() -> Unit)? = null,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 8.dp)
    ) {
        Box {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.fillMaxWidth()
            ) {
                Image(
                    painter = painterResource(image),
                    contentDescription = null,
                    modifier = Modifier
                        .size(120.dp)
                        .padding(end = 8.dp),
                    contentScale = ContentScale.Crop
                )
                Column(
                    verticalArrangement = Arrangement.Center,
                    modifier = Modifier.weight(1f)
                ) {
                    Text(text = formatTimestamp(timestamp), style = MaterialTheme.typography.bodySmall)
                    Spacer(modifier = Modifier.height(8.dp))
                    Text(
                        text = "Level:",
                        style = MaterialTheme.typography.bodyMedium
                    )
                    Text(
                        text = level,
                        style = MaterialTheme.typography.headlineMedium
                    )
                    Row(
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = "Score:",
                            style = MaterialTheme.typography.bodyMedium,
                            modifier = Modifier.padding(end = 8.dp)
                        )
                        Text(
                            text = score.toString(),
                            style = MaterialTheme.typography.titleLarge,
                            modifier = Modifier.padding(end = 16.dp)
                        )
                        Text(
                            text = "Time left:",
                            style = MaterialTheme.typography.bodyMedium,
                            modifier = Modifier.padding(end = 8.dp)
                        )
                        Text(
                            text = timeLeft.toString(),
                            style = MaterialTheme.typography.titleLarge,
                            modifier = Modifier.padding(end = 4.dp)
                        )
                        Text(
                            text = "s",
                            style = MaterialTheme.typography.titleSmall
                        )
                    }
                }
            }
            if (onReplayButtonClick != null) {
                Column(
                    horizontalAlignment = Alignment.End,
                    verticalArrangement = Arrangement.Top,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    IconButton(
                        modifier = Modifier.padding(horizontal = 4.dp),
                        onClick = onReplayButtonClick
                    ) {
                        Icon(
                            painter = painterResource(id = R.drawable.ic_replay_circle_filled_24),
                            contentDescription = "ranking information",
                            tint = MaterialTheme.colorScheme.primary,
                            modifier = Modifier.size(32.dp)
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun CurrentRank(
    @StringRes rank: Int,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier.fillMaxWidth()
    ) {
        Text(
            text = "Current rank:",
            style = MaterialTheme.typography.bodyLarge
        )
        Row (
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier.padding(top = 4.dp)
        ) {
            Text(
                text = "Sparkling Champion",
                style = MaterialTheme.typography.headlineLarge
            )
            IconButton(
                modifier = Modifier.padding(horizontal = 16.dp),
                onClick = { /*TODO*/ }
            ) {
                Icon(
                    painter = painterResource(id = R.drawable.ic_info_24),
                    contentDescription = "ranking information",
                    tint = MaterialTheme.colorScheme.primary,
                    modifier = Modifier.size(32.dp)
                )
            }
        }

    }
}

private fun formatNumber(number: Double, locale: Locale = Locale.getDefault()): String {
    val formatter = NumberFormat.getNumberInstance(locale)
    formatter.maximumFractionDigits = 1
    return formatter.format(number)
}

@Composable
fun Stat(title: String, stat: String, modifier: Modifier = Modifier) {
    Column (modifier = modifier){
        Text(
            text = title,
            style = MaterialTheme.typography.bodyLarge
        )
        Spacer(modifier = Modifier.height(4.dp))
        Text(
            text = stat,
            style = MaterialTheme.typography.headlineLarge
        )
    }
}

@Preview(showBackground = true, showSystemUi = true)
@Composable
fun StatsScreenPreview() {
    AppTheme {
        StatsScreen()
    }
}