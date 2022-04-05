package com.saverio.finapp.api.news

import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName
import com.saverio.finapp.api.LastUpdateList

data class NewsList(
    @SerializedName("last-update")
    val lastUpdate: LastUpdateList? = null,
    @SerializedName("news")
    val news: List<NewsItemsList>? = null
)