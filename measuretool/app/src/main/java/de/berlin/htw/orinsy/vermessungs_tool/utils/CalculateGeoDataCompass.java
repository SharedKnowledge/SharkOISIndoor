package de.berlin.htw.orinsy.vermessungs_tool.utils;

/**
 * @brief class provides longitude and latitude calculations
 *
 * @author Maik M
 */
public class CalculateGeoDataCompass {
    
    private double latitude;
    private double longitude;
    private double distance;
    private double angle;

    /**
     *  @brief constructor
     *
     * @param latitude
     * @param longitude
     * @param distance
     * @param angle
     */


    public CalculateGeoDataCompass(double latitude, double longitude, double distance, double angle) {

        this.latitude = latitude;
        this.longitude = longitude;
        this.distance = distance;
        this.angle = angle;
    }

    /**
     * @brief method calculates new latitude
     *
     * @return new latitude
     */


    public double newLatitude(){

        double changeLatitudeDirectionInMeter = (this.distance / Math.sin(90 * Math.PI / 180)) * Math.sin((450 - this.angle) * Math.PI / 180);

        double minuteDecimal = 0.016666666;

        double latitudeInMeter = minuteDecimal / 1850;

        double changeInDecimalGrade = changeLatitudeDirectionInMeter * latitudeInMeter;

        return this.latitude = this.latitude + changeInDecimalGrade;

    }

    /**
     * @brief method calculates new longitude
     *
     * @return new longitude
     */


    public double newLongitude(){

        double changeLongitudeDirectionInMeter = (this.distance / Math.sin(90 * Math.PI / 180)) * Math.sin(this.angle * Math.PI / 180);

        double minuteDecimal = 0.016666666;

        double longitudeInMeter = (minuteDecimal / 1850) / Math.cos(this.latitude * Math.PI / 180);

        double changeInDecimalGrade = longitudeInMeter * changeLongitudeDirectionInMeter;

        return this.longitude = this.longitude + changeInDecimalGrade;
    }

}


