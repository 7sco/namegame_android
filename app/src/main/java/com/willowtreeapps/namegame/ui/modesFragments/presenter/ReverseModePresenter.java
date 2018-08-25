package com.willowtreeapps.namegame.ui.modesFragments.presenter;

import android.support.annotation.NonNull;

import com.willowtreeapps.namegame.core.ListRandomize;
import com.willowtreeapps.namegame.network.api.ProfilesRepository;
import com.willowtreeapps.namegame.network.api.model2.Person2;
import com.willowtreeapps.namegame.ui.modesFragments.ReverseModeContract;
import java.util.List;

public class ReverseModePresenter implements ReverseModeContract.Presenter{
    private ReverseModeContract.ViewContract viewImpl;
    private ListRandomize listRandomize;
    private ProfilesRepository profilesRepository;
    private ProfilesRepository.Listener listener;
    private Person2 randomPerson;
    private List<Person2> randomList;
    private List<Person2> downloadedList;

    public ReverseModePresenter(ReverseModeContract.ViewContract viewImpl, ListRandomize listRandomize, ProfilesRepository profilesRepository) {
        this.viewImpl = viewImpl;
        this.listRandomize = listRandomize;
        this.profilesRepository = profilesRepository;
    }

    @Override
    public void getData() {
        setListener();
        profilesRepository.register(listener);
    }

    public void onPersonSelected(int position, Person2 person) {
        if (person==randomPerson){
            viewImpl.showToast("Correct!!");
            viewImpl.animateFacesOut();
        }else {
            viewImpl.animateViewOut(position);
        }
    }

    @Override
    public void getClickedViewInfo(int position) {
        Person2 selectedPerson=randomList.get(position);
        onPersonSelected(position, selectedPerson);
    }

    @Override
    public void unregisterListener() {

        profilesRepository.unregister(listener);
    }

    @Override
    public void reShuffle() {
        randomizeData();
    }

    private void setListener() {
        listener= new ProfilesRepository.Listener() {
            @Override
            public void onLoadFinished(@NonNull List<Person2> people) {
                //Gets List and Randomizes list, get only 5 elements
                downloadedList=people;
                randomizeData();
            }

            @Override
            public void onError(@NonNull Throwable error) {
                viewImpl.logMessage(error.getMessage());
            }
        };

    }

    private void randomizeData() {
        randomList= listRandomize.pickN(downloadedList, 6);
        randomPerson= listRandomize.pickOne(randomList);
        loadImage(randomPerson);
        viewImpl.setNames(randomList);
    }

    private void loadImage(Person2 person) {
        String url="";
        if(person.getHeadshot().getUrl()!=null){
            url= person.getHeadshot().getUrl();
            viewImpl.logMessage(url);
            url="http://"+url.substring(2,url.length());
        }
        viewImpl.loadImage(url);
    }

}
