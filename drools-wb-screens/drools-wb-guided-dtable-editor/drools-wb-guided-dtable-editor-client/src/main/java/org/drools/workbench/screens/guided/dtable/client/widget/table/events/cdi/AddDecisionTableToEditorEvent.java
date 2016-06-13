/*
 * Copyright 2016 Red Hat, Inc. and/or its affiliates.
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
package org.drools.workbench.screens.guided.dtable.client.widget.table.events.cdi;

import org.uberfire.backend.vfs.ObservablePath;
import org.uberfire.commons.validation.PortablePreconditions;
import org.uberfire.mvp.impl.PathPlaceRequest;

public class AddDecisionTableToEditorEvent {

    private final PathPlaceRequest existingEditorPlaceRequest;
    private final ObservablePath newDecisionTablePath;

    public AddDecisionTableToEditorEvent( final PathPlaceRequest existingEditorPlaceRequest,
                                          final ObservablePath newDecisionTablePath ) {
        this.existingEditorPlaceRequest = PortablePreconditions.checkNotNull( "existingEditorPlaceRequest",
                                                                              existingEditorPlaceRequest );
        this.newDecisionTablePath = PortablePreconditions.checkNotNull( "newDecisionTablePath",
                                                                        newDecisionTablePath );
    }

    public PathPlaceRequest getExistingEditorPlaceRequest() {
        return existingEditorPlaceRequest;
    }

    public ObservablePath getNewDecisionTablePath() {
        return newDecisionTablePath;
    }

}
