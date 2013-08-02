/*
 * Copyright 2012 JBoss Inc
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *       http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.uberfire.client.mvp;

import javax.enterprise.event.Event;
import javax.inject.Inject;

import org.uberfire.client.workbench.context.WorkbenchContext;
import org.uberfire.mvp.Command;
import org.uberfire.mvp.PlaceRequest;
import org.uberfire.workbench.events.ContextUpdateEvent;

/**
 * Base class for Activities
 */
public abstract class AbstractActivity
        implements
        Activity {

    @Inject
    protected Event<ContextUpdateEvent> contextUpdateEvent;

    @Inject
    protected WorkbenchContext wstatecontext;

    protected PlaceManager placeManager;

    protected PlaceRequest place;

    protected Command callback;

    public AbstractActivity( final PlaceManager placeManager ) {
        this.placeManager = placeManager;
    }

    @Override
    public void launch( final PlaceRequest place,
                        final Command callback ) {
        this.place = place;
        this.callback = callback;
    }

    @Override
    public void onOpen() {
        executeOnRevealCallback();
        placeManager.executeOnRevealCallback( this.place );
    }

    private void executeOnRevealCallback() {
        if ( callback != null ) {
            callback.execute();
        }
    }
}
