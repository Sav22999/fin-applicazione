package com.saverio.finapp.api

import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName

data class LastUpdateList(
    @Expose
    @SerializedName("datetime")
    val datetime: String? = null,
    @Expose
    @SerializedName("date")
    val date: String? = null,
    @Expose
    @SerializedName("time")
    val time: String? = null
)