// Andrew Murray S1628827
package gcu.mpd.mpdcoursework20192020.Helpers;

import java.io.StringReader;
import java.util.ArrayList;
import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserFactory;
import gcu.mpd.mpdcoursework20192020.Model.CurrentIncidents;
import gcu.mpd.mpdcoursework20192020.Model.Roadworks;

public class PullParserHelper {

    public static ArrayList<CurrentIncidents> parseIncidents(String xml){
        CurrentIncidents ci = null;
        ArrayList<CurrentIncidents> incidentList = new ArrayList<>();
        try{
            XmlPullParserFactory factory = XmlPullParserFactory.newInstance();
            factory.setNamespaceAware(true);
            XmlPullParser xpp = factory.newPullParser();
            xpp.setInput( new StringReader( xml ) );
            int eventType = xpp.getEventType();

            while (eventType != XmlPullParser.END_DOCUMENT) {

                String name = null;

                switch (eventType) {
                    case XmlPullParser.START_TAG:
                        name = xpp.getName();
                        if (name.equalsIgnoreCase("Item")) {
                            ci = new CurrentIncidents();
                        } else if (ci != null) {
                            if (name.equalsIgnoreCase("Title")) {
                                ci.setTitle(xpp.nextText());
                            } else if (name.equalsIgnoreCase("Description")) {
                                ci.setDescription(xpp.nextText());
                            } else if (name.equalsIgnoreCase("Link")) {
                                ci.setLink(xpp.nextText());
                            } else if (name.equalsIgnoreCase("point")) {
                                String[] latlong = xpp.nextText().split(" ");
                                Float lat = Float.parseFloat(latlong[0]);
                                Float longitude = Float.parseFloat(latlong[1]);
                                ci.setLat(lat);
                                ci.setLongitude(longitude);
                            }
                        }
                        break;
                    case XmlPullParser.END_TAG:
                        name = xpp.getName();
                        if (name.equalsIgnoreCase("Item") && ci != null) {
                            incidentList.add(ci);
                        }
                        break;
                }
                eventType = xpp.next();
            }

        } catch(Exception e){
            e.printStackTrace();
        }
        return incidentList;
    }

    public static ArrayList<Roadworks> parseRoadworks(String xml){
        ArrayList<Roadworks> roadworksList = new ArrayList<>();
        try{
            boolean parsingComplete = false;
            Roadworks roadwork = null;
            XmlPullParser xmlParser = XmlPullParserFactory.newInstance().newPullParser();
            xmlParser.setInput(new StringReader(xml));
            int eventType = xmlParser.getEventType();
            while (eventType != XmlPullParser.END_DOCUMENT && !parsingComplete) {
                String name = null;
                switch (eventType) {
                    case XmlPullParser.START_DOCUMENT:
                        break;
                    case XmlPullParser.START_TAG:
                        name = xmlParser.getName();
                        if (name.equalsIgnoreCase("Item")) {
                            roadwork = new Roadworks();
                        } else if (roadwork != null) {
                            if (name.equalsIgnoreCase("Title")) {
                                roadwork.setTitle(xmlParser.nextText());
                            } else if (name.equalsIgnoreCase("Description")) {
                                roadwork.setDescription(xmlParser.nextText());
                            } else if (name.equalsIgnoreCase("Link")) {
                                roadwork.setLink(xmlParser.nextText());
                            } else if (name.equalsIgnoreCase("Georss:point")) {
                                String[] latlong = xmlParser.nextText().split(" ");
                                Float lat = Float.parseFloat(latlong[0]);
                                Float longitude = Float.parseFloat(latlong[1]);
                                roadwork.setLat(lat);
                                roadwork.setLongitude(longitude);
                            }
                        }
                        break;
                    case XmlPullParser.END_TAG:
                        name = xmlParser.getName();
                        if (name.equalsIgnoreCase("Item") && roadwork != null) {
                            roadworksList.add(roadwork);
                        } else if (name.equalsIgnoreCase("Channel")) {
                            parsingComplete = true;
                        }
                        break;
                }
                eventType = xmlParser.next();
            }
        } catch(Exception e){
            e.printStackTrace();
        }
        return roadworksList;
    }
}
