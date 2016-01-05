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

import static org.uberfire.commons.validation.PortablePreconditions.*;

/**
 * An Event indicating a Resource has been copied
 */
@Portable
public class ResourceCopiedEvent extends ResourceCopied implements ResourceEvent {

    private Path sourcePath;
    private SessionInfo sessionInfo;

    public ResourceCopiedEvent( @MapsTo("sourcePath") final Path sourcePath,
                                @MapsTo("destinationPath") final Path destinationPath,
                                @MapsTo("message") final String message,
                                @MapsTo("sessionInfo") final SessionInfo sessionInfo ) {
        super( destinationPath, message );
        this.sourcePath = checkNotNull( "sourcePath", sourcePath );
        this.sessionInfo = checkNotNull( "sessionInfo", sessionInfo );
    }

    public SessionInfo getSessionInfo() {
        return sessionInfo;
    }

    @Override
    public Path getPath() {
        return this.sourcePath;
    }

    @Override
    public String toString() {
        return "ResourceCopiedEvent{" +
                "sourcePath=" + sourcePath +
                ", destinationPath=" + getDestinationPath() +
                ", message=" + getMessage() +
                ", sessionInfo=" + sessionInfo +
                '}';
    }
}
