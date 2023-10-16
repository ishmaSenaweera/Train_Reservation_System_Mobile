package com.example.mobile_client;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.text.SpannableString;
import android.text.style.UnderlineSpan;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.view.View;
import android.widget.Toast;

import com.example.mobile_client.api.TravelerAPI;
import com.example.mobile_client.model.Traveler;

import java.io.IOException;

import okhttp3.OkHttpClient;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

/*
 * File Name: RegisterActivity.java
 * Description: Activity to Register to the App.
 * Author: IT20123468
 */

public class RegisterActivity extends AppCompatActivity {

    private Retrofit retrofit;

    // backend URL
    private static final String BASE_URL = "https://10.0.2.2:44425/";
    OkHttpClient unsafeOkHttpClient = NetworkUtils.getUnsafeOkHttpClient();

    private EditText regFirstName, regLastName, regNIC, regPhone, regEmail, regPassword, regRePassword;
    private TextView login;
    private ProgressBar progressBar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        getSupportActionBar().hide();
        initializeViews();
        retrofit = new Retrofit.Builder()
                .baseUrl(BASE_URL)
                .client(unsafeOkHttpClient)
                .addConverterFactory(GsonConverterFactory.create())
                .build();
    }

    private void initializeViews() {
        regFirstName = findViewById(R.id.regfirstname);
        regLastName = findViewById(R.id.reglastname);
        regNIC = findViewById(R.id.regnic);
        regPhone = findViewById(R.id.regphone);
        regEmail = findViewById(R.id.regemail);
        regPassword = findViewById(R.id.regpassword);
        regRePassword = findViewById(R.id.regrepassword);
        login = findViewById(R.id.login);
        progressBar = findViewById(R.id.progressBar);

        findViewById(R.id.registerbtn).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                registerUser();
            }
        });

        // Underlining the "Login" TextView to indicate it's clickable
        SpannableString content = new SpannableString("Login");
        content.setSpan(new UnderlineSpan(), 0, content.length(), 0);
        login.setText(content);

        login.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Redirect to login activity,
                Intent intent = new Intent(RegisterActivity.this, LoginActivity.class);
                startActivity(intent);
            }
        });
    }

    private void registerUser() {
        // Retrieve the data from views
        String firstName = regFirstName.getText().toString().trim();
        String lastName = regLastName.getText().toString().trim();
        String nic = regNIC.getText().toString().trim();
        String phone = regPhone.getText().toString().trim();
        String email = regEmail.getText().toString().trim();
        String password = regPassword.getText().toString().trim();
        String rePassword = regRePassword.getText().toString().trim();

        // Validate the user input
        if (validateInput(firstName, lastName, nic, phone, email, password, rePassword)) {
            progressBar.setVisibility(View.VISIBLE);
            Traveler traveler = new Traveler();
            traveler.setFirstName(firstName);
            traveler.setLastName(lastName);
            traveler.setNic(nic);
            traveler.setPhone(phone);
            traveler.setEmail(email);
            traveler.setPassword(password);

            TravelerAPI api = retrofit.create(TravelerAPI.class);
            Call<Void> call = api.registerTraveler(traveler);

            call.enqueue(new Callback<Void>() {
                @Override
                public void onResponse(Call<Void> call, Response<Void> response) {
                    if (response.isSuccessful()) {
                        progressBar.setVisibility(View.INVISIBLE);
                        Toast.makeText(RegisterActivity.this, "User Registered Successfully", Toast.LENGTH_SHORT).show();
                        // navigate to login
                        Intent intent = new Intent(RegisterActivity.this, LoginActivity.class);
                        startActivity(intent);
                    } else {
                        try {
                            String errorMessage = response.errorBody().string();
                            progressBar.setVisibility(View.INVISIBLE);
                            Toast.makeText(RegisterActivity.this, "Error: " + errorMessage, Toast.LENGTH_SHORT).show();
                        } catch (IOException e) {
                            progressBar.setVisibility(View.INVISIBLE);
                            Toast.makeText(RegisterActivity.this, "Registration Failed", Toast.LENGTH_SHORT).show();
                        }

                    }
                }

                @Override
                public void onFailure(Call<Void> call, Throwable t) {
                    progressBar.setVisibility(View.INVISIBLE);
                    Toast.makeText(RegisterActivity.this, "Error: " + t.getMessage(), Toast.LENGTH_SHORT).show();
                }
            });
        }

    }

    private boolean validateInput(String firstName, String lastName, String nic, String phone, String email, String password, String rePassword) {
        if (firstName.isEmpty() || lastName.isEmpty() || nic.isEmpty() || phone.isEmpty() || email.isEmpty() || password.isEmpty() || rePassword.isEmpty()) {
            Toast.makeText(this, "All fields are mandatory.", Toast.LENGTH_SHORT).show();
            return false;
        }

        if (!password.equals(rePassword)) {
            Toast.makeText(this, "Passwords do not match.", Toast.LENGTH_SHORT).show();
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

        if (password.length() <= 8) {
            Toast.makeText(this, "Password should be more than 8 characters.", Toast.LENGTH_SHORT).show();
            return false;
        }

        return true;
    }

    // helper method to validate email using a regex pattern
    private boolean isValidEmail(String email) {
        String emailPattern = "[a-zA-Z0-9._-]+@[a-z]+\\.+[a-z]+"; // Simple regex for email
        return email.matches(emailPattern);
    }


}