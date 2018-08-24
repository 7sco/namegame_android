package com.willowtreeapps.namegame.ui;

import android.app.Fragment;
import android.app.FragmentManager;
import android.app.FragmentTransaction;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;

import com.willowtreeapps.namegame.R;
import com.willowtreeapps.namegame.core.NameGameApplication;
import com.willowtreeapps.namegame.ui.modesFragments.ReverseModeFragment;

public class NameGameActivity extends AppCompatActivity {

    private static final String FRAG_TAG = "NameGameFragmentTag";
    private android.support.v4.app.FragmentManager fragmentManager = getSupportFragmentManager();

    SharedPreferences.Editor editor ;

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
        editor = getSharedPreferences("MyPrefsFile", MODE_PRIVATE).edit();
        switch (mode){
            case "hint":

                break;
            case "reverse":
                loadFragment(new ReverseModeFragment());
                break;
            case "stats":

                break;
                default:
                    editor.putString("modeNormal", mode);
                    loadFragment(new NameGameFragment());
        }
        editor.apply();
    }

    private void loadFragment(android.support.v4.app.Fragment fragment) {
        fragmentManager.beginTransaction()
                .replace(R.id.container, fragment)
                .addToBackStack(null)
                .addToBackStack("currentFragment")
                .commit();
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        finish();
    }
}
