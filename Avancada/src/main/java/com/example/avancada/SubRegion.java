package com.example.avancada;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;

public class SubRegion extends Region {
    private Region mainRegion;

    public SubRegion(String nome, double lat, double lon, int user, Region mainRegion) {
        super(nome, lat, lon, user);
        this.mainRegion = mainRegion;
    }

    //Construtor que recebe os valores lidos do banco de dados em Json
    @JsonCreator
    public SubRegion (@JsonProperty("name") String nome, @JsonProperty("latitude")double lat, @JsonProperty("longitude")double lon, @JsonProperty("timestamp") long timestamp, @JsonProperty("user")int user, @JsonProperty("regionFather")String mainRegion){
        super(nome, lat, lon, timestamp, user);
        this.mainRegion = null;
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
