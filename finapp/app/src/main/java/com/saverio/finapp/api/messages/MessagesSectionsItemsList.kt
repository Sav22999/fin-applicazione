package com.saverio.finapp.api.messages

import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName
import com.saverio.finapp.api.LastUpdateList

data class MessagesSectionsItemsList(
    @SerializedName("section")
    val section: String
)