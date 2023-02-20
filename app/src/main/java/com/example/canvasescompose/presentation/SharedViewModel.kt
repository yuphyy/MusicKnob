package com.example.canvasescompose.presentation

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class SharedViewModel @Inject constructor() : ViewModel() {

    val rotation = MutableStateFlow(25f)

    val volume = MutableStateFlow(0f)

    fun updateRotation(value: Float){
        viewModelScope.launch {
            rotation.emit(value)
        }
    }

    fun updateVolume(value: Float){
        viewModelScope.launch {
            volume.emit(value)
        }
    }
}