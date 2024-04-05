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
import com.google.firebase.firestore.AggregateQuerySnapshot;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.LinkedList;
import java.util.NoSuchElementException;
import java.util.Objects;
import java.util.Queue;
import java.util.concurrent.locks.ReentrantLock;

public class LocationService {
    ReentrantLock lock = new ReentrantLock();
    private final Queue<Region> queue = new LinkedList<>();
    private int contador = 0;
    private FirebaseFirestore bd = FirebaseFirestore.getInstance();

    public void enqueue(Location item){
        Region regiao = new Region ("Região "+contador, item.getLatitude(), item.getLongitude(), 0);
        if (checaDistancia(regiao)){
            new Thread(() -> { //Thread que enfilera as regiões
                try {
                    lock.lock(); //Trava do semáforo
                    queue.add(regiao);
                    contador++;
                    Log.d("Fila", "Sucesso ao adicionar à fila.");
                } finally {
                    lock.unlock(); //Destravamento do semáforo
                }
            }).start();
        } else {
            Log.d("Fila","Falha ao adicionar à fila.");
        }
    }

    private boolean checaDistancia(Region r1){
        LinkedList<Region> lista = (LinkedList<Region>)queue; //Conversão inversa do tipo da fila para acesso
        for(int i=0; i<lista.size(); i++){
            if (!Utility.permitirAddFila(r1,lista.get(i))){ //Percorre a lista até o primeiro impedimento do boolean
                return false;
            }
        } // Caso não haja região dentro dos 30 metros, ela será adicionada.
        return true;
    }

    private boolean checaDistBD(Region r1) {
        bd.collection("suaColecao").get().addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                QuerySnapshot querySnapshot = task.getResult();
                for (DocumentSnapshot document : querySnapshot) {

                }
            } else {
                // Trate o erro
            }
        });
        return true;
    }

    public Region dequeue() {
        Region aux = null;
        if (lock.tryLock()) { //Se o semáforo ja está travado, será falso.
            try {
                lock.lock();
                aux = queue.remove();
            } catch (NoSuchElementException e) {
                Log.e("QUEUE EXCEPTION", "Deu erro aqui filhote");
            } finally {
                lock.unlock();
            }
        }
        return aux;
    }
}