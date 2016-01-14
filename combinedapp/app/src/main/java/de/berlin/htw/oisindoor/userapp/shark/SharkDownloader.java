package de.berlin.htw.oisindoor.userapp.shark;

import android.os.AsyncTask;
import android.support.annotation.NonNull;

import java.util.ArrayList;

import de.berlin.htw.oisindoor.userapp.model.Topic;

/**
 * Created by Max on 30.11.2015.
 */
public class SharkDownloader extends AsyncTask<String, Void, ArrayList<Topic>> {
    private String readBeaconLink;
    private GenericCallback<ArrayList<Topic>> callback;

    public SharkDownloader(@NonNull String readBeaconLink, @NonNull GenericCallback<ArrayList<Topic>> callback) {
        this.readBeaconLink = readBeaconLink;
        this.callback = callback;
    }

    /**
     *  TODO: make magic calls insteadof fake the results
     * @param params
     * @return
     */
    @Override
    protected ArrayList<Topic> doInBackground(String... params) {
        try {
            Thread.sleep(3_000);
        } catch (InterruptedException e) {}

        return Topic.ITEMS;
    }

    @Override
    protected void onPostExecute(ArrayList<Topic> beaconContent) {
        callback.onResult(beaconContent);
    }

}
