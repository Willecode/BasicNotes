package com.portfolio.basicnotes.di

import android.content.Context
import androidx.room.Room
import com.portfolio.basicnotes.data.DefaultNoteRepository
import com.portfolio.basicnotes.data.NoteRepository
import com.portfolio.basicnotes.data.source.BasicNotesDatabase
import com.portfolio.basicnotes.data.source.NoteDao
import dagger.Binds
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.components.ViewModelComponent
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(ViewModelComponent::class)
abstract class RepositoryModule {
    @Binds
    abstract fun bindEntryRepository(defaultNoteRepository: DefaultNoteRepository): NoteRepository
}

@Module
@InstallIn(SingletonComponent::class)
object DatabaseModule {
    @Singleton
    @Provides
    fun provideDataBase(@ApplicationContext context: Context): BasicNotesDatabase {
        return Room.databaseBuilder(
            context.applicationContext,
            BasicNotesDatabase::class.java,
            "notes.db"
        ).build()
    }

    @Provides
    fun provideNoteDao(database: BasicNotesDatabase): NoteDao = database.noteDao()
}