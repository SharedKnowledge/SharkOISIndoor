package de.berlin.htw.oisindoor.userapp;

import android.Manifest;
import android.annotation.TargetApi;
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
import android.os.Build;
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
import android.widget.Toast;

import java.util.ArrayList;
import java.util.List;

import butterknife.Bind;
import butterknife.ButterKnife;
import de.berlin.htw.oisindoor.userapp.fragments.AdminFragment;
import de.berlin.htw.oisindoor.userapp.fragments.PositioningFragment;
import de.berlin.htw.oisindoor.userapp.model.Topic;
import de.berlin.htw.oisindoor.userapp.positioning.BTLEService;
import de.berlin.htw.oisindoor.userapp.shark.GenericCallback;
import de.berlin.htw.oisindoor.userapp.shark.SharkDownloader;
import de.berlin.htw.oisindoor.userapp.util.Util;

/**
 * Main Class for the OIS Indoor app
 * <p> The main content is a {@link android.support.design.widget.TabLayout.TabView}, which contains a {@link PositioningFragment}
 * and an {@link AdminFragment} </p>
 * <p>When the app becomes visible (#onStart) the app tries to start a scanning process, when minimized or finished the app stops the search</p>
 * Via a {@link FloatingActionButton}, the user can restart the bluetooth signals searching
 *
 * @see BTLEService
 * @see PositioningFragment
 * @see AdminFragment
 * @see de.berlin.htw.oisindoor.userapp.TabbedActivity.BTLEReceiver
 * @author Max M
 */
public class TabbedActivity extends AppCompatActivity implements AdminFragment.OnFragmentInteractionListener {
    /**
     * LOG Indicator
     */
    private static final String TAG = TabbedActivity.class.getSimpleName();
    /**
     * request code for #startActivityForResult, when asking for the {@link android.Manifest.permission#ACCESS_COARSE_LOCATION}
     */
    private static final int REQUEST_ACCESS_COARSE_LOCATION = 561;
    /**
     * request code for #startActivityForResult, when asking to enable Bluetooth
     */
    private static final int REQUEST_IS_BLUETOOTH_ENABLED = 788;
    /**
     * request code for #startActivityForResult, when asking to enable a location provider
     */
    private static final int REQUEST_IS_LOCATION_ENABLED = 789;
    /**
     * request code for #startActivityForResult, when asking to enable a network
     */
    private static final int REQUEST_IS_NETWORK_ENABLED = 780;

    /**
     * member for a dialog, which is only shown on the first searching {@see #showSearchingDialog}
     */
    private ProgressDialog dialog;
    /**
     * indicates, if it's the first search, so we show the dialog
     */
    private boolean hasBeaconFound = false;
    /**
     * reference to a {@link de.berlin.htw.oisindoor.userapp.TabbedActivity.BTLEReceiver}
     */
    private BTLEReceiver btleReceiver;
    private IntentFilter filter;
    private LocalBroadcastManager localBroadcastManager;
    private SectionsPagerAdapter sectionsPagerAdapter;
    private SimpleCursorAdapter suggestionAdapter;
    private SearchView searchView;
    @Bind(R.id.ac_tabbed_vp) ViewPager viewPager;
    @Bind(R.id.ac_tabbed_main_content) View content;

    /**
     * Indicates if the app has all necessary permissions
     */
    private boolean hasNecessaryPermissions;
    /**
     * Indicates if a location provider is enabled
     */
    private boolean isLocationEnabled;
    /**
     * Indicates if bluetooth is enabled
     */
    private boolean isBluetoothEnabled;
    /**
     * Indicates if an internet network is available
     */
    private boolean isNetworkAvailable;

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
        hasNecessaryPermissions = false;
        localBroadcastManager.registerReceiver(btleReceiver, filter);
        startSearching();
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
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        switch (requestCode) {
            case REQUEST_ACCESS_COARSE_LOCATION: {
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED){
                    Log.d(TAG, "Permission granted");
                } else {
                    Log.w(TAG, "Permission not granted");
                    Toast.makeText(this, R.string.no_permissions_granted, Toast.LENGTH_SHORT).show();
                    finish();
                }
            }
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_ac_tabbed, menu);
        searchView = (SearchView) menu.findItem(R.id.action_search_topic).getActionView();
        searchView.setIconifiedByDefault(false);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.action_bluetooth_settings:
                startActivity(new Intent(Settings.ACTION_BLUETOOTH_SETTINGS));
                return true;
            case R.id.action_location_settings:
                startActivityForResult(new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS), REQUEST_IS_LOCATION_ENABLED);
                return true;
            case R.id.action_settings:
                Util.showUIMessage(content, R.string.action_settings);
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    /* Permissions */

    /**
     * tries to start a searching process</br>
     * the method checks if all following requirements are met:
     * <ul>
     *     <li>Network is available (Communication to the Shark-DB)</li>
     *     <li>Bluetooth ist enabled (and from Android Lollipop, the Bluetooth Permissions are granted)</li>
     *     <li>A location provider (e.g. GPS) is enabled</li>
     * </ul>
     *
     */
    private void startSearching() {
        checkNecessaryPermission();
        if (hasNecessaryPermissions) {
            checkBluetoothIsActive();
            checkNetworkIsAvailable();
            checkLocationIsAvailable();
            startSearchingForBeacons();
        }
    }

    /**
     * stop a searching process</br>
     * <ul>
     *     <li>Stops the {@link BTLEService search}</li>
     *     <li>hides the dialog, if it's shown</li>
     * </ul>
     */
    private void stopSearching(boolean keepSearching) {
        if (!keepSearching) {
            BTLEService.stopService(this);
        }
        ((AdminFragment) sectionsPagerAdapter.getItem(1)).onStopSearching();
        if (dialog != null) {
            dialog.dismiss();
        }
    }

    /**
     * checks if the {@link android.Manifest.permission#ACCESS_COARSE_LOCATION} is granted
     * if not, the App request the permission
     */
    @TargetApi(Build.VERSION_CODES.M)
    private void checkNecessaryPermission() {
        Log.d(TAG, "checkNecessaryPermission");
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
            hasNecessaryPermissions = true;
        } else {
            hasNecessaryPermissions = false;
            requestPermissions(new String[]{Manifest.permission.ACCESS_COARSE_LOCATION}, REQUEST_ACCESS_COARSE_LOCATION);
        }
    }

    /**
     * checks if Bluetooth is enabled
     * if not, a dialog is shown, which informs the user and provides a <i>Fix</i> button,
     * which allows the user to fix the problem
     */
    private void checkBluetoothIsActive() {
        isBluetoothEnabled = Util.isBluetoothEnabled(this);
        Log.d(TAG, "checkBluetoothIsActive: " + isBluetoothEnabled);
        if (!isBluetoothEnabled){
            Util.showDialog(this, R.string.bluetooth_enable, R.string.bluetooth_enable_text, R.mipmap.perm_group_bluetooth, new Util.DialogCallback() {
                @Override
                public void onPositiveButtonPressed() {
                    TabbedActivity.this.startActivityForResult(new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE), REQUEST_IS_BLUETOOTH_ENABLED);
                }
                @Override
                public void onNegativeButtonPressed() {
                    Util.showUIMessage(content, R.string.enable_requirements);
                }
            });
        } else {
            startSearchingForBeacons();
        }
    }

    /**
     * checks if an internet network is available
     * if not, a dialog is shown, which informs the user and provides a <i>Fix</i> button,
     * which sends the user to the android network settings
     */
    private void checkNetworkIsAvailable() {
        isNetworkAvailable = Util.isInternetAvailable(this);
        Log.d(TAG, "checkNetworkIsAvailable: " + isNetworkAvailable);
        if (!isNetworkAvailable) {
            Util.showDialog(this, R.string.network_enabled, R.string.network_enabled_text, R.mipmap.ic_lock_airplane_mode_alpha, new Util.DialogCallback() {
                @Override
                public void onPositiveButtonPressed() {
                    TabbedActivity.this.startActivityForResult(new Intent(Settings.ACTION_SETTINGS), REQUEST_IS_NETWORK_ENABLED);
                }
                @Override
                public void onNegativeButtonPressed() {
                    Util.showUIMessage(content, R.string.enable_requirements);
                }
            });
        } else {
            startSearchingForBeacons();
        }
    }

    /**
     * checks if a location provider is enabled
     * if not, a dialog is shown, which informs the user and provides a <i>Fix</i> button,
     * which sends the user to the android location settings
     * <b>Only on Android M or above</b>
     */
    private void checkLocationIsAvailable() {
        isLocationEnabled = Util.isLocationProviderEnabled(this);
        Log.d(TAG, "checkLocationIsAvailable: " + isLocationEnabled);
        if (!isLocationEnabled) {
            Util.showDialog(this, R.string.location_enable, R.string.location_enable_text, android.R.drawable.ic_menu_mylocation, new Util.DialogCallback() {
                @Override
                public void onPositiveButtonPressed() {
                    TabbedActivity.this.startActivityForResult(new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS), REQUEST_IS_LOCATION_ENABLED);
                }
                @Override
                public void onNegativeButtonPressed() {
                    Util.showUIMessage(content, R.string.enable_requirements);
                }
            });
        } else {
            startSearchingForBeacons();
        }
    }

    /**
     * start a search process
     * <ul>
     *     <li>show a process dialog (once while the first searching</li>
     *     <li>start a search process {@link BTLEService search process} </li>
     * </ul></br>
     * The following requirements must be met:
     * <ul>
     *     <li>Network is available (Communication to the Shark-DB)</li>
     *     <li>Bluetooth ist enabled (and from Android Lollipop, the BT Permissions are granted)</li>
     *     <li>From Android Lollipop, a location provider(e.g. GPS) is enabled</li>
     * </ul>
     *
     * if a least one requirement isn't met, no searching process is started
     */
    private void startSearchingForBeacons() {
        Log.d(TAG, "startSearchingForBeacons: " + hasBeaconFound);
        if (isBluetoothEnabled && isLocationEnabled && isNetworkAvailable && !hasBeaconFound) {
            Log.d(TAG, "startSearchingForBeacons: start");
            BTLEService.startService(this);
            if (!hasBeaconFound) {
                showSearchingDialog();
            }
            ((AdminFragment) sectionsPagerAdapter.getItem(1)).onStartSearching();
        }
    }

    /**
     * Init the SearchView in the toolbar</br>
     * Currently it will filter the title of the topics
     * Clicks on a search result will open the url
     * @param topicList - list of topics received from the shark-db
     */
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

    /**
     * show an ProgressDialog, which is cancelable or stopped via {@link TabbedActivity#stopSearching(boolean)}
     */
    private void showSearchingDialog() {
        dialog = new ProgressDialog(this);
        dialog.setIcon(R.mipmap.perm_group_bluetooth);
        dialog.setTitle(R.string.bt_searchForBeacons);
        dialog.setCancelable(true);
        dialog.setCanceledOnTouchOutside(false);
        dialog.setButton(DialogInterface.BUTTON_NEGATIVE, getString(android.R.string.cancel), new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                stopSearching(true);
            }
        });
        dialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
        dialog.setIndeterminate(true);
        dialog.show();
        hasBeaconFound = true;
    }

    /**
     * {@link de.berlin.htw.oisindoor.userapp.fragments.AdminFragment.OnFragmentInteractionListener#onFragmentInteraction(Uri)}
     * @param uri - ???
     */
    @Override
    public void onFragmentInteraction(Uri uri) {
        Log.d(TAG, "onFragmentInteraction: " + uri);
    }

    /* Classes */

    /**
     * A FragmentPagerAdapter, which handles the Tabs<br/>
     * The used Fragment must be created once and reused in {@link #getItem(int)},
     * because the every time new created fragments will not handle the lifecycle correct
     */
    private class SectionsPagerAdapter extends FragmentPagerAdapter {
        private PositioningFragment p = PositioningFragment.newInstance();
        private AdminFragment a = AdminFragment.newInstance();

        public SectionsPagerAdapter(FragmentManager fm) {
            super(fm);
        }

        @Override
        public Fragment getItem(int position) {
            switch (position) {
                case 0:
                    return p;
                case 1:
                    return a;
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

    /**
     * local BroadcastReceiver, which receives signals from the {@link BTLEService}
     * the Broadcast contains the location or an error message
     */
    private class BTLEReceiver extends BroadcastReceiver {
        @Override
        public void onReceive(Context context, Intent intent) {
            switch (intent.getAction()){
                case BTLEService.RESPONSE_LOCATION:
                    final String url = intent.getStringExtra(BTLEService.RESPONSE_LOCATION_VALUE);
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            handleFoundBeacon(url);
                        }
                    });
                    break;

                case BTLEService.RESPONSE_ERROR:
                    final String errorMsg = intent.getStringExtra(BTLEService.RESPONSE_ERROR_VALUE);
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            stopSearching(false);
                            Util.showUIMessage(content, errorMsg);
                        }
                    });
                    break;

                default:
                    Log.w(TAG, "unknown action: " + intent.getAction());
                    break;
            }
        }

        /**
         * handles the found URL, which contains the location data
         * @param url - read location data
         */
        private void handleFoundBeacon(String url) {
            stopSearching(true);
            final PositioningFragment f0 = (PositioningFragment) sectionsPagerAdapter.getItem(0);
            final AdminFragment f1 = (AdminFragment) sectionsPagerAdapter.getItem(1);

            if (url != null) {
                f0.updatePosition(url);
                SharkDownloader task = new SharkDownloader(url, new GenericCallback<ArrayList<Topic>>() {
                    @Override
                    public void onResult(ArrayList<Topic> data) {
                        f0.updateTopics(data);
                        initSearchView(data);
                        hasBeaconFound = true;
                    }
                });
                task.execute();

                f1.updatePosition(url);
                f1.onStopSearching();
            }
        }

    }

}
