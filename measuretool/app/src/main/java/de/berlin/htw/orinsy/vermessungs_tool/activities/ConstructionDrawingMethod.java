package de.berlin.htw.orinsy.vermessungs_tool.activities;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.os.Environment;
import android.util.Log;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import de.berlin.htw.orinsy.vermessungs_tool.R;
import de.berlin.htw.orinsy.vermessungs_tool.utils.CalculateGeoDataConstructionDrawing;
import de.berlin.htw.orinsy.vermessungs_tool.utils.GeoData;
import de.berlin.htw.orinsy.vermessungs_tool.utils.GeoDataLoad;
import de.berlin.htw.orinsy.vermessungs_tool.utils.GeoDataSave;

public class ConstructionDrawingMethod extends Activity {

    private ArrayList<String> results, getResults, getSetupCoordinates;
    private Intent intent;
    private double startLatitude = 0, startLongitude = 0, referenceLatitude = 0, referenceLongitude = 0,
            newLatitude = 0, newLongitude = 0, yAxis = 0, xAxis = 0, heightValue = 0;
    private TextView textView1, textView2, textView4, textView5, textView6, tvLatitudeStart, tvLongitudeStart, tvLatitudeReference, tvLongitudeReference;
    private EditText inputYAxis, inputXAxis;
    private String info, height;
    private CalculateGeoDataConstructionDrawing geoData;
    private InputMethodManager inputManager;
    private static int SUBACTIVITY_REQUESTCODE = 10;
    private List<GeoData> newGeoDataList;
    private GeoData geoDataXml;
    private File newDataFile;
    private final String MYLOG = "My Log";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_construction_drawing_method);

        this.inputYAxis = (EditText) findViewById(R.id.y_coordinate);
        this.inputXAxis = (EditText) findViewById(R.id.x_coordinate);
        this.textView1 = (TextView) findViewById(R.id.tv_result_new_latitude);
        this.textView2 = (TextView) findViewById(R.id.tv_result_new_longitude);
        this.textView4 = (TextView) findViewById(R.id.textView4);
        this.textView5 = (TextView) findViewById(R.id.textView5);
        this.textView6 = (TextView) findViewById(R.id.textView6);
        this.tvLatitudeStart = (TextView) findViewById(R.id.tv_latitude_start);
        this.tvLongitudeStart = (TextView) findViewById(R.id.tv_longitude_start);
        this.tvLatitudeReference = (TextView) findViewById(R.id.tv_latitude_reference);
        this.tvLongitudeReference = (TextView) findViewById(R.id.tv_longitude_reference);
        this.getResults = new ArrayList<>();
        this.getSetupCoordinates = new ArrayList<>();
        this.newGeoDataList = new ArrayList<>();
        this.newDataFile = new File(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_NOTIFICATIONS) , "newGeoData.mgs");

        if (this.newDataFile.exists()) {
            this.newDataFile.delete();
        }

    }

    public void onClick(View view) {

        try {

            switch (view.getId()) {

                case R.id.btn_calculate_new_geodata:


                    /**

                     this.startLatitude = Double.parseDouble(this.tvLatitudeStart.getText().toString());
                     this.startLongitude= Double.parseDouble(this.tvLongitudeStart.getText().toString());
                     this.referenceLatitude = Double.parseDouble(this.tvLatitudeReference.getText().toString());
                     this.referenceLongitude = Double.parseDouble(this.tvLongitudeReference.getText().toString());

                     */


                    this.yAxis = Double.parseDouble(this.inputYAxis.getText().toString());
                    this.xAxis = Double.parseDouble(this.inputXAxis.getText().toString());

                   // Log.d(MYLOG, "Y:   " + yAxis + "x: " + xAxis);

                    this.geoData = new CalculateGeoDataConstructionDrawing(this.startLatitude, this.startLongitude, this.referenceLatitude, this.referenceLongitude, this.yAxis, this.xAxis);

                    this.geoData.calculateDistance();
                    this.geoData.calculateRotationAngle();
                    this.newLatitude = this.geoData.calculateNewLatitude();
                    this.newLongitude = this.geoData.calculateNewLongitude();

                    this.newLatitude = Math.round(100000000.0 * this.newLatitude) / 100000000.0;
                    this.newLongitude = Math.round(100000000.0 * this.newLongitude) / 100000000.0;



                    this.inputManager = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
                    this.inputManager.hideSoftInputFromWindow((null == getCurrentFocus()) ? null : getCurrentFocus().getWindowToken(), InputMethodManager.HIDE_NOT_ALWAYS);



                    AlertBuilder alertBuilder = new AlertBuilder(this);
                    /*
                    try {
                        geoDataXml = new GeoData(info, heightValue, newLatitude, newLongitude);
                        Log.d("GeoData", String.valueOf(geoDataXml.getHeight()));
                        if (newDataFile.exists()){
                            newGeoDataList = GeoDataLoad.loadGeoData(newDataFile);
                        }
                        newGeoDataList.add(geoDataXml);

                        for (int i = 0; i < newGeoDataList.size(); i++){
                            Log.d("Debug ListNEWCR", newGeoDataList.get(i).getInfo());
                        }
                        GeoDataSave.saveGeoData(newGeoDataList, newDataFile);

                    } catch (Exception ex){
                        Log.e("Data FileSave", "speichern der newGeoData.mgd fehlgeschlagen");
                    }
                    */
                    break;

                case R.id.btn_apply_new_geodata:

                    this.intent = new Intent(this, GeoDataList.class).putStringArrayListExtra("newGeoDatas", this.getResults).putStringArrayListExtra("setup_coordinates", this.getSetupCoordinates);
                    startActivityForResult(this.intent, SUBACTIVITY_REQUESTCODE);
                    break;

                case R.id.btn_setup_coordinates:

                    this.intent = new Intent(this, SetupCoordinates.class);
                    startActivityForResult(this.intent, SUBACTIVITY_REQUESTCODE);
                    break;

                default:
                    break;
            }
        } catch (Exception e) {
            e.printStackTrace();
            Toast.makeText(this, "fill out all fields", Toast.LENGTH_SHORT).show();
        }
    }

    public class AlertBuilder {

        public AlertBuilder(Context context) {

            AlertDialog.Builder alert = new AlertDialog.Builder(context);
            //  alert.setTitle("Alert Dialog With EditText"); //Set Alert dialog title here
            alert.setMessage("Enter Info for Geo Data"); //Message here

            // Set an EditText view to get user input
            final EditText input = new EditText(context);
            input.setInputType(0x00000001);
            alert.setView(input);

            alert.setPositiveButton("OK", new DialogInterface.OnClickListener() {
                public void onClick(DialogInterface dialog, int whichButton) {
                    //You will get as string input data in this variable.
                    // here we convert the input to a string and show in a toast.
                    ConstructionDrawingMethod.this.info = input.getEditableText().toString();
                    textView1.setText(String.valueOf(newLatitude));
                    textView2.setText(String.valueOf(newLongitude));
                    textView4.setVisibility(View.VISIBLE);

                    try {

                        geoDataXml = new GeoData(info, heightValue, newLatitude, newLongitude);
                        if (newDataFile.exists()) {
                            newGeoDataList = GeoDataLoad.loadGeoData(newDataFile);
                        }
                        newGeoDataList.add(geoDataXml);

                        for (int i = 0; i < newGeoDataList.size(); i++) {
                            Log.d("Debug ListNEWCR", newGeoDataList.get(i).getInfo());
                        }
                        GeoDataSave.saveGeoData(newGeoDataList, newDataFile);

                    } catch (Exception ex) {
                        Log.e("Data FileSave", "speichern der newGeoData.mgs fehlgeschlagen");
                    }


                    getResults.add("Info: " + info + "  Height: " + height + "\nLa: " + newLatitude + "\nLo: " + newLongitude);

                } // End of onClick(DialogInterface dialog, int whichButton)
            }); //End of alert.setPositiveButton
            alert.setNegativeButton("CANCEL", new DialogInterface.OnClickListener() {
                public void onClick(DialogInterface dialog, int whichButton) {
                    // Canceled.
                    dialog.cancel();
                }
            }); //End of alert.setNegativeButton
            AlertDialog alertDialog = alert.create();
            alertDialog.show();
        }
    }

    protected void onActivityResult(int requestCode, int resultCode, Intent data) {

        try {

            if (requestCode == SUBACTIVITY_REQUESTCODE && resultCode == Activity.RESULT_OK) {

                if (data.hasExtra("actual_list")) {

                    this.getResults = data.getStringArrayListExtra("actual_list");
                }

                if (data.hasExtra("setup_coordinates")) {

                    this.getSetupCoordinates = data.getStringArrayListExtra("setup_coordinates");
                    setSetupCoordinates();
                }

                if (data.hasExtra("input_datas")) {

                    this.getSetupCoordinates = data.getStringArrayListExtra("input_datas");
                    setSetupCoordinates();
                }
            }
        } catch (Exception e) {

            e.printStackTrace();
        }
    }

    public void setSetupCoordinates() {

        this.startLatitude = Double.parseDouble(this.getSetupCoordinates.get(0));
       // this.setupCoordinates.add(0, getSetupCoordinates.get(0));
        // Log.d(MYLOG, "" + this.startLatitude);
        // Log.d(MYLOG, this.setupCoordinates.get(0));
        this.startLongitude = Double.parseDouble(getSetupCoordinates.get(1));
       // this.setupCoordinates.add(1, getSetupCoordinates.get(1));
        this.referenceLatitude = Double.parseDouble(getSetupCoordinates.get(2));
        //this.setupCoordinates.add(2, getSetupCoordinates.get(2));
        this.referenceLongitude = Double.parseDouble(getSetupCoordinates.get(3));
        //this.setupCoordinates.add(3, getSetupCoordinates.get(3));
        this.height = getSetupCoordinates.get(4);
        //this.setupCoordinates.add(4, getSetupCoordinates.get(4));
        this.heightValue = Double.parseDouble(getSetupCoordinates.get(5));
        this.tvLatitudeStart.setText(String.valueOf(startLatitude));
        this.textView5.setVisibility(View.VISIBLE);
        this.tvLongitudeStart.setText(String.valueOf(startLongitude));
        this.tvLatitudeReference.setText(String.valueOf(referenceLatitude));
        this.textView6.setVisibility(View.VISIBLE);
        this.tvLongitudeReference.setText(String.valueOf(referenceLongitude));
        Log.d(MYLOG, "startLatitude: " + startLatitude + " " + "");
    }

}
