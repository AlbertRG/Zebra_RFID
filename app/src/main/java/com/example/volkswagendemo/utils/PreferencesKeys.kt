package com.example.volkswagendemo.utils

import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.intPreferencesKey
import androidx.datastore.preferences.core.stringPreferencesKey

object PreferencesKeys {

    //LOCATION
    val IS_LOCATION_SAVED = booleanPreferencesKey("is_location_saved")
    val LOCATION = stringPreferencesKey("location")
    val ADDRESS = stringPreferencesKey("address")

    //WORKSHOP
    val WORKSHOP_NAME = stringPreferencesKey("workshop_name")

    //SETTINGS
    val ANTENNA_POWER = intPreferencesKey("300")


}