package com.example.avancada;

import android.util.Log;
import com.fasterxml.jackson.databind.ObjectMapper;

public class Utility {

    //Método que chama o tipo de Região (Region, SubRegion, RestrictedRegion) para ser convertida.
    public static Region jsonToObject (String json) {
        long tini = System.currentTimeMillis(); //Tarefa 4 - Ci // Tarefa 4
        Region region;
        if (json.contains("regionFather") && json.contains("restricted")) {
            //Se for uma região restrita:
            region = convertJsonToObject(json, RestrictedRegion.class);
        } else if (json.contains("regionFather") && !json.contains("restricted")) {
            //Se for uma sub região:
            region = convertJsonToObject(json, SubRegion.class);
        } else {
            //Se for uma Região:
            region = convertJsonToObject(json, Region.class);
        }

        long tfin = System.currentTimeMillis(); //Tarefa 4 - Ci // Tarefa 4
        TimeUtil.addTempo((tfin-tini), 4);
        return region;
    }

    //Método que converte um Objeto para Json
    public static String convertObjectToJson(Object object) {
        long tini = System.currentTimeMillis(); //Tarefa 6 - Ci // Tarefa 4
        String strObject;
        ObjectMapper mapper = new ObjectMapper();
        try {
            strObject = mapper.writeValueAsString(object);
        } catch (Exception e) {
            strObject = null;
        }

        long tfin = System.currentTimeMillis(); //Tarefa 6 - Ci // Tarefa 4
        TimeUtil.addTempo((tfin-tini), 6);

        return strObject;
    }

    //Método que converte a string Json para um Objeto passado por parâmetro (Nesse caso, Region, SubRegion ou RestrictedRegion)
    private static <T> T convertJsonToObject(String jsonString, Class<T> valueType) {
        ObjectMapper mapper = new ObjectMapper();
        try {
            return mapper.readValue(jsonString, valueType);
        } catch (Exception e) {
            Log.d("Teste", e.getMessage());
            return null;
        }
    }
}
