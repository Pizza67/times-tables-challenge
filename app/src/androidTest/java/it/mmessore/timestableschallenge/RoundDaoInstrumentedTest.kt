package it.mmessore.timestableschallenge

import androidx.room.Room
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.platform.app.InstrumentationRegistry
import it.mmessore.timestableschallenge.data.persistency.RoundDao
import it.mmessore.timestableschallenge.data.persistency.AppDatabase
import it.mmessore.timestableschallenge.data.persistency.Round
import kotlinx.coroutines.runBlocking
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class RoundDaoInstrumentedTest {

    private lateinit var roundDao: RoundDao
    private lateinit var db: AppDatabase

    @Before
    fun setup() {
        // Inizializza il database in memoria per i test
        db = Room.inMemoryDatabaseBuilder(
            InstrumentationRegistry.getInstrumentation().context,
            AppDatabase::class.java
        ).allowMainThreadQueries().build()
        roundDao = db.roundDao() // Ottieni l'istanza del DAO
    }

    @After
    fun teardown() {
        db.close()
    }

    @Test
    fun insertRoundIfBetterOrEquals_noExistingRound_insertsNewRound() = runBlocking {
        val newRound = Round("round1", System.currentTimeMillis(), 10, 30)
        roundDao.insertRoundIfBetterOrEquals(newRound, true)

        val retrievedRound = roundDao.getRound("round1")
        assertEquals(newRound, retrievedRound)
    }

    @Test
    fun insertRoundIfBetterOrEquals_existingRoundWithLowerScore_updatesRound() = runBlocking {
        val existingRound = Round("round1", System.currentTimeMillis(), 5, 20)
        roundDao.insertRound(existingRound)

        val newRound = Round("round1", System.currentTimeMillis(), 10, 30)
        roundDao.insertRoundIfBetterOrEquals(newRound, true)

        val retrievedRound = roundDao.getRound("round1")
        assertEquals(newRound, retrievedRound)
    }

    @Test
    fun insertRoundIfBetterOrEquals_existingRoundWithHigherScore_doesNotUpdateRound() = runBlocking {
        val existingRound = Round("round1", System.currentTimeMillis(), 15,40)
        roundDao.insertRound(existingRound)

        val newRound = Round("round1", System.currentTimeMillis(), 10, 30)
        roundDao.insertRoundIfBetterOrEquals(newRound, true)

        val retrievedRound = roundDao.getRound("round1")
        assertEquals(existingRound, retrievedRound)
    }

    @Test
    fun insertRoundIfBetterOrEquals_existingRoundWithEqualScoreButLessTimeLeft_updatesRound() = runBlocking {
        val existingRound = Round("round1", System.currentTimeMillis(), 10, 20)
        roundDao.insertRound(existingRound)

        val newRound = Round("round1", System.currentTimeMillis(), 10, 30)
        roundDao.insertRoundIfBetterOrEquals(newRound, true)

        val retrievedRound = roundDao.getRound("round1")
        assertEquals(newRound, retrievedRound)
    }
}