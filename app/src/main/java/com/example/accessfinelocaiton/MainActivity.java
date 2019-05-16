package com.example.accessfinelocaiton;

import android.Manifest;
import android.content.pm.PackageManager;
import android.location.Location;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.TextView;

import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationCallback;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationResult;

public class MainActivity extends AppCompatActivity {

    public static int MY_PERMISSIONS_REQUEST_ACCESS_FINE_LOCATION = 1;
    private FusedLocationProviderClient fusedLocationProviderClient;
    private LocationCallback locationCallback;
    private LocationRequest locationRequest;
    TextView tvLocation;
    private boolean requestingLocationUpdate = true;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        tvLocation = findViewById(R.id.tv_location);

        fusedLocationProviderClient = new FusedLocationProviderClient(this);
        locationCallback = new LocationCallback() {
            @Override
            public void onLocationResult(LocationResult locationResult) {
                if(locationResult == null) {
                    return;
                }
                for (Location location : locationResult.getLocations()) {
                    Double lat = location.getLatitude();
                    Double lon = location.getLongitude();
                    tvLocation.setText("Lat: " + lat + ", Lon: " + lon);
                }
            }
        };

        locationRequest = new LocationRequest();
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (requestingLocationUpdate) {
            startLocationUpdate();
        }
    }

    private void startLocationUpdate() {
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION)
                != PackageManager.PERMISSION_GRANTED) {
            // Requesting location permission
            ActivityCompat.requestPermissions(this,
                    new String[]{Manifest.permission.ACCESS_FINE_LOCATION},
                    MY_PERMISSIONS_REQUEST_ACCESS_FINE_LOCATION);
        } else {
            fusedLocationProviderClient.requestLocationUpdates(locationRequest, locationCallback, null);
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
            if (requestCode == MY_PERMISSIONS_REQUEST_ACCESS_FINE_LOCATION) {
                if(grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_DENIED) {
                    // Requesting location permission
                    ActivityCompat.requestPermissions(this,
                            new String[]{Manifest.permission.ACCESS_FINE_LOCATION},
                            MY_PERMISSIONS_REQUEST_ACCESS_FINE_LOCATION);
                } else {
                    fusedLocationProviderClient.requestLocationUpdates(locationRequest, locationCallback, null);
                }
            }
    }
}
