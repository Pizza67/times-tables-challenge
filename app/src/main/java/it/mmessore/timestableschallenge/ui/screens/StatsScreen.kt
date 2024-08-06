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
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.Card
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.PathEffect
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.drawText
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.rememberTextMeasurer
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import it.mmessore.timestableschallenge.R
import it.mmessore.timestableschallenge.data.BadgeInfo
import it.mmessore.timestableschallenge.data.Badges
import it.mmessore.timestableschallenge.data.Levels
import it.mmessore.timestableschallenge.data.persistency.Round
import it.mmessore.timestableschallenge.ui.DialogPager
import it.mmessore.timestableschallenge.ui.DialogScaffold
import it.mmessore.timestableschallenge.ui.SFXDialog
import it.mmessore.timestableschallenge.ui.ScreenContainer
import it.mmessore.timestableschallenge.utils.formatNumber
import it.mmessore.timestableschallenge.utils.formatTimestamp

@Composable
fun StatsScreen(
    viewModel: StatsViewModel = hiltViewModel(),
    onRetryRoundButtonClick: (String) -> Unit,
    modifier: Modifier = Modifier
){
    val currentRank = viewModel.currentRank.collectAsState()
    val currentRankImg = viewModel.currentRankImg.collectAsState()
    val numRounds = viewModel.numRounds.collectAsState()
    val avgScore = viewModel.avgRounds.collectAsState()
    val totalScore = viewModel.totScore.collectAsState()
    val bestRound = viewModel.bestRound.collectAsState()
    val worstRound = viewModel.worstRound.collectAsState()
    val badges = viewModel.badges.collectAsState()

    ScreenContainer(
        modifier = modifier
    ) {
        CurrentRank(currentRank.value)
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 8.dp)
        ) {
            Image(
                painter = painterResource(currentRankImg.value),
                contentDescription = null,
                contentScale = ContentScale.Crop,
                modifier = Modifier
                    .clip(MaterialTheme.shapes.small)
                    .weight(1f)
            )
            Column(
                verticalArrangement = Arrangement.Center,
                modifier = Modifier.padding(horizontal = 16.dp)
            ) {
                Stat(
                    stringResource(
                        id = R.string.stat_num_rounds
                    ),
                    formatNumber(numRounds.value.toDouble()),
                    Modifier.padding(vertical = 8.dp)
                )
                Stat(
                    stringResource(
                        id = R.string.stat_total_score
                    ),
                    formatNumber(totalScore.value.toDouble()),
                    Modifier.padding(vertical = 8.dp)
                )
                Stat(
                    stringResource(
                        id = R.string.stat_avg_score
                    ),
                    formatNumber(avgScore.value),
                    Modifier.padding(vertical = 8.dp)
                )
            }
        }

        AchievementList(badges.value, modifier = Modifier.padding(top = 8.dp))

        bestRound.value?.let { bestRound ->
            Text(
                text = stringResource(R.string.your_best_round),
                style = MaterialTheme.typography.bodyLarge
            )
            StatsRoundCard(
                round = bestRound,
                useTimeleft = viewModel.useTimeLeft()
            )
        }
        worstRound.value?.let { worstRound ->
            Spacer(modifier = Modifier.height(8.dp))
            Text(
                text = stringResource(R.string.replay_your_worst_round),
                style = MaterialTheme.typography.bodyLarge
            )
            StatsRoundCard(
                round = worstRound,
                useTimeleft = viewModel.useTimeLeft(),
                onReplayButtonClick = onRetryRoundButtonClick
            )
        }
    }
}

@Composable
private fun AchievementList(
    badges : List<BadgeInfo>,
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
            items(badges) { badge ->
                if (badge.isAchieved()) {
                    Image(
                        painter = painterResource(Badges.list[badge.id].image),
                        contentDescription = stringResource(Badges.list[badge.id].nameStrId),
                        modifier = Modifier
                            .size(85.dp)
                            .padding(8.dp)
                            .clip(CircleShape)
                            .clickable {
                                selectedBadge = badge
                                isDialogVisible = true
                            }
                        )
                    } else {
                        DashedCircle(
                            circleColor = MaterialTheme.colorScheme.primary,
                            modifier = Modifier
                                .size(85.dp)
                                .padding(10.dp)
                                .clickable {
                                    selectedBadge = badge
                                    isDialogVisible = true
                                }
                        )
                    }
                }
            }
        }

    selectedBadge?.let { badge ->
        SFXDialog(showDialog = isDialogVisible, onDismissRequest = { isDialogVisible = false }) {
            DialogScaffold(
                content = if (badge.isAchieved()) {
                    {
                        DialogPager(
                            listOf(
                                { AchievedBadgeDescription(badgeInfo = badge) },
                                { AchievedBadgeInfo(badgeInfo = badge) }
                            )
                        )
                    }
                } else {
                    { NotAchievedBadgeInfo(badgeInfo = badge) }
                },
                painter = painterResource(id =
                    if (badge.isAchieved())
                        Badges.list[badge.id].image
                    else
                        R.drawable.img_badge_not_achieved
                ),
                onCloseButtonClick = { isDialogVisible = false }
            )
        }
    }
}

@Composable
fun AchievedBadgeDescription(badgeInfo: BadgeInfo) {
    Column(modifier = Modifier.padding(16.dp)) {
        Spacer(modifier = Modifier.height(8.dp))
        Text(
            text = stringResource(id = Badges.list[badgeInfo.id].nameStrId),
            style = MaterialTheme.typography.titleLarge,
            fontWeight = FontWeight.Bold
        )
        Spacer(modifier = Modifier.height(8.dp))
        Text(text = stringResource(id = Badges.list[badgeInfo.id].description))
    }
}

@Composable
fun AchievedBadgeInfo(badgeInfo: BadgeInfo) {
    Column(modifier = Modifier.padding(16.dp)) {
        Spacer(modifier = Modifier.height(8.dp))
        Text(
            text = stringResource(R.string.achieved_on),
            style = MaterialTheme.typography.bodyLarge,
        )
        Spacer(modifier = Modifier.height(8.dp))
        Text(
            text = formatTimestamp(badgeInfo.timestamp, showTime = true),
            style = MaterialTheme.typography.titleLarge,
        )
        Spacer(modifier = Modifier.height(8.dp))
        Text(
            text = stringResource(R.string.round_number),
            style = MaterialTheme.typography.bodyLarge,
        )
        Spacer(modifier = Modifier.height(8.dp))
        Text(
            text = formatNumber(badgeInfo.numRounds.toDouble()),
            style = MaterialTheme.typography.titleLarge,
        )
        Spacer(modifier = Modifier.height(8.dp))
        Text(
            text = stringResource(R.string.avarage_score),
            style = MaterialTheme.typography.bodyLarge,
        )
        Spacer(modifier = Modifier.height(8.dp))
        Text(
            text = formatNumber(badgeInfo.avgScore),
            style = MaterialTheme.typography.titleLarge,
        )
    }
}

@Composable
fun NotAchievedBadgeInfo(badgeInfo: BadgeInfo) {
    Column(modifier = Modifier.padding(16.dp)) {
        Spacer(modifier = Modifier.height(8.dp))
        Text(
            text = stringResource(R.string.requirements),
            style = MaterialTheme.typography.titleLarge,
            fontWeight = FontWeight.Bold
        )
        Spacer(modifier = Modifier.height(8.dp))
        Text(
            text = stringResource(
                R.string.achievement_requirements_desc,
                Badges.list[badgeInfo.id].minRounds,
                Badges.list[badgeInfo.id].minAvgScore
            ),
            style = MaterialTheme.typography.bodyMedium
        )
    }
}

@Composable
private fun StatsRoundCard(
    round: Round,
    useTimeleft: Boolean,
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
                        .clip(MaterialTheme.shapes.small),
                    contentScale = ContentScale.Crop
                )
                Column(
                    verticalArrangement = Arrangement.Center,
                    modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp)
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
                            modifier = Modifier.padding(end = 4.dp)
                        )
                        Text(
                            text = round.score.toString(),
                            style = MaterialTheme.typography.titleMedium,
                            modifier = Modifier.padding(end = 8.dp)
                        )
                        if (useTimeleft && round.timeLeft > 0) {
                            Spacer(modifier = Modifier.weight(1f))
                            Text(
                                text = stringResource(R.string.stats_round_time_left),
                                style = MaterialTheme.typography.bodyMedium,
                                modifier = Modifier.padding(end = 4.dp)
                            )
                            Text(
                                text = formatNumber(round.timeLeft/1000.0, 3),
                                style = MaterialTheme.typography.titleMedium,
                                maxLines = 1,
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
fun DashedCircle(
    circleColor: Color,
    modifier: Modifier
) {
    val textMeasurer = rememberTextMeasurer()
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
        val text = "?"
        val textLayoutResult = textMeasurer.measure(
            text = AnnotatedString(text),
            style = TextStyle(
                color = circleColor,
                fontSize = (radius / 2).sp,
                textAlign = TextAlign.Center
            )
        )
        drawText(
            textLayoutResult = textLayoutResult,
            topLeft = Offset(
                x = center.x - textLayoutResult.size.width / 2,
                y = center.y - textLayoutResult.size.height / 2
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
        }
    }
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

