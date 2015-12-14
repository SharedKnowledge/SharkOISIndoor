package de.berlin.htw.orinsy.vermessungs_tool;

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

public class GeoDataList extends Activity {

    private ArrayAdapter<String> adapter;
    private Bundle subActivityExtras;
    private ArrayList<String> results;
    private ArrayList<String> getResults;
    private String checkedItem = "null";
    private ListView lvMain;
    private Intent intent;
    private List<GeoData> allGeoData;
    private List<GeoData> newGeoDataList;
    private Measurement measurementXml;
    private Persister serializerXml;
    private File xmlFile;
    private File dataFile;
    private File newDataFile;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.geo_data_list);

        results = new ArrayList<>();
        newGeoDataList = new ArrayList<>();
        allGeoData = new ArrayList<>();
        measurementXml = new Measurement();
        serializerXml = new Persister();
        newDataFile = new File(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_NOTIFICATIONS) + "newGeoData.mgs");
        dataFile = new File(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_NOTIFICATIONS)  + "geoData.mgs");
        xmlFile = new File(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_NOTIFICATIONS) , "geoData.xml");

        try{
            newGeoDataList = GeoDataLoad.loadGeoData(newDataFile);
            for (int i = 0; i < newGeoDataList.size(); i++) {
                Log.d("Debug ListNEW", newGeoDataList.get(i).getInfo());
            }
        } catch (IOException ex){
            Log.e("Debug FileLoad", "Lesen der newGeoData.mgd fehlgeschlagen");
        }

        subActivityExtras = getIntent().getExtras();

        if (subActivityExtras.containsKey("newGeoDatas")) {

            getResults = subActivityExtras.getStringArrayList("newGeoDatas");
        }

        for (int i = 0; i < getResults.size(); i++) {

            results.add(getResults.get(i));
        }

        lvMain = (ListView) findViewById(R.id.lv_main);
        lvMain.setChoiceMode((ListView.CHOICE_MODE_SINGLE));
        adapter = new ArrayAdapter<>(this, android.R.layout.select_dialog_multichoice, results);
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
                        results.remove(lvMain.getCheckedItemPosition());

                    }

                    newGeoDataList.remove(lvMain.getCheckedItemPosition());
                    GeoDataSave.saveGeoData(newGeoDataList, newDataFile);
                    for (int i = 0; i < newGeoDataList.size(); i++) {
                        Log.d("Debug ListREMOVE", newGeoDataList.get(i).getInfo());
                    }
                    adapter.notifyDataSetChanged();
                    break;

                case R.id.btn_use_as_start_coordinates:

                    for (int i = 0; i < sbArray.size(); i++){
                        int key = sbArray.keyAt(i);
                        if (sbArray.get(key));
                        checkedItem = results.get(lvMain.getCheckedItemPosition());
                    }
                    intent = new Intent();
                    intent.putExtra("checked_item", checkedItem).putStringArrayListExtra("actual_list", results);
                    setResult(RESULT_OK, intent);
                    finish();
                    break;

                case R.id.btn_export_file:

                    try{
                        if (dataFile.exists()){
                            allGeoData = GeoDataLoad.loadGeoData(dataFile);
                            for (int i = 0; i < allGeoData.size(); i++) {
                                Log.d("Debug ListALL", allGeoData.get(i).getInfo());
                            }
                        }
                        for (int i = 0; i < newGeoDataList.size(); i++) {
                            allGeoData.add(newGeoDataList.get(i));
                        }
                        GeoDataSave.saveGeoData(allGeoData, dataFile);
                        for (int i = 0; i < allGeoData.size(); i++) {
                            Log.d("Debug ListEXPORT", allGeoData.get(i).getInfo());
                        }
                        measurementXml.setGeoData(allGeoData);
                        serializerXml.write(measurementXml, xmlFile);
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
        intent.putStringArrayListExtra("actual_list", results);
        setResult(RESULT_OK, intent);
        finish();

    }



}
