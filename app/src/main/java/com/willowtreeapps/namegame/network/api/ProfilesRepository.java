package com.willowtreeapps.namegame.network.api;

import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.util.Log;

import com.willowtreeapps.namegame.network.api.model.Person;
import com.willowtreeapps.namegame.network.api.model.Profiles;
import com.willowtreeapps.namegame.network.api.model2.Person2;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class ProfilesRepository {

    @NonNull
    private final NameGameApi api;
    @NonNull
    private List<Listener> listeners = new ArrayList<>(1);
    @Nullable
    private Person2 profiles;
    List<Person2> listPerson;

    public ProfilesRepository(@NonNull NameGameApi api, Listener... listeners) {
        this.api = api;
        if (listeners != null) {
            this.listeners = new ArrayList<>(Arrays.asList(listeners));
        }
        load();
    }

//    private void load() {
//        this.api.getProfiles().enqueue(new Callback<Profiles>() {
//            @Override
//            public void onResponse(Call<Profiles> call, Response<Profiles> response) {
//                Log.d("test", "onResponse: "+response.toString());
//                profiles = response.body();
//                for (Listener listener : listeners) {
//                    listener.onLoadFinished(profiles);
//                }
//            }
//
//            @Override
//            public void onFailure(Call<Profiles> call, Throwable t) {
//                for (Listener listener : listeners) {
//                    listener.onError(t);
//                }
//            }
//        });
//    }

    private void load() {

        Log.d("Test", "load: "+this.api);

        this.api.getProfiles().enqueue(new Callback<List<Person2>>() {
            @Override
            public void onResponse(Call<List<Person2>> call, Response<List<Person2>> response) {
                Log.d("Test", "onResponse: "+response.toString());
                Log.d("Test", "onResponse: "+response.body().get(0).getFirstName());
//                profiles = response.body().toArray();
                listPerson= new ArrayList<>();
               listPerson=response.body();

                for (Listener listener : listeners) {
                    listener.onLoadFinished(listPerson);
                }

            }

            @Override
            public void onFailure(Call<List<Person2>> call, Throwable t) {
                Log.d("Test", "onResponse: "+t.getMessage());
                for (Listener listener : listeners) {
                    listener.onError(t);
                }

            }
        });
    }

    public void register(@NonNull Listener listener) {
        if (listeners.contains(listener)) throw new IllegalStateException("Listener is already registered.");
        listeners.add(listener);
        if (profiles != null) {
            listener.onLoadFinished(listPerson);
        }
    }

    public void unregister(@NonNull Listener listener) {
        listeners.remove(listener);
    }

    public interface Listener {
        void onLoadFinished(@NonNull List<Person2> people);
        void onError(@NonNull Throwable error);
    }

}
