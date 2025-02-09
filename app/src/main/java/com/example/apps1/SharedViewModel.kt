package com.example.apps1

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel

class SharedViewModel : ViewModel() {
    private val _songTitle = MutableLiveData<String>()
    val songTitle: LiveData<String> get() = _songTitle

    fun updateSongTitle(title: String) {
        _songTitle.value = title
    }
}