package de.berlin.htw.oisindoor.userapp.positioning;

import android.annotation.TargetApi;
import android.bluetooth.le.ScanCallback;
import android.bluetooth.le.ScanFilter;
import android.bluetooth.le.ScanResult;
import android.bluetooth.le.ScanSettings;
import android.os.Build;
import android.os.ParcelUuid;
import android.util.Log;

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
            Log.d(TAG, "onScanResult1: " + result.getDevice().getName() + " " + result.getDevice().getAddress());
            Map<ParcelUuid, byte[]> t = result.getScanRecord().getServiceData();

            for (ParcelUuid i : t.keySet()) {
                String beaconContent = new String(t.get(i), UTF8).trim();
                Log.d(TAG, "getServiceData: " + i.getUuid() + " " + t.get(i) + " (" + beaconContent + ")");
                sendLocationToActivity(beaconContent);
            }
            stop();
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
    void startScanning() {
        if (!bluetoothAdapter.isEnabled()) {
            Log.d(TAG, "Bluetooth is disabled");
            stopSelf();
            return;
        }
        stopScanning();
        Log.d(TAG, "startSearching");
        List<ScanFilter> l = new ArrayList<>();
        l.add(new ScanFilter.Builder().setDeviceAddress("78:A5:04:4A:58:0F").build());
        l.add(new ScanFilter.Builder().setDeviceAddress("78:A5:04:4A:28:88").build());
        bluetoothAdapter.getBluetoothLeScanner().startScan(l, new ScanSettings.Builder().build(), scanCallback);
    }

    @Override
    void stopScanning() {
        Log.d(TAG, "stopScanning");
        if (bluetoothAdapter != null && bluetoothAdapter.getBluetoothLeScanner() != null) {
            bluetoothAdapter.getBluetoothLeScanner().stopScan(scanCallback);
        }
    }

}
