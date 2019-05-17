package com.example.accessfinelocaiton;

import android.Manifest;
import android.content.pm.PackageManager;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationCallback;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationResult;

import java.io.IOException;
import java.util.List;
import java.util.Locale;

public class MainActivity extends AppCompatActivity {

    public static int MY_PERMISSIONS_REQUEST_ACCESS_FINE_LOCATION = 1;
    private FusedLocationProviderClient fusedLocationProviderClient;
    private LocationCallback locationCallback;
    private LocationRequest locationRequest;
    TextView tvLocation;
    TextView tvLocationStr;
    private boolean requestingLocationUpdate = true;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        tvLocation = findViewById(R.id.tv_location);
        tvLocationStr = findViewById(R.id.tv_location_str);

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
                    tvLocationStr.setText(getCompleteAddressString(lat, lon));
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

    private String getCompleteAddressString(double LATITUDE, double LONGITUDE) {
        Geocoder geocoder;
        List<Address> addresses = null;
        geocoder = new Geocoder(this, Locale.getDefault());
        String addr = "No Address Found";

        try {
            addresses = geocoder.getFromLocation(LATITUDE, LONGITUDE, 1); // Here 1 represent max location result to returned, by documents it recommended 1 to 5
            String address = addresses.get(0).getAddressLine(0); // If any additional address line present than only, check with max available address lines by getMaxAddressLineIndex()
            // String city = addresses.get(0).getLocality();
            // String state = addresses.get(0).getAdminArea();
            // String country = addresses.get(0).getCountryName();
            // String postalCode = addresses.get(0).getPostalCode();
            addr = address;
        } catch (IOException ex) {
            Toast.makeText(this, "Exception", Toast.LENGTH_SHORT);
        }


        return addr;
    }
}