package de.berlin.htw.oisindorr.userapp.adapter;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.util.List;

import de.berlin.htw.oisindorr.userapp.R;
import de.berlin.htw.oisindorr.userapp.fragments.DetailFragment;
import de.berlin.htw.oisindorr.userapp.model.Topic;

/**
 * Created by Max on 26.11.2015.
 */
public class BeaconTopicsRecyclerViewAdapter extends RecyclerView.Adapter<BeaconTopicsRecyclerViewAdapter.ViewHolder> {
    private final List<Topic> data;
    private final DetailFragment.TopicListener callback;

    public BeaconTopicsRecyclerViewAdapter(List<Topic> data, DetailFragment.TopicListener callback) {
        this.data = data;
        this.callback = callback;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_topic, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        Topic t = data.get(position);
        holder.topicText.setText(t.getTitle());
    }

    @Override
    public int getItemCount() {
        return data.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        public final View view;
        public final TextView topicText;

        public ViewHolder(View itemView) {
            super(itemView);
            view = itemView;
            view.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    callback.onTopicClicked(data.get(getAdapterPosition()));
                }
            });
            topicText = (TextView) view.findViewById(R.id.item_topic_title);
        }
    }
}
