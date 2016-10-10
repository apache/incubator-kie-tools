/*
 * Copyright 2016 Red Hat, Inc. and/or its affiliates.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * 	http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.kie.workbench.common.stunner.backend.service;

import org.jboss.errai.bus.server.annotations.Service;
import org.kie.workbench.common.stunner.core.lookup.criteria.Criteria;
import org.kie.workbench.common.stunner.core.lookup.definition.DefinitionLookupManager;
import org.kie.workbench.common.stunner.core.lookup.definition.DefinitionLookupRequest;
import org.kie.workbench.common.stunner.core.lookup.definition.DefinitionRepresentation;

import javax.inject.Inject;

@Service
public class DefinitionLookupService implements org.kie.workbench.common.stunner.core.remote.DefinitionLookupService {

    DefinitionLookupManager lookupManager;

    @Inject
    public DefinitionLookupService( @Criteria DefinitionLookupManager lookupManager ) {
        this.lookupManager = lookupManager;
    }

    @Override
    public LookupResponse<DefinitionRepresentation> lookup( DefinitionLookupRequest request ) {
        return lookupManager.lookup( request );
    }

}
