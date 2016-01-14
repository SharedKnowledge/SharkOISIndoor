package de.berlin.htw.orinsy.vermessungs_tool.utils;

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
        this.floor = 0;
        this.latitude = 0;
        this.longitude = 0;
    }

    public GeoData(String info, double height, double latitude, double longitude, int floor){
        this.info = info;
        this.height = height;
        this.floor = floor;
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

    @Element
    private int floor;


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

    public void setFloor(int floor) {
        this.floor = floor;
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

    public int getFloor() {
        return floor;
    }
}


