package com.willowtreeapps.namegame.ui.modesFragments.presenter;

import android.support.annotation.NonNull;
import android.util.Log;

import com.squareup.picasso.Picasso;
import com.willowtreeapps.namegame.core.ListRandomizer;
import com.willowtreeapps.namegame.core.NameGameApplication;
import com.willowtreeapps.namegame.network.api.ProfilesRepository;
import com.willowtreeapps.namegame.network.api.model2.Person2;
import com.willowtreeapps.namegame.ui.modesFragments.Contract;

import java.util.List;

public class ReverseModePresenter implements Contract.Presenter{
    private Contract.View viewImpl;
    ListRandomizer listRandomizer;
    Picasso picasso;
    ProfilesRepository profilesRepository;
    ProfilesRepository.Listener listener;


    public ReverseModePresenter(Contract.View viewImpl, ListRandomizer listRandomizer, Picasso picasso, ProfilesRepository profilesRepository, ProfilesRepository.Listener listener) {
        this.viewImpl = viewImpl;
        this.listRandomizer = listRandomizer;
        this.picasso = picasso;
        this.profilesRepository = profilesRepository;
        this.listener = listener;
    }


    @Override
    public void getData() {
        setListener();
    }

    private void setListener() {
        listener= new ProfilesRepository.Listener() {
            @Override
            public void onLoadFinished(@NonNull List<Person2> people) {
                Log.d("TEST", "onLoadFinished: ");
                //Gets List and Randomizes list, get only 5 elements

                randomList=listRandomizer.pickN(people, 6);
                randomPerson= listRandomizer.pickOne(randomList);

                loadImage(randomPerson);
                setNames(names ,randomList);
            }

            @Override
            public void onError(@NonNull Throwable error) {
                Log.d("TEST", "onError: "+error.getMessage());

            }
        };
    }
}
