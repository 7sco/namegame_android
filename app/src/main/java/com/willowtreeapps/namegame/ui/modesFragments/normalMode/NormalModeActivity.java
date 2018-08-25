package com.willowtreeapps.namegame.ui.modesFragments.normalMode;

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
import com.willowtreeapps.namegame.ui.modesFragments.normalMode.presenter.NameGamePresenter;
import com.willowtreeapps.namegame.util.CircleBorderTransform;
import com.willowtreeapps.namegame.util.Ui;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import javax.inject.Inject;

public class NormalModeActivity extends AppCompatActivity implements View.OnClickListener, NameGameContract.ViewContract{

    private static final Interpolator OVERSHOOT = new OvershootInterpolator();

    @Inject
    ListRandomize listRandomize;
    @Inject
    Picasso picasso;
    @Inject
    ProfilesRepository profilesRepository;

    private TextView title;
    private ViewGroup container;
    private Button playAgainButton;
    private List<ImageView> faces = new ArrayList<>(5);
    private SharedPreferences prefs;
    private NameGamePresenter presenter;
    private int correctCounter=0;
    private int incorrectCounter=0;
    List<Person2> randomList;
    List<Person2> downloadedList;
    Person2 randomPerson;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_normal_mode);
        NameGameApplication.get(this).component().inject(this);

        setViews();
        presenter= new NameGamePresenter(this, listRandomize, profilesRepository);
        getMode();
        prefsUpdateStats();
        if(savedInstanceState!=null){
            randomList= (ArrayList<Person2>) savedInstanceState.getSerializable("randomList");
            downloadedList= (ArrayList<Person2>) savedInstanceState.getSerializable("downloadedList");
            randomPerson= (Person2) savedInstanceState.getSerializable("randomPerson");
            presenter.updatedownloadedList(downloadedList);
            presenter.updateRandomList(randomList);
            presenter.updateRandomPerson(randomPerson);
            String name=randomPerson.getFirstName()+ " "+randomPerson.getLastName();
            hideViews();
            setName(name);
            loadImage(randomList);
            //setNames(randomList);
        }
        else{
            getData();
        }

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
        correctCounter=prefs.getInt("correct",0);
        incorrectCounter=prefs.getInt("incorrect",0);
    }

    private void setViews() {
        title = findViewById(R.id.title);
        container = findViewById(R.id.face_container);
        playAgainButton = findViewById(R.id.playAgain);
        playAgainButton.setVisibility(View.INVISIBLE);
    }

    private void getMode() {
        prefs = this.getSharedPreferences("MyPrefsFile", MODE_PRIVATE);
        presenter.checkMatModeEnable(prefs.getString("modeNormal", null));
    }

    private void getData() {
        hideViews();
        presenter.getData();
    }

    private void hideViews() {
        //Hide the views until data loads
        title.setAlpha(0);
        int n = container.getChildCount();
        for (int i = 0; i < n; i++) {
            ImageView face = (ImageView) container.getChildAt(i);
            face.setOnClickListener(this);
            faces.add(face);

            //Hide the views until data loads
            face.setScaleX(0);
            face.setScaleY(0);
        }
    }

    /**
     * A method to animate the faces into view
     */
    private void animateFacesIn() {
        title.animate().alpha(1).start();
        for (int i = 0; i < faces.size(); i++) {
            ImageView face = faces.get(i);
            face.animate().scaleX(1).scaleY(1).setStartDelay(800 + 120 * i).setInterpolator(OVERSHOOT).start();
        }
    }

    /**
     * A method for setting the images from people into the imageviews
     */
    @Override
    public void loadImage(List<Person2> profiles) {
        int imageSize = (int) Ui.convertDpToPixel(100, this);
        int n = faces.size();
        for (int i = 0; i < n; i++) {
            ImageView face = faces.get(i);
            String url="";
            if(profiles.get(i).getHeadshot().getUrl()!=null){
                url= profiles.get(i).getHeadshot().getUrl();
                Log.d("Test", "setImages:"+url);
                url="http://"+url.substring(2,url.length());
            }
            else {
                url="http://grupsapp.com/wp-content/uploads/2016/04/willowtreeapps.png";
            }
            picasso.get().load(url)
                    .placeholder(R.drawable.ic_face_white_48dp)
                    .resize(imageSize, imageSize)
                    .transform(new CircleBorderTransform())
                    .into(face);
        }
        animateFacesIn();
    }


    /**
     * A method to animate the faces into view
     */
    @Override
    public void animateFacesOut() {
        title.animate().alpha(0).start();
        for (int i = faces.size()-1; i >= 0; i--) {
            ImageView face = faces.get(i);
            face.animate().scaleX(0).scaleY(0).setStartDelay(50 * i).setInterpolator(OVERSHOOT).start();
        }
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
        faces.get(position).animate().scaleX(0).scaleY(0).setStartDelay(100).setInterpolator(OVERSHOOT).start();
        incorrectCounter++;
    }

    @Override
    public void logMessage(String message) {
    }

    @Override
    public void setName(String fullName) {
        title.setText(fullName );
    }

    @Override
    public void onClick(View v) {
        presenter.getClickedViewInfo(container.indexOfChild(v));
    }



    @Override
    protected void onStop() {
        super.onStop();

        SharedPreferences.Editor editor = this.getSharedPreferences("MyPrefsFile", MODE_PRIVATE).edit();
        editor.putInt("correct", correctCounter);
        editor.putInt("incorrect", incorrectCounter);
        editor.apply();
        presenter.unregisterListener();
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


    @Override
    public void onBackPressed() {
        super.onBackPressed();
        finish();
    }
}
