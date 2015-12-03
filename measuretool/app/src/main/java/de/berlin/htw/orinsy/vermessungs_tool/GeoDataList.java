package de.berlin.htw.orinsy.vermessungs_tool;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.os.Environment;
import android.util.SparseBooleanArray;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;

public class GeoDataList extends Activity {

    private ArrayAdapter<String> adapter;
    private Bundle subActivityExtras;
    private ArrayList<String> results;
    private ArrayList<String> getResults;
    private String checkedItem = "null";
    private ListView lvMain;
    private Intent intent;
    private FileWriter writer;
    private File file;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.geo_data_list);

        results = new ArrayList<>();
        file = new File(Environment.getExternalStorageDirectory(), "geoData.xml");

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

                /**

                case R.id.btn_export_file:

                    try{
                        this.write(results, file);
                    }   catch (IOException ex) {
                        Toast.makeText(GeoDataList.this, "Speichern fehlgeschlagen", Toast.LENGTH_SHORT).show();
                    }
                    break;

                 */

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

    public void  write(ArrayList<String> allGeoData, File file) throws IOException{

        writer = new FileWriter(file);
        writer.write("<?xml version=\"1.0\" encoding=\"utf-8\"?>");

        for( int i = 0; i < allGeoData.size(); i++){
            writer.write("<GeoData  content=\"" + allGeoData.get(i));
            writer.write("</GeoData>");
        }

        writer.flush();
    }

    @Override
    protected void onPause() {
        super.onPause();
    }
}
