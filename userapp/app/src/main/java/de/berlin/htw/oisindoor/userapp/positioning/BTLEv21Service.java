package de.berlin.htw.oisindoor.userapp.positioning;

import android.annotation.TargetApi;
import android.app.Service;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothManager;
import android.bluetooth.le.ScanCallback;
import android.bluetooth.le.ScanFilter;
import android.bluetooth.le.ScanResult;
import android.bluetooth.le.ScanSettings;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
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
@TargetApi(Build.VERSION_CODES.LOLLIPOP)
public class BTLEv21Service extends BTLEService {
    private static final String TAG = BTLEv21Service.class.getSimpleName();

    private ScanCallback scanCallback = new ScanCallback() {
        @Override
        public void onScanResult(int callbackType, ScanResult result) { // 78:A5:04:4A:58:0F und
            Log.d(TAG, "onScanResult: " + result.toString());
            Log.d(TAG, "onScanResult: " + result.getScanRecord());

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

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        int action = intent.getIntExtra(ACTION, -1);
        Log.d(TAG, "onStartCommand: " + action);
        switch (action) {
            case ACTION_START:
                if (!bluetoothAdapter.isEnabled()) {
                    Log.d(TAG, "Bluetooth is disabled");
                    stopSelf();
                    break;
                }
                stopScanning();
                Log.d(TAG, "startSearching");
                List<ScanFilter> l = new ArrayList<>();
                l.add(new ScanFilter.Builder().setDeviceAddress("78:A5:04:4A:58:0F").build());
                l.add(new ScanFilter.Builder().setDeviceAddress("78:A5:04:4A:28:88").build());
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

    void stopScanning() {
        Log.d(TAG, "stopScanning");
        if (bluetoothAdapter != null && bluetoothAdapter.getBluetoothLeScanner() != null) {
            bluetoothAdapter.getBluetoothLeScanner().stopScan(scanCallback);
        }
    }

}
