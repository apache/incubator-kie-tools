/*
 * Copyright 2015 JBoss, by Red Hat, Inc
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
package org.uberfire.client.workbench.events;

import org.uberfire.mvp.PlaceRequest;

/**
 * Fired by the framework each time a workbench editor or screen which was previously the selected/active part within a
 * panel has been hidden due to a different part in the same panel being selected.
 */
public class PlaceLostFocusEvent extends AbstractPlaceEvent {

    public PlaceLostFocusEvent( final PlaceRequest place ) {
        super( place );
    }

    @Override
    public String toString() {
        return "PlaceLostFocusEvent [place=" + getPlace() + "]";
    }

}
