package de.berlin.htw.orinsy.vermessungs_tool;

import org.simpleframework.xml.ElementList;
import org.simpleframework.xml.Root;

import java.util.List;

/**
 * Created by ziera on 07.12.2015.
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
