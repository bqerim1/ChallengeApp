package com.challengeapp.data.local

import androidx.room.Database
import androidx.room.RoomDatabase

@Database(
    entities = [FormItemEntity::class, ResponseEntity::class],
    version = 1,
    exportSchema = false,
)
abstract class AppDatabase : RoomDatabase() {
    abstract fun formDao(): FormDao
}
