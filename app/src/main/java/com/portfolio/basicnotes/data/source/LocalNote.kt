package com.portfolio.basicnotes.data.source

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(
    tableName = "note"
)
data class LocalNote(
    @PrimaryKey(autoGenerate = true)
    val id: Int = 0,
    val title: String,
    val content: String,
    val color: Int
)