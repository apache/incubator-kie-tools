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

import static org.kie.soup.commons.validation.PortablePreconditions.checkNotNull;

@Portable
public class ResourceCopied implements UberFireEvent,
                                       ResourceChange {

    private Path destinationPath;
    private String message;

    public ResourceCopied(@MapsTo("destinationPath") final Path destinationPath,
                          @MapsTo("message") final String message) {
        this.destinationPath = checkNotNull("destinationPath",
                                            destinationPath);
        this.message = message;
    }

    @Override
    public ResourceChangeType getType() {
        return ResourceChangeType.COPY;
    }

    @Override
    public String getMessage() {
        return message;
    }

    public Path getDestinationPath() {
        return this.destinationPath;
    }
}
