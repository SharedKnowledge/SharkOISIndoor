package bluetoothmanager;

/**
 * Created by stefan on 17.12.15.
 */
public interface BeaconActions {
    public String getBeaconName();
    public void changeBeaconName();
    public String getBeaconURL();
    public void changeBeaconURL();
    public void discoverBeacons();
    public boolean disableDeviceBluethooth();
    public void enableDeviceBluetooth();
}
