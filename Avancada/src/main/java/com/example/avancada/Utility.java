package com.example.avancada;

import android.location.Location;

public class Utility {

    //Método para calcular a distância entre duas Regiões (sendo menor que 30)
    public static boolean permitirAddFila(Region r1, Region r2){
        float[] result = new float[1]; //os indíces do resultado podem ser 1, 2 ou 3,
                                       //sendo 1 o esperado para retornar a distância.
        Location.distanceBetween(r1.getLatitude(), r1.getLongitude(), r2.getLatitude(), r2.getLongitude(), result);
        return result[0]>30;
    }
}
