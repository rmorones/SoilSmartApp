package soilsmart.soilsmartapp.views;

import android.content.Context;
import android.content.DialogInterface;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.support.design.widget.Snackbar;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AlertDialog;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.lang.reflect.Array;

import soilsmart.soilsmartapp.R;
import soilsmart.soilsmartapp.SoilSmartNode;
import soilsmart.soilsmartapp.SoilSmartService;
import soilsmart.soilsmartapp.UserLocalStore;

/**
 * Created by jesus on 5/30/16.
 */
public class SolenoidControlActivity extends BaseMenuActivity {

    private UserLocalStore userLocalStore;
    private SoilSmartNode node;
    private JSONArray zones;
    private String nodeid;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_solenoid_control);

        final ActionBar bar = getSupportActionBar();
        if (bar != null) {
            bar.setDisplayShowHomeEnabled(true);
            bar.setIcon(R.mipmap.soilsmart_icon);
            bar.show();
        }
        userLocalStore = new UserLocalStore(this);

        final Button irrigate = (Button) findViewById(R.id.irrigateButton);
        irrigate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //Irrigate some shit
                SoilSmartService.getInstance().postIrrigate(userLocalStore.getLoggedInUser());
            }
        });

        final Button off = (Button) findViewById(R.id.irrigateButton);
        off.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //Force Off
                if (nodeid != null) {
                    SoilSmartService.getInstance().postForceOff(userLocalStore.getLoggedInUser(), nodeid);
                }
            }
        });


    }

    @Override
    protected void onStart() {
        super.onStart();

        if (!userLocalStore.getUserLoggedIn()) {
            launchActivity(LoginActivity.class);
        }
        //check for a network connection: use chached nodes or fetch latest nodes.
        if (!isNetworkAvailable()) {
            AlertDialog.Builder dlgAlert = new AlertDialog.Builder(this);
            dlgAlert.setMessage("Irrigation controls not available offline.");
            dlgAlert.setTitle("No Network Connection");
            dlgAlert.setPositiveButton("Continue", new DialogInterface.OnClickListener() {
                public void onClick(DialogInterface dialog, int which) {
                    // continue with cache
                }
            });

            dlgAlert.setCancelable(false);
            dlgAlert.create().show();

        } else {
            //fetch zones and draw UI
            zones = SoilSmartService.getInstance().getIrrigation(userLocalStore.getLoggedInUser());
            if(zones != null) {
                Toast.makeText(this, "json size " + zones.length(), Toast.LENGTH_LONG).show();
            }
            //irrigationUI(zones);
        }
    }

    // Check if we have an active network connection
    public boolean isNetworkAvailable() {
        ConnectivityManager connectivityManager = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo activeNetworkInfo = connectivityManager.getActiveNetworkInfo();
        return activeNetworkInfo != null;
    }

    private void irrigationUI(JSONArray jsonn){
        JSONArray waffle;
        try {
            if(jsonn.getJSONObject(0) != null){
                nodeid = jsonn.getString(0);
                waffle = jsonn.getJSONObject(0).optJSONArray("CyclesTimeInMinutes");
                int[] numbers = new int[waffle.length()];
                for(int i = 0; i<waffle.length(); i++){
                    numbers[i] = waffle.optInt(i);
                }
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

}
