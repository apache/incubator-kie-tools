/*
 * Copyright 2016 Red Hat, Inc. and/or its affiliates.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * 
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
*/

package org.kie.workbench.common.screens.library.api;

import org.jboss.errai.common.client.api.annotations.Portable;
import static org.uberfire.commons.validation.PortablePreconditions.checkNotNull;

@Portable
public class LibraryContextSwitchEvent {

    private String uri;

    private EventType eventType;

    public LibraryContextSwitchEvent() {
    }

    public LibraryContextSwitchEvent( EventType eventType ) {
        checkNotNull( "eventType", eventType );
        this.eventType = eventType;
    }

    public LibraryContextSwitchEvent( EventType eventType, String uri ) {
        checkNotNull( "eventType", eventType );
        checkNotNull( "uri", uri );
        this.eventType = eventType;
        this.uri = uri;
    }

    public String getUri() {
        return uri;
    }

    public EventType getEventType() {
        return eventType;
    }

    public boolean isProjectFromExample() {
        return eventType == EventType.PROJECT_FROM_EXAMPLE;
    }

    public boolean isProjectSelected() {
        return eventType == EventType.PROJECT_SELECTED;
    }

    @Portable
    public enum EventType {
        PROJECT_SELECTED, PROJECT_FROM_EXAMPLE;
    }
}
