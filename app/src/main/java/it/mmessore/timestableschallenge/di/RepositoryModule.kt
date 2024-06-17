package it.mmessore.timestableschallenge.di

import android.content.Context
import androidx.room.Room
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import it.mmessore.timestableschallenge.data.RoundRepository
import it.mmessore.timestableschallenge.data.persistency.RoundDatabase
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object RepositoryModule {
    @Provides
    @Singleton
    fun provideRoundDatabase(@ApplicationContext context: Context): RoundDatabase {
        return Room.databaseBuilder(
            context,
            RoundDatabase::class.java,
            "app_database"
        ).build()
    }

    @Provides
    @Singleton
    fun provideRoundRepository(database: RoundDatabase): RoundRepository {
        return RoundRepository(database.roundDao())
    }
}