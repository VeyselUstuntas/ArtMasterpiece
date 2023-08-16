package com.vustuntas.artbookfragment.model

import androidx.room.Database
import androidx.room.RoomDatabase
import com.vustuntas.artbookfragment.Art

@Database(entities = arrayOf(Art::class), version = 1)
abstract class ArtDB : RoomDatabase() {
    abstract fun artDao() : ArtDao
}