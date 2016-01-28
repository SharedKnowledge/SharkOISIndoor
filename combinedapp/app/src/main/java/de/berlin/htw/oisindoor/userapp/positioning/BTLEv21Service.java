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
 * Version 21+ Impl of {@link BTLEService}
 * @author Max M
 */
@TargetApi(Build.VERSION_CODES.LOLLIPOP)
public class BTLEv21Service extends BTLEService {
    private static final String TAG = BTLEv21Service.class.getSimpleName();

    private ScanCallback scanCallback = new ScanCallback() {
        @Override
        public void onScanResult(int callbackType, ScanResult result) {
            Log.d(TAG, "onScanResult: " + result.getDevice().getName() + " " + result.getDevice().getAddress() + " (" + result.getRssi() +")");

            if (isANewBeacon(result.getDevice(), result.getRssi())) {
                Map<ParcelUuid, byte[]> t = result.getScanRecord().getServiceData();
                String beaconContent = "";
                for (ParcelUuid i : t.keySet()) {
                    beaconContent = new String(t.get(i), UTF8).trim();
                    Log.d(TAG, "getServiceData: " + i.getUuid() + " " + t.get(i) + " (" + beaconContent + ")");
                }
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
            stop();
        }
    };

    @Override
    void startScanning() {
        if (!bluetoothAdapter.isEnabled()) {
            Log.d(TAG, "Bluetooth is disabled");
            stop();
            return;
        }
        stopScanning();
        // TODO: check beacon whitelist
        Log.d(TAG, "startSearching");
        List<ScanFilter> l = new ArrayList<>();
        // l.add(new ScanFilter.Builder().setDeviceAddress("78:A5:04:4A:58:0F").build());
        // l.add(new ScanFilter.Builder().setDeviceAddress("78:A5:04:4A:28:88").build());
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
