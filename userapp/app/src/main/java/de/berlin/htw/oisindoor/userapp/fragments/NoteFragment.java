package de.berlin.htw.oisindoor.userapp.fragments;

import android.content.Context;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import java.util.ArrayList;
import java.util.List;

import de.berlin.htw.oisindoor.userapp.R;
import de.berlin.htw.oisindoor.userapp.adapter.NotesGeoCoordinateRecyclerViewAdapter;
import de.berlin.htw.oisindoor.userapp.model.GeoCoordinate;

public class NoteFragment extends Fragment {
    private static final String ARG_LIST = "geolist";

    public static NoteFragment newInstance(@NonNull ArrayList<GeoCoordinate> l) {
        NoteFragment fragment = new NoteFragment();
        Bundle args = new Bundle();
        args.putParcelableArrayList(ARG_LIST, l);
        fragment.setArguments(args);
        return fragment;
    }

    public interface OnListFragmentInteractionListener {
        void onListClicked(@NonNull GeoCoordinate item);
    }

    private List<GeoCoordinate> list;
    private OnListFragmentInteractionListener listener;

    public NoteFragment() {}

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (context instanceof OnListFragmentInteractionListener) {
            listener = (OnListFragmentInteractionListener) context;
        } else {
            throw new RuntimeException(context.toString() + " must implement OnListFragmentInteractionListener");
        }
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            list = getArguments().getParcelableArrayList(ARG_LIST);
        }else {
            throw new IllegalStateException("Arguments are null");
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        RecyclerView recyclerView = (RecyclerView) inflater.inflate(R.layout.f_notes, container, false);
        recyclerView.setLayoutManager(new LinearLayoutManager(recyclerView.getContext()));
        recyclerView.setHasFixedSize(true);
        recyclerView.setAdapter(new NotesGeoCoordinateRecyclerViewAdapter(list, listener));
        return recyclerView;
    }

    @Override
    public void onDetach() {
        super.onDetach();
        listener = null;
    }

}
