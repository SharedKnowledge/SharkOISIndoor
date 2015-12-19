package de.berlin.htw.oisindoor.userapp.positioning;

/**
 * Created by maikt on 05.11.2015.
 */
import android.bluetooth.BluetoothDevice;

public class BluetoothLeDevice implements Comparable<BluetoothLeDevice> {
    private String name;
    private String address;
    private String companyId;
    private String uuid;
    private int rssi;
    private int major;
    private int minor;
    private int txPower;
    private long timeStamp;

    public BluetoothLeDevice(String name, String address, String companyId, String uuid, int rssi,
                             int major, int minor, int txPower, long
                                     timeStamp) {
        initialize(name, address, companyId, uuid, rssi, major, minor, txPower, timeStamp);
    }

    public BluetoothLeDevice(String name, String address, String companyId, String uuid, int rssi,
                             String major, String minor, String txPower, long timeStamp) {
        initialize(name, address, companyId, uuid, rssi, Integer.parseInt(major, 16),
                Integer.parseInt(minor, 16), Integer.valueOf(txPower, 16).shortValue(), timeStamp);
    }

    public BluetoothLeDevice(BluetoothDevice device, byte[] scanRecord, int rssi) {
        String uuid = "";
        String companyId = String.format("%02x", scanRecord[5]) + String.format("%02x", scanRecord[6]);
        String major = String.format("%02x", scanRecord[25]) + String.format("%02x", scanRecord[26]);
        String minor = String.format("%02x", scanRecord[27]) + String.format("%02x", scanRecord[28]);
        String txPower = String.format("%02x", scanRecord[29]);
        for (int i = 0; i < scanRecord.length; i++) {
            if (i >= 9 && i <= 24) {
                uuid += String.format("%02x", scanRecord[i]);
            }
        }
        initialize(device.getName(), device.getAddress(), companyId, uuid, rssi,
                Integer.parseInt(major, 16), Integer.parseInt(minor, 16),
                Integer.valueOf(txPower, 16).shortValue(), System.currentTimeMillis());
    }

    private void initialize(String name, String address, String companyId, String uuid, int rssi,
                            int major, int minor, int txPower, long timeStamp) {
        this.name = name;
        this.address = address;
        this.companyId = companyId;
        this.uuid = uuid;
        this.rssi = rssi;
        this.major = major;
        this.minor = minor;
        this.txPower = txPower;
        this.timeStamp = timeStamp;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public String getCompanyId() {
        return companyId;
    }

    public void setCompanyId(String companyId) {
        this.companyId = companyId;
    }

    public int getRssi() {
        return rssi;
    }

    public void setRssi(int rssi) {
        this.rssi = rssi;
    }

    public long getTimeStamp() {
        return timeStamp;
    }

    public void setTimeStamp(long timeStamp) {
        this.timeStamp = timeStamp;
    }

    public int getMajor() {
        return major;
    }

    public void setMajor(int major) {
        this.major = major;
    }

    public int getMinor() {
        return minor;
    }

    public void setMinor(int minor) {
        this.minor = minor;
    }

    public String getIdentificator() {
        return major+"|"+minor;
    }

    public String getUuid() {
        return uuid;
    }

    public void setUuid(String uuid) {
        this.uuid = uuid;
    }

    public int getTxPower() {
        return txPower;
    }

    public void setTxPower(int txPower) {
        this.txPower = txPower;
    }

    @Override
    public int compareTo(BluetoothLeDevice another) {
        if(rssi > another.rssi) {
            return 1;
        } else if(rssi < another.rssi) {
            return -1;
        }
        return 0;
    }

    @Override
    public String toString() {
        return "BluetoothLeDevice{" +
                "name='" + name + '\'' +
                ", address='" + address + '\'' +
                ", companyId='" + companyId + '\'' +
                ", uuid='" + uuid + '\'' +
                ", rssi=" + rssi +
                ", major=" + major +
                ", minor=" + minor +
                ", txPower=" + txPower +
                ", timeStamp=" + timeStamp +
                '}';
    }
}