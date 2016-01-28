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

/**
 * @brief setup coordinates class provides possibility to setup start and reference coordinates
 *
 * @author Maik M
 */

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

    /**
     * @brief alert builder class creates and shows dialogue with different texts and functions, affected
     *          by key value
     */

    public class AlertBuilder {

        /**
         * @brief constructor
         *
         * @param context
         */

        public AlertBuilder(Context context) {

            AlertDialog.Builder alert = new AlertDialog.Builder(context);

            if (SetupCoordinates.this.key == 1) {
                alert.setMessage("Enter Height");
            }
            if (SetupCoordinates.this.key == 2) {
                alert.setMessage("Enter Floor");
            }

            final EditText input = new EditText(context);
            input.setInputType(0x00000003);
            alert.setView(input);

            alert.setPositiveButton("OK", new DialogInterface.OnClickListener() {
                public void onClick(DialogInterface dialog, int whichButton) {

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

}
