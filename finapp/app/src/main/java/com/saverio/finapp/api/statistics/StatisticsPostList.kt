package com.saverio.finapp.api.statistics

import com.google.gson.annotations.SerializedName

data class StatisticsPostList(
    @SerializedName("userid")
    val userid: String,
    @SerializedName("type")
    val type: Int,
    @SerializedName("datetime")
    val datetime: String,
    @SerializedName("correct_answer")
    val correct_answer: String,
    @SerializedName("user_answer")
    val user_answer: String = "",
    @SerializedName("question_id")
    val question_id: Int,
    @SerializedName("milliseconds")
    val milliseconds: Int = 0
)