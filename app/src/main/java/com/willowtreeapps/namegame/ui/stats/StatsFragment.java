package com.willowtreeapps.namegame.ui.stats;


import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.willowtreeapps.namegame.R;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.Unbinder;

import static android.content.Context.MODE_PRIVATE;


public class StatsFragment extends Fragment {

    @BindView(R.id.correct_num)
    TextView correctNum;
    @BindView(R.id.incorrect_num)
    TextView incorrectNum;
    @BindView(R.id.average_num)
    TextView averageNum;
    Unbinder unbinder;
    private View v;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        v = inflater.inflate(R.layout.fragment_stats, container, false);
        unbinder = ButterKnife.bind(this, v);
        getData();

        return v;
    }

    private void getData() {
        SharedPreferences prefs = this.getActivity().getSharedPreferences("MyPrefsFile", MODE_PRIVATE);
        int correctSharedPref = prefs.getInt("correct", 0);
        int incorrectSharedPref = prefs.getInt("incorrect", 0);
        setData(correctSharedPref, incorrectSharedPref);

    }

    private void setData(int correct, int incorrect) {
        correctNum.setText(String.valueOf(correct));
        incorrectNum.setText(String.valueOf(incorrect));
        String resultPercentage= String.valueOf(setAverage(correct,incorrect))+"%";
        averageNum.setText(resultPercentage);
    }

    private double setAverage(int correct, int incorrect) {
        int totalGames=correct+incorrect;
        double correctToDecimal=correct/100.0;
        return Math.round(correctToDecimal*totalGames);
    }


    @Override
    public void onDestroyView() {
        super.onDestroyView();
        unbinder.unbind();
    }
}
