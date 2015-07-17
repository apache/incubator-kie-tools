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

import org.uberfire.mvp.PlaceRequest;
import org.uberfire.workbench.events.UberFireEvent;

/**
 * Fired by the PlaceManager when the activity, panel, presenter, and view associated with a place are about to be
 * removed from the workbench. Observers of this event should clean up any of their own state associated with the given
 * place being live. It is not possible for an observer of this event to cancel the close operation.
 */
public class ClosePlaceEvent implements UberFireEvent {

    private final PlaceRequest place;

    public ClosePlaceEvent( final PlaceRequest place ) {
        this.place = place;
    }

    public PlaceRequest getPlace() {
        return place;
    }

    @Override
    public String toString() {
        return "ClosePlaceEvent [place=" + place + "]";
    }

}
