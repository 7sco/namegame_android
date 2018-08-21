package com.willowtreeapps.namegame.network.api.model;

import android.os.Parcel;
import android.os.Parcelable;

import com.willowtreeapps.namegame.network.api.model2.Person2;

import java.util.ArrayList;
import java.util.List;

public class Profiles{ //implements Parcelable {

    private List<Person2> items;

//
//    public Profiles(List<Person2> items) {
//        this.items = items;
//
//    }
//
//    private Profiles(Parcel in) {
//        this.items = new ArrayList<>();
//        in.readList(this.items, Person2.class.getClassLoader());
//
//    }
//
    public List<Person2> getPeople() {
        return items;
    }
//
//
//
//    @Override
//    public void writeToParcel(Parcel dest, int flags) {
//        dest.writeList(this.items);
//    }
//
//    public static final Creator<Profiles> CREATOR = new Creator<Profiles>() {
//        @Override
//        public Profiles createFromParcel(Parcel source) {
//            return new Profiles(source);
//        }
//
//        @Override
//        public Profiles[] newArray(int size) {
//            return new Profiles[size];
//        }
//    };
//
//    @Override
//    public int describeContents() {
//        return 0;
//    }

    public List<Person2> getItems() {
        return items;
    }

    public void setItems(List<Person2> items) {
        this.items = items;
    }
}