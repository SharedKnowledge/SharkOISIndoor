package compenomatikus.httpsgithub.ois_admin;

import android.Manifest;
import android.bluetooth.BluetoothDevice;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.FragmentManager;
import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import bluetoothmanager.BeaconManager;

public class AdminMain extends AppCompatActivity implements AdminFragment.OnFragmentInteractionListener{

    private BeaconManager beaconManager = null;
    public static final String className = "AdminMain";
    private int permissionCheck;
    private final int REQUEST_ACCESS_COARSE_LOCATION = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_admin);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);


        Log.i(BeaconManager.className, "Bluetooth-Manager for normal bluetooth devices started.");
        beaconManager = new BeaconManager(this);
        beaconManager.enable();
        beaconManager.enableDeviceBluetooth();
        beaconManager.discoverBeacons();
        // Check if the app has rights to ACCESS_COARSE_LOCATION
        permissionCheck = ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION);
        Log.i(className, permissionCheck + "");

        if ( permissionCheck != PackageManager.PERMISSION_GRANTED ) {
            String[] permissions = { Manifest.permission.ACCESS_COARSE_LOCATION };
            ActivityCompat.requestPermissions(AdminMain.this, permissions, REQUEST_ACCESS_COARSE_LOCATION);
        }



        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
                        .setAction("Action", null).show();
            }
        });

        FragmentManager fragmentManager = getSupportFragmentManager();
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
        AdminFragment adminFragment = new AdminFragment();
        fragmentTransaction.add(R.id.fragment, adminFragment);
        fragmentTransaction.commit();
    }


    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        Log.i(className, "onRequestPermissionsResult");
        switch (requestCode) {
            case REQUEST_ACCESS_COARSE_LOCATION: {
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED){
                    Log.i(className, "Permission garanted");
                    IntentFilter filter = new IntentFilter(BluetoothDevice.ACTION_FOUND);
                    Log.i(BeaconManager.className, "Garanted permission to find broadcasting devices.");
                    registerReceiver(beaconManager, filter);
                } else {
                    Log.i(BeaconManager.className, "Perission dinied to find broadcasting devices.");
                    Toast.makeText(AdminMain.this, "Will not scan for devices", Toast.LENGTH_SHORT).show();
                }
                return;
            }
        }
    }

    @Override
    protected void onResume() {
        Log.i(className, "Resume");
        beaconManager.enable();
        super.onResume();
    }

    @Override
    protected void onStop() {
        Log.i(className, "Stop.");
        beaconManager.abort();
        super.onStop();
    }

    @Override
    protected void onDestroy() {
        Log.i(className, "Destroid");
        beaconManager.abort();
        super.onDestroy();
    }

    @Override
    public void onFragmentInteraction(Uri uri) {

    }

}