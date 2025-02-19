package com.example.volkswagendemo.ui.states

sealed class LocationStates {
    object Hide : LocationStates()
    object Loading : LocationStates()
    object Show : LocationStates()
    object Error : LocationStates()
    object InternetError : LocationStates()
}