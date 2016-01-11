package de.berlin.htw.oisindoor.userapp;

import android.Manifest;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.bluetooth.BluetoothAdapter;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.database.MatrixCursor;
import android.net.Uri;
import android.os.Bundle;
import android.provider.BaseColumns;
import android.provider.Settings;
import android.support.annotation.NonNull;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.content.ContextCompat;
import android.support.v4.content.LocalBroadcastManager;
import android.support.v4.view.ViewPager;
import android.support.v4.widget.CursorAdapter;
import android.support.v4.widget.SimpleCursorAdapter;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.SearchView;
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

import java.util.ArrayList;
import java.util.List;

import butterknife.Bind;
import butterknife.ButterKnife;
import de.berlin.htw.oisindoor.userapp.fragments.IPositioning;
import de.berlin.htw.oisindoor.userapp.fragments.NoteFragment;
import de.berlin.htw.oisindoor.userapp.fragments.PositioningFragment;
import de.berlin.htw.oisindoor.userapp.model.GeoCoordinate;
import de.berlin.htw.oisindoor.userapp.model.Topic;
import de.berlin.htw.oisindoor.userapp.positioning.BTLEService;
import de.berlin.htw.oisindoor.userapp.util.Util;

public class TabbedActivity extends AppCompatActivity implements NoteFragment.OnListFragmentInteractionListener {
    private static final String TAG = TabbedActivity.class.getSimpleName();
    private static final int REQUEST_IS_BLUETOOTH_ENABLED = 788;
    private static final int REQUEST_IS_LOCATION_ENABLED = 789;
    private static final int REQUEST_IS_NETWORK_ENABLED = 780;

    private boolean isSearching = false;
    private boolean hasBeaconFound = false;
    private ProgressDialog dialog;
    private BTLEReceiver btleReceiver;
    private IntentFilter filter;
    private LocalBroadcastManager localBroadcastManager;
    private SectionsPagerAdapter sectionsPagerAdapter;
    private SimpleCursorAdapter suggestionAdapter;
    private SearchView searchView;
    @Bind(R.id.ac_tabbed_vp) ViewPager viewPager;
    @Bind(R.id.ac_tabbed_main_content) View content;

    // handle Permissions
    private boolean hasNecessaryPermissions;
    private boolean isLocationEnabled;
    private boolean isBluetoothEnabled;
    private boolean isNetworkAvailable;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.ac_tabbed);
        ButterKnife.bind(this);
        Dexter.initialize(this);

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
                hasBeaconFound = false;
                startSearching();
            }
        });

        btleReceiver = new BTLEReceiver();
        localBroadcastManager = LocalBroadcastManager.getInstance(this);

        filter = new IntentFilter();
        filter.addAction(BTLEService.RESPONSE_LOCATION);
        filter.addAction(BTLEService.RESPONSE_ERROR);
    }

    @Override
    protected void onStart() {
        super.onStart();
        startSearching();
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
            case REQUEST_IS_BLUETOOTH_ENABLED:
                isBluetoothEnabled = (resultCode == RESULT_OK);
                if (!isBluetoothEnabled) {
                    Util.showUIMessage(content, R.string.enable_requirements);
                }
                startSearching();
                break;

            case REQUEST_IS_LOCATION_ENABLED:
                isLocationEnabled = Util.isLocationProviderEnabled(this);
                if (!isLocationEnabled) {
                    Util.showUIMessage(content, R.string.enable_requirements);
                }
                startSearching();
                break;

            case REQUEST_IS_NETWORK_ENABLED:
                isNetworkAvailable = Util.isInternetAvailable(this);
                if (!isNetworkAvailable) {
                    Util.showUIMessage(content, R.string.enable_requirements);
                }
                startSearching();
                break;

            default:
                Log.w(TAG, "unknown Action: " + requestCode + " " + resultCode + " " + data);
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_tabbed, menu);
        searchView = (SearchView) menu.findItem(R.id.action_seach_topic).getActionView();
        searchView.setIconifiedByDefault(false);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.action_bluetooth_settings:
                startActivity(new Intent(Settings.ACTION_BLUETOOTH_SETTINGS));
                return true;
            case R.id.action_settings:
                Util.showUIMessage(content, R.string.action_settings);
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    /* Permissions */

    private void startSearching() {
        checkNecessaryPermission();
        if (hasNecessaryPermissions) {
            checkBluetoothIsActive();
            checkNetworkIsAvailable();
            checkLocationIsAvailable();
            startSearchingForBeacons();
        }
    }

    private void checkNecessaryPermission() {
        Log.d(TAG, "checkNecessaryPermission");
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.READ_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED &&
            ContextCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED &&
            ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) == PackageManager.PERMISSION_GRANTED){
            hasNecessaryPermissions = true;
        } else {
            Dexter.checkPermissions(new MultiplePermissionsListener() {
                @Override
                public void onPermissionsChecked(MultiplePermissionsReport report) {
                    Log.d(TAG, "onPermissionsChecked");
                    if (!report.areAllPermissionsGranted()) {
                        hasNecessaryPermissions = false;
                        new AlertDialog.Builder(TabbedActivity.this)
                                .setCancelable(true)
                                .setIcon(R.mipmap.perm_group_bluetooth)
                                .setTitle(R.string.permissions)
                                .setMessage(R.string.permission_text)
                                .setNegativeButton(android.R.string.cancel, new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialog, int which) {
                                        dialog.dismiss();
                                        finish();
                                    }
                                })
                                .show();
                    } else {
                        hasNecessaryPermissions = true;
                        checkBluetoothIsActive();
                        checkNetworkIsAvailable();
                        checkLocationIsAvailable();
                    }
                }

                @Override
                public void onPermissionRationaleShouldBeShown(List<PermissionRequest> permissions, PermissionToken token) {
                    Log.d(TAG, "onPermissionRationaleShouldBeShown");
                }

            }, Manifest.permission.READ_EXTERNAL_STORAGE, Manifest.permission.WRITE_EXTERNAL_STORAGE, Manifest.permission.ACCESS_COARSE_LOCATION);
            hasNecessaryPermissions = false;
        }
    }

    private void checkBluetoothIsActive() {
        isBluetoothEnabled = Util.isBluetoothEnabled(this);
        Log.d(TAG, "checkBluetoothIsActive: " + isBluetoothEnabled);
        if (!isBluetoothEnabled){
            Util.showDialog(this, R.string.bluetooth_enable, R.string.bluetooth_enable_text, BluetoothAdapter.ACTION_REQUEST_ENABLE, REQUEST_IS_BLUETOOTH_ENABLED);
        } else {
            startSearchingForBeacons();
        }
    }

    private void checkNetworkIsAvailable() {
        isNetworkAvailable = Util.isInternetAvailable(this);
        Log.d(TAG, "checkNetworkIsAvailable: " + isNetworkAvailable);
        if (!isNetworkAvailable) {
            Util.showDialog(this, R.string.network_enabled, R.string.network_enabled_text, Settings.ACTION_SETTINGS, REQUEST_IS_NETWORK_ENABLED);
        } else {
            startSearchingForBeacons();
        }
    }

    private void checkLocationIsAvailable() {
        isLocationEnabled = Util.isLocationProviderEnabled(this);
        Log.d(TAG, "checkLocationIsAvailable: " + isLocationEnabled);
        if (!isLocationEnabled) {
            Util.showDialog(this, R.string.location_enable, R.string.location_enable_text, Settings.ACTION_LOCATION_SOURCE_SETTINGS, REQUEST_IS_LOCATION_ENABLED);
        } else {
            startSearchingForBeacons();
        }
    }

    private void startSearchingForBeacons() {
        Log.d(TAG, "startSearchingForBeacons: Searching: " + isSearching + " found: " + hasBeaconFound);
        if (isBluetoothEnabled && isLocationEnabled && isNetworkAvailable && !hasBeaconFound && !isSearching) {
            isSearching = true;
            Log.d(TAG, "startSearchingForBeacons: start");
            BTLEService.startService(this);
            showSearchingDialog();
        }
    }

    /**
     * {@link de.berlin.htw.oisindoor.userapp.fragments.NoteFragment.OnListFragmentInteractionListener#onListClicked(GeoCoordinate)}
     */
    @Override
    public void onListClicked(@NonNull GeoCoordinate item) {
        Util.showUIMessage(content, item.toString());
    }

    private void initSearchView(final List<Topic> topicList) {
        final ArrayList<Topic> filteredTopics = new ArrayList<>(topicList.size());
        suggestionAdapter = new SimpleCursorAdapter(
            this,
            R.layout.item_topic,
            null,
            new String[] {"title", "author", "target"},
            new int[]{R.id.item_topic_title, R.id.item_topic_author, R.id.item_topic_target},
            CursorAdapter.FLAG_REGISTER_CONTENT_OBSERVER);

        searchView.setSuggestionsAdapter(suggestionAdapter);
        searchView.setOnSuggestionListener(new SearchView.OnSuggestionListener() {
            @Override
            public boolean onSuggestionSelect(int position) {
                Log.d(TAG, "onSuggestionSelect: " + position);
                return true;
            }

            @Override
            public boolean onSuggestionClick(int position) {
                Topic t = filteredTopics.get(position);
                Log.d(TAG, "onSuggestionClick: " + position + " " + t.getTitle());
                startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse(t.getTargetURL())));
                return true;
            }
        });
        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                Log.d(TAG, "onQueryTextSubmit: " + query);
                return true;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                Log.d(TAG, "onQueryTextChange: " + newText);
                populateAdapter(newText);
                return true;
            }

            private void populateAdapter(String newText) {
                final MatrixCursor c = new MatrixCursor(new String[]{BaseColumns._ID, "title", "author", "target"});
                filteredTopics.clear();
                for (int i = 0; i < topicList.size(); i++) {
                    Topic t = topicList.get(i);
                    if (t.getTitle().toLowerCase().startsWith(newText.toLowerCase())) {
                        filteredTopics.add(t);
                        c.addRow(new Object[]{i, t.getTitle(), t.getAuthor(), t.getTargetURL()});
                    }
                }
                suggestionAdapter.changeCursor(c);
            }

        });
    }

    private void showSearchingDialog() {
        dialog = new ProgressDialog(this);
        dialog.setIcon(R.mipmap.perm_group_bluetooth);
        dialog.setTitle(R.string.bt_searchForBeacons);
        dialog.setCancelable(true);
        dialog.setCanceledOnTouchOutside(false);
        dialog.setButton(DialogInterface.BUTTON_NEGATIVE, getString(android.R.string.cancel), new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                cancelSearchingDialog();
            }
        });
        dialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
        dialog.setIndeterminate(true);
        dialog.show();
    }

    private void cancelSearchingDialog(){
        BTLEService.stopService(this);
        isSearching = false;
        dialog.dismiss();
    }

    /* Classes */

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
                    cancelSearchingDialog();
                    final Fragment f = sectionsPagerAdapter.getItem(0);
                    if (f instanceof IPositioning) {
                        final String url = intent.getStringExtra(BTLEService.RESPONSE_LOCATION_VALUE);
                        if (url != null) {
                            ((IPositioning) f).updatePosition(url);
//                            TODO: Later
//                            MagicSharkTask task = new MagicSharkTask(url, new GenericCallback<ArrayList<Topic>>() {
//                                @Override
//                                public void onResult(ArrayList<Topic> data) {
//                                    ((IPositioning) f).updateTopics(data);
//                                    initSearchView(Topic.ITEMS);
//                                    hasBeaconFound = true;
//                                }
//                            });
//                            task.execute();
                            ((IPositioning) f).updateTopics(Topic.ITEMS);
                            initSearchView(Topic.ITEMS);
                            hasBeaconFound = true;
                        }
                    }
                    break;

                case BTLEService.RESPONSE_ERROR:
                    cancelSearchingDialog();
                    Util.showUIMessage(content, intent.getStringExtra(BTLEService.RESPONSE_ERROR_VALUE));
                    break;

                default:
                    Log.w(TAG, "unknown action: " + intent.getAction());
                    break;
            }
        }

    }

}
