/*
 * Copyright 2015 Red Hat, Inc. and/or its affiliates.
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

import org.uberfire.mvp.PlaceRequest;

import static org.kie.soup.commons.validation.PortablePreconditions.checkNotNull;

public abstract class AbstractActivity implements Activity {

    protected PlaceRequest place;

    protected boolean open = false;

    @Override
    public void onStartup(PlaceRequest place) {
        this.place = checkNotNull("place",
                                  place);
    }

    @Override
    public void onOpen() {
        if (this.place == null) {
            throw new IllegalStateException("Activity " + this + " has not been started");
        }
        if (open) {
            throw new IllegalStateException("Activity " + this + " already open");
        }
        open = true;
    }

    @Override
    public void onClose() {
        if (this.place == null) {
            throw new IllegalStateException("Activity " + this + " has not been started");
        }
        if (!open) {
            throw new IllegalStateException("Activity " + this + " not open");
        }
        open = false;
        place = null;
    }

    @Override
    public String toString() {
        return getClass().getName() + (place == null ? " (not started)" : " for " + place);
    }
}
