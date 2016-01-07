package bluetoothmanager;

import android.app.Activity;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.util.Log;
import android.widget.Toast;

/**
 * This class creates a BeaconManager object.
 * A BeaconManager is an object, to listen on several Bluetooth adapter broadcast
 * actions and provides some magic stuff. Like unicorns. Black unicorns. Black unicorns
 * from outerspace with laser horns.
 */

public class BeaconManager extends BroadcastReceiver implements BeaconActions {

    public static final String DISCOVERY_STARTED = BluetoothAdapter.ACTION_DISCOVERY_STARTED;
    public static final String DISCOVERY_FINISHED = BluetoothAdapter.ACTION_DISCOVERY_FINISHED;
    public static final String STATE_CHANGED = BluetoothAdapter.ACTION_STATE_CHANGED;
    public static final String REQUEST_ENABLE = BluetoothAdapter.ACTION_REQUEST_ENABLE;
    public static final String FOUND_DEVICE = BluetoothDevice.ACTION_FOUND;

    private BluetoothAdapter bluetoothAdapter = null;
    private boolean isEnabled;
    private boolean isDisabled;
    private boolean isDiscovering;
    private Context context = null;
    private Activity activity = null;
    public final static String className = "Beacon-Manager";

    public BeaconManager(){
        bluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
    }

    public BeaconManager(Activity activity){
        bluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
        this.activity = activity;
    }

    @Override
    public void enableDeviceBluetooth() {
        Intent enableIntent = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
        activity.startActivity(enableIntent);
    }

    @Override
    public boolean disableDeviceBluethooth() {
        isDisabled = bluetoothAdapter.disable();
        if ( isDisabled )
            Log.i(className, "Enabling bluetooth returns: " + isDisabled);
        else
            Log.w(className, "Enabling bluetooth returns: " + isDisabled);
        return isDisabled;
    }
    @Override
    public void discoverBeacons() {
        isDiscovering = bluetoothAdapter.startDiscovery();
        if ( isDiscovering )
            Log.i(className, "Discovering beacons returns: " + isDiscovering );
        else
            Log.w(className, "Discovering beacons returns: " + isDiscovering );
    }

    /**
     *  Aborting the current Beacon-Manager.
     *  This will stop the search for Bluetooth devices. Call this method in onStop(), if you want
     *  to stop the search for devices as soon as the App is no longer visible.
     */
    public void abort(){
        Log.i(className, "Aborting");
        PackageManager packageManager = activity.getPackageManager();
        ComponentName componentName = new ComponentName(activity.getApplicationContext(), BeaconManager.class);
        packageManager.setComponentEnabledSetting(componentName, PackageManager.COMPONENT_ENABLED_STATE_DISABLED,
                                                    PackageManager.DONT_KILL_APP);
    }

    /**
     *  Enabling the current Beacon-Manager.
     *  This will enable the search for Bluetooth devices. Call this method in onResume() ( and in
     *  onCreate(), if you set android:enabled="false" in your manifest ) , if you want
     *  to start the search for devices as soon as the App is visible again. 
     */
    public void enable(){
        Log.i(className, "Enabling");
        PackageManager packageManager = activity.getPackageManager();
        ComponentName componentName = new ComponentName(activity.getApplicationContext(), BeaconManager.class);
        packageManager.setComponentEnabledSetting(componentName, PackageManager.COMPONENT_ENABLED_STATE_ENABLED,
                PackageManager.DONT_KILL_APP);
    }


    @Override
    public String getBeaconName() {
        return null;
    }

    @Override
    public void changeBeaconName() {

    }

    @Override
    public String getBeaconURL() {
        return null;
    }

    @Override
    public void changeBeaconURL() {

    }

    /**
     *
     * @param context Context -> Read BroadcastReceiver.onReceive docu
     * @param intent Intent -> Read BroadcastReceiver.onReceive docu
     */
    @Override
    public void onReceive(Context context, Intent intent) {
        String action = intent.getAction();
        if ( action.equals(BluetoothAdapter.ACTION_STATE_CHANGED) && bluetoothAdapter.isEnabled() ) {
            Log.i(className, "The state of the local Bluetooth adapter has been changed to ON.");
            discoverBeacons();
        }
        if ( action.equals(BluetoothAdapter.ACTION_DISCOVERY_FINISHED)) {
            Log.i(className, "The local Bluetooth adapter has finished the device discovery process.");
            discoverBeacons();
        }

        if ( action.equals(BluetoothDevice.ACTION_FOUND)) {
            BluetoothDevice device = intent.getParcelableExtra(BluetoothDevice.EXTRA_DEVICE);
            Log.i(className, "Name: " + device.getName() + " Addr: " + device.getAddress());
        }
    }
}
