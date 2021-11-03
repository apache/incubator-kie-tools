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

package org.uberfire.workbench.events;

import org.jboss.errai.common.client.api.annotations.MapsTo;
import org.jboss.errai.common.client.api.annotations.Portable;
import org.uberfire.backend.vfs.Path;
import org.uberfire.rpc.SessionInfo;

import static org.kie.soup.commons.validation.PortablePreconditions.checkNotNull;

/**
 * An Event indicating a Resource has been updated
 */
@Portable
public class ResourceUpdatedEvent extends ResourceUpdated implements ResourceEvent {

    private Path path;
    private SessionInfo sessionInfo;

    public ResourceUpdatedEvent(@MapsTo("path") final Path path,
                                @MapsTo("message") final String message,
                                @MapsTo("sessionInfo") final SessionInfo sessionInfo) {
        super(message);
        this.path = checkNotNull("path",
                                 path);
        this.sessionInfo = checkNotNull("sessionInfo",
                                        sessionInfo);
    }

    @Override
    public Path getPath() {
        return this.path;
    }

    public SessionInfo getSessionInfo() {
        return sessionInfo;
    }

    @Override
    public String toString() {
        return "ResourceUpdatedEvent [path=" + path + ", sessionInfo=" + sessionInfo + "]";
    }
}
