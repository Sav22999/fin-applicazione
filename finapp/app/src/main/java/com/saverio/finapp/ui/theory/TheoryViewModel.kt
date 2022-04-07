package com.saverio.finapp.ui.theory

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel

class TheoryViewModel : ViewModel() {

    val chaptersChanged = MutableLiveData<Boolean>()
    fun setChaptersChanged(changed: Boolean) {
        chaptersChanged.value = changed
    }
}