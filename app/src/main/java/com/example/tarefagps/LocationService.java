package com.example.tarefagps;

import android.content.Context;
import android.content.pm.PackageManager;
import android.location.Location;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.core.app.ActivityCompat;

import com.example.avancada.Region;
import com.example.avancada.Utility;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.Priority;
import com.google.android.gms.tasks.CancellationToken;
import com.google.android.gms.tasks.CancellationTokenSource;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.OnTokenCanceledListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.AggregateQuerySnapshot;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.LinkedList;
import java.util.NoSuchElementException;
import java.util.Objects;
import java.util.Queue;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.locks.ReentrantLock;

public class LocationService {
    ReentrantLock lock = new ReentrantLock();
    private final Queue<Region> queue = new LinkedList<>();
    private int contador = 0;
    private FirebaseFirestore bd = FirebaseFirestore.getInstance();

    public void enqueue(Location item){
        Region regiao = new Region ("Região "+contador, item.getLatitude(), item.getLongitude(), 0);
        AtomicBoolean boolFila = new AtomicBoolean(false);
        AtomicBoolean boolBancoD = new AtomicBoolean(false);

        Thread verFila = canAddFila(regiao, boolFila);
        Thread verBD = canAddBD(regiao, boolBancoD);

        new Thread(() -> {
            try {
                verBD.start();
                verFila.start();
                verBD.join();
                verFila.join();

                if(boolFila.get() && boolBancoD.get()) {
                    if (lock.tryLock()){
                        queue.add(regiao);
                        lock.unlock();
                        Log.d("Teste", "Lock está fechado.");
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

    private Thread canAddBD (Region r1, AtomicBoolean canAdd) {
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
                            Region region = new Region(document.getData());
                            if (!Utility.permitirAddFila(region, r1)) {
                                canAdd.set(false);
                                break;
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

    private Thread canAddFila(Region r1, AtomicBoolean canAdd) {
        return new Thread(() -> {
            Log.d("Teste", "Iniciando canAddFila");
            canAdd.set(true);
            LinkedList<Region> list = (LinkedList<Region>) queue;
            for (Region r : list) {
                Log.d("Teste", "Lendo Lista de Região");
                if (!Utility.permitirAddFila(r1, r)) {
                    Log.d("Teste", "Não permite adicionar");
                    canAdd.set(false);
                }
            }
        });
    }

    public Region dequeue() {
        Region region = null;
        if (lock.tryLock()) {
            region = queue.poll();
            lock.unlock();
            Log.d("Teste", "Lock está aberto.");
        }
        return region;
    }
}