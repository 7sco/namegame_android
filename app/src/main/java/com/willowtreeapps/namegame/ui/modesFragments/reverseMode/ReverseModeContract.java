package com.willowtreeapps.namegame.ui.modesFragments.reverseMode;

import com.willowtreeapps.namegame.core.ListRandomize;
import com.willowtreeapps.namegame.network.api.model2.Person2;

import java.io.Serializable;
import java.util.List;

public interface ReverseModeContract {
    interface ViewContract {

        void loadImage(String url);

        void setNames(List<Person2> randomList);

        void animateFacesOut();

        void showToast(String s);

        void animateViewOut(int position);

        void logMessage(String message);

        void sendRandomList(List<Person2> randomList);

        void sendRandomPerson(Person2 randomPerson);

        void sendMainList(List<Person2> downloadedList);
    }

    interface Presenter{

        void getData();

        void getClickedViewInfo(int position);

        void unregisterListener();

        void reShuffle();

        void getRandomList();

        void loadSavedPerson(Person2 personSaved);

        void loadSavedRandomList(List<Person2> listSaved);

        void updateRandomList(List<Person2> randomList);

        void updateRandomPerson(Person2 randomPerson);

        void updatedownloadedList(List<Person2> downloadedList);

        void getallData();
    }


}
