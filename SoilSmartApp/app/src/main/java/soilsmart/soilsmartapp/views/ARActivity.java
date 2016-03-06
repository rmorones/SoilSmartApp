package soilsmart.soilsmartapp.views;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.hardware.SensorManager;
import android.location.Location;
import android.location.LocationListener;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.widget.Toast;

import com.wikitude.architect.ArchitectView;
import com.wikitude.architect.StartupConfiguration;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.File;
import java.io.FileOutputStream;
import java.util.HashMap;
import java.util.List;

import soilsmart.soilsmartapp.R;
import soilsmart.soilsmartapp.SoilSmartNode;
import soilsmart.soilsmartapp.views.wikitude.AbstractArchitectCamActivity;
import soilsmart.soilsmartapp.views.wikitude.ArchitectViewHolderInterface;
import soilsmart.soilsmartapp.views.wikitude.LocationProvider;

public class ARActivity extends AbstractArchitectCamActivity {

    /**
     * last time the calibration toast was shown, this avoids too many toast shown when compass needs calibration
     */
    private long lastCalibrationToastShownTimeMillis = System.currentTimeMillis();

    protected Bitmap screenCapture = null;

    final static int WIKITUDE_PERMISSIONS_REQUEST_EXTERNAL_STORAGE = 3;
    final static int WIKITUDE_PERMISSIONS_REQUEST_FINE_LOCATION = 5;
    final static int WIKITUDE_PERMISSIONS_REQUEST_CAMERA = 6;

    @Override
    public void onCreate(final Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // Get permissions
        if ( ContextCompat.checkSelfPermission(ARActivity.this, Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED ) {
            ActivityCompat.requestPermissions(ARActivity.this, new String[]{Manifest.permission.CAMERA}, WIKITUDE_PERMISSIONS_REQUEST_CAMERA);
        }
        if ( ContextCompat.checkSelfPermission(ARActivity.this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED ) {
            ActivityCompat.requestPermissions(ARActivity.this, new String[]{Manifest.permission.CAMERA}, WIKITUDE_PERMISSIONS_REQUEST_FINE_LOCATION);
        }
    }

    @Override
    protected void onPostCreate(final Bundle savedInstanceState) {
        super.onPostCreate(savedInstanceState);
        // inject data to javascript
        this.injectData();
    }

    @Override
    public JSONArray getPoiNodeInfo(final Location userLocation) {
        final List<SoilSmartNode> nodes = NodeLocationsActivity.GetRandomNodes();
        final JSONArray pois = new JSONArray();

        // ensure these attributes are also used in JavaScript when extracting POI data
        final String ATTR_ID = "id";
        final String ATTR_NAME = "name";
        final String ATTR_DESCRIPTION = "description";
        final String ATTR_LATITUDE = "latitude";
        final String ATTR_LONGITUDE = "longitude";
        final String ATTR_ALTITUDE = "altitude";
        final String ATTR_LVL = "level";
        final float UNKNOWN_ALTITUDE = -32768f;

        for (SoilSmartNode node : nodes) {
            final HashMap<String, String> poiInfo = new HashMap<>();
            poiInfo.put(ATTR_ID, String.valueOf(node.getId()));
            double latDiff = userLocation.getLatitude() - node.getLat();
            double lonDiff = userLocation.getLongitude() - node.getLon();
            if (latDiff <= 0.3 && latDiff >= -0.3) {
                latDiff = 0;
            } else if (latDiff < 0) { //if negative
                latDiff = -0.3;
            } else {
                latDiff = 0.3;
            }

            if (lonDiff <= 0.3 && lonDiff >= -0.3) {
                lonDiff = 0;
            } else if (lonDiff < 0) {
                lonDiff = 0.3;
            } else {
                lonDiff = -0.3;
            }
            poiInfo.put(ATTR_LATITUDE, String.valueOf(userLocation.getLatitude() - latDiff));
            poiInfo.put(ATTR_LONGITUDE, String.valueOf(userLocation.getLongitude() - lonDiff));
            poiInfo.put(ATTR_ALTITUDE, String.valueOf(UNKNOWN_ALTITUDE));
            poiInfo.put(ATTR_NAME, "Node: " + String.valueOf(node.getId()));
            poiInfo.put(ATTR_DESCRIPTION, "Zone: " + (node.getZone() != null ? node.getZone() : "unknown"));
            if (node.getValuesLvl1Avg() >= 0.85 && node.getValuesLvl2Avg() >= 0.9 && node.getValuesLvl3Avg() >= 0.9) {
                poiInfo.put(ATTR_LVL, "full");
            } else if (node.getValuesLvl1Avg() >= 0.7 && node.getValuesLvl2Avg() >= 0.7 && node.getValuesLvl3Avg() >= 0.7) {
                poiInfo.put(ATTR_LVL, "almost_full");
            } else if (node.getValuesLvl1Avg() >= 0.4 && node.getValuesLvl2Avg() >= 0.4 && node.getValuesLvl3Avg() >= 0.4) {
                poiInfo.put(ATTR_LVL, "half");
            } else if (node.getValuesLvl1Avg() >= 0.15 && node.getValuesLvl2Avg() >= 0.15 && node.getValuesLvl3Avg() >= 0.15) {
                poiInfo.put(ATTR_LVL, "low");
            } else {
                poiInfo.put(ATTR_LVL, "very_low");
            }
            pois.put(new JSONObject(poiInfo));
        }
        return pois;
    }

    @Override
    protected StartupConfiguration.CameraPosition getCameraPosition() {
        return StartupConfiguration.CameraPosition.DEFAULT;
    }

    @Override
    protected boolean hasGeo() {
        return true;
    }

    @Override
    protected boolean hasIR() {
        return false;
    }

    @Override
    public String getActivityTitle() {
        return getString(R.string.title_activity_ar);
    }

    @Override
    public String getARchitectWorldPath() {
        return "file:///android_asset/augmented_reality/index.html";
    }

    @Override
    public ArchitectView.ArchitectUrlListener getUrlListener() {
        return new ArchitectView.ArchitectUrlListener() {
            @Override
            public boolean urlWasInvoked(String uriString) {
                final Uri invokedUri = Uri.parse(uriString);

                // pressed "More" button on POI-detail panel
                if ("markerselected".equalsIgnoreCase(invokedUri.getHost())) {
                    final Intent poiDetailIntent = new Intent(ARActivity.this, SampleDetailActivity.class);
                    poiDetailIntent.putExtra(SampleDetailActivity.EXTRAS_KEY_POI_ID, String.valueOf(invokedUri.getQueryParameter("id")) );
                    poiDetailIntent.putExtra(SampleDetailActivity.EXTRAS_KEY_POI_TITILE, String.valueOf(invokedUri.getQueryParameter("title")) );
                    poiDetailIntent.putExtra(SampleDetailActivity.EXTRAS_KEY_POI_DESCR, String.valueOf(invokedUri.getQueryParameter("description")) );
                    ARActivity.this.startActivity(poiDetailIntent);
                    return true;
                }
                // pressed snapshot button. check if host is button to fetch e.g. 'architectsdk://button?action=captureScreen', you may add more checks if more buttons are used inside AR scene
                else if ("button".equalsIgnoreCase(invokedUri.getHost())) {
                    ARActivity.this.architectView.captureScreen(ArchitectView.CaptureScreenCallback.CAPTURE_MODE_CAM_AND_WEBVIEW, new ArchitectView.CaptureScreenCallback() {

                        @Override
                        public void onScreenCaptured(final Bitmap screenCapture) {
                            if ( ContextCompat.checkSelfPermission(ARActivity.this, Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED ) {
                                ARActivity.this.screenCapture = screenCapture;
                                ActivityCompat.requestPermissions(ARActivity.this, new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE}, WIKITUDE_PERMISSIONS_REQUEST_EXTERNAL_STORAGE);
                            } else {
                                ARActivity.this.saveScreenCaptureToExternalStorage(screenCapture);
                            }
                        }
                    });
                }
                return true;
            }
        };
    }

    @Override
    public int getContentViewId() {
        return R.layout.activity_ar;
    }

    @Override
    public String getWikitudeSDKLicenseKey() {
        return getString(R.string.wikitude_key);
    }

    @Override
    public int getArchitectViewId() {
        return R.id.architectView;
    }

    @Override
    public ILocationProvider getLocationProvider(final LocationListener locationListener) {
        return new LocationProvider(this, locationListener);
    }

    @Override
    public ArchitectView.SensorAccuracyChangeListener getSensorAccuracyListener() {
        return new ArchitectView.SensorAccuracyChangeListener() {
            @Override
            public void onCompassAccuracyChanged( int accuracy ) {
				/* UNRELIABLE = 0, LOW = 1, MEDIUM = 2, HIGH = 3 */
                if ( accuracy < SensorManager.SENSOR_STATUS_ACCURACY_MEDIUM &&
                        ARActivity.this != null &&
                        !ARActivity.this.isFinishing() &&
                        System.currentTimeMillis() - ARActivity.this.lastCalibrationToastShownTimeMillis > 5 * 1000) {
                    Toast.makeText( ARActivity.this,
                            R.string.compass_accuracy_low,
                            Toast.LENGTH_LONG ).show();
                    ARActivity.this.lastCalibrationToastShownTimeMillis = System.currentTimeMillis();
                }
            }
        };
    }

    @Override
    public float getInitialCullingDistanceMeters() {
        return ArchitectViewHolderInterface.CULLING_DISTANCE_DEFAULT_METERS;
    }

    protected void saveScreenCaptureToExternalStorage(final Bitmap screenCapture) {
        if ( screenCapture != null ) {
            // store screenCapture into external cache directory
            final File screenCaptureFile = new File(Environment
                    .getExternalStorageDirectory().toString(),
                    "screenCapture_" + System.currentTimeMillis() + ".jpg");

            // 1. Save bitmap to file & compress to jpeg. You may use PNG too
            try {

                final FileOutputStream out = new FileOutputStream(screenCaptureFile);
                screenCapture.compress(Bitmap.CompressFormat.JPEG, 90, out);
                out.flush();
                out.close();

                // 2. create send intent
                final Intent share = new Intent(Intent.ACTION_SEND);
                share.setType("image/jpg");
                share.putExtra(Intent.EXTRA_STREAM, Uri.fromFile(screenCaptureFile));

                // 3. launch intent-chooser
                final String chooserTitle = "Share Snaphot";
                ARActivity.this.startActivity(Intent.createChooser(share, chooserTitle));

            } catch (final Exception e) {
                // should not occur when all permissions are set
                ARActivity.this.runOnUiThread(new Runnable() {

                    @Override
                    public void run() {
                        // show toast message in case something went wrong
                        Toast.makeText(ARActivity.this,
                                "Unexpected error, " + e,
                                Toast.LENGTH_LONG).show();
                    }
                });
            }
        }
    }

    @Override
    public void onRequestPermissionsResult(final int requestCode, @NonNull final String[] permissions, @NonNull final int[] grantResults) {
        switch (requestCode) {
            case WIKITUDE_PERMISSIONS_REQUEST_EXTERNAL_STORAGE: {
                if ( grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED ) {
                    this.saveScreenCaptureToExternalStorage(ARActivity.this.screenCapture);
                } else {
                    Toast.makeText(this,
                            "Please allow access to external storage, otherwise the screen capture can not be saved.",
                            Toast.LENGTH_SHORT).show();
                }
            }
            case WIKITUDE_PERMISSIONS_REQUEST_CAMERA: {
                if ( grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_DENIED ) {
                    Toast.makeText(this,
                            "Please allow access to the camera, otherwise augmented reality mode is useless.",
                            Toast.LENGTH_SHORT).show();
                }
            }
            case WIKITUDE_PERMISSIONS_REQUEST_FINE_LOCATION: {
                if ( grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_DENIED ) {
                    Toast.makeText(this,
                            "Please allow access to location, otherwise the geo-locations cannot be resolved.",
                            Toast.LENGTH_SHORT).show();
                }
            }
        }
    }
}
