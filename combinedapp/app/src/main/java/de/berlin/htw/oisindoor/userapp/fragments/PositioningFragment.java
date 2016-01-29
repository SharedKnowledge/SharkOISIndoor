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

import butterknife.Bind;
import butterknife.ButterKnife;
import de.berlin.htw.oisindoor.userapp.R;
import de.berlin.htw.oisindoor.userapp.adapter.BeaconTopicsRecyclerViewAdapter;
import de.berlin.htw.oisindoor.userapp.model.Topic;
import de.berlin.htw.oisindoor.userapp.util.Util;

/**
 * Fragment, which contains a statusbar with the current locations and a list containing topics related to the location<br/>
 * Any Activity can easily update the content, (the location and the topic list) via
 * {@link #updatePosition(String)} and {@link #updateTopics(List)}
 *
 * @author Max M
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
    private BeaconTopicsRecyclerViewAdapter adapter;

    public PositioningFragment() {
        // Required empty public constructor
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        Log.d(TAG, "onCreateView");
        View v = inflater.inflate(R.layout.f_positioning, container, false);
        ButterKnife.bind(this, v);

        adapter = new BeaconTopicsRecyclerViewAdapter(new ArrayList<Topic>(), new TopicListener() {
            @Override
            public void onTopicClicked(@NonNull Topic t) {
                Log.d(TAG, "onTopicClicked: " + t);
                startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse(t.getTargetURL())));
            }
        });

        RecyclerView recyclerView = (RecyclerView) v.findViewById(R.id.f_positioning_rv);
        recyclerView.setLayoutManager(new LinearLayoutManager(v.getContext()));
        recyclerView.setHasFixedSize(true);
        recyclerView.setNestedScrollingEnabled(true);
        recyclerView.setAdapter(adapter);
        return v;
    }

    public void updateTopics(@Nullable List<Topic> topicList) {
        adapter.updateItems(topicList);
    }

    /* IPositioning */

    @Override
    public void updatePosition(@NonNull String url){
        Log.d(TAG, "updatePosition " + url);
        List<String> t = Util.readPropperGEO(getResources(), url);
        latText.setText(t.get(0));
        lonText.setText(t.get(1));
        altText.setText(t.get(2));
    }

}
