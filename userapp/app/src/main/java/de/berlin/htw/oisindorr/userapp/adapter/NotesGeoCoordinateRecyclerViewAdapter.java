package de.berlin.htw.oisindorr.userapp.adapter;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.util.List;

import de.berlin.htw.oisindorr.userapp.R;
import de.berlin.htw.oisindorr.userapp.fragments.NoteFragment;
import de.berlin.htw.oisindorr.userapp.model.GeoCoordinate;

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
        holder.mItem = mValues.get(position);
        holder.latitudeText.setText(""+mValues.get(position).getLatitude());
        holder.longitudeText.setText(""+mValues.get(position).getLongitude());
        holder.textText.setText(mValues.get(position).getText());

        holder.mView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (null != mListener) {
                    mListener.onListClicked(holder.mItem);
                }
            }
        });
    }

    @Override
    public int getItemCount() {
        return mValues.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        public final View mView;
        public final TextView latitudeText;
        public final TextView longitudeText;
        public final TextView textText;
        public GeoCoordinate mItem;

        public ViewHolder(View view) {
            super(view);
            mView = view;
            latitudeText = (TextView) view.findViewById(R.id.item_notes_lat);
            longitudeText = (TextView) view.findViewById(R.id.item_notes_lon);
            textText = (TextView) view.findViewById(R.id.item_notes_alt);
        }

        @Override
        public String toString() {
            return super.toString() + " '" + textText.getText() + "'";
        }
    }
}
