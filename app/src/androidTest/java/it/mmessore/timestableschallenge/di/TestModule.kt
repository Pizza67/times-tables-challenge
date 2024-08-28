package it.mmessore.timestableschallenge.di

import android.content.Context
import dagger.Module
import dagger.Provides
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import dagger.hilt.testing.TestInstallIn
import it.mmessore.timestableschallenge.data.AppRepository
import it.mmessore.timestableschallenge.data.FakeRepository
import it.mmessore.timestableschallenge.data.FakeRoundGenerator
import it.mmessore.timestableschallenge.data.RoundGenerator
import it.mmessore.timestableschallenge.data.persistency.AppPreferences
import it.mmessore.timestableschallenge.data.persistency.Constants
import it.mmessore.timestableschallenge.data.persistency.FakeAppPreferences
import it.mmessore.timestableschallenge.data.persistency.FakeConstants
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import javax.inject.Singleton

@Module
@TestInstallIn(
    components = [SingletonComponent::class],
    replaces = [ApplicationModule::class]
)
object TestModule {
    @Provides
    fun provideAppRepository(@ApplicationContext context: Context): AppRepository {
        return FakeRepository(context)
    }

    @Provides
    @Singleton
    fun provideAppPreferences(): AppPreferences {
        return FakeAppPreferences()
    }

    @Provides
    @Singleton
    fun provideConstants(): Constants {
        return FakeConstants()
    }

    @Provides
    @Singleton
    fun provideRoundGenerator(appPreferences: AppPreferences): RoundGenerator {
        return FakeRoundGenerator(appPreferences)
    }

    @Singleton
    @Provides
    fun providesCoroutineScope() = CoroutineScope(Dispatchers.IO)
}