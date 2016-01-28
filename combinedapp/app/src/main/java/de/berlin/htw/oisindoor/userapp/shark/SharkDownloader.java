package de.berlin.htw.oisindoor.userapp.shark;

import android.os.AsyncTask;
import android.support.annotation.NonNull;

import java.util.ArrayList;

import de.berlin.htw.oisindoor.userapp.model.Topic;

/**
 * Class which does send the read URL (the location info) to the Shark-DB Server</br>
 * Result is ja List of {@link Topic Topics}</br>
 * To do not block the UI, it is implemented a AsyncTask
 * @author Max M
 */
public class SharkDownloader extends AsyncTask<Void, Void, ArrayList<Topic>> {
    private String readBeaconLink;
    private GenericCallback<ArrayList<Topic>> callback;

    /**
     * Creates a Instance of the SharkDownloader
     * @param readBeaconLink - The read location info (latitude, longitude, altitude)
     * @param callback - A Callback, which get informed when the Result are there
     */
    public SharkDownloader(@NonNull String readBeaconLink, @NonNull GenericCallback<ArrayList<Topic>> callback) {
        this.readBeaconLink = readBeaconLink;
        this.callback = callback;
    }

    /**
     *  TODO: make magic calls instead of fake the results
     * @param params - nothing...
     * @return - A List of topics related to the Location
     */
    @Override
    protected ArrayList<Topic> doInBackground(Void... params) {
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
