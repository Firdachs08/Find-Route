package com.firda.route;

import androidx.annotation.Nullable;
import android.Manifest;
import android.widget.EditText;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import com.example.getpresensi.R;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapView;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.tasks.OnSuccessListener;

import java.io.IOException;
import java.util.List;
import java.util.Locale;

public class MainActivity extends AppCompatActivity implements OnMapReadyCallback {

    private static final int LOCATION_PERMISSION_REQUEST_CODE = 1001;
    private static final int PROFILE_EDIT_REQUEST_CODE = 101;
    private MapView mapView;
    private GoogleMap googleMap;
    private FusedLocationProviderClient fusedLocationClient;
    private TextView textViewWelcome;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        textViewWelcome = findViewById(R.id.textViewWelcome);
        String username = getIntent().getStringExtra("fullName"); // Mengambil nama pengguna dari intent
        textViewWelcome.setText("Welcome, " + username + "!");

        fusedLocationClient = LocationServices.getFusedLocationProviderClient(this);

        mapView = findViewById(R.id.mapView);
        mapView.onCreate(savedInstanceState);
        mapView.getMapAsync(this);

        Button buttonHadirSekarang = findViewById(R.id.buttonCariRute);
        buttonHadirSekarang.setOnClickListener(new View.OnClickListener() {
            // Dalam metode onClick untuk buttonHadirSekarang
            @Override
            public void onClick(View v) {
                // Mendapatkan nama pengguna dari intent
                String username = getIntent().getStringExtra("fullName");

                // Mendeklarasikan final variabel yang dapat diakses dari dalam inner class
                final String[] currentLocation = {"Lokasi terkini belum didapatkan"};

                // Cek apakah izin lokasi sudah diberikan sebelumnya
                if (ContextCompat.checkSelfPermission(MainActivity.this, Manifest.permission.ACCESS_FINE_LOCATION)
                        == PackageManager.PERMISSION_GRANTED) {
                    // Mendapatkan lokasi terkini
                    fusedLocationClient.getLastLocation()
                            .addOnSuccessListener(MainActivity.this, new OnSuccessListener<Location>() {
                                @Override
                                public void onSuccess(Location location) {
                                    if (location != null) {
                                        // Lokasi berhasil didapatkan
                                        double latitude = location.getLatitude();
                                        double longitude = location.getLongitude();

                                        // Mendapatkan alamat tujuan dari EditText
                                        EditText editTextTujuan = findViewById(R.id.editTextRoute);
                                        String alamatTujuan = editTextTujuan.getText().toString().trim();

                                        // Mendapatkan koordinat tujuan dari alamat menggunakan Geocoder
                                        if (!alamatTujuan.isEmpty()) {
                                            try {
                                                Geocoder geocoder = new Geocoder(MainActivity.this, Locale.getDefault());
                                                List<Address> addresses = geocoder.getFromLocationName(alamatTujuan, 1);
                                                if (addresses != null && !addresses.isEmpty()) {
                                                    Address address = addresses.get(0);
                                                    double latitudeTujuan = address.getLatitude();
                                                    double longitudeTujuan = address.getLongitude();

                                                    // Membuat URI intent untuk membuka Google Maps dengan arah rute dari lokasi saat ini ke lokasi tujuan
                                                    Uri gmmIntentUri = Uri.parse("google.navigation:q=" + latitudeTujuan + "," + longitudeTujuan);

                                                    // Membuat Intent untuk membuka Google Maps
                                                    Intent mapIntent = new Intent(Intent.ACTION_VIEW, gmmIntentUri);
                                                    mapIntent.setPackage("com.google.android.apps.maps");

                                                    // Memeriksa apakah terdapat aplikasi Google Maps yang terpasang di perangkat
                                                    if (mapIntent.resolveActivity(getPackageManager()) != null) {
                                                        // Buka Google Maps
                                                        startActivity(mapIntent);
                                                    } else {
                                                        // Aplikasi Google Maps tidak terpasang, beri tahu pengguna
                                                        Toast.makeText(MainActivity.this, "Aplikasi Google Maps tidak ditemukan.", Toast.LENGTH_SHORT).show();
                                                    }
                                                } else {
                                                    // Alamat tujuan tidak ditemukan
                                                    Toast.makeText(MainActivity.this, "Alamat tujuan tidak ditemukan.", Toast.LENGTH_SHORT).show();
                                                }
                                            } catch (IOException e) {
                                                e.printStackTrace();
                                                // Kesalahan saat menggunakan Geocoder
                                                Toast.makeText(MainActivity.this, "Error: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                                            }
                                        } else {
                                            // Alamat tujuan tidak boleh kosong
                                            Toast.makeText(MainActivity.this, "Alamat tujuan tidak boleh kosong.", Toast.LENGTH_SHORT).show();
                                        }
                                    } else {
                                        // Tidak dapat mendapatkan lokasi terkini
                                        Toast.makeText(MainActivity.this, "Lokasi terkini tidak tersedia.", Toast.LENGTH_SHORT).show();
                                    }
                                }
                            });
                } else {
                    // Izin lokasi belum diberikan
                    ActivityCompat.requestPermissions(MainActivity.this,
                            new String[]{Manifest.permission.ACCESS_FINE_LOCATION},
                            LOCATION_PERMISSION_REQUEST_CODE);
                }
            }
        });



        // Ketika tombol Cek Profile diklik
        Button buttonCekProfile = findViewById(R.id.buttonCekProfile);
        buttonCekProfile.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String fullName = getIntent().getStringExtra("fullName");

                // Membuat instance DatabaseHelper
                DatabaseHelper dbHelper = new DatabaseHelper(MainActivity.this);

                // Memanggil fungsi untuk mencari username berdasarkan fullName
                String username = dbHelper.getUsernameByFullName(fullName);

                // Jika username ditemukan, kirim ke ProfileActivity
                if (username != null) {
                    Intent intent = new Intent(MainActivity.this, ProfileActivity.class);
                    intent.putExtra("username", username);
                    intent.putExtra("fullName", fullName);
                    startActivity(intent);
                } else {
                    // Jika tidak ditemukan, beri pesan kesalahan
                    Toast.makeText(MainActivity.this, "Username not found", Toast.LENGTH_SHORT).show();
                }
            }
        });




        // Minta izin lokasi jika belum diberikan
        if (ContextCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_FINE_LOCATION)
                != android.content.pm.PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this,
                    new String[]{android.Manifest.permission.ACCESS_FINE_LOCATION},
                    LOCATION_PERMISSION_REQUEST_CODE);
        } else {
            // Jika izin sudah diberikan, dapatkan lokasi pengguna
            getLastLocation();
        }
    }


    private void getLastLocation() {
        // Cek apakah izin lokasi sudah diberikan sebelumnya
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION)
                == PackageManager.PERMISSION_GRANTED) {
            // Izin sudah diberikan, coba mendapatkan lokasi terkini
            fusedLocationClient.getLastLocation()
                    .addOnSuccessListener(this, new OnSuccessListener<Location>() {
                        @Override
                        public void onSuccess(Location location) {
                            if (location != null) {
                                // Lokasi berhasil didapatkan, tampilkan di peta
                                LatLng userLocation = new LatLng(location.getLatitude(), location.getLongitude());
                                googleMap.addMarker(new MarkerOptions().position(userLocation).title("You are here"));
                                googleMap.moveCamera(CameraUpdateFactory.newLatLngZoom(userLocation, 15));
                            } else {
                                // Tidak dapat mendapatkan lokasi terkini
                                Toast.makeText(MainActivity.this, "Location not available", Toast.LENGTH_SHORT).show();
                            }
                        }
                    });
        } else {
            // Izin belum diberikan, minta izin lokasi kepada pengguna
            ActivityCompat.requestPermissions(this,
                    new String[]{Manifest.permission.ACCESS_FINE_LOCATION},
                    LOCATION_PERMISSION_REQUEST_CODE);
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == PROFILE_EDIT_REQUEST_CODE && resultCode == RESULT_OK) {
            // Mengambil data nama lengkap yang diperbarui dari intent
            String updatedFullName = data.getStringExtra("updatedFullName");

            // Memperbarui tampilan nama pengguna
            textViewWelcome.setText("Welcome, " + updatedFullName + "!");
        }
    }


    @Override
    public void onMapReady(GoogleMap googleMap) {
        this.googleMap = googleMap;
    }

    @Override
    protected void onResume() {
        super.onResume();
        mapView.onResume();
    }

    @Override
    protected void onPause() {
        super.onPause();
        mapView.onPause();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        mapView.onDestroy();
    }

    @Override
    public void onLowMemory() {
        super.onLowMemory();
        mapView.onLowMemory();
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == LOCATION_PERMISSION_REQUEST_CODE) {
            if (grantResults.length > 0 && grantResults[0] == android.content.pm.PackageManager.PERMISSION_GRANTED) {
                // Izin diberikan, dapatkan lokasi terkini
                getLastLocation();
            } else {
                // Izin ditolak, beri tahu pengguna
                Toast.makeText(this, "Location permission denied", Toast.LENGTH_SHORT).show();
            }
        }
    }
}
