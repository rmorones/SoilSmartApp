package soilsmart.soilsmartapp;

import android.app.IntentService;
import android.content.Intent;
import android.util.Log;

/**
 * An {@link IntentService} subclass for handling asynchronous task requests in
 * a service on a separate handler thread.
 * <p/>
 * TODO: Customize class - update intent actions and extra parameters.
 */
public class LeakageDetectionService extends IntentService {

    public LeakageDetectionService() {
        super("LeakageDetectionService");
    }

    @Override
    protected void onHandleIntent(Intent intent) {
        if (intent != null) {
            final UserLocalStore userLocalStore = new UserLocalStore(this);
            final User user = userLocalStore.getLoggedInUser();
            boolean leakageDetected = SoilSmartService.getInstance().isLeakageDetected(user);
            if (leakageDetected) {
                LeakageNotification.notify(this,"New Leakage Detected",1);
            }
        }
    }

}
