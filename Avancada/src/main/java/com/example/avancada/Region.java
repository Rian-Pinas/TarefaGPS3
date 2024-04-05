package com.example.avancada;

import java.util.HashMap;
import java.util.Map;

public class Region {
    private String name;
    private double latitude;
    private double longitude;
    private int user;
    private long timestamp;

    public Region (String nome, double lat, double lon, int user){
        this.name = nome;
        this.latitude = lat;
        this.longitude = lon;
        this.user = user;
        this.timestamp = System.nanoTime();
    }

    // Construtor que recebe os valores lidos do banco de dados
    public Region (Map<String, Object> map){
        this.name = (String) map.get("name");
        this.latitude = (double) map.get("latitude");
        this.longitude = (double) map.get("longitude");
        this.user = Math.toIntExact((long)map.get("user"));
        this.timestamp = (long) map.get("timestamp");
    }

    public double getLatitude(){
        return this.latitude;
    }

    public double getLongitude(){
        return this.longitude;
    }

    public String getName(){
        return this.name;
    }

    public int getUser(){
        return this.user;
    }

    public long getTimestamp(){
        return this.timestamp;
    }

}
