package com.example.avancada;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;

public class RestrictedRegion extends Region {
    private Region mainRegion;
    private boolean restricted;
    public RestrictedRegion(String nome, double lat, double lon, int user, Region mainRegion, boolean restricted) {
        super(nome, lat, lon, user);
        this.mainRegion = mainRegion;
        this.restricted = restricted;
    }

    //Construtor que recebe os valores lidos do banco de dados em Json
    @JsonCreator
    public RestrictedRegion (@JsonProperty("name") String nome, @JsonProperty("latitude")double lat, @JsonProperty("longitude")double lon, @JsonProperty("timestamp") long timestamp, @JsonProperty("user")int user, @JsonProperty("regionFather")String mainRegion, @JsonProperty("retricted")boolean restricted){
        super(nome, lat, lon, timestamp, user);
        this.mainRegion = null;
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
