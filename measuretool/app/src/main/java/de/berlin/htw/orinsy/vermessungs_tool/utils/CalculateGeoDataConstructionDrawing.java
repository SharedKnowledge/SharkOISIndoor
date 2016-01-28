package de.berlin.htw.orinsy.vermessungs_tool.utils;

import android.util.Log;

/**
 * @brief class provides longitude and latitude calculations with help of start and reference coordinates
 *
 * @author Maik M.
 */

public class CalculateGeoDataConstructionDrawing  {

    private double latitudeStart;
    private double longitudeStart;
    private double latitudeReference;
    private double longitudeReference;
    private double rotationAngle;
    private double triangleSideA;
    private double triangleSideB;
    private double triangleSideC;
    private double yAxis;
    private double xAxis;


    /**
     *  @brief constructor
     *
     * @param latitudeStart
     * @param longitudeStart
     * @param latitudeReference
     * @param longitudeReference
     * @param yAxis
     * @param xAxis
     */

    public CalculateGeoDataConstructionDrawing(double latitudeStart, double longitudeStart, double latitudeReference, double longitudeReference, double yAxis, double xAxis){

        this.latitudeStart = latitudeStart;
        this.longitudeStart = longitudeStart;
        this.latitudeReference = latitudeReference;
        this.longitudeReference = longitudeReference;

        this.yAxis = yAxis;
        this.xAxis = xAxis;

        this.triangleSideA = 0;
        this.triangleSideB = 0;
        this.triangleSideC = 0;
        this.rotationAngle = 0;
    }

    /**
     * @brief method calculates distance between start and reference coordinate
     *
     * @return triangleSideC
     */

    public double calculateDistance(){

        double minuteDecimal = 0.016666666;

        double latitudeInMeter = minuteDecimal / 1850;

        Log.d("MYLOG", "LatInMe: " + latitudeInMeter);


        double x = this.latitudeStart - this.latitudeReference;

        this.triangleSideA  = Math.abs(x) / latitudeInMeter ;

        Log.d("MYLOG", "Seite A: " + this.triangleSideA);

        double longitudeInMeter = latitudeInMeter / Math.cos(this.latitudeStart * (Math.PI / 180));

        double y = this.longitudeStart - this.longitudeReference;

        this.triangleSideB =  Math.abs(y) / longitudeInMeter ;

        Log.d("MYLOG", "Seite B: " + this.triangleSideB);

        this.triangleSideC = Math.sqrt(Math.pow(this.triangleSideA, 2) + Math.pow(this.triangleSideB, 2));

        Log.d("MYLOG", "Seite C: " + this.triangleSideC);

        return this.triangleSideC;
    }

    /**
     * @brief method calculates rotation angle
     *
     * @return rotationAngle
     */

    public double calculateRotationAngle() {

        double x = (Math.pow(this.triangleSideB, 2) + Math.pow(this.triangleSideC, 2) - Math.pow(this.triangleSideA, 2)) / (2 * triangleSideB * triangleSideC) ;

        Log.d("MYLOG", "X: " + x);

        double z = Math.acos(x);

        this.rotationAngle = (z / (Math.PI / 180)) * (-1);

        Log.d("MYLOG", "Winkel: " + this.rotationAngle);

        return this.rotationAngle;
    }

    /**
     * @brief method calculated new Latitude
     *
     * @return new latitude
     */

    public double calculateNewLatitude() {

        double changeInDecimalDegree = (this.yAxis * Math.cos(this.rotationAngle * (Math.PI / 180))) - (xAxis * Math.sin(this.rotationAngle * (Math.PI / 180))) ;

       // Log.d("MYLOG", "" + (this.latitudeStart + (changeInDecimalDegree * (0.016666666 / 1850))));

        return this.latitudeStart + (changeInDecimalDegree * (0.016666666 / 1850));
    }

    /**
     * @brief method calculates new Longitude
     *
     * @return new longitude
     */


    public double calculateNewLongitude() {

        double changeInDecimalDegree = (this.yAxis * Math.sin(this.rotationAngle * (Math.PI / 180))) + (this.xAxis * Math.cos(this.rotationAngle * (Math.PI / 180)));

        double longitudeInMeter = (0.016666666 / 1850) / Math.cos(this.latitudeStart * (Math.PI / 180));

        return this.longitudeStart + (changeInDecimalDegree * longitudeInMeter);

    }

}
