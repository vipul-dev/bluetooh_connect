package com.vipul.bluetoohconnect.viewmodel

import android.Manifest
import android.app.Application
import android.bluetooth.BluetoothAdapter
import android.bluetooth.BluetoothDevice
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import androidx.annotation.RequiresPermission
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.AndroidViewModel
import com.vipul.bluetoohconnect.model.BluetoothDeviceUiState
import dagger.hilt.android.lifecycle.HiltViewModel
import java.util.UUID
import javax.inject.Inject

@HiltViewModel
class BluetoothViewModel @Inject constructor(application: Application) :
    AndroidViewModel(application) {

    companion object {
        private val MY_UUID: UUID = UUID.fromString("00001101-0000-1000-8000-00805F9B34FB")
    }

    private val bluetoothAdapter:BluetoothAdapter? = BluetoothAdapter.getDefaultAdapter()


    var scanDevices by mutableStateOf<List<BluetoothDeviceUiState>>(emptyList())
        private set


    private val _receiver = object : BroadcastReceiver() {
        @RequiresPermission(Manifest.permission.BLUETOOTH_CONNECT)
        override fun onReceive(context: Context?, intent: Intent?) {
            when(intent?.action){
                BluetoothDevice.ACTION_FOUND->{
                    val device : BluetoothDevice = intent.getParcelableExtra(BluetoothDevice.EXTRA_DEVICE)!!

                    if (!scanDevices.any{it.address == device.address}){
                        scanDevices = scanDevices + BluetoothDeviceUiState(device.name,device.address)
                    }
                }
            }
        }

    }


    @RequiresPermission(Manifest.permission.BLUETOOTH_SCAN)
    fun startScan(){
        val filter = IntentFilter(BluetoothDevice.ACTION_FOUND)
        val context = getApplication<Application>().applicationContext

        context.registerReceiver(_receiver,filter)
        bluetoothAdapter?.startDiscovery()
    }

    @RequiresPermission(Manifest.permission.BLUETOOTH_SCAN)
    fun stopScanning(){
        val context = getApplication<Application>().applicationContext

        context.unregisterReceiver(_receiver)
        bluetoothAdapter?.cancelDiscovery()
    }

    @RequiresPermission(allOf = [Manifest.permission.BLUETOOTH_SCAN, Manifest.permission.BLUETOOTH_CONNECT])
    fun connectToDevice(address:String):Boolean{
        val device = bluetoothAdapter?.getRemoteDevice(address)

        return try {
            device?.createRfcommSocketToServiceRecord(MY_UUID)?.let { socket->
                bluetoothAdapter?.cancelDiscovery()
                socket.connect()

                true
            }?:false
        }catch (e:Exception){
            false
        }
    }

}