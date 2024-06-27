package it.mmessore.timestableschallenge.ui.screens

import android.graphics.Bitmap
import android.graphics.Color
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.zxing.BarcodeFormat
import com.google.zxing.EncodeHintType
import com.google.zxing.common.BitMatrix
import com.google.zxing.qrcode.QRCodeWriter
import com.google.zxing.qrcode.decoder.ErrorCorrectionLevel
import dagger.hilt.android.lifecycle.HiltViewModel
import it.mmessore.timestableschallenge.data.RoundGenerator
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import java.util.Hashtable
import javax.inject.Inject

@HiltViewModel
class ShareViewModel @Inject constructor(
    private val coroutineScope: CoroutineScope
) : ViewModel() {
    private lateinit var roundUrl: String

    private val _sharedRoundId: MutableStateFlow<String?> = MutableStateFlow(null)
    val sharedRoundId: StateFlow<String?> = _sharedRoundId
    private val _roundId: MutableStateFlow<String> = MutableStateFlow("")
    val roundId: StateFlow<String> = _roundId
    private val _bitmap = MutableStateFlow<Bitmap?>(null)
    val qrCodeBitmap: StateFlow<Bitmap?> = _bitmap

    init {
        _roundId.value = RoundGenerator.serialize(RoundGenerator().generate())
    }

    fun setReceivedRoundId(roundId: String?) {
        // TODO: Validate roundId
        if (roundId != null) {
            _sharedRoundId.value = roundId
            _roundId.value = roundId
        }
        generateQRCode(generateRoundUrl(_roundId.value), 1024, 1024)
    }

    private fun generateRoundUrl(roundId: String): String {
        roundUrl = "ttchallenge://?roundId=$roundId"
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