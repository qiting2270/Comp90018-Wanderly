package com.example.wanderly;

import android.content.Context;
import android.hardware.SensorManager;
import android.os.Bundle;
import android.os.Looper;
import android.view.View;
import android.widget.ImageButton;
import android.widget.ImageView;

import androidx.activity.EdgeToEdge;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.camera.core.CameraSelector;
import androidx.camera.core.Preview;
import androidx.camera.view.PreviewView;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.core.content.ContextCompat;
import androidx.core.app.ActivityCompat;
import androidx.camera.lifecycle.ProcessCameraProvider;
import android.Manifest;
import android.content.pm.PackageManager;
import android.location.Location;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationCallback;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationResult;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.common.util.concurrent.ListenableFuture;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import java.util.concurrent.ExecutionException;

public class ARActivity extends AppCompatActivity {


    private static final int CAMERA_REQUEST_CODE = 100;
    private static final int LOCATION_REQUEST_CODE = 101;
    private static final float ARRIVAL_THRESHOLD = 20;

    private FusedLocationProviderClient fusedLocationClient;
    private SensorManager sensorManager;
    private ImageView arrow;
    private ImageView arrivedIcon;
    private TextView distanceText;
    private LinearLayout arrivedLayout;
    private PreviewView previewView;
    private Location targetLocation;
    private float bearingToTarget = 0f;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_aractivity);

        previewView = findViewById(R.id.previewView);
        arrow = findViewById(R.id.arrow);
        distanceText = findViewById(R.id.distanceText);
        arrivedLayout = findViewById(R.id.arrivedLayout);

        // !!!目的地 假设!!!
        targetLocation = new Location("");
        targetLocation.setLatitude(37.7749);  // Example: Latitude of San Francisco
        targetLocation.setLongitude(-122.4194); // Example: Longitude of San Francisco


        // Request camera permissions and start camera
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.CAMERA}, CAMERA_REQUEST_CODE);
        } else {
            startCamera();
        }

        // show arrived page
//        arrow.setVisibility(View.GONE);
//        distanceText.setVisibility(View.GONE);
//        arrivedLayout.setVisibility(View.VISIBLE);


        // Initialize location services and sensors
        fusedLocationClient = LocationServices.getFusedLocationProviderClient(this);
        getLocation();
        sensorManager = (SensorManager) getSystemService(Context.SENSOR_SERVICE);
        startSensors();

        // Back button setup
        ImageButton backButton = findViewById(R.id.backButton);
        backButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        //!!!arrived page buttons!!!
        findViewById(R.id.closeButton).setOnClickListener(v -> finish()); // Close the activity
        findViewById(R.id.viewDetailsButton).setOnClickListener(v -> showStopDetails()); // View stop details
    }


    private void startCamera() {
        ListenableFuture<ProcessCameraProvider> cameraProviderFuture = ProcessCameraProvider.getInstance(this);
        cameraProviderFuture.addListener(() -> {
            try {
                ProcessCameraProvider cameraProvider = cameraProviderFuture.get();
                Preview preview = new Preview.Builder().build();
                CameraSelector cameraSelector = CameraSelector.DEFAULT_BACK_CAMERA;

                preview.setSurfaceProvider(previewView.getSurfaceProvider());

                cameraProvider.bindToLifecycle(this, cameraSelector, preview);
            } catch (ExecutionException | InterruptedException e) {
                e.printStackTrace();
            }
        }, ContextCompat.getMainExecutor(this));
    }

    private void getLocation() {
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, LOCATION_REQUEST_CODE);
        } else {
            fusedLocationClient.getLastLocation()
                    .addOnSuccessListener(this, new OnSuccessListener<Location>() {
                        @Override
                        public void onSuccess(Location location) {
                            if (location != null) {
                                calculateBearingToTarget(location);
                                updateDistance(location);
                            } else {
                                requestNewLocationData();
                            }
                        }
                    });
        }
    }


    private void requestNewLocationData() {
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, LOCATION_REQUEST_CODE);
        } else {
            // Permission is granted, update locations
            LocationRequest locationRequest = LocationRequest.create();
            locationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
            locationRequest.setInterval(5000); // 5 seconds
            locationRequest.setFastestInterval(2000); // 2 seconds

            fusedLocationClient.requestLocationUpdates(locationRequest, locationCallback, Looper.getMainLooper());
        }
    }

    private LocationCallback locationCallback = new LocationCallback() {
        @Override
        public void onLocationResult(LocationResult locationResult) {
            Location location = locationResult.getLastLocation();
            if (location != null) {
                calculateBearingToTarget(location);
                updateDistance(location);
            }
        }
    };

    // Calculate the bearing from the device's current location to the target location
    private void calculateBearingToTarget(Location currentLocation) {
        if (currentLocation != null && targetLocation != null) {
            bearingToTarget = currentLocation.bearingTo(targetLocation);
        }
    }

    // Update the distance to the target location and check if the user has arrived
    private void updateDistance(Location currentLocation) {
        float distanceInMeters = currentLocation.distanceTo(targetLocation);
        distanceText.setText(String.format("Distance: %.1f km", distanceInMeters / 1000));

        if (distanceInMeters <= ARRIVAL_THRESHOLD) {
            arrow.setVisibility(View.GONE);
            distanceText.setVisibility(View.GONE);
            arrivedLayout.setVisibility(View.VISIBLE);
        }
    }

    // Handle permission requests result
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == LOCATION_REQUEST_CODE) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                getLocation();
            } else {
                Toast.makeText(this, "Location permission is required to get GPS data", Toast.LENGTH_SHORT).show();
            }
        }
    }

    private void startSensors() {
        Sensor accelerometer = sensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);
        Sensor magnetometer = sensorManager.getDefaultSensor(Sensor.TYPE_MAGNETIC_FIELD);
        SensorEventListener sensorEventListener = new SensorEventListener() {
            float[] gravity;
            float[] geomagnetic;

            @Override
            public void onSensorChanged(SensorEvent event) {
                if (event.sensor.getType() == Sensor.TYPE_ACCELEROMETER)
                    gravity = event.values;
                if (event.sensor.getType() == Sensor.TYPE_MAGNETIC_FIELD)
                    geomagnetic = event.values;
                if (gravity != null && geomagnetic != null) {
                    float R[] = new float[9];
                    float I[] = new float[9];
                    boolean success = SensorManager.getRotationMatrix(R, I, gravity, geomagnetic);
                    if (success) {
                        float orientation[] = new float[3];
                        SensorManager.getOrientation(R, orientation);
                        float azimuth = orientation[0];// Azimuth in radians

                        // Convert azimuth to degrees
                        float azimuthInDegrees = (float) Math.toDegrees(azimuth);
                        if (azimuthInDegrees < 0) {
                            azimuthInDegrees += 360;
                        }

                        // Rotate arrow to point towards the target location
                        arrow.setRotation(bearingToTarget - azimuthInDegrees);
                    }
                }
            }

            @Override
            public void onAccuracyChanged(Sensor sensor, int accuracy) {}
        };
        sensorManager.registerListener(sensorEventListener, accelerometer, SensorManager.SENSOR_DELAY_UI);
        sensorManager.registerListener(sensorEventListener, magnetometer, SensorManager.SENSOR_DELAY_UI);
    }

    // !!!! Handle showing stop details (replace with actual implementation) !!!!
    private void showStopDetails() {
        // This can open a new activity or show more information about the stop
        Toast.makeText(this, "Viewing stop details...", Toast.LENGTH_SHORT).show();
    }
}