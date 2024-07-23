package it.mmessore.timestableschallenge.utils

import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.runtime.Composable
import androidx.compose.ui.test.junit4.AndroidComposeTestRule
import androidx.lifecycle.lifecycleScope
import it.mmessore.timestableschallenge.data.FakeRoundGenerator
import it.mmessore.timestableschallenge.data.FakeRepository
import it.mmessore.timestableschallenge.data.Quest
import it.mmessore.timestableschallenge.data.RoundGeneratorImpl
import it.mmessore.timestableschallenge.data.persistency.FakeAppPreferences
import it.mmessore.timestableschallenge.data.persistency.FakeConstants
import it.mmessore.timestableschallenge.ui.screens.RoundViewModel
import kotlinx.coroutines.CoroutineScope
import org.junit.rules.TestRule

fun <R : TestRule, A : ComponentActivity> AndroidComposeTestRule<R, A>.overrideActivityContent(
    content: @Composable () -> Unit
) {
    this.activity.runOnUiThread {
        this.activity.setContent {
            content()
        }
    }
}

fun fakeRoundViewModel(
    activity: ComponentActivity,
    fakeRepository: FakeRepository = FakeRepository(activity),
    fakePreferences: FakeAppPreferences = FakeAppPreferences(),
    fakeRoundGenerator: FakeRoundGenerator = FakeRoundGenerator(fakePreferences),
    fakeConstants: FakeConstants = FakeConstants(),
    coroutineScope: CoroutineScope = activity.lifecycleScope
): RoundViewModel {
    return RoundViewModel(
        fakeRepository,
        fakeRoundGenerator,
        fakePreferences,
        fakeConstants,
        coroutineScope
    )
}