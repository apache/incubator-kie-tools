package org.uberfire.wbtest.client.api;

import java.util.Collection;
import java.util.Collections;

import org.uberfire.client.mvp.AbstractSplashScreenActivity;
import org.uberfire.client.mvp.PlaceManager;
import org.uberfire.client.workbench.widgets.splash.SplashView;


public abstract class AbstractTestSplashScreenActivity extends AbstractSplashScreenActivity {

    public AbstractTestSplashScreenActivity( PlaceManager placeManager, SplashView view ) {
        super( placeManager, view );
    }

    @Override
    public String getTitle() {
        return getClass().getName();
    }

    @Override
    public Integer getBodyHeight() {
        return 300;
    }

    @Override
    public Collection<String> getRoles() {
        return Collections.<String>emptyList();
    }

    @Override
    public Collection<String> getTraits() {
        return Collections.<String>emptyList();
    }

    @Override
    public String getSignatureId() {
        return getClass().getName();
    }

}
