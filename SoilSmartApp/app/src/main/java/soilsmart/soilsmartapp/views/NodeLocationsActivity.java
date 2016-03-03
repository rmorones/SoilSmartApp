package soilsmart.soilsmartapp.views;

import android.content.Intent;
import android.support.v7.app.ActionBar;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.LatLngBounds;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;

import soilsmart.soilsmartapp.R;
import soilsmart.soilsmartapp.SoilSmartNode;
import soilsmart.soilsmartapp.UserLocalStore;

public class NodeLocationsActivity extends BaseMenuActivity implements OnMapReadyCallback {

    private GoogleMap mMap;
    private UserLocalStore userLocalStore;
    private Map<String,SoilSmartNode> nodes;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_node_locations);
        final ActionBar bar = getSupportActionBar();
        if (bar != null) {
            bar.setDisplayShowHomeEnabled(true);
            bar.setIcon(R.mipmap.soilsmart_icon);
            bar.show();
        }

        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);
        userLocalStore = new UserLocalStore(this);
    }

    /**
     * Manipulates the map once available.
     * This callback is triggered when the map is ready to be used.
     * This is where we can add markers or lines, add listeners or move the camera. In this case,
     * we just add a marker near Sydney, Australia.
     * If Google Play services is not installed on the device, the user will be prompted to install
     * it inside the SupportMapFragment. This method will only be triggered once the user has
     * installed Google Play services and returned to the app.
     */
    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;
        List<SoilSmartNode> tempNodes;
        mMap.setMapType(GoogleMap.MAP_TYPE_SATELLITE);

        LatLngBounds.Builder bounds = new LatLngBounds.Builder();
        String id;
        tempNodes = GetRandomNodes();
        nodes = new HashMap<>();

        for (SoilSmartNode node : tempNodes) {
            LatLng coords = new LatLng(node.getLat(), node.getLon());
            bounds.include(coords);
            id = mMap.addMarker(new MarkerOptions().position(coords).icon(BitmapDescriptorFactory.fromResource(R.mipmap.soilsmart_icon))).getId();
            nodes.put(id, node);
        }

        mMap.moveCamera(CameraUpdateFactory.newLatLngBounds(bounds.build(),100));

        // Setup ClickListener to start new activity for individual sensor module data page
        mMap.setOnInfoWindowClickListener(new GoogleMap.OnInfoWindowClickListener() {
            @Override
            public void onInfoWindowClick(Marker marker) {
                Intent newIntent = new Intent(getApplicationContext(), NodeDetailActivity.class);
                startActivity(newIntent);
            }
        });

        // Implement custom InfoWindowAdapter for popup after marker click
        mMap.setInfoWindowAdapter(new GoogleMap.InfoWindowAdapter() {
            @Override
            public View getInfoWindow(Marker marker) {
                return null;
            }

            @Override
            public View getInfoContents(Marker marker) {
                View v = getLayoutInflater().inflate(R.layout.node_location_info_window, null);
                SoilSmartNode node = nodes.get(marker.getId());

                TextView tvID = (TextView) v.findViewById(R.id.tv_ID);
                TextView tvLat = (TextView) v.findViewById(R.id.tv_lat);
                TextView tvLon = (TextView) v.findViewById(R.id.tv_lon);

                TextView tvLvl1 = (TextView) v.findViewById(R.id.tv_lvl1);
                TextView tvLvl2 = (TextView) v.findViewById(R.id.tv_lvl2);
                TextView tvLvl3 = (TextView) v.findViewById(R.id.tv_lvl3);

                tvID.setText("ID: " + node.getId());
                tvLat.setText("Latitude: " + String.format("%.6f",node.getLat()));
                tvLon.setText("Longitude: " + String.format("%.6f",node.getLon()));

                tvLvl1.setText("Lvl1 Avg: " + String.format("%.2f",node.getValuesLvl1Avg()*100)+"%");
                tvLvl2.setText("Lvl2 Avg: " + String.format("%.2f",node.getValuesLvl2Avg()*100)+"%");
                tvLvl3.setText("Lvl3 Avg: " + String.format("%.2f",node.getValuesLvl3Avg()*100)+"%");
                return v;
            }
        });
    }

    public List<SoilSmartNode> GetRandomNodes() {
        // Lat/lon max/min for the Work family's grove
        double latMax = 33.349969;
        double latMin = 33.349292;
        double lonMax = -117.180180;
        double lonMin = -117.178305;

        double[] val1 = {0.1, 0.15, 0.125};
        double[] val2 = {0.2, 0.25, 0.225};
        double[] val3 = {0.3, 0.35, 0.325};

        List<SoilSmartNode> nodes = new ArrayList<>();
        int i;
        double lat;
        double lon;
        Date date = new Date();

        Random r = new Random();
        for (i = 0; i < 20; ++i) {
            lat = latMin + (latMax - latMin) * r.nextDouble();
            lon = lonMin + (lonMax - lonMin) * r.nextDouble();

            SoilSmartNode node = new SoilSmartNode(Integer.toString(i), Integer.toString((i / 4)), lat,
                    lon, date, val1, val2, val3);
            nodes.add(node);
        }
        return nodes;
    }

    @Override
    protected void onStart() {
        super.onStart();
        if (!userLocalStore.getUserLoggedIn()) {
            launchActivity(LoginActivity.class);
        }
    }
}
