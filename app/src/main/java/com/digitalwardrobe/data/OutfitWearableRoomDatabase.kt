package com.digitalwardrobe.data

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.sqlite.db.SupportSQLiteDatabase
import java.util.concurrent.ExecutorService
import java.util.concurrent.Executors

// exportSchema set to false to avoid DB migrations
@Database(entities = [OutfitWearable::class], version = 1, exportSchema = false)
abstract class OutfitWearableRoomDatabase : RoomDatabase() {

    abstract fun outfitWearableDao(): OutfitWearableDao

    companion object {

        @Volatile
        private var INSTANCE: OutfitWearableRoomDatabase? = null

        private const val nThreads: Int = 4
        val databaseWriteExecutor: ExecutorService = Executors.newFixedThreadPool(nThreads)

        private val sRoomDatabaseCallback = object: RoomDatabase.Callback() {
            override fun onCreate(db: SupportSQLiteDatabase) {
                super.onCreate(db)

                /* What happens when the database gets called for the first time? */
                databaseWriteExecutor.execute() {

                }
            }
        }

        /*fun getDatabase(context: Context) : WearableRoomDatabase {
            return INSTANCE ?: synchronized (this) {
                val _INSTANCE = Room.databaseBuilder(
                    context.applicationContext,
                    WearableRoomDatabase::class.java,
                    "wearable_database"
                ).addCallback(sRoomDatabaseCallback).build()
                INSTANCE = _INSTANCE
                _INSTANCE
            }
        }*/

        fun getDatabase(context: Context): OutfitWearableRoomDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    OutfitWearableRoomDatabase::class.java,
                    "outfit_wearable_database"
                ).build()
                INSTANCE = instance
                instance
            }
        }
    }
}