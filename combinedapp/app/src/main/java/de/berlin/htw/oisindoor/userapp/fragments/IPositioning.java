package de.berlin.htw.oisindoor.userapp.fragments;

import android.support.annotation.NonNull;

/**
 * Interface which can be implement by Fragments, to by notified from the Activity
 *
 * @author Max M
 */
public interface IPositioning {

    /**
     * @param url - read Beacon Content (i.e. latitude, longitude, altitude), which will be split later
     */
    void updatePosition(@NonNull String url);
}
