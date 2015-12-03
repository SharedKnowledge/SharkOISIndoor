package de.berlin.htw.oisindorr.userapp.model;

import android.os.Parcel;
import android.os.Parcelable;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Max on 26.11.2015.
 */
public class BeaconContent implements Parcelable {

    private String postion;
    private String author;
    private ArrayList<Topic> topics;

    public BeaconContent(String postion, String author, ArrayList<Topic> topics) {
        this.postion = postion;
        this.author = author;
        this.topics = topics;
    }

    protected BeaconContent(Parcel in) {
        postion = in.readString();
        author = in.readString();
    }

    public String getPostion() {
        return postion;
    }

    public String getAuthor() {
        return author;
    }

    public List<Topic> getTopics() {
        return topics;
    }

    @Override
    public String toString() {
        return "BeaconContent{" +
                "postion='" + postion + '\'' +
                ", author='" + author + '\'' +
                ", topics=" + topics +
                '}';
    }

    /**
     * @link {Parcelable}
     */

    public static final Creator<BeaconContent> CREATOR = new Creator<BeaconContent>() {
        @Override
        public BeaconContent createFromParcel(Parcel in) {
            return new BeaconContent(in);
        }

        @Override
        public BeaconContent[] newArray(int size) {
            return new BeaconContent[size];
        }
    };

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(postion);
        dest.writeString(author);
    }
}
