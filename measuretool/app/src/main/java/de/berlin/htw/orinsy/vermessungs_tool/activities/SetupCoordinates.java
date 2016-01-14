package de.berlin.htw.orinsy.vermessungs_tool.activities;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;

import de.berlin.htw.orinsy.vermessungs_tool.R;

public class SetupCoordinates extends Activity {

    private EditText enterLongitudeStart, enterLatitudeStart, enterLongitudeReference, enterLatitudeReference;
    private TextView showHeight, showFloor;
    private Intent intent;
    private String height, info, floor;
    private ArrayList<String> results;
    private AlertBuilder alertBuilder;
    private int key = 0;

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
        this.showFloor = (TextView) findViewById(R.id.tv_input_floor);

    }

    public void onClick(View v) {

        try {

            switch (v.getId()) {

                case R.id.btn_menu_height:

                    key = 1;

                    alertBuilder = new AlertBuilder(this);

                    /**

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

                     */
                    break;

                case R.id.btn_menu_floor:

                    key = 2;
                    alertBuilder = new AlertBuilder(this);

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
                    this.results.add(5, this.floor);
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

    public class AlertBuilder {

        public AlertBuilder(Context context) {

            AlertDialog.Builder alert = new AlertDialog.Builder(context);
            //  alert.setTitle("Alert Dialog With EditText"); //Set Alert dialog title here
            if (SetupCoordinates.this.key == 1) {
                alert.setMessage("Enter Height"); //Message here
            }
            if (SetupCoordinates.this.key == 2) {
                alert.setMessage("Enter Floor");
            }

            // Set an EditText view to get user input
            final EditText input = new EditText(context);
            input.setInputType(0x00000003);
            alert.setView(input);

            alert.setPositiveButton("OK", new DialogInterface.OnClickListener() {
                public void onClick(DialogInterface dialog, int whichButton) {
                    //You will get as string input data in this variable.
                    // here we convert the input to a string and show in a toast.

                    SetupCoordinates.this.info = input.getEditableText().toString();

                    if (SetupCoordinates.this.key == 1) {

                        SetupCoordinates.this.height = info;
                        showHeight.setText(info);
                        showHeight.setVisibility(View.VISIBLE);
                    }
                    if (SetupCoordinates.this.key == 2) {

                        SetupCoordinates.this.floor = info;
                        showFloor.setText(info);
                        showFloor.setVisibility(View.VISIBLE);
                    }

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

}
