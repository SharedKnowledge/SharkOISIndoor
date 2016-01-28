package de.berlin.htw.oisindoor.userapp.positioning;

import android.app.Service;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothManager;
import android.bluetooth.le.ScanCallback;
import android.content.Context;
import android.content.Intent;
import android.os.IBinder;
import android.support.annotation.Nullable;
import android.support.v4.content.LocalBroadcastManager;
import android.util.Log;

import java.nio.charset.Charset;

import de.berlin.htw.oisindoor.userapp.util.Util;

/**
 * Service for searching Bluetooth LE Signals</br></br>
 * This Class abstracts the searching from the Android version specific implementation</br>
 * Only access this Service via it's static methods:
 * <ul>
 *     <li>start via {@link #startService}</li>
 *     <li>stop via {@link #stopService}</li>
 * </ul>
 *
 * Callback (found signals (representing an location with latitude, longitude and altitude) from beacons)
 * will only received the strongest signal from any beacons.
 * If a beacons signal is stronger then the latest received signal, an callback will be thrown.</br>
 * If a error occurs, an error callback will be thrown.</br>
 *
 * Callback can be received on any {@link android.content.BroadcastReceiver} in a local Context {@link LocalBroadcastManager}}</br>
 * They need to listen for {@link #RESPONSE_LOCATION} and {@link #RESPONSE_ERROR} Action value in their {@link android.content.IntentFilter}</br>
 * Then, they can get a String extra: {@link #RESPONSE_LOCATION_VALUE} or {@link #RESPONSE_ERROR_VALUE}</br>
 *
 * e.g. Callback via BroadcastReceiver</br>
 * <code>
 *   @Override
     public void onReceive(Context context, Intent intent) {
       switch (intent.getAction()){
        case BTLEService.RESPONSE_LOCATION:
         String url = intent.getStringExtra(BTLEService.RESPONSE_LOCATION_VALUE);
         // ...
         break;

        case BTLEService.RESPONSE_ERROR:
         String errorMsg = intent.getStringExtra(BTLEService.RESPONSE_ERROR_VALUE);
         // ...
         break;
        }
     }</code>
    </br>
    Register Receiver:
    <code>
        IntentFilter filter = new IntentFilter();
        filter.addAction(BTLEService.RESPONSE_LOCATION);
        filter.addAction(BTLEService.RESPONSE_ERROR);
        LocalBroadcastManager.getInstance(**activity**).registerReceiver(**receiver**, filter);
    </code>
 *
 * @see BTLEv18Service
 * @see BTLEv21Service
 * @author Max M
 */
public abstract class BTLEService extends Service {
    public static final String RESPONSE_LOCATION = "de.berlin.htw.oisindoor.userapp.positioning.location";
    public static final String RESPONSE_LOCATION_VALUE = "de.berlin.htw.oisindoor.userapp.positioning.location_value";
    public static final String RESPONSE_ERROR = "de.berlin.htw.oisindoor.userapp.positioning.error";
    public static final String RESPONSE_ERROR_VALUE = "de.berlin.htw.oisindoor.userapp.positioning.error_value";

    /**
     * starts the Service, which scans for bluetooth le signals</vr>
     * Results can be received via a Broadcastreceiver
     * @see BTLEService
     * @param context - Context
     */
    public static void startService(Context context) {
        Class clazz = Util.isLollipop() ? BTLEv21Service.class : BTLEv18Service.class;
        Intent startIntent = new Intent(context, clazz);
        startIntent.putExtra(ACTION, ACTION_START);
        context.startService(startIntent);
    }

    /**
     * stops the Service, no more result can be received
     * @param context - Context
     */
    public static void stopService(Context context){
        Class clazz = Util.isLollipop() ? BTLEv21Service.class : BTLEv18Service.class;
        Intent stopIntent = new Intent(context, clazz);
        stopIntent.putExtra(ACTION, ACTION_STOP);
        context.startService(stopIntent);
    }

    /**
     * Log Tag
     */
    private static final String TAG = BTLEService.class.getSimpleName();
    /**
     * Used charset to parse the beacons content
     */
    static final Charset UTF8 = Charset.forName("UTF-8");
    static final String ACTION = "btleAction";
    /**
     * startCommand signal, which indicates to start the service
     */
    static final int ACTION_START = 78;
    /**
     * startCommand signal, which indicates to stop the service
     */
    static final int ACTION_STOP = 79;

    /**
     * the latest signal strength of the current bluetooth device
     * it's used for calculating if an update is necessary
     */
    int currentRSSI;
    /**
     * the current Bluetooth device (beacon), is is used until another device is closer
     * to the user
     */
    BluetoothDevice currentBeacon;
    /**
     * BluetoothAdapter, the interface for accessing the android bluetooth api
     */
    BluetoothAdapter bluetoothAdapter;

    @Override
    public void onCreate() {
        bluetoothAdapter = ((BluetoothManager) getSystemService(Context.BLUETOOTH_SERVICE)).getAdapter();
        currentRSSI = Integer.MIN_VALUE;
    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        int action = intent.getIntExtra(ACTION, -1);
        Log.d(TAG, "onStartCommand: " + action);
        switch (action) {
            case ACTION_START:
                startScanning();
                break;

            case ACTION_STOP:
                stop();
                break;

            default:
                stop();
                break;
        }
        return START_NOT_STICKY;
    }

    abstract void startScanning();

    abstract void stopScanning();

    void stop() {
        stopScanning();
        stopSelf();
    }

    /**
     * checks if a received beacon </br>
     * if it's closer to the user, a callback will be thrown via a Broadcast</br>
     * the current device und signal strength are update too.
     *
     * @param device - device, which
     * @param rssi - signal strength of the device
     * @return true, if an update via a Broadcast should be thrown
     * else false
     */
    boolean isANewBeacon(BluetoothDevice device, int rssi) {
        if (currentBeacon == null) { // first signal
            currentBeacon = device;
            currentRSSI = rssi;
            return true;
        } else if (currentBeacon.equals(device)) { // correct rssi strength of the current device
            currentRSSI = rssi;
        } else if (currentRSSI < rssi) { // a new device is closer
            currentBeacon = device;
            currentRSSI = rssi;
            return true;
            //} else { // a new device is further
            // no need
        }
        return false;
    }

    /**
     * send a Broadcast with the Key {@link #RESPONSE_LOCATION} and value {@link #RESPONSE_LOCATION_VALUE}
     * @param url - received string from any beacon
     */
    void sendLocationToActivity(String url){
        Intent i = new Intent(RESPONSE_LOCATION);
        i.putExtra(RESPONSE_LOCATION_VALUE, url);
        LocalBroadcastManager.getInstance(this).sendBroadcast(i);
    }

    /**
     * send a Broadcast with the Key {@link #RESPONSE_ERROR} and value {@link #RESPONSE_ERROR_VALUE}
     * @param errorMsg - an error message, which should help the user fix the error
     */
    void sendErrorToActivity(String errorMsg){
        Intent i = new Intent(RESPONSE_ERROR);
        i.putExtra(RESPONSE_ERROR_VALUE, errorMsg);
        LocalBroadcastManager.getInstance(this).sendBroadcast(i);
    }

    /**
     * simple method to convert android error code to human readable string
     * @param code - android error code
     * @return - readable string
     */
    static String errorCodeToString(int code) {
        switch (code){
            case ScanCallback.SCAN_FAILED_ALREADY_STARTED:
                return "SCAN_FAILED_ALREADY_STARTED";
            case ScanCallback.SCAN_FAILED_APPLICATION_REGISTRATION_FAILED:
                return "SCAN_FAILED_APPLICATION_REGISTRATION_FAILED";
            case ScanCallback.SCAN_FAILED_FEATURE_UNSUPPORTED:
                return "SCAN_FAILED_FEATURE_UNSUPPORTED";
            case ScanCallback.SCAN_FAILED_INTERNAL_ERROR:
                return "SCAN_FAILED_INTERNAL_ERROR";
            default:
                return "Unknown: " + code;
        }
    }

}
