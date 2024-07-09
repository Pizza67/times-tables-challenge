package it.mmessore.timestableschallenge.ui.screens

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
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
fun HomeScreen(
    onStartButtonClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier.fillMaxSize().padding(16.dp),
        verticalArrangement = Arrangement.SpaceBetween
    ) {
        Image(
            painter = painterResource(id = R.drawable.app_icon),
            contentDescription = null,
            Modifier.clip(MaterialTheme.shapes.small)
        )
        Text(
            text = stringResource(id = R.string.home_intro),
            style = MaterialTheme.typography.titleLarge,
            textAlign = TextAlign.Center
        )
        RoundButton(
            onClick = onStartButtonClick,
            text = stringResource(id = R.string.start_button),
            modifier = Modifier
                .padding(vertical = 16.dp)
                .align(alignment = Alignment.CenterHorizontally)
        )

    }
}
