package com.saverio.finapp.ui.theory

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel

class TheoryViewModel : ViewModel() {

    private val _text = MutableLiveData<String>().apply {
        value = "This is theory Fragment"
    }
    val text: LiveData<String> = _text
}