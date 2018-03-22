/*
 * Copyright 2016 Red Hat, Inc. and/or its affiliates.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.kie.workbench.common.stunner.core.client.session.event;

import org.jboss.errai.common.client.api.annotations.NonPortable;
import org.kie.workbench.common.stunner.core.diagram.Metadata;
import org.uberfire.workbench.events.UberFireEvent;

@NonPortable
public class SessionDestroyedEvent implements UberFireEvent {

    private final String sessionUUID;
    private final String diagramName;
    private final String graphUuid;
    private final Metadata metadata;

    public SessionDestroyedEvent(final String sessionUUID,
                                 final String diagramName,
                                 final String graphUuid,
                                 final Metadata metadata) {
        this.sessionUUID = sessionUUID;
        this.diagramName = diagramName;
        this.graphUuid = graphUuid;
        this.metadata = metadata;
    }

    public String getSessionUUID() {
        return sessionUUID;
    }

    public String getDiagramName() {
        return diagramName;
    }

    public String getGraphUuid() {
        return graphUuid;
    }

    public Metadata getMetadata() {
        return metadata;
    }
}
