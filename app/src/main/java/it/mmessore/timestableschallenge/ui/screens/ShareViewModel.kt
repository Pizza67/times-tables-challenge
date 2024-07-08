package it.mmessore.timestableschallenge.ui.screens

import android.graphics.Bitmap
import android.graphics.Color
import android.net.Uri
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.zxing.BarcodeFormat
import com.google.zxing.EncodeHintType
import com.google.zxing.common.BitMatrix
import com.google.zxing.qrcode.QRCodeWriter
import com.google.zxing.qrcode.decoder.ErrorCorrectionLevel
import dagger.hilt.android.lifecycle.HiltViewModel
import it.mmessore.timestableschallenge.data.RoundGenerator
import it.mmessore.timestableschallenge.data.persistency.AppPreferences
import it.mmessore.timestableschallenge.data.persistency.ConstantsImpl
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import java.util.Hashtable
import javax.inject.Inject

@HiltViewModel
class ShareViewModel @Inject constructor(
    private val appPreferences: AppPreferences,
    private val coroutineScope: CoroutineScope
) : ViewModel() {
    private lateinit var roundUrl: String

    private val _roundToPlay: MutableStateFlow<String> = MutableStateFlow(RoundGenerator.serialize(RoundGenerator(appPreferences).generate()))
    val roundToPlay: StateFlow<String> = _roundToPlay
    private val _receivedRound: MutableStateFlow<String?> = MutableStateFlow(null)
    val receivedRound: StateFlow<String?> = _receivedRound
    private val _bitmap = MutableStateFlow<Bitmap?>(null)
    val qrCodeBitmap: StateFlow<Bitmap?> = _bitmap

    fun setReceivedRoundId(receivedRoundId: String?): Boolean {
        val isValid = isValidRoundId(receivedRoundId)
        if (isValid) {
            _roundToPlay.value = receivedRoundId!!
            _receivedRound.value = receivedRoundId
            generateQRCode(createRoundUrl(receivedRoundId), 1024, 1024)
        } else {
            generateQRCode(createRoundUrl(_roundToPlay.value), 1024, 1024)
        }
        return isValid
    }

    private fun isValidRoundId(inputText: String?): Boolean {
        var isValid = false
        if (inputText != null) {
            // Check first if user has input the whole url
            val roundId = Uri.parse(inputText).getQueryParameter(ConstantsImpl.QUERY_PARAM_ROUND_ID) ?: inputText
            isValid = RoundGenerator.isValid(roundId)
        }
        return isValid
    }

    fun getShareUrl() = createRoundUrl(_roundToPlay.value)

    private fun createRoundUrl(roundId: String): String {
        roundUrl = "${ConstantsImpl.CUSTOM_URI_SCHEME}://?${ConstantsImpl.QUERY_PARAM_ROUND_ID}=$roundId"
        return roundUrl
    }

    private fun generateQRCode(text: String, width: Int, height: Int) {
        viewModelScope.launch (context = coroutineScope.coroutineContext) {
            val hints = Hashtable<EncodeHintType, Any>()
            hints[EncodeHintType.CHARACTER_SET] = "UTF-8"
            hints[EncodeHintType.ERROR_CORRECTION] = ErrorCorrectionLevel.H

            val writer = QRCodeWriter()
            val bitMatrix = writer.encode(text, BarcodeFormat.QR_CODE, width, height, hints)
            _bitmap.value = createBitmapFromBitMatrix(bitMatrix)
        }
    }

    private fun createBitmapFromBitMatrix(bitMatrix: BitMatrix): Bitmap {
        val width = bitMatrix.width
        val height = bitMatrix.height
        val pixels = IntArray(width * height)

        for (y in 0 until height) {
            val offset = y * width
            for (x in 0 until width) {pixels[offset + x] = if (bitMatrix[x, y]) Color.BLACK else Color.WHITE
            }
        }

        val bitmap = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888)
        bitmap.setPixels(pixels, 0, width, 0, 0, width, height)
        return bitmap
    }

}