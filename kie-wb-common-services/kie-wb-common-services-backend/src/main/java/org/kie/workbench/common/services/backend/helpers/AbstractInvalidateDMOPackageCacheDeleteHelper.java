/*
 * Copyright 2017 Red Hat, Inc. and/or its affiliates.
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

package org.kie.workbench.common.services.backend.helpers;

import javax.enterprise.event.Event;

import org.guvnor.common.services.project.builder.events.InvalidateDMOPackageCacheEvent;
import org.uberfire.backend.vfs.Path;
import org.uberfire.ext.editor.commons.backend.service.helper.DeleteHelper;
import org.uberfire.workbench.type.ResourceTypeDefinition;

/**
 * DeleteHelper to invalidate LRUDataModelOracleCache entries when a file is deleted.
 */
public abstract class AbstractInvalidateDMOPackageCacheDeleteHelper<T extends ResourceTypeDefinition> implements DeleteHelper {

    private T resourceType;
    private Event<InvalidateDMOPackageCacheEvent> invalidateDMOPackageCache;

    public AbstractInvalidateDMOPackageCacheDeleteHelper( final T resourceType,
                                                          final Event<InvalidateDMOPackageCacheEvent> invalidateDMOPackageCache ) {
        this.resourceType = resourceType;
        this.invalidateDMOPackageCache = invalidateDMOPackageCache;
    }

    @Override
    public boolean supports( final Path path ) {
        return resourceType.accept( path );
    }

    @Override
    public void postProcess( final Path path ) {
        if ( supports( path ) ) {
            invalidateDMOPackageCache.fire( new InvalidateDMOPackageCacheEvent( path ) );
        }
    }

}
