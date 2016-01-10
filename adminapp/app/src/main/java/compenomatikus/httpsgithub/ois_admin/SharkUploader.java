package compenomatikus.httpsgithub.ois_admin;

import android.app.Activity;
import android.app.AlertDialog;
import android.bluetooth.BluetoothAdapter;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.os.AsyncTask;
import android.util.Log;

import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.List;

/**
 * Created by stefan on 10.01.16.
 */
public class SharkUploader extends AsyncTask<Void, Void, Void> {

    private List<String> uploadData;
    private final String TAG = this.getClass().getSimpleName();

    public SharkUploader(List<String> uploadData){
        this.uploadData = uploadData;
    }

    private void upload(){
        /**** Put upload here ****/
        long testcase = 1000000;
        long i = -100000;
        while (i < testcase)
            Log.i(TAG, "" + i++);
    }

    @Override
    protected Void doInBackground(Void... params) {
        upload();
        return null;
    }
}
