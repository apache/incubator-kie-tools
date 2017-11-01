/*
 * Copyright 2017 Red Hat, Inc. and/or its affiliates.
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

package org.drools.workbench.screens.dsltext.backend.server;

import javax.enterprise.context.ApplicationScoped;
import javax.enterprise.event.Event;
import javax.inject.Inject;

import org.drools.workbench.screens.dsltext.type.DSLResourceTypeDefinition;
import org.guvnor.common.services.project.builder.events.InvalidateDMOPackageCacheEvent;
import org.kie.workbench.common.services.backend.helpers.AbstractInvalidateDMOPackageCacheDeleteHelper;

/**
 * DeleteHelper for DSLs to invalidate LRUDataModelOracleCache entries when a DSL is deleted.
 */
@ApplicationScoped
public class DSLInvalidateDMOPackageCacheDeleteHelper extends AbstractInvalidateDMOPackageCacheDeleteHelper<DSLResourceTypeDefinition> {

    @Inject
    public DSLInvalidateDMOPackageCacheDeleteHelper( final DSLResourceTypeDefinition resourceType,
                                                     final Event<InvalidateDMOPackageCacheEvent> invalidateDMOPackageCache ) {
        super( resourceType,
               invalidateDMOPackageCache );
    }

}
