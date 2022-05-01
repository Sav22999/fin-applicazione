package com.saverio.finapp.api.messages

import com.google.gson.annotations.SerializedName
import com.saverio.finapp.api.LastUpdateList

data class MessagesSectionsList(
    @SerializedName("sections")
    val sections: List<MessagesSectionsItemsList>? = null
)