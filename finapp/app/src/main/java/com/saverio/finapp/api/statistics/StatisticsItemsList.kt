package com.saverio.finapp.api.statistics

import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName
import com.saverio.finapp.api.LastUpdateList

data class StatisticsItemsList(
    @SerializedName("type")
    val type: Int = 0,
    @SerializedName("datetime")
    val datetime: String = "",
    @SerializedName("correct_answer")
    val correct_answer: String = "",
    @SerializedName("user_answer")
    val user_answer: String = "",
    @SerializedName("question_id")
    val question_id: Int = 0,
    @SerializedName("milliseconds")
    val milliseconds: Int = 0
)