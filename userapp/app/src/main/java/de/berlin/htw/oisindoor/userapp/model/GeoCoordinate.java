package de.berlin.htw.oisindoor.userapp.model;

import android.os.Parcel;
import android.os.Parcelable;

import java.util.ArrayList;

/**
 * Created by Max on 23.11.2015.
 */
public class GeoCoordinate implements Parcelable {

    public static final ArrayList<GeoCoordinate> ITEMS = new ArrayList<>();

    static {
        ITEMS.add(new GeoCoordinate(52.123f, 13.2478f, 0.1f));
        ITEMS.add(new GeoCoordinate(12.123f, 16.2478f, 2.1f));
        ITEMS.add(new GeoCoordinate(67.123f, 23.2478f, 1.1f));
        ITEMS.add(new GeoCoordinate(6.123f, 45.2478f, 15.1f));
        ITEMS.add(new GeoCoordinate(52.123f, 13.2478f, 0.1f));
        ITEMS.add(new GeoCoordinate(12.123f, 16.2478f, 2.1f));
        ITEMS.add(new GeoCoordinate(67.123f, 23.2478f, 1.1f));
        ITEMS.add(new GeoCoordinate(6.123f, 45.2478f, 15.1f));
        ITEMS.add(new GeoCoordinate(52.123f, 13.2478f, 0.1f));
        ITEMS.add(new GeoCoordinate(12.123f, 16.2478f, 2.1f));
        ITEMS.add(new GeoCoordinate(67.123f, 23.2478f, 1.1f));
        ITEMS.add(new GeoCoordinate(6.123f, 45.2478f, 15.1f));
        ITEMS.add(new GeoCoordinate(52.123f, 13.2478f, 0.1f));
        ITEMS.add(new GeoCoordinate(12.123f, 16.2478f, 2.1f));
        ITEMS.add(new GeoCoordinate(67.123f, 23.2478f, 1.1f));
        ITEMS.add(new GeoCoordinate(6.123f, 45.2478f, 15.1f));
    }

    private float latitude;
    private float longitude;
    private float altitude;
    private String text;

    public GeoCoordinate(float latitude, float longitude, float altitude) {
        this(latitude, longitude, altitude, null);
    }

    public GeoCoordinate(float latitude, float longitude, float altitude, String text) {
        this.latitude = latitude;
        this.longitude = longitude;
        this.altitude = altitude;
        this.text = text;
    }

    protected GeoCoordinate(Parcel in) {
        latitude = in.readFloat();
        longitude = in.readFloat();
        altitude = in.readFloat();
    }

    public float getLatitude() {
        return latitude;
    }

    public float getLongitude() {
        return longitude;
    }

    public float getAltitude() {
        return altitude;
    }

    public String getText() {
        return text;
    }

    @Override
    public String toString() {
        return "GeoCoordinate{" +
                "latitude=" + latitude +
                ", longitude=" + longitude +
                ", altitude=" + altitude +
                '}';
    }

    /**
     * @link {Parcelable}
     */

    public static final Creator<GeoCoordinate> CREATOR = new Creator<GeoCoordinate>() {
        @Override
        public GeoCoordinate createFromParcel(Parcel in) {
            return new GeoCoordinate(in);
        }

        @Override
        public GeoCoordinate[] newArray(int size) {
            return new GeoCoordinate[size];
        }
    };

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeFloat(latitude);
        dest.writeFloat(longitude);
        dest.writeFloat(altitude);
    }


}
