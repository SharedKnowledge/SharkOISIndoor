package de.berlin.htw.oisindoor.userapp.util;

import android.app.AlertDialog;
import android.bluetooth.BluetoothManager;
import android.content.Context;
import android.content.DialogInterface;
import android.content.res.Resources;
import android.location.LocationManager;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Build;
import android.support.annotation.DrawableRes;
import android.support.annotation.NonNull;
import android.support.annotation.StringRes;
import android.support.design.widget.Snackbar;
import android.util.Log;
import android.view.View;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import de.berlin.htw.oisindoor.userapp.R;

public class Util {
    private static boolean isDialogShown;

    private static LocationManager getLocationManager(Context context) {
        return (LocationManager) context.getSystemService(Context.LOCATION_SERVICE);
    }

    private static ConnectivityManager getConnectivityManager(Context context) {
        return (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
    }

    private static BluetoothManager getBluetoothManager(Context context) {
        return (BluetoothManager) context.getSystemService(Context.BLUETOOTH_SERVICE);
    }

    public static boolean isLocationProviderEnabled(Context context) {
        LocationManager lm = getLocationManager(context);
        return lm.isProviderEnabled(LocationManager.GPS_PROVIDER) || lm.isProviderEnabled(LocationManager.NETWORK_PROVIDER);
    }

    public static boolean isInternetAvailable(Context context) {
        NetworkInfo info = getConnectivityManager(context).getActiveNetworkInfo();
        return (info != null && info.isConnectedOrConnecting());
    }

    public static boolean isBluetoothEnabled(Context context) {
        return getBluetoothManager(context).getAdapter().isEnabled();
    }

    public static boolean isLollipop() {
        return Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP;
    }

    public interface DialogCallback {
        void onPositiveButtonPressed();
        void onNegativeButtonPressed();
    }

    public static void showDialog(@NonNull Context c, @StringRes int titleID, @StringRes int messageID, @DrawableRes int iconID,
                                  @NonNull final DialogCallback callback) {
        if (isDialogShown) {
            return;
        }
        isDialogShown = true;
        new AlertDialog.Builder(c)
                .setCancelable(false)
                .setIcon(iconID)
                .setTitle(titleID)
                .setMessage(messageID)
                .setPositiveButton(R.string.fix, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        callback.onPositiveButtonPressed();
                        isDialogShown = false;
                        dialog.dismiss();
                    }
                })
                .setNegativeButton(android.R.string.cancel, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        callback.onNegativeButtonPressed();
                        isDialogShown = false;
                        dialog.dismiss();
                    }
                })
                .setOnDismissListener(new DialogInterface.OnDismissListener() {
                    @Override
                    public void onDismiss(DialogInterface dialog) {
                        Log.d("Util", "onDismiss");
                    }
                })
                .show();
    }

    public static void showUIMessage(@NonNull View v, @StringRes int res) {
        Snackbar.make(v, res, Snackbar.LENGTH_SHORT).show();
    }

    public static void showUIMessage(@NonNull View v, @NonNull CharSequence res) {
        Snackbar.make(v, res, Snackbar.LENGTH_SHORT).show();
    }

    /**
     * Method to search a given String for geo information.
     * It will use the regex <b>(\+|-)?\d{1,3}\.\d*</b> to get the latitude, longitude and altitude values
     * from the given String using a while loop until all characters are analyzed.
     * If the method found exactly three geo values the search finished successfully and those values
     * will be returned. Else a default return value will be created.
     *
     * @param geoAsString A String may holding geo information
     * @return Either an ArrayList<String> with found geos or an ArrayList<String> with the content
     * "Keine","korrekten",geos".
     */
    public static List<String> readPropperGEO(final Resources r, String geoAsString) {
        int matched = 0;
        List<String> results = new ArrayList<>();
        Pattern pattern = Pattern.compile("(\\+|-)?\\d{1,3}\\.\\d*");
        Matcher matcher = pattern.matcher(geoAsString);
        Log.i("readPropperGEO(Str):", "START");
        while (matcher.find()) {
            Log.i("( " + ++matched + " ) Matched: ", matcher.group());
            results.add(matcher.group());
            if (matched > 3) {
                Log.e("Util", "Found more information(" + matched + "), than needed. Break loop.");
                break;
            }
        }
        Log.i("readPropperGeo(Str):", "END " + matched);
        if (matched == 3)
            return results;
        else {
            return new ArrayList<String>() {{
                add(r.getString(R.string.no_propper_geos_1));
                add(r.getString(R.string.no_propper_geos_2));
                add(r.getString(R.string.no_propper_geos_3));
            }};
        }
    }

    private Util() {
    }

}