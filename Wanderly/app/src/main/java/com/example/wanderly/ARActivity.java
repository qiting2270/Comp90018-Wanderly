//package com.example.myapplication;
//
//import android.Manifest;
//import android.content.pm.PackageManager;
//import android.location.Geocoder;
//import android.location.Location;
//import android.os.Bundle;
//import android.widget.Button;
//import android.widget.EditText;
//import android.widget.Toast;
//
//import androidx.annotation.NonNull;
//import androidx.appcompat.app.AppCompatActivity;
//import androidx.core.app.ActivityCompat;
//import androidx.core.content.ContextCompat;
//
//import com.example.myapplication.databinding.ActivityAractivityBinding;
//import com.google.android.gms.location.FusedLocationProviderClient;
//import com.google.android.gms.location.LocationServices;
//import com.google.android.gms.maps.CameraUpdateFactory;
//import com.google.android.gms.maps.GoogleMap;
//import com.google.android.gms.maps.OnMapReadyCallback;
//import com.google.android.gms.maps.SupportMapFragment;
//import com.google.android.gms.maps.model.LatLng;
//import com.google.android.gms.maps.model.MarkerOptions;
//
//import java.io.IOException;
//
//public class ARActivity extends AppCompatActivity implements OnMapReadyCallback {
//
//    private GoogleMap mMap;
//    private ActivityAractivityBinding binding;
//    private FusedLocationProviderClient fusedLocationClient;
//    private EditText latitudeInput;
//    private EditText longitudeInput;
//    private Button showStreetViewButton;
//
//    private static final int LOCATION_PERMISSION_REQUEST_CODE = 1;
//
//    @Override
//    protected void onCreate(Bundle savedInstanceState) {
//        super.onCreate(savedInstanceState);
//
//        binding = ActivityAractivityBinding.inflate(getLayoutInflater());
//        setContentView(binding.getRoot());
//
//        // 初始化输入框和按钮
//        latitudeInput = findViewById(R.id.latitude_input);
//        longitudeInput = findViewById(R.id.longitude_input);
//        showStreetViewButton = findViewById(R.id.show_street_view_button);
//
//        // 获取位置服务客户端
//        fusedLocationClient = LocationServices.getFusedLocationProviderClient(this);
//
//        // 初始化 SupportMapFragment 并设置异步回调
//        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
//                .findFragmentById(R.id.map);
//        if (mapFragment != null) {
//            mapFragment.getMapAsync(this);
//        }
//
//        // 设置显示街景按钮的点击事件
//        showStreetViewButton.setOnClickListener(v -> {
//            String latStr = latitudeInput.getText().toString();
//            String lonStr = longitudeInput.getText().toString();
//            if (!latStr.isEmpty() && !lonStr.isEmpty()) {
//                try {
//                    double latitude = Double.parseDouble(latStr);
//                    double longitude = Double.parseDouble(lonStr);
//                    LatLng latLng = new LatLng(latitude, longitude);
//                    updateLocation(latLng);
//                } catch (NumberFormatException e) {
//                    Toast.makeText(this, "Invalid latitude or longitude", Toast.LENGTH_SHORT).show();
//                }
//            } else {
//                Toast.makeText(this, "Please enter both latitude and longitude", Toast.LENGTH_SHORT).show();
//            }
//        });
//    }
//
//    @Override
//    public void onMapReady(@NonNull GoogleMap googleMap) {
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
//    }
//
//    // 根据用户输入的经纬度更新位置
//    private void updateLocation(LatLng latLng) {
//        mMap.clear();
//        mMap.addMarker(new MarkerOptions().position(latLng).title("Selected Location"));
//        mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(latLng, 15f));
//    }
//
//    // 获取设备当前位置并显示其地址
//    private void getDeviceLocation() {
//        try {
//            fusedLocationClient.getLastLocation().addOnSuccessListener(this, location -> {
//                if (location != null) {
//                    LatLng currentLatLng = new LatLng(location.getLatitude(), location.getLongitude());
//
//                    // 使用 Geocoder 获取地址信息
//                    Geocoder geocoder = new Geocoder(this);
//                    try {
//                        String addressText = geocoder.getFromLocation(location.getLatitude(), location.getLongitude(), 1)
//                                .get(0).getAddressLine(0);
//
//                        // 在当前位置添加标记并显示地址信息
//                        mMap.addMarker(new MarkerOptions().position(currentLatLng).title("Current Location").snippet(addressText));
//                        mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(currentLatLng, 15f));
//                    } catch (IOException e) {
//                        e.printStackTrace();
//                    }
//                } else {
//                    Toast.makeText(this, "Unable to find current location", Toast.LENGTH_SHORT).show();
//                }
//            });
//        } catch (SecurityException e) {
//            e.printStackTrace();
//        }
//    }
//
//    // 处理权限请求结果
//    @Override
//    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
//        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
//        if (requestCode == LOCATION_PERMISSION_REQUEST_CODE) {
//            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
//                // 权限被授予，启用位置功能
//                if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
//                    mMap.setMyLocationEnabled(true);
//                    getDeviceLocation();
//                }
//            } else {
//                // 权限被拒绝，给出反馈
//                Toast.makeText(this, "Location permission denied", Toast.LENGTH_SHORT).show();
//            }
//        }
//    }
//}
