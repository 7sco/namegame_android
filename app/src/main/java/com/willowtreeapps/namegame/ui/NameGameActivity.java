package com.willowtreeapps.namegame.ui;

import android.app.Fragment;
import android.app.FragmentManager;
import android.app.FragmentTransaction;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;

import com.willowtreeapps.namegame.R;
import com.willowtreeapps.namegame.core.NameGameApplication;
import com.willowtreeapps.namegame.ui.modesFragments.ReverseModeFragment;

public class NameGameActivity extends AppCompatActivity {

    private static final String FRAG_TAG = "NameGameFragmentTag";
    private android.support.v4.app.FragmentManager fragmentManager = getSupportFragmentManager();


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.name_game_activity);
        NameGameApplication.get(this).component().inject(this);
        
        getIntentExtras();
    }

    private void getIntentExtras() {
        Intent intent= getIntent();
        String mode=intent.getExtras().getString("mode");
        switch (mode){
            case "hint":

                break;
            case "normal":
                loadFragment(new NameGameFragment());
                break;
            case "mat":

                break;
            case "reverse":
                loadFragment(new ReverseModeFragment());

                break;
            case "stats":

                break;
        }
    }

    private void loadFragment(android.support.v4.app.Fragment fragment) {
        fragmentManager.beginTransaction()
                .replace(R.id.container, fragment)
                .addToBackStack(null)
                .commit();
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();

        finish();
    }
}
