package com.firda.route;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import androidx.appcompat.app.AppCompatActivity;

import com.example.getpresensi.R;

public class EditProfileActivity extends AppCompatActivity {

    private EditText editTextFullName;
    private Button buttonSaveChanges;
    private DatabaseHelper databaseHelper;
    private String username;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_profile);

        editTextFullName = findViewById(R.id.editTextFullName);
        buttonSaveChanges = findViewById(R.id.buttonSaveChanges);
        databaseHelper = new DatabaseHelper(this);

        // Menerima data pengguna dari Intent
        username = getIntent().getStringExtra("username");
        String fullName = getIntent().getStringExtra("fullName");

        // Menampilkan nama lengkap saat ini di EditText
        editTextFullName.setText(fullName);

        buttonSaveChanges.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Mengambil nama lengkap yang diubah
                String newFullName = editTextFullName.getText().toString().trim();

                // Memperbarui nama lengkap di database
                databaseHelper.updateFullName(username, newFullName);

                // Kembali ke ProfileActivity setelah menyimpan perubahan
                Intent intent = new Intent(EditProfileActivity.this, ProfileActivity.class);
                intent.putExtra("username", username);
                intent.putExtra("fullName", newFullName);
                startActivity(intent);
                finish(); // Menutup aktivitas edit profile
            }
        });
    }
}

