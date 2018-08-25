package com.willowtreeapps.namegame.ui.modesFragments.reverseMode;

import com.willowtreeapps.namegame.network.api.model.Person;

import java.util.List;

public interface ReverseModeContract {
    interface ViewContract {

        void loadImage(String url);

        void setNames(List<Person> randomList);

        void animateFacesOut();

        void showToast(String s);

        void animateViewOut(int position);

        void sendRandomList(List<Person> randomList);

        void sendRandomPerson(Person randomPerson);

        void sendMainList(List<Person> downloadedList);
    }

    interface Presenter{

        void getData();

        void getClickedViewInfo(int position);

        void unregisterListener();

        void reShuffle();

        void loadSavedPerson(Person personSaved);

        void updateRandomList(List<Person> randomList);

        void updateRandomPerson(Person randomPerson);

        void updatedownloadedList(List<Person> downloadedList);

        void getAllData();
    }
}
