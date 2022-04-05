package com.saverio.finapp.ui.home

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel

class HomeViewModel : ViewModel() {

    val newsChanged = MutableLiveData<Boolean>()
    val loading = MutableLiveData<Boolean>()

    fun setNewsChanged(changed: Boolean) {
        newsChanged.value = changed
    }

    fun setLoading(load: Boolean) {
        loading.value = load
    }
}