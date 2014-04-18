package org.uberfire.client.screens.splash;

import javax.inject.Singleton;

import org.jboss.errai.ioc.client.container.BeanActivator;

import com.google.gwt.user.client.Window;

/**
 * A simple example of a bean activator: reports true unless there is a "nosplash" parameter in the location bar.
 */
@Singleton
public class NoSplashQueryParamActivator implements BeanActivator {

    @Override
    public boolean isActivated() {
        return Window.Location.getParameter( "nosplash" ) == null;
    }

}
