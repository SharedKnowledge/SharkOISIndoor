package de.berlin.htw.oisindoor.userapp.fragments;

import android.Manifest;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.ConnectivityManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.support.v4.app.Fragment;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;

import java.util.ArrayList;
import java.util.List;

import de.berlin.htw.oisindoor.userapp.R;
import de.berlin.htw.oisindoor.userapp.positioning.BLESniffer;
import de.berlin.htw.oisindoor.userapp.shark.SharkUploader;

public class AdminFragment extends Fragment {
    private static final int REQUEST_ACCESS_COARSE_LOCATION = 684;
    private static final String TAG = AdminFragment.class.getSimpleName();

    public static AdminFragment newInstance() {
        return new AdminFragment();
    }

    private BLESniffer bleSniffer;
    private EditText altitude;
    private EditText author;
    private EditText content;
    private EditText latidude;
    private EditText longitude;
    private EditText topic;
    private Handler snifferHandler = new Handler(Looper.getMainLooper());
    private ImageButton bleButton;
    private boolean isListeing;
    private List<String> uploadData;
    private SharkUploader sharkUploader;

    private OnFragmentInteractionListener mListener;

    public AdminFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, final ViewGroup container, Bundle savedInstanceState) {
        View v =  inflater.inflate(R.layout.f_admin, container, false);
        setHasOptionsMenu(true);
        v.setFocusable(false);
        latidude = ( EditText ) v.findViewById(R.id.latitude);
        longitude = ( EditText ) v.findViewById(R.id.longitude);
        altitude = ( EditText ) v.findViewById(R.id.altitude);
        topic = ( EditText ) v.findViewById(R.id.topic);
        topic.requestFocus();
        author = ( EditText ) v.findViewById(R.id.author);
        content = ( EditText ) v.findViewById(R.id.content);
        final Button uploadSharkButton = (Button) v.findViewById(R.id.middleButton);
        isListeing = false;
        uploadData = new ArrayList<>();
        bleButton = ( ImageButton ) v.findViewById(R.id.bluetooth_BTN);
        bleSniffer = new BLESniffer(getActivity(), latidude, longitude, altitude, bleButton);

        // Avoid onclickListener not working because the view is first tapped and you need to double tap the button
        uploadSharkButton.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                if(hasFocus)
                    showSharkUploadInfo();
            }
        });

        // Avoid onFocusChangeListener no recognition after uploadSharkButton lost focus
        uploadSharkButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showSharkUploadInfo();
            }
        });


        bleButton.setImageResource(R.mipmap.bluetooth_off);
        if(getActivity().getPackageManager().hasSystemFeature(PackageManager.FEATURE_BLUETOOTH_LE)) {
            bleButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (!isListeing) {
                        //Checks the build version of the app, if lollipop or higher, use new permission model
                        if (Build.VERSION.SDK_INT > Build.VERSION_CODES.LOLLIPOP_MR1) {
                            // Check if the app has rights to ACCESS_COARSE_LOCATION
                            if (getActivity().checkSelfPermission(Manifest.permission.ACCESS_COARSE_LOCATION)
                                    != PackageManager.PERMISSION_GRANTED) {
                                requestPermissions(new String[]{Manifest.permission.ACCESS_COARSE_LOCATION},
                                        REQUEST_ACCESS_COARSE_LOCATION);
                            } else {
                                snifferHandler.post(bleSniffer);
                                isListeing = true;
                            }
                        } else {
                            snifferHandler.post(bleSniffer);
                            isListeing = true;
                        }
                    } else {
                        bleSniffer.stop();
                        isListeing = false;
                    }
                }
            });
        } else {
            latidude.setText(getResources().getString(R.string.ble_not_found_1));
            longitude.setText(getResources().getString(R.string.ble_not_found_2));
            altitude.setText(getResources().getString(R.string.ble_not_found_3));
        }

        latidude.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) { }
            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                latidude.setError(null);
            }
            @Override
            public void afterTextChanged(Editable s) {  }
        });
        longitude.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) { }
            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                longitude.setError(null);
            }
            @Override
            public void afterTextChanged(Editable s) { }
        });

        altitude.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) { }
            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                altitude.setError(null);
            }
            @Override
            public void afterTextChanged(Editable s) { }

        });
        return v;
    }

    private void showSharkUploadInfo(){
        boolean isNotEmpty = true;
        if (TextUtils.isEmpty(topic.getText().toString())) {
            topic.setError(getResources().getString(R.string.topic_err));
            isNotEmpty = false;
        }
        if (TextUtils.isEmpty(author.getText().toString())) {
            author.setError(getResources().getString(R.string.author_err));
            isNotEmpty = false;
        }
        if (TextUtils.isEmpty(content.getText().toString())) {
            content.setError(getResources().getString(R.string.content_err));
            isNotEmpty = false;
        }
        if (TextUtils.isEmpty(latidude.getText().toString())) {
            latidude.setError(getResources().getString(R.string.geo_err));
            longitude.setError(getResources().getString(R.string.geo_err ));
            altitude.setError(getResources().getString(R.string.geo_err));
            isNotEmpty = false;
        }

        if (isNotEmpty) {
            String sLatitude = latidude.getText().toString();
            String sLongitude = longitude.getText().toString();
            String sAltitude = altitude.getText().toString();
            uploadData.add(sLatitude + "," + sLongitude + "," + sAltitude);
            uploadData.add(topic.toString());
            uploadData.add(author.toString());
            uploadData.add(content.toString());
            AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
            builder.setTitle(R.string.uploud_information);
            builder.setMessage("" +
                    getResources().getString(R.string.latitude_TXT) + ": " + sLatitude + "\n" +
                    getResources().getString(R.string.longitude_TXT) + ": " + sLongitude + "\n" +
                    getResources().getString(R.string.altitude_TXT) + ": " + sAltitude + "\n" +
                    getResources().getString(R.string.topic_TXT) + ": " + topic.getText().toString() + "\n" +
                    getResources().getString(R.string.author_TXT) + ": " + author.getText().toString() + "\n" +
                    getResources().getString(R.string.content_TXT) + ": " + content.getText().toString());
            builder.setPositiveButton(R.string.upload, new DialogInterface.OnClickListener() {
                public void onClick(DialogInterface dialog, int id) {
                    if (isNetworkConnected()) {
                        bleSniffer.stop();
                        snifferHandler.removeCallbacks(bleSniffer);
                        sharkUploader = new SharkUploader(uploadData);
                        sharkUploader.execute();
                    }
                }
            });
            builder.setNegativeButton(R.string.abort, new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    dialog.cancel();
                }
            });
            builder.show();
        }
    }

    private boolean isNetworkConnected() {
        boolean isConnected = true;
        ConnectivityManager connectivityManager = (ConnectivityManager) getActivity().getSystemService(Context.CONNECTIVITY_SERVICE);
        if (connectivityManager.getActiveNetworkInfo() != null) {
            if (connectivityManager.getActiveNetworkInfo().isConnected())
                isConnected = true;
        }
        else {
            AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
            builder.setTitle(R.string.no_internet_con);
            builder.setMessage(R.string.no_internet_con_hint);
            builder.setPositiveButton(R.string.no_internet_con_open_pref, new DialogInterface.OnClickListener() {
                public void onClick(DialogInterface dialog, int id) {
                    getActivity().startActivityForResult(new Intent(android.provider.Settings.ACTION_SETTINGS), 0);
                }
            });
            builder.setNegativeButton(R.string.abort, new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    dialog.cancel();
                }
            });
            builder.show();
            isConnected = false;
        }
        return isConnected;
    }

    // TODO: Rename method, update argument and hook method into UI event
    public void onButtonPressed(Uri uri) {
        if (mListener != null) {
            mListener.onFragmentInteraction(uri);
        }
    }

    @SuppressWarnings("deprecation")
    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        try {
            mListener = (OnFragmentInteractionListener) activity;
        } catch (ClassCastException e) {
            throw new ClassCastException(activity.toString()
                    + " must implement OnFragmentInteractionListener");
        }
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        super.onCreateOptionsMenu(menu, inflater);
        inflater.inflate(R.menu.menu_f_admin, menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()){
            case R.id.action_info:
                AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
                builder.setTitle(R.string.information);
                builder.setMessage(R.string.information_content);
                builder.setNeutralButton("Ok", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        dialog.cancel();
                    }
                });
                builder.show();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mListener = null;
    }

    @Override
    public void onStop() {
        bleSniffer.stop();
        snifferHandler.removeCallbacks(bleSniffer);
        super.onStop();
    }

    @Override
    public void onStart() {
        if ( isListeing ) {
            if (Build.VERSION.SDK_INT > Build.VERSION_CODES.LOLLIPOP_MR1) {
                if (getActivity().checkSelfPermission(Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                    requestPermissions(new String[]{Manifest.permission.ACCESS_COARSE_LOCATION}, REQUEST_ACCESS_COARSE_LOCATION);
                } else {
                    Log.w(TAG, "Continue scan");
                    bleButton.setImageResource(R.mipmap.bluetooth_on);
                    snifferHandler.post(bleSniffer);
                }
            } else {
                Log.w(TAG, "Continue scan");
                bleButton.setImageResource(R.mipmap.bluetooth_on);
                snifferHandler.post(bleSniffer);
            }
        } else {
            Log.w(TAG, "No previous sans.");
                    bleButton.setImageResource(R.mipmap.bluetooth_off);
            // do not continue sniffing
        }
        super.onStart();
    }

    /**
     * This interface must be implemented by activities that contain this
     * fragment to allow an interaction in this fragment to be communicated
     * to the activity and potentially other fragments contained in that
     * activity.
     * <p/>
     * See the Android Training lesson <a href=
     * "http://developer.android.com/training/basics/fragments/communicating.html"
     * >Communicating with Other Fragments</a> for more information.
     */
    public interface OnFragmentInteractionListener {
        // TODO: Update argument type and name
        public void onFragmentInteraction(Uri uri);
    }

}
