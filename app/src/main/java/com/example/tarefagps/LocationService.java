package com.example.tarefagps;

import android.location.Location;
import android.util.Log;

import com.example.avancada.Cryptography;
import com.example.avancada.Region;
import com.example.avancada.RestrictedRegion;
import com.example.avancada.SubRegion;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.LinkedList;
import java.util.Queue;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicReference;
import java.util.concurrent.locks.ReentrantLock;

public class LocationService {
    ReentrantLock lock = new ReentrantLock();
    private final Queue<String> queue = new LinkedList<>();
    private int contador = 0;
    private FirebaseFirestore bd = FirebaseFirestore.getInstance();
    private boolean subRest = false;

    public void enqueue(Location item){
        Region regiao = new Region ("Região "+contador, item.getLatitude(), item.getLongitude(), 0);
        AtomicBoolean boolFila = new AtomicBoolean(false);
        AtomicReference<Region> mainRegion = new AtomicReference<>(null); //Utilizado para dar o valor de mainRegion para as sub regiões.
        AtomicBoolean boolBancoD = new AtomicBoolean(false);

        Thread verFila = canAddFila(regiao, boolFila, mainRegion);
        Thread verBD = canAddBD(regiao, boolBancoD, mainRegion);

        new Thread(() -> {
            try {
                verBD.start();
                verFila.start();
                verBD.join();
                verFila.join();

                if(boolFila.get() && boolBancoD.get()) {
                    Region novaRegiao = new Region("Região " + contador, item.getLatitude(), item.getLongitude(), 0);
                    if (mainRegion.get() != null) {
                        if (subRest) {
                            novaRegiao = new RestrictedRegion("Região "+contador, item.getLatitude(), item.getLongitude(), 0, mainRegion.get(), true);
                            Log.d("Teste", "Adicionada Região Restrita");
                        } else {
                            novaRegiao = new SubRegion("Região "+contador, item.getLatitude(), item.getLongitude(), 0, mainRegion.get());
                            Log.d("Teste", "Adicionada Sub-Região");
                        }
                        subRest = !subRest;
                    }

                    if (lock.tryLock()){
                        queue.add(Cryptography.encryptRegion(novaRegiao));
                        lock.unlock();
                        contador++;
                        Log.d("Teste", "Sucesso ao adicionar à fila.");
                    }
                } else {
                    Log.d("Teste", "Não foi possível adicionar.");
                }
            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            }
        }).start();
    }

    private Thread canAddBD (Region r1, AtomicBoolean canAdd, AtomicReference<Region> mainRegion) {
        return new Thread(() ->{
            Log.d("Teste", "Iniciando canAddBD");
            Task<QuerySnapshot> task = bd.collection("Regiões").get();
            canAdd.set(true);
            try {
                while (!(task.isComplete() || task.isCanceled()));
                if (task.isSuccessful()) {
                    Log.d("Teste", "Sucesso encontrando Documentos");
                    if (!task.getResult().isEmpty()) {
                        for (QueryDocumentSnapshot document : task.getResult()) {
                            Log.d("Teste", "Lendo documentos do banco de dados...");
                            Region region = Cryptography.decryptRegion((String) document.getData().get("Regiao")); //Função para receber o tipo de objeto

                            if ((region instanceof SubRegion) || (region instanceof RestrictedRegion)) { // Se é sub ou restricted
                                if (!region.permitirAddFila(r1)) {
                                    Log.d("Teste", "Não permite adicionar Região");
                                    canAdd.set(false);
                                }
                            } else {
                                if (!region.permitirAddFila(r1)) {
                                    mainRegion.set(region);
                                }
                            }
                        }
                    }
                } else {
                    Log.d("Teste", "Erro ao encontrar documentos", task.getException());
                }
            } catch (Exception e) {
                throw new RuntimeException(e);
            }
        });
    }

    private Thread canAddFila(Region r1, AtomicBoolean canAdd, AtomicReference<Region> mainRegion) {
        return new Thread(() -> {
            Log.d("Teste", "Iniciando canAddFila");
            canAdd.set(true);
            LinkedList<String> list = (LinkedList<String>) queue; //A fila agora armazena o Json encriptografado.
            for (String strRegion : list) {
                Region r = Cryptography.decryptRegion(strRegion); //Conversão de Json encriptografado para Objeto Region.
                Log.d("Teste", "Lendo Lista de Região");
                if ((r instanceof SubRegion) || (r instanceof RestrictedRegion)) { // Se é sub ou restricted
                    if (!r.permitirAddFila(r1)) {
                        Log.d("Teste", "Não permite adicionar Região");
                        canAdd.set(false);
                    }
                } else {
                    if (!r.permitirAddFila(r1)) {
                        mainRegion.set(r);
                    }
                }
            }
        });
    }

    public String dequeue() {
        String region = null;
        if (lock.tryLock()) {
            region = queue.poll();
            lock.unlock();
        }
        return region;
    }
}