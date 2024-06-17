package it.mmessore.timestableschallenge.ui

import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import kotlinx.coroutines.delay
import java.util.Locale

@Composable
fun RoundButton(
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    text: String,
    uppercase: Boolean = true
) {
    val btnText = if (uppercase)
        text.uppercase(Locale.getDefault())
    else
        text.replaceFirstChar { if (it.isLowerCase()) it.titlecase(Locale.getDefault()) else it.toString() }
    Button(
        onClick = onClick,
        shape = CircleShape,
        modifier = modifier
            .padding(8.dp)
            .clip(CircleShape)
            .background(MaterialTheme.colorScheme.primary)
            .padding(4.dp)
    ) {
        Text(
            text = btnText,
            textAlign = TextAlign.Center,
            style = MaterialTheme.typography.headlineMedium
        )
    }
}

@Preview(showBackground = true)
@Composable
fun RoundButtonPreview() {
    RoundButton(onClick = {}, text = "hello")
}

@Composable
fun DelayedFadeInContent(
    startDelayMillis: Long = 1000,
    endDelayMillis: Long = 1000,
    fadeInDurationMillis: Int = 1000,
    onAnimationEnd: () -> Unit = {},
    content: @Composable () -> Unit
) {
    var visible by remember { mutableStateOf(false) }
    val alpha by animateFloatAsState(
        targetValue = if (visible) 1f else 0f,
        animationSpec = tween(durationMillis = fadeInDurationMillis)
    )

    LaunchedEffect(Unit) {
        delay(startDelayMillis)
        visible = true
        delay(endDelayMillis)
        onAnimationEnd()
    }

    Box(modifier = Modifier.alpha(alpha)) {
        content()
    }
}