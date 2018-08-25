package com.willowtreeapps.namegame.ui.stats;

import android.content.SharedPreferences;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

import com.willowtreeapps.namegame.R;
import com.willowtreeapps.namegame.network.api.model2.Person2;

import java.util.ArrayList;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.Unbinder;

public class StatsActivity extends AppCompatActivity {

    @BindView(R.id.correct_num)
    TextView correctNum;
    @BindView(R.id.incorrect_num)
    TextView incorrectNum;
    @BindView(R.id.average_num)
    TextView averageNum;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_stats);
        ButterKnife.bind(this);
        if(savedInstanceState!=null){
            String correct=savedInstanceState.getString("correct");
            String incorrect=savedInstanceState.getString("incorrect");
            String average=savedInstanceState.getString("average");
            correctNum.setText(correct);
            incorrectNum.setText(incorrect);
            averageNum.setText(average);
        }
        else{
            getData();
        }
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);

        outState.putString("correct", correctNum.getText().toString());
        outState.putString("incorrect", incorrectNum.getText().toString());
        outState.putString("average", averageNum.getText().toString());
    }

    private void getData() {
        SharedPreferences prefs = this.getSharedPreferences("MyPrefsFile", MODE_PRIVATE);
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
        double percentage=correct/(totalGames * 1.0);
        return Math.round(percentage*100);
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        finish();
    }
}
