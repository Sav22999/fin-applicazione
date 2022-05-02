package com.saverio.finapp.db

class QuizzesModel(
    var id: Int = -1,
    var chapter: Int?,
    var section: String?,
    var question: String?,
    var A: String?,
    var B: String?,
    var C: String?,
    var D: String?,
    var correct: String?
)