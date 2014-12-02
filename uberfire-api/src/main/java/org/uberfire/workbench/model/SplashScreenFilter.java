package org.uberfire.workbench.model;

import java.util.Collection;

import org.jboss.errai.common.client.api.annotations.Portable;

/**
 * Describes the current interception rules for a splash screen, including the user's current preference for whether
 * or not the screen should be displayed next time one of its interception points is matched.
 * <p>
 * All implementations of this interface must be marked as {@link Portable}.
 */
public interface SplashScreenFilter {

    String getName();

    void setName( final String name );

    boolean displayNextTime();

    void setDisplayNextTime( final boolean value );

    Collection<String> getInterceptionPoints();

    void setInterceptionPoints( final Collection<String> places );

}
