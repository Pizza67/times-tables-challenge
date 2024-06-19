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
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import it.mmessore.timestableschallenge.R
import it.mmessore.timestableschallenge.ui.theme.AppTheme
import java.text.NumberFormat
import java.util.Locale

@Composable
fun StatsScreen(
    modifier: Modifier = Modifier
){
    Column(
        modifier = modifier
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
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 16.dp)
        ) {
            Image(
                painter = painterResource(R.drawable.img_level_champion),
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
                    formatNumber(149.toDouble()),
                    Modifier.padding(vertical = 8.dp)
                )
                Stat(stringResource(
                    id = R.string.stat_total_score),
                    formatNumber(15259.toDouble()),
                    Modifier.padding(vertical = 8.dp)
                )
                Stat(stringResource(
                    id = R.string.stat_avg_score),
                    formatNumber(15.3),
                    Modifier.padding(vertical = 8.dp)
                )
            }
        }
    }
}

private fun formatNumber(number: Double, locale: Locale = Locale.getDefault()): String {
    val formatter = NumberFormat.getNumberInstance(locale)
    formatter.maximumFractionDigits = 1
    return formatter.format(number)
}

@Composable
fun Stat(title: String, stat: String, modifier: Modifier) {
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

@Preview(showBackground = true, showSystemUi = true)
@Composable
fun StatsScreenPreview() {
    AppTheme {
        StatsScreen()
    }
}