package com.willowtreeapps.namegame.core;

import com.willowtreeapps.namegame.network.NetworkModule;
import com.willowtreeapps.namegame.ui.NameGameActivity;
import com.willowtreeapps.namegame.ui.NameGameFragment;
import com.willowtreeapps.namegame.ui.modesFragments.ReverseModeFragment;
import com.willowtreeapps.namegame.ui.modesFragments.presenter.ReverseModePresenter;
import com.willowtreeapps.namegame.ui.stats.StatsFragment;

import javax.inject.Singleton;

import dagger.Component;

@Singleton
@Component(modules = {
        ApplicationModule.class,
        NetworkModule.class,
})
public interface ApplicationComponent {
    void inject(NameGameActivity activity);
    void inject(NameGameFragment fragment);
    void inject(ReverseModeFragment fragment);
    void inject(ReverseModePresenter presenter);
}