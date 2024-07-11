package com.portfolio.basicnotes.data.source

import androidx.room.Database
import androidx.room.RoomDatabase

@Database(entities = [LocalNote::class], version = 1)
abstract class BasicNotesDatabase : RoomDatabase(){

    abstract fun noteDao(): NoteDao
}