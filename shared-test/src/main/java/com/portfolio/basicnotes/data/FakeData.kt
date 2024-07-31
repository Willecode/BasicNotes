package com.portfolio.basicnotes.data

import com.portfolio.basicnotes.R
import com.portfolio.basicnotes.data.source.LocalNote
import java.time.LocalDate

fun getMockNotes(): List<LocalNote> {
    return listOf(
        LocalNote(
            title = "Weekly Goals",
            content = "Complete the project report by Wednesday. Start the new marketing campaign. Schedule a team meeting to discuss next quarter's targets. Exercise at least three times this week.",
            color = R.color.post_it_blue,
            date = LocalDate.now()
        ),
        LocalNote(
            title = "Gardening Tips",
            content = "Water plants early in the morning. Use compost for better soil nutrition. Prune the roses and remove weeds regularly. Consider planting new herbs.",
            color = R.color.post_it_green,
            date = LocalDate.now()
        ),
        LocalNote(
            title = "Dream Journal",
            content = "Had a dream about exploring an ancient city.",
            color = R.color.post_it_pink,
            date = LocalDate.now()
        ),
        LocalNote(
            title = "Restaurant Recommendations",
            content = "Try the new Italian place downtown.",
            color = R.color.post_it_orange,
            date = LocalDate.now()
        ),
        LocalNote(
            title = "Creative Writing Ideas",
            content = "Write a short story set in a dystopian future. Explore the theme of time travel.",
            color = R.color.post_it_white,
            date = LocalDate.now()
        )
    )
}