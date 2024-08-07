package it.mmessore.timestableschallenge.di

import android.content.Context
import androidx.room.Room
import androidx.room.migration.Migration
import androidx.sqlite.db.SupportSQLiteDatabase
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import it.mmessore.timestableschallenge.data.AppRepository
import it.mmessore.timestableschallenge.data.AppRepositoryImpl
import it.mmessore.timestableschallenge.data.RoundGenerator
import it.mmessore.timestableschallenge.data.RoundGeneratorImpl
import it.mmessore.timestableschallenge.data.persistency.AppDatabase
import it.mmessore.timestableschallenge.data.persistency.AppPreferences
import it.mmessore.timestableschallenge.data.persistency.AppPreferencesImpl
import it.mmessore.timestableschallenge.data.persistency.Constants
import it.mmessore.timestableschallenge.data.persistency.ConstantsImpl
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object ApplicationModule {
    @Provides
    @Singleton
    fun provideRoundDatabase(@ApplicationContext context: Context): AppDatabase {
        return Room.databaseBuilder(
            context,
            AppDatabase::class.java,
            "app_database"
        ).addMigrations(
            object : Migration(1, 2) {
                override fun migrate(database: SupportSQLiteDatabase) {
                    database.execSQL(
                        """
                        CREATE TABLE Achievement (
                            id INTEGER PRIMARY KEY NOT NULL,
                            name TEXT NOT NULL,
                            avgScore REAL NOT NULL,
                            numRounds INTEGER NOT NULL,
                            timestamp INTEGER NOT NULL
                        )
                        """
                )
            }
        }
        ).build()
    }

    @Provides
    @Singleton
    fun provideAppRepository(@ApplicationContext context: Context, appPreferences: AppPreferences, database: AppDatabase): AppRepository {
        return AppRepositoryImpl(context, appPreferences, database.roundDao(), database.achievementDao())
    }

    @Provides
    @Singleton
    fun provideAppPreferences(@ApplicationContext context: Context): AppPreferences {
        return AppPreferencesImpl(context)
    }

    @Provides
    @Singleton
    fun provideRoundGenerator(appPreferences: AppPreferences): RoundGenerator {
        return RoundGeneratorImpl(appPreferences)
    }

    @Singleton
    @Provides
    fun providesCoroutineScope() = CoroutineScope(Dispatchers.IO)

    @Provides
    @Singleton
    fun provideConstants(@ApplicationContext context: Context): Constants {
        val constants = ConstantsImpl
        constants.init(context)
        return constants
    }
}