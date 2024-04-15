package com.example.avancada;

import android.util.Log;
import com.fasterxml.jackson.databind.ObjectMapper;

public class Utility {

    //Método que chama o tipo de Região (Region, SubRegion, RestrictedRegion) para ser convertida.
    public static Region jsonToObject (String json) {
        if (json.contains("regionFather") && json.contains("restricted")) {
            //Se for uma região restrita:
            return convertJsonToObject(json, RestrictedRegion.class);
        } else if (json.contains("regionFather") && !json.contains("restricted")) {
            //Se for uma sub região:
            return convertJsonToObject(json, SubRegion.class);
        }
        //Se for uma Região:
        return convertJsonToObject(json, Region.class);
    }

    //Método que converte um Objeto para Json
    public static String convertObjectToJson(Object object) {
        ObjectMapper mapper = new ObjectMapper();
        try {
            return mapper.writeValueAsString(object);
        } catch (Exception e) {
            return null;
        }
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
