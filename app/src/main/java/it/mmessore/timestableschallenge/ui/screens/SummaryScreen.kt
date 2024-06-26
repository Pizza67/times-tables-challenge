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
import kotlinx.coroutines.delay

@Composable
fun SummaryScreen(
    roundId: String,
    viewModel: SummaryViewModel = hiltViewModel(),
    onMenuButtonClick: () -> Unit = {},
    onRewardOkButtonClick: () -> Unit = {},
    modifier: Modifier = Modifier
) {
    val roundInfo = viewModel.roundInfo.collectAsStateWithLifecycle()
    val rewardDialogInfo = viewModel.rewardDialogInfo.collectAsStateWithLifecycle()

    var showDialog by remember {
        mutableStateOf(false)
    }

    LaunchedEffect(roundId) {
        viewModel.fetchRoundInfo(roundId)
        viewModel.checkRewards()
    }

    LaunchedEffect(rewardDialogInfo.value) {
        delay(1500)
        showDialog = rewardDialogInfo.value != null
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

    SFXDialog(
        showDialog = showDialog,
        audioResource = R.raw.reveal,
        onDismissRequest = { showDialog = false }
    ) {
        DialogScaffold(
            content = { DialogBody(stringResource(id = rewardDialogInfo.value!!.title), stringResource(id = rewardDialogInfo.value!!.message)) },
            painter = painterResource(id = rewardDialogInfo.value!!.image),
            contentDescription = stringResource(rewardDialogInfo.value!!.contentDescription),
            okBtnText = stringResource(id = R.string.details),
            onDismissRequest = { showDialog = false },
            onOkButtonClick = onRewardOkButtonClick
        )
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