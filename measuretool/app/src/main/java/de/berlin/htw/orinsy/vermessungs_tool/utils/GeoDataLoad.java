package de.berlin.htw.orinsy.vermessungs_tool.utils;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;

/**
 * Die Klasse GeoDataLoad laedt die Daten aus der erstellten Datei der Klasse GeoDataSave.java.
 * Die Datei wird Zeile fuer Zeile ausgelesen. Da in jeder Zeile eine bestimmte Variable steht
 * wird diese auch passend abgelegt und dann mit diesen ein neues <GeoData> Objekt erstellt und
 * einer Liste uebergeben
 *
 * @param File file Gibt den Ort und Namen der zu ladenden Datei an.
 *
 * @return Gibt eine Liste von GeoData zurueck
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
            int floor;

            while( (info = reader.readLine()) != null){

                height = Double.parseDouble(reader.readLine());
                floor = Integer.parseInt(reader.readLine());
                latitude = Double.parseDouble(reader.readLine());
                longitude = Double.parseDouble(reader.readLine());


                datatmp = new GeoData(info, height, latitude, longitude, floor);
                tmpList.add(datatmp);
            }
        } finally {
            reader.close();
        }

        return tmpList;
    }
}
