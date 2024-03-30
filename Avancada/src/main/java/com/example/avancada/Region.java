package com.example.avancada;

import java.util.HashMap;

public class Region {
    private String name;
    private double latitude;
    private double longitude;
    private int user;
    private long timestamp = System.nanoTime();

    public Region (String nome, double lat, double lon, int user){
        this.name = nome;
        this.latitude = lat;
        this.longitude = lon;
        this.user = user;
    }

    public double getLatitude(){

        return this.latitude;
    }

    public double getLongitude(){

        return this.longitude;
    }

    public HashMap<String,Double> toMap(){
        HashMap<String, Double> mapa = new HashMap<>();
        mapa.put("Latitude", this.getLatitude());
        mapa.put("Longitude", this.getLongitude());
        return mapa;
    }


}
