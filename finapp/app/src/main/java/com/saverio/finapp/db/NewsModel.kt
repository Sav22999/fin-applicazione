package com.saverio.finapp.db

class NewsModel(
    var id: Int = 0,
    var type: Int = 0,
    var date: String = "",
    var title: String? = null,
    var image: String? = null,
    var link: String? = null,
    var text: String = ""
)