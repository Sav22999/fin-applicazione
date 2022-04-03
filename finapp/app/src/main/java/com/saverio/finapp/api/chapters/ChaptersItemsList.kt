package com.saverio.finapp.api.chapters

import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName
import com.saverio.finapp.api.LastUpdateList

data class ChaptersItemsList(
    @SerializedName("chapter")
    @Expose
    val chapter: Int,
    @SerializedName("title")
    @Expose
    val title: String
)