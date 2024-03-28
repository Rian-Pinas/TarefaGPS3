package com.example.tarefagps;

import static androidx.core.location.LocationManagerCompat.requestLocationUpdates;

import android.Manifest;
import android.content.pm.PackageManager;
import android.location.Location;
import android.os.Bundle;
import android.os.Looper;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.content.res.AppCompatResources;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import com.example.tarefagps.databinding.ActivityMainBinding;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationCallback;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationResult;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.location.Priority;

import org.osmdroid.api.IMapController;
import org.osmdroid.tileprovider.tilesource.XYTileSource;
import org.osmdroid.util.GeoPoint;
import org.osmdroid.views.overlay.Marker;

import java.util.ArrayList;


public class MainActivity extends AppCompatActivity {
    private LocationService locationService;
    private Location lastLocation = null;
    private FusedLocationProviderClient fusedLocationClient;
    private ActivityMainBinding binding; //Binding para realizar a relação entre a IU com o código
    private LocationCallback locationCallback;
    private LocationRequest locationRequest;

    @Override
    protected void onCreate(Bundle savedInstanceState) { //Método onCreate: usado ao se iniciar o aplicativo
        super.onCreate(savedInstanceState);
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
                }
            }
        };

        //Criação dos pedidos de localização necessários para o callback
        createLocationRequest();

        //Pedido de Permissão ao usuário do aplicativo
        requestPermissionsIfNecessary(new String[]{
                android.Manifest.permission.ACCESS_FINE_LOCATION,
                android.Manifest.permission.ACCESS_COARSE_LOCATION,
                Manifest.permission.INTERNET,
                android.Manifest.permission.WRITE_EXTERNAL_STORAGE
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