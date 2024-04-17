package com.example.ble_receiver_watch


import android.Manifest
import android.annotation.SuppressLint
import android.app.Activity
import android.bluetooth.BluetoothManager
import android.bluetooth.BluetoothProfile
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.os.BatteryManager
import android.os.Build
import android.util.Log
import android.widget.Toast
import androidx.annotation.RequiresApi
import androidx.core.app.ActivityCompat

class BatteryLevelReceiver : BroadcastReceiver() {
    @RequiresApi(Build.VERSION_CODES.KITKAT)
    override fun onReceive(context: Context, intent: Intent) {
        when (intent.action) {
            Intent.ACTION_POWER_CONNECTED -> {
                val intentt = Intent(context, FullScreenActivity::class.java)
                context.startActivity(intentt)

                Toast.makeText(context, "Battery plugin connected!", Toast.LENGTH_SHORT).show()
            }

            Intent.ACTION_POWER_DISCONNECTED -> {
                // Battery plugin disconnected
                val status = intent.getIntExtra(BatteryManager.EXTRA_STATUS, -1)
                if (!isBleConnected(context)) {
                    val i = Intent(context, MainActivity::class.java)
                    i.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
                    context.startActivity(i)
                }
                Toast.makeText(context, "Battery plugin disconnected!", Toast.LENGTH_SHORT).show()
                val isCharging = status == BatteryManager.BATTERY_STATUS_CHARGING ||
                        status == BatteryManager.BATTERY_STATUS_FULL
                if (!isCharging) {
                    val i = Intent(context, NFC_Tag::class.java)
                    (context as Activity).finish()
                    context.startActivity(i)

                    Log.i("Charging", "Not charging")
                    Toast.makeText(context, "Calling NFC_Tag Class", Toast.LENGTH_SHORT).show()

                }
                Toast.makeText(context, "Battery plugin disconnected!", Toast.LENGTH_SHORT).show()
            }

            Intent.ACTION_BATTERY_CHANGED -> {
                val status = intent.getIntExtra(BatteryManager.EXTRA_STATUS, -1)
                BatteryManager.ACTION_CHARGING
                val isCharging = status == BatteryManager.BATTERY_STATUS_CHARGING ||
                        status == BatteryManager.BATTERY_STATUS_FULL
                if (isCharging) {
                    val intentt = Intent(context, FullScreenActivity::class.java)
                    context.startActivity(intentt)
                    Toast.makeText(context, "Docked Successfully", Toast.LENGTH_SHORT).show()
                    NFC_Tag.clearTagData()
                }
            }
        }
    }

    @RequiresApi(Build.VERSION_CODES.S)
    @SuppressLint("ServiceCast")
    private fun isBleConnected(context: Context): Boolean {
        val bluetoothManager =
            context.getSystemService(Context.BLUETOOTH_SERVICE) as BluetoothManager
        val bluetoothAdapter = bluetoothManager.adapter
        if (ActivityCompat.checkSelfPermission(context, Manifest.permission.BLUETOOTH_CONNECT) != PackageManager.PERMISSION_GRANTED) {
            return false
        }
        val devices = bluetoothManager.getConnectedDevices(BluetoothProfile.GATT)
        return devices.isNotEmpty()
    }
}

//    private fun isCharging(intent: Intent): Boolean {
//        val status = intent.getIntExtra(BatteryManager.EXTRA_STATUS, -1)
//        return status == BatteryManager.BATTERY_STATUS_CHARGING ||
//                status == BatteryManager.BATTERY_STATUS_FULL
//    }



//class BatteryLevelReceiver : BroadcastReceiver() {
//    @RequiresApi(Build.VERSION_CODES.KITKAT)
//    override fun onReceive(context: Context, intent: Intent) {
//
//        if (intent.action == Intent.ACTION_POWER_CONNECTED) {
//
//            // Battery plugin connected
//            val intentt = Intent(context, FullScreenActivity::class.java)
//            context.startActivity(intentt)
//            Toast.makeText(context, "Battery plugin connected!", Toast.LENGTH_SHORT).show()
//        } else if (intent.action == Intent.ACTION_POWER_DISCONNECTED) {
//            // Battery plugin disconnected
//            val status = intent.getIntExtra(BatteryManager.EXTRA_STATUS, -1)
//            val isCharging = status == BatteryManager.BATTERY_STATUS_CHARGING ||
//                    status == BatteryManager.BATTERY_STATUS_FULL
//            if (!isCharging) {
//                val i = Intent(context, NFC_Tag::class.java)
//                //i.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
//
//                context.startActivity(i)
//                Log.i("Charging","Not charging")
//                Toast.makeText(context, "Calling NFC_Tag Class", Toast.LENGTH_SHORT).show()
////                val intentt = Intent(context, NFC_Tag::class.java)
////                context.startActivity(intentt)
//            }
//            Toast.makeText(context, "Battery plugin disconnected!", Toast.LENGTH_SHORT).show()
//        }
//        else if (intent.action == Intent.ACTION_BATTERY_CHANGED) {
//            val status = intent.getIntExtra(BatteryManager.EXTRA_STATUS, -1)
//            val isCharging = status == BatteryManager.BATTERY_STATUS_CHARGING ||
//                    status == BatteryManager.BATTERY_STATUS_FULL
//            if (isCharging) {
//                val intentt = Intent(context, FullScreenActivity::class.java)
//                context.startActivity(intentt)
//                Toast.makeText(context, "Docked Successfully", Toast.LENGTH_SHORT).show()
//            }
//        }
//    }
//}