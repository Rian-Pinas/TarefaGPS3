/*package com.example.avancada;

import android.util.Log;

import java.util.ArrayList;

public class TimeUtil {
    private static ArrayList<Long> tempoT1 = new ArrayList<>(); //T1 = Adicionar regiões na fila.
    private static ArrayList<Long> tempoT2 = new ArrayList<>(); //T2 = Ler banco de dados.
    private static ArrayList<Long> tempoT3 = new ArrayList<>(); //T3 = Descriptografar regiões.
    private static ArrayList<Long> tempoT4 = new ArrayList<>(); //T4 = Converter JSON para objeto.
    private static ArrayList<Long> tempoT5 = new ArrayList<>(); //T5 = Enviar para o banco de dados.
    private static ArrayList<Long> tempoT6 = new ArrayList<>(); //T6 = Converter objeto para JSON.
    private static ArrayList<Long> tempoT7 = new ArrayList<>(); //T7 = Criptografar regiões.
    private static ArrayList<Long> tempoT8 = new ArrayList<>(); //T8 = Ler a Fila.

    public static synchronized void addTempo(Long tempo, int tarefa) {
        switch(tarefa) {
            case 1:
                tempoT1.add(tempo);
                break;
            case 2:
                tempoT2.add(tempo);
                break;
            case 3:
                tempoT3.add(tempo);
                break;
            case 4:
                tempoT4.add(tempo);
                break;
            case 5:
                tempoT5.add(tempo);
                break;
            case 6:
                tempoT6.add(tempo);
                break;
            case 7:
                tempoT7.add(tempo);
                break;
            case 8:
                tempoT8.add(tempo);
                break;
        }
    }

    public static Long mean(ArrayList<Long> tempoTn){ //media
        long sum = 0;
        Log.d("Tarefas", tempoTn.toString());
        for (long T: tempoTn)
            sum+=T;
        if (sum == 0) {
            return 0L;
        }
        return sum/tempoTn.size();
    }

    public static void showTime(){
        Log.d("Tarefas", "Média dos tempos de Execução:");
        Log.d("Tarefas", "Ci - Tarefa 1: "+String.valueOf(mean(tempoT1)));
        Log.d("Tarefas", "Ci - Tarefa 2: "+String.valueOf(mean(tempoT2)));
        Log.d("Tarefas", "Ci - Tarefa 3: "+String.valueOf(mean(tempoT3)));
        Log.d("Tarefas", "Ci - Tarefa 4: "+String.valueOf(mean(tempoT4)));
        Log.d("Tarefas", "Ci - Tarefa 5: "+String.valueOf(mean(tempoT5)));
        Log.d("Tarefas", "Ci - Tarefa 6: "+String.valueOf(mean(tempoT6)));
        Log.d("Tarefas", "Ci - Tarefa 7: "+String.valueOf(mean(tempoT7)));
        Log.d("Tarefas", "Ci - Tarefa 8: "+String.valueOf(mean(tempoT8)));
    }
}*/