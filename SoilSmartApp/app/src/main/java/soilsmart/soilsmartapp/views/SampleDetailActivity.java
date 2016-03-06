package soilsmart.soilsmartapp.views;

import android.app.Activity;
import android.os.Bundle;
import android.widget.TextView;

import soilsmart.soilsmartapp.R;

public class SampleDetailActivity extends Activity {

    public static final String EXTRAS_KEY_POI_ID = "id";
    public static final String EXTRAS_KEY_POI_TITILE = "title";
    public static final String EXTRAS_KEY_POI_DESCR = "description";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        this.setContentView(R.layout.activity_sample_detail);

        ((TextView)findViewById(R.id.poi_id)).setText(getIntent().getExtras().getString(EXTRAS_KEY_POI_ID));
        ((TextView)findViewById(R.id.poi_title)).setText(getIntent().getExtras().getString(EXTRAS_KEY_POI_TITILE));
        ((TextView)findViewById(R.id.poi_description)).setText(getIntent().getExtras().getString(EXTRAS_KEY_POI_DESCR));
    }

}
