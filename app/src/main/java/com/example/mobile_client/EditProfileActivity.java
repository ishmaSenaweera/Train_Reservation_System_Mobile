package com.example.mobile_client;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;
import com.example.mobile_client.api.TravelerAPI;
import com.example.mobile_client.model.ChangePasswordRequest;
import com.example.mobile_client.model.Traveler;
import java.io.IOException;
import okhttp3.OkHttpClient;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

/*
 * File Name: EditProfileActivity.java
 * Description: Activity to Edit Traveler Profile Details.
 * Author: IT20123468
 */

public class EditProfileActivity extends AppCompatActivity {

    private Retrofit retrofit;
    private Traveler updateTraveler;

    // backend URL
    private static final String BASE_URL = "https://10.0.2.2:44425/";
    OkHttpClient unsafeOkHttpClient = NetworkUtils.getUnsafeOkHttpClient();

    private Traveler loggedInUser;
    private EditText editFirstName, editLastName, editNic, editPhone, editEmail;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_profile);

        getSupportActionBar().setTitle("Edit Profile");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        retrofit = new Retrofit.Builder()
                .baseUrl(BASE_URL)
                .client(unsafeOkHttpClient)
                .addConverterFactory(GsonConverterFactory.create())
                .build();

        // Initialize the fields
        editFirstName = findViewById(R.id.editfirstname);
        editLastName = findViewById(R.id.editlastname);
        editNic = findViewById(R.id.editnic);
        editPhone = findViewById(R.id.editphone);
        editEmail = findViewById(R.id.editemail);

        Button changePasswordBtn = findViewById(R.id.changepwbtn);
        Button deactivateAccountBtn = findViewById(R.id.deactivateaccbtn);

        // Set onClickListener to changePasswordBtn
        changePasswordBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showChangePasswordDialog(); // Show the dialog for changing the password
            }
        });

        // Set onClickListener to deactivateAccountBtn
        deactivateAccountBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                new AlertDialog.Builder(EditProfileActivity.this)
                        .setTitle("Deactivate Account")
                        .setMessage("Are you sure you want to deactivate your account?")
                        .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                deactivateAccount();
                            }
                        })
                        .setNegativeButton("No", null)
                        .show();
            }
        });

        if (getIntent() != null && getIntent().hasExtra("traveler_data")) {
            loggedInUser = (Traveler) getIntent().getSerializableExtra("traveler_data");

            // Populate fields with existing data
            editFirstName.setText(loggedInUser.getFirstName());
            editLastName.setText(loggedInUser.getLastName());
            editNic.setText(loggedInUser.getNic());
            editPhone.setText(loggedInUser.getPhone());
            editEmail.setText(loggedInUser.getEmail());
        }

        // Set click listener for the update button
        findViewById(R.id.updatebtn).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                updateProfile();
            }
        });
    }

    private void updateProfile() {
        // Retrieve the data from fields
        String firstName = editFirstName.getText().toString().trim();
        String lastName = editLastName.getText().toString().trim();
        String nic = editNic.getText().toString().trim();
        String phone = editPhone.getText().toString().trim();
        String email = editEmail.getText().toString().trim();

        // Validate the user input
        if (validateInput(firstName, lastName, nic, phone, email)) {
            updateTraveler = new Traveler();
            updateTraveler.setFirstName(firstName);
            updateTraveler.setLastName(lastName);
            updateTraveler.setNic(nic);
            updateTraveler.setPhone(phone);
            updateTraveler.setEmail(email);
            updateTraveler.setId(loggedInUser.getId());
            updateTraveler.setPassword(loggedInUser.getPassword());

            TravelerAPI api = retrofit.create(TravelerAPI.class);
            Call<Void> call = api.updateTraveler(loggedInUser.getNic(), updateTraveler);

            call.enqueue(new Callback<Void>() {
                @Override
                public void onResponse(Call<Void> call, Response<Void> response) {
                    if (response.isSuccessful()) {
                        saveUserToPrefs(updateTraveler);
                        Toast.makeText(EditProfileActivity.this, "Profile updated successfully!", Toast.LENGTH_SHORT).show();
                        // Navigate to ProfileActivity with the updated data
                        Intent intent = new Intent(EditProfileActivity.this, ProfileActivity.class);
                        intent.putExtra("traveler_data", updateTraveler);
                        startActivity(intent);
                        finish(); // This will finish the EditProfileActivity and prevent going back to it on pressing the back button
                    } else {
                        try {
                            String errorMessage = response.errorBody().string();
                            Toast.makeText(EditProfileActivity.this, "Error: " + errorMessage, Toast.LENGTH_LONG).show();
                        } catch (IOException e) {
                            Toast.makeText(EditProfileActivity.this, "Error parsing error message.", Toast.LENGTH_SHORT).show();
                        }
                    }

                }

                @Override
                public void onFailure(Call<Void> call, Throwable t) {
                    Toast.makeText(EditProfileActivity.this, "Error: " + t.getMessage(), Toast.LENGTH_SHORT).show();
                }
            });
        }

    }

    private boolean validateInput(String firstName, String lastName, String nic, String phone, String email) {
        if (firstName.isEmpty() || lastName.isEmpty() || nic.isEmpty() || phone.isEmpty() || email.isEmpty()) {
            Toast.makeText(this, "All fields are mandatory.", Toast.LENGTH_SHORT).show();
            return false;
        }


        if (nic.length() != 10) {
            Toast.makeText(this, "NIC should have 10 digits.", Toast.LENGTH_SHORT).show();
            return false;
        }

        if (phone.length() != 10) {
            Toast.makeText(this, "Phone number should have 10 digits.", Toast.LENGTH_SHORT).show();
            return false;
        }

        if (!isValidEmail(email)) {
            Toast.makeText(this, "Enter a valid email.", Toast.LENGTH_SHORT).show();
            return false;
        }

        return true;
    }

    // Helper method to validate email using a regex pattern
    private boolean isValidEmail(String email) {
        String emailPattern = "[a-zA-Z0-9._-]+@[a-z]+\\.+[a-z]+"; // Simple regex for email
        return email.matches(emailPattern);
    }

    // Method to display a dialog prompting the user to enter their current and new password
    private void showChangePasswordDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(EditProfileActivity.this);
        LayoutInflater inflater = this.getLayoutInflater();
        View dialogView = inflater.inflate(R.layout.dialog_change_password, null);
        builder.setView(dialogView);

        EditText currentPasswordInput = dialogView.findViewById(R.id.current_password);
        EditText newPasswordInput = dialogView.findViewById(R.id.new_password);

        builder.setTitle("Change Password")
                .setPositiveButton("Change", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int id) {
                        String currentPassword = currentPasswordInput.getText().toString();
                        String newPassword = newPasswordInput.getText().toString();

                        if (currentPassword.isEmpty() || newPassword.isEmpty()) {
                            Toast.makeText(EditProfileActivity.this, "All fields are mandatory.", Toast.LENGTH_SHORT).show();
                        }

                        if (currentPassword.equals(newPassword)) {
                            Toast.makeText(EditProfileActivity.this, "New password should be different from the current password.", Toast.LENGTH_SHORT).show();
                            return;
                        }

                        if (currentPassword.length() <= 8 || newPassword.length() <= 8) {
                            Toast.makeText(EditProfileActivity.this, "Password should be more than 8 characters.", Toast.LENGTH_SHORT).show();
                            return;
                        }

                        changePassword(currentPassword, newPassword); // Make the API call
                    }
                })
                .setNegativeButton("Cancel", null)
                .create()
                .show();
    }

    // This method handles the change password logic
    private void changePassword(String currentPassword, String newPassword) {
        ChangePasswordRequest request = new ChangePasswordRequest();
        request.setCurrentPassword(currentPassword);
        request.setNewPassword(newPassword);

        TravelerAPI api = retrofit.create(TravelerAPI.class);
        Call<Void> call = api.changePassword(loggedInUser.getNic(), request);

        call.enqueue(new Callback<Void>() {
            @Override
            public void onResponse(Call<Void> call, Response<Void> response) {
                if (response.isSuccessful()) {
                    Toast.makeText(EditProfileActivity.this, "Password changed successfully.", Toast.LENGTH_SHORT).show();
                } else {
                    try {
                        String errorMessage = response.errorBody().string();
                        Toast.makeText(EditProfileActivity.this, "Error: " + errorMessage, Toast.LENGTH_SHORT).show();
                    } catch (IOException e) {
                        Toast.makeText(EditProfileActivity.this, "Failed to change password.", Toast.LENGTH_SHORT).show();
                    }
                }
            }

            @Override
            public void onFailure(Call<Void> call, Throwable t) {
                Toast.makeText(EditProfileActivity.this, "Error: " + t.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void deactivateAccount() {
        TravelerAPI api = retrofit.create(TravelerAPI.class);
        Call<Void> call = api.changeAccountStatus(loggedInUser.getNic());

        call.enqueue(new Callback<Void>() {
            @Override
            public void onResponse(Call<Void> call, Response<Void> response) {
                if (response.isSuccessful()) {
                    Toast.makeText(EditProfileActivity.this, "Account deactivated successfully.", Toast.LENGTH_SHORT).show();
                    clearUserFromPrefs();
                    Intent intent = new Intent(EditProfileActivity.this, LoginActivity.class);
                    startActivity(intent);
                } else {
                    Toast.makeText(EditProfileActivity.this, "Failed to deactivate account.", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<Void> call, Throwable t) {
                Toast.makeText(EditProfileActivity.this, "Error: " + t.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void saveUserToPrefs(Traveler traveler) {
        SharedPreferences sharedPref = getSharedPreferences("myData", Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPref.edit();
        editor.putString("id", traveler.getId());
        editor.putString("firstName", traveler.getFirstName());
        editor.putString("lastName", traveler.getLastName());
        editor.putString("nic", traveler.getNic());
        editor.putString("phoneNumber", traveler.getPhone());
        editor.putString("email", traveler.getEmail());
        editor.putString("password", traveler.getPassword());

        editor.apply();
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
        } else if (id == R.id.logout) {
            Toast.makeText(this, "Logged out.", Toast.LENGTH_SHORT).show();
            clearUserFromPrefs();
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

    @Override
    public void onBackPressed() {
        Intent intent = new Intent(EditProfileActivity.this, HomeActivity.class);
        intent.putExtra("traveler_data", updateTraveler);
        startActivity(intent);
        finish();
    }

}