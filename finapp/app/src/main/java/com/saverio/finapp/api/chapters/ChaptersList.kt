package com.saverio.finapp.api.chapters

import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName
import com.saverio.finapp.api.LastUpdateList

data class ChaptersList(
    @SerializedName("last-update")
    @Expose
    val lastUpdate: LastUpdateList? = null,
    @SerializedName("chapters")
    @Expose
    val chapters: List<ChaptersItemsList>? = null
)