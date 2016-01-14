package de.berlin.htw.oisindoor.userapp.shark;

import android.os.AsyncTask;
import android.support.annotation.NonNull;

import java.util.ArrayList;

import de.berlin.htw.oisindoor.userapp.model.Topic;

/**
 * Created by Max on 30.11.2015.
 */
public class MagicSharkTask extends AsyncTask<String, Void, ArrayList<Topic>> {
    private String readBeaconLink;
    private GenericCallback<ArrayList<Topic>> callback;

    public MagicSharkTask(@NonNull String readBeaconLink, @NonNull GenericCallback<ArrayList<Topic>> callback) {
        this.readBeaconLink = readBeaconLink;
        this.callback = callback;
    }

    @Override
    protected ArrayList<Topic> doInBackground(String... params) {
        // TODO make magic calls
        return null;
    }

    @Override
    protected void onPostExecute(ArrayList<Topic> beaconContent) {
        callback.onResult(beaconContent);
    }

}
