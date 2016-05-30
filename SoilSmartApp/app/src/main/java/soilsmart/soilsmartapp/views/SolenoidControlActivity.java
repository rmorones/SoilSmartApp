package soilsmart.soilsmartapp.views;

import android.os.Bundle;
import android.support.design.widget.Snackbar;
import android.support.v7.app.ActionBar;

import soilsmart.soilsmartapp.R;
import soilsmart.soilsmartapp.SoilSmartNode;
import soilsmart.soilsmartapp.UserLocalStore;

/**
 * Created by jesus on 5/30/16.
 */
public class SolenoidControlActivity extends BaseMenuActivity {

    private UserLocalStore userLocalStore;
    private SoilSmartNode node;

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
        //yo
        Snackbar.make(findViewById(R.id.container), R.string.snackbarMsg,
                Snackbar.LENGTH_LONG)
                .show();

    }

}
