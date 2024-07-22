package it.mmessore.timestableschallenge.ui.screens

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
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
import androidx.compose.ui.unit.dp
import it.mmessore.timestableschallenge.R
import it.mmessore.timestableschallenge.ui.RoundButton
import it.mmessore.timestableschallenge.ui.ScreenContainer

@Composable
fun HomeScreen(
    onStartButtonClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    ScreenContainer(
        modifier = modifier,
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.SpaceBetween
    ) {
        Image(
            painter = painterResource(id = R.drawable.app_icon),
            contentDescription = null,
            contentScale = ContentScale.Crop,
            modifier = Modifier.clip(MaterialTheme.shapes.small).weight(1f)
        )
        Spacer(modifier = Modifier.height(16.dp))
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
