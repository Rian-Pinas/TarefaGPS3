package com.example.avancada;

import android.util.Base64;
import android.util.Log;

import javax.crypto.Cipher;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.SecretKeySpec;
import java.nio.charset.StandardCharsets;
import java.security.Key;

public class Cryptography {
    private static final String keyString = "CHAVES_DE_ACESSO";
    private static final String ALGORITHM = "AES";
    private static final String TRANSFORMATION = "AES/CBC/PKCS5Padding";

    //Método para realização da criptografia - encriptografar
    private static String encrypt(String json) throws Exception {
        Key key = new SecretKeySpec(keyString.getBytes(StandardCharsets.UTF_8), ALGORITHM);
        Cipher cipher = Cipher.getInstance(TRANSFORMATION);
        IvParameterSpec iv = generateIV();
        cipher.init(Cipher.ENCRYPT_MODE, key, iv);
        byte[] encrypted = cipher.doFinal(json.getBytes(StandardCharsets.UTF_8));
        return Base64.encodeToString(encrypted, Base64.DEFAULT);
    }

    //Método para realização da criptografia - descriptografar
    private static String decrypt(String encryptedJson) throws Exception{
        Key key = new SecretKeySpec(keyString.getBytes(StandardCharsets.UTF_8), ALGORITHM);
        Cipher cipher = Cipher.getInstance(TRANSFORMATION);
        IvParameterSpec iv = generateIV();
        cipher.init(Cipher.DECRYPT_MODE, key, iv);
        byte[] decoded = Base64.decode(encryptedJson, Base64.DEFAULT);
        byte[] decrypted = cipher.doFinal(decoded);
        return new String(decrypted, StandardCharsets.UTF_8);
    }

    //Método no qual é realizada a conversão, seguida da encriptografia e retorna o Json encriptografado.
    public static String encryptRegion(Region region){
        long tini = System.currentTimeMillis(); //Tarefa 7 - Ci //Tarefa 4
        String strAux;
        String regConverted = Utility.convertObjectToJson(region);
        Log.d("Teste", regConverted);
        try {
            assert regConverted != null;
            strAux = encrypt(regConverted);
        } catch (Exception e) {
            Log.d("Teste", e.getMessage());
            strAux = null;
        }

        long tfin = System.currentTimeMillis(); //Tarefa 7 - Ci // Tarefa 4
        TimeUtil.addTempo((tfin-tini), 7);

        return strAux;
    }

    //Método no qual é realizada a descriptografia, seguida da conversão do Json e retorna o objeto esperado.
    public static Region decryptRegion(String json){
        Region region = null;
        long tini = System.currentTimeMillis(); //Tarefa 3 - Ci //Tarefa 4
        try {
            String aux = decrypt(json);
            Log.d("Teste", aux);
            region = Utility.jsonToObject(aux);
        } catch (Exception e) {
            Log.d("Teste", e.getMessage());
        }
        long tfin = System.currentTimeMillis(); //Tarefa 3 - Ci //Tarefa 4
        TimeUtil.addTempo((tfin-tini), 3);
        return region;
    }

    //Gerador do Vetor de Inicialização, necessário em criptografias CBC, precisa de 16 bytes.
    private static IvParameterSpec generateIV() {
        return new IvParameterSpec("IVTESTECHAVEBOAS".getBytes());
    }
}
