package com.example.tarefagps;

import com.example.avancada.Region;

public class Coletor {
    private Region[] regioes = new Region[6];

    private long[][] y_time = new long[10][6];
    private int posicao = 0;
    private int execucao = 0;

    public Coletor (Region inicial, Region finalist, Region R1, Region R2, Region R3, Region R4){
        this.regioes[0] = inicial;
        this.regioes[1] = R1;
        this.regioes[2] = R2;
        this.regioes[3] = R3;
        this.regioes[4] = R4;
        this.regioes[5] = finalist;
    }

    //Atualização da localização para obter a posição atual.
    public String LocUpdate(double lat, double lon){
        String mensagem = "";

        if (posicao<6 && execucao<10){
            if (regioes[posicao].distance(new Region("Regiao", lat, lon, 0)) <= 10){
                y_time[execucao][posicao] = System.nanoTime() / 1_000_000_000; //tempo em segundos

                mensagem = "Passou pelo ponto " + posicao + " Com o tempo: " + y_time[execucao][posicao] + " segs";
                posicao++;
                if(posicao == 6) {
                    execucao++;
                    posicao = 0;
                }
            }
        }
        return mensagem;
    }

}
