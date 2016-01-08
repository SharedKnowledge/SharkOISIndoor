package de.berlin.htw.oisindoor.userapp.positioning;

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.le.ScanFilter;
import android.bluetooth.le.ScanSettings;
import android.content.Intent;
import android.util.Log;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

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
        }
    };

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        int action = intent.getIntExtra(ACTION, -1);
        Log.d(TAG, "onStartCommand: " + action);
        switch (action){
            case ACTION_START:
                if (!bluetoothAdapter.isEnabled()) {
                    Log.d(TAG, "Bluetooth is disabled");
                    stopSelf();
                    break;
                }
                stopScanning();
                Log.d(TAG, "startSearching");
                bluetoothAdapter.startLeScan(scanCallback);
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

    @Override
    void stopScanning() {
        if (bluetoothAdapter != null) {
            bluetoothAdapter.stopLeScan(scanCallback);
        }
    }

}
