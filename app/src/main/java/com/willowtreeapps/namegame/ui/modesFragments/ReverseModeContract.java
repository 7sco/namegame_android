package com.willowtreeapps.namegame.ui.modesFragments;

import com.willowtreeapps.namegame.network.api.model2.Person2;

import java.util.List;

public interface ReverseModeContract {
    interface ViewContract {

        void loadImage(String url);

        void setNames(List<Person2> randomList);

        void animateFacesOut();

        void showToast(String s);

        void animateViewOut(int position);

        void logMessage(String message);
    }

    interface Presenter{

        void getData();

        void getClickedViewInfo(int position);

        void unregisterListener();

        void reShuffle();
    }


}
