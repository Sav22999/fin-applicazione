package com.saverio.finapp.api.messages

import com.google.gson.annotations.SerializedName
import com.saverio.finapp.api.LastUpdateList

data class AllMessagesList(
    @SerializedName("last-update")
    val lastUpdate: LastUpdateList? = null,
    @SerializedName("messages")
    val messages: List<AllMessagesItemsList>? = null
)