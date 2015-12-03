package de.berlin.htw.orinsy.vermessungs_tool;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.os.Environment;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import java.io.File;
import java.util.ArrayList;

public class MainActivity extends Activity {

    private ArrayList<String> results;
    private Intent intent;
    private EditText enterLongitude, enterLatitude, enterDistance, enterAngle, enterHeight;
    private double longitude = 0, latitude = 0, distance = 0, angle = 0, height = 0;
    private double newLatitude = 0, newLongitude = 0;
    private TextView textView1, textView2, textView4;
    private final Context context = this;
    private String info, checkedItem;
    private String[] splitedSubstring1, splitedSubstring2, splitedSubstring3;
    private CalculateGeoData geoData;
    private InputMethodManager inputManager;
    private static int SUBACTIVITY_REQUESTCODE = 10;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        enterLatitude = (EditText) findViewById(R.id.latitude);
        enterLongitude = (EditText) findViewById(R.id.longitude);
        enterDistance = (EditText) findViewById(R.id.edit_distance);
        enterAngle = (EditText) findViewById(R.id.edit_angle);
        enterHeight = (EditText) findViewById(R.id.edit_height);
        textView1 = (TextView) findViewById(R.id.tv_result_new_latitude);
        textView2 = (TextView) findViewById(R.id.tv_result_new_longitude);
        textView4 = (TextView) findViewById(R.id.textView4);
        results = new ArrayList<>();
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

                    geoData = new CalculateGeoData(latitude, longitude, distance, angle);

                    newLatitude = geoData.newLatitude();
                    newLongitude = geoData.newLongitude();

                    newLatitude = Math.round(100000000.0 * newLatitude) / 100000000.0;
                    newLongitude = Math.round(100000000.0 * newLongitude) / 100000000.0;

                    inputManager = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
                    inputManager.hideSoftInputFromWindow((null == getCurrentFocus()) ? null : getCurrentFocus().getWindowToken(), InputMethodManager.HIDE_NOT_ALWAYS);

                    AlertDialog.Builder alert = new AlertDialog.Builder(context);
                    //  alert.setTitle("Alert Dialog With EditText"); //Set Alert dialog title here
                    alert.setMessage("Enter Info for Geo Data"); //Message here

                    // Set an EditText view to get user input
                    final EditText input = new EditText(context);
                    input.setInputType(0x00000001);
                    alert.setView(input);

                    alert.setPositiveButton("OK", new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int whichButton) {
                            //You will get as string input data in this variable.
                            // here we convert the input to a string and show in a toast.
                            MainActivity.this.info = input.getEditableText().toString();
                            textView1.setText(String.valueOf(newLatitude));
                            textView2.setText(String.valueOf(newLongitude));
                            textView4.setVisibility(View.VISIBLE);

                            results.add("Info: " + info + "  Height: " + height + "\nLa: " + newLatitude + "\nLo: " + newLongitude);

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
                    break;

                case R.id.btn_apply_new_geodata:

                    intent = new Intent(this, GeoDataList.class).putStringArrayListExtra("newGeoDatas", results);
                    startActivityForResult(intent, SUBACTIVITY_REQUESTCODE);
                    break;

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

                results = data.getStringArrayListExtra("actual_list");

                if (data.hasExtra("checked_item")) {

                    checkedItem = data.getStringExtra("checked_item");
                    splitedSubstring1 = checkedItem.split("\nLo: ");
                    splitedSubstring2 = splitedSubstring1[0].split("La: ");
                    splitedSubstring3 = splitedSubstring2[0].split("Height: ");
                    enterLatitude.setText(splitedSubstring2[1]);
                    enterLongitude.setText(splitedSubstring1[1]);
                    enterAngle.setText(splitedSubstring3[1]);
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
