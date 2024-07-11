package com.portfolio.basicnotes.data

import androidx.annotation.ColorRes
import com.portfolio.basicnotes.R

data class Note(
    val id : Int,
    val title : String,
    val content : String,
    val date : String,
    @ColorRes val noteColor : Int = R.color.post_it_gray
)
