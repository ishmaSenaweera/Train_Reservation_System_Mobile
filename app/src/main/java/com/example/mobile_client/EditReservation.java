package com.example.mobile_client;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.example.mobile_client.model.Reservation;
import com.example.mobile_client.model.ReservationModel;
import retrofit2.Retrofit;

/*
 * File Name: EditReservation.java
 * Description: Activity to edit reservation details.
 * Author: IT20168704
 */

public class EditReservation extends AppCompatActivity {
    private EditText name, phone, email, address, zipcode, quentity;
    private Button edit;
    private DbHandler dbHandler;
    private Context context;

    private Retrofit retrofit;
    private static final String BASE_URL = "https://10.0.2.2:44425/";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_reservation);

        context = this;
        dbHandler = new DbHandler(context);

        name = findViewById(R.id.edit_name);
        phone = findViewById(R.id.edit_phone);
        email = findViewById(R.id.edit_email);
        address = findViewById(R.id.edit_address);
        zipcode = findViewById(R.id.edit_zipcode);
        quentity = findViewById(R.id.edit_qty);
        edit = findViewById(R.id.btn_edit);

        final String id = getIntent().getStringExtra("id");
        ReservationModel reservation = dbHandler.getSingleReservation(Integer.parseInt(id));

        name.setText(reservation.getName());
        phone.setText(Integer.toString(reservation.getPhone()));
        email.setText(reservation.getEmail());
        address.setText(reservation.getAddress());
        zipcode.setText(reservation.getZipcode());
        quentity.setText(Integer.toString(reservation.getQuentity()));

        edit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String nameText = name.getText().toString();
                String phoneText = phone.getText().toString();
                String emailText = email.getText().toString();
                String addressText = address.getText().toString();
                String zipcodeText = zipcode.getText().toString();
                String quentityText = quentity.getText().toString();

                Reservation reservation = new Reservation();
                reservation.setName(nameText);
                reservation.setMobile(phoneText);
                reservation.setEmail(emailText);
                reservation.setDate(addressText);
                reservation.setDetails(zipcodeText);
                reservation.setTickets(quentityText);

                ReservationModel delivery = new ReservationModel(Integer.parseInt(id),nameText,Integer.parseInt(phoneText),
                        emailText,addressText,zipcodeText,Integer.parseInt(quentityText));
                int state = dbHandler.updateSingleReservation(delivery);
                System.out.println(state);

                startActivity(new Intent(context,HomeActivity.class));

                Toast.makeText(EditReservation.this,
                        "Reservation Updated Successfully..",
                        Toast.LENGTH_LONG).show();

            }
        });
    }
}