package com.willowtreeapps.namegame.ui.modesFragments;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
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
import com.willowtreeapps.namegame.ui.modesFragments.presenter.ReverseModePresenter;
import com.willowtreeapps.namegame.util.CircleBorderTransform;
import com.willowtreeapps.namegame.util.Ui;

import java.util.ArrayList;
import java.util.List;

import javax.inject.Inject;

import static android.content.Context.MODE_PRIVATE;

public class ReverseModeFragment extends Fragment implements View.OnClickListener, ReverseModeContract.ViewContract {

    private static final Interpolator OVERSHOOT = new OvershootInterpolator();
    private static final String TAG= ReverseModeFragment.class.getSimpleName();

    @Inject
    ListRandomize listRandomize;
    @Inject
    Picasso picasso;
    @Inject
    ProfilesRepository profilesRepository;

    private ReverseModePresenter presenter;
    private ImageView imageOne;
    private List<TextView> names = new ArrayList<>(5);
    private ViewGroup container;
    private View view;
    private Button playAgainButton;
    private SharedPreferences prefs;
    private int correctCounter=0;
    private int incorrectCounter=0;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        NameGameApplication.get(getActivity()).component().inject(this);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.fragment_reverse_mode, container, false);
        return view;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        setViews(view);
        presenter= new ReverseModePresenter(this, listRandomize, profilesRepository);
        prefsUpdateStats();
        getData();
    }

    private void prefsUpdateStats() {
        prefs = this.getActivity().getSharedPreferences("MyPrefsFile", MODE_PRIVATE);
        correctCounter=prefs.getInt("correct",0);
        incorrectCounter=prefs.getInt("incorrect",0);
    }

    private void setViews(@NonNull View view) {
        imageOne=view.findViewById(R.id.imagePerson);
        container = view.findViewById(R.id.face_container);
        playAgainButton = view.findViewById(R.id.playAgain);
        playAgainButton.setVisibility(View.INVISIBLE);
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
        int imageSize = (int) Ui.convertDpToPixel(100, getContext());
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
        Toast.makeText(getContext(), message, Toast.LENGTH_SHORT).show();
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
    public void onDestroyView() {
        super.onDestroyView();
        presenter.unregisterListener();
        SharedPreferences.Editor editor = this.getActivity().getSharedPreferences("MyPrefsFile", MODE_PRIVATE).edit();
        editor.putInt("correct", correctCounter);
        editor.putInt("incorrect", incorrectCounter);
        editor.apply();
    }

//    @Override
//    public void onDetach() {
//        super.onDetach();
//        presenter.unregisterListener();
//    }

    @Override
    public void onPause() {
        super.onPause();
        presenter.unregisterListener();
    }
}
