package com.willowtreeapps.namegame.ui.modesFragments.normalMode;

import android.content.Context;
import android.content.SharedPreferences;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
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
import com.willowtreeapps.namegame.network.api.model.Person;
import com.willowtreeapps.namegame.ui.modesFragments.normalMode.presenter.NameGamePresenter;
import com.willowtreeapps.namegame.util.CircleBorderTransform;
import com.willowtreeapps.namegame.util.Ui;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import javax.inject.Inject;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class NormalModeActivity extends AppCompatActivity implements View.OnClickListener, NameGameContract.ViewContract {

    private static final Interpolator OVERSHOOT = new OvershootInterpolator();

    @Inject
    ListRandomize listRandomize;
    @Inject
    Picasso picasso;
    @Inject
    ProfilesRepository profilesRepository;
    @BindView(R.id.play_again_btn)
    Button playAgainButton;
    @BindView(R.id.reload_btn)
    Button reloadBtn;

    private TextView title;
    private ViewGroup container;
    private List<ImageView> faces = new ArrayList<>(5);
    private List<Person> randomList;
    private List<Person> downloadedList;
    private SharedPreferences prefs;
    private NameGamePresenter presenter;
    private Person randomPerson;
    private int correctCounter = 0;
    private int incorrectCounter = 0;
    private boolean connected;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_normal_mode);
        ButterKnife.bind(this);
        NameGameApplication.get(this).component().inject(this);
        setViews();
        presenter = new NameGamePresenter(this, listRandomize, profilesRepository);
        getMode();
        prefsUpdateStats();
        networkStatus();
        manageRotation(savedInstanceState);
    }

    /**
     * @param outState onSaveInstanceState() store data before rotation
     */
    @Override
    public void onSaveInstanceState(@NonNull Bundle outState) {
        presenter.getAllData();
        outState.putSerializable(getResources().getString(R.string.randomList), (Serializable) randomList);
        outState.putSerializable(getResources().getString(R.string.randomPerson), randomPerson);
        outState.putSerializable(getResources().getString(R.string.downloadedList), (Serializable) downloadedList);
        super.onSaveInstanceState(outState);
    }

    /**
     * @param savedInstanceState manageRotation() checks savedInstanceState to use data saved after rotation
     */
    private void manageRotation(Bundle savedInstanceState) {
        if (savedInstanceState != null && connected) {
            randomList = (ArrayList<Person>) savedInstanceState.getSerializable(getResources().getString(R.string.randomList));
            downloadedList = (ArrayList<Person>) savedInstanceState.getSerializable(getResources().getString(R.string.downloadedList));
            randomPerson = (Person) savedInstanceState.getSerializable(getResources().getString(R.string.randomPerson));
            presenter.updateDownloadedList(downloadedList);
            presenter.updateRandomList(randomList);
            presenter.updateRandomPerson(randomPerson);
            String name = randomPerson.getFirstName() + " " + randomPerson.getLastName();
            hideViews();
            setName(name);
            loadImage(randomList);
        } else {
            getDataOnConnection();
        }
    }

    private void networkStatus() {
        ConnectivityManager cm = (ConnectivityManager) getApplicationContext().getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo nInfo = cm.getActiveNetworkInfo();
        connected = nInfo != null && nInfo.isAvailable() && nInfo.isConnected();
    }

    /**
     * Set views at creation of activity
     */
    private void setViews() {
        title = findViewById(R.id.person_name);
        container = findViewById(R.id.face_container);
        playAgainButton.setVisibility(View.INVISIBLE);
    }

    /**
     * prefsUpdateStats() get correct & incorrect count from sharedPreferences to be updated
     */
    private void prefsUpdateStats() {
        correctCounter = prefs.getInt(getResources().getString(R.string.correct), 0);
        incorrectCounter = prefs.getInt(getResources().getString(R.string.incorrect), 0);
    }

    /**
     * getMode() gets mode type to determine what type of people is shown
     */
    private void getMode() {
        prefs = this.getSharedPreferences(getResources().getString(R.string.sharedP), MODE_PRIVATE);
        presenter.checkMatModeEnable(prefs.getString(getResources().getString(R.string.modeNormal), null));
    }

    /**
     * getData() hides views at creation and then proceeds to getData with network request
     */
    private void getData() {
        hideViews();
        presenter.getData();
    }

    private void getDataOnConnection() {
        networkStatus();
        if (connected) {
            getData();
            reloadBtn.setVisibility(View.GONE);
        } else {
            reloadBtn.setVisibility(View.VISIBLE);
            Toast.makeText(this, getResources().getString(R.string.network_fail), Toast.LENGTH_SHORT).show();
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
     * A method to animate the faces into view
     */
    @Override
    public void animateFacesOut() {
        title.animate().alpha(0).start();
        for (int i = faces.size() - 1; i >= 0; i--) {
            ImageView face = faces.get(i);
            face.animate().scaleX(0).scaleY(0).setStartDelay(50 * i).setInterpolator(OVERSHOOT).start();
        }
        correctCounter++;
        playAgainButton.setVisibility(View.VISIBLE);
    }

    /**
     * hideViews() Hide all views before getting data so no dummy data is shown
     */
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
     * A method for setting the images from people into the imageviews
     */
    @Override
    public void loadImage(List<Person> profiles) {
        int imageSize = (int) Ui.convertDpToPixel(100, this);
        int n = faces.size();
        for (int i = 0; i < n; i++) {
            ImageView face = faces.get(i);
            String url = "";
            if (profiles.get(i).getHeadshot().getUrl() != null) {
                url = profiles.get(i).getHeadshot().getUrl();
                url = getResources().getString(R.string.http) + url.substring(2, url.length());
            } else {
                url = getResources().getString(R.string.linkImage);
            }
            picasso.get().load(url)
                    .placeholder(R.drawable.ic_face_white_48dp)
                    .resize(imageSize, imageSize)
                    .transform(new CircleBorderTransform())
                    .into(face);
        }
        animateFacesIn();
    }

    @OnClick({R.id.play_again_btn, R.id.reload_btn})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.play_again_btn:
                presenter.reShuffle();
                playAgainButton.setVisibility(View.INVISIBLE);
                break;
            case R.id.reload_btn:
                getDataOnConnection();
                break;
        }
    }

    @Override
    public void showToast(String message) {
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show();
    }

    /**
     * @param position animateViewOut() hides view at given position
     */
    @Override
    public void animateViewOut(int position) {
        faces.get(position).animate().scaleX(0).scaleY(0).setStartDelay(100).setInterpolator(OVERSHOOT).start();
        incorrectCounter++;
    }

    @Override
    public void setName(String fullName) {
        title.setText(fullName);
    }

    @Override
    public void onClick(View v) {
        presenter.getClickedViewInfo(container.indexOfChild(v));
    }

    /**
     * @param randomList sendRandomList() send data retrieved after rotation to presenter
     */
    @Override
    public void sendRandomList(List<Person> randomList) {
        this.randomList = randomList;
    }

    /**
     * @param randomPerson sendRandomPerson() send data retrieved after rotation to presenter
     */
    @Override
    public void sendRandomPerson(Person randomPerson) {
        this.randomPerson = randomPerson;
    }

    /**
     * @param downloadedList sendMainList() send data retrieved after rotation to presenter
     */
    @Override
    public void sendMainList(List<Person> downloadedList) {
        this.downloadedList = downloadedList;
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        presenter.unregisterListener();
        finish();
    }

    /**
     * onStop() updates shared preferences stats
     */
    @Override
    protected void onStop() {
        super.onStop();
        SharedPreferences.Editor editor = this.getSharedPreferences(getResources().getString(R.string.sharedP), MODE_PRIVATE).edit();
        editor.putInt(getResources().getString(R.string.correct), correctCounter);
        editor.putInt(getResources().getString(R.string.incorrect), incorrectCounter);
        editor.apply();
    }

    @Override
    protected void onDestroy() {
        presenter.unregisterListener();
        super.onDestroy();
    }
}
