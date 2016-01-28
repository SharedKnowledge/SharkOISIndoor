package de.berlin.htw.oisindoor.userapp.adapter;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

import butterknife.Bind;
import butterknife.ButterKnife;
import de.berlin.htw.oisindoor.userapp.R;
import de.berlin.htw.oisindoor.userapp.fragments.PositioningFragment;
import de.berlin.htw.oisindoor.userapp.model.Topic;

/**
 * RecyclerView.Adapter which handles the List of Topics shown in the {@link de.berlin.htw.oisindoor.userapp.TabbedActivity}
 *
 * @author Max M
 */
public class BeaconTopicsRecyclerViewAdapter extends RecyclerView.Adapter<BeaconTopicsRecyclerViewAdapter.ViewHolder> {
    private final List<Topic> data;
    private final PositioningFragment.TopicListener callback;

    public BeaconTopicsRecyclerViewAdapter(PositioningFragment.TopicListener callback) {
        this(new ArrayList<Topic>(), callback);
    }

    public BeaconTopicsRecyclerViewAdapter(List<Topic> data, PositioningFragment.TopicListener callback) {
        this.data = data;
        this.callback = callback;
    }

    public void updateItems(List<Topic> topicList){
        this.data.clear();
        this.data.addAll(topicList);
        notifyDataSetChanged();
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
        holder.authorText.setText(t.getAuthor());
        holder.targetText.setText(t.getTargetURL());
    }

    @Override
    public int getItemCount() {
        return data.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        @Bind(R.id.item_topic_title) public TextView topicText;
        @Bind(R.id.item_topic_author) public TextView authorText;
        @Bind(R.id.item_topic_target) public TextView targetText;

        public ViewHolder(View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    callback.onTopicClicked(data.get(getAdapterPosition()));
                }
            });
        }
    }
}
