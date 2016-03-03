package soilsmart.soilsmartapp.views;

import android.location.LocationListener;

import com.wikitude.architect.ArchitectView;
import com.wikitude.architect.StartupConfiguration;

import soilsmart.soilsmartapp.R;
import soilsmart.soilsmartapp.views.wikitude.AbstractArchitectCamActivity;

public class ARActivity extends AbstractArchitectCamActivity {

    @Override
    protected StartupConfiguration.CameraPosition getCameraPosition() {
        return null;
    }

    @Override
    protected boolean hasGeo() {
        return false;
    }

    @Override
    protected boolean hasIR() {
        return false;
    }

    @Override
    public String getActivityTitle() {
        return null;
    }

    @Override
    public String getARchitectWorldPath() {
        return null;
    }

    @Override
    public ArchitectView.ArchitectUrlListener getUrlListener() {
        return null;
    }

    @Override
    public int getContentViewId() {
        return R.layout.activity_ar;
    }

    @Override
    public String getWikitudeSDKLicenseKey() {
        return null;
    }

    @Override
    public int getArchitectViewId() {
        return R.id.architectView;
    }

    @Override
    public ILocationProvider getLocationProvider(final LocationListener locationListener) {
        return null;
    }

    @Override
    public ArchitectView.SensorAccuracyChangeListener getSensorAccuracyListener() {
        return null;
    }

    @Override
    public float getInitialCullingDistanceMeters() {
        return 0;
    }
}
