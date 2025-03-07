package com.example.volkswagendemo.data.provider

import android.content.Context
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.preferencesDataStore
import com.example.volkswagendemo.data.models.LocationData
import com.example.volkswagendemo.utils.PreferencesKeys.ADDRESS
import com.example.volkswagendemo.utils.PreferencesKeys.IS_LOCATION_SAVED
import com.example.volkswagendemo.utils.PreferencesKeys.LOCATION
import com.example.volkswagendemo.utils.PreferencesKeys.WORKSHOP_NAME
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.flow.map
import kotlinx.serialization.json.Json
import javax.inject.Inject

private val Context.dataStore by preferencesDataStore(name = "RFID_PREFERENCES")

class DataStore @Inject constructor(@ApplicationContext context: Context) {

    private val dataStore = context.dataStore

    suspend fun setLocationSaved(isSaved: Boolean) {
        dataStore.edit { preferences ->
            preferences[IS_LOCATION_SAVED] = isSaved
        }
    }

    fun getLocationSaved() = dataStore.data.map {
        it[IS_LOCATION_SAVED] ?: false
    }.flowOn(Dispatchers.IO)

    suspend fun setLocation(location: LocationData) {
        dataStore.edit { preferences ->
            preferences[LOCATION] = Json.encodeToString(location)
        }
    }

    fun getLocation() = dataStore.data.map { preferences ->
        preferences[LOCATION]?.let {
            Json.decodeFromString<LocationData>(it)
        }
    }.flowOn(Dispatchers.IO)

    suspend fun setAddress(address: String) {
        dataStore.edit { preferences ->
            preferences[ADDRESS] = address
        }
    }

    fun getAddress() = dataStore.data.map {
        it[ADDRESS] ?: ""
    }.flowOn(Dispatchers.IO)

    suspend fun setWorkshopName(name: String) {
        dataStore.edit { preferences ->
            preferences[WORKSHOP_NAME] = name
        }
    }

    fun getWorkshopName() = dataStore.data.map {
        it[WORKSHOP_NAME] ?: ""
    }.flowOn(Dispatchers.IO)

}