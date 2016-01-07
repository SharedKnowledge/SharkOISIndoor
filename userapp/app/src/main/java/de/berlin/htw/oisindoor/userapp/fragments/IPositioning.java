package de.berlin.htw.oisindoor.userapp.fragments;

import android.support.annotation.NonNull;

/**
 * Created by Max on 22.12.2015.
 */
public interface IPositioning {

    void showSearchingDialog();

    void cancelSearchingDialog();

    void updatePosition(@NonNull String url);
}