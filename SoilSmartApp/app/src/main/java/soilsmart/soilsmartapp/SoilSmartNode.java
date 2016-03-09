package soilsmart.soilsmartapp;

import java.io.Serializable;
import java.util.Date;

/**
 * SoilSmartApp
 * Created by Ricardo Morones on 2/27/16.
 */
public class SoilSmartNode implements Serializable {
    private double lat;
    private double lon;
    private String zone;
    private String id;
    private Date startDate;
    private double valuesLvl1[];
    private double valuesLvl2[];
    private double valuesLvl3[];
    private double monthValues[];
    private double weekValues[];


    public SoilSmartNode(String id, String zone, double lat, double lon, Date startDate,
                  double[] values1, double[] values2, double[] values3, double monthVal[], double weekVal[]) {
        this.lat = lat;
        this.lon = lon;
        this.zone = zone;
        this.id = id;
        this.startDate = startDate;
        this.valuesLvl1 = new double[values1.length];
        System.arraycopy(values1, 0, valuesLvl1, 0, valuesLvl1.length);
        this.valuesLvl2 = new double[values2.length];
        System.arraycopy(values2, 0, valuesLvl2, 0, valuesLvl2.length);
        this.valuesLvl3 = new double[values3.length];
        System.arraycopy(values3, 0, valuesLvl3, 0, valuesLvl3.length);
        this.monthValues = new double[monthVal.length];
        System.arraycopy(monthVal, 0, monthValues, 0, monthValues.length);
        this.weekValues = new double[weekVal.length];
        System.arraycopy(weekVal, 0, weekValues, 0, weekValues.length);
    }

    public double getLat() {
        return lat;
    }

    public double getLon() {
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

    public double[] getValuesLvl1() {
        return valuesLvl1;
    }

    public double[] getValuesLvl2() {
        return valuesLvl2;
    }
    public double[] getValuesLvl3() {
        return valuesLvl3;
    }
    public double[] getValuesMonth() {
        return monthValues;
    }
    public double[] getValuesWeek() {
        return weekValues;
    }

    public double getValuesLvl1Avg() {
        double ret=0;
        for (int i = 0; i < valuesLvl1.length; ++i) {
            ret += valuesLvl1[i];
        }
        return ret/(double)valuesLvl1.length;
    }

    public double getValuesLvl2Avg() {
        double ret=0;
        for (int i = 0; i < valuesLvl2.length; ++i) {
            ret += valuesLvl2[i];
        }
        return ret/(double)valuesLvl2.length;
    }

    public double getValuesLvl3Avg() {
        double ret=0;
        for (int i = 0; i < valuesLvl3.length; ++i) {
            ret += valuesLvl3[i];
        }
        return ret/(double)valuesLvl3.length;
    }

}
