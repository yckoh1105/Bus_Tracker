package com.example.dellpc.bus_tracker;

/**
 * Created by Dell PC on 7/10/2018.
 */

public class Route {
    private int routeId;
    private String routeName;

    public Route(int routeId, String routeName) {
        this.routeId = routeId;
        this.routeName = routeName;
    }

    public int getRouteId() {
        return routeId;
    }

    public void setRouteId(int routeId) {
        this.routeId = routeId;
    }

    public String getRouteName() {
        return routeName;
    }

    public void setRouteName(String routeName) {
        this.routeName = routeName;
    }
}
