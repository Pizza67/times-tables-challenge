package it.mmessore.timestableschallenge.ui.screens

import android.media.MediaPlayer
import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.widthIn
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.State
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import it.mmessore.timestableschallenge.R
import it.mmessore.timestableschallenge.data.Quest
import it.mmessore.timestableschallenge.data.persistency.Round
import it.mmessore.timestableschallenge.ui.DelayedFadeInContent
import it.mmessore.timestableschallenge.ui.Keyboard
import kotlinx.coroutines.delay

@Composable
fun RoundScreen(
    viewModel: RoundViewModel = hiltViewModel(),
    onRoundFinished: (Round?) -> Unit = {},
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current

    val inputVal = viewModel.answer.collectAsState()
    val score = viewModel.score.collectAsState()
    val timeLeft = viewModel.timeLeft.collectAsState()
    val roundState = viewModel.roundState.collectAsState()
    val currentQuest = viewModel.currentQuest.collectAsState()
    val submitAnswer = viewModel.submitAnswer.collectAsState()

    val mediaAnswerCorrect = MediaPlayer.create(context, R.raw.correct)
    val mediaAnswerWrong = MediaPlayer.create(context, R.raw.wrong)

    Column(
        modifier = modifier
            .fillMaxSize()
            .padding(16.dp),
        verticalArrangement = Arrangement.SpaceBetween,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        when(roundState.value) {
            RoundViewModel.RoundState.STARTING -> {
                CountdownAnimation(onCountdownFinished = { viewModel.startRound() })
            }
            RoundViewModel.RoundState.IN_PROGRESS -> {
                RoundPanel(timeLeft, score, currentQuest, inputVal)
                if (submitAnswer.value != RoundViewModel.NO_ANSWER)
                    viewModel.onAnswer(inputVal.value, mediaAnswerCorrect, mediaAnswerWrong)
            }
            RoundViewModel.RoundState.FINISHED -> {
                DelayedFadeInContent (
                    endDelayMillis = 3000,
                    onAnimationEnd = { onRoundFinished(viewModel.finishedRound) }
                ) {
                    Text(
                        text = stringResource(id =
                            when (viewModel.getFinishReason()) {
                                RoundViewModel.FinishReason.TIME_UP -> R.string.game_over
                                RoundViewModel.FinishReason.COMPLETED -> R.string.round_complete
                                else -> -1
                            }
                        ),
                        style = MaterialTheme.typography.displayLarge,
                        textAlign = TextAlign.Center,
                        modifier = Modifier
                            .padding(vertical = 48.dp)
                            .fillMaxWidth()
                    )
                }
            }
        }

        Keyboard(
            onBackspaceClick = { viewModel.onBackspace() },
            onNumberClick = { viewModel.onNumberClick(it) },
            onNextClick = { viewModel.onAnswer(inputVal.value, mediaAnswerCorrect, mediaAnswerWrong) },
            autoConfirm = viewModel.isAutoConfirmEnabled(),
            modifier = Modifier
                .widthIn(max = 600.dp)
                .testTag("keyboard")
        )
    }
}

@Composable
private fun RoundPanel(
    timeLeft: State<Int>,
    score: State<Int>,
    currentQuest: State<Quest>,
    inputVal: State<String>
) {
    Row(
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween,
        modifier = Modifier.fillMaxWidth()
    ) {
        TimeLeft(time = timeLeft.value)
        Score(score = score.value)
    }
    Text(
        text = "${currentQuest.value.op1} x ${currentQuest.value.op2} = ?",
        style = MaterialTheme.typography.displayLarge,
        fontSize = 70.sp,
        textAlign = TextAlign.Center,
        modifier = Modifier
            .padding(vertical = 16.dp)
            .fillMaxWidth()
    )
    BlinkingText(text = inputVal, RoundViewModel.NO_ANSWER)
}

@Composable
fun BlinkingText(text: State<String>, blinkValue: String, modifier: Modifier = Modifier) {
    var visible by remember { mutableStateOf(true) }
    val alpha = rememberInfiniteTransition("blinking cursor").animateFloat(
        initialValue = 1f,
        targetValue = if (visible) 1f else 0f,
        animationSpec = infiniteRepeatable(
            animation = tween(durationMillis = 250),
            repeatMode = RepeatMode.Reverse
        ), label = "blinking cursor"
    )

    LaunchedEffect(text.value) {
        while (text.value == blinkValue) {
            visible = !visible
            delay(500)
        }
        visible = true
    }

    Text(
        text = text.value,
        fontSize = 95.sp,
        style = MaterialTheme.typography.displayLarge,
        modifier = modifier
            .fillMaxWidth()
            .heightIn(min = 150.dp)
            .padding(16.dp)
            .alpha(alpha.value),
        textAlign = TextAlign.Center
    )
}

@Composable
fun TimeLeft(time: Int, modifier: Modifier = Modifier) {
    Row (
        verticalAlignment = Alignment.CenterVertically,
        modifier = modifier
    ) {
        Text(
            text = stringResource(id = R.string.time_remaining),
            style = MaterialTheme.typography.bodyLarge,
            textAlign = TextAlign.End
        )
        Text(
            text = time.toString(),
            style = MaterialTheme.typography.headlineLarge,
            textAlign = TextAlign.Center,
            modifier = Modifier
                .padding(horizontal = 8.dp)
                .testTag("timeLeft")
        )
    }
}

@Composable
fun Score(score: Int, modifier: Modifier = Modifier) {
    Row (
        verticalAlignment = Alignment.CenterVertically,
        modifier = modifier
    ) {
        Text(
            text = stringResource(id = R.string.score),
            style = MaterialTheme.typography.bodyLarge,
            textAlign = TextAlign.End
        )
        Text(
            text = score.toString(),
            style = MaterialTheme.typography.headlineLarge,
            textAlign = TextAlign.Center,
            modifier = Modifier
                .padding(horizontal = 8.dp)
                .testTag("score")
        )
    }
}

@Composable
fun CountdownAnimation(onCountdownFinished: () -> Unit = {}) {
    var count by remember { mutableStateOf(3) }
    val animatedSize = remember { Animatable(0f) }

    LaunchedEffect(key1 = count) {
        while (count > 0) {
            animatedSize.snapTo(0f)
            animatedSize.animateTo(
                targetValue = 1f,
                animationSpec = tween(durationMillis = 1000)
            )
            count--
        }
        onCountdownFinished()
    }

    if (count > 0) {
        Box(
            modifier = Modifier.padding(top = 36.dp),
            contentAlignment = Alignment.Center
        ) {
            Text(
                text = count.toString(),
                fontSize = (animatedSize.value * 200).sp,
                style = MaterialTheme.typography.displayLarge
            )
        }
    }
}
