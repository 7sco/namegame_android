package com.willowtreeapps.namegame.core;

import com.willowtreeapps.namegame.network.NetworkModule;
import com.willowtreeapps.namegame.ui.NameGameActivity;
import com.willowtreeapps.namegame.ui.modesFragments.normalMode.NormalModeActivity;
import com.willowtreeapps.namegame.ui.modesFragments.reverseMode.ReverseModeActivity;

import com.willowtreeapps.namegame.ui.modesFragments.reverseMode.presenter.ReverseModePresenter;

import javax.inject.Singleton;

import dagger.Component;

@Singleton
@Component(modules = {
        ApplicationModule.class,
        NetworkModule.class,
})
public interface ApplicationComponent {
    void inject(NameGameActivity activity);
    void inject(ReverseModeActivity activity);
    void inject(NormalModeActivity activity);
    void inject(ReverseModePresenter presenter);
}