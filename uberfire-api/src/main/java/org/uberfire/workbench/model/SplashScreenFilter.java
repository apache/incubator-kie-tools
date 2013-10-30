package org.uberfire.workbench.model;

import java.util.Collection;

import org.uberfire.mvp.PlaceRequest;

public interface SplashScreenFilter {

    String getName();

    void setName( final String name );

    boolean displayNextTime();

    void setDisplayNextTime( final boolean value );

    Collection<String> getInterceptionPoints();

    void setInterceptionPoints( final Collection<String> places );

}
