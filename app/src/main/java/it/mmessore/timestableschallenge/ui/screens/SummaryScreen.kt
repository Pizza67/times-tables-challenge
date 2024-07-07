package it.mmessore.timestableschallenge.ui.screens

import android.util.Log
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
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import it.mmessore.timestableschallenge.R
import it.mmessore.timestableschallenge.ui.DialogScaffold
import it.mmessore.timestableschallenge.ui.RoundButton
import it.mmessore.timestableschallenge.ui.SFXDialog
import it.mmessore.timestableschallenge.utils.formatNumber
import kotlinx.coroutines.delay

@Composable
fun SummaryScreen(
    roundId: String,
    viewModel: SummaryViewModel = hiltViewModel(),
    onMenuButtonClick: () -> Unit = {},
    onStatsButtonClick: () -> Unit = {},
    modifier: Modifier = Modifier
) {
    val roundInfo = viewModel.roundInfo.collectAsStateWithLifecycle()
    val rewardDialogInfo = viewModel.rewardDialogInfo.collectAsStateWithLifecycle()
    val bestScoreDialogInfo = viewModel.bestScoreDialogInfo.collectAsStateWithLifecycle()

    var showRewardDialog by remember {
        mutableStateOf(false)
    }
    var showBestScoreDialog by remember {
        mutableStateOf(false)
    }

    LaunchedEffect(roundId) {
        viewModel.fetchRoundInfo(roundId)
        viewModel.checkRewards()
    }

    LaunchedEffect(rewardDialogInfo.value, bestScoreDialogInfo.value) {
        delay(500)
        showRewardDialog = rewardDialogInfo.value != null
        showBestScoreDialog = !showRewardDialog && bestScoreDialogInfo.value != null
        Log.d("showRewardDialog", showRewardDialog.toString())
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
            if (roundInfo.value.timeLeft > 0) {
                Row(
                    horizontalArrangement = Arrangement.Center,
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text(
                        text = stringResource(R.string.stats_round_time_left),
                        style = MaterialTheme.typography.titleMedium,
                        modifier = Modifier.padding(end = 8.dp)
                    )
                    Text(
                        text = formatNumber(roundInfo.value.timeLeft/1000.0),
                        style = MaterialTheme.typography.titleLarge,
                        modifier = Modifier.padding(end = 4.dp)
                    )
                    Text(
                        text = stringResource(R.string.stats_round_sec),
                        style = MaterialTheme.typography.titleMedium
                    )
                }
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
            Modifier.clip(MaterialTheme.shapes.small).fillMaxWidth()
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

    SFXDialog(
        showDialog = showRewardDialog,
        audioResource = R.raw.reveal,
        onDismissRequest = { showRewardDialog = false }
    ) {
        DialogScaffold(
            content = { DialogBody(stringResource(id = rewardDialogInfo.value!!.title), stringResource(id = rewardDialogInfo.value!!.message)) },
            painter = painterResource(id = rewardDialogInfo.value!!.image),
            contentDescription = stringResource(rewardDialogInfo.value!!.contentDescription),
            okBtnText = if (bestScoreDialogInfo.value == null) stringResource(id = R.string.details) else null,
            onOkButtonClick = onStatsButtonClick
        ) {
            showRewardDialog = false
            showBestScoreDialog = bestScoreDialogInfo.value != null
        }
    }

    SFXDialog(
        showDialog = showBestScoreDialog,
        audioResource = R.raw.reveal,
        onDismissRequest = { showBestScoreDialog = false }
    ) {
        DialogScaffold(
            content = { DialogBody(stringResource(id = bestScoreDialogInfo.value!!.title), stringResource(id = bestScoreDialogInfo.value!!.message)) },
            painter = painterResource(id = bestScoreDialogInfo.value!!.image),
            contentDescription = stringResource(bestScoreDialogInfo.value!!.contentDescription),
            okBtnText = stringResource(id = R.string.details),
            onOkButtonClick = onStatsButtonClick
        ) { showBestScoreDialog = false }
    }
}

@Composable
fun DialogBody(title: String, message: String) {
    Column(
        modifier = Modifier.padding(16.dp)
    ) {
        Spacer(modifier = Modifier.height(8.dp))
        Text(
            text = title,
            style = MaterialTheme.typography.titleLarge,
            fontWeight = FontWeight.Bold,
        )
        Spacer(modifier = Modifier.height(8.dp))
        Text(text = message)
    }
}