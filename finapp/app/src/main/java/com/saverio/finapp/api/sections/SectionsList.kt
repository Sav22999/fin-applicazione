package com.saverio.finapp.api.sections

import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName
import com.saverio.finapp.api.LastUpdateList

data class SectionsList(
    @SerializedName("last-update")
    val lastUpdate: LastUpdateList? = null,
    @SerializedName("sections")
    val sections: List<SectionsItemsList>? = null
)