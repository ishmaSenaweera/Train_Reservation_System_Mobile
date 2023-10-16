package com.example.mobile_client.api;

import com.example.mobile_client.model.Reservation;
import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.POST;

/*
 * File Name: ReservationAPI.java
 * Description: Contains APIs to communicate with backend.
 * Author: IT20168704
 */

public interface ReservationAPI {

    @POST("api/reservation")
    Call<Void> registerTraveler(@Body Reservation reservation);

}
