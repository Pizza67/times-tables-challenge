package it.mmessore.timestableschallenge.ui

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.painter.Painter
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import it.mmessore.timestableschallenge.R

@Composable
fun Keyboard(
    onNumberClick: (digit: Char) -> Unit,
    modifier: Modifier = Modifier,
    onBackspaceClick: () -> Unit = {},
    onNextClick: () -> Unit = {}
) {
    Column(
        modifier = modifier
    ) {

        Row(
            modifier = Modifier.fillMaxWidth()
        ) {
            NumberButton(number = 1, onClick = onNumberClick, modifier = Modifier.weight(1f))
            NumberButton(number = 2, onClick = onNumberClick, modifier = Modifier.weight(1f))
            NumberButton(number = 3, onClick = onNumberClick, modifier = Modifier.weight(1f))
        }
        Row(
            modifier = Modifier.fillMaxWidth()
        ) {
            NumberButton(number = 4, onClick = onNumberClick, modifier = Modifier.weight(1f))
            NumberButton(number = 5, onClick = onNumberClick, modifier = Modifier.weight(1f))
            NumberButton(number = 6, onClick = onNumberClick, modifier = Modifier.weight(1f))
        }

        Row(
            modifier = Modifier.fillMaxWidth()
        ) {
            NumberButton(number = 7, onClick = onNumberClick, modifier = Modifier.weight(1f))
            NumberButton(number = 8, onClick = onNumberClick, modifier = Modifier.weight(1f))
            NumberButton(number = 9, onClick = onNumberClick, modifier = Modifier.weight(1f))
        }

        Row(
            modifier = Modifier.fillMaxWidth()
        ) {
            ActionButton(iconPainter = painterResource(id = R.drawable.ic_backspace), contentDescription = "Backspace", onClick = onBackspaceClick, modifier = Modifier.weight(1f))
            NumberButton(number = 0, onClick = onNumberClick, modifier = Modifier.weight(1f))
            ActionButton(iconPainter = painterResource(id = R.drawable.ic_next), contentDescription = "Next", onClick = onNextClick, modifier = Modifier.weight(1f))
        }
    }
}

@Composable
private fun NumberButton(
    number: Int,
    onClick: (digit: Char) -> Unit,
    modifier: Modifier = Modifier
) {
    OutlinedButton(
        onClick = {
            onClick(number.digitToChar())
        },
        modifier = modifier
            .padding(4.dp)
            .aspectRatio(1.4f)
            .padding(4.dp)
    ) {
        Text(
            text = number.toString(),
            fontSize = 30.sp,
            style = MaterialTheme.typography.titleMedium,
            color = MaterialTheme.colorScheme.primary
        )
    }
}

@Composable
private fun ActionButton(
    iconPainter: Painter,
    contentDescription: String,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Button(
        onClick = onClick,
        modifier = modifier
            .padding(4.dp)
            .aspectRatio(1.4f)
            .padding(8.dp)
    ) {
        Icon(painter = iconPainter, contentDescription = contentDescription)
    }
}