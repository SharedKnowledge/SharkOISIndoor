package de.berlin.htw.orinsy.vermessungs_tool;

import org.simpleframework.xml.Element;
import org.simpleframework.xml.Root;

/**
 * Created by ziera on 07.12.2015.
 */

@Root
public class GeoData {

    public GeoData(){
        this.info = null;
        this.height = 0;
        this.latitude = 0;
        this.longitude = 0;
    }

    public GeoData(String info, double height, double latitude, double longitude){
        this.info = info;
        this.height = height;
        this.latitude = latitude;
        this.longitude = longitude;
    }

    @Element
    private String info;

    @Element
    private double latitude;

    @Element
    private double longitude;

    @Element
    private double height;

    public void setInfo(String info) {
        this.info = info;
    }

    public void setLatitude(double latitude) {
        this.latitude = latitude;
    }

    public void setLongitude(double longitude) {
        this.longitude = longitude;
    }

    public void setHeight(double height) {
        this.height = height;
    }

    public String getInfo(){
        return info;
    }

    public double getLatitude() {
        return latitude;
    }

    public double getLongitude() {
        return longitude;
    }

    public double getHeight() {
        return height;
    }
}


