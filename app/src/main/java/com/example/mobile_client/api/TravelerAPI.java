package com.example.mobile_client.api;

import com.example.mobile_client.model.ChangePasswordRequest;
import com.example.mobile_client.model.Traveler;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.GET;
import retrofit2.http.POST;
import retrofit2.http.PUT;
import retrofit2.http.Path;

public interface TravelerAPI {

    @POST("api/traveler")
    Call<Void> registerTraveler(@Body Traveler traveler);

    @POST("api/traveler/login")
    Call<Traveler> loginTraveler(@Body Traveler traveler);

    @PUT("api/traveler/{nic}")
    Call<Void> updateTraveler(@Path("nic") String nic, @Body Traveler traveler);

    @PUT("api/traveler/change-password/{nic}")
    Call<Void> changePassword(@Path("nic") String nic, @Body ChangePasswordRequest changePasswordRequest);

    @PUT("api/traveler/change-status/{nic}")
    Call<Void> changeAccountStatus(@Path("nic") String nic);

    @GET("api/traveler/{nic}")
    Call<Void> getTraveler(@Path("nic") String nic);

}
