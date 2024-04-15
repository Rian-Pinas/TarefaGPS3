package com.example.avancada;

import android.location.Location;

import java.util.HashMap;
import java.util.Map;

public class Region {
    protected String name;
    protected double latitude;
    protected double longitude;
    protected int user;
    protected long timestamp;

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

    //Método para calcular a distância entre duas Regiões (sendo menor que 30)
    public double distance(Region r2){
        float[] result = new float[1]; //os indíces do resultado podem ser 1, 2 ou 3,
        //sendo 1 o esperado para retornar a distância.
        Location.distanceBetween(this.getLatitude(), this.getLongitude(), r2.getLatitude(), r2.getLongitude(), result);
        return result[0];
    }

    public boolean permitirAddFila(Region r2) {
        return distance (r2) >= 30;
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
