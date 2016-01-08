package de.berlin.htw.oisindoor.userapp.positioning;

import android.app.Service;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothManager;
import android.bluetooth.le.ScanCallback;
import android.content.Context;
import android.content.Intent;
import android.os.IBinder;
import android.support.annotation.Nullable;
import android.support.v4.content.LocalBroadcastManager;

import java.nio.charset.Charset;

import de.berlin.htw.oisindoor.userapp.util.PermissionUtil;

/**
 * Created by Max on 19.12.2015.
 */
public abstract class BTLEService extends Service {
    public static final String RESPONSE_LOCATION = "de.berlin.htw.oisindoor.userapp.positioning.location";
    public static final String RESPONSE_LOCATION_VALUE = "de.berlin.htw.oisindoor.userapp.positioning.location_value";
    public static final String RESPONSE_ERROR = "de.berlin.htw.oisindoor.userapp.positioning.error";
    public static final String RESPONSE_ERROR_VALUE = "de.berlin.htw.oisindoor.userapp.positioning.error_value";

    public static void startService(Context context) {
        Class clazz = PermissionUtil.isLollipop() ? BTLEv21Service.class : BTLEv19Service.class;
        Intent startIntent = new Intent(context, clazz);
        startIntent.putExtra(ACTION, ACTION_START);
        context.startService(startIntent);
    }

    public static void stopService(Context context){
        Class clazz = PermissionUtil.isLollipop() ? BTLEv21Service.class : BTLEv19Service.class;
        Intent stopIntent = new Intent(context, clazz);
        stopIntent.putExtra(ACTION, ACTION_STOP);
        context.startService(stopIntent);
    }

    static final String ACTION = "btleAction";
    static final int ACTION_START = 78;
    static final int ACTION_STOP = 79;
    static final Charset UTF8 = Charset.forName("UTF-8");
    BluetoothAdapter bluetoothAdapter;

    @Override
    public void onCreate() {
        super.onCreate();
        BluetoothManager bluetoothManager = (BluetoothManager) getSystemService(Context.BLUETOOTH_SERVICE);
        bluetoothAdapter = bluetoothManager.getAdapter();
    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public abstract int onStartCommand(Intent intent, int flags, int startId);

    abstract void stopScanning();

    void sendLocationToActivity(String url){
        Intent i = new Intent(RESPONSE_LOCATION);
        i.putExtra(RESPONSE_LOCATION_VALUE, url);
        LocalBroadcastManager.getInstance(this).sendBroadcast(i);
    }

    void sendErrorToActivity(String errorMsg){
        Intent i = new Intent(RESPONSE_ERROR);
        i.putExtra(RESPONSE_ERROR_VALUE, errorMsg);
        LocalBroadcastManager.getInstance(this).sendBroadcast(i);
    }

    String errorCodeToString(int code) {
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
