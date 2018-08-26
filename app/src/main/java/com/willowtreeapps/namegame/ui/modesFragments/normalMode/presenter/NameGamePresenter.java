package com.willowtreeapps.namegame.ui.modesFragments.normalMode.presenter;

import android.support.annotation.NonNull;
import com.willowtreeapps.namegame.core.ListRandomize;
import com.willowtreeapps.namegame.network.api.ProfilesRepository;
import com.willowtreeapps.namegame.network.api.model.Person;
import com.willowtreeapps.namegame.ui.modesFragments.normalMode.NameGameContract;

import java.util.ArrayList;
import java.util.List;

import io.reactivex.Observable;
import io.reactivex.Observer;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.Disposable;
import io.reactivex.functions.Function;
import io.reactivex.schedulers.Schedulers;

public class NameGamePresenter implements NameGameContract.Presenter {

    private NameGameContract.ViewContract viewImpl;
    private ListRandomize listRandomize;
    private ProfilesRepository profilesRepository;
    private ProfilesRepository.Listener listener;
    private Person randomPerson;
    private List<Person> randomList;
    private List<Person> downloadedList;
    private List<Person> matList;
    private Boolean isMatMode;


    public NameGamePresenter(NameGameContract.ViewContract viewImpl, ListRandomize listRandomize, ProfilesRepository profilesRepository) {
        this.viewImpl = viewImpl;
        this.listRandomize = listRandomize;
        this.profilesRepository = profilesRepository;
    }

    @Override
    public void getData() {
        setListener();
        //profilesRepository.load();
        profilesRepository.register(listener);
    }

    /**
     * @param mode
     * checkMatModeEnable() helps the presenter know if a Mat filter should be apply to the data
     */
    @Override
    public void checkMatModeEnable(String mode) {
        if (mode != null && mode.equals("mat")) {
            isMatMode=true;
        }
        else {
            isMatMode=false;
        }
    }

    /**
     * setListener initiates the listener to be used throughout the activity lifeTime
     * it contains onLoadFinished() and onError() wich react to the data request to the network
     */
    private void setListener() {
        listener= new ProfilesRepository.Listener() {
            @Override
            public void onLoadFinished(@NonNull List<Person> people) {
                //Gets List and Randomizes list, get only 5 elements
                downloadedList=people;
                randomizeData();
            }
            @Override
            public void onError(@NonNull Throwable error) {
                viewImpl.showToast("Error");
            }
        };

    }

    /**
     * randomizeData() decides the game mode being played and according to it it will filter data downloaded
     */
    private void randomizeData() {
        if(isMatMode){
            Observable.just(downloadedList).subscribeOn(Schedulers.io())
                    .flatMapIterable((Function<List<Person>, List<Person>>) v -> v)
                    .filter(person2 -> person2.getFirstName().contains("Mat"))
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribe(getObserver());
        }
        else {
            fillData();
        }
    }

    /**
     * fillData() gets random n people and a random person from there to be shown on screen
     */
    private void fillData() {
        randomList=  listRandomize.pickN(downloadedList, 6);
        randomPerson= listRandomize.pickOne(randomList);
        setPersonName(randomPerson);
        loadImages(randomList);
    }

    /**
     * @param person
     * setPersonName() send name gather from the randomize list to be displayed
     */
    private void setPersonName(Person person) {
        String fullName= person.getFirstName()+" "+ person.getLastName();
        viewImpl.setName(fullName);
    }

    private Observer<Person> getObserver(){
        return  new Observer<Person>(){
            @Override
            public void onSubscribe(Disposable d) {
                matList= new ArrayList<>();
            }
            @Override
            public void onNext(Person person) { ;
                matList.add(person);
            }
            @Override
            public void onError(Throwable e) {
                viewImpl.showToast("Error");
            }
            @Override
            public void onComplete() {
                downloadedList=matList;
                fillData();
            }
        };
    }

    /**
     * @param people
     * loadImages() sned a list o people to the view which will display them
     */
    private void loadImages(List<Person> people) {
        viewImpl.loadImage(people);
    }

    @Override
    public void getClickedViewInfo(int position) {
        Person selectedPerson=randomList.get(position);
        onPersonSelected(position, selectedPerson);
    }

    /**
     * A method to handle when a person is selected
     *
     * @param position   The view position that was selected
     * @param person The person that was selected
     */
    public void onPersonSelected(int position, Person person) {
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

    /**
     * reShuffle() from downloaded list it will shuffle list again, new data will be shown
     * it help to not make a new network call
     */
    @Override
    public void reShuffle() {
        fillData();
    }

    /**
     * @param randomList
     * updateRandomList() update randomList after state change (rotation)
     */
    @Override
    public void updateRandomList(List<Person> randomList) {
        this.randomList=randomList;
    }

    /**
     * @param randomPerson
     * updateRandomPerson() update randomPerson after state change (rotation)
     */
    @Override
    public void updateRandomPerson(Person randomPerson) {
        this.randomPerson=randomPerson;
    }

    /**
     * @param downloadedList
     * updateDownloadedList() update downloadeList after state change (rotation)
     */
    @Override
    public void updateDownloadedList(List<Person> downloadedList) {
        this.downloadedList=downloadedList;
    }

    /**
     * getAllData() send Data to activity to be stored before screen rotation
     */
    @Override
    public void getAllData() {
        viewImpl.sendRandomPerson(randomPerson);
        viewImpl.sendRandomList(randomList);
        viewImpl.sendMainList(downloadedList);
    }
}
