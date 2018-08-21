package com.willowtreeapps.namegame.network.api;

import com.willowtreeapps.namegame.network.api.model.Person;
import com.willowtreeapps.namegame.network.api.model.Profiles;
import com.willowtreeapps.namegame.network.api.model2.Person2;

import java.util.List;

import retrofit2.Call;
import retrofit2.http.GET;

public interface NameGameApi {
    @GET("/api/v1.0/profiles")
    Call<List<Person2>> getProfiles();
    //Call<Profiles> getProfiles();
}
