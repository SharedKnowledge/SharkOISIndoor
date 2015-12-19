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
    public static final String ACTION = "btleAction";
    public static final int ACTION_START = 78;
    public static final int ACTION_STOP = 79;
    public static final String ACTION_LOCATION = "de.berlin.htw.oisindoor.userapp.positioning.location";
    public static final String ACTION_LOCATION_VALUE = "de.berlin.htw.oisindoor.userapp.positioning.location_value";

    private static final String TAG = BTLEService.class.getSimpleName();
    private BluetoothAdapter bluetoothAdapter;

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
                bluetoothAdapter.enable();
                ScanFilter f = new ScanFilter.Builder().setDeviceAddress("78:A5:04:4A:58:0F").build();
                List<ScanFilter> l = new ArrayList<>(); l.add(f);
                bluetoothAdapter.getBluetoothLeScanner().startScan(l, new ScanSettings.Builder().build(), scanCallback);
                break;
            case ACTION_STOP:
                if (bluetoothAdapter != null) {
                    bluetoothAdapter.getBluetoothLeScanner().stopScan(scanCallback);
                    stopSelf();
                }
                break;
            default:
                break;
        }
        return START_NOT_STICKY;
    }

    private ScanCallback scanCallback = new ScanCallback() {
        @Override
        public void onScanResult(int callbackType, ScanResult result) { // 78:A5:04:4A:58:0F
            Log.d(TAG, "onScanResult: " + result.toString());
            Map<ParcelUuid, byte[]> t = result.getScanRecord().getServiceData();
            for (ParcelUuid i : t.keySet()) {
                String beaconContent = new String(t.get(i), Charset.forName("UTF-8")).trim();
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
            Log.d(TAG, "onBatchScanResults " + errorCode);
        }
    };

    public void sendLocationToActivity(String url){
        Intent i = new Intent(ACTION_LOCATION);
        i.putExtra(ACTION_LOCATION_VALUE, url);
        LocalBroadcastManager manager = LocalBroadcastManager.getInstance(this);
        manager.sendBroadcast(i);
    }

}
