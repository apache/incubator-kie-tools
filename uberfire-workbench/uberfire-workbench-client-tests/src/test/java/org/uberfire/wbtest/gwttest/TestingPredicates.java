package org.uberfire.wbtest.gwttest;

import org.uberfire.wbtest.client.main.DefaultScreenActivity;
import org.uberfire.wbtest.client.panels.docking.NestingScreen;

import com.google.common.base.Predicate;
import com.google.gwt.user.client.DOM;


public class TestingPredicates {

    /**
     * Returns true as long as no instances of DefaultScreenActivity have been created since
     * {@link DefaultScreenActivity#instanceCount} has been reset to 0.
     */
    public static final Predicate<Void> DEFAULT_SCREEN_NOT_LOADED = new Predicate<Void>() {
        @Override
        public boolean apply( Void input ) {
            return DefaultScreenActivity.instanceCount == 0;
        }
    };

    /**
     * Returns true as long as there are no instances of DefaultScreenActivity's view in the DOM.
     */
    public static final Predicate<Void> DEFAULT_SCREEN_NOT_VISIBLE = new Predicate<Void>() {
        @Override
        public boolean apply( Void input ) {
            return DOM.getElementById( "gwt-debug-" + DefaultScreenActivity.DEBUG_ID ) == null;
        }
    };

    /**
     * Returns true as long as there are no instances of NestingScreen loaded in the ActivityManager.
     */
    public static final Predicate<Void> NESTING_SCREEN_NOT_LOADED = new Predicate<Void>() {
        @Override
        public boolean apply( Void input ) {
            return NestingScreen.instanceCount == 0;
        }
    };
}
