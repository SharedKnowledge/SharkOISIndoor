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

/**
 * @brief construction drawing method provides possibility to calculate new latitude and longitude with help
 *          of consruction drawing.
 *
 * @author Maik M
 */

public class ConstructionDrawingMethod extends Activity {

    private ArrayList<String> getResults, getSetupCoordinates;
    private Intent intent;
    private double startLatitude = 0, startLongitude = 0, referenceLatitude = 0, referenceLongitude = 0,
            newLatitude = 0, newLongitude = 0, yAxis = 0, xAxis = 0, height = 0;
    private int floor = 0;
    private TextView textView1, textView2, textView4, textView5, textView6, tvLatitudeStart, tvLongitudeStart, tvLatitudeReference, tvLongitudeReference, tvHeight, tvFloor;
    private EditText inputYAxis, inputXAxis;
    private String info;
    private CalculateGeoDataConstructionDrawing geoData;
    private InputMethodManager inputManager;
    private static int SUBACTIVITY_REQUESTCODE = 10;
    private List<GeoData> newGeoDataList;
    private GeoData geoDataXml;
    private File newDataFile;

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
        this.tvHeight = (TextView) findViewById(R.id.tv_height_1);
        this.tvFloor = (TextView) findViewById(R.id.tv_floor_1);
        this.getResults = new ArrayList<>();
        this.getSetupCoordinates = new ArrayList<>();
        this.newGeoDataList = new ArrayList<>();
        this.newDataFile = new File(Environment.getExternalStorageDirectory() , "/GeoDatas/newGeoData.mgs");

        if (this.newDataFile.exists()) {
            this.newDataFile.delete();
        }

    }

    public void onClick(View view) {

        try {

            switch (view.getId()) {

                case R.id.btn_calculate_new_geodata:

                    this.yAxis = Double.parseDouble(this.inputYAxis.getText().toString());
                    this.xAxis = Double.parseDouble(this.inputXAxis.getText().toString());

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

    /**
     * @brief inner class creates and shows dialogue to add an information, shown in list entry
     */

    public class AlertBuilder {

        /**
         * @brief constructor
         *
         * @param context
         */

        public AlertBuilder(Context context) {

            AlertDialog.Builder alert = new AlertDialog.Builder(context);

            alert.setMessage("Enter Info for Geo Data");

            final EditText input = new EditText(context);
            input.setInputType(0x00000001);
            alert.setView(input);

            alert.setPositiveButton("OK", new DialogInterface.OnClickListener() {
                public void onClick(DialogInterface dialog, int whichButton) {

                    ConstructionDrawingMethod.this.info = input.getEditableText().toString();
                    textView1.setText(String.valueOf(newLatitude));
                    textView2.setText(String.valueOf(newLongitude));
                    textView4.setVisibility(View.VISIBLE);

                    try {

                        geoDataXml = new GeoData(info, height, newLatitude, newLongitude, floor);
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

                    getResults.add("Info: " + info + " Height: " + height + " Floor: " + floor + "\nLa: " + newLatitude + "\nLo: " + newLongitude);

                }
            });
            alert.setNegativeButton("CANCEL", new DialogInterface.OnClickListener() {
                public void onClick(DialogInterface dialog, int whichButton) {

                    dialog.cancel();
                }
            });
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
        this.startLongitude = Double.parseDouble(getSetupCoordinates.get(1));
        this.referenceLatitude = Double.parseDouble(getSetupCoordinates.get(2));
        this.referenceLongitude = Double.parseDouble(getSetupCoordinates.get(3));
        this.height = Double.parseDouble(getSetupCoordinates.get(4));
        this.floor = Integer.parseInt(getSetupCoordinates.get(5));
        this.tvLatitudeStart.setText(String.valueOf(startLatitude));
        this.textView5.setVisibility(View.VISIBLE);
        this.tvLongitudeStart.setText(String.valueOf(startLongitude));
        this.tvLatitudeReference.setText(String.valueOf(referenceLatitude));
        this.textView6.setVisibility(View.VISIBLE);
        this.tvLongitudeReference.setText(String.valueOf(referenceLongitude));
        this.tvHeight.setText(String.valueOf(this.height));
        this.tvFloor.setText(String.valueOf(this.floor));
    }

}