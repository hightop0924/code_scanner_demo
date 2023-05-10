package com.example.firsttry;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.bluetooth.BluetoothAdapter;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Paint;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.VibrationEffect;
import android.os.Vibrator;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import java.util.HashMap;
import java.util.List;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;

import com.socketmobile.capture.AppKey;
import com.socketmobile.capture.CaptureError;
import com.socketmobile.capture.Property;
import com.socketmobile.capture.SocketCamStatus;
import com.socketmobile.capture.android.Capture;
import com.socketmobile.capture.android.events.ConnectionStateEvent;
import com.socketmobile.capture.client.CaptureClient;
import com.socketmobile.capture.client.*;
import com.socketmobile.capture.client.callbacks.PropertyCallback;
import com.socketmobile.capture.socketcam.client.CaptureExtension;
import com.socketmobile.capture.troy.ExtensionScope;
import com.socketmobile.capture.types.DeviceType;

import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.util.ArrayList;
import java.util.Map;
import java.util.Objects;
import java.util.stream.Collectors;

public class MainActivity extends AppCompatActivity {
    private static final String tag = "MainAcitivity";
    public static final int ADD_ACTIVITY_REQUEST_CODE = 1;
    private CaptureClient captureClient = null;
    private ActivityResultLauncher<Intent> addActivityResultLauncher;
    private EditText artikelnummerInput;
    private Button suchenButton;
    private Button cameraButton;
    private RecyclerView artikelListe;
    private List<Artikel> artikelListeData = new ArrayList<>();
    private ArtikelAdapter artikelAdapter;
    private static final String SAVED_ARTIKEL_LISTE_KEY = "saved_artikel_liste";
    private HashMap<String, DeviceState> deviceStateMap = new HashMap<String, DeviceState>();
    private HashMap<String, DeviceClient> deviceClientMap = new HashMap<String, DeviceClient>();
    private Button deviceButton = null;
    private SocketCamDeviceReadyListener socketCamDeviceReadyListener = null;
    private CaptureExtension captureExtension = null;
    private int serviceStatus = ConnectionState.DISCONNECTED;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

//        if (checkSelfPermission("android.permission.CAMERA") == PackageManager.PERMISSION_GRANTED)

        AppKey appkey = new AppKey("MCwCFBKVAWVaSNeqU75qHHyVvI6Yr0ELAhRM3btEthQxAV9ANMSkWuJ2CXGA8Q==",
                "android:com.example.firsttry",
                "e1c1ca5c-fcda-ed11-a7c7-6045bd074938");
        CaptureClient client = new CaptureClient(appkey);

        artikelnummerInput = findViewById(R.id.artikelnummer_input);
        suchenButton = findViewById(R.id.suchen_button);
        artikelListe = findViewById(R.id.artikel_liste);
        cameraButton = findViewById(R.id.Camera);
//        deviceButton = (Button) findViewById(R.id.deviceButton);

        artikelAdapter = new ArtikelAdapter(this, artikelListeData);
        artikelListe.setAdapter(artikelAdapter);
        artikelListe.setLayoutManager(new LinearLayoutManager(this));

        DatenbankHelper dbHelper = new DatenbankHelper(this);

        TextView T2011 = findViewById(R.id.T2011);
        TextView T2012 = findViewById(R.id.T2012);
        TextView T3011 = findViewById(R.id.T3011);
        TextView T3012 = findViewById(R.id.T3012);
        TextView T4111 = findViewById(R.id.T4111);
        TextView T4112 = findViewById(R.id.T4112);
        TextView T2031 = findViewById(R.id.T2031);
        TextView T2032 = findViewById(R.id.T2032);
        TextView T5001 = findViewById(R.id.T5001);
        TextView T5002 = findViewById(R.id.T5002);
        TextView T6111 = findViewById(R.id.T6111);
        TextView T6112 = findViewById(R.id.T6112);

        artikelAdapter.setOnItemCountChangeListener(new ArtikelAdapter.OnItemCountChangeListener() {
            @Override
            public void onItemCountChange(int itemCount) {
                int countT2011 = 0;
                int countT2012 = 0;
                int countT3011 = 0;
                int countT3012 = 0;
                int countT4111 = 0;
                int countT4112 = 0;
                int countT2031 = 0;
                int countT2032 = 0;
                int countT5001 = 0;
                int countT5002 = 0;
                int countT6111 = 0;
                int countT6112 = 0;

                for (Artikel artikel : artikelListeData) {
                    if (artikel.getBeschreibung().contains("PLUS Front")) {
                        countT2011++;
                    }
                    if (artikel.getBeschreibung().contains("PLUS Backen")) {
                        countT2012++;
                    }
                    if (artikel.getBeschreibung().contains("LUX Front")) {
                        countT3011++;
                    }
                    if (artikel.getBeschreibung().contains("LUX Backen")) {
                        countT3012++;
                    }
                    if (artikel.getBeschreibung().contains("DENT Front")) {
                        countT4111++;
                    }
                    if (artikel.getBeschreibung().contains("DENT Backen")) {
                        countT4112++;
                    }
                    if (artikel.getBeschreibung().contains("COMP Front")) {
                        countT2031++;
                    }
                    if (artikel.getBeschreibung().contains("COMP Backen")) {
                        countT2032++;
                    }
                    if (artikel.getBeschreibung().contains("Bambino Front")) {
                        countT5001++;
                    }
                    if (artikel.getBeschreibung().contains("Bambino Backen")) {
                        countT5002++;
                    }
                    if (artikel.getBeschreibung().contains("Facetten Front")) {
                        countT6111++;
                    }
                    if (artikel.getBeschreibung().contains("Facetten Backen")) {
                        countT6112++;
                    }
                }
                T2011.setText("T2011: " + countT2011);
                T2012.setText("T2012: " + countT2012);
                T3011.setText("T3011: " + countT3011);
                T3012.setText("T3012: " + countT3012);
                T4111.setText("T4111: " + countT4111);
                T4112.setText("T4112: " + countT4112);
                T2031.setText("T2031: " + countT2031);
                T2032.setText("T2032: " + countT2032);
                T5001.setText("T5001: " + countT5001);
                T5002.setText("T5002: " + countT5002);
                T6111.setText("T6111: " + countT6111);
                T6112.setText("T6112: " + countT6112);
            }
        });

        Button plusButton = findViewById(R.id.plus);
        plusButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MainActivity.this, com.example.firsttry.Add.class);
                addActivityResultLauncher.launch(intent);
            }
        });

        addActivityResultLauncher = registerForActivityResult(
                new ActivityResultContracts.StartActivityForResult(),
                result -> {
                    if (result.getResultCode() == RESULT_OK && result.getData() != null) {
                        String addText = result.getData().getStringExtra("add_text");
                        if (addText != null) {
                            artikelnummerInput.setText(addText);
                            suchenButton.performClick();
                        }
                    }
                });

        suchenButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String artikelnummer = artikelnummerInput.getText().toString();
                if (!artikelnummer.isEmpty()) {
                    switch (artikelnummer.length()) {
                        case 19:
                            artikelnummer = artikelnummer.substring(0, 14);
                            break;
                        case 20:
                            artikelnummer = artikelnummer.substring(0, 14);
                            break;
                        case 21:
                            break;
                        case 22:
                            artikelnummer = artikelnummer.substring(0, 18);
                            break;
                        case 23:
                            artikelnummer = artikelnummer.substring(0, 18);
                            break;
                        default:
                            break;
                    }

                    try {
                        Artikel artikel = dbHelper.getArtikel(artikelnummer);
                        // artikelListeData.clear(); // vorherige Daten entfernen
                        artikelListeData.add(artikel);
                        artikelAdapter.notifyDataSetChanged();
                        artikelnummerInput.setText("");
                    } catch (Exception e) {
                        Artikel dummyArtikel = new Artikel(artikelnummer, "Kein Eintrag gefunden");
                        artikelListeData.add(dummyArtikel);
                        artikelAdapter.notifyDataSetChanged();
                        artikelnummerInput.setText("");
                    }
                } else {
                    Toast.makeText(MainActivity.this, "Bitte QR-Code eingeben!", Toast.LENGTH_SHORT).show();
                }
            }
        });

        cameraButton.setOnClickListener(new View.OnClickListener() {
                                            @Override
                                            public void onClick(View v) {
                                                onScanClicked();
                                            }
                                        }
        );

        artikelnummerInput.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                if (actionId == EditorInfo.IME_ACTION_DONE || (event.getAction() == KeyEvent.ACTION_DOWN && event.getKeyCode() == KeyEvent.KEYCODE_ENTER)) {
                    suchenButton.performClick();
                    return true;
                }
                return false;
            }
        });

        // Zustand der ArtikelListeData wiederherstellen, falls vorhanden
        if (savedInstanceState != null && savedInstanceState.containsKey(SAVED_ARTIKEL_LISTE_KEY)) {
            artikelListeData = savedInstanceState.getParcelableArrayList(SAVED_ARTIKEL_LISTE_KEY);
            artikelAdapter = new ArtikelAdapter(this, artikelListeData);
            artikelListe.setAdapter(artikelAdapter);
        }

        Button clearButton = findViewById(R.id.Clear);
        clearButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //Toast.makeText(MainActivity.this, "Toast", Toast.LENGTH_SHORT).show();
                artikelnummerInput.setText("");
                artikelListeData.clear();
                artikelAdapter.notifyDataSetChanged();
            }
        });
    }

    private void onScanClicked() {
        if (canTriggerScanner()) {
            triggerDevices();
        } else {
            showCompanionDialog();
        }
    }

    private void showCompanionDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Companion Dialog");
        builder.setMessage("No socket scanner is connected.");
        builder.setPositiveButton("Use Camera", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
                startSocketCamExtension();
            }
        });


        builder.setNegativeButton("Launch Companion", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(getString(R.string.companion_store_url)));
                startActivity(intent);
                dialog.dismiss();
            }
        });
        builder.create().show();


//        val dialogFrag = CompanionDialogFragment()
//        dialogFrag.companionDialogListener = object: OnCompanionDialogListener {
//            override fun onUseCamera() {
//                startSocketCamExtension()
//            }
//        }
//        dialogFrag.show(supportFragmentManager, getString(R.string.title_companion_dialog))
    }

    private void startSocketCamExtension() {
        CaptureClient client = captureClient;
        if (captureClient == null) {
            socketCamDeviceReadyListener = new SocketCamDeviceReadyListener() {
                @Override
                public void onSocketCamDeviceReady() {
                    triggerDevices();
                }
            };
            return;
        }
        captureExtension = new CaptureExtension.Builder()
                .setContext(this)
                .setClientHandle(client.getHandle())
                .setExtensionScope(ExtensionScope.LOCAL)
                .setListener(new CaptureExtension.Listener() {
                    @Override
                    public void onExtensionStateChanged(ConnectionState connectionState) {
                        Log.d(tag, "Extension State Changed :" + connectionState.intValue());
                        if (connectionState.intValue() == ConnectionState.CONNECTED) {
                            client.setSocketCamStatus(SocketCamStatus.ENABLE, new PropertyCallback() {
                                @Override
                                public void onComplete(CaptureError captureError, Property property) {
                                    if (captureError != null) {
                                        Log.d(tag, "Failed setSocketCamStatus " + captureError.getMessage());
                                    }
                                }
                            });

                        }
                    }

                    @Override
                    public void onError(CaptureError error) {
                        if (error != null) {
                            Log.d(tag, "Error on start Capture Extension: ${error.message}");
                        }
                    }
                }).build();
        captureExtension.start();
    }

    private void triggerDevices() {
        List<DeviceClient> readyDevices = deviceStateMap.entrySet()
                .stream()
                .filter(entry -> entry.getValue().intValue() == DeviceState.READY)
                .collect(Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue))
                .keySet()
                .stream()
                .map(entry -> deviceClientMap.get(entry))
                .filter(Objects::nonNull)
                .collect(Collectors.toList());
        List<DeviceClient> bluetoothReaders = readyDevices.stream()
                .filter(device -> (device.getDeviceType() != DeviceType.kModelSocketCamC820))
                .collect(Collectors.toList());
        List<DeviceClient> socketCamDevices = readyDevices.stream()
                .filter(device -> (device.getDeviceType() == DeviceType.kModelSocketCamC820))
                .collect(Collectors.toList());
        if (bluetoothReaders.size() > 0) {
            for (int i = 0; i < bluetoothReaders.size(); i++) {
                DeviceClient device = bluetoothReaders.get(i);
                device.trigger(new PropertyCallback() {
                    @Override
                    public void onComplete(CaptureError captureError, Property property) {
                        Log.d(tag, "trigger callback : " + captureError + " : " + property);
                    }
                });
            }
        } else {
            socketCamDevices.stream().findFirst().orElse(null)
                    .trigger(new PropertyCallback() {
                        @Override
                        public void onComplete(CaptureError captureError, Property property) {
                            Log.d(tag, "trigger callback : " + captureError + " : " + property);
                        }
                    });
        }
//                .filter { it.value.intValue() == DeviceState.READY }.keys
//                .mapNotNull { deviceClientMap[it] }

//        var bluetoothReaders = readyDevices.filter { entry -> !entry.isSocketCamDevice() }
//        var socketCamDevices = readyDevices.filter { entry -> entry.isSocketCamDevice() }
//        if (bluetoothReaders.count() > 0) {
//            for(device in bluetoothReaders) {
//                device.trigger { error, property ->
//                        Log.d(tag, "trigger callback : $error, $property")
//                }
//            }
//        } else {
//            socketCamDevices.firstOrNull()?.trigger{ error, property ->
//                    Log.d(tag, "trigger callback : $error, $property")
//            }
//        }

    }

    private boolean canTriggerScanner() {
        return isServiceConnected() && isConnectedDevice();
    }

    private boolean isConnectedDevice() {
        return deviceStateMap.entrySet()
                .stream()
                .filter(entry -> entry.getValue().intValue() == DeviceState.READY)
                .collect(Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue))
                .size() > 0;
//        return deviceStateMap.filter { entry -> entry.value.intValue() == DeviceState.READY }.count() > 0
    }

    private boolean isServiceConnected() {
        return serviceStatus == ConnectionState.READY;
    }

    private void stopSocketCamExtension() {
        if (captureExtension != null)
            captureExtension.stop();
    }

    @Subscribe(threadMode = ThreadMode.MAIN, sticky = true)
    void onCaptureDeviceStateChange(DeviceStateEvent event) {
        DeviceClient device = event.getDevice();
        DeviceState state = event.getState();
        int scannerStatus = state.intValue();
        String deviceGuid = device.getDeviceGuid();
        deviceStateMap.put(deviceGuid, state);
        deviceClientMap.put(deviceGuid, device);

        if (device.getDeviceType() != DeviceType.kModelSocketCamC820) {
            stopSocketCamExtension();
        }
        Log.d(tag, "Scanner  : " + device.getDeviceName() + " - " + device.getDeviceGuid());

        switch (scannerStatus) {
            case DeviceState.AVAILABLE: {
                Log.d(tag, "Scanner State Available.");
                break;
            }
            case DeviceState.OPEN: {
                Log.d(tag, "Scanner State Open.");
                break;
            }
            case DeviceState.READY: {
                Log.d(tag, "Scanner State Ready.");
                socketCamDeviceReadyListener.onSocketCamDeviceReady();
                socketCamDeviceReadyListener = null;
                break;
            }
            case DeviceState.GONE: {
                Log.d(tag, "Scanner State Gone.");
                deviceStateMap.remove(deviceGuid);
                deviceClientMap.remove(deviceGuid);
                break;
            }
            default:
                Log.d(tag, "Scanner State " + scannerStatus);
                break;
        }
        updateDeviceButton();
    }

    private void updateDeviceButton() {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                enableDeviceButton(canTriggerScanner());
            }
        });
    }

    private void enableDeviceButton(boolean enabled) {
//        deviceButton.isEnabled = enabled;
    }

    @Subscribe(threadMode = ThreadMode.MAIN, sticky = true)
    void onCaptureServiceConnectionStateChange(ConnectionStateEvent event) {
        ConnectionState state = event.getState();

        if (state.hasError()) {
            CaptureError error = state.getError();
            Log.d(tag, "Error on service connection. Error: " + error.getCode() + " " + error.getMessage());
            switch (error.getCode()) {
                case CaptureError.COMPANION_NOT_INSTALLED: {
                    AlertDialog alert = new AlertDialog.Builder(this)
                            .setMessage(R.string.prompt_install_companion)
                            .setPositiveButton("Cancel", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    dialog.dismiss();
                                }
                            })
                            .setNegativeButton("Install", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    Intent i = new Intent(Intent.ACTION_VIEW, Uri.parse(getString(R.string.companion_store_url)));
                                    startActivity(i);
                                }
                            }).create();
                    alert.show();
                    break;
                }
                case CaptureError.SERVICE_NOT_RUNNING: {
                    if (state.isDisconnected()) {
                        if (Capture.notRestartedRecently()) {
                            Capture.restart(this);
                        }
                    }
                    break;
                }
                case CaptureError.BLUETOOTH_NOT_ENABLED: {
                    Intent i = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
                    if (ActivityCompat.checkSelfPermission(this, android.Manifest.permission.BLUETOOTH_CONNECT) != PackageManager.PERMISSION_GRANTED) {
                        // T ODO: Consider calling
                        //    A ctivityCompat#requestPermissions
                        // here to request the missing permissions, and then overriding
                        //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
                        //                                          int[] grantResults)
                        // to handle the case where the user grants the permission. See the documentation
                        // for ActivityCompat#requestPermissions for more details.
                        break;
                    }
                    startActivity(i);
                    break;
                }
                default:
                {
                    break;
                }
            }
        } else {
            captureClient = event.getClient();

            serviceStatus = state.intValue();
            Log.d(tag, "Service Status is changed to " + serviceStatus + " (" + state +")");
            switch (serviceStatus) {
                case ConnectionState.CONNECTING:
                {
                    break;
                }
                case ConnectionState.CONNECTED:
                {
                    break;
                }
                case ConnectionState.READY:
                {
                    break;
                }
                case ConnectionState.DISCONNECTING:
                {
                    break;
                }
                case ConnectionState.DISCONNECTED:
                {
                    break;
                }
            }
        }
        updateDeviceButton();
    }

    @Override
    protected void onSaveInstanceState(@NonNull Bundle outState) {
        super.onSaveInstanceState(outState);
        // Zustand der ArtikelListeData speichern
        outState.putParcelableArrayList(SAVED_ARTIKEL_LISTE_KEY, new ArrayList<>(artikelListeData));
    }

    @Override
    protected void onRestoreInstanceState(@NonNull Bundle savedInstanceState) {
        super.onRestoreInstanceState(savedInstanceState);
        // Zustand der ArtikelListeData wiederherstellen, falls vorhanden
        if (savedInstanceState.containsKey(SAVED_ARTIKEL_LISTE_KEY)) {
            artikelListeData = savedInstanceState.getParcelableArrayList(SAVED_ARTIKEL_LISTE_KEY);
            artikelAdapter = new ArtikelAdapter(this, artikelListeData);
            artikelListe.setAdapter(artikelAdapter);
        }
    }

    interface SocketCamDeviceReadyListener {
        void onSocketCamDeviceReady();
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    void onData(DataEvent event) {
        String data = event.getData().getString().trim();
        addScanData(getLineForBarcode(this, data));
    }

    private void addScanData(String data) {
        String newContent = artikelnummerInput.toString() + data;
        artikelnummerInput.setText(newContent);
        if (PreferenceHelperKt.isVibrationOnScan(this)) {
            Vibrator vibrator = (Vibrator) getSystemService(Context.VIBRATOR_SERVICE);
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                vibrator.vibrate(VibrationEffect.createOneShot(500, VibrationEffect.DEFAULT_AMPLITUDE));
            } else {
                vibrator.vibrate(500);
            }
        }
    }

    private void goToEnd() {
        artikelnummerInput.setSelection(artikelnummerInput.toString().length());
    }

    String getLineForBarcode(Context c, String barcode) {
        String retValue = null;
        if (barcode.isEmpty())
            retValue = "txt_barcode";
        else retValue = barcode;
        int defaultQuantity = PreferenceHelperKt.getDefaultQuantity(c);
        if (PreferenceHelperKt.autoAddQuantity(c)) {
            String value;
            if (PreferenceHelperKt.isDelineatorComma(c)) {
                value = ", " + defaultQuantity;
            } else {
                value = " " + defaultQuantity;
            }

            retValue += value;
        }

        String newLineSymbol;
        if (PreferenceHelperKt.isAddNewLine(c))
            newLineSymbol = "\n";
        else
            newLineSymbol = ";";
         if (!barcode.isEmpty()) {
            retValue = newLineSymbol + retValue;
        } else {
            retValue = retValue + newLineSymbol;
        }

        return retValue;
    }
}