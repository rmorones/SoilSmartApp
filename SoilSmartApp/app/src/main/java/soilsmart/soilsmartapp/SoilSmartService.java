package soilsmart.soilsmartapp;

import java.util.Date;
import java.util.List;

/**
 * SoilSmartApp
 * Created by Ricardo Morones on 2/28/16.
 */
public class SoilSmartService implements ISoilSmartService, IAuthenticateUser {

    /**
     * A dummy authentication store containing known user names and passwords.
     * TODO: remove after connecting to a real authentication system.
     */
    private static final String[] DUMMY_CREDENTIALS = new String[]{
            "foo@example.com:Hello12", "bar@example.com:World12"
    };

    private SoilSmartService() {}

    private static class SingletonHolder {
        public static final SoilSmartService instance = new SoilSmartService();
    }

    public static SoilSmartService getInstance() {
        return SingletonHolder.instance;
    }

    @Override
    public boolean authenticate(User user) {
        for (String credentials : DUMMY_CREDENTIALS) {
            String[] creds = credentials.split(":");
            if (creds[0].equals(user.getEmail()) && creds[1].equals(user.getPasswordHash())) {
                return true;
            }
        }
        return false;
    }

    @Override
    public List<SoilSmartNode> getNodes(final User user) {
        return null;
    }

    @Override
    public double[] getLastWeek(final long nodeId) {
        return new double[0];
    }

    @Override
    public double[] getLastMonth(final long nodeId) {
        return new double[0];
    }

    @Override
    public void appendPoint(final long nodeId, final Date date, final double value) {

    }

    @Override
    public boolean registerUser(final User user, final String key) {
        return false;
    }

}
