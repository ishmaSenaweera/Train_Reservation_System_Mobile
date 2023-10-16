package com.example.mobile_client;


import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.mobile_client.model.ReservationModel;
import com.example.mobile_client.model.Traveler;
import org.checkerframework.checker.nullness.qual.NonNull;

import java.util.ArrayList;
import java.util.List;

import okhttp3.OkHttpClient;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

/*
 * File Name: HomeActivity.java
 * Description: Landing Activity after Login and includes all reservation details.
 * Author: IT20123468 and IT20168704
 */

public class HomeActivity extends AppCompatActivity {
    private Traveler loggedInUser;
    Button add;
    ListView listView;
    TextView count;
    Context context;
    private DbHandler dbHandler;
    private List<ReservationModel> reservations;

    private Retrofit retrofit;
    private static final String BASE_URL = "https://10.0.2.2:44425/";
    OkHttpClient unsafeOkHttpClient = NetworkUtils.getUnsafeOkHttpClient();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);

        getSupportActionBar().setTitle("Home");

        retrofit = new Retrofit.Builder()
                .baseUrl(BASE_URL)
                .client(unsafeOkHttpClient)
                .addConverterFactory(GsonConverterFactory.create())
                .build();

        dbHandler = new DbHandler(this);
        add = findViewById(R.id.add);
        listView = findViewById(R.id.deliverylist);
        count = findViewById(R.id.deliverycount);
        context = this;
        reservations = new ArrayList<>();

        reservations = dbHandler.getAllReservations();

        //display dilivery list
        ReservationAdapter adapter = new ReservationAdapter(context, R.layout.singlereservation, reservations);
        listView.setAdapter(adapter);



        //get delivery count
        int countDelivery = dbHandler.countReservation();
        count.setText("You have "+countDelivery+" Reservations");

        add.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(context, AddReservation.class));
            }
        });

        //display alert box
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {

                ReservationModel reservation = reservations.get(i);
                AlertDialog.Builder builder = new AlertDialog.Builder(context);
                builder.setTitle("Reservation details");
                builder.setMessage("Name       : "+reservation.getName()+ "\n" +"Phone No: "+reservation.getPhone()+"\n"+"Email        : "+reservation.getEmail()+
                        "\n"+"Date         : "+reservation.getAddress()+"\n"+"Details     : "+reservation.getZipcode()+"\n"+"Quantity   : "+reservation.getQuentity()+"\n");

                builder.setNegativeButton("Delete", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        dbHandler.deleteReservation(reservation.getId());
                        startActivity(new Intent(context, HomeActivity.class));

                        Toast.makeText(HomeActivity.this,
                                "Reservation Deleted Successfully..",
                                Toast.LENGTH_LONG).show();
                    }
                });

                builder.setNeutralButton("Update", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        Intent intent = new Intent(context, EditReservation.class);
                        intent.putExtra("id",String.valueOf(reservation.getId()));
                        startActivity(intent);
                    }
                });
                builder.show();


            }
        });
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
