package com.vipul.bluetoohconnect.model

data class BluetoothDeviceUiState(
    val name: String?,
    val address: String,
    val bonded: Boolean = false
)
