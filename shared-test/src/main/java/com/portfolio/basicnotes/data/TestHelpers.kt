package com.portfolio.basicnotes.data

import com.portfolio.basicnotes.data.source.LocalNote

fun LocalNote.equalsIgnoreId(other: LocalNote): Boolean {
    return (
            this.content == other.content
                    && this.title == other.title
                    && this.color == other.color
                    && this.date == other.date
            )
}

fun Note.toLocalNote(): LocalNote {
    return LocalNote(
        id = this.id,
        title = this.title,
        content = this.content,
        color = this.noteColor,
        date = this.date
    )
}

/**
 * Checks if [a] and [b] have the same LocalNotes, ignoring ids and element order
 */
fun entityListsEqual(
    a: List<LocalNote>,
    b: List<LocalNote>
): Boolean {
    if (a.size != b.size)
        return false

    val sortedA = a.sortedWith(compareBy({ it.title }, { it.content }, { it.color }))
    val sortedB = b.sortedWith(compareBy({ it.title }, { it.content }, { it.color }))

    for (i in sortedA.indices) {
        val expected = sortedA[i]
        val actual = sortedB[i]

        if (!expected.equalsIgnoreId(actual))
            return false
    }
    return true
}

fun noteListsEqual(
    a: List<Note>,
    b: List<Note>
): Boolean {
    return entityListsEqual(a.toLocalNoteList(), b.toLocalNoteList())
}

fun List<Note>.toLocalNoteList(): List<LocalNote> {
    return this.map { it.toLocalNote() }
}