package soilsmart.soilsmartapp;

import java.util.Date;
import java.util.List;

/**
 * SoilSmartApp
 * Created by Ricardo Morones on 2/28/16.
 */
public interface ISoilSmartService {

    /**
     * Gets all the node ids and gps location of the nodes,
     * and the values for each level
     *
     * @param user
     * @return
     */
    List<SoilSmartNode> getNodes(User user);

    /**
     * Get last week worth of data
     *
     * @param nodeId
     * @return
     */
    double[] getLastWeek(long nodeId);

    /**
     * Get last month worth of data
     *
     * @param nodeId
     * @return
     */
    double[] getLastMonth(long nodeId);

    /**
     * Append soil moisture value with a timestamp for
     * the node with id, nodeId
     *
     * @param nodeId
     * @param date
     * @param value
     */
    void appendPoint(long nodeId, Date date, double value);

    /**
     * Register user with email, password, and product key.
     *
     */
    boolean registerUser(User user, String key);

    boolean isLeakageDetected(User user);
}
