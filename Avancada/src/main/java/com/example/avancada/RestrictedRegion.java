package com.example.avancada;

import android.location.Location;

import java.util.Map;

public class RestrictedRegion extends Region {
    private Region mainRegion;
    private boolean restricted;
    public RestrictedRegion(String nome, double lat, double lon, int user, Region mainRegion, boolean restricted) {
        super(nome, lat, lon, user);
        this.mainRegion = mainRegion;
        this.restricted = restricted;
    }

    public RestrictedRegion(Map<String, Object> map, Region mainRegion, boolean restricted) {
        super(map);
        this.mainRegion = mainRegion;
        this.restricted = restricted;
    }

    //Distancia entre sub/restrit regiões precisa ser de até 5 metros
    @Override
    public boolean permitirAddFila(Region r2) {
        return distance (r2) >= 5;
    }

    public String getRegionFather() {
        return this.mainRegion.getName();
    }

    public boolean getRestricted() {
        return this.restricted;
    }
}
