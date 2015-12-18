package compenomatikus.httpsgithub.ois_admin;

import android.Manifest;
import android.app.Activity;
import android.bluetooth.BluetoothDevice;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import bluetoothmanager.BeaconManager;


public class AdminFragment extends Fragment {
    private static final int REQUEST_ENABLE_BT  = 1;
    private static final int REQUEST_ACCESS_COARSE_LOCATION = 0;
    private BeaconManager receiver = null;
    private int permissionCheck = 0;
    public final static String className = "AdminFragment";

    private OnFragmentInteractionListener mListener;

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment AdminFragment.
     */
    public static AdminFragment newInstance(String param1, String param2) {
        AdminFragment fragment = new AdminFragment();
        Bundle args = new Bundle();
        fragment.setArguments(args);
        return fragment;
    }

    public AdminFragment() {
        // Required empty public constructor
    }

 //--------------------------------------------------------------------------------------


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View v =  inflater.inflate(R.layout.fragment_admin_fragment, container, false);
        EditText latidude = ( EditText ) v.findViewById(R.id.latitude);
        EditText longitude = ( EditText ) v.findViewById(R.id.longitude);
        EditText altitude = ( EditText ) v.findViewById(R.id.altitude);
        EditText topic = ( EditText ) v.findViewById(R.id.topic);
        topic.requestFocus();
        EditText author = ( EditText ) v.findViewById(R.id.author);
        EditText content = ( EditText ) v.findViewById(R.id.content);
        Button button = ( Button ) v.findViewById(R.id.middleButton);
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(v.getContext(), "MAGIC", Toast.LENGTH_SHORT).show();
            }
        });


        List<String> geos = readPropperGEO("https://24.197611,120.780512,4.197631");
        latidude.setText(geos.get(0));
        longitude.setText(geos.get(1));
        altitude.setText(geos.get(2));

        // Check if the app has rights to ACCESS_COARSE_LOCATION
        permissionCheck = ContextCompat.checkSelfPermission(getActivity(), Manifest.permission.ACCESS_COARSE_LOCATION);
        Log.i(className, permissionCheck + "");

        if ( permissionCheck != PackageManager.PERMISSION_GRANTED ) {
            String[] permissions = { Manifest.permission.ACCESS_CHECKIN_PROPERTIES };
            ActivityCompat.requestPermissions(getActivity(), permissions, REQUEST_ACCESS_COARSE_LOCATION);
        }
        return v;
    }


    // TODO: Rename method, update argument and hook method into UI event
    public void onButtonPressed(Uri uri) {
        if (mListener != null) {
            mListener.onFragmentInteraction(uri);
        }
    }

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
    public void onDestroyView() {

        getContext().unregisterReceiver(receiver);
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mListener = null;
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


    /* ####################### Functions ######################## */

    private List<String> readPropperGEO(URL geoAsURL) {
        String url = geoAsURL.toString();
        Pattern pattern = Pattern.compile("\\d{1,3}\\.\\d*");
        Matcher matcher = pattern.matcher(url);
        return null;
    }

    private List<String> readPropperGEO(String geoAsString) {
        String url = geoAsString.toString();
        int matched = 0;
        List<String> results = new ArrayList<String>();
        Pattern pattern = Pattern.compile("\\d{1,3}\\.\\d*");
        Matcher matcher = pattern.matcher(url);
        Log.i("readPropperGEO(Str):", "START");
        while ( matcher.find() ) {
            Log.i("( " + matched++ + " ) Matched: ", matcher.group());
            results.add(matcher.group());
        }
        Log.i("readPropperGeo(Str):", "END");
        return results;
    }


}
