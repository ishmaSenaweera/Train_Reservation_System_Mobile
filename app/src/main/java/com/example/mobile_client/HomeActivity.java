package com.example.mobile_client;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Toast;
import com.example.mobile_client.model.Traveler;

/*
 * File Name: HomeActivity.java
 * Description: Landing Activity after Login.
 * Author: IT20123468
 */

public class HomeActivity extends AppCompatActivity {
    private Traveler loggedInUser;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);

        getSupportActionBar().setTitle("Home");
    }

    @Override
    protected void onResume() {
        super.onResume();
        loadUserData();
    }

    private void loadUserData() {
        Traveler savedUser = getUserFromPrefs();
        if (savedUser.getNic() != null && !savedUser.getNic().isEmpty()) {
            loggedInUser = savedUser;
        } else {
            // If no data found, redirect to LoginActivity
            Intent intent = new Intent(this, LoginActivity.class);
            startActivity(intent);
            finish();  // Ensure that the HomeActivity is closed
        }
    }

    private Traveler getUserFromPrefs() {
        SharedPreferences sharedPref = getSharedPreferences("myData", Context.MODE_PRIVATE);
        String id = sharedPref.getString("id", null);
        String firstName = sharedPref.getString("firstName", null);
        String lastName = sharedPref.getString("lastName", null);
        String nic = sharedPref.getString("nic", null);
        String phoneNumber = sharedPref.getString("phoneNumber", null);
        String email = sharedPref.getString("email", null);
        String password = sharedPref.getString("password", null);

        Traveler traveler = new Traveler();
        traveler.setId(id);
        traveler.setFirstName(firstName);
        traveler.setLastName(lastName);
        traveler.setNic(nic);
        traveler.setPhone(phoneNumber);
        traveler.setEmail(email);
        traveler.setPassword(password);

        return traveler;
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
