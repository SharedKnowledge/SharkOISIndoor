package de.berlin.htw.oisindoor.userapp.positioning;

import android.app.Service;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothManager;
import android.bluetooth.le.ScanCallback;
import android.bluetooth.le.ScanFilter;
import android.bluetooth.le.ScanResult;
import android.bluetooth.le.ScanSettings;
import android.content.Context;
import android.content.Intent;
import android.os.IBinder;
import android.os.ParcelUuid;
import android.support.annotation.Nullable;
import android.support.v4.content.LocalBroadcastManager;
import android.util.Log;

import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * Created by Max on 19.12.2015.
 */
public class BTLEService extends Service {
    public static final String RESPONSE_LOCATION = "de.berlin.htw.oisindoor.userapp.positioning.location";
    public static final String RESPONSE_LOCATION_VALUE = "de.berlin.htw.oisindoor.userapp.positioning.location_value";
    public static final String RESPONSE_ERROR = "de.berlin.htw.oisindoor.userapp.positioning.error";
    public static final String RESPONSE_ERROR_VALUE = "de.berlin.htw.oisindoor.userapp.positioning.error_value";

    public static void startService(Context context) {
        Intent startIntent = new Intent(context, BTLEService.class);
        startIntent.putExtra(ACTION, ACTION_START);
        context.startService(startIntent);
    }

    public static void stopService(Context context){
        Intent stopIntent = new Intent(context, BTLEService.class);
        stopIntent.putExtra(ACTION, ACTION_STOP);
        context.startService(stopIntent);
    }

    private static final String TAG = BTLEService.class.getSimpleName();
    private static final String ACTION = "btleAction";
    private static final int ACTION_START = 78;
    private static final int ACTION_STOP = 79;
    private static final Charset UTF8 = Charset.forName("UTF-8");

    private BluetoothAdapter bluetoothAdapter;
    private ScanCallback scanCallback = new ScanCallback() {
        @Override
        public void onScanResult(int callbackType, ScanResult result) { // 78:A5:04:4A:58:0F
            Log.d(TAG, "onScanResult: " + result.toString());
            Map<ParcelUuid, byte[]> t = result.getScanRecord().getServiceData();
            for (ParcelUuid i : t.keySet()) {
                String beaconContent = new String(t.get(i), UTF8).trim();
                Log.d(TAG, i.getUuid() + " " + beaconContent);
                sendLocationToActivity(beaconContent);
            }
        }

        @Override
        public void onBatchScanResults(List<ScanResult> results) {
            Log.d(TAG, "onBatchScanResults " + results);
        }

        @Override
        public void onScanFailed(int errorCode) {
            String errorMsg = errorCodeToString(errorCode);
            Log.d(TAG, "onScanFailed " + errorMsg);
            sendErrorToActivity(errorMsg);
            stopScanning();
        }
    };

    private void stopScanning() {
        if (bluetoothAdapter != null && bluetoothAdapter.getBluetoothLeScanner() != null) {
            bluetoothAdapter.getBluetoothLeScanner().stopScan(scanCallback);
        }
    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        int action = intent.getIntExtra(ACTION, -1);
        Log.d(TAG, "onStartCommand: " + action);
        switch (action){
            case ACTION_START:
                BluetoothManager bluetoothManager = (BluetoothManager) getSystemService(Context.BLUETOOTH_SERVICE);
                bluetoothAdapter = bluetoothManager.getAdapter();
                if (!bluetoothAdapter.isEnabled()) {
                    Log.d(TAG, "Bluetooth is disabled");
                    stopSelf();
                    break;
                }
                stopScanning();
                Log.d(TAG, "startSearching");
                ScanFilter f = new ScanFilter.Builder().setDeviceAddress("78:A5:04:4A:58:0F").build();
                List<ScanFilter> l = new ArrayList<>(); l.add(f);
                bluetoothAdapter.getBluetoothLeScanner().startScan(l, new ScanSettings.Builder().build(), scanCallback);
                break;

            case ACTION_STOP:
                stopScanning();
                stopSelf();
                break;

            default:
                stopScanning();
                stopSelf();
                break;
        }
        return START_NOT_STICKY;
    }

    public void sendLocationToActivity(String url){
        Intent i = new Intent(RESPONSE_LOCATION);
        i.putExtra(RESPONSE_LOCATION_VALUE, url);
        LocalBroadcastManager.getInstance(this).sendBroadcast(i);
    }

    public void sendErrorToActivity(String errorMsg){
        Intent i = new Intent(RESPONSE_ERROR);
        i.putExtra(RESPONSE_ERROR_VALUE, errorMsg);
        LocalBroadcastManager.getInstance(this).sendBroadcast(i);
    }

    private String errorCodeToString(int code) {
        switch (code){
            case ScanCallback.SCAN_FAILED_ALREADY_STARTED:
                return "SCAN_FAILED_ALREADY_STARTED";
            case ScanCallback.SCAN_FAILED_APPLICATION_REGISTRATION_FAILED:
                return "SCAN_FAILED_APPLICATION_REGISTRATION_FAILED";
            case ScanCallback.SCAN_FAILED_FEATURE_UNSUPPORTED:
                return "SCAN_FAILED_FEATURE_UNSUPPORTED";
            case ScanCallback.SCAN_FAILED_INTERNAL_ERROR:
                return "SCAN_FAILED_INTERNAL_ERROR";
            default:
                return "Unknown: " + code;
        }
    }

}
