package com.example.avancada;

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


}
