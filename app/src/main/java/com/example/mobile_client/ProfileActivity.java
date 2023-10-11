package com.example.mobile_client;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;
import com.bumptech.glide.Glide;
import com.example.mobile_client.model.Traveler;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

/*
 * File Name: ProfileActivity.java
 * Description: Activity for Traveler Profile.
 * Author: IT20123468
 */

public class ProfileActivity extends AppCompatActivity {

    private TextView profileWelcome, profileName, profileNic, profilePhone, profileEmail;
    private Traveler loggedInUser;
    private ImageView profileImage;
    private Button profilePicBtn, deletePicBtn;

    private static final int PICK_IMAGE_REQUEST = 1;
    private final ActivityResultLauncher<Intent> imagePickerLauncher = registerForActivityResult(
            new ActivityResultContracts.StartActivityForResult(),
            result -> {
                if (result.getResultCode() == Activity.RESULT_OK && result.getData() != null) {
                    Uri imageUri = result.getData().getData();
                    uploadImageToFirebase(imageUri);
                }
            });


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
        profileImage = findViewById(R.id.profileimage);
        profilePicBtn = findViewById(R.id.profilepicbtn);
        deletePicBtn = findViewById(R.id.deletepicbtn);

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

        loadProfileImage();

        profilePicBtn.setOnClickListener(v -> {
            Intent intent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
            imagePickerLauncher.launch(intent);
        });


        deletePicBtn.setOnClickListener(v -> {
            deleteProfileImage();
        });

    }

    private void loadProfileImage() {
        String nic = loggedInUser.getNic();
        StorageReference storageReference = FirebaseStorage.getInstance().getReference("profileImages/" + nic);

        storageReference.getDownloadUrl().addOnSuccessListener(uri -> {
            Glide.with(ProfileActivity.this)
                    .load(uri)
                    .placeholder(R.drawable.ic_baseline_account_circle_24) // set the default image
                    .into(profileImage);
        }).addOnFailureListener(e -> {
            profileImage.setImageResource(R.drawable.ic_baseline_account_circle_24); // set the default image on failure
        });
    }

    private void uploadImageToFirebase(Uri imageUri) {
        if (imageUri != null) {
            String nic = loggedInUser.getNic(); // using loggedInUser's NIC as a unique identifier
            StorageReference storageReference = FirebaseStorage.getInstance().getReference("profileImages/" + nic);

            storageReference.putFile(imageUri).addOnSuccessListener(taskSnapshot -> {
                Toast.makeText(ProfileActivity.this, "Profile Image Uploaded", Toast.LENGTH_SHORT).show();
                Glide.with(ProfileActivity.this).load(imageUri).into(profileImage);
            }).addOnFailureListener(e -> {
                Toast.makeText(ProfileActivity.this, "Failed to Upload Profile Image", Toast.LENGTH_SHORT).show();
            });
        }
    }

    private void deleteProfileImage() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Delete Profile Image");
        builder.setMessage("Are you sure you want to delete your profile image?");
        builder.setPositiveButton("Yes", (dialog, which) -> {
            String nic = loggedInUser.getNic();
            StorageReference storageReference = FirebaseStorage.getInstance().getReference("profileImages/" + nic);

            storageReference.delete().addOnSuccessListener(aVoid -> {
                Toast.makeText(ProfileActivity.this, "Profile Image Deleted", Toast.LENGTH_SHORT).show();
                profileImage.setImageResource(R.drawable.ic_baseline_account_circle_24);
            }).addOnFailureListener(e -> {
                Toast.makeText(ProfileActivity.this, "Failed to Delete Profile Image", Toast.LENGTH_SHORT).show();
            });
        });
        builder.setNegativeButton("Cancel", (dialog, which) -> dialog.dismiss());
        AlertDialog dialog = builder.create();
        dialog.show();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.common_menu, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
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