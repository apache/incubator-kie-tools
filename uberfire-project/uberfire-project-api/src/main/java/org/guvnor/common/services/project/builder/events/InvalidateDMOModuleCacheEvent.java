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

import org.guvnor.common.services.project.model.Module;
import org.jboss.errai.common.client.api.annotations.Portable;
import org.uberfire.backend.vfs.Path;
import org.uberfire.rpc.SessionInfo;

import static org.kie.soup.commons.validation.PortablePreconditions.checkNotNull;

/**
 * Event to invalidate all entries in a DataModelOracleCache for the Project containing the given resource.
 * The resource path is used within the Event as all editors that could affect the validity of a DataModelOracleCache
 * entry will know their resource's Path but not the Project path without performing a server round-trip to resolve such.
 */
@Portable
public class InvalidateDMOModuleCacheEvent {

    private Path resourcePath;

    private Module module;

    private SessionInfo sessionInfo;

    public InvalidateDMOModuleCacheEvent() {
    }

    public InvalidateDMOModuleCacheEvent(SessionInfo sessionInfo, Module module, Path resourcePath) {
        checkNotNull("sessionInfo", sessionInfo);
        checkNotNull("project",
                     module);
        checkNotNull("resourcePath", resourcePath);
        this.sessionInfo = sessionInfo;
        this.module = module;
        this.resourcePath = resourcePath;
    }

    public Path getResourcePath() {
        return this.resourcePath;
    }

    public SessionInfo getSessionInfo() {
        return sessionInfo;
    }

    public Module getModule() {
        return module;
    }
}
