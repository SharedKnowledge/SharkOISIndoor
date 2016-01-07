package de.berlin.htw.orinsy.vermessungs_tool.activities;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;

import de.berlin.htw.orinsy.vermessungs_tool.R;

public class StartActivity extends Activity {

    Intent intent;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_start);
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
