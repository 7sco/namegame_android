package com.willowtreeapps.namegame.ui.modesActivity;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.willowtreeapps.namegame.R;
import com.willowtreeapps.namegame.ui.NameGameActivity;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;


public class ModesActivity extends AppCompatActivity {

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
        setContentView(R.layout.activity_modes);
        ButterKnife.bind(this);
    }


    @OnClick({R.id.normal_game, R.id.mat_game, R.id.reverse_game, R.id.stats_btn})
    public void onViewClicked(View view) {
        Intent i =new Intent(this, NameGameActivity.class);

        switch (view.getId()) {
            case R.id.normal_game:
                showToast("Normal Mode");
                i.putExtra("mode", "normal");
                break;
            case R.id.mat_game:
                showToast("Mat Mode");
                i.putExtra("mode", "mat");
                break;
            case R.id.reverse_game:
                showToast("Reverse Mode");
                i.putExtra("mode", "reverse");
                break;
            case R.id.stats_btn:
                showToast("Stats");
                i.putExtra("mode", "stats");
                break;
                default:
                    i=null;
        }

        startActivity(i);
    }

    private void showToast(String message) {
        Toast.makeText(this, message, Toast.LENGTH_LONG).show();
    }
}
