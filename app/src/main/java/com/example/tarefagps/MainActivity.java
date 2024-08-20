package com.example.tarefagps;

import android.Manifest;
import android.content.pm.PackageManager;
import android.location.Location;
import android.os.Bundle;
import android.os.Looper;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.content.res.AppCompatResources;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import com.example.avancada.Region;
import com.example.avancada.TimeUtil;
import com.example.tarefagps.databinding.ActivityMainBinding;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationCallback;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationResult;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.location.Priority;
import com.google.firebase.firestore.FirebaseFirestore;

import org.osmdroid.api.IMapController;
import org.osmdroid.tileprovider.tilesource.XYTileSource;
import org.osmdroid.util.GeoPoint;
import org.osmdroid.views.overlay.Marker;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Objects;

import android.os.Process;
import android.widget.Toast;


public class MainActivity extends AppCompatActivity {
    private LocationService locationService;
    private Location lastLocation = null;
    private FusedLocationProviderClient fusedLocationClient;
    private ActivityMainBinding binding; //Binding para realizar a relação entre a IU com o código
    private LocationCallback locationCallback;
    private LocationRequest locationRequest;
    private FirebaseFirestore bd = FirebaseFirestore.getInstance(); //Banco de Dados Firestore

    private Coletor coletor = new Coletor(
            new Region("", -21.2538, -45.0059, 0),
            new Region("", -21.2513, -45.0030, 0),
            new Region("", -21.2470, -45.0004, 0),
            new Region("", -21.2416, -44.9972, 0),
            new Region("", -21.2345, -44.9974, 0),
            new Region("", -21.2312, -44.9941, 0)
    );


    @Override
    protected void onCreate(Bundle savedInstanceState) { //Método onCreate: usado ao se iniciar o aplicativo
        super.onCreate(savedInstanceState);

        /*//Teste de núcleos
        setContentView(R.layout.activity_main);
        // Forçar a aplicação a usar apenas o núcleo 0
        int coreNumber = 0; // Escolha o núcleo que você deseja usar
        int mask = 1 << coreNumber; // Cria uma máscara para o núcleo escolhido
        Process.setThreadPriority(Process.myTid(), mask); // Aplica a máscara ao thread atual*/


        fusedLocationClient = LocationServices.getFusedLocationProviderClient(this);
        locationService = new LocationService();
        binding = ActivityMainBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        binding.mapView.setTileSource(new XYTileSource("OSM", 0, 17, 512, ".png",
                new String[]{"https://tile.openstreetmap.de/"}) {

        });

        locationCallback = new LocationCallback() {
            public void onLocationResult(LocationResult locationResult) {
                if (locationResult != null) {
                    binding.latitude.setText(String.format("LAT: %s", locationResult.getLastLocation().getLatitude()));
                    binding.longitude.setText(String.format("LONG: %s", locationResult.getLastLocation().getLongitude()));
                    lastLocation = locationResult.getLastLocation();
                    setMarker(lastLocation);
                    goToPoint(lastLocation);

                    String mensagem = coletor.LocUpdate(lastLocation.getLatitude(), lastLocation.getLongitude());
                    if (!Objects.equals(mensagem, "")) {
                        Toast.makeText(getApplicationContext(),mensagem,Toast.LENGTH_LONG).show();
                    }
                    if (coletor.getExecucao()==10){
                        coletor.imprime();
                    }
                }
            }
        };

        //Criação dos pedidos de localização necessários para o callback
        createLocationRequest();

        //Pedido de Permissão ao usuário do aplicativo
        requestPermissionsIfNecessary(new String[]{
                Manifest.permission.ACCESS_FINE_LOCATION,
                Manifest.permission.ACCESS_COARSE_LOCATION,
                Manifest.permission.INTERNET,
                Manifest.permission.WRITE_EXTERNAL_STORAGE
        });

        setListeners();
    }

    protected void createLocationRequest() {
        locationRequest = new LocationRequest.Builder(500)
                .setIntervalMillis(500)
                .setPriority(Priority.PRIORITY_HIGH_ACCURACY)
                .build();

    }

    @Override
    public void onResume() { //Método onResume: usado ao ativar o aplicativo novamente (pós-segundo plano)
        super.onResume();
        binding.mapView.onResume();
        startLocationUpdates();
        setZoom(15.0);
    }

    private void startLocationUpdates() {
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED || ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
            fusedLocationClient.requestLocationUpdates(locationRequest,
                    locationCallback, Looper.getMainLooper());

        }
    }

    @Override
    public void onPause() { //Método onPause: ao colocar o aplicativo em segundo plano
        super.onPause();
        stopLocationUpdates();
        binding.mapView.onPause();
        TimeUtil.showTime();
    }

    private void stopLocationUpdates() {
        fusedLocationClient.removeLocationUpdates(locationCallback);
    }

    private void setListeners() { //Método setListeners: receptores dos cliques dos botões
        binding.btnEnqueue.setOnClickListener(v -> {
            if (lastLocation != null) {
                locationService.enqueue(lastLocation);
            }
        });

        binding.btnGravar.setOnClickListener(v -> {
            long tini = System.currentTimeMillis(); //Tarefa 5 - Ci //Tarefa 4
            String regiao = locationService.dequeue();
            if (regiao != null){
                HashMap<String, String> map = new HashMap<>(); //O mapa de String agora faz referência ao Json criptografado
                map.put("Regiao",regiao);
                bd.collection("Regiões").add(map).addOnCompleteListener(unused->{
                    long tfin = System.currentTimeMillis(); //Tarefa 5 - Ci // Tarefa 4
                    TimeUtil.addTempo((tfin-tini), 5);
                });
            }
        });
    }

    private void goToPoint(Location location) { //Método goToPoint: Centraliza o mapa para a localização definida
        if (location == null) return;

        IMapController mapController = binding.mapView.getController();
        GeoPoint startPoint = new GeoPoint(location.getLatitude(), location.getLongitude());
        mapController.setCenter(startPoint);
    }

    private void setZoom(double zoom) { //Método setZoom: Define o zoom do mapa
        IMapController mapController = binding.mapView.getController();
        mapController.zoomTo(zoom);
    }

    private void setMarker(Location location) { //Método setMarker: Define o marcador (pin) no mapa
        if (location != null) {
            Marker startMarker;
            if (binding.mapView.getOverlays().isEmpty()) {
                startMarker = new Marker(binding.mapView);
                startMarker.setIcon(AppCompatResources.getDrawable(this, R.drawable.navigation));
                startMarker.setAnchor(Marker.ANCHOR_CENTER, Marker.ANCHOR_CENTER);
                binding.mapView.getOverlays().add(startMarker);
            } else {
                startMarker = (Marker) binding.mapView.getOverlays().get(0);
            }
            GeoPoint startPoint = new GeoPoint(location.getLatitude(), location.getLongitude());
            startMarker.setPosition(startPoint);
        }
    }

    private void requestPermissionsIfNecessary(String[] permissions) { //Método que requisita as permissões
        ArrayList<String> permissionsToRequest = new ArrayList<>();
        for (String permission : permissions) {
            if (ContextCompat.checkSelfPermission(this, permission)
                    != PackageManager.PERMISSION_GRANTED) {
                permissionsToRequest.add(permission);
            }
        }
        if (!permissionsToRequest.isEmpty()) {
            int REQUEST_PERMISSIONS_REQUEST_CODE = 1;
            ActivityCompat.requestPermissions(
                    this,
                    permissionsToRequest.toArray(new String[0]),
                    REQUEST_PERMISSIONS_REQUEST_CODE);
        }
    }

}