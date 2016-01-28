package de.berlin.htw.orinsy.vermessungs_tool.activities;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.os.Environment;
import android.util.Log;
import android.util.SparseBooleanArray;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.Toast;

import org.simpleframework.xml.core.Persister;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import de.berlin.htw.orinsy.vermessungs_tool.R;
import de.berlin.htw.orinsy.vermessungs_tool.utils.GeoData;
import de.berlin.htw.orinsy.vermessungs_tool.utils.GeoDataLoad;
import de.berlin.htw.orinsy.vermessungs_tool.utils.GeoDataSave;
import de.berlin.htw.orinsy.vermessungs_tool.utils.Measurement;

/**
 * @brief activity lists calculated geo datas with help of scroll view
 *
 * @author Maik M, ziera
 */

public class GeoDataList extends Activity {

    private ArrayAdapter<String> adapter;
    private Bundle subActivityExtras;
    private ArrayList<String> setupCoordinates, getResults, getSetupCoordinates;
    private String checkedItem = "null";
    private ListView lvMain;
    private Intent intent;
    private List<GeoData> allGeoData, newGeoDataList;
    private Measurement measurementXml;
    private Persister serializerXml;
    private File xmlFileCompass, xmlFileConstuction, dataFileCompass, dataFileConstruction, newDataFile;
    private boolean method = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_geo_data_list_compass);

        // verification of intent content

        subActivityExtras = getIntent().getExtras();

        if (subActivityExtras.containsKey("newGeoDatas")) {

            getResults = subActivityExtras.getStringArrayList("newGeoDatas");

            method = false;
        }

        if (subActivityExtras.containsKey("setup_coordinates")) {

            setContentView(R.layout.activity_geo_data_list_construction);

            getSetupCoordinates = subActivityExtras.getStringArrayList("setup_coordinates");

            method = true;

        }

        setupCoordinates = new ArrayList<>();
        newGeoDataList = new ArrayList<>();
        allGeoData = new ArrayList<>();
        measurementXml = new Measurement();
        serializerXml = new Persister();
        newDataFile = new File(Environment.getExternalStorageDirectory() , "/GeoDatas/newGeoData.mgs");
        dataFileCompass = new File(Environment.getExternalStorageDirectory() , "/GeoDatas/geoDataCompass.mgs");
        dataFileConstruction = new File(Environment.getExternalStorageDirectory() , "/GeoDatas/geoDataConstruction.mgs");
        xmlFileCompass = new File(Environment.getExternalStorageDirectory() , "/GeoDatas/geoDataCompass.xml");
        xmlFileConstuction = new File(Environment.getExternalStorageDirectory() , "/GeoDatas/geoDataConstruction.xml");

        // load and read geo datas from file

        try{
            newGeoDataList = GeoDataLoad.loadGeoData(newDataFile);
            for (int i = 0; i < newGeoDataList.size(); i++) {
                Log.d("Debug ListNEW", newGeoDataList.get(i).getInfo());
            }
        } catch (IOException ex){
            Log.e("Debug FileLoad", "Lesen der newGeoData.mgs fehlgeschlagen");
        }

        lvMain = (ListView) findViewById(R.id.lv_main);
        lvMain.setChoiceMode((ListView.CHOICE_MODE_SINGLE));
        adapter = new ArrayAdapter<>(this, android.R.layout.select_dialog_multichoice, getResults);
        lvMain.setAdapter(adapter);
    }

    public void onClick(View view){

        SparseBooleanArray sbArray = lvMain.getCheckedItemPositions();
        try{
            switch(view.getId()) {

                case R.id.btn_remove_item:

                    for (int i = 0; i < sbArray.size(); i++){
                        int key = sbArray.keyAt(i);
                        if (sbArray.get(key));
                        getResults.remove(lvMain.getCheckedItemPosition());
                    }
                    adapter.notifyDataSetChanged();
                    newGeoDataList.remove(lvMain.getCheckedItemPosition());
                    GeoDataSave.saveGeoData(newGeoDataList, newDataFile);
                    for (int i = 0; i < newGeoDataList.size(); i++) {
                        Log.d("Debug ListREMOVE", newGeoDataList.get(i).getInfo());
                    }
                    break;

                case R.id.btn_use_as_start_coordinates:

                    for (int i = 0; i < sbArray.size(); i++){
                        int key = sbArray.keyAt(i);
                        if (sbArray.get(key));
                        checkedItem = getResults.get(lvMain.getCheckedItemPosition());
                    }
                    intent = new Intent();
                    intent.putExtra("checked_item", checkedItem).putStringArrayListExtra("actual_list", getResults);
                    setResult(RESULT_OK, intent);
                    finish();
                    break;

                case R.id.btn_export_file:

                    try {
                        if (!method) {
                            if (dataFileCompass.exists()) {
                                allGeoData = GeoDataLoad.loadGeoData(dataFileCompass);
                                for (int i = 0; i < allGeoData.size(); i++) {
                                    Log.d("Debug ListALL", allGeoData.get(i).getInfo());
                                }
                            }
                            for (int i = 0; i < newGeoDataList.size(); i++) {
                                allGeoData.add(newGeoDataList.get(i));
                            }
                            GeoDataSave.saveGeoData(allGeoData, dataFileCompass);
                            for (int i = 0; i < allGeoData.size(); i++) {
                                Log.d("Debug ListEXPORT", allGeoData.get(i).getInfo());
                            }
                            measurementXml.setGeoData(allGeoData);
                            serializerXml.write(measurementXml, xmlFileCompass);

                        } else {
                            if (dataFileConstruction.exists()) {
                                allGeoData = GeoDataLoad.loadGeoData(dataFileConstruction);
                                for (int i = 0; i < allGeoData.size(); i++) {
                                    Log.d("Debug ListALL", allGeoData.get(i).getInfo());
                                }
                            }
                            for (int i = 0; i < newGeoDataList.size(); i++) {
                                allGeoData.add(newGeoDataList.get(i));
                            }
                            GeoDataSave.saveGeoData(allGeoData, dataFileConstruction);
                            for (int i = 0; i < allGeoData.size(); i++) {
                                Log.d("Debug ListEXPORT", allGeoData.get(i).getInfo());
                            }
                            measurementXml.setGeoData(allGeoData);
                            serializerXml.write(measurementXml, xmlFileConstuction);
                        }

                    }   catch (IOException ex) {
                        Log.e("Debug XML", "xml Datei nicht erstellt");
                        Toast.makeText(GeoDataList.this, "Speichern fehlgeschlagen", Toast.LENGTH_SHORT).show();
                    }
                    break;

                default:
                    break;
            }
        } catch (Exception e){
            e.printStackTrace();
        }
    }

    public void onBackPressed() {

        intent = new Intent();
        intent.putStringArrayListExtra("setup_coordinates", this.getSetupCoordinates)
                .putStringArrayListExtra("actual_list", getResults);
        setResult(RESULT_OK, intent);
        finish();

    }
}
