package soilsmart.soilsmartapp.views;

import android.content.Intent;
import android.os.Parcelable;
import android.support.v7.app.ActionBar;
import android.os.Bundle;
import android.util.Log;
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

import java.io.Serializable;
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
    public Map<String,SoilSmartNode> nodes;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_node_locations);

        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);
        userLocalStore = new UserLocalStore(this);
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;
        List<SoilSmartNode> tempNodes;
        mMap.setMapType(GoogleMap.MAP_TYPE_SATELLITE);

        final LatLngBounds.Builder bounds = new LatLngBounds.Builder();
        String id;
        tempNodes = GetRandomNodes();
        nodes = new HashMap<>();

        for (SoilSmartNode node : tempNodes) {
            final LatLng coords = new LatLng(node.getLat(), node.getLon());
            bounds.include(coords);
            id = mMap.addMarker(new MarkerOptions().position(coords).icon(BitmapDescriptorFactory.fromResource(R.mipmap.soilsmart_icon))).getId();
            nodes.put(id, node);
        }

        int width = getResources().getDisplayMetrics().widthPixels;
        int height = getResources().getDisplayMetrics().heightPixels;
        int padding = (int) (width * 0.12);
        mMap.moveCamera(CameraUpdateFactory.newLatLngBounds(bounds.build(), width, height, padding));

        // Setup ClickListener to start new activity for individual sensor module data page
        mMap.setOnInfoWindowClickListener(new GoogleMap.OnInfoWindowClickListener() {
            @Override
            public void onInfoWindowClick(Marker marker) {
                Intent newIntent = new Intent(getApplicationContext(), NodeDetailActivity.class);
                //need to send over the node that we are going to display information for
                SoilSmartNode node = nodes.get(marker.getId());
                newIntent.putExtra("node", node);
                startActivity(newIntent);
            }
        });

        // Implement custom InfoWindowAdapter for popup after marker click
        mMap.setInfoWindowAdapter(new GoogleMap.InfoWindowAdapter() {
            @Override
            public View getInfoWindow(Marker marker) {

                View v = getLayoutInflater().inflate(R.layout.node_location_info_window, null);
                SoilSmartNode node = nodes.get(marker.getId());

                TextView tvID = (TextView) v.findViewById(R.id.id_window);
                TextView tvLat = (TextView) v.findViewById(R.id.latitude_window);
                TextView tvLon = (TextView) v.findViewById(R.id.longitude_window);

                TextView tvLvl1 = (TextView) v.findViewById(R.id.level_1_window);
                TextView tvLvl2 = (TextView) v.findViewById(R.id.level_2_window);
                TextView tvLvl3 = (TextView) v.findViewById(R.id.level_3_window);

                tvID.setText(node.getId());
                tvLat.setText(String.format("%.6f", node.getLat()));
                tvLon.setText(String.format("%.6f", node.getLon()));

                tvLvl1.setText(String.format("%.2f", node.getValuesLvl1Avg() * 100) + "%");
                tvLvl2.setText(String.format("%.2f", node.getValuesLvl2Avg() * 100) + "%");
                tvLvl3.setText(String.format("%.2f", node.getValuesLvl3Avg() * 100) + "%");
                return v;
            }

            @Override
            public View getInfoContents(Marker marker) {
                return null;
            }
        });
    }

    static public List<SoilSmartNode> GetRandomNodes() {
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
