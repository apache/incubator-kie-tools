package org.uberfire.wbtest.client.api;

import java.util.Collection;
import java.util.Collections;

import org.uberfire.client.mvp.AbstractWorkbenchScreenActivity;
import org.uberfire.client.mvp.PlaceManager;

/**
 * Provides default implementations for most of the screen activity interface.
 */
public abstract class AbstractTestScreenActivity extends AbstractWorkbenchScreenActivity {

    public AbstractTestScreenActivity( PlaceManager placeManager ) {
        super( placeManager );
    }

    @Override
    public String getTitle() {
        return getClass().getName();
    }

}
