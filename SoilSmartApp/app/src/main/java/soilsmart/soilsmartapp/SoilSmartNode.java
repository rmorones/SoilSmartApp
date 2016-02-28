package soilsmart.soilsmartapp;

import java.io.Serializable;
import java.util.Date;

/**
 * SoilSmartApp
 * Created by Ricardo Morones on 2/27/16.
 */
public class SoilSmartNode implements Serializable {
    private float lat;
    private float lon;
    private String zone;
    private String id;
    private Date startDate;
    private double valuesLvl1[];
    private double valuesLvl2[];
    private double valuesLvl3[];

    private SoilSmartNode() {
    }

    SoilSmartNode(String id, String zone, float lat, float lon, Date startDate,
                  double[] values1, double[] values2, double[] values3) {
        this.lat = lat;
        this.lon = lon;
        this.zone = zone;
        this.id = id;
        this.startDate = startDate;
        this.valuesLvl1 = new double[valuesLvl1.length];
        System.arraycopy(valuesLvl1, 0, this.valuesLvl1, 0, valuesLvl1.length);
        this.valuesLvl2 = new double[valuesLvl2.length];
        System.arraycopy(valuesLvl2, 0, this.valuesLvl2, 0, valuesLvl2.length);
        this.valuesLvl3 = new double[valuesLvl3.length];
        System.arraycopy(valuesLvl3, 0, this.valuesLvl3, 0, valuesLvl3.length);
    }

    public float getLat() {
        return lat;
    }

    public float getLon() {
        return lon;
    }

    public String getId() {
        return id;
    }

    public String getZone() {
        return zone;
    }

    public Date getStartDate() {
        return startDate;
    }

    public double[] getValuesLvl3() {
        return valuesLvl3;
    }

    public double[] getValuesLvl1() {
        return valuesLvl1;
    }

    public double[] getValuesLvl2() {
        return valuesLvl2;
    }
}
