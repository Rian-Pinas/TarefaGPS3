package com.example.avancada;

import android.location.Location;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;

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

    //Construtor que recebe os valores lidos do banco de dados em Json
    @JsonCreator
    public Region (@JsonProperty("name") String nome, @JsonProperty("latitude")double lat, @JsonProperty("longitude")double lon, @JsonProperty("timestamp") long timestamp, @JsonProperty("user")int user){
        this.name = nome;
        this.latitude = lat;
        this.longitude = lon;
        this.user = user;
        this.timestamp = timestamp;
    }

    //Método para calcular a distância entre duas Regiões (sendo menor que 30)
    public double distance(Region r2){
        float[] result = new float[1]; //os indíces do resultado podem ser 1, 2 ou 3,
        //sendo 1 o esperado para retornar a distância.
        Location.distanceBetween(this.getLatitude(), this.getLongitude(), r2.getLatitude(), r2.getLongitude(), result);
        return result[0];
    }

    //Permissão para adicionar à fila
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
