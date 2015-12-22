package de.berlin.htw.oisindoor.userapp.fragments;

import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import butterknife.Bind;
import butterknife.ButterKnife;
import de.berlin.htw.oisindoor.userapp.R;
import de.berlin.htw.oisindoor.userapp.adapter.BeaconTopicsRecyclerViewAdapter;
import de.berlin.htw.oisindoor.userapp.model.Topic;

/**
 * Created by Max on 23.11.2015.
 */
public class PositioningFragment extends Fragment implements IPositioning {

    public static PositioningFragment newInstance() {
        return new PositioningFragment();
    }

    public interface TopicListener {
        void onTopicClicked(@NonNull Topic t);
    }

    private static final String TAG = PositioningFragment.class.getSimpleName();

    @Bind(R.id.f_positioning_lat) TextView latText;
    @Bind(R.id.f_positioning_lon) TextView lonText;
    @Bind(R.id.f_positioning_alt) TextView altText;
    private ProgressDialog dialog;
    /**
     * Workaround because the activity is faster then the fragment instantiation when bluetooth is already enabled,
     * i.e. not ready to show a dialog
     * so add a pending flag
     */
    private boolean isDialogPending = false;

    /**
     * Mandatory empty constructor for the fragment manager to instantiate the
     * fragment (e.g. upon screen orientation changes).
     */
    public PositioningFragment() {}

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        Log.d(TAG, "onCreateView");
        View v = inflater.inflate(R.layout.f_positioning, container, false);
        ButterKnife.bind(this, v);

        RecyclerView recyclerView = (RecyclerView) v.findViewById(R.id.f_positioning_rv);
        recyclerView.setLayoutManager(new LinearLayoutManager(v.getContext()));
        recyclerView.setHasFixedSize(true);
        recyclerView.setNestedScrollingEnabled(true);
        recyclerView.setAdapter(new BeaconTopicsRecyclerViewAdapter(Topic.ITEMS, new TopicListener() {
            @Override
            public void onTopicClicked(@NonNull Topic t) {
                Log.d(TAG, "onTopicClicked: " + t);
                startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse(t.getTargetURL())));
            }
        }));
        return v;
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        if (isDialogPending) {
            showSearchingDialog();
        }
    }

    /*
     * IPositioning
     */

    public void showSearchingDialog() {
        if (isAdded()) {
            isDialogPending = false;
            showDialog();
        } else {
            isDialogPending = true;
        }
    }

    @Override
    public void cancelSearchingDialog() {
        if (dialog != null) {
            dialog.dismiss();
        }
    }

    public void updatePosition(@NonNull String url){
        Log.d(TAG, "updatePosition " + url);
        if (dialog != null) {
            dialog.dismiss();
        }
        url = url.substring(2); // FIXME: Coding and co problems
        List<String> t = readPropperGEO(url);
        latText.setText(t.get(0));
        lonText.setText(t.get(1));
        altText.setText(t.get(2));
    }

    /*
     * Stuff
     */

    private void showDialog() {
        dialog = new ProgressDialog(getActivity());
        dialog.setIcon(R.mipmap.perm_group_bluetooth);
        dialog.setTitle(R.string.bt_searchForBeacons);
        dialog.setCancelable(true);
        dialog.setCanceledOnTouchOutside(false);
        dialog.setButton(DialogInterface.BUTTON_NEGATIVE, getString(android.R.string.cancel), new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
            }
        });
        dialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
        dialog.setIndeterminate(true);
        dialog.show();
    }

    // thx to Stefan J.
    private List<String> readPropperGEO(String geoAsString) {
        List<String> results = new ArrayList<>();
        Pattern pattern = Pattern.compile("\\d{1,3}\\.\\d*");
        Matcher matcher = pattern.matcher(geoAsString);
        while(matcher.find()) {
            results.add(matcher.group());
        }
        return results;
    }

}
