package com.example.avancada;

import android.location.Location;

import java.util.HashMap;
import java.util.Map;

public class SubRegion extends Region {
    private Region mainRegion;

    public SubRegion(String nome, double lat, double lon, int user, Region mainRegion) {
        super(nome, lat, lon, user);
        this.mainRegion = mainRegion;
    }

    public SubRegion(Map<String, Object> map, Region mainRegion) {
        super(map);
        this.mainRegion = mainRegion;
    }

    //Distancia entre sub/restrict regiões precisa ser de até 5 metros
    @Override
    public boolean permitirAddFila(Region r2) {
        return distance (r2) >= 5;
    }

    public String getRegionFather() {
        return this.mainRegion.getName();
    }
}
