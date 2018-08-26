package com.willowtreeapps.namegame.ui.modesFragments.reverseMode;

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
import com.willowtreeapps.namegame.ui.modesFragments.reverseMode.presenter.ReverseModePresenter;
import com.willowtreeapps.namegame.util.CircleBorderTransform;
import com.willowtreeapps.namegame.util.Ui;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import javax.inject.Inject;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class ReverseModeActivity extends AppCompatActivity implements View.OnClickListener, ReverseModeContract.ViewContract {

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

    private ImageView imageOne;
    private ViewGroup container;
    private List<TextView> names = new ArrayList<>(5);
    private List<Person> randomList;
    private List<Person> downloadedList;
    private ReverseModePresenter presenter;
    private Person randomPerson;
    private int correctCounter = 0;
    private int incorrectCounter = 0;
    private boolean connected;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_reverse_mode);
        ButterKnife.bind(this);
        NameGameApplication.get(this).component().inject(this);
        setViews();
        presenter = new ReverseModePresenter(this, listRandomize, profilesRepository);
        prefsUpdateStats();
        networkStatus();
        manageRotation(savedInstanceState);
    }

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
            randomList = (ArrayList<Person>) savedInstanceState.getSerializable("randomList");
            downloadedList = (ArrayList<Person>) savedInstanceState.getSerializable("downloadedList");
            randomPerson = (Person) savedInstanceState.getSerializable("randomPerson");
            presenter.updatedownloadedList(downloadedList);
            presenter.updateRandomList(randomList);
            presenter.updateRandomPerson(randomPerson);
            hideViews();
            presenter.loadSavedPerson(randomPerson);
            setNames(randomList);
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
        imageOne = findViewById(R.id.image_one);
        container = findViewById(R.id.face_container);
        playAgainButton.setVisibility(View.INVISIBLE);
    }

    /**
     * prefsUpdateStats() get correct & incorrect count from sharedPreferences to be updated
     */
    private void prefsUpdateStats() {
        SharedPreferences prefs = this.getSharedPreferences(getResources().getString(R.string.sharedP), MODE_PRIVATE);
        correctCounter = prefs.getInt(getResources().getString(R.string.correct), 0);
        incorrectCounter = prefs.getInt(getResources().getString(R.string.incorrect), 0);
    }

    /**
     * getMode() gets mode type to determine what type of people is shown
     */
    private void getData() {
        hideViews();
        presenter.getData();
    }

    private void getDataOnConnection() {
        networkStatus();
        if (connected) {
            reloadBtn.setVisibility(View.GONE);
            getData();
        } else {
            reloadBtn.setVisibility(View.VISIBLE);
            Toast.makeText(this, getResources().getString(R.string.network_fail), Toast.LENGTH_SHORT).show();
        }
    }

    /**
     * A method to animate the faces into view
     */
    private void animateFacesIn() {
        imageOne.animate().alpha(1).start();
        for (int i = 0; i < names.size(); i++) {
            TextView face = names.get(i);
            face.animate().scaleX(1).scaleY(1).setStartDelay(800 + 120 * i).setInterpolator(OVERSHOOT).start();
        }
    }

    /**
     * A method to animate the faces into view
     */
    @Override
    public void animateFacesOut() {
        imageOne.animate().alpha(0).start();
        for (int i = names.size() - 1; i >= 0; i--) {
            TextView face = names.get(i);
            face.animate().scaleX(0).scaleY(0).setStartDelay(50 * i).setInterpolator(OVERSHOOT).start();
        }
        showPlayAgainButton();
    }

    /**
     * hideViews() Hide all views before getting data so no dummy data is shown
     */
    private void hideViews() {
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

    /**
     * A method for setting the images from people into the imageviews
     */
    @Override
    public void loadImage(String url) {
        int imageSize = (int) Ui.convertDpToPixel(100, this);
        if (url.equals("")) {
            url = getResources().getString(R.string.linkImage);
        }
        picasso.get().load(url)
                .placeholder(R.drawable.ic_face_white_48dp)
                .resize(imageSize, imageSize)
                .transform(new CircleBorderTransform())
                .into(imageOne);
    }

    /**
     * Set views at creation of activity
     */
    @Override
    public void setNames(List<Person> people) {
        int n = names.size();
        for (int i = 0; i < n; i++) {
            TextView face = names.get(i);
            String fullName = people.get(i).getFirstName() + " " + people.get(i).getLastName();
            face.setText(fullName);
        }
        animateFacesIn();
    }

    private void showPlayAgainButton() {
        correctCounter++;
        playAgainButton.setVisibility(View.VISIBLE);
    }

    @OnClick({R.id.reload_btn, R.id.play_again_btn})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.reload_btn:
                getDataOnConnection();
                break;
            case R.id.play_again_btn:
                presenter.reShuffle();
                playAgainButton.setVisibility(View.INVISIBLE);
                break;
        }
    }

    @Override
    public void onClick(View v) {
        presenter.getClickedViewInfo(container.indexOfChild(v));
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
        names.get(position).animate().scaleX(0).scaleY(0).setStartDelay(100).setInterpolator(OVERSHOOT).start();
        incorrectCounter++;
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
        finish();
    }

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
