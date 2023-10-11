package com.example.mobile_client;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.TextView;
import android.widget.Toast;
import com.example.mobile_client.model.Traveler;

/*
 * File Name: ProfileActivity.java
 * Description: Activity for Traveler Profile.
 * Author: IT20123468
 */

public class ProfileActivity extends AppCompatActivity {

    private TextView profileWelcome, profileName, profileNic, profilePhone, profileEmail;
    private Traveler loggedInUser;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);

        getSupportActionBar().setTitle("My Profile");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        // Linking the XML views to Java objects
        profileWelcome = findViewById(R.id.profilewelcome);
        profileName = findViewById(R.id.profilename);
        profileNic = findViewById(R.id.profilenic);
        profilePhone = findViewById(R.id.profilephone);
        profileEmail = findViewById(R.id.profileemail);

        // Retrieve the Traveler object passed from HomeActivity
        if (getIntent() != null && getIntent().hasExtra("traveler_data")) {
            loggedInUser = (Traveler) getIntent().getSerializableExtra("traveler_data");

            // Populate the views with user data
            profileWelcome.setText("Welcome, " + loggedInUser.getFirstName());
            profileName.setText(loggedInUser.getFirstName() + " " + loggedInUser.getLastName());
            profileNic.setText(loggedInUser.getNic());
            profilePhone.setText(loggedInUser.getPhone());
            profileEmail.setText(loggedInUser.getEmail());
        } else {
            // Handle case where no user data is available
            Toast.makeText(this, "Error loading user data", Toast.LENGTH_SHORT).show();

        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.common_menu, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        int id = item.getItemId();
        if (id == R.id.myprofile) {
            Intent intent = new Intent(this, ProfileActivity.class);
            intent.putExtra("traveler_data", loggedInUser);
            startActivity(intent);
        } else if (id == R.id.editprofile) {
            Intent intent = new Intent(this, EditProfileActivity.class);
            intent.putExtra("traveler_data", loggedInUser);
            startActivity(intent);
        }  else if (id == R.id.logout) {
            Toast.makeText(this, "Logged out.", Toast.LENGTH_SHORT).show();
            clearUserFromPrefs(); // Clear traveler_data on logout
            Intent intent = new Intent(this, LoginActivity.class);
            startActivity(intent);
            finish();
        }
        return super.onOptionsItemSelected(item);
    }

    // Method to clear user data from shared preferences
    private void clearUserFromPrefs() {
        SharedPreferences sharedPref = getSharedPreferences("myData", Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPref.edit();
        editor.clear();
        editor.apply();
    }
}