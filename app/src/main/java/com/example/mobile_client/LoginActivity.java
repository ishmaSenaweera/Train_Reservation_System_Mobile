package com.example.mobile_client;

import android.os.Bundle;
import android.text.method.HideReturnsTransformationMethod;
import android.text.method.PasswordTransformationMethod;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;
import android.view.View;
import android.content.Intent;
import android.text.SpannableString;
import android.text.style.UnderlineSpan;
import androidx.appcompat.app.AppCompatActivity;
import com.example.mobile_client.api.TravelerAPI;
import com.example.mobile_client.model.Traveler;
import java.io.IOException;
import okhttp3.OkHttpClient;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;
import android.content.Context;
import android.content.SharedPreferences;

/*
 * File Name: LoginActivity.java
 * Description: Activity to Login to the App.
 * Author: IT20123468
 */

public class LoginActivity extends AppCompatActivity {

    private Retrofit retrofit;
    private static final String BASE_URL = "https://10.0.2.2:44425/";
    private OkHttpClient unsafeOkHttpClient = NetworkUtils.getUnsafeOkHttpClient();

    private EditText nicField, passwordField;
    private ImageView showHidePwd;
    private ProgressBar progressBar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        getSupportActionBar().hide();

        nicField = findViewById(R.id.loginnic);
        passwordField = findViewById(R.id.loginpassword);
        showHidePwd = findViewById(R.id.showhidepassword);
        showHidePwd.setImageResource(R.drawable.ic_hide_pwd);  // Start with the password hidden
        passwordField.setTransformationMethod(PasswordTransformationMethod.getInstance()); // Ensure initial state
        progressBar = findViewById(R.id.progressBar);

        showHidePwd.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (passwordField.getTransformationMethod() instanceof PasswordTransformationMethod) {
                    // If password is hidden, show it.
                    passwordField.setTransformationMethod(HideReturnsTransformationMethod.getInstance());
                    showHidePwd.setImageResource(R.drawable.ic_show_pwd);  // Set icon to "show" state
                } else {
                    // If password is shown, hide it.
                    passwordField.setTransformationMethod(PasswordTransformationMethod.getInstance());
                    showHidePwd.setImageResource(R.drawable.ic_hide_pwd);  // Set icon to "hide" state
                }
            }
        });

        findViewById(R.id.loginbtn).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                loginUser();
            }
        });

        // Underlining the "Register" TextView to indicate it's clickable
        TextView register = findViewById(R.id.register);
        SpannableString content = new SpannableString("Register");
        content.setSpan(new UnderlineSpan(), 0, content.length(), 0);
        register.setText(content);

        register.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(LoginActivity.this, RegisterActivity.class);
                startActivity(intent);
            }
        });

        retrofit = new Retrofit.Builder()
                .baseUrl(BASE_URL)
                .client(unsafeOkHttpClient)
                .addConverterFactory(GsonConverterFactory.create())
                .build();
    }

    private void loginUser() {
        String nic = nicField.getText().toString().trim();
        String password = passwordField.getText().toString().trim();

        if (validateInput(nic, password)) {
            progressBar.setVisibility(View.VISIBLE);
            Traveler traveler = new Traveler();
            traveler.setNic(nic);
            traveler.setPassword(password);

            TravelerAPI api = retrofit.create(TravelerAPI.class);
            Call<Traveler> call = api.loginTraveler(traveler);

            call.enqueue(new Callback<Traveler>() {
                @Override
                public void onResponse(Call<Traveler> call, Response<Traveler> response) {
                    if (response.isSuccessful()) {
                        Traveler loggedInUser = response.body();
                        saveUserToPrefs(loggedInUser);
                        progressBar.setVisibility(View.INVISIBLE);

                        // Check for null, just in case
                        if (loggedInUser != null) {
                            Toast.makeText(LoginActivity.this, "Welcome " + loggedInUser.getFirstName() + " " + loggedInUser.getLastName(), Toast.LENGTH_SHORT).show();

                            // navigate to home.
                            Intent intent = new Intent(LoginActivity.this, HomeActivity.class);
                            intent.putExtra("traveler_data", loggedInUser);
                            intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
                            startActivity(intent);
                            finish(); // close login activity
                        } else {
                            Toast.makeText(LoginActivity.this, "Error: Received empty user data", Toast.LENGTH_SHORT).show();
                        }
                    } else {
                        try {
                            String errorMessage = response.errorBody().string();
                            progressBar.setVisibility(View.INVISIBLE);
                            Toast.makeText(LoginActivity.this, "Error: " + errorMessage, Toast.LENGTH_SHORT).show();
                        } catch (IOException e) {
                            progressBar.setVisibility(View.INVISIBLE);
                            Toast.makeText(LoginActivity.this, "Login Failed", Toast.LENGTH_SHORT).show();
                        }
                    }
                }

                @Override
                public void onFailure(Call<Traveler> call, Throwable t) {
                    progressBar.setVisibility(View.INVISIBLE);
                    Toast.makeText(LoginActivity.this, "Error: " + t.getMessage(), Toast.LENGTH_SHORT).show();
                }


            });


        }

    }

    private boolean validateInput(String nic, String password) {
        if (nic.isEmpty() || password.isEmpty()) {
            Toast.makeText(this, "All fields are mandatory.", Toast.LENGTH_SHORT).show();
            return false;
        }

        if (nic.length() != 10) {
            Toast.makeText(this, "NIC should have 10 digits.", Toast.LENGTH_SHORT).show();
            return false;
        }


        if (password.length() <= 8) {
            Toast.makeText(this, "Password should be more than 8 characters.", Toast.LENGTH_SHORT).show();
            return false;
        }

        return true;
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
    protected void onStart() {
        super.onStart();
        Traveler savedUser = getUserFromPrefs();
        if (savedUser.getNic() != null && !savedUser.getNic().isEmpty()) {
            // User is already logged in, redirect them to HomeActivity or any other activity
            Intent intent = new Intent(LoginActivity.this, HomeActivity.class);
            intent.putExtra("traveler_data", savedUser);
            startActivity(intent);
            finish(); // Close LoginActivity
        }
    }


}
