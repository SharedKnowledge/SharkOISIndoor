package de.berlin.htw.oisindoor.userapp.fragments;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
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
import de.berlin.htw.oisindoor.userapp.shark.SharkUploader;
import de.berlin.htw.oisindoor.userapp.util.Util;

public class AdminFragment extends Fragment implements IPositioning {
    private static final String TAG = AdminFragment.class.getSimpleName();

    public static AdminFragment newInstance() {
        return new AdminFragment();
    }

    private EditText altitude;
    private EditText author;
    private EditText content;
    private EditText latitude;
    private EditText longitude;
    private EditText topic;
    private ImageButton bleButton;
    private List<String> uploadData;
    private SharkUploader sharkUploader;
    private OnFragmentInteractionListener mListener;

    public AdminFragment() {
        // Required empty public constructor
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        try {
            mListener = (OnFragmentInteractionListener) getActivity();
        } catch (ClassCastException e) {
            throw new ClassCastException(getActivity().toString() + " must implement OnFragmentInteractionListener");
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, final ViewGroup container, Bundle savedInstanceState) {
        View v =  inflater.inflate(R.layout.f_admin, container, false);
        setHasOptionsMenu(true);
        v.setFocusable(false);
        latitude = ( EditText ) v.findViewById(R.id.latitude);
        longitude = ( EditText ) v.findViewById(R.id.longitude);
        altitude = ( EditText ) v.findViewById(R.id.altitude);
        topic = ( EditText ) v.findViewById(R.id.topic);
        topic.requestFocus();
        author = ( EditText ) v.findViewById(R.id.author);
        content = ( EditText ) v.findViewById(R.id.content);
        final Button uploadSharkButton = (Button) v.findViewById(R.id.middleButton);
        uploadData = new ArrayList<>();
        bleButton = ( ImageButton ) v.findViewById(R.id.bluetooth_BTN);

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

        latitude.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) { }
            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                latitude.setError(null);
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

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        super.onCreateOptionsMenu(menu, inflater);
        inflater.inflate(R.menu.menu_f_admin, menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()){
            case R.id.action_info:
                new AlertDialog.Builder(getActivity())
                        .setTitle(R.string.information)
                        .setMessage(R.string.information_content)
                        .setNeutralButton("Ok", new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int id) {
                                dialog.cancel();
                            }
                        })
                        .show();
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

    public void onStartSearching() {
        if (bleButton != null) {
            bleButton.setImageResource(R.mipmap.bluetooth_on);
        }
    }

    public void onStopSearching(){
        if (bleButton != null) {
            bleButton.setImageResource(R.mipmap.bluetooth_off);
        }
    }

    private void showSharkUploadInfo(){
        Log.d(TAG, "showSharkUploadInfo");
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
        if (TextUtils.isEmpty(latitude.getText().toString())) {
            latitude.setError(getResources().getString(R.string.geo_err));
            longitude.setError(getResources().getString(R.string.geo_err ));
            altitude.setError(getResources().getString(R.string.geo_err));
            isNotEmpty = false;
        }

        if (isNotEmpty) {
            String sLatitude = latitude.getText().toString();
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
        Log.d(TAG, "isNetworkConnected");
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

    /* IPositioning */

    @Override
    public void updatePosition(@NonNull String url) {
        Log.d(TAG, "updatePosition: " + url);
        List<String> geos = Util.readPropperGEO(url);
        longitude.setText(geos.get(0));
        latitude.setText(geos.get(1));
        altitude.setText(geos.get(2));
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
        void onFragmentInteraction(Uri uri);
    }

}
