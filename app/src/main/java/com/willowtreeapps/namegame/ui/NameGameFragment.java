package com.willowtreeapps.namegame.ui;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.annotation.NonNull;
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
import com.willowtreeapps.namegame.core.ListRandomizer;
import com.willowtreeapps.namegame.core.NameGameApplication;
import com.willowtreeapps.namegame.network.api.ProfilesRepository;

import com.willowtreeapps.namegame.network.api.model2.Person2;
import com.willowtreeapps.namegame.util.CircleBorderTransform;
import com.willowtreeapps.namegame.util.Ui;

import java.util.ArrayList;
import java.util.List;

import javax.inject.Inject;

import io.reactivex.Observable;
import io.reactivex.ObservableSource;
import io.reactivex.Observer;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.Disposable;
import io.reactivex.functions.Function;
import io.reactivex.functions.Predicate;
import io.reactivex.schedulers.Schedulers;

import static android.content.Context.MODE_PRIVATE;

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
    private Button playAgainButton;
    private List<ImageView> faces = new ArrayList<>(5);

    ProfilesRepository.Listener listener;

    private Person2 randomPerson;
    private List<Person2> randomList;
    private List<Person2> matList;
    private Boolean isMatMode=false;
    SharedPreferences prefs;

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
        playAgainButton = (Button) view.findViewById(R.id.playAgain);

        playAgainButton.setVisibility(View.INVISIBLE);

        getMode();

        getData();

    }

    private void getMode() {
        prefs = this.getActivity().getSharedPreferences("MyPrefsFile", MODE_PRIVATE);
        String modeMat = prefs.getString("modeNormal", null);
        if (modeMat != null) {
            if (modeMat.equals("mat")){
                isMatMode=true;
            }
        }
    }

    private void getData() {

        hideViews();
        listener= new ProfilesRepository.Listener() {
            @Override
            public void onLoadFinished(@NonNull List<Person2> people) {
                Log.d("TEST", "onLoadFinished: ");
                //Gets List and Randomizes list, get only 5 elements

                getRandomList(people);
                //randomList=listRandomizer.pickN(people, 6);
//                randomPerson= listRandomizer.pickOne(randomList);
//                title.setText(randomPerson.getFirstName());
//                setImages(faces ,randomList);
            }

            @Override
            public void onError(@NonNull Throwable error) {
                Log.d("TEST", "onError: "+error.getMessage());

            }
        };


        profilesRepository.register(listener);

        //animateFacesOut();
    }

    private void getRandomList(List<Person2> people) {
        if(isMatMode){
            //get all mat
             Observable.just(people).subscribeOn(Schedulers.io())
                    .flatMapIterable(new Function<List<Person2>, List<Person2>>() {
                        @Override public List<Person2> apply(List<Person2> v) {
                            return v;
                        }
                    })
                    .filter(person2 -> person2.getFirstName().contains("Mat"))
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribe(getObserver());
        }
        else {
           randomList=  listRandomizer.pickN(people, 6);
            randomPerson= listRandomizer.pickOne(randomList);
            String fullName=randomPerson.getFirstName()+" "+randomPerson.getLastName();
            title.setText(fullName);
            setImages(faces ,randomList);
        }
    }


        private Observer<Person2> getObserver(){
            return  new Observer<Person2>(){
                @Override
                public void onSubscribe(Disposable d) {
                    Log.d("Rx" ,"onSubscribe: SUBSCRIBED");
                    matList= new ArrayList<>();
                }

                @Override
                public void onNext(Person2 person2) {
                    Log.d("Rx", "onNext: "+person2.getFirstName());
                    matList.add(person2);
                }

                @Override
                public void onError(Throwable e) {
                    Log.d("Rx", "onError: ");
                }

                @Override
                public void onComplete() {
                    Log.d("Rx", "onComplete: ");
                    Log.d("Rx", "onComplete: "+matList.size());
                    randomList=listRandomizer.pickN(matList, 6);
                    randomPerson= listRandomizer.pickOne(matList);
                    title.setText(randomPerson.getFirstName()+" "+randomPerson.getLastName());
                    setImages(faces ,matList);
                }
            };
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
     * A method for setting the images from people into the imageviews
     */
    private void setImages(List<ImageView> faces, List<Person2> profiles) {
        List<Person2> people = profiles;
        int imageSize = (int) Ui.convertDpToPixel(100, getContext());
        int n = faces.size();

        for (int i = 0; i < n; i++) {
            ImageView face = faces.get(i);
            String url="";
            if(people.get(i).getHeadshot().getUrl()!=null){
                url= people.get(i).getHeadshot().getUrl();
                Log.d("Test", "setImages:"+url);
                url="http://"+url.substring(2,url.length());
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
    private void animateFacesOut() {
        title.animate().alpha(0).start();


        for (int i = faces.size()-1; i >= 0; i--) {
            ImageView face = faces.get(i);
            face.animate().scaleX(0).scaleY(0).setStartDelay(800 + 120 * i).setInterpolator(OVERSHOOT).start();
        }


        playAgainButton.setVisibility(View.VISIBLE);
        playAgainButton.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {

                profilesRepository.load();
                playAgainButton.setVisibility(View.INVISIBLE);

            }
        });
    }

    /**
     * A method to handle when a person is selected
     *
     * @param view   The view that was selected
     * @param person The person that was selected
     */
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

    private void animateViewOut(View view) {
        view.animate().scaleX(0).scaleY(0).setStartDelay(800 + 120).setInterpolator(OVERSHOOT).start();
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
