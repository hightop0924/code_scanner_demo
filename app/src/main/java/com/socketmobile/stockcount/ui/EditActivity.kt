/**  Copyright © 2018 Socket Mobile, Inc. */

package com.socketmobile.stockcount.ui

import android.Manifest
import android.bluetooth.BluetoothAdapter
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.os.VibrationEffect
import android.os.Vibrator
import androidx.core.app.ActivityCompat
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import android.util.Log
import android.view.KeyEvent
import android.view.Menu
import android.view.MenuItem
import android.view.View
import at.wolframdental.Scanner.R
import com.socketmobile.capture.CaptureError
import com.socketmobile.capture.SocketCamStatus
import com.socketmobile.capture.android.Capture
import com.socketmobile.capture.android.events.ConnectionStateEvent
import com.socketmobile.capture.client.*
import com.socketmobile.capture.socketcam.client.CaptureExtension
import com.socketmobile.capture.troy.ExtensionScope
import com.socketmobile.stockcount.helper.*
import kotlinx.android.synthetic.main.activity_edit.*
import org.greenrobot.eventbus.Subscribe
import org.greenrobot.eventbus.ThreadMode
import java.util.*

class EditActivity : AppCompatActivity() {
//    lateinit var file: RMFile
    private var captureClient: CaptureClient? = null
    private var captureExtension: CaptureExtension? = null
    private var serviceStatus = ConnectionState.DISCONNECTED
    private val tag = EditActivity::class.java.name
    private val deviceStateMap = HashMap<String, DeviceState>()
    private val deviceClientMap = HashMap<String, DeviceClient>()
    private var socketCamDeviceReadyListener: SocketCamDeviceReadyListener? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_edit)

        fileEditText.setOnFocusChangeListener { _, hasFocus ->
            editTypeView.visibility = if (hasFocus || true) View.VISIBLE else View.GONE
        }

        deviceButton.isEnabled = false
        scanButton.setOnClickListener {
            if (canTriggerScanner()) {
                triggerCamDevices()
            }
        }
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return super.onOptionsItemSelected(item)
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        return true
    }


    override fun onSupportNavigateUp(): Boolean {
        return true
    }

    override fun onKeyDown(keyCode: Int, event: KeyEvent?): Boolean {
        return super.onKeyDown(keyCode, event)
    }
    private fun showCompanionDialog() {
        val dialogFrag = CompanionDialogFragment()
        dialogFrag.companionDialogListener = object: OnCompanionDialogListener {
            override fun onUseCamera() {
                startSocketCamExtension()
            }
        }
        dialogFrag.show(supportFragmentManager, getString(R.string.title_companion_dialog))
    }
    private fun startSocketCamExtension() {
        val client = captureClient ?: return
        socketCamDeviceReadyListener = object: SocketCamDeviceReadyListener {
            override fun onSocketCamDeviceReady() {
            }
        }

        captureExtension = CaptureExtension.Builder()
                .setContext(this)
                .setClientHandle(client.handle)
                .setExtensionScope(ExtensionScope.LOCAL)
                .setListener(object: CaptureExtension.Listener {
                    override fun onExtensionStateChanged(connectionState: ConnectionState?) {
                        Log.d(tag, "Extension State Changed : ${connectionState?.intValue()}")
                        if (connectionState?.intValue() == ConnectionState.CONNECTED) {
                            client.setSocketCamStatus(SocketCamStatus.ENABLE) {err, property ->
                                if (err != null) {
                                    Log.d(tag, "Failed setSocketCamStatus ${err.message}")
                                }
                            }
                        } else {
                            updateCamButton(false)
                        }
                    }

                    override fun onError(error: CaptureError?) {
                        if (error != null) {
                            Log.d(tag, "Error on start Capture Extension: ${error.message}")
                        }
                    }
                })
                .build()
        captureExtension?.start()
    }

    private fun stopSocketCamExtension() {
        captureExtension?.stop()
    }

    private fun hasBLDevices() : Boolean {
        val readyDevices = deviceStateMap
            .filter { it.value.intValue() == DeviceState.READY }.keys
            .mapNotNull { deviceClientMap[it] }

        var bluetoothReaders = readyDevices.filter { entry -> !entry.isSocketCamDevice() }
        return (bluetoothReaders.isNotEmpty())
    }

    private fun triggerCamDevices() {
        val readyDevices = deviceStateMap
            .filter { it.value.intValue() == DeviceState.READY }.keys
            .mapNotNull { deviceClientMap[it] }

        var socketCamDevices = readyDevices.filter { entry -> entry.isSocketCamDevice() }
        socketCamDevices.firstOrNull()?.trigger{ error, property ->
            Log.d(tag, "trigger callback : $error, $property")
        }
    }

    private fun hasCamDevices(): Boolean {
        val readyDevices = deviceStateMap
            .filter { it.value.intValue() == DeviceState.READY }.keys
            .mapNotNull { deviceClientMap[it] }

        var socketCamDevices = readyDevices.filter { entry -> entry.isSocketCamDevice() }
        return socketCamDevices.isNotEmpty()
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    fun onData(event: DataEvent) {
        val data = event.data.string.trim()
        addScanData(getLineForBarcode(this, data))
    }

    private fun addScanData(data: String) {
        val newContent = data
        fileEditText.setText(newContent)
        goToEnd()
        if (isVibrationOnScan(this)) {
            val vibrator = getSystemService(Context.VIBRATOR_SERVICE) as Vibrator
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                vibrator.vibrate(VibrationEffect.createOneShot(500, VibrationEffect.DEFAULT_AMPLITUDE))
            } else {
                vibrator.vibrate(500)
            }
        }
    }
    private fun goToEnd() {
        fileEditText.setSelection(fileEditText.text.toString().length)
    }
    @Subscribe(threadMode = ThreadMode.MAIN, sticky = true)
    fun onCaptureDeviceStateChange(event: DeviceStateEvent) {
        val device = event.device
        var state = event.state
        val scannerStatus = state.intValue()
        val deviceGuid = device.deviceGuid
        deviceStateMap[deviceGuid] = state
        deviceClientMap[deviceGuid] = device

        if (!device.isSocketCamDevice()) {
            stopSocketCamExtension()
        }
        Log.d(tag, "Scanner : ${device.deviceName} - ${device.deviceGuid}")

        when(scannerStatus) {
            DeviceState.AVAILABLE -> {
                Log.d(tag, "Scanner State Available.")
            }
            DeviceState.OPEN -> {
                Log.d(tag, "Scanner State Open.")
            }
            DeviceState.READY -> {
                Log.d(tag, "Scanner State Ready.")
                socketCamDeviceReadyListener?.onSocketCamDeviceReady()
                socketCamDeviceReadyListener = null
                updateCamButton(hasCamDevices())
                updateDeviceButton(hasBLDevices())
            }
            DeviceState.GONE -> {
                Log.d(tag, "Scanner State Gone.")
                deviceStateMap.remove(deviceGuid)
                deviceClientMap.remove(deviceGuid)
            }
            else -> {
                Log.d(tag, "Scanner State $scannerStatus")
            }
        }

    }

    @Subscribe(threadMode = ThreadMode.MAIN, sticky = true)
    fun onCaptureServiceConnectionStateChange(event: ConnectionStateEvent) {
        val state = event.state

        if (state.hasError()) {
            val error = state.error
            Log.d(tag, "Error on service connection. Error: ${error.code}, ${error.message}")
            when(error.code) {
                CaptureError.COMPANION_NOT_INSTALLED -> {
                    val alert = AlertDialog.Builder(this)
                            .setMessage(R.string.prompt_install_companion)
                            .setPositiveButton(R.string.cancel) { dialog, _ ->
                                dialog.dismiss()
                            }.setNegativeButton(R.string.install) { dialog, _ ->
                                dialog.dismiss()
                                val i = Intent(Intent.ACTION_VIEW, Uri.parse(getString(R.string.companion_store_url)))
                                startActivity(i)
                            }.create()
                    alert.show()
                }
                CaptureError.SERVICE_NOT_RUNNING -> {
                    if (state.isDisconnected) {
                        if (Capture.notRestartedRecently()) {
                            Capture.restart(this)
                        }
                    }
                }
                CaptureError.BLUETOOTH_NOT_ENABLED -> {
                    val i = Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE)
                    if (ActivityCompat.checkSelfPermission(
                            this,
                            Manifest.permission.BLUETOOTH_CONNECT
                        ) != PackageManager.PERMISSION_GRANTED
                    ) {
                        // TODO: Consider calling
                        //    ActivityCompat#requestPermissions
                        // here to request the missing permissions, and then overriding
                        //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
                        //                                          int[] grantResults)
                        // to handle the case where the user grants the permission. See the documentation
                        // for ActivityCompat#requestPermissions for more details.
                        return
                    }
                    startActivity(i)
                }
                else -> {

                }
            }
        } else {
            captureClient = event.client

            serviceStatus = state.intValue()
            Log.d(tag, "Service Status is changed to $serviceStatus($state)")
            when(serviceStatus) {
                ConnectionState.CONNECTING -> {
                }
                ConnectionState.CONNECTED -> {
                    startSocketCamExtension()
                }
                ConnectionState.READY -> {

                }
                ConnectionState.DISCONNECTING -> {

                }
                ConnectionState.DISCONNECTED -> {

                }
            }
        }
    }

    private fun isServiceConnected(): Boolean {
        return serviceStatus == ConnectionState.READY
    }
    private fun isConnectedDevice(): Boolean {
        return deviceStateMap.filter { entry -> entry.value.intValue() == DeviceState.READY }.count() > 0
    }
    private fun canTriggerScanner(): Boolean {
        return isServiceConnected() && isConnectedDevice()
    }
    private fun updateDeviceButton(enabled: Boolean) {
        runOnUiThread {
            enableDeviceButton(enabled)
        }
    }

    private fun updateCamButton(enabled : Boolean) {
        runOnUiThread {
            enableCamButton(enabled)
        }
    }

    private fun enableDeviceButton(enabled: Boolean) {
        deviceButton.isEnabled = enabled
    }

    private fun enableCamButton(enabled: Boolean) {
        scanButton.isEnabled = enabled
    }
}
interface SocketCamDeviceReadyListener {
    fun onSocketCamDeviceReady()
}