package de.berlin.htw.oisindoor.userapp.positioning;

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.util.Log;

/**
 * Version 18+ Impl of {@link BTLEService}
 * @author Max M
 */
public class BTLEv18Service extends BTLEService {
    private static final String TAG = BTLEv18Service.class.getSimpleName();

    private BluetoothAdapter.LeScanCallback scanCallback = new BluetoothAdapter.LeScanCallback() {
        @Override
        public void onLeScan(BluetoothDevice device, int rssi, byte[] scanRecord) {
            Log.d(TAG, "onScanResult: " + device.getName() + " " + device.getAddress() + " (" + rssi +")");
            if (isANewBeacon(device, rssi)) {
                String beaconContent = new String(scanRecord, UTF8).trim();
                Log.d(TAG, device.getName() + " " + device.getAddress() + " : " + beaconContent);
                sendLocationToActivity(beaconContent);
            }

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
        Log.d(TAG, "startSearching");
        bluetoothAdapter.startLeScan(scanCallback);
    }

    @Override
    void stopScanning() {
        if (bluetoothAdapter != null) {
            bluetoothAdapter.stopLeScan(scanCallback);
        }
    }

}
