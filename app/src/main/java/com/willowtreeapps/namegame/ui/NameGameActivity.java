package com.willowtreeapps.namegame.ui;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import com.willowtreeapps.namegame.R;
import com.willowtreeapps.namegame.core.NameGameApplication;
import com.willowtreeapps.namegame.ui.modesFragments.normalMode.NormalModeActivity;
import com.willowtreeapps.namegame.ui.modesFragments.reverseMode.ReverseModeActivity;
import com.willowtreeapps.namegame.ui.stats.StatsActivity;
import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class NameGameActivity extends AppCompatActivity {

    @BindView(R.id.textView2)
    TextView textView2;
    @BindView(R.id.normal_game)
    Button normalGame;
    @BindView(R.id.mat_game)
    Button matGame;
    @BindView(R.id.reverse_game)
    Button reverseGame;
    @BindView(R.id.textView4)
    TextView textView4;
    @BindView(R.id.stats_btn)
    Button statsBtn;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.name_game_activity);
        ButterKnife.bind(this);
        NameGameApplication.get(this).component().inject(this);
    }

    @OnClick({R.id.normal_game, R.id.mat_game, R.id.reverse_game, R.id.stats_btn})
    public void onViewClicked(View view) {
        Intent i= null;
        SharedPreferences.Editor editor = getSharedPreferences(getResources().getString(R.string.sharedP), MODE_PRIVATE).edit();

        switch (view.getId()) {
            case R.id.normal_game:
                i = new Intent(this, NormalModeActivity.class);
                editor.putString(getResources().getString(R.string.modeNormal), getResources().getString(R.string.normal));
                break;
            case R.id.mat_game:
                i = new Intent(this, NormalModeActivity.class);
                editor.putString(getResources().getString(R.string.modeNormal), getResources().getString(R.string.mat));
                break;
            case R.id.reverse_game:
                i = new Intent(this, ReverseModeActivity.class);
                break;
            case R.id.stats_btn:
                i= new Intent(this, StatsActivity.class);
                break;
        }
        editor.apply();
        startActivity(i);
    }


    @Override
    public void onBackPressed() {
        super.onBackPressed();
        finish();
    }

}
