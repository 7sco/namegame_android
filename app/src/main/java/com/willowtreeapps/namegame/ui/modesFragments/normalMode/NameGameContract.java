package com.willowtreeapps.namegame.ui.modesFragments.normalMode;

import com.willowtreeapps.namegame.network.api.model2.Person2;

import java.util.List;

public interface NameGameContract {
    interface ViewContract {

        void animateFacesOut();

        void showToast(String s);

        void animateViewOut(int position);

        void logMessage(String message);

        void setName(String fullName);


        void loadImage(List<Person2> randomList);

        void sendRandomList(List<Person2> randomList);

        void sendRandomPerson(Person2 randomPerson);

        void sendMainList(List<Person2> downloadedList);
    }

    interface Presenter{

        void getData();

        void getClickedViewInfo(int position);

        void unregisterListener();

        void reShuffle();

        void checkMatModeEnable(String modeNormal);

        void updatedownloadedList(List<Person2> downloadedList);

        void updateRandomList(List<Person2> randomList);

        void updateRandomPerson(Person2 randomPerson);
        void getallData();

        //void loadSavedPerson(Person2 randomPerson);
    }


}
