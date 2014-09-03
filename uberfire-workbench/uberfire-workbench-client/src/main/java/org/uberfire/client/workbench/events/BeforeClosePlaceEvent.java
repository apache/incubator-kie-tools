/*
 * Copyright 2012 JBoss Inc
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not
 * use this file except in compliance with the License. You may obtain a copy of
 * the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS, WITHOUT
 * WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the
 * License for the specific language governing permissions and limitations under
 * the License.
 */
package org.uberfire.client.workbench.events;

import static org.uberfire.commons.validation.PortablePreconditions.*;

import org.uberfire.client.mvp.PlaceManager;
import org.uberfire.mvp.PlaceRequest;
import org.uberfire.workbench.events.UberFireEvent;

/**
 * Fired by the PlaceManager before the process of closing a place begins.
 */
public class BeforeClosePlaceEvent implements UberFireEvent {

    private final PlaceRequest place;
    private final boolean force;

    /**
     * Applications should not fire this event, so there is no reason to create instances of this class!
     * <p>
     * This constructor will be removed in UberFire 0.6.
     * 
     * @deprecated Prior to UberFire 0.5, it was possible for an application to close a place by firing this event. This
     *             no longer works. Instead, use one of the PlaceManager.closeXXX methods.
     */
    @Deprecated
    public BeforeClosePlaceEvent( final PlaceRequest place ) {
        throw new UnsupportedOperationException("Don't fire this event from apps. Use one of the PlaceManager.closeXXX methods instead.");
    }

    /**
     * Applications should not fire this event, so there is no reason to create instances of this class!
     * <p>
     * This constructor will be removed in UberFire 0.6.
     * 
     * @deprecated Prior to UberFire 0.5, it was possible for an application to close a place by firing this event. This
     *             no longer works. Instead, use one of the PlaceManager.closeXXX methods.
     */
    @Deprecated
    public BeforeClosePlaceEvent( final PlaceRequest place,
                                  final boolean force ) {
        throw new UnsupportedOperationException("Don't fire this event from apps. Use one of the PlaceManager.closeXXX methods instead.");
    }

    /**
     * Internal workbench API. Don't use! If you want to close a place, use {@link PlaceManager#closePlace(PlaceRequest)}.
     * 
     * @param place The place that's about to be closed. Not null.
     * @param force Whether this will be a forced close operation.
     * @param frameworkInternal flag to differentiate from the deprecated/disabled constructors.
     */
    public BeforeClosePlaceEvent(PlaceRequest place, boolean force, boolean frameworkInternal) {
        this.place = checkNotNull( "place", place );
        this.force = force;
    }

    /**
     * Returns the place that is about to be closed. Never null.
     */
    public PlaceRequest getPlace() {
        return place;
    }

    /**
     * Indicates whether or not the place is being closed forcibly. See
     * {@link PlaceManager#forceClosePlace(PlaceRequest)} for details.
     */
    public boolean isForce() {
        return force;
    }

    @Override
    public String toString() {
        return "BeforeClosePlaceEvent [place=" + place + ", force=" + force + "]";
    }

}
