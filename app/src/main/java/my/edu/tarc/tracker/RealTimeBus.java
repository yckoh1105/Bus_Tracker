package my.edu.tarc.tracker;

/**
 * Created by Dell PC on 9/10/2018.
 */

public class RealTimeBus {
    private int routeID;
    private String busPlateNum;
    private double lat;
    private double lon;
    private double orderNum;
    private String traf_dateTime;
    private String status;
    private double speed;

    public RealTimeBus(int routeID, String busPlateNum, double lat, double lon, double orderNum, String traf_dateTime, String status, double speed) {
        this.routeID = routeID;
        this.busPlateNum = busPlateNum;
        this.lat = lat;
        this.lon = lon;
        this.orderNum = orderNum;
        this.traf_dateTime = traf_dateTime;
        this.status = status;
        this.speed = speed;
    }

    public int getRouteID() {
        return routeID;
    }

    public void setRouteID(int routeID) {
        this.routeID = routeID;
    }

    public String getBusPlateNum() {
        return busPlateNum;
    }

    public void setBusPlateNum(String busPlateNum) {
        this.busPlateNum = busPlateNum;
    }

    public double getLat() {
        return lat;
    }

    public void setLat(double lat) {
        this.lat = lat;
    }

    public double getLon() {
        return lon;
    }

    public void setLon(double lon) {
        this.lon = lon;
    }

    public double getOrderNum() {
        return orderNum;
    }

    public void setOrderNum(double orderNum) {
        this.orderNum = orderNum;
    }

    public String getTraf_dateTime() {
        return traf_dateTime;
    }

    public void setTraf_dateTime(String traf_dateTime) {
        this.traf_dateTime = traf_dateTime;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public double getSpeed() {
        return speed;
    }

    public void setSpeed(double speed) {
        this.speed = speed;
    }
}
