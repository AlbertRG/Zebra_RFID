package com.example.volkswagendemo.data.models

import kotlinx.serialization.Serializable

@Serializable
data class SettingsData(
    var antennaPower: Float,
    var beeperVolume: Int
)