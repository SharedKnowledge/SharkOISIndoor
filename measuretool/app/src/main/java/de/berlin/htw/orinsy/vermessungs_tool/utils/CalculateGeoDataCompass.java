package de.berlin.htw.orinsy.vermessungs_tool.utils;

/**
 * Created by Maik on 24/11/15.
 */
public class CalculateGeoDataCompass {
    
    private double latitude;
    private double longitude;
    private double distance;
    private double angle;


    public CalculateGeoDataCompass(double latitude, double longitude, double distance, double angle) {

        this.latitude = latitude;
        this.longitude = longitude;
        this.distance = distance;
        this.angle = angle;
    }

    public double newLatitude(){

        double changeLatitudeDirectionInMeter = (this.distance / Math.sin(90 * Math.PI / 180)) * Math.sin((450 - this.angle) * Math.PI / 180);

        double minuteDecimal = 0.016666666;

        double latitudeInMeter = minuteDecimal / 1850;

        double changeInDecimalGrade = changeLatitudeDirectionInMeter * latitudeInMeter;

        return this.latitude = this.latitude + changeInDecimalGrade;

    }

    public double newLongitude(){

        double changeLongitudeDirectionInMeter = (this.distance / Math.sin(90 * Math.PI / 180)) * Math.sin(this.angle * Math.PI / 180);

        double minuteDecimal = 0.016666666;

        double longitudeInMeter = (minuteDecimal / 1850) / Math.cos(this.latitude * Math.PI / 180);

        double changeInDecimalGrade = longitudeInMeter * changeLongitudeDirectionInMeter;

        return this.longitude = this.longitude + changeInDecimalGrade;
    }

}


