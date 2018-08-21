package com.willowtreeapps.namegame.ui;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.view.animation.Interpolator;
import android.view.animation.OvershootInterpolator;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.squareup.picasso.Picasso;
import com.willowtreeapps.namegame.R;
import com.willowtreeapps.namegame.core.ListRandomizer;
import com.willowtreeapps.namegame.core.NameGameApplication;
import com.willowtreeapps.namegame.network.api.ProfilesRepository;
import com.willowtreeapps.namegame.network.api.model.Person;
import com.willowtreeapps.namegame.network.api.model.Profiles;
import com.willowtreeapps.namegame.network.api.model2.Person2;
import com.willowtreeapps.namegame.util.CircleBorderTransform;
import com.willowtreeapps.namegame.util.Ui;

import java.util.ArrayList;
import java.util.List;

import javax.inject.Inject;

public class NameGameFragment extends Fragment implements OnClickListener{

    private static final Interpolator OVERSHOOT = new OvershootInterpolator();

    @Inject
    ListRandomizer listRandomizer;
    @Inject
    Picasso picasso;
    @Inject
    ProfilesRepository profilesRepository;



    private TextView title;
    private ViewGroup container;
    private List<ImageView> faces = new ArrayList<>(5);

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        NameGameApplication.get(getActivity()).component().inject(this);
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.name_game_fragment, container, false);
    }


    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        title = (TextView) view.findViewById(R.id.title);
        container = (ViewGroup) view.findViewById(R.id.face_container);



        profilesRepository.register(new ProfilesRepository.Listener() {

            @Override
            public void onLoadFinished(@NonNull List<Person2> people) {
                Log.d("TEST", "onLoadFinished: ");

                setImages(faces ,people);

            }

            @Override
            public void onError(@NonNull Throwable error) {
                Log.d("TEST", "onError: "+error.getMessage());
            }
        });

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

        container.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                Log.d("Test=", "onTouch: "+v.getId()+" - "+event.toString());
                return false;
            }
        });

//        container.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                onPersonSelected(v,faces.get(container.tou));
//            }
//        });

        //Log.d("Test=", "onViewCreated: "+container.ge);




    }



    /**
     * A method for setting the images from people into the imageviews
     */
    private void setImages(List<ImageView> faces, List<Person2> profiles) {
        List<Person2> people = profiles;
        int imageSize = (int) Ui.convertDpToPixel(100, getContext());
        int n = faces.size();

        for (int i = 0; i < n; i++) {
            ImageView face = faces.get(i);

            String url= people.get(i).getHeadshot().getUrl();
            url="http://"+url.substring(2,url.length());
            Log.d("Test", "setImages:"+url);
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
    private void animateFacesIn() {
        title.animate().alpha(1).start();
        for (int i = 0; i < faces.size(); i++) {
            ImageView face = faces.get(i);
            face.animate().scaleX(1).scaleY(1).setStartDelay(800 + 120 * i).setInterpolator(OVERSHOOT).start();
        }
    }

    /**
     * A method to handle when a person is selected
     *
     * @param view   The view that was selected
     * @param person The person that was selected
     */
    private void onPersonSelected(@NonNull View view, @NonNull Person person) {
        //TODO evaluate whether it was the right person and make an action based on that
        Log.d("TEST", "onPersonSelected: "+person.getFirstName());
    }


    @Override
    public void onClick(View v) {
        Log.d("test==", "onClick: "+(Integer)v.getId());
        Log.d("test==", "onClick: "+container.indexOfChild(v));
        onPersonSelected(v, faces.get());


    }
}