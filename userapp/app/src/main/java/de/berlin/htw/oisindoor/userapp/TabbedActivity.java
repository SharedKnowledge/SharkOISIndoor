package de.berlin.htw.oisindoor.userapp;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.os.Environment;
import android.support.annotation.NonNull;
import android.support.annotation.StringRes;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.content.LocalBroadcastManager;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;

import java.io.File;
import java.util.List;

import butterknife.Bind;
import butterknife.ButterKnife;
import de.berlin.htw.oisindoor.userapp.fragments.PositioningFragment;
import de.berlin.htw.oisindoor.userapp.fragments.NoteFragment;
import de.berlin.htw.oisindoor.userapp.model.GeoCoordinate;
import de.berlin.htw.oisindoor.userapp.positioning.BTLEService;
import de.berlin.htw.oisindoor.userapp.positioning.technology.BluetoothLeStrengthTechnology;
import de.hadizadeh.positioning.controller.PositionListener;
import de.hadizadeh.positioning.controller.PositionManager;
import de.hadizadeh.positioning.exceptions.PositioningException;
import de.hadizadeh.positioning.exceptions.PositioningPersistenceException;
import de.hadizadeh.positioning.model.PositionInformation;

public class TabbedActivity extends AppCompatActivity implements NoteFragment.OnListFragmentInteractionListener {
    private static final String TAG = TabbedActivity.class.getSimpleName();
    private PositionManager positionManager;
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

        Toolbar toolbar = (Toolbar) findViewById(R.id.ac_tabbed_toolbar);
        setSupportActionBar(toolbar);

        sectionsPagerAdapter = new SectionsPagerAdapter(getSupportFragmentManager());

        viewPager.setAdapter(sectionsPagerAdapter);

        TabLayout tabLayout = (TabLayout) findViewById(R.id.ac_tabbed_tablayout);
        tabLayout.setupWithViewPager(viewPager);

        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.ac_tabbed_fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                showUIMessage("Floating Action Button");
                if (positionManager != null) {
                    Log.d(TAG, "startPositioning");
                    positionManager.startPositioning(250);
                }
            }
        });

        btleReceiver = new BTLEReceiver();
        localBroadcastManager = LocalBroadcastManager.getInstance(this);
    }

    @Override
    protected void onStart() {
        super.onStart();
//        setUpPositionManager();
        localBroadcastManager.registerReceiver(btleReceiver, new IntentFilter(BTLEService.ACTION_LOCATION));

        Intent serviceIntent = new Intent(this, BTLEService.class);
        serviceIntent.putExtra(BTLEService.ACTION, BTLEService.ACTION_START);
        startService(serviceIntent);
    }

    @Override
    protected void onStop() {
        super.onStop();
//        stopPositionManager();
        if (btleReceiver != null) {
            localBroadcastManager.unregisterReceiver(btleReceiver);
        }

        Intent stopIntent = new Intent(this, BTLEService.class);
        stopIntent.putExtra(BTLEService.ACTION, BTLEService.ACTION_STOP);
        startService(stopIntent);
    }

    private void setUpPositionManager() {
        Log.d(TAG, "setUpPositionManager");

        File file = new File(Environment.getExternalStorageDirectory(), "positioningPersistence.xml");
        try {
            positionManager = new PositionManager(file);
            positionManager.addTechnology(new BluetoothLeStrengthTechnology("BLUETOOTH_LE_STRENGTH", 4000, null));
            Log.d(TAG, "initialized");
        } catch (PositioningPersistenceException e) { // never happen
            e.printStackTrace();
        } catch (PositioningException e) {
            e.printStackTrace();
        }

        positionManager.registerPositionListener(new PositionListener() {
            @Override
            public void positionReceived(PositionInformation positionInformation) {
                Log.d(TAG, "" + positionInformation);
            }

            @Override
            public void positionReceived(List<PositionInformation> list) {
                Log.d(TAG, "" + list);
            }
        });
    }

    private void stopPositionManager() {
        Log.d(TAG, "stopPositionManager");
        if (positionManager != null){
            positionManager.stopPositioning();
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
            case R.id.action_settings:
                showUIMessage("Settings");
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    @Override
    public void onListClicked(@NonNull GeoCoordinate item) {
        showUIMessage(item.toString());
    }

    private void showUIMessage(CharSequence s) {
        Snackbar.make(content, s, Snackbar.LENGTH_SHORT).show();
    }

    private void showUIMessage(@StringRes int res) {
        Snackbar.make(content, res, Snackbar.LENGTH_SHORT).show();
    }

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
            switch (position) {
                case 0:
                    return "Positioning";
                case 1:
                    return "Notes";
                default:
                    throw new IllegalArgumentException("wrong Position: " + position);
            }
        }
    }

    private class BTLEReceiver extends BroadcastReceiver {
        @Override
        public void onReceive(Context context, Intent intent) {
            String url = intent.getStringExtra(BTLEService.ACTION_LOCATION_VALUE).substring(2);
            Fragment f = sectionsPagerAdapter.getItem(0);
            if (f instanceof PositioningFragment) {
                ((PositioningFragment) f).updatePosition(url);
            }
        }
    }

}
