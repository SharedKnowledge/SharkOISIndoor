package de.berlin.htw.oisindoor.userapp.positioning;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Service;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothManager;
import android.bluetooth.le.ScanCallback;
import android.bluetooth.le.ScanResult;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.location.LocationManager;
import android.os.Build;
import android.os.ParcelUuid;
import android.provider.Settings;
import android.util.Log;
import android.widget.EditText;
import android.widget.ImageButton;

import java.nio.charset.Charset;
import java.util.List;
import java.util.Map;

import de.berlin.htw.oisindoor.userapp.R;
import de.berlin.htw.oisindoor.userapp.util.Util;

/**
 * BLESniffer is responsible for scanning all nearby ble-devices and
 * trying to read their broadcast data. In case the broadcast data contains
 * geo information, BLESniffer will show this data on screen. However, this will
 * only happen if the current found ble-device is the closest to the mobile.
 * @since Android API 18
 * @version 1.0
 * @author thor Stefan Jagdmann
 */

public class BLESniffer implements Runnable {
    /**
     * Used in system Bluetooth enable request ( 0 = enable )
     */
    private final int REQUEST_ENABLE_BT = 0;
    /**
     * Activity needed to display AlertDialogs
     */
    private Activity activity = null;
    /**
     * The device Bluetooth manager needed to get the default device Bluetooth adapter
     */
    protected BluetoothManager bluetoothManager;
    /**
     * The device Bluetooth adapter used to search for Bluetooth Low Energy devices
     */
    private BluetoothAdapter bluetoothAdapter = null;
    /**
     * ScanCallback for devices with an API less than 21
     */
    private BluetoothAdapter.LeScanCallback leScanCallback;
    /**
     * EditText-field to pass an altitude value to
     */
    private EditText altitude;
    /**
     * EditText-field to pass a latitude value to
     */
    private EditText latidue;
    /**
     * EditText-field to pass a longitude value to
     */
    private EditText longitude;
    /**
     * ImageButton to enable, disable and indicate a ble scan
     */
    private ImageButton bleButton;
    /**
     * Used to indicate the current state of the GPS
     */
    private LocationManager locationManager = null;
    /**
     * RSSI ( Received signal strength indication ). Used to check the signal strength of a found
     * ble device.
     */
    private int rssi = -1000;
    /**
     * The classname
     */
    private final String TAG = this.getClass().getSimpleName();
    /**
     * ScanCallback for devices with an API equal ot higher than 21
     */
    private ScanCallback scanCallback;

    /**
     * Constructor of the BLESniffer.class.
     * @param activity  Parent activity. Use "this" in an Activity or getActivity() in a Fragment
     * @param latidue   EditText representing the latidute value in an Activity or Fragment
     * @param longitude EditText representing the longitude value in an Activity or Fragment
     * @param altitude  EditText representing the altitude value in an Activity or Fragment
     * @param bleButton ImageButton to start or stop a ble scan
     */
    public BLESniffer(Activity activity, EditText latidue, EditText longitude,
                      EditText altitude, ImageButton bleButton){

        this.activity = activity;
        this.altitude = altitude;
        this.bleButton = bleButton;
        this.latidue = latidue;
        this.longitude = longitude;

        bluetoothManager = (BluetoothManager) activity.getSystemService(Context.BLUETOOTH_SERVICE);
        bluetoothAdapter = bluetoothManager.getAdapter();
        locationManager = (LocationManager) activity.getSystemService(Service.LOCATION_SERVICE);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP)
            buildScanCallbackAPI21();
        else
            buildScanCallbackAP18();
    }

    /*
     *  Will start the scan for ble-devices if Bluetooth and the location service of the mobile are
     *  enabled. If one of them is not enabled, it will call enableBluetooth() [and  enableLocation().]
     */
    @Override
    public void run() {
        if((locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER) && bluetoothAdapter.isEnabled())) {
            Log.i(TAG, "Starting beacon-scan.");
            if (Build.VERSION.SDK_INT > Build.VERSION_CODES.LOLLIPOP_MR1) {
                bluetoothAdapter.getBluetoothLeScanner().startScan(scanCallback);
                bleButton.setImageResource(R.mipmap.bluetooth_on);
            } else {
                //noinspection deprecation
                bluetoothAdapter.startLeScan(leScanCallback);
                bleButton.setImageResource(R.mipmap.bluetooth_on);
            }
        } else {
            if (Build.VERSION.SDK_INT > Build.VERSION_CODES.LOLLIPOP_MR1)
                enableLocation();
            enableBluetooth();
        }
    }

    /**
     * Method to build a new ScanCallback for devices with an API higher or equal than 21.
     * It will check if the current found ble-device is further or closer to the one,
     * found before. In case the current one is closer, it will print information about the ble-device
     * on log-level concerning name, mac-address and the broadcasted service data ( Service-Charesteristics
     * with String containing the geos ). In addition the method will display the longitude, latitude and
     * altitude in the given EditTexts of this class. @see
     * Moreover the
     * The ScanCallback will only use ScanCallback.onScanResult.
     */
    private void buildScanCallbackAPI21(){
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            scanCallback = new ScanCallback() {
                @Override
                public void onScanResult(int callbackType, ScanResult result) {
                    String beaconContent = "";
                    if (Build.VERSION.SDK_INT > Build.VERSION_CODES.LOLLIPOP) {
                        if (checkRange(result.getRssi())) {
                            //noinspection ConstantConditions
                            Log.i(TAG, "Found: " + result.getScanRecord().getDeviceName());
                            Log.i(TAG, "MAC: " + result.getDevice());
                            Log.i(TAG, "Service Data: ");
                            Map<ParcelUuid, byte[]> serviceData = result.getScanRecord().getServiceData();
                            for (ParcelUuid parcelUuid : serviceData.keySet()) {
                                beaconContent = new String(serviceData.get(parcelUuid), Charset.defaultCharset()).trim();
                                Log.d(TAG, parcelUuid.getUuid() + " " + beaconContent);
                            }
                            List<String> geos = Util.readPropperGEO(beaconContent);
                            longitude.setText(geos.get(0));
                            latidue.setText(geos.get(1));
                            altitude.setText(geos.get(2));
                        }
                    }
                }
            };
        }
    }

    /**
     * NOT TESTED!!! MAY NOT WORK. HAVE NO API 18 DEVICE. SHOULD PRINT SAMPLE-DATA.
     * Method to build a new ScanCallback for devices with an API between 18 and 20.
     * It will print information about a found ble-device on log-level concerning name, mac-address
     * and the broadcasted service data ( Service-Charesteristics with String containing the geos ).
     * Moreover the method will check if the current ble-device is further or closer to the one,
     * found before. In case the current one is closer, it will display its longitude, latitude and
     * altitude in the given EditTexts of this class, if the broadcasted String includes them.
     * The ScanCallback will only use ScanCallback.onScanResult.
     */
    private void buildScanCallbackAP18(){
        leScanCallback = new BluetoothAdapter.LeScanCallback() {
            @Override
            public void onLeScan(BluetoothDevice device, int rssi, byte[] scanRecord) {
                String deviceName = device.getName();
                if ( checkRange(rssi)) {
                    if( deviceName == null )
                        Log.i(TAG, "Found device with no name.");
                    else
                        Log.i(TAG, "Found: " + device.getName() );
                    Log.i(TAG, "MAC: " + device.getAddress());
                    String bytes = new String(scanRecord, Charset.defaultCharset());
                    List<String> geos = Util.readPropperGEO(bytes);
                    longitude.setText(geos.get(0));
                    latidue.setText(geos.get(1));
                    altitude.setText(geos.get(2));
                }
            }
        };
    }

    /**
     * Method to check if a given received signal strength indication is grater, lesser or equal to
     * the one before.
     * In case the new rssi is lesser than the one before, the rssi value of the current BLESniffer
     * object will be overwritten to the current one and the method will return true. This indicates,
     * that the new ble-device is the closest one to the mobile and the ble-device's data can be print
     * on screen.
     * @param rssi  The received signal strength indication of the current ble-device.
     * @return  True if given rssi is lesser than the one before. False if not or equal.
     */
    private boolean checkRange(int rssi){
        if(this.rssi > rssi ) {
            Log.i(TAG, "New signal strength " + rssi + " is further [ OLD: " + this.rssi + "]");
            return false;
        } else if ( this.rssi == rssi ) {
            Log.i(TAG, "New signal strength " + rssi + " is the same. [ OLD: " + this.rssi + "]");
            return false;
        }
        else {
            Log.i(TAG, "New signal strength " + rssi + " is closer [ OLD: " + this.rssi + "]");
            this.rssi = rssi;
            return true;
        }
    }

    /*
     * Method to stop a current scan for ble-devices.
     * It will print a warning notification on log-level. Moreover it will check, what API level
     * is in use and decides which scanCallback should be stopped. At least the bleButton Image
     * will be changed to indicate this stop visually.
     */
    public void stop(){
        Log.w(TAG, "Stopped ble-scan");
        if (Build.VERSION.SDK_INT > Build.VERSION_CODES.LOLLIPOP_MR1) {
            bluetoothAdapter.getBluetoothLeScanner().stopScan(scanCallback);
            bleButton.setImageResource(R.mipmap.bluetooth_off);
        } else {
            //noinspection deprecation
            bluetoothAdapter.stopLeScan(leScanCallback);
            bleButton.setImageResource(R.mipmap.bluetooth_off);
        }
    }

    /*
     * Method to print a AlertDialog, where the user can decide either to enable bluetooth or not.
     * It will first check, if the mobiles Bluetooth is already enabled or not. If so, it will just
     * print a notification about it on log-level. If not the method will show an AlertDialog with
     * some user addressed information what is going on. If the user decides to enable Bluetooth,
     * a new Intent will be created, letting Android enable Bluetooth by its self. Else the AlertDialog
     * will be just closed.
     */
    private void enableBluetooth(){
        if( bluetoothAdapter.isEnabled()) {
            Log.i(TAG, "Bluetooth is on.");
        } else {
            Log.w(TAG, "Bluetooth not enabled. Can't scan for beacons. Print AlertDialog.");
            AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(activity);
            alertDialogBuilder.setTitle("¯\\_(ツ)_/¯");
            alertDialogBuilder.setMessage(R.string.bluetooth_not_enabled)
                    .setCancelable(false)
                    .setPositiveButton(R.string.bluetooth_safe_enable, new DialogInterface.OnClickListener() {
                                public void onClick(DialogInterface dialog, int id) {
                                    Intent enable = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
                                    activity.startActivityForResult(enable, REQUEST_ENABLE_BT);
                                }
                            });
            alertDialogBuilder.setNegativeButton(R.string.abort, new DialogInterface.OnClickListener(){
                        public void onClick(DialogInterface dialog, int id){
                            bleButton.setImageResource(R.mipmap.bluetooth_off);
                            dialog.cancel();
                        }
                    });
            AlertDialog alert = alertDialogBuilder.create();
            alert.show();
        }
    }

    /*
     * Method to print a AlertDialog, where the user can decide either to enable location service or not.
     * It will first check, if the mobiles location service is already enabled or not. If so, it will just
     * print a notification about it on log-level. If not the method will show an AlertDialog with
     * some user addressed information what is going on. If the user decides to enable location service,
     * a new Intent will be created. This will open the mobiles location service settings. Else the
     * AlertDialog will be just closed.
     */
    private void enableLocation(){
        if(locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER)) {
            Log.i(TAG, "GPS is on. App can scan for beacons. Print AlertDialog.");
        } else {
            Log.w(TAG, "GPS is not enabled. Can't scan for beacons");
            AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(activity);
            alertDialogBuilder.setTitle("¯\\_(ツ)_/¯");
            alertDialogBuilder.setMessage(R.string.location_enable)
                    .setCancelable(false)
                    .setPositiveButton(R.string.open_location_settings, new DialogInterface.OnClickListener() {
                                public void onClick(DialogInterface dialog, int id) {
                                    activity.startActivity(new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS));
                                }
                            });
            alertDialogBuilder.setNegativeButton(R.string.abort,
                    new DialogInterface.OnClickListener(){
                        public void onClick(DialogInterface dialog, int id){
                            bleButton.setImageResource(R.mipmap.bluetooth_off);
                            dialog.cancel();
                        }
                    });
            AlertDialog alert = alertDialogBuilder.create();
            alert.show();
        }
    }

}
