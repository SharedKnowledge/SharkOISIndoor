package de.berlin.htw.oisindoor.userapp.fragments;

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
public class PositioningFragment extends Fragment {
    private static final String TAG = PositioningFragment.class.getName();

    public static PositioningFragment newInstance() {
        return new PositioningFragment();
    }

    public interface TopicListener {
        void onTopicClicked(@NonNull Topic t);
    }

    @Bind(R.id.f_positioning_lat) TextView latText;
    @Bind(R.id.f_positioning_lon) TextView lonText;
    @Bind(R.id.f_positioning_alt) TextView altText;

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
                Intent browserIntent = new Intent(Intent.ACTION_VIEW, Uri.parse(t.getTargetURL()));
                startActivity(browserIntent);
            }
        }));
        return v;
    }

    public void updatePosition(String url){
        Log.d(TAG, "onReceive " + url);
        List<String> t = readPropperGEO(url);
        latText.setText(t.get(0));
        lonText.setText(t.get(1));
        altText.setText(t.get(2));
    }

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

