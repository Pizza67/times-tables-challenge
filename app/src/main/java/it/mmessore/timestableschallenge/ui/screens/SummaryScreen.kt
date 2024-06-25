package it.mmessore.timestableschallenge.ui.screens

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.spring
import androidx.compose.animation.expandVertically
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.IntrinsicSize
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
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
import androidx.compose.ui.graphics.painter.Painter
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import it.mmessore.timestableschallenge.R
import it.mmessore.timestableschallenge.ui.SFXDialog
import it.mmessore.timestableschallenge.ui.RoundButton
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
        RewardDialogContent(
            title = stringResource(id = rewardDialogInfo.value!!.title),
            message = stringResource(id = rewardDialogInfo.value!!.message),
            painter = painterResource(id = rewardDialogInfo.value!!.image),
            contentDescription = stringResource(rewardDialogInfo.value!!.contentDescription),
            okBtnText = stringResource(id = R.string.details),
            onDismissRequest = { showDialog = false },
            onOkButtonClick = onRewardOkButtonClick
        )
    }
}

@Composable
fun RewardDialogContent(
    painter: Painter = painterResource(id = R.drawable.img_rank_medium),
    contentDescription: String? = null,
    title: String,
    message: String,
    okBtnText: String? = null,
    closeBtnText: String = stringResource(id = R.string.close),
    onDismissRequest: () -> Unit = {},
    onOkButtonClick: () -> Unit = {}
) {
    Column(Modifier.background(MaterialTheme.colorScheme.surface)) {

        var graphicVisible by remember { mutableStateOf(false) }

        LaunchedEffect(Unit) { graphicVisible = true }

        AnimatedVisibility(
            visible = graphicVisible,
            enter = expandVertically(
                animationSpec = spring(stiffness = Spring.StiffnessMediumLow),
                expandFrom = Alignment.CenterVertically,
            )
        ) {
            Image(
                painter = painter,
                contentDescription = contentDescription,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(180.dp),
                contentScale = ContentScale.FillWidth
            )
        }

        Column(
            modifier = Modifier.padding(16.dp)
        ) {
            Box(modifier = Modifier.height(8.dp))
            Text(
                text = title,
                style = MaterialTheme.typography.titleLarge,
                fontWeight = FontWeight.Bold,
            )
            Box(modifier = Modifier.height(8.dp))
            Text(text = message)
        }
        Row(
            modifier = Modifier.height(IntrinsicSize.Min)
        ) {
            Box(
                modifier = Modifier
                    .padding(8.dp)
                    .clip(RoundedCornerShape(8.dp))
                    .clickable { onDismissRequest() }
                    .weight(1f)
                    .padding(vertical = 20.dp),
                contentAlignment = Alignment.Center
            ) {
                Text(text = closeBtnText.uppercase(), fontWeight = FontWeight.Bold)
            }
            okBtnText?.let {
                Box(
                    modifier = Modifier
                        .padding(vertical = 10.dp)
                        .width(2.dp)
                        .fillMaxHeight()
                        .background(
                            MaterialTheme.colorScheme.onSurface.copy(alpha = .08f),
                            shape = RoundedCornerShape(10.dp)
                        )
                )

                Box(
                    modifier = Modifier
                        .padding(8.dp)
                        .clip(RoundedCornerShape(8.dp))
                        .clickable {
                            onDismissRequest()
                            onOkButtonClick()
                        }
                        .weight(1f)
                        .padding(vertical = 20.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = okBtnText.uppercase(),
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.primary
                    )
                }
            }
        }
    }
}