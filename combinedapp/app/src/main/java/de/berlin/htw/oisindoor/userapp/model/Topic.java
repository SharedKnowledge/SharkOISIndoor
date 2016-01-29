package de.berlin.htw.oisindoor.userapp.model;

import android.os.Parcel;
import android.os.Parcelable;

import java.util.ArrayList;

/**
 * Class, which represents a Topic receives from the Shark-DB <br/>
 * for Testing, it's contain a list of fake topics {@link #ITEMS}
 * @author Max M
 */
public class Topic implements Parcelable {
    public static final ArrayList<Topic> ITEMS = new ArrayList<>();
    private static final String TAG = Topic.class.getName();

    static {
        ITEMS.add(new Topic("Raumbelegung", "User1", "https://lsf.htw-berlin.de/qisserver/rds?state=wplan&act=Raum&pool=Raum&show=plan&P.subc=plan&raum.rgid=4343"));
        ITEMS.add(new Topic("Raumbelegung", "User1", "https://lsf.htw-berlin.de/qisserver/rds?state=wplan&act=Raum&pool=Raum&show=plan&P.subc=plan&raum.rgid=4344"));
        ITEMS.add(new Topic("Raumbelegung", "User1", "https://lsf.htw-berlin.de/qisserver/rds?state=wplan&act=Raum&pool=Raum&show=plan&P.subc=plan&raum.rgid=4345"));
        ITEMS.add(new Topic("Prof Schwotzer", "User2", "https://people.f4.htw-berlin.de/lehrende/schwotzer/"));
        ITEMS.add(new Topic("Prof Gärtner", "User2", "http://people.f4.htw-berlin.de/lehrende/gaertner/"));
        ITEMS.add(new Topic("Prof Hrta", "User2", "http://people.f4.htw-berlin.de/lehrende/herta/"));
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

    @Override
    public String toString() {
        return title;
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
