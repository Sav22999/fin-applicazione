package com.saverio.finapp.db

class SimulationQuizzesModel(
    var id: Int,
    var chapter: Int,
    var section: String,
    var question: String,
    var A: String,
    var B: String,
    var C: String,
    var D: String,
    var correct: String,
    var user_answer: String
)