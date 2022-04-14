package com.saverio.finapp.db

class StatisticsModel(
    var id: Int = 0,
    var type: Int = 0,
    var question_id: Int = 0,
    var datetime: String = "",
    var correct_answer: String? = null,
    var user_answer: String? = null,
    var milliseconds: Int = -1
)