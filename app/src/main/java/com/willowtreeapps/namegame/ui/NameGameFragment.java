package com.willowtreeapps.namegame.ui;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
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
import com.willowtreeapps.namegame.ui.modesFragments.NameGamePresenter;
import com.willowtreeapps.namegame.ui.modesFragments.NameGameContract;
import com.willowtreeapps.namegame.util.CircleBorderTransform;
import com.willowtreeapps.namegame.util.Ui;
import java.util.ArrayList;
import java.util.List;
import javax.inject.Inject;
import static android.content.Context.MODE_PRIVATE;

public class NameGameFragment extends Fragment implements OnClickListener, NameGameContract.ViewContract{

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
    private View view;
    private NameGamePresenter presenter;
    private int correctCounter=0;
    private int incorrectCounter=0;
    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        NameGameApplication.get(getActivity()).component().inject(this);
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        view= inflater.inflate(R.layout.name_game_fragment, container, false);
        return view;
    }


    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        setViews();
        presenter= new NameGamePresenter(this, listRandomize, profilesRepository);
        getMode();
        prefsUpdateStats();
        getData();
    }

    private void prefsUpdateStats() {
        correctCounter=prefs.getInt("correct",0);
        incorrectCounter=prefs.getInt("incorrect",0);
    }

    private void setViews() {
        title = view.findViewById(R.id.title);
        container = view.findViewById(R.id.face_container);
        playAgainButton = view.findViewById(R.id.playAgain);
        playAgainButton.setVisibility(View.INVISIBLE);
    }

    private void getMode() {
        prefs = this.getActivity().getSharedPreferences("MyPrefsFile", MODE_PRIVATE);
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
        int imageSize = (int) Ui.convertDpToPixel(100, getContext());
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
        Toast.makeText(getContext(), message, Toast.LENGTH_SHORT).show();
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
    public void onDestroyView() {
        super.onDestroyView();
        SharedPreferences.Editor editor = this.getActivity().getSharedPreferences("MyPrefsFile", MODE_PRIVATE).edit();
        editor.putInt("correct", correctCounter);
        editor.putInt("incorrect", incorrectCounter);
        editor.apply();
    }


    @Override
    public void onDetach() {
        super.onDetach();
        presenter.unregisterListener();
    }

}
