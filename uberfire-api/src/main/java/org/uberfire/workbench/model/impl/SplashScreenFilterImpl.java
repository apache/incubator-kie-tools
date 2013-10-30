package org.uberfire.workbench.model.impl;

import java.util.ArrayList;
import java.util.Collection;

import org.jboss.errai.common.client.api.annotations.Portable;
import org.uberfire.workbench.model.SplashScreenFilter;

/**
 * Default implementation of SplashScreenFilter
 */
@Portable
public class SplashScreenFilterImpl implements SplashScreenFilter {

    private String name;
    private boolean displayNextTime;
    private Collection<String> interceptionPoints = new ArrayList<String>();

    public SplashScreenFilterImpl() {
    }

    public SplashScreenFilterImpl( final String name,
                                   final boolean displayNextTime,
                                   final Collection<String> interceptionPoints ) {
        this.name = name;
        this.displayNextTime = displayNextTime;
        this.interceptionPoints.addAll( interceptionPoints );
    }

    @Override
    public String getName() {
        return name;
    }

    @Override
    public void setName( final String name ) {
        this.name = name;
    }

    @Override
    public boolean displayNextTime() {
        return displayNextTime;
    }

    @Override
    public void setDisplayNextTime( final boolean value ) {
        this.displayNextTime = value;
    }

    @Override
    public Collection<String> getInterceptionPoints() {
        return interceptionPoints;
    }

    @Override
    public void setInterceptionPoints( final Collection<String> places ) {
        interceptionPoints.clear();
        interceptionPoints.addAll( places );
    }
}
