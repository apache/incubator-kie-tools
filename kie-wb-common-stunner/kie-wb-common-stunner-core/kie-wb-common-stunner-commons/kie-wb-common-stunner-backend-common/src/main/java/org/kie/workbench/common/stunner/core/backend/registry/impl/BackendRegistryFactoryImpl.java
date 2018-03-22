/*
 * Copyright 2018 Red Hat, Inc. and/or its affiliates.
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

package org.kie.workbench.common.stunner.core.backend.registry.impl;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;

import org.kie.workbench.common.stunner.core.definition.adapter.AdapterManager;
import org.kie.workbench.common.stunner.core.diagram.Diagram;
import org.kie.workbench.common.stunner.core.registry.BackendRegistryFactory;
import org.kie.workbench.common.stunner.core.registry.diagram.DiagramRegistry;
import org.kie.workbench.common.stunner.core.registry.impl.AbstractRegistryFactory;

@ApplicationScoped
public class BackendRegistryFactoryImpl
        extends AbstractRegistryFactory
        implements BackendRegistryFactory {

    protected BackendRegistryFactoryImpl() {
        this(null);
    }

    @Inject
    public BackendRegistryFactoryImpl(final AdapterManager adapterManager) {
        super(adapterManager);
    }

    @Override
    public <T extends Diagram> DiagramRegistry<T> newDiagramSynchronizedRegistry() {
        return new SyncDiagramListRegistry<T>();
    }
}
