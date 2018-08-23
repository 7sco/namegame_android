package com.willowtreeapps.namegame.ui.modesFragments;


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
import com.willowtreeapps.namegame.core.ListRandomizer;
import com.willowtreeapps.namegame.core.NameGameApplication;
import com.willowtreeapps.namegame.network.api.ProfilesRepository;
import com.willowtreeapps.namegame.network.api.model2.Person2;
import com.willowtreeapps.namegame.util.CircleBorderTransform;
import com.willowtreeapps.namegame.util.Ui;

import java.util.ArrayList;
import java.util.List;

import javax.inject.Inject;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.Unbinder;

public class ReverseModeFragment extends Fragment implements View.OnClickListener{

    private static final Interpolator OVERSHOOT = new OvershootInterpolator();

    @Inject
    ListRandomizer listRandomizer;
    @Inject
    Picasso picasso;
    @Inject
    ProfilesRepository profilesRepository;

    private ImageView imageOne;


    private List<TextView> names = new ArrayList<>(5);
    private Person2 randomPerson;
    private List<Person2> randomList;
    ProfilesRepository.Listener listener;

    private ViewGroup container;
    private View view;
    private Button playAgainButton;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        NameGameApplication.get(getActivity()).component().inject(this);

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        view = inflater.inflate(R.layout.fragment_reverse_mode, container, false);

        return view;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        imageOne=view.findViewById(R.id.imagePerson);
        container = (ViewGroup) view.findViewById(R.id.face_container);
        playAgainButton = (Button) view.findViewById(R.id.playAgain);


        playAgainButton.setVisibility(View.INVISIBLE);
        getData();
    }

    private void getData() {
        hideViews();
        listener= new ProfilesRepository.Listener() {
            @Override
            public void onLoadFinished(@NonNull List<Person2> people) {
                Log.d("TEST", "onLoadFinished: ");
                //Gets List and Randomizes list, get only 5 elements

                randomList=listRandomizer.pickN(people, 6);
                randomPerson= listRandomizer.pickOne(randomList);

                loadImage(randomPerson);
                setNames(names ,randomList);
            }

            @Override
            public void onError(@NonNull Throwable error) {
                Log.d("TEST", "onError: "+error.getMessage());

            }
        };



        profilesRepository.register(listener);
        //animateFacesOut();
    }

    private void setNames(List<TextView> names, List<Person2> profiles) {
        List<Person2> people = profiles;
        //int n = names.size();
        int n = 6;

        for (int i = 0; i < n; i++) {
            TextView face = names.get(i);
            face.setText(people.get(i).getFirstName());
        }
        animateFacesIn();
    }

    private void animateFacesIn() {
        imageOne.animate().alpha(1).start();
        for (int i = 0; i < names.size(); i++) {
            TextView face = names.get(i);
            face.animate().scaleX(1).scaleY(1).setStartDelay(800 + 120 * i).setInterpolator(OVERSHOOT).start();
        }
    }

    private void loadImage(Person2 person) {
        int imageSize = (int) Ui.convertDpToPixel(200, getContext());
        String url="";
        if(person.getHeadshot().getUrl()!=null){
            url= person.getHeadshot().getUrl();
            Log.d("Test", "setImages:"+url);
            url="http://"+url.substring(2,url.length());
        }
        picasso.get().load(url)
                .placeholder(R.drawable.ic_face_white_48dp)
                .resize(imageSize, imageSize)
                .transform(new CircleBorderTransform())
                .into(imageOne);
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

    private void animateFacesOut() {
        imageOne.animate().alpha(0).start();


        for (int i = names.size()-1; i >= 0; i--) {
            TextView face = names.get(i);
            face.animate().scaleX(0).scaleY(0).setStartDelay(800 + 120 * i).setInterpolator(OVERSHOOT).start();
        }


        playAgainButton.setVisibility(View.VISIBLE);
        playAgainButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                profilesRepository.load();
                playAgainButton.setVisibility(View.INVISIBLE);

            }
        });
    }

    private void animateViewOut(View view) {
        view.animate().scaleX(0).scaleY(0).setStartDelay(800 + 120).setInterpolator(OVERSHOOT).start();
    }

    private void onPersonSelected(@NonNull View view, @NonNull Person2 person) {
        //TODO evaluate whether it was the right person and make an action based on that
        Log.d("TEST", "onPersonSelected: "+person.getFirstName());

        if (person==randomPerson){
            Toast.makeText(getContext(), "WINNER !!!!", Toast.LENGTH_SHORT).show();
            animateFacesOut();
            //profilesRepository.unregister(listener);
        }else {
            animateViewOut(view);
        }

    }

    @Override
    public void onClick(View v) {
        Person2 selectedPerson= randomList.get(container.indexOfChild(v));
        onPersonSelected(v, selectedPerson);

    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        profilesRepository.unregister(listener);
    }

    @Override
    public void onDetach() {
        super.onDetach();
        profilesRepository.unregister(listener);
    }

    @Override
    public void onPause() {
        super.onPause();
        profilesRepository.unregister(listener);
    }

}
