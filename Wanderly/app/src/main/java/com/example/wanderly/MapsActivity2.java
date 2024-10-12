package com.example.wanderly;

import android.Manifest;
import android.content.pm.PackageManager;
import android.location.Address;
import android.location.Geocoder;
import android.os.Bundle;
import android.util.Log;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.FragmentActivity;

import com.example.wanderly.databinding.ActivityMaps2Binding;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;

import java.io.IOException;
import java.util.List;
import java.util.Locale;

public class MapsActivity2 extends FragmentActivity implements OnMapReadyCallback {

    private GoogleMap mMap;
    private ActivityMaps2Binding binding;
    private FusedLocationProviderClient fusedLocationClient;
    private EditText latitudeInput;
    private EditText longitudeInput;
    private Button showStreetViewButton;

    private static final int LOCATION_PERMISSION_REQUEST_CODE = 1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        binding = ActivityMaps2Binding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        // 初始化输入框和按钮
        latitudeInput = findViewById(R.id.latitude_input);
        longitudeInput = findViewById(R.id.longitude_input);
        showStreetViewButton = findViewById(R.id.show_street_view_button);

        // 获取位置服务客户端
        fusedLocationClient = LocationServices.getFusedLocationProviderClient(this);

        // 初始化 SupportMapFragment 并设置异步回调
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);

        // 设置显示街景按钮的点击事件
        showStreetViewButton.setOnClickListener(v -> {
            String latStr = latitudeInput.getText().toString();
            String lonStr = longitudeInput.getText().toString();
            if (!latStr.isEmpty() && !lonStr.isEmpty()) {
                try {
                    double latitude = Double.parseDouble(latStr);
                    double longitude = Double.parseDouble(lonStr);
                    LatLng latLng = new LatLng(latitude, longitude);
                    updateLocation(latLng);
                } catch (NumberFormatException e) {
                    Toast.makeText(MapsActivity2.this, "Invalid latitude or longitude", Toast.LENGTH_SHORT).show();
                }
            } else {
                Toast.makeText(MapsActivity2.this, "Please enter both latitude and longitude", Toast.LENGTH_SHORT).show();
            }
        });
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
//        mMap = googleMap;
//
//        // 启用地图的 UI 控件（放大缩小按钮）
//        mMap.getUiSettings().setZoomControlsEnabled(true);
//
//        // 检查并请求位置权限
//        if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
//            // 启用地图的当前位置功能
//            mMap.setMyLocationEnabled(true);
//            getDeviceLocation();
//        } else {
//            // 请求权限
//            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, LOCATION_PERMISSION_REQUEST_CODE);
//        }


        mMap = googleMap;

        // 启用地图的 UI 控件（放大缩小按钮）
        mMap.getUiSettings().setZoomControlsEnabled(true);

        // 检查并请求位置权限
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
            // 启用地图的当前位置功能
            mMap.setMyLocationEnabled(true);
            getDeviceLocation();
        } else {
            // 请求权限
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, LOCATION_PERMISSION_REQUEST_CODE);
        }

        // 设置地图点击监听器，返回点击位置的经纬度
        mMap.setOnMapClickListener(latLng -> {
            double latitude = latLng.latitude;
            double longitude = latLng.longitude;
            Log.d("MapsActivity2", "Latitude: " + latitude + ", Longitude: " + longitude);
            Toast.makeText(MapsActivity2.this, "Latitude: " + latitude + ", Longitude: " + longitude, Toast.LENGTH_LONG).show();

            // 可选：点击后添加标记
            mMap.clear();  // 清除之前的标记
            mMap.addMarker(new MarkerOptions().position(latLng).title("Clicked Location"));
            mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(latLng, 15f));
        });
    }

    // 根据用户输入的经纬度更新位置
    private void updateLocation(LatLng latLng) {
        mMap.clear();
        mMap.addMarker(new MarkerOptions().position(latLng).title("Selected Location"));
        mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(latLng, 15f));
    }

    // 获取设备当前位置并显示其地址
    private void getDeviceLocation() {
        try {
            fusedLocationClient.getLastLocation().addOnSuccessListener(location -> {
                if (location != null) {
                    LatLng currentLatLng = new LatLng(location.getLatitude(), location.getLongitude());

                    // 使用 Geocoder 获取地址信息
                    Geocoder geocoder = new Geocoder(this, Locale.getDefault());
                    try {
                        List<Address> addresses = geocoder.getFromLocation(location.getLatitude(), location.getLongitude(), 1);
                        String addressText = addresses != null && !addresses.isEmpty() ? addresses.get(0).getAddressLine(0) : "Unknown Address";

                        // 在当前位置添加标记并显示地址信息
                        mMap.addMarker(new MarkerOptions().position(currentLatLng).title("Current Location").snippet(addressText));
                        mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(currentLatLng, 15f));
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                } else {
                    Toast.makeText(MapsActivity2.this, "Unable to find current location", Toast.LENGTH_SHORT).show();
                }
            });
        } catch (SecurityException e) {
            e.printStackTrace();
        }
    }

    // 处理权限请求结果
    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == LOCATION_PERMISSION_REQUEST_CODE) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                // 权限被授予，启用位置功能
                if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
                    mMap.setMyLocationEnabled(true);
                    getDeviceLocation();
                }
            } else {
                // 权限被拒绝，给出反馈
                Toast.makeText(this, "Location permission denied", Toast.LENGTH_SHORT).show();
            }
        }
    }
}
