package com.portfolio.basicnotes.data.source

import androidx.room.Database
import androidx.room.RoomDatabase
import androidx.room.TypeConverter
import androidx.room.TypeConverters
import java.time.LocalDate
import java.time.format.DateTimeFormatter

@Database(entities = [LocalNote::class], version = 1)
@TypeConverters(Converters::class)
abstract class BasicNotesDatabase : RoomDatabase(){

    abstract fun noteDao(): NoteDao
}

// Date converter class for the database. Enables conversion between Long and LocalDate.
class Converters {
    @TypeConverter
    fun fromLocalDate(date: LocalDate?): String? {
        return date?.format(DateTimeFormatter.ISO_LOCAL_DATE)
    }

    @TypeConverter
    fun toLocalDate(dateString: String?): LocalDate? {
        return dateString?.let {
            LocalDate.parse(it, DateTimeFormatter.ISO_LOCAL_DATE)
        }
    }
}