/*
 * Copyright 2016 Red Hat, Inc. and/or its affiliates.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *   http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.uberfire.wbtest.client.panels.custom;

import javax.enterprise.context.ApplicationScoped;
import javax.enterprise.event.Event;
import javax.inject.Inject;

@ApplicationScoped
public class CustomPanelInstanceCounter {

    @Inject
    Event<CustomPanelInstanceCounter> event;

    private int liveInstances;
    private int creationCount;

    public void instanceCreated() {
        liveInstances++;
        creationCount++;
        event.fire(this);
    }

    public void instanceDestroyed() {
        liveInstances--;
        event.fire(this);
    }

    /**
     * Returns the number of instances that are still alive (created but not destroyed).
     */
    public int getLiveInstances() {
        return liveInstances;
    }

    /**
     * Returns the total number of instances ever created (not decremented when an instance is destroyed).
     */
    public int getCreationCount() {
        return creationCount;
    }
}
