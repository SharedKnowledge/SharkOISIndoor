package de.berlin.htw.oisindorr.userapp.fragments;

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

import de.berlin.htw.oisindorr.userapp.R;
import de.berlin.htw.oisindorr.userapp.adapter.BeaconTopicsRecyclerViewAdapter;
import de.berlin.htw.oisindorr.userapp.model.Topic;

/**
 * Created by Max on 23.11.2015.
 */
public class DetailFragment extends Fragment {
    private static final String TAG = DetailFragment.class.getName();

    public static DetailFragment newInstance() {
        return new DetailFragment();
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.f_positioning, container, false);

//        TextView beconText = (TextView) v.findViewById(R.id.f_positioning_beacon_text);
//        beconText.setMovementMethod(LinkMovementMethod.getInstance());

        RecyclerView recyclerView = (RecyclerView) v.findViewById(R.id.f_positioning_rv);
        recyclerView.setLayoutManager(new LinearLayoutManager(v.getContext()));
        recyclerView.setHasFixedSize(true);
        recyclerView.setNestedScrollingEnabled(true);
        recyclerView.setAdapter(new BeaconTopicsRecyclerViewAdapter(Topic.ITEMS, new TopicListener(){
            @Override
            public void onTopicClicked(@NonNull Topic t) {
                Log.d(TAG, "onTopicClicked: " + t);
                Intent browserIntent = new Intent(Intent.ACTION_VIEW, Uri.parse(t.getTargetURL()));
                startActivity(browserIntent);
            }
        }));
        return v;
    }

    public interface TopicListener {
        void onTopicClicked(@NonNull Topic t);
    }
}
