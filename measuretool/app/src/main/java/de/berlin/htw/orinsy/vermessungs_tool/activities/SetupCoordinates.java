package de.berlin.htw.orinsy.vermessungs_tool.activities;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.PopupMenu;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;

import de.berlin.htw.orinsy.vermessungs_tool.R;

public class SetupCoordinates extends Activity {

    private EditText enterLongitudeStart, enterLatitudeStart, enterLongitudeReference, enterLatitudeReference;
    private TextView showHeight;
    private Intent intent;
    private String height;
    private double heightValue;
    private ArrayList<String> results;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_setup_coordinates);
        this.results = new ArrayList<>();

        this.height = "";
        this.enterLatitudeStart = (EditText) findViewById(R.id.start_latitude);
        this.enterLongitudeStart = (EditText) findViewById(R.id.start_longitude);
        this.enterLatitudeReference = (EditText) findViewById(R.id.reference_latitude);
        this.enterLongitudeReference = (EditText) findViewById(R.id.reference_longitude);
        this.showHeight = (TextView) findViewById(R.id.tv_input_height);
    }

    public void onClick(View v) {

        try {

            switch (v.getId()) {

                case R.id.btn_menu_height:

                    PopupMenu popup = new PopupMenu(this, findViewById(R.id.btn_menu_height));
                    //Inflating the Popup using xml file
                    popup.getMenuInflater().inflate(R.menu.popup_menu_height, popup.getMenu());

                    //registering popup with OnMenuItemClickListener
                    popup.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
                        public boolean onMenuItemClick(MenuItem item) {

                            SetupCoordinates.this.height = item.getTitle().toString();
                            switch (item.getItemId()){
                                case R.id.floor_0:
                                    heightValue = 0.0;
                                    break;
                                case R.id.floor_1:
                                    heightValue = 5.0;
                                    break;
                                case R.id.floor_2:
                                    heightValue = 10.0;
                                    break;
                                case R.id.floor_3:
                                    heightValue = 15.0;
                                    break;
                                case R.id.floor_4:
                                    heightValue = 20.0;
                                    break;
                                case R.id.floor_5:
                                    heightValue = 25.0;
                                    break;
                                case R.id.floor_6:
                                    heightValue = 30.0;
                                    break;
                            }
                            SetupCoordinates.this.showHeight.setText(String.valueOf(height));
                            Toast.makeText(getBaseContext(), "You Clicked : " + height, Toast.LENGTH_SHORT).show();
                            return true;
                        }
                    });
                    popup.show();//showing popup menu
                    break;

                case R.id.btn_ok:

                    if (this.height.equals("")){
                        throw new Exception();
                    }

                    this.results.add(0, this.enterLatitudeStart.getText().toString());
                    this.results.add(1, this.enterLongitudeStart.getText().toString());
                    this.results.add(2, this.enterLatitudeReference.getText().toString());
                    this.results.add(3, this.enterLongitudeReference.getText().toString());
                    this.results.add(4, this.height);
                    this.results.add(5, String.valueOf(this.heightValue));
                    this.intent = new Intent(this, ConstructionDrawingMethod.class);
                    this.intent.putStringArrayListExtra("input_datas", this.results);
                    setResult(RESULT_OK, this.intent);
                    finish();
                    break;

            }
        } catch (Exception e) {
            e.printStackTrace();
            Toast.makeText(this, "fill out all fields", Toast.LENGTH_SHORT).show();
        }
    }

    public double getHeightValue(){
        return heightValue;
    }
}
