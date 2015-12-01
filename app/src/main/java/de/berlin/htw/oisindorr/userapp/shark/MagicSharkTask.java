package de.berlin.htw.oisindorr.userapp.shark;

import android.os.AsyncTask;
import android.support.annotation.NonNull;

import de.berlin.htw.oisindorr.userapp.model.BeaconContent;

/**
 * Created by Max on 30.11.2015.
 */
public class MagicSharkTask extends AsyncTask<String, Void, BeaconContent> {
    private String readBeaconLink;
    private GenericCallback<BeaconContent> callback;

    public MagicSharkTask(@NonNull String readBeaconLink, @NonNull GenericCallback<BeaconContent> callback) {
        this.readBeaconLink = readBeaconLink;
        this.callback = callback;
    }

    @Override
    protected BeaconContent doInBackground(String... params) {
        // TODO make magic calls
        return null;
    }

    @Override
    protected void onPostExecute(BeaconContent beaconContent) {
        callback.onResult(beaconContent);
    }
}
