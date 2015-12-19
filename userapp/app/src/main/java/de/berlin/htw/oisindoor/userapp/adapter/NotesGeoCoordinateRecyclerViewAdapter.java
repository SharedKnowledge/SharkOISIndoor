package de.berlin.htw.oisindoor.userapp.adapter;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.util.List;

import butterknife.Bind;
import butterknife.ButterKnife;
import de.berlin.htw.oisindoor.userapp.R;
import de.berlin.htw.oisindoor.userapp.fragments.NoteFragment;
import de.berlin.htw.oisindoor.userapp.model.GeoCoordinate;

public class NotesGeoCoordinateRecyclerViewAdapter extends RecyclerView.Adapter<NotesGeoCoordinateRecyclerViewAdapter.ViewHolder> {

    private final List<GeoCoordinate> mValues;
    private final NoteFragment.OnListFragmentInteractionListener mListener;

    public NotesGeoCoordinateRecyclerViewAdapter(List<GeoCoordinate> items, NoteFragment.OnListFragmentInteractionListener listener) {
        mValues = items;
        mListener = listener;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_note, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(final ViewHolder holder, int position) {
        final GeoCoordinate item = mValues.get(position);
        holder.latitudeText.setText(String.valueOf(item.getLatitude()));
        holder.longitudeText.setText(String.valueOf(item.getLongitude()));
        holder.textText.setText(String.valueOf(item.getAltitude()));

        holder.mView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (null != mListener) {
                    mListener.onListClicked(item);
                }
            }
        });
    }

    @Override
    public int getItemCount() {
        return mValues.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        private final View mView;
        @Bind(R.id.item_notes_lat) TextView latitudeText;
        @Bind(R.id.item_notes_lon) TextView longitudeText;
        @Bind(R.id.item_notes_alt) TextView textText;

        public ViewHolder(View view) {
            super(view);
            ButterKnife.bind(this, view);
            mView = view;
        }
    }

}
