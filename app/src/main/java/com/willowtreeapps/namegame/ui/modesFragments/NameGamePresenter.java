package com.willowtreeapps.namegame.ui.modesFragments;

import android.support.annotation.NonNull;
import android.util.Log;

import com.willowtreeapps.namegame.core.ListRandomize;
import com.willowtreeapps.namegame.network.api.ProfilesRepository;
import com.willowtreeapps.namegame.network.api.model2.Person2;
import java.util.ArrayList;
import java.util.List;

import io.reactivex.Observable;
import io.reactivex.Observer;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.Disposable;
import io.reactivex.functions.Function;
import io.reactivex.schedulers.Schedulers;

public class NameGamePresenter implements NameGameContract.Presenter{
    private NameGameContract.ViewContract viewImpl;
    private ListRandomize listRandomize;
    private ProfilesRepository profilesRepository;
    private ProfilesRepository.Listener listener;
    private Person2 randomPerson;
    private List<Person2> randomList;
    private List<Person2> downloadedList;
    private List<Person2> matList;
    private Boolean isMatMode;


    public NameGamePresenter(NameGameContract.ViewContract viewImpl, ListRandomize listRandomize, ProfilesRepository profilesRepository) {
        this.viewImpl = viewImpl;
        this.listRandomize = listRandomize;
        this.profilesRepository = profilesRepository;
    }

    @Override
    public void getData() {
        setListener();
        profilesRepository.register(listener);

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
        if(isMatMode){
            //get all mat
            Observable.just(downloadedList).subscribeOn(Schedulers.io())
                    .flatMapIterable((Function<List<Person2>, List<Person2>>) v -> v)
                    .filter(person2 -> person2.getFirstName().contains("Mat"))
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribe(getObserver());
        }
        else {
            fillData(downloadedList);
        }
    }

    private void fillData(List<Person2> people) {
        randomList=  listRandomize.pickN(people, 6);
        randomPerson= listRandomize.pickOne(randomList);
        setPersonName(randomPerson);
        loadImages(randomList);
    }

    private void setPersonName(Person2 person) {
        String fullName= person.getFirstName()+" "+ person.getLastName();
        viewImpl.setName(fullName);
    }

    private Observer<Person2> getObserver(){
        return  new Observer<Person2>(){
            @Override
            public void onSubscribe(Disposable d) {
                Log.d("Rx" ,"onSubscribe: SUBSCRIBED");
                matList= new ArrayList<>();
            }

            @Override
            public void onNext(Person2 person2) {
                Log.d("Rx", "onNext: "+person2.getFirstName());
                matList.add(person2);
            }

            @Override
            public void onError(Throwable e) {
                Log.d("Rx", "onError: ");
            }

            @Override
            public void onComplete() {
                fillData(matList);
            }
        };
    }

    private void loadImages(List<Person2> people) {
        viewImpl.loadImage(people);
    }


    @Override
    public void getClickedViewInfo(int position) {
        Person2 selectedPerson=randomList.get(position);
        onPersonSelected(position, selectedPerson);
    }


    /**
     * A method to handle when a person is selected
     *
     * @param position   The view position that was selected
     * @param person The person that was selected
     */
    public void onPersonSelected(int position, Person2 person) {
        if (person==randomPerson){
            viewImpl.showToast("Correct!!");
            ///all out instead of one by one
            viewImpl.animateFacesOut();
        }else {
            viewImpl.animateViewOut(position);
        }
    }

    @Override
    public void unregisterListener() {
        profilesRepository.unregister(listener);

    }

    @Override
    public void reShuffle() {
        randomizeData();
    }

    @Override
    public void checkMatModeEnable(String mode) {
        if (mode != null && mode.equals("mat")) {
             isMatMode=true;
        }
        else {
            isMatMode=false;
        }
    }


}
