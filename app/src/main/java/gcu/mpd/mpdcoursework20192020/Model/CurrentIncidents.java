// Andrew Murray S1628827
package gcu.mpd.mpdcoursework20192020.Model;

public class CurrentIncidents {
    String title;
    String description;
    String link;
    Float lat;
    Float longitude;

    public CurrentIncidents() { }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
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

    public void setLat(Float lat) { this.lat = lat; }

    public Float getLongitude() {
        return longitude;
    }

    public void setLongitude(Float longitude) {
        this.longitude = longitude;
    }

}
