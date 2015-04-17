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

    protected boolean open = false;

    public AbstractActivity( final PlaceManager placeManager ) {
        this.placeManager = placeManager;
    }

    /**
     * Tracks start/shutdown lifecycle. Subclasses should always call <tt>super.onStartup()</tt> in methods that
     * override this one.
     */
    @Override
    public void onStartup( PlaceRequest place ) {
        this.place = checkNotNull( "place", place );
    }

    /**
     * Tracks open/closed lifecycle. Subclasses should always call <tt>super.onOpen()</tt> in methods that override this
     * one.
     */
    @Override
    public void onOpen() {
        if ( this.place == null ) {
            throw new IllegalStateException( "Activity " + this + " has not been started" );
        }
        if ( open ) {
            throw new IllegalStateException( "Activity " + this + " already open" );
        }
        open = true;
        placeManager.executeOnOpenCallback( this.place );
    }

    /**
     * Tracks open/closed lifecycle. Subclasses should always call <tt>super.onClose()</tt> in methods that override
     * this one.
     */
    @Override
    public void onClose() {
        if ( !open ) {
            throw new IllegalStateException( "Activity " + this + " not open" );
        }
        open = false;
    }

    /**
     * Tracks start/shutdown lifecycle. Subclasses should always call <tt>super.onShutdown()</tt> in methods that
     * override this one.
     */
    @Override
    public void onShutdown() {
        if ( this.place == null ) {
            throw new IllegalStateException( "Activity " + this + " has not been started" );
        }
        if ( open ) {
            throw new IllegalStateException( "Activity " + this + " is open" );
        }
        this.place = null;
    }

    @Override
    public PlaceRequest getPlace() {
        return place;
    }

    @Override
    public String toString() {
        return getClass().getName() + ( place == null ? " (not started)" : " for " + place );
    }
}
