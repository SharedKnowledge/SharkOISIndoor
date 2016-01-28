package de.berlin.htw.orinsy.vermessungs_tool.utils;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.util.List;

/**
 * Die Klasse GeoDataSave speichert die uebergebenen Objekte <GeoData> in eine eigene Datei mit
 * der Endung .mgs.
 * Jede Variable wird dabei in jeweils eine Zeile geschrieben.
 *
 * @param List<GeoData> allGeoData ist die uebergeben Liste an Objekte, welche abgespeichert werden
 * @param File dataFile gibt den Ort und den Namen der Datei an, die erstellt wird
 */

public class GeoDataSave {


    public static void saveGeoData(List<GeoData> allGeoData, File dataFile) throws IOException {


        FileOutputStream out = new FileOutputStream(dataFile);
        OutputStreamWriter writer = new OutputStreamWriter(out);
        GeoData datatmp;

        for (int i = 0; i < allGeoData.size(); i++){
            datatmp = allGeoData.get(i);
            writer.write("" + datatmp.getInfo());
            writer.write("\n");
            writer.write("" + datatmp.getHeight());
            writer.write("\n");
            writer.write("" + datatmp.getFloor());
            writer.write("\n");
            writer.write("" + datatmp.getLatitude());
            writer.write("\n");
            writer.write("" + datatmp.getLongitude());
            writer.write("\n");

        }

        writer.close();

       /* FileWriter fw = new FileWriter("geoData.mdg");
        BufferedWriter writer = new BufferedWriter(fw);
        */
    }


}
