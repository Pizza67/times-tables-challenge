package it.mmessore.timestableschallenge.ui.screens

import android.content.Intent
import android.graphics.Bitmap
import androidx.compose.animation.Crossfade
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Share
import androidx.compose.material.icons.filled.Warning
import androidx.compose.material3.Card
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
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
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import it.mmessore.timestableschallenge.R
import it.mmessore.timestableschallenge.ui.RoundButton
import kotlinx.coroutines.delay

@Composable
fun ShareScreen(
    receivedRoundId: String? = null,
    onStartRoundButtonClick: (String) -> Unit,
    viewModel: ShareViewModel = hiltViewModel(),
    modifier: Modifier = Modifier
) {

    val qrCodeBitmap = viewModel.qrCodeBitmap.collectAsState()
    val roundToPlay = viewModel.roundToPlay.collectAsState()
    val receivedRound = viewModel.receivedRound.collectAsState()

    LaunchedEffect(receivedRoundId) {
        viewModel.setReceivedRoundId(receivedRoundId)
    }

    Column(
        modifier = modifier
            .fillMaxSize()
            .padding(16.dp),
        verticalArrangement = Arrangement.SpaceBetween,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(
            text = stringResource(id = R.string.menu_share_new_game),
            style = MaterialTheme.typography.displayMedium,
            textAlign = TextAlign.Center,
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 16.dp)
        )
        Text (
            text = stringResource(id = R.string.share_round_desc),
            style = MaterialTheme.typography.titleLarge,
            textAlign = TextAlign.Center
        )
        QRCard(
            receivedRound = receivedRound,
            textToShare = viewModel.getShareUrl(),
            qrCodeBitmap = qrCodeBitmap,
            onInputSharedRoundId = viewModel::setReceivedRoundId,
            modifier = modifier
        )
        RoundButton(
            onClick = { onStartRoundButtonClick(roundToPlay.value) },
            text = stringResource(id = R.string.start_button)
        )
    }
}

@Composable
private fun QRCard(
    receivedRound: State<String?>,
    textToShare: String,
    qrCodeBitmap: State<Bitmap?>,
    onInputSharedRoundId: (String) -> Boolean,
    modifier: Modifier
) {
    val context = LocalContext.current
    Card(
        modifier = modifier
            .fillMaxWidth()
            .padding(vertical = 8.dp)
    ) {
        Column(
            verticalArrangement = Arrangement.SpaceBetween,
            horizontalAlignment = Alignment.CenterHorizontally,
            modifier = Modifier.padding(16.dp)
        ) {
            QRCodeImage(
                bitmap = qrCodeBitmap.value,
                modifier = Modifier.size(250.dp)
            )
            Spacer(modifier = Modifier.height(16.dp))
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween,
                modifier = Modifier.fillMaxWidth()
            ) {
                Text(
                    text = stringResource(id = R.string.share_round_howto),
                    style = MaterialTheme.typography.bodyLarge,
                    modifier = Modifier
                        .weight(1f)
                )
                Spacer(modifier = Modifier.width(16.dp))
                IconButton(
                    onClick = {
                        val sendIntent: Intent = Intent().apply {
                            action = Intent.ACTION_SEND
                            putExtra(Intent.EXTRA_TEXT, textToShare)
                            type = "text/plain"
                        }
                        context.startActivity(Intent.createChooser(sendIntent, null))
                    },
                    modifier = Modifier
                        .size(52.dp)
                        .clip(CircleShape)
                        .background(MaterialTheme.colorScheme.primary)
                ) {
                    Icon(
                        imageVector = Icons.Filled.Share,
                        contentDescription = "share",
                        tint = MaterialTheme.colorScheme.surface
                    )
                }
            }
            Spacer(modifier = Modifier.height(8.dp))
            RoundCodeRow(receivedRound, onInputSharedRoundId)
        }
    }
}

@Composable
private fun RoundCodeRow(
    receivedRound: State<String?>,
    onRoundIdValidation: (String) -> Boolean,
    modifier: Modifier = Modifier
) {
    var showTextField by remember { mutableStateOf(false) }
    var enableTextField by remember { mutableStateOf(true) }
    var inputText by remember { mutableStateOf(receivedRound.value ?: "") }
    var isValid by remember { mutableStateOf(false) }

    LaunchedEffect(receivedRound.value) {
        inputText = receivedRound.value ?: ""
        enableTextField = receivedRound.value == null
    }

    LaunchedEffect(inputText) {
        delay(500)
        isValid = onRoundIdValidation(inputText)
    }

    Crossfade(
        targetState = (showTextField || !enableTextField),
        label = "showInputTextField",
        modifier = modifier.fillMaxWidth()
    ) { showOutlinedTextField ->
        if (showOutlinedTextField) {
            OutlinedTextField(
                value = inputText,
                onValueChange = { inputText = it },
                label = { Text(stringResource(R.string.round_code_received)) },
                enabled = enableTextField,
                singleLine = true,
                isError = !isValid,
                trailingIcon = {
                    if (inputText.isNotEmpty()) {
                        Icon(
                            imageVector = if (isValid) Icons.Filled.CheckCircle else Icons.Filled.Warning,
                            contentDescription = if (isValid) "Valid Round ID" else "Invalid Round ID",
                            tint = if (isValid) MaterialTheme.colorScheme.primary else Color.Red
                        )
                    }
                },
                modifier = Modifier.fillMaxWidth()
            )
        } else {
            Text(
                text = stringResource(R.string.received_round_code_quest),
                style = MaterialTheme.typography.bodyLarge.copy(fontWeight = FontWeight.Bold),
                modifier = Modifier.clickable { showTextField = true }
            )
        }
    }
}

@Composable
fun QRCodeImage(bitmap: Bitmap?, modifier: Modifier = Modifier) {
    bitmap?.let {
        Image(
            bitmap = bitmap.asImageBitmap(),
            contentDescription = "QR Code",
            modifier = modifier
        )
    }
}