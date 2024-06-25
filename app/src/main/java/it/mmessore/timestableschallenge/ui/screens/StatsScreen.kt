package it.mmessore.timestableschallenge.ui.screens

import androidx.annotation.StringRes
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
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
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Card
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.PathEffect
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import it.mmessore.timestableschallenge.R
import it.mmessore.timestableschallenge.data.BadgeInfo
import it.mmessore.timestableschallenge.data.Levels
import it.mmessore.timestableschallenge.data.persistency.Round
import it.mmessore.timestableschallenge.ui.SFXDialog
import it.mmessore.timestableschallenge.utils.formatTimestamp
import java.text.NumberFormat
import java.util.Locale

@Composable
fun StatsScreen(
    viewModel: StatsViewModel = hiltViewModel(),
    onRetryRoundButtonClick: (String) -> Unit,
    modifier: Modifier = Modifier
){
    val scrollState = rememberScrollState()

    val currentRank = viewModel.currentRank.collectAsState()
    val currentRankImg = viewModel.currentRankImg.collectAsState()
    val numRounds = viewModel.numRounds.collectAsState()
    val avgScore = viewModel.avgRounds.collectAsState()
    val totalScore = viewModel.totScore.collectAsState()
    val bestRound = viewModel.bestRound.collectAsState()
    val worstRound = viewModel.worstRound.collectAsState()
    val badges = viewModel.badges.collectAsState()

    val items by remember {
        derivedStateOf {
            badges.value.map { badge ->
                if (badge == null) {
                    ItemType.DashedCircle
                } else {
                    ItemType.BadgeItem(badge) {

                    }
                }
            }
        }
    }

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
        CurrentRank(currentRank.value)
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 8.dp)
        ) {
            Image(
                painter = painterResource(currentRankImg.value),
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
                    formatNumber(numRounds.value.toDouble()),
                    Modifier.padding(vertical = 8.dp)
                )
                Stat(stringResource(
                    id = R.string.stat_total_score),
                    formatNumber(totalScore.value.toDouble()),
                    Modifier.padding(vertical = 8.dp)
                )
                Stat(stringResource(
                    id = R.string.stat_avg_score),
                    formatNumber(avgScore.value),
                    Modifier.padding(vertical = 8.dp)
                )
            }
        }

        AchievementList(items, modifier = Modifier.padding(top = 8.dp))

        bestRound.value?.let { bestRound ->
            Text(
                text = stringResource(R.string.your_best_round),
                style = MaterialTheme.typography.bodyLarge
            )
            StatsRoundCard(bestRound)
        }
        worstRound.value?.let { worstRound ->
            Spacer(modifier = Modifier.height(8.dp))
            Text(
                text = stringResource(R.string.replay_your_worst_round),
                style = MaterialTheme.typography.bodyLarge
            )
            StatsRoundCard(worstRound, onRetryRoundButtonClick)
        }
    }
}

@Composable
private fun AchievementList(
    items : List<ItemType>,
    modifier: Modifier = Modifier
) {
    var selectedBadge by remember { mutableStateOf<BadgeInfo?>(null) }
    var isDialogVisible by remember { mutableStateOf(false) }

    Column(modifier = modifier) {
        Text(
            text = stringResource(R.string.your_achievements),
            style = MaterialTheme.typography.bodyLarge
        )

        LazyRow(
            horizontalArrangement = Arrangement.SpaceEvenly,
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 8.dp)
        ) {
            items(items) { item ->
                when (item) {
                    is ItemType.BadgeItem -> {
                        Image(
                            painter = painterResource(item.badgeInfo.image),
                            contentDescription = stringResource(item.badgeInfo.nameStrId),
                            modifier = Modifier
                                .size(85.dp)
                                .padding(8.dp)
                                .clip(CircleShape)
                                .clickable {
                                    selectedBadge = item.badgeInfo
                                    isDialogVisible = true
                                }
                        )
                    }

                    is ItemType.DashedCircle -> {
                        DashedCircle(
                            circleColor = MaterialTheme.colorScheme.primary,
                            modifier = Modifier
                                .size(85.dp)
                                .padding(10.dp)
                        )
                    }
                }
            }
        }
    }

    selectedBadge?.let { badge ->
        SFXDialog(showDialog = isDialogVisible, onDismissRequest = { isDialogVisible = false }) {
            RewardDialogContent(
                title = stringResource(id = badge.nameStrId),
                message = stringResource(id = badge.description),
                painter = painterResource(id = badge.image),
                onDismissRequest = { isDialogVisible = false }
            )
        }
    }
}

@Composable
private fun StatsRoundCard(
    round: Round,
    onReplayButtonClick: ((String) -> Unit)? = null,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier
            .fillMaxWidth()
            .padding(vertical = 8.dp)
    ) {
        Box {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.fillMaxWidth()
            ) {
                Image(
                    painter = painterResource(Levels.getLevelByScore(round.score).image),
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
                    Text(text = formatTimestamp(round.timestamp), style = MaterialTheme.typography.bodySmall)
                    Spacer(modifier = Modifier.height(8.dp))
                    Text(
                        text = stringResource(R.string.stats_round_level),
                        style = MaterialTheme.typography.bodyMedium
                    )
                    Text(
                        text = stringResource(Levels.getLevelByScore(round.score).name),
                        style = MaterialTheme.typography.headlineMedium
                    )
                    Row(
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = stringResource(R.string.stats_round_score),
                            style = MaterialTheme.typography.bodyMedium,
                            modifier = Modifier.padding(end = 8.dp)
                        )
                        Text(
                            text = round.score.toString(),
                            style = MaterialTheme.typography.titleLarge,
                            modifier = Modifier.padding(end = 16.dp)
                        )
                        if (round.timeLeft > 0) {
                            Text(
                                text = stringResource(R.string.stats_round_time_left),
                                style = MaterialTheme.typography.bodyMedium,
                                modifier = Modifier.padding(end = 8.dp)
                            )
                            Text(
                                text = formatNumber(round.timeLeft/1000.0, 3),
                                style = MaterialTheme.typography.titleLarge,
                                modifier = Modifier.padding(end = 4.dp)
                            )
                            Text(
                                text = stringResource(R.string.stats_round_sec),
                                style = MaterialTheme.typography.titleSmall
                            )
                        }
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
                        onClick = { onReplayButtonClick (round.roundId) }
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
fun DashedCircle(circleColor: Color, modifier: Modifier) {
    Canvas(modifier = modifier) {
        val radius = size.minDimension / 2
        drawCircle(
            color = circleColor,
            radius = radius,
            style = Stroke(
                width = 2.dp.toPx(),
                pathEffect = PathEffect.dashPathEffect(floatArrayOf(20f, 10f)),
            )
        )
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
            text = stringResource(R.string.current_rank),
            style = MaterialTheme.typography.bodyLarge
        )
        Row (
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier.padding(top = 4.dp)
        ) {
            Text(
                text = stringResource(id = rank),
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

private fun formatNumber(number: Double, maximumFractionDigits: Int = 1, locale: Locale = Locale.getDefault()): String {
    val formatter = NumberFormat.getNumberInstance(locale)
    formatter.maximumFractionDigits = maximumFractionDigits
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

sealed class ItemType {
    data class BadgeItem (val badgeInfo: BadgeInfo, val onClick: () -> Unit) : ItemType()
    data object DashedCircle : ItemType()
}
