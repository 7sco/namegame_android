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
import com.willowtreeapps.namegame.ui.stats.StatsFragment;

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
                loadFragment(new StatsFragment());
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
        clearStack();
        finish();
    }

    public void clearStack() {
        //Here we are clearing back stack fragment entries
        int backStackEntry = getSupportFragmentManager().getBackStackEntryCount();
        if (backStackEntry > 0) {
            for (int i = 0; i < backStackEntry; i++) {
                getSupportFragmentManager().popBackStackImmediate();
            }
        }

        //Here we are removing all the fragment that are shown here
        if (getSupportFragmentManager().getFragments() != null && getSupportFragmentManager().getFragments().size() > 0) {
            for (int i = 0; i < getSupportFragmentManager().getFragments().size(); i++) {
                android.support.v4.app.Fragment mFragment = getSupportFragmentManager().getFragments().get(i);
                if (mFragment != null) {
                    getSupportFragmentManager().beginTransaction().remove(mFragment).commit();
                }
            }
        }
    }
}
