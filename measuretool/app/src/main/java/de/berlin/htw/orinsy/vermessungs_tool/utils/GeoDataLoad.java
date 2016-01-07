package de.berlin.htw.orinsy.vermessungs_tool.utils;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by ziera on 10.12.2015.
 */
public class GeoDataLoad {

    public static List<GeoData> loadGeoData (File file) throws IOException{

        FileInputStream inStream = new FileInputStream(file);
        BufferedReader reader = new BufferedReader(new InputStreamReader(inStream));
        List <GeoData> tmpList = new ArrayList<>();
        GeoData datatmp;

        try{

            String info;
            double height;
            double latitude;
            double longitude;

            while( (info = reader.readLine()) != null){

                height = Double.parseDouble(reader.readLine());
                latitude = Double.parseDouble(reader.readLine());
                longitude = Double.parseDouble(reader.readLine());

                datatmp = new GeoData(info, height, latitude, longitude);
                tmpList.add(datatmp);
            }
        } finally {
            reader.close();
        }

        return tmpList;
    }
}
