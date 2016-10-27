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

package org.kie.workbench.common.stunner.backend.registry.impl;

import org.kie.workbench.common.stunner.core.backend.registry.impl.AbstractBackendRegistryFactory;
import org.kie.workbench.common.stunner.core.definition.adapter.AdapterManager;
import org.kie.workbench.common.stunner.core.registry.BackendRegistryFactory;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;

@ApplicationScoped
public class BackendRegistryFactoryImpl extends AbstractBackendRegistryFactory implements BackendRegistryFactory {

    protected BackendRegistryFactoryImpl() {
        this( null );
    }

    @Inject
    public BackendRegistryFactoryImpl( final AdapterManager adapterManager ) {
        super( adapterManager );
    }

}
