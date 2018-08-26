package com.willowtreeapps.namegame.ui.modesFragments.normalMode;

import com.willowtreeapps.namegame.network.api.model.Person;

import java.util.List;

public interface NameGameContract {
    interface ViewContract {

        void animateFacesOut();

        void showToast(String s);

        void animateViewOut(int position);

        void setName(String fullName);

        void loadImage(List<Person> randomList);

        void sendRandomList(List<Person> randomList);

        void sendRandomPerson(Person randomPerson);

        void sendMainList(List<Person> downloadedList);
    }

    interface Presenter{

        void getData();

        void getClickedViewInfo(int position);

        void unregisterListener();

        void reShuffle();

        void checkMatModeEnable(String modeNormal);

        void updateDownloadedList(List<Person> downloadedList);

        void updateRandomList(List<Person> randomList);

        void updateRandomPerson(Person randomPerson);

        void getAllData();
    }
}
