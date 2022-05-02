package com.saverio.finapp.api.statistics

import com.google.gson.annotations.SerializedName
import com.saverio.finapp.api.LastUpdateList

data class StatisticsList(
    @SerializedName("statistics")
    val statistics: List<StatisticsItemsList>? = null
)