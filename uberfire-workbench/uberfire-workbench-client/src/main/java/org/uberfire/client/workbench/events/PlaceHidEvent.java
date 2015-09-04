/*
 *
 *  * Copyright 2012 JBoss Inc
 *  *
 *  * Licensed under the Apache License, Version 2.0 (the "License"); you may not
 *  * use this file except in compliance with the License. You may obtain a copy of
 *  * the License at
 *  *
 *  * http://www.apache.org/licenses/LICENSE-2.0
 *  *
 *  * Unless required by applicable law or agreed to in writing, software
 *  * distributed under the License is distributed on an "AS IS" BASIS, WITHOUT
 *  * WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the
 *  * License for the specific language governing permissions and limitations under
 *  * the License.
 *
 */
package org.uberfire.client.workbench.events;

import org.uberfire.mvp.PlaceRequest;

/**
 * Fired by the framework each time a workbench editor or screen gets hid.
 */
public class PlaceHidEvent extends AbstractPlaceEvent {

    public PlaceHidEvent(final PlaceRequest place) {
        super(place);
    }

    @Override
    public String toString() {
        return "PlaceHidEvent [place=" + getPlace() + "]";
    }


}
