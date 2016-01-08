package de.berlin.htw.oisindoor.userapp.model;

import android.os.Parcel;
import android.os.Parcelable;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Max on 26.11.2015.
 */
public class Topic implements Parcelable {
    public static final List<Topic> ITEMS = new ArrayList<>();
    private static final String TAG = Topic.class.getName();

    static {
        ITEMS.add(new Topic("Raumbelegung", "User1", "https://lsf.htw-berlin.de/qisserver/rds?state=wplan&act=Raum&pool=Raum&show=plan&P.subc=plan&raum.rgid=4343"));
        ITEMS.add(new Topic("Prof Website", "User2", "https://people.f4.htw-berlin.de/lehrende/schwotzer/lehrveranstaltungen/mobile-informationssysteme.html"));
    }

    private String title;
    private String author;
    private String targetURL;

    public Topic(String title, String author, String targetURL) {
        this.title = title;
        this.author = author;
        this.targetURL = targetURL;
    }

    protected Topic(Parcel in) {
        title = in.readString();
        author = in.readString();
        targetURL = in.readString();
    }

    public String getTitle() {
        return title;
    }

    public String getAuthor() {
        return author;
    }

    public String getTargetURL() {
        return targetURL;
    }

    /**
     * @link {Parcelable}
     */

    public static final Creator<Topic> CREATOR = new Creator<Topic>() {
        @Override
        public Topic createFromParcel(Parcel in) {
            return new Topic(in);
        }

        @Override
        public Topic[] newArray(int size) {
            return new Topic[size];
        }
    };

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(title);
        dest.writeString(author);
        dest.writeString(targetURL);
    }
}
