package de.berlin.htw.orinsy.vermessungs_tool.activities;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.os.Environment;
import android.util.Log;
import android.view.View;

import java.io.File;

import de.berlin.htw.orinsy.vermessungs_tool.R;

/**
 * @brief start activity provides 2 buttons to choose between separate longitude and latitude calculations
 *
 * @author Maik M
 */

public class StartActivity extends Activity {

    Intent intent;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_start);

        File GeoDataFolder = new File(Environment.getExternalStorageDirectory() + "/GeoDatas");
        boolean successCreateFolder = false;
        if(!GeoDataFolder.exists()){
            GeoDataFolder.mkdirs();
            successCreateFolder = true;
        }
        if(successCreateFolder){
            Log.d("FolderCREA", "Folder created!");
        }
        else{
            Log.d("FolderCREA", "Folder not created.");
        }

    }

    public void onClick(View v) {

        switch (v.getId()) {

            case R.id.btn_gps_method:
                intent = new Intent(this, CompassMethod.class);
                startActivity(intent);
                break;

            case R.id.btn_construction_drawing_method:
                intent = new Intent(this, ConstructionDrawingMethod.class);
                startActivity(intent);
                break;
        }
    }
}
