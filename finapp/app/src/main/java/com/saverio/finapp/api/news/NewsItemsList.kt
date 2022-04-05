package com.saverio.finapp.api.news

import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName
import com.saverio.finapp.api.LastUpdateList

data class NewsItemsList(
    @SerializedName("id")
    val id: Int = 0,
    @SerializedName("type")
    val type: Int = 0,
    @SerializedName("date")
    val date: String = "",
    @SerializedName("title")
    val title: String = "",
    @SerializedName("image")
    val image: String = "",
    @SerializedName("link")
    val link: String = "",
    @SerializedName("text")
    val text: String = ""
)