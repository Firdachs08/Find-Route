package com.firda.route;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.example.getpresensi.R;

public class ProfileActivity extends AppCompatActivity {

    private TextView textViewFullname;
    private TextView textViewUsername;
    private static final int EDIT_PROFILE_REQUEST = 1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);

        // Menerima data pengguna dari Intent
        String username = getIntent().getStringExtra("username");
        String fullName = getIntent().getStringExtra("fullName");

        // Menampilkan data pengguna di UI
        TextView textViewUsername = findViewById(R.id.textViewUsername);
        TextView textViewFullName = findViewById(R.id.textViewFullname);

        textViewUsername.setText(username);
        textViewFullName.setText(fullName);

        // Button untuk memulai EditProfileActivity
        Button buttonEditProfile = findViewById(R.id.buttonEditProfile);
        buttonEditProfile.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Intent untuk memulai EditProfileActivity
                Intent intent = new Intent(ProfileActivity.this, EditProfileActivity.class);
                intent.putExtra("username", username);
                intent.putExtra("fullName", fullName);
                startActivityForResult(intent, EDIT_PROFILE_REQUEST);
            }
        });

    }

    // Metode untuk menangani hasil dari EditProfileActivity
    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == EDIT_PROFILE_REQUEST) {
            if (resultCode == RESULT_OK && data != null) {
                // Mengambil data nama lengkap yang diperbarui dari intent
                String updatedFullName = data.getStringExtra("updatedFullName");

                // Update tampilan dengan nama lengkap yang diperbarui
                TextView textViewFullName = findViewById(R.id.textViewFullname);
                textViewFullName.setText(updatedFullName);

                // Mengirim kembali data yang diperbarui ke MainActivity
                Intent resultIntent = new Intent();
                resultIntent.putExtra("updatedFullName", updatedFullName);
                setResult(RESULT_OK, resultIntent);
                finish();
            }
        }

    }
}
