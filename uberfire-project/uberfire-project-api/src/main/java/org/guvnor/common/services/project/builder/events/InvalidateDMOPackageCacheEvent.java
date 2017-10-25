/*
 * Copyright 2015 Red Hat, Inc. and/or its affiliates.
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

package org.guvnor.common.services.project.builder.events;

import org.jboss.errai.common.client.api.annotations.Portable;
import org.kie.soup.commons.validation.PortablePreconditions;
import org.uberfire.backend.vfs.Path;

/**
 * Event to invalidate an entry in a DataModelOracleCache. The resource path is used within the Event
 * as all editors that could affect the validity of a DataModelOracleCache entry will know their resource's
 * Path but not the Project path without performing a server round-trip to resolve such.
 */
@Portable
public class InvalidateDMOPackageCacheEvent {

    private Path resourcePath;

    public InvalidateDMOPackageCacheEvent() {
    }

    public InvalidateDMOPackageCacheEvent(final Path resourcePath) {
        PortablePreconditions.checkNotNull("resourcePath",
                                           resourcePath);
        this.resourcePath = resourcePath;
    }

    public Path getResourcePath() {
        return this.resourcePath;
    }
}
