package de.berlin.htw.orinsy.vermessungs_tool.utils;

import org.simpleframework.xml.ElementList;
import org.simpleframework.xml.Root;

import java.util.List;

/**
 * Die Klasse Measurement ist nur fuer den Aufbau der XML-Datei zustaendig.
 * Es wird eine Liste von GeoData uebergeben und diese wird dann also
 * XML-Tree uebernommen.
 */

@Root
public class Measurement {
    @ElementList
    private List<GeoData> geoData;

    public void setGeoData(List<GeoData> geoData) {
        this.geoData = geoData;
    }

    public List getMeasurements(){
        return geoData;
    }
}
