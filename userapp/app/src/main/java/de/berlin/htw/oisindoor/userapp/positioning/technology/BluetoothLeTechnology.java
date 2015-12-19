package de.berlin.htw.oisindoor.userapp.positioning.technology;

/**
 * Created by maikt on 05.11.2015.
 */
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;

import java.util.Collections;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.Map;

import de.berlin.htw.oisindoor.userapp.positioning.BluetoothLeDevice;
import de.hadizadeh.positioning.controller.Technology;
import de.hadizadeh.positioning.model.SignalInformation;

public class BluetoothLeTechnology extends Technology {
    private Map<String, SignalInformation> signalData;
    private BluetoothAdapter bluetoothAdapter;
    private int cacheSize = 10;
    private LinkedList<BluetoothLeDevice> btLeDevices;
    private boolean scanning;

    private BluetoothAdapter.LeScanCallback leScanCallback = new BluetoothAdapter.LeScanCallback() {
        @Override
        public void onLeScan(BluetoothDevice device, int rssi, byte[] scanRecord) {
            BluetoothLeDevice btDevice = new BluetoothLeDevice(device, scanRecord, rssi);

            // Adapt the RSSI value to your hardware so you can filter devices.
            // 4c00 is the iBeacon type.
            if (rssi > -75 && "4c00".equals(btDevice.getCompanyId())) {
                btLeDevices.add(btDevice);
            }

            while (btLeDevices.size() > cacheSize) {
                btLeDevices.removeFirst();
            }
        }
    };

    public BluetoothLeTechnology(String name) {
        super(name, null);
        btLeDevices = new LinkedList<BluetoothLeDevice>();
        providesExactlyPosition();
        this.bluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
        this.signalData = new HashMap<String, SignalInformation>();
        signalData = new HashMap<String, SignalInformation>();
        bluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
        bluetoothAdapter.startLeScan(leScanCallback);
        scanning = true;
        new Thread() {
            @Override
            public void run() {
                while (scanning) {
                    try {
                        if (btLeDevices.size() > 0) {
                            btLeDevices.removeFirst();
                        }
                        Thread.sleep(1000);
                    } catch (Exception ex) {
                        ex.printStackTrace();
                    }
                }
            }
        }.start();
    }

    @Override
    public Map<String, SignalInformation> getSignalData() {
        if (btLeDevices.size() > 0) {
            signalData.clear();
            BluetoothLeDevice bestDevice = Collections.max(btLeDevices);
            signalData.put(bestDevice.getAddress(), new SignalInformation(1.0));
            return signalData;
        }
        return null;
    }

    @Override
    public void stopScanning() {
        super.stopScanning();
        scanning = false;
        bluetoothAdapter.stopLeScan(leScanCallback);
    }
}
