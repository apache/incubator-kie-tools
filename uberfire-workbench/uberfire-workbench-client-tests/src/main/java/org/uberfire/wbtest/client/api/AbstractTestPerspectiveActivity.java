package org.uberfire.wbtest.client.api;

import java.util.Collection;
import java.util.Collections;

import org.uberfire.client.mvp.AbstractWorkbenchPerspectiveActivity;
import org.uberfire.client.mvp.PlaceManager;

/**
 * Provides default implementations for most of the persective activity interface.
 */
public abstract class AbstractTestPerspectiveActivity extends AbstractWorkbenchPerspectiveActivity {

    public AbstractTestPerspectiveActivity( PlaceManager placeManager ) {
        super( placeManager );
    }

    @Override
    public String getIdentifier() {
        return getClass().getName();
    }

    @Override
    public String getSignatureId() {
        return getClass().getName();
    }

    @Override
    public Collection<String> getRoles() {
        return Collections.emptyList();
    }

    @Override
    public Collection<String> getTraits() {
        return Collections.emptyList();
    }
}
