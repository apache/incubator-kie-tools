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

import static org.uberfire.commons.validation.PortablePreconditions.*;

import org.uberfire.mvp.PlaceRequest;

/**
 * Implementation of behaviour common to all activity types.
 * <p>
 * AbstractActivity is not typically subclassed directly, even by generated code. See the more purpose-based subclasses.
 */
public abstract class AbstractActivity implements Activity {

    protected PlaceManager placeManager;

    protected PlaceRequest place;

    public AbstractActivity( final PlaceManager placeManager ) {
        this.placeManager = placeManager;
    }

    @Override
    public void onStartup( PlaceRequest place ) {
        this.place = checkNotNull( "place", place );
    }

    @Override
    public void onOpen() {
        if ( this.place == null ) {
            throw new IllegalStateException( "Activity has not been started" );
        }
        placeManager.executeOnOpenCallback( this.place );
    }

    /** Does nothing. */
    @Override
    public void onClose() {
        // Do nothing.
    }

    /** Does nothing. */
    @Override
    public void onShutdown() {
        // Do nothing.
    }

    @Override
    public String toString() {
        return getClass().getName() + ": " + place;
    }
}
