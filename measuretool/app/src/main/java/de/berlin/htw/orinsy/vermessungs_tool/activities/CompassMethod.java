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

import de.berlin.htw.orinsy.vermessungs_tool.utils.GeoData;
import de.berlin.htw.orinsy.vermessungs_tool.R;
import de.berlin.htw.orinsy.vermessungs_tool.utils.CalculateGeoDataCompass;
import de.berlin.htw.orinsy.vermessungs_tool.utils.GeoDataLoad;
import de.berlin.htw.orinsy.vermessungs_tool.utils.GeoDataSave;

/**
 * @brief compass method activity provides possibility to calculate new latitude and longitude from measured datas
 *
 * @author Maik Müller
 */

public class CompassMethod extends Activity {

    private ArrayList<String> results;
    private Intent intent;
    private int floor = 0;
    private EditText enterLongitude, enterLatitude, enterDistance, enterAngle, enterHeight, enterFloor;
    private double longitude = 0, latitude = 0, distance = 0, angle = 0, height = 0, newLatitude = 0, newLongitude = 0;
    private TextView textView1, textView2, textView4;
    private final Context context = this;
    private String info, checkedItem;
    private String[] splitedSubstring1, splitedSubstring2, splitedSubstring3, splitedSubstring4;
    private CalculateGeoDataCompass geoData;
    private InputMethodManager inputManager;
    private static int SUBACTIVITY_REQUESTCODE = 10;
    private List<GeoData> newGeoDataList;
    private GeoData geoDataXml;
    private File newDataFile;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_compass_method);

        enterLatitude = (EditText) findViewById(R.id.latitude);
        enterLongitude = (EditText) findViewById(R.id.longitude);
        enterDistance = (EditText) findViewById(R.id.edit_distance);
        enterFloor = (EditText) findViewById(R.id.edit_floor);
        enterAngle = (EditText) findViewById(R.id.edit_angle);
        enterHeight = (EditText) findViewById(R.id.edit_height);
        textView1 = (TextView) findViewById(R.id.tv_result_new_latitude);
        textView2 = (TextView) findViewById(R.id.tv_result_new_longitude);
        textView4 = (TextView) findViewById(R.id.textView4);
        results = new ArrayList<>();
        newGeoDataList = new ArrayList<>();
        newDataFile = new File(Environment.getExternalStorageDirectory() , "/GeoDatas/newGeoData.mgs");

        if (newDataFile.exists()){
            newDataFile.delete();
        }

    }

    public void onClick(View view) {

        try {

            switch (view.getId()) {

                case R.id.btn_calculate_new_geodata:

                    longitude = Double.parseDouble(enterLongitude.getText().toString());
                    latitude = Double.parseDouble(enterLatitude.getText().toString());
                    distance = Double.parseDouble(enterDistance.getText().toString());
                    angle = Double.parseDouble(enterAngle.getText().toString());
                    height = Double.parseDouble(enterHeight.getText().toString());
                    floor = Integer.parseInt(enterFloor.getText().toString());

                    geoData = new CalculateGeoDataCompass(latitude, longitude, distance, angle);

                    newLatitude = geoData.newLatitude();
                    newLongitude = geoData.newLongitude();

                    newLatitude = Math.round(100000000.0 * newLatitude) / 100000000.0;
                    newLongitude = Math.round(100000000.0 * newLongitude) / 100000000.0;

                    inputManager = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
                    inputManager.hideSoftInputFromWindow((null == getCurrentFocus()) ? null : getCurrentFocus().getWindowToken(), InputMethodManager.HIDE_NOT_ALWAYS);

                    // Alertbuilder for information input

                    AlertDialog.Builder alert = new AlertDialog.Builder(context);
                    alert.setMessage("Enter Info for Geo Data");

                    final EditText input = new EditText(context);
                    input.setInputType(0x00000001);
                    alert.setView(input);

                    alert.setPositiveButton("OK", new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int whichButton) {

                            CompassMethod.this.info = input.getEditableText().toString();
                            textView1.setText(String.valueOf(newLatitude));
                            textView2.setText(String.valueOf(newLongitude));
                            textView4.setVisibility(View.VISIBLE);

                            try {

                                geoDataXml = new GeoData(info, height, newLatitude, newLongitude, floor);
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

                            results.add("Info: " + info + " Height: " + height + " Floor: " + floor  + "\nLa: " + newLatitude + "\nLo: " + newLongitude);

                        } // End of onClick(DialogInterface dialog, int whichButton)
                    });
                    alert.setNegativeButton("CANCEL", new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int whichButton) {

                            dialog.cancel();
                        }
                    });
                    AlertDialog alertDialog = alert.create();
                    alertDialog.show();
                    break;

                case R.id.btn_apply_new_geodata:

                    // intent filled with calculated geo datas

                    intent = new Intent(this, GeoDataList.class).putStringArrayListExtra("newGeoDatas", results);
                    startActivityForResult(intent, SUBACTIVITY_REQUESTCODE);

                default:
                    break;
            }
        } catch (Exception e) {
            e.printStackTrace();
            Toast.makeText(this, "fill out all fields", Toast.LENGTH_SHORT).show();
        }
    }

    protected void onActivityResult(int requestCode, int resultCode, Intent data) {

        try {

            if (requestCode == SUBACTIVITY_REQUESTCODE && resultCode == Activity.RESULT_OK) {

                this.results = data.getStringArrayListExtra("actual_list");

                if (data.hasExtra("checked_item")) {

                    checkedItem = data.getStringExtra("checked_item");
                    splitedSubstring1 = checkedItem.split("\nLo: ");
                    splitedSubstring2 = splitedSubstring1[0].split("\nLa: ");
                    splitedSubstring3 = splitedSubstring2[0].split("Floor: ");
                    splitedSubstring4 = splitedSubstring3[0].split("Height: ");
                    enterLatitude.setText(splitedSubstring2[1]);
                    enterLongitude.setText(splitedSubstring1[1]);
                    enterHeight.setText(splitedSubstring4[1]);
                    enterFloor.setText(splitedSubstring3[1]);
                    enterDistance.setText("");
                    enterAngle.setText("");
                    textView1.setText("");
                    textView2.setText("");
                    textView4.setVisibility(View.INVISIBLE);
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

}
