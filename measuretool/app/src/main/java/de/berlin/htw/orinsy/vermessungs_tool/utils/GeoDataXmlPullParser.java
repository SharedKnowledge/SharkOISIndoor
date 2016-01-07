package de.berlin.htw.orinsy.vermessungs_tool.utils;

import android.os.Environment;
import android.util.Log;
import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserFactory;
import java.io.File;
import java.io.FileReader;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by ziera on 08.12.2015.
 */
public class GeoDataXmlPullParser {

    static final String KEY_GEODATA = "geoData";
    static final String KEY_INFO = "info";
    static final String KEY_HEIGHT = "height";
    static final String KEY_LATITUDE = "latitude";
    static final String KEY_LONGITUDE = "longitude";

    public static List<GeoData> getGeoDataFromFile(File file){

        List<GeoData> geoData;
        geoData = new ArrayList<>();

        GeoData currentGeoData = null;

        String currentText = "";

        try{

            XmlPullParserFactory factory = XmlPullParserFactory.newInstance();
            XmlPullParser xpp = factory.newPullParser();

            FileReader fr = new FileReader(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_NOTIFICATIONS) + "geoData.xml");
            xpp.setInput(fr);

            int eventType = xpp.getEventType();

            while(eventType != XmlPullParser.END_DOCUMENT){

                String tagname = xpp.getName();

                switch (eventType){
                    case XmlPullParser.START_TAG:
                        if (tagname.equalsIgnoreCase(KEY_GEODATA)){
                            currentGeoData = new GeoData();
                        }
                        break;
                    case XmlPullParser.TEXT:

                        currentText = xpp.getText();
                        break;
                    case XmlPullParser.END_TAG:
                        if (tagname.equalsIgnoreCase(KEY_GEODATA)){
                            geoData.add(currentGeoData);
                        } else if (tagname.equalsIgnoreCase(KEY_INFO)){
                            currentGeoData.setInfo(currentText);
                        } else if (tagname.equalsIgnoreCase(KEY_HEIGHT)){
                            currentGeoData.setHeight(Double.parseDouble(currentText));
                        } else if (tagname.equalsIgnoreCase(KEY_LATITUDE)){
                            currentGeoData.setLatitude(Double.parseDouble(currentText));
                        } else if (tagname.equalsIgnoreCase(KEY_LONGITUDE)){
                            currentGeoData.setLongitude(Double.parseDouble(currentText));
                        }
                        break;
                    default:
                        break;
                }

                eventType = xpp.next();
            }
        } catch (Exception ex){
            Log.e("XmlPullParser", "Auslesen fehlgeschlagen");
        }
        return geoData;
    }

}
