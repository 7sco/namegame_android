package com.willowtreeapps.namegame.ui.modesFragments.reverseMode.presenter;

import android.support.annotation.NonNull;
import com.willowtreeapps.namegame.core.ListRandomize;
import com.willowtreeapps.namegame.network.api.ProfilesRepository;
import com.willowtreeapps.namegame.network.api.model.Person;
import com.willowtreeapps.namegame.ui.modesFragments.reverseMode.ReverseModeContract;

import java.util.List;

public class ReverseModePresenter implements ReverseModeContract.Presenter{
    private ReverseModeContract.ViewContract viewImpl;
    private ListRandomize listRandomize;
    private ProfilesRepository profilesRepository;
    private ProfilesRepository.Listener listener;
    private Person randomPerson;
    private List<Person> randomList;
    private List<Person> downloadedList;

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
        randomList= listRandomize.pickN(downloadedList, 6);
        randomPerson= listRandomize.pickOne(randomList);

        loadImage(randomPerson);
        viewImpl.setNames(randomList);
    }

    /**
     * @param person
     * loadImage() send image url gather from the randomize list to be displayed
     */
    private void loadImage(Person person) {
        String url="";
        if(person.getHeadshot().getUrl()!=null){
            url= person.getHeadshot().getUrl();
            url="http://"+url.substring(2,url.length());
        }
        viewImpl.loadImage(url);
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
            viewImpl.animateFacesOut();
        }else {
            viewImpl.animateViewOut(position);
        }
    }

    @Override
    public void getClickedViewInfo(int position) {
        Person selectedPerson=randomList.get(position);
        onPersonSelected(position, selectedPerson);
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
        randomizeData();
    }


    @Override
    public void loadSavedPerson(Person personSaved) {
        loadImage(personSaved);
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
    public void updatedownloadedList(List<Person> downloadedList) {
        this.downloadedList=downloadedList;
    }

    /**
     * getAllData() gets random n people and a random person from there to be shown on screen
     */
    @Override
    public void getAllData() {
        viewImpl.sendRandomPerson(randomPerson);
        viewImpl.sendRandomList(randomList);
        viewImpl.sendMainList(downloadedList);
    }
}
