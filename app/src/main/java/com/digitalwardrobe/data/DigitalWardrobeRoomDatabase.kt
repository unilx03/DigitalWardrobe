package com.digitalwardrobe.data

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.sqlite.db.SupportSQLiteDatabase
import java.util.concurrent.ExecutorService
import java.util.concurrent.Executors

// exportSchema set to false to avoid DB migrations
@Database(
    entities = [
        Outfit::class,
        Wearable::class,
        OutfitWearable::class,
        MoodboardItem::class,
        DailyOutfit::class,
    ],
    version = 1,
    exportSchema = false
)

abstract class DigitalWardrobeRoomDatabase : RoomDatabase() {

    abstract fun wearableDao(): WearableDao
    abstract fun outfitDao(): OutfitDao
    abstract fun outfitWearableDao(): OutfitWearableDao
    abstract fun moodboardItemDao(): MoodboardItemDao
    abstract fun dailyOutfitDao(): DailyOutfitDao

    companion object {

        @Volatile
        private var INSTANCE: DigitalWardrobeRoomDatabase? = null

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

        fun getDatabase(context: Context): DigitalWardrobeRoomDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    DigitalWardrobeRoomDatabase::class.java,
                    "digital_wardrobe_database"
                )
                    .addCallback(object : RoomDatabase.Callback() {
                        override fun onOpen(db: SupportSQLiteDatabase) {
                            super.onOpen(db)
                            db.execSQL("PRAGMA foreign_keys=ON;")
                        }
                    })
                    .build()
                INSTANCE = instance
                instance
            }
        }

    }
}