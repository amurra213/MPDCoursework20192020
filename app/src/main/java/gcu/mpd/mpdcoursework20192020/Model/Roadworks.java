// Andrew Murray S1628827
package gcu.mpd.mpdcoursework20192020.Model;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

public class Roadworks {
    String title;
    String description;
    Date startDate;
    Date endDate;
    String link;
    Float lat;
    Float longitude;


    public Roadworks() { }


    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) throws ParseException {
        String formattedDescription = "";
        String[] splitDescription = description.split("<br />");
        String[] startDateSplit = splitDescription[0].split(", ");
        String startDateStr = startDateSplit[1];
        Date startDate = new SimpleDateFormat("dd MMM yyyy").parse(startDateStr);
        setStartDate(startDate);

        String[] endDateSplit = splitDescription[1].split(", ");
        String endDateStr = endDateSplit[1];
        Date endDate = new SimpleDateFormat("dd MMM yyyy").parse(endDateStr);
        setEndDate(endDate);

        if(splitDescription.length == 3) {
            String[] descriptionTailSplit = splitDescription[2].split(":");
            formattedDescription = descriptionTailSplit[1];
        } else {
            formattedDescription = "No details provided";
        }
        this.description = formattedDescription;

    }

    public Date getStartDate() {
        return startDate;
    }

    public void setStartDate(Date startDate) {
        this.startDate = startDate;
    }

    public Date getEndDate() {
        return endDate;
    }

    public void setEndDate(Date endDate) {
        this.endDate = endDate;
    }

    public String getLink() {
        return link;
    }

    public void setLink(String link) {
        this.link = link;
    }

    public Float getLat() {
        return lat;
    }

    public void setLat(Float lat) {
        this.lat = lat;
    }

    public Float getLongitude() {
        return longitude;
    }

    public void setLongitude(Float longitude) {
        this.longitude = longitude;
    }

}
