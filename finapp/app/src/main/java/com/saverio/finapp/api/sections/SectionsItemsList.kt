package com.saverio.finapp.api.sections

import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName
import com.saverio.finapp.api.LastUpdateList

data class SectionsItemsList(
    @SerializedName("section")
    val id: String = "",
    @SerializedName("chapter")
    val chapter: Int = 0,
    @SerializedName("title")
    val title: String = "",
    @SerializedName("author")
    val author: String = "",
    @SerializedName("text")
    val text: String = ""
)