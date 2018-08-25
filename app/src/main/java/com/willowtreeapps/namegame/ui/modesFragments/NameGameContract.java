package com.willowtreeapps.namegame.ui.modesFragments;

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
    }

    interface Presenter{

        void getData();

        void getClickedViewInfo(int position);

        void unregisterListener();

        void reShuffle();

        void checkMatModeEnable(String modeNormal);
    }


}
