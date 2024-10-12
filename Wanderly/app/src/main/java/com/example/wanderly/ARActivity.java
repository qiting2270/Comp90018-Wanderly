package com.example.wanderly;

import android.content.Context;
import android.hardware.SensorManager;
import android.os.Bundle;
import android.os.Looper;
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
import android.widget.Toast;

import java.util.concurrent.ExecutionException;

public class ARActivity extends AppCompatActivity {


    private static final int CAMERA_REQUEST_CODE = 100;
    private static final int LOCATION_REQUEST_CODE = 101;

    private FusedLocationProviderClient fusedLocationClient;
    private SensorManager sensorManager;
    private ImageView arrow;
    private PreviewView previewView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_aractivity);

        previewView = findViewById(R.id.previewView);
        arrow = findViewById(R.id.arrow);

        // 检查并请求相机权限
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.CAMERA}, CAMERA_REQUEST_CODE);
        } else {
            // 初始化相机
            startCamera();
        }


        // 初始化位置服务
        fusedLocationClient = LocationServices.getFusedLocationProviderClient(this);
        getLocation();

        // 初始化方向传感器
        sensorManager = (SensorManager) getSystemService(Context.SENSOR_SERVICE);
        startSensors();
    }


    private void startCamera() {
        // 相机的启动逻辑
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

//    private void getLocation() {
//
//        // 检查并请求GPS权限
//        if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
//            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, LOCATION_REQUEST_CODE);
//        }
//
//        // 获取GPS定位
//        fusedLocationClient.getLastLocation()
//                .addOnSuccessListener(this, new OnSuccessListener<Location>() {
//                    @Override
//                    public void onSuccess(Location location) {
//                        if (location != null) {
//                            // 处理位置数据
//                        }
//                    }
//                });
//    }

    private void getLocation() {
        // 检查GPS权限
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, LOCATION_REQUEST_CODE);
        } else {
            // 获取GPS定位
            fusedLocationClient.getLastLocation()
                    .addOnSuccessListener(this, new OnSuccessListener<Location>() {
                        @Override
                        public void onSuccess(Location location) {
                            if (location != null) {
                                // 处理位置数据
                                double latitude = location.getLatitude();
                                double longitude = location.getLongitude();
                                // 使用经纬度进行导航或其他操作
                            } else {
                                // 如果 location 为 null，考虑请求位置更新
                                requestNewLocationData();
                            }
                        }
                    });
        }
    }

    // 请求实时位置更新
    private void requestNewLocationData() {
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            // Request the permission if it hasn't been granted
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, LOCATION_REQUEST_CODE);
        } else {
            // Permission is already granted, proceed with location updates
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
                double latitude = location.getLatitude();
                double longitude = location.getLongitude();
                // 使用经纬度进行操作
            }
        }
    };

    // 处理权限请求结果
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == LOCATION_REQUEST_CODE) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                // 用户授予了权限，重新调用获取位置的方法
                getLocation();
            } else {
                // 用户拒绝了权限，显示提示信息
                Toast.makeText(this, "Location permission is required to get GPS data", Toast.LENGTH_SHORT).show();
            }
        }
    }

    private void startSensors() {
        // 开启方向传感器
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
                        float azimuth = orientation[0]; // 设备的朝向

                        // 计算箭头的旋转角度
                        arrow.setRotation((float) Math.toDegrees(azimuth));
                    }
                }
            }

            @Override
            public void onAccuracyChanged(Sensor sensor, int accuracy) {}
        };
        sensorManager.registerListener(sensorEventListener, accelerometer, SensorManager.SENSOR_DELAY_UI);
        sensorManager.registerListener(sensorEventListener, magnetometer, SensorManager.SENSOR_DELAY_UI);
    }
}