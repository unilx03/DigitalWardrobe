package com.digitalwardrobe.data

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.sqlite.db.SupportSQLiteDatabase

// exportSchema set to false to avoid DB migrations
@Database(
    version = 1,
    entities = [
        Outfit::class,
        Wearable::class,
        OutfitWearable::class,
        MoodboardItem::class,
        DailyOutfit::class,
        GeofenceVisit::class
    ],
    exportSchema = false
)

abstract class DigitalWardrobeRoomDatabase : RoomDatabase() {

    abstract fun wearableDao(): WearableDao
    abstract fun outfitDao(): OutfitDao
    abstract fun outfitWearableDao(): OutfitWearableDao
    abstract fun moodboardItemDao(): MoodboardItemDao
    abstract fun dailyOutfitDao(): DailyOutfitDao
    abstract fun geofenceVisitDao(): GeofenceVisitDao

    companion object {

        @Volatile
        private var INSTANCE: DigitalWardrobeRoomDatabase? = null

        fun getDatabase(context: Context): DigitalWardrobeRoomDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    DigitalWardrobeRoomDatabase::class.java,
                    "digital_wardrobe_database"
                )
                    .fallbackToDestructiveMigration()
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