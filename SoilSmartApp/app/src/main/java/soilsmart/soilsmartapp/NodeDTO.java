package soilsmart.soilsmartapp;

import java.io.Serializable;
import java.util.Date;

/**
 * Created by jesus on 3/10/16.
 */
public class NodeDTO implements Serializable {

    public double Latitude;
    public double Longitude;
    public String Zone;
    public String NodeName;
    public String Id;
    public Date[] Dates;
    public double[] Level_1;
    public double[] Level_2;
    public double[] Level_3;
    public String ProductKey;

    public double getLongitude() {
        return Longitude;
    }

    public void setLongitude(double longitude) {
        Longitude = longitude;
    }

    public String getZone() {
        return Zone;
    }

    public void setZone(String zone) {
        Zone = zone;
    }

    public String getNodeName() {
        return NodeName;
    }

    public void setNodeName(String nodeName) {
        NodeName = nodeName;
    }

    public String getId() {
        return Id;
    }

    public void setId(String id) {
        Id = id;
    }

    public Date[] getDates() {
        return Dates;
    }

    public void setDates(Date[] dates) {
        Dates = dates;
    }

    public double[] getLevel_1() {
        return Level_1;
    }

    public void setLevel_1(double[] level_1) {
        Level_1 = level_1;
    }

    public double[] getLevel_2() {
        return Level_2;
    }

    public void setLevel_2(double[] level_2) {
        Level_2 = level_2;
    }

    public double[] getLevel_3() {
        return Level_3;
    }

    public void setLevel_3(double[] level_3) {
        Level_3 = level_3;
    }

    public String getProductKey() {
        return ProductKey;
    }

    public void setProductKey(String productKey) {
        ProductKey = productKey;
    }


    public double getLatitude() {
        return Latitude;
    }

    public void setLatitude(double latitude) {
        Latitude = latitude;
    }
}
