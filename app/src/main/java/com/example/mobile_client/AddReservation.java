package com.example.mobile_client;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.example.mobile_client.api.ReservationAPI;
import com.example.mobile_client.model.Reservation;
import com.example.mobile_client.model.ReservationModel;
import okhttp3.OkHttpClient;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

/*
 * File Name: AddReservation.java
 * Description: Activity to add a new reservation.
 * Author: IT20168704
 */

public class AddReservation extends AppCompatActivity {
    private EditText name, phone, email, address, zipcode, quentity;
    private Button add;

    private DbHandler dbHandler;
    private Context context;
    private Retrofit retrofit;
    private static final String BASE_URL = "https://10.0.2.2:44425/";
    OkHttpClient unsafeOkHttpClient = NetworkUtils.getUnsafeOkHttpClient();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_reservation);

        retrofit = new Retrofit.Builder()
                .baseUrl(BASE_URL)
                .client(unsafeOkHttpClient)
                .addConverterFactory(GsonConverterFactory.create())
                .build();

        name = findViewById(R.id.et_name);
        phone = findViewById(R.id.et_phone);
        email = findViewById(R.id.et_email);
        address = findViewById(R.id.et_address);
        zipcode = findViewById(R.id.et_zipcode);
        quentity = findViewById(R.id.et_qty);

        add = findViewById(R.id.btn_add);

        context = this;
        dbHandler = new DbHandler(context);

        add.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String username = name.getText().toString();
                Integer userphone = 0;
                try {
                    userphone = Integer.parseInt(phone.getText().toString());
                } catch (NumberFormatException ex) {
                    System.out.println("not a number");
                }
                String useremail = email.getText().toString();
                String useraddress = address.getText().toString();
                String userzipcode = zipcode.getText().toString();

                Integer userquentity = 0;
                try {
                    userquentity = Integer.parseInt(quentity.getText().toString());
                } catch (NumberFormatException ex) {
                    System.out.println("not a number");
                }
                String vphone = String.valueOf(userphone);
                String vzipcode = String.valueOf(userzipcode);
                String vquentity = String.valueOf(userquentity);

                boolean check = validateinfo(username, vphone, useremail, useraddress, vzipcode, vquentity);

                Reservation reservation = new Reservation();
                reservation.setName(username);
                reservation.setMobile(vphone);
                reservation.setEmail(useremail);
                reservation.setDate(useraddress);
                reservation.setDetails(userzipcode);
                reservation.setTickets(vquentity);

                if (check == true) {

                    Toast.makeText(getApplicationContext(), "Data is valid", Toast.LENGTH_SHORT).show();

                    ReservationModel delivery = new ReservationModel(username, userphone, useremail, useraddress, userzipcode, userquentity);
                    dbHandler.addReservation(delivery);

                    ReservationAPI api = retrofit.create(ReservationAPI.class);
                    Call<Void> call = api.registerTraveler(reservation);

                    call.enqueue(new Callback<Void>() {
                        @Override
                        public void onResponse(Call<Void> call, Response<Void> response) {
                            if (response.isSuccessful()) {
                                startActivity(new Intent(context, HomeActivity.class));

                                Toast.makeText(AddReservation.this,
                                        "Reservation Inserted Successfully..",
                                        Toast.LENGTH_LONG).show();
                            } else {
                                Toast.makeText(getApplicationContext(), "Sorry check information again", Toast.LENGTH_SHORT).show();
                            }
                        }

                        @Override
                        public void onFailure(Call<Void> call, Throwable t) {
                            Toast.makeText(AddReservation.this, "Error: " + t.getMessage(), Toast.LENGTH_SHORT).show();
                        }
                    });


                }
            }
        });
    }

    private Boolean validateinfo(String username, String vphone, String useremail, String useraddress, String userzipcode, String userquentity) {
        if (username.length() == 0) {
            name.requestFocus();
            name.setError("FIELD CANNOT BE EMPTY");
            return false;
        } else if (vphone.length() == 0) {
            phone.requestFocus();
            phone.setError("FIELD CANNOT BE EMPTY");
            return false;
        } else if (!vphone.matches("^[+]?[0-9]{8,20}$")) {
            phone.requestFocus();
            phone.setError("Correct Format: +94xxxxxxxx");
            return false;
        } else if (useremail.length() == 0) {
            email.requestFocus();
            email.setError("FIELD CANNOT BE EMPTY");
            return false;
        } else if (!useremail.matches("[a-zA-Z0-9._%+-]+@[a-z0-9]+\\.[a-z]{2,3}")) {
            email.requestFocus();
            email.setError("ENTER VALID EMAIL");
            return false;
        } else if (useraddress.length() == 0) {
            email.requestFocus();
            email.setError("FIELD CANNOT BE EMPTY");
            return false;
        } else if (userzipcode.length() == 0) {
            email.requestFocus();
            email.setError("FIELD CANNOT BE EMPTY");
            return false;
        } else if (userquentity.length() == 0) {
            email.requestFocus();
            email.setError("FIELD CANNOT BE EMPTY");
            return false;
        } else {
            return true;
        }
    }

}