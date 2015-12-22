package de.berlin.htw.oisindoor.userapp;

import android.Manifest;
import android.app.AlertDialog;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothManager;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.StringRes;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.content.ContextCompat;
import android.support.v4.content.LocalBroadcastManager;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;

import com.karumi.dexter.Dexter;
import com.karumi.dexter.MultiplePermissionsReport;
import com.karumi.dexter.PermissionToken;
import com.karumi.dexter.listener.PermissionRequest;
import com.karumi.dexter.listener.multi.MultiplePermissionsListener;

import java.util.List;

import butterknife.Bind;
import butterknife.ButterKnife;
import de.berlin.htw.oisindoor.userapp.fragments.IPositioning;
import de.berlin.htw.oisindoor.userapp.fragments.NoteFragment;
import de.berlin.htw.oisindoor.userapp.fragments.PositioningFragment;
import de.berlin.htw.oisindoor.userapp.model.GeoCoordinate;
import de.berlin.htw.oisindoor.userapp.positioning.BTLEService;

public class TabbedActivity extends AppCompatActivity implements NoteFragment.OnListFragmentInteractionListener {
    private static final String TAG = TabbedActivity.class.getSimpleName();
    private static final int ACTION_REQUEST_BT = 789;

    private BTLEReceiver btleReceiver;
    private LocalBroadcastManager localBroadcastManager;
    private SectionsPagerAdapter sectionsPagerAdapter;
    @Bind(R.id.ac_tabbed_vp) ViewPager viewPager;
    @Bind(R.id.ac_tabbed_main_content) View content;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.ac_tabbed);
        ButterKnife.bind(this);
        Dexter.initialize(getApplicationContext());

        Toolbar toolbar = (Toolbar) findViewById(R.id.ac_tabbed_toolbar);
        setSupportActionBar(toolbar);

        sectionsPagerAdapter = new SectionsPagerAdapter(getSupportFragmentManager());

        viewPager.setAdapter(sectionsPagerAdapter);

        TabLayout tabLayout = (TabLayout) findViewById(R.id.ac_tabbed_tablayout);
        tabLayout.setupWithViewPager(viewPager);
        tabLayout.getTabAt(0).setIcon(R.drawable.posselector);
        tabLayout.getTabAt(1).setIcon(R.drawable.notesselector);

        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.ac_tabbed_fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startSearching();
            }
        });

        btleReceiver = new BTLEReceiver();
        localBroadcastManager = LocalBroadcastManager.getInstance(this);
    }

    @Override
    protected void onStart() {
        super.onStart();
        startSearching();
        IntentFilter filter = new IntentFilter();
        filter.addAction(BTLEService.RESPONSE_LOCATION);
        filter.addAction(BTLEService.RESPONSE_ERROR);
        localBroadcastManager.registerReceiver(btleReceiver, filter);
    }

    @Override
    protected void onStop() {
        super.onStop();
        if (btleReceiver != null) {
            localBroadcastManager.unregisterReceiver(btleReceiver);
        }
        BTLEService.stopService(this);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        switch (requestCode){
            case ACTION_REQUEST_BT:
                if (resultCode == RESULT_OK) { // is enabled now
                    startSearchingForBeacons();
                } else {
                    showUIMessage(R.string.bt_permission_enable_text);
                }
                break;
            default:
                Log.w(TAG, "unknown Action: " + requestCode + " " + resultCode + " " + data);
        }
    }

    private void startSearching() {
        if (checkBTPermission()){
            checkBTisActive();
        }
    }

    private boolean checkBTPermission() {
        // check permissions
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.BLUETOOTH) == PackageManager.PERMISSION_GRANTED &&
            ContextCompat.checkSelfPermission(this, Manifest.permission.BLUETOOTH_ADMIN) == PackageManager.PERMISSION_GRANTED) {
            return true;
        } else {
            Dexter.checkPermissions(new MultiplePermissionsListener() {
                @Override
                public void onPermissionsChecked(MultiplePermissionsReport report) {
                    Log.d(TAG, "onPermissionsChecked");
                    if (!report.areAllPermissionsGranted()) {
                        new AlertDialog.Builder(TabbedActivity.this)
                                .setCancelable(true)
                                .setIcon(R.mipmap.perm_group_bluetooth)
                                .setTitle(R.string.bt_permission)
                                .setMessage(R.string.bt_permission_text)
                                .setNegativeButton(android.R.string.cancel, new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialog, int which) {
                                        dialog.dismiss();
                                        finish();
                                    }
                                })
                                .show();
                    } else {
                        checkBTisActive();
                    }
                }

                @Override
                public void onPermissionRationaleShouldBeShown(List<PermissionRequest> permissions, PermissionToken token) {
                    Log.d(TAG, "onPermissionRationaleShouldBeShown");
                }

            }, Manifest.permission.BLUETOOTH, Manifest.permission.BLUETOOTH_ADMIN);
            return false;
        }
    }


    private void checkBTisActive() {
        BluetoothManager bluetoothManager = (BluetoothManager) getSystemService(Context.BLUETOOTH_SERVICE);
        BluetoothAdapter bluetoothAdapter = bluetoothManager.getAdapter();
        if (bluetoothAdapter.isEnabled()) {
            startSearchingForBeacons();
        } else {
            startActivityForResult(new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE), ACTION_REQUEST_BT);
        }
    }

    private void startSearchingForBeacons(){
        BTLEService.startService(TabbedActivity.this);
        Fragment f = sectionsPagerAdapter.getItem(0);
        if (f instanceof IPositioning) {
            ((IPositioning) f).showSearchingDialog();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_tabbed, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.action_bluetooth_settings:
                startActivity(new Intent(android.provider.Settings.ACTION_BLUETOOTH_SETTINGS));
                return true;
            case R.id.action_settings:
                showUIMessage(R.string.action_settings);
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    /**
     * {@link de.berlin.htw.oisindoor.userapp.fragments.NoteFragment.OnListFragmentInteractionListener#onListClicked(GeoCoordinate)}
     */
    @Override
    public void onListClicked(@NonNull GeoCoordinate item) {
        showUIMessage(item.toString());
    }

    /*
     * Stuff
     */

    private void showUIMessage(@NonNull CharSequence s) {
        Snackbar.make(content, s, Snackbar.LENGTH_SHORT).show();
    }

    private void showUIMessage(@StringRes int res) {
        Snackbar.make(content, res, Snackbar.LENGTH_SHORT).show();
    }

    /*
     * Classes
     */

    private class SectionsPagerAdapter extends FragmentPagerAdapter {
        private PositioningFragment p = PositioningFragment.newInstance();
        private NoteFragment n = NoteFragment.newInstance(GeoCoordinate.ITEMS);

        public SectionsPagerAdapter(FragmentManager fm) {
            super(fm);
        }

        @Override
        public Fragment getItem(int position) {
            switch (position) {
                case 0:
                    return p;
                case 1:
                    return n;
                default:
                    throw new IllegalArgumentException("wrong Position: " + position);
            }
        }

        @Override
        public int getCount() {
            return 2;
        }

        @Override
        public CharSequence getPageTitle(int position) {
            return null;
        }
    }

    private class BTLEReceiver extends BroadcastReceiver {
        @Override
        public void onReceive(Context context, Intent intent) {
            switch (intent.getAction()){
                case BTLEService.RESPONSE_LOCATION:
                    Fragment f = sectionsPagerAdapter.getItem(0);
                    if (f instanceof IPositioning) {
                        String url = intent.getStringExtra(BTLEService.RESPONSE_LOCATION_VALUE);
                        if (url == null) {
                            ((IPositioning) f).cancelSearchingDialog();
                        } else {
                            ((IPositioning) f).updatePosition(url);
                        }
                    }
                    break;

                case BTLEService.RESPONSE_ERROR:
                    showUIMessage(intent.getStringExtra(BTLEService.RESPONSE_ERROR_VALUE));
                    cancelSearchingDialog();
                    break;

                default:
                    Log.w(TAG, "unknown action: " + intent.getAction());
                    break;
            }
        }

        private void cancelSearchingDialog() {
            Fragment f = sectionsPagerAdapter.getItem(0);
            if (f instanceof IPositioning) {
                ((IPositioning) f).cancelSearchingDialog();
            }
        }
    }

}
