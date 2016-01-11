package de.berlin.htw.oisindoor.userapp.positioning;

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.util.Log;

/**
 * Created by Max on 07.01.2016.
 */
public class BTLEv19Service extends BTLEService {
    private static final String TAG = BTLEv19Service.class.getSimpleName();

    private BluetoothAdapter.LeScanCallback scanCallback = new BluetoothAdapter.LeScanCallback() {
        @Override
        public void onLeScan(BluetoothDevice device, int rssi, byte[] scanRecord) {
            String beaconContent = new String(scanRecord, UTF8).trim();
            Log.d(TAG, device.getName() + " " + device.getAddress() + " : " + beaconContent);
            sendLocationToActivity(beaconContent);
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
