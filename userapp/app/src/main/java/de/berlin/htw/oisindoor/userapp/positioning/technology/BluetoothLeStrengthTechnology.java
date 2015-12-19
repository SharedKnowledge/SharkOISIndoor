package de.berlin.htw.oisindoor.userapp.positioning.technology;

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import de.hadizadeh.positioning.controller.Technology;
import de.berlin.htw.oisindoor.userapp.positioning.BluetoothLeDevice;
import de.hadizadeh.positioning.model.SignalInformation;

/**
 * Created by maikt on 05.11.2015.
 */

public class BluetoothLeStrengthTechnology extends Technology {
    private Map<String, SignalInformation> signalData;
    private BluetoothAdapter bluetoothAdapter;
    private Map<String, BluetoothLeDevice> btLeDevices;
    private List<String> allowedBtLeDevices;
    private long validityTime;

    private BluetoothAdapter.LeScanCallback leScanCallback = new BluetoothAdapter.LeScanCallback() {
        @Override
        public void onLeScan(BluetoothDevice device, int rssi, byte[] scanRecord) {
            BluetoothLeDevice btDevice = new BluetoothLeDevice(device, scanRecord, rssi);
//            if ("4c00".equals(btDevice.getCompanyId()) && (allowedBtLeDevices == null || allowedBtLeDevices.contains(btDevice.getUuid()))) {
                btLeDevices.put(btDevice.getUuid(), btDevice);
//            }
        }
    };

    public BluetoothLeStrengthTechnology(String name, long validityTime, List<String> allowedBtLeDevices) {
        super(name, null);
        this.validityTime = validityTime;
        this.allowedBtLeDevices = allowedBtLeDevices;
        this.btLeDevices = new HashMap<String, BluetoothLeDevice>();
        this.bluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
        this.signalData = new HashMap<String, SignalInformation>();
        bluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
        bluetoothAdapter.startLeScan(leScanCallback);
    }

    @Override
    public Map<String, SignalInformation> getSignalData() {
        signalData.clear();
        long currentTime = System.currentTimeMillis();
        for (Map.Entry<String, BluetoothLeDevice> btLeDevice : btLeDevices.entrySet()) {
            if (btLeDevice.getValue().getTimeStamp() + validityTime >= currentTime) {
                signalData.put(btLeDevice.getValue().getAddress(), new SignalInformation(btLeDevice.getValue().getRssi()));
            }
        }
        return signalData;
    }

    @Override
    public void stopScanning() {
        super.stopScanning();
        bluetoothAdapter.stopLeScan(leScanCallback);
    }
}
