package com.example.ble_receiver_watch

import android.Manifest
import android.annotation.SuppressLint
import android.bluetooth.BluetoothAdapter
import android.bluetooth.BluetoothGatt
import android.bluetooth.BluetoothGattCallback
import android.bluetooth.BluetoothGattCharacteristic
import android.bluetooth.BluetoothProfile
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.content.pm.ActivityInfo
import android.content.pm.PackageManager
import android.os.Build
import android.os.Bundle
import android.util.Log
import android.view.View
import android.view.WindowManager
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import java.util.UUID

class FullScreenActivity : AppCompatActivity() {

    private val bluetoothStateReceiver = object : BroadcastReceiver() {
        override fun onReceive(context: Context?, intent: Intent?) {
            val action = intent?.action
            if (action == BluetoothAdapter.ACTION_STATE_CHANGED) {
                val state = intent.getIntExtra(BluetoothAdapter.EXTRA_STATE, BluetoothAdapter.ERROR)
                when (state) {
                    BluetoothAdapter.STATE_DISCONNECTED,
                    BluetoothAdapter.STATE_DISCONNECTING,
                    BluetoothAdapter.STATE_CONNECTING -> {
                        startActivity(Intent(this@FullScreenActivity, MainActivity::class.java))
                        finish()
                    }

                    BluetoothAdapter.STATE_CONNECTED -> {
                    }
                }
            }
        }
    }
            private val gattCallback = object : BluetoothGattCallback() {
                override fun onConnectionStateChange(gatt: BluetoothGatt?, status: Int, newState: Int) {
                    super.onConnectionStateChange(gatt, status, newState)
                    Log.e("onConnectionStateChange","Working properly")
                    if (newState == BluetoothProfile.STATE_DISCONNECTED) {
                        startActivity(Intent(this@FullScreenActivity, MainActivity::class.java))
                        finish()
                    } else if (newState == BluetoothProfile.STATE_DISCONNECTING) {
                        startActivity(Intent(this@FullScreenActivity, MainActivity::class.java))
                        finish()
                    } else if (newState == BluetoothProfile.STATE_CONNECTING) {
                        startActivity(Intent(this@FullScreenActivity, MainActivity::class.java))
                        finish()
                    } else if (newState == BluetoothProfile.STATE_CONNECTED){
                        Log.e("BLE connection status","Connected and will call the sendCommandToDevice")
                       sendCommandToDevice(gatt)
                    }
                }
            }
            private fun sendCommandToDevice(gatt: BluetoothGatt?){

                Log.e("sendCommandToDevice","Working")
                val Service_UUID = UUID.fromString("0000180f-0000-1000-8000-00805f9b34fb")
                val BATTERY_LEVEL_UUID = UUID.fromString("00002a19-0000-1000-8000-00805f9b34fb")

                Log.e("sendCommandToDevice after uuid","Working")
                val service = gatt?.getService(Service_UUID)
                val characteristic = service?.getCharacteristic(BATTERY_LEVEL_UUID)
            if (service !=null && characteristic != null) {

                Log.e("Write status", "Successful")
                val command = "Connected successfully".toByteArray(Charsets.UTF_8)
                characteristic.value = command
                if (characteristic.properties and BluetoothGattCharacteristic.PROPERTY_WRITE != 0) {
                    if (ActivityCompat.checkSelfPermission(this, Manifest.permission.BLUETOOTH_CONNECT) != PackageManager.PERMISSION_GRANTED) {
                        return
                    }
                    gatt.writeCharacteristic(characteristic)
                } else {
                    Log.e("sendCommandToDevice", "Characteristic does not support write")
                }
            }else{
                Log.e("sendCommandToDevice","Service or Characteristic not found")
            }
            }


                @SuppressLint("MissingInflatedId")
                override fun onCreate(savedInstanceState: Bundle?) {
                    super.onCreate(savedInstanceState)
                    requestedOrientation = ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE
                    window.addFlags(
                        WindowManager.LayoutParams.FLAG_FULLSCREEN or
                                WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON or
                                WindowManager.LayoutParams.FLAG_DISMISS_KEYGUARD or
                                WindowManager.LayoutParams.FLAG_SHOW_WHEN_LOCKED or
                                WindowManager.LayoutParams.FLAG_TURN_SCREEN_ON or
                                WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE or
                                WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE or
                                WindowManager.LayoutParams.FLAG_LAYOUT_IN_SCREEN
                    )
                    val decorView = window.decorView
                    decorView.systemUiVisibility =
                        View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY or View.SYSTEM_UI_FLAG_HIDE_NAVIGATION

                    setContentView(R.layout.activity_full_screen)

                    val filter = IntentFilter(BluetoothAdapter.ACTION_STATE_CHANGED)
                    registerReceiver(bluetoothStateReceiver, filter)


                    val bluetoothAdapter = BluetoothAdapter.getDefaultAdapter()

                    val deviceAddress = "76:06:4C:29:99:C6"
                    val device = bluetoothAdapter.getRemoteDevice(deviceAddress)
                    val gatt: BluetoothGatt = if (ActivityCompat.checkSelfPermission(this, Manifest.permission.BLUETOOTH_CONNECT) != PackageManager.PERMISSION_GRANTED)
                    {
                        return
                    } else {
                        device.connectGatt(this, false, gattCallback)
                    }
                }


                     override fun onDestroy() {
                        super.onDestroy()
                        unregisterReceiver(bluetoothStateReceiver)
//                      gatt?.close()
                    }
                }


