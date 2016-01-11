package de.berlin.htw.oisindoor.userapp.util;

import android.app.Activity;
import android.app.AlertDialog;
import android.bluetooth.BluetoothManager;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.location.LocationManager;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Build;
import android.support.annotation.NonNull;
import android.support.annotation.StringRes;
import android.support.design.widget.Snackbar;
import android.view.View;

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

    public static void showDialog(final Activity c, int titleID, int messageID, final String action, final int code) {
        if (isDialogShown) {
            return;
        }

        isDialogShown = true;
        new AlertDialog.Builder(c)
            .setCancelable(false)
            .setIcon(R.mipmap.perm_group_bluetooth)
            .setTitle(titleID)
            .setMessage(messageID)
            .setPositiveButton(R.string.fix, new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    c.startActivityForResult(new Intent(action), code);
                    isDialogShown = false;
                    dialog.dismiss();
                }
            })
            .setNegativeButton(android.R.string.cancel, new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    isDialogShown = false;
                    dialog.dismiss();
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

    private Util() {}

}