package com.willowtreeapps.namegame.ui.modesFragments.reverseMode;

import android.content.SharedPreferences;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Interpolator;
import android.view.animation.OvershootInterpolator;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.squareup.picasso.Picasso;
import com.willowtreeapps.namegame.R;
import com.willowtreeapps.namegame.core.ListRandomize;
import com.willowtreeapps.namegame.core.NameGameApplication;
import com.willowtreeapps.namegame.network.api.ProfilesRepository;
import com.willowtreeapps.namegame.network.api.model2.Person2;
import com.willowtreeapps.namegame.ui.modesFragments.reverseMode.presenter.ReverseModePresenter;
import com.willowtreeapps.namegame.util.CircleBorderTransform;
import com.willowtreeapps.namegame.util.Ui;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import javax.inject.Inject;

public class ReverseModeActivity extends AppCompatActivity implements View.OnClickListener, ReverseModeContract.ViewContract{

    private static final Interpolator OVERSHOOT = new OvershootInterpolator();
    private static final String TAG= ReverseModeActivity.class.getSimpleName();

    @Inject
    ListRandomize listRandomize;
    @Inject
    Picasso picasso;
    @Inject
    ProfilesRepository profilesRepository;

    private ReverseModePresenter presenter;
    private ImageView imageOne;
    private List<TextView> names = new ArrayList<>(5);;
    private ViewGroup container;
    private Button playAgainButton;
    private SharedPreferences prefs;
    private int correctCounter=0;
    private int incorrectCounter=0;
    List<Person2> randomList;
    List<Person2> downloadedList;
    Person2 randomPerson;
    int sizeArray=5;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_reverse_mode);
        NameGameApplication.get(this).component().inject(this);
        setViews();
        presenter= new ReverseModePresenter(this, listRandomize, profilesRepository);
        prefsUpdateStats();

        if(savedInstanceState!=null){
            randomList= (ArrayList<Person2>) savedInstanceState.getSerializable("randomList");
            downloadedList= (ArrayList<Person2>) savedInstanceState.getSerializable("downloadedList");
            randomPerson= (Person2) savedInstanceState.getSerializable("randomPerson");
            presenter.updatedownloadedList(downloadedList);
            presenter.updateRandomList(randomList);
            presenter.updateRandomPerson(randomPerson);
            hideViews();
            presenter.loadSavedPerson(randomPerson);
            setNames(randomList);
        }
        else{
            getData();
        }
    }


    private void setViews() {
        imageOne=findViewById(R.id.imagePerson);
        container =findViewById(R.id.face_container);
        playAgainButton = findViewById(R.id.playAgain);
        playAgainButton.setVisibility(View.INVISIBLE);
    }



    @Override
    public void onSaveInstanceState(@NonNull Bundle outState) {
        presenter.getallData();
        outState.putSerializable("randomList", (Serializable) randomList);
        outState.putSerializable("randomPerson", randomPerson);
        outState.putSerializable("downloadedList", (Serializable)downloadedList);
        super.onSaveInstanceState(outState);
    }

    private void prefsUpdateStats() {
        prefs = this.getSharedPreferences("MyPrefsFile", MODE_PRIVATE);
        correctCounter=prefs.getInt("correct",0);
        incorrectCounter=prefs.getInt("incorrect",0);
    }



    private void getData() {
        hideViews();
        presenter.getData();
    }

    private void animateFacesIn() {
        imageOne.animate().alpha(1).start();
        for (int i = 0; i < names.size(); i++) {
            TextView face = names.get(i);
            face.animate().scaleX(1).scaleY(1).setStartDelay(800 + 120 * i).setInterpolator(OVERSHOOT).start();
        }
    }
    @Override
    public void animateFacesOut() {
        imageOne.animate().alpha(0).start();
        for (int i = names.size()-1; i >= 0; i--) {
            TextView face = names.get(i);
            face.animate().scaleX(0).scaleY(0).setStartDelay(50 * i).setInterpolator(OVERSHOOT).start();
        }
        showPlayAgainButton();
    }

    @Override
    public void loadImage(String url) {
        int imageSize = (int) Ui.convertDpToPixel(100, this);
        if(url.equals("")){
            url="http://grupsapp.com/wp-content/uploads/2016/04/willowtreeapps.png";
        }
        picasso.get().load(url)
                .placeholder(R.drawable.ic_face_white_48dp)
                .resize(imageSize, imageSize)
                .transform(new CircleBorderTransform())
                .into(imageOne);
    }

    @Override
    public void setNames(List<Person2> people) {
        int n = names.size();
        for (int i = 0; i < n; i++) {
            TextView face = names.get(i);
            String fullName= people.get(i).getFirstName()+" "+people.get(i).getLastName();
            face.setText(fullName);
        }
        animateFacesIn();
    }


    private void showPlayAgainButton() {
        correctCounter++;
        playAgainButton.setVisibility(View.VISIBLE);
        playAgainButton.setOnClickListener(v -> {
            presenter.reShuffle();
            playAgainButton.setVisibility(View.INVISIBLE);
        });
    }

    @Override
    public void showToast(String message) {
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show();
    }

    @Override
    public void animateViewOut(int position) {
        names.get(position).animate().scaleX(0).scaleY(0).setStartDelay(100).setInterpolator(OVERSHOOT).start();
        incorrectCounter++;
    }

    @Override
    public void logMessage(String message) {
        Log.d(TAG, "logMessage: "+message);
    }

    @Override
    public void sendRandomList(List<Person2> randomList) {
        Log.d("Test1", "sendRandomList: "+randomList.get(0).getFirstName());
        this.randomList=randomList;
    }

    @Override
    public void sendRandomPerson(Person2 randomPerson) {
        Log.d("Test1", "sendRandomPerson: Person"+randomPerson.getFirstName());
        this.randomPerson=randomPerson;
    }

    @Override
    public void sendMainList(List<Person2> downloadedList) {
        this.downloadedList=downloadedList;
    }


    private void hideViews() {
        //Hide the views until data loads
        //imageOne.setAlpha(0);
        int n = container.getChildCount();
        for (int i = 0; i < n; i++) {
            TextView name = (TextView) container.getChildAt(i);
            name.setOnClickListener(this);
            names.add(name);
            //Hide the views until data loads
            name.setScaleX(0);
            name.setScaleY(0);
        }
    }

    @Override
    public void onClick(View v) {
        presenter.getClickedViewInfo( container.indexOfChild(v));
    }

    @Override
    protected void onDestroy() {
        presenter.unregisterListener();
        SharedPreferences.Editor editor = this.getSharedPreferences("MyPrefsFile", MODE_PRIVATE).edit();
        editor.putInt("correct", correctCounter);
        editor.putInt("incorrect", incorrectCounter);
        editor.apply();
        super.onDestroy();
    }

    //    @Override
//    public void onDetach() {
//        super.onDetach();
//        presenter.unregisterListener();
//    }

    @Override
    public void onPause() {
        super.onPause();
        //presenter.unregisterListener();
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        finish();
    }
}
