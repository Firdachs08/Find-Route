package com.firda.route;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.example.getpresensi.R;

public class LoginActivity extends AppCompatActivity {

    EditText editTextUsername, editTextPassword;
    Button buttonLogin;
    TextView textViewRegister;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        editTextUsername = findViewById(R.id.editTextUsername);
        editTextPassword = findViewById(R.id.editTextPassword);
        buttonLogin = findViewById(R.id.buttonLogin);
        textViewRegister = findViewById(R.id.textViewRegister);

        buttonLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Tambahkan logika untuk melakukan login di sini
                String username = editTextUsername.getText().toString().trim();
                String password = editTextPassword.getText().toString().trim();

                // Contoh sederhana: validasi kosong
                if (username.isEmpty() || password.isEmpty()) {
                    Toast.makeText(LoginActivity.this, "Please enter username and password", Toast.LENGTH_SHORT).show();
                } else {
                    // Lakukan proses login
                    // Misalnya, dengan memanggil metode dari kelas DatabaseHelper
                    DatabaseHelper dbHelper = new DatabaseHelper(LoginActivity.this);
                    boolean loginSuccessful = dbHelper.checkUser(username, password);
                    if (loginSuccessful) {
                        // Jika login berhasil, meneruskan data nama lengkap ke MainActivity
                        String fullName = dbHelper.getFullName(username); // Anda perlu menambahkan metode ini di DatabaseHelper
                        Intent intent = new Intent(LoginActivity.this, MainActivity.class);
                        intent.putExtra("fullName", fullName);
                        startActivity(intent);
                        finish(); // Menutup aktivitas login agar tidak dapat diakses kembali setelah login berhasil
                    } else {
                        Toast.makeText(LoginActivity.this, "Invalid username or password", Toast.LENGTH_SHORT).show();
                    }
                }
            }
        });

        textViewRegister.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Menuju halaman registrasi
                startActivity(new Intent(LoginActivity.this, RegisterActivity.class));
            }
        });
    }
}


