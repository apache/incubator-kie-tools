package org.uberfire.client.screens.splash;

import org.jboss.errai.ioc.client.container.BeanActivator;

import com.google.gwt.user.client.Window;

import javax.inject.Singleton;

@Singleton
public class NoSplashQueryParamActivator implements BeanActivator {

    @Override
    public boolean isActivated() {
        return Window.Location.getParameter( "nosplash" ) == null;
    }

}
