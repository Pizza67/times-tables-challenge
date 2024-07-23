package it.mmessore.timestableschallenge

import android.content.Context
import androidx.activity.ComponentActivity
import androidx.compose.ui.graphics.ImageBitmap
import androidx.compose.ui.graphics.asAndroidBitmap
import androidx.compose.ui.test.ExperimentalTestApi
import androidx.compose.ui.test.assertContentDescriptionEquals
import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.assertIsEnabled
import androidx.compose.ui.test.assertIsNotDisplayed
import androidx.compose.ui.test.assertIsNotEnabled
import androidx.compose.ui.test.captureToImage
import androidx.compose.ui.test.hasContentDescription
import androidx.compose.ui.test.junit4.createAndroidComposeRule
import androidx.compose.ui.test.onNodeWithTag
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.performClick
import androidx.compose.ui.test.performTextInput
import androidx.lifecycle.lifecycleScope
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.google.zxing.BinaryBitmap
import com.google.zxing.MultiFormatReader
import com.google.zxing.RGBLuminanceSource
import com.google.zxing.common.HybridBinarizer
import dagger.hilt.android.testing.HiltAndroidRule
import dagger.hilt.android.testing.HiltAndroidTest
import it.mmessore.timestableschallenge.data.FakeRoundGenerator
import it.mmessore.timestableschallenge.data.persistency.FakeAppPreferences
import it.mmessore.timestableschallenge.data.persistency.FakeConstants
import it.mmessore.timestableschallenge.ui.screens.ShareScreen
import it.mmessore.timestableschallenge.ui.screens.ShareViewModel
import it.mmessore.timestableschallenge.ui.theme.AppTheme
import kotlinx.coroutines.CoroutineScope
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import javax.inject.Inject

@HiltAndroidTest
@RunWith(AndroidJUnit4::class)
class ShareScreenInstrumentedTest {
    @get:Rule(order = 0)
    var hiltRule = HiltAndroidRule(this)

    @get:Rule(order = 1)
    val composeTestRule = createAndroidComposeRule<ComponentActivity>()
    private lateinit var context: Context
    @Inject lateinit var fakeRoundGenerator: FakeRoundGenerator
    private lateinit var fakePreferences: FakeAppPreferences
    private lateinit var fakeConstants: FakeConstants
    private lateinit var viewModel: ShareViewModel
    private lateinit var coroutineScope: CoroutineScope
    private lateinit var roundId: String

    @Before
    fun setup() {
        hiltRule.inject()
        context = composeTestRule.activity
        coroutineScope = composeTestRule.activity.lifecycleScope
        fakePreferences = FakeAppPreferences()
        fakeConstants = FakeConstants()
        roundId = fakeRoundGenerator.getRoundId()
        viewModel = ShareViewModel(fakeConstants, fakeRoundGenerator, fakePreferences, coroutineScope)
    }

    private fun setShareScreen(
        receivedRoundId: String? = null,
        onStartRoundButtonClick: (String) -> Unit = {},
        viewModel: ShareViewModel = this.viewModel
    ) {
        composeTestRule.setContent {
            AppTheme {
                ShareScreen(receivedRoundId, onStartRoundButtonClick, viewModel)
            }
        }
        composeTestRule.waitForIdle()
    }

    private fun ImageBitmap.toIntArray(): IntArray {
        val bitmap = asAndroidBitmap()
        val intArray = IntArray(bitmap.width * bitmap.height)
        bitmap.getPixels(intArray, 0, bitmap.width, 0, 0, bitmap.width, bitmap.height)
        return intArray
    }

    private fun getTextFromQRImage(capturedImage: ImageBitmap): String? {
        val source = RGBLuminanceSource(
            capturedImage.width,
            capturedImage.height,
            capturedImage.toIntArray()
        )
        val binaryBitmap = BinaryBitmap(HybridBinarizer(source))
        return try {
            MultiFormatReader().decode(binaryBitmap).text
        } catch (e:Exception) {
            null
        }
    }

    @Test
    fun startRound_asInitiator() {
        setShareScreen()
        composeTestRule.onNodeWithText(context.getString(R.string.received_round_code_quest)).assertIsDisplayed()
        composeTestRule.onNodeWithTag("qrcode").assertIsDisplayed()
        // Check the QRcode contains the correct information
        val qrImage = composeTestRule.onNodeWithTag("qrcode").captureToImage()
        assert(getTextFromQRImage(qrImage) == viewModel.createRoundUrl(roundId))
    }

    @OptIn(ExperimentalTestApi::class)
    @Test
    fun startRound_asGuestInsertingCode_isValidCode() {
        setShareScreen()
        val receivedRoundId = fakeRoundGenerator.getNewRoundId()
        composeTestRule.apply {
            onNodeWithText(context.getString(R.string.received_round_code_quest)).performClick()
            onNodeWithTag("inputCode").performTextInput(receivedRoundId)
            // Wait for validation
            waitUntilAtLeastOneExists(hasContentDescription(context.getString(R.string.round_code_valid)), 5000)
            onNodeWithText(receivedRoundId).assertIsDisplayed()
            onNodeWithText(receivedRoundId).assertIsNotEnabled()
            val qrImage = composeTestRule.onNodeWithTag("qrcode").captureToImage()
            assert(getTextFromQRImage(qrImage) == viewModel.createRoundUrl(receivedRoundId))
        }
    }

    @Test
    fun startRound_asGuestInsertingCode_isNotValidCode() {
        val invalidRoundId = "òklefjiljsdfilaweoljfdp="
        setShareScreen()
        composeTestRule.apply {
            onNodeWithText(context.getString(R.string.received_round_code_quest)).performClick()
            onNodeWithTag("inputCode").performTextInput(invalidRoundId)
            onNodeWithText(invalidRoundId).assertIsDisplayed()
                .assertContentDescriptionEquals(context.getString(R.string.round_code_invalid))
                .assertIsEnabled()
        }
        // The QR code doesn't have to change when the user enters an invalid code
        val qrImage = composeTestRule.onNodeWithTag("qrcode").captureToImage()
        assert(getTextFromQRImage(qrImage) == viewModel.createRoundUrl(roundId))
    }

    @OptIn(ExperimentalTestApi::class)
    @Test
    fun startRound_asGuestInsertingCode_isValidUrl() {
        setShareScreen()
        val receivedRoundId = fakeRoundGenerator.getNewRoundId()
        val receivedRoundUrl = viewModel.createRoundUrl(receivedRoundId)
        composeTestRule.apply {
            onNodeWithText(context.getString(R.string.received_round_code_quest)).performClick()
            onNodeWithTag("inputCode").performTextInput(receivedRoundUrl)
            // Wait for validation
            waitUntilAtLeastOneExists(hasContentDescription(context.getString(R.string.round_code_valid)), 5000)
            // In this case the input text field just contains the id
            onNodeWithText(receivedRoundId).assertIsDisplayed()
            onNodeWithText(receivedRoundId).assertIsNotEnabled()
            val qrImage = composeTestRule.onNodeWithTag("qrcode").captureToImage()
            assert(getTextFromQRImage(qrImage) == receivedRoundUrl)
        }
    }

    @Test
    fun startRound_asGuestInsertingCode_isNotValidUrl() {
        val invalidRoundId = "òklefjiljsdfilaweoljfdp="
        val invalidRoundUrl = viewModel.createRoundUrl(invalidRoundId)
        setShareScreen()
        composeTestRule.apply {
            onNodeWithText(context.getString(R.string.received_round_code_quest)).performClick()
            onNodeWithTag("inputCode").performTextInput(invalidRoundUrl)
            onNodeWithText(invalidRoundUrl).assertIsDisplayed()
                .assertContentDescriptionEquals(context.getString(R.string.round_code_invalid))
                .assertIsEnabled()
        }
        // The QR code doesn't have to change when the user enters an invalid code
        val qrImage = composeTestRule.onNodeWithTag("qrcode").captureToImage()
        assert(getTextFromQRImage(qrImage) == viewModel.createRoundUrl(roundId))
    }

    @OptIn(ExperimentalTestApi::class)
    @Test
    fun startRound_asGuestScanningCode_isValidCode() {
        val receivedRoundId = fakeRoundGenerator.getNewRoundId()
        setShareScreen(receivedRoundId = receivedRoundId)
        composeTestRule.apply {
            // wait for validation
            waitUntilAtLeastOneExists(hasContentDescription(context.getString(R.string.round_code_valid)), 5000)
            onNodeWithText(receivedRoundId).assertIsDisplayed()
            onNodeWithText(receivedRoundId).assertIsNotEnabled()
            val qrImage = composeTestRule.onNodeWithTag("qrcode").captureToImage()
            assert(getTextFromQRImage(qrImage) == viewModel.createRoundUrl(receivedRoundId))
        }
    }

    @Test
    fun startRound_asGuestScanningCode_isNotValidCode() {
        val invalidRoundId = "òklefjiljsdfilaweoljfdp="
        setShareScreen(receivedRoundId = invalidRoundId)
        // In case of scanned code the received round is ignored
        composeTestRule.onNodeWithText(invalidRoundId).assertIsNotDisplayed()
        // The QR code doesn't have to change when the user scanned an invalid code
        val qrImage = composeTestRule.onNodeWithTag("qrcode").captureToImage()
        assert(getTextFromQRImage(qrImage) == viewModel.createRoundUrl(roundId))
    }
}