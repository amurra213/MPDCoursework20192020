// Andrew Murray S1628827
package gcu.mpd.mpdcoursework20192020;

import androidx.fragment.app.FragmentActivity;
import android.app.DatePickerDialog;
import android.graphics.Color;
import android.graphics.Typeface;
import android.os.Bundle;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;
import com.google.android.gms.common.api.Status;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.LatLngBounds;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.libraries.places.api.Places;
import com.google.android.libraries.places.api.model.Place;
import com.google.android.libraries.places.api.net.PlacesClient;
import com.google.android.libraries.places.widget.AutocompleteSupportFragment;
import com.google.android.libraries.places.widget.listener.PlaceSelectionListener;
import org.xmlpull.v1.XmlPullParserException;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.URL;
import java.net.URLConnection;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Date;
import gcu.mpd.mpdcoursework20192020.Helpers.PullParserHelper;
import gcu.mpd.mpdcoursework20192020.Model.CurrentIncidents;
import gcu.mpd.mpdcoursework20192020.Model.Roadworks;

public class MapsActivity extends FragmentActivity implements OnMapReadyCallback {

    private GoogleMap mMap;
    private TextView rawDataDisplay;
    private String result = "";
    Date selectedDate;
    private Button currentIncidentsButton;
    private Button plannedRoadworksButton;
    private Button roadworksButton;
    // Traffic Scotland URLs
    private String roadworksEndpoint = "https://trafficscotland.org/rss/feeds/roadworks.aspx";
    private String plannedRoadworksEndpoint = "https://trafficscotland.org/rss/feeds/plannedroadworks.aspx";
    private String currentIncidentsEndpoint = "https://trafficscotland.org/rss/feeds/currentincidents.aspx";

    private String response = "";
    public MapsActivity() {
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_maps);
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);
        Places.initialize(getApplicationContext(), "AIzaSyCxS1G8V-Sop54lo0Sez2fJnyBiq4Hw7Z4");
        PlacesClient placesClient = Places.createClient(this);
        currentIncidentsButton = findViewById(R.id.button);
        plannedRoadworksButton = findViewById(R.id.button2);
        roadworksButton = findViewById(R.id.button3);
        searchForPlace();
        currentIncidentsButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                callIncidentsEndpoint();
            }
        });
        plannedRoadworksButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                new DatePickerDialog(MapsActivity.this, date, myCalendar
                        .get(Calendar.YEAR), myCalendar.get(Calendar.MONTH),
                        myCalendar.get(Calendar.DAY_OF_MONTH)).show();

            }
        });
        roadworksButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                callRoadworksEndpoint();
            }
        });
    }

    final Calendar myCalendar = Calendar.getInstance();
    DatePickerDialog.OnDateSetListener date = new DatePickerDialog.OnDateSetListener() {

        @Override
        public void onDateSet(DatePicker view, int year, int monthOfYear,
                              int dayOfMonth) {
            // TODO Auto-generated method stub
            myCalendar.set(Calendar.YEAR, year);
            myCalendar.set(Calendar.MONTH, monthOfYear);
            myCalendar.set(Calendar.DAY_OF_MONTH, dayOfMonth);
            SimpleDateFormat sdf = new SimpleDateFormat("E dd MMM y");
            try {
                selectedDate = new SimpleDateFormat("E dd MMM y").parse(sdf.format(myCalendar.getTime()));
                callPlannedRoadworksEndpoint();
            } catch (ParseException e) {
                e.printStackTrace();
            }
        }

    };


    public void searchForPlace(){
        AutocompleteSupportFragment autocompleteFragment = (AutocompleteSupportFragment)
                getSupportFragmentManager().findFragmentById(R.id.autocomplete_fragment);
        autocompleteFragment.setPlaceFields(Arrays.asList(Place.Field.ID, Place.Field.NAME, Place.Field.LAT_LNG, Place.Field.ADDRESS));
        autocompleteFragment.setOnPlaceSelectedListener(new PlaceSelectionListener() {

            public void onPlaceSelected(Place place) {
                LatLng latlng = place.getLatLng();
                mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(latlng, 15));
            }

            public void onError(Status status) {
                System.out.println(status);
            }
        });
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;

        // Add a marker in Sydney and move the camera
        LatLngBounds SCOTLAND = new LatLngBounds(
                new LatLng(54.176980, -7.264573),
                new LatLng(59.696921, -1.419846)
        );
        mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(SCOTLAND.getCenter(), 6));
        mMap.setLatLngBoundsForCameraTarget(SCOTLAND);
        mMap.setInfoWindowAdapter(new GoogleMap.InfoWindowAdapter() {

            @Override
            public View getInfoWindow(Marker arg0) {
                return null;
            }

            @Override
            public View getInfoContents(Marker marker) {
                LinearLayout info = new LinearLayout(MapsActivity.this);
                info.setOrientation(LinearLayout.VERTICAL);

                TextView title = new TextView(MapsActivity.this);
                title.setTextColor(Color.BLACK);
                title.setGravity(Gravity.CENTER);
                title.setTypeface(null, Typeface.BOLD);
                title.setText(marker.getTitle());

                TextView snippet = new TextView(MapsActivity.this);
                snippet.setTextColor(Color.GRAY);
                snippet.setText(marker.getSnippet());
                snippet.setGravity(Gravity.CENTER);
                info.addView(title);
                info.addView(snippet);

                return info;
            }
        });
    }

    public void callIncidentsEndpoint() {
        startProgress(currentIncidentsEndpoint, "currentIncidents");
    }

    public void callRoadworksEndpoint() {
        startProgress(roadworksEndpoint, "roadworks");
    }

    public void callPlannedRoadworksEndpoint() {
        startProgress(plannedRoadworksEndpoint, "plannedRoadworks");
    }

    public void startProgress(String urlSource, String endpointName) {
        // Run network access on a separate thread;
        new Thread(new Task(urlSource, endpointName)).start();
    } //

    public void populateMapWithCurrentIncidentMarkers(ArrayList<CurrentIncidents> list) {
        if(!list.isEmpty()) {
            mMap.clear();
            for(int i=0; i<list.size(); i++) {
                mMap.addMarker(new MarkerOptions()
                        .position(new LatLng(list.get(i).getLat(), list.get(i).getLongitude()))
                        .title(list.get(i).getTitle())
                        .snippet(list.get(i).getDescription() + "\n  \n " +
                                "Link - " + list.get(i).getLink())
                );
            }
        } else {
            Toast.makeText(MapsActivity.this, "There are currently no incidents to show", Toast.LENGTH_SHORT).show();
        }
    }

    public void populateMapWithPlannedRoadworkMarkers(ArrayList<Roadworks> list) {

        if(list.size() > 0 ) {
            mMap.clear();
            for(int i=0; i<list.size(); i++) {
                if(selectedDate != null && selectedDate.after(list.get(i).getStartDate()) && selectedDate.before(list.get(i).getEndDate())) {
                    SimpleDateFormat DATE_FORMAT = new SimpleDateFormat("E dd MMM y");
                    mMap.addMarker(new MarkerOptions()
                            .position(new LatLng(list.get(i).getLat(), list.get(i).getLongitude()))
                            .title(list.get(i).getTitle())
                            .snippet(list.get(i).getDescription() + "\n" +
                                    "From -  " + DATE_FORMAT.format(list.get(i).getStartDate()) + "\n" +
                                    "until - " + DATE_FORMAT.format(list.get(i).getEndDate()) + "\n \n" +
                                    "Link - " + list.get(i).getLink()
                            )
                    );
                }
            }
        }

    }

    public void populateMapWithRoadworkMarkers(ArrayList<Roadworks> list) {
        if(list.size() > 0 ) {
            mMap.clear();
            for(int i=0; i<list.size(); i++) {
                SimpleDateFormat DATE_FORMAT = new SimpleDateFormat("E dd MMM y");
                mMap.addMarker(new MarkerOptions()
                        .position(new LatLng(list.get(i).getLat(), list.get(i).getLongitude()))
                        .title(list.get(i).getTitle())
                        .snippet(list.get(i).getDescription() + "\n" +
                                "From -  " + DATE_FORMAT.format(list.get(i).getStartDate()) + "\n" +
                                "until - " + DATE_FORMAT.format(list.get(i).getEndDate()) + "\n \n" +
                                "Link - " + list.get(i).getLink()
                        )
                );
            }
        }
    }

    // Need separate thread to access the internet resource over network
    // Other neater solutions should be adopted in later iterations.
    private class Task implements Runnable {
        private String url;
        private String endpointName;

        public Task(String aurl, String endpointName) {
            url = aurl;
            this.endpointName = endpointName;
        }

        @Override
        public void run() {

            URL aurl;
            URLConnection yc;
            BufferedReader in = null;
            String inputLine = "";

            try {
                aurl = new URL(url);
                yc = aurl.openConnection();
                in = new BufferedReader(new InputStreamReader(yc.getInputStream()));
                while ((inputLine = in.readLine()) != null) {
                    result = result + inputLine;
                }
                in.close();
            } catch (IOException ae) {
                Log.e("MyTag", "ioexception");
            }


            MapsActivity.this.runOnUiThread(new Runnable() {

                public void run() {
                    if (endpointName.equals("currentIncidents")) {
                        ArrayList<CurrentIncidents> incidentList =  PullParserHelper.parseIncidents(result);
                        populateMapWithCurrentIncidentMarkers(incidentList);
                    } else if (endpointName.equals("plannedRoadworks")) {
                        PullParserHelper p = new PullParserHelper();
                        ArrayList<Roadworks> plannedRoadworksList = PullParserHelper.parseRoadworks(result);
                        populateMapWithPlannedRoadworkMarkers(plannedRoadworksList);
                    } else if (endpointName.equals("roadworks")) {
                        ArrayList<Roadworks> roadworksList = PullParserHelper.parseRoadworks(result);
                        populateMapWithRoadworkMarkers(roadworksList);
                    }

                }
            });
        }
    }
}
