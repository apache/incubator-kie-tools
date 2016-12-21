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
import org.uberfire.backend.vfs.Path;
import org.uberfire.mvp.Command;

import static org.uberfire.commons.validation.PortablePreconditions.*;

public class LibraryContextSwitchEvent {

    private Path resourcePath;

    private EventType eventType;

    private Command contextSwitchedCallback;

    public LibraryContextSwitchEvent() {
    }

    public LibraryContextSwitchEvent( final EventType eventType ) {
        checkNotNull( "eventType", eventType );
        this.eventType = eventType;
    }

    public LibraryContextSwitchEvent( final EventType eventType,
                                      final Path resourcePath,
                                      final Command contextSwitchedCallback ) {
        checkNotNull( "eventType", eventType );
        checkNotNull( "resourcePath", resourcePath );
        this.eventType = eventType;
        this.resourcePath = resourcePath;
        this.contextSwitchedCallback = contextSwitchedCallback;
    }

    public Path getResourcePath() {
        return resourcePath;
    }

    public Command getContextSwitchedCallback() {
        return contextSwitchedCallback;
    }

    public boolean isProjectFromExample() {
        return eventType == EventType.PROJECT_FROM_EXAMPLE;
    }

    public boolean isProjectSelected() {
        return eventType == EventType.PROJECT_SELECTED;
    }

    public boolean isAssetSelected() {
        return eventType == EventType.ASSET_SELECTED;
    }

    @Portable
    public enum EventType {
        PROJECT_SELECTED, PROJECT_FROM_EXAMPLE, ASSET_SELECTED
    }
}
