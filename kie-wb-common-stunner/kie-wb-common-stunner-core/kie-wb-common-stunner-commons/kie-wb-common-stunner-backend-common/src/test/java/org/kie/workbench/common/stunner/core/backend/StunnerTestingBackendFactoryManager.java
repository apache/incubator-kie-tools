/*
 * Copyright 2019 Red Hat, Inc. and/or its affiliates.
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

package org.kie.workbench.common.stunner.core.backend;

import org.kie.workbench.common.stunner.core.api.DefinitionManager;
import org.kie.workbench.common.stunner.core.factory.definition.TypeDefinitionFactory;
import org.kie.workbench.common.stunner.core.factory.graph.EdgeFactory;
import org.kie.workbench.common.stunner.core.factory.graph.GraphFactory;
import org.kie.workbench.common.stunner.core.factory.graph.NodeFactory;
import org.kie.workbench.common.stunner.core.factory.impl.EdgeFactoryImpl;
import org.kie.workbench.common.stunner.core.factory.impl.GraphFactoryImpl;
import org.kie.workbench.common.stunner.core.factory.impl.NodeFactoryImpl;
import org.kie.workbench.common.stunner.core.registry.RegistryFactory;
import org.kie.workbench.common.stunner.core.util.DefinitionUtils;

public class StunnerTestingBackendFactoryManager extends BackendFactoryManager {

    public StunnerTestingBackendFactoryManager(final DefinitionUtils definitionUtils,
                                               final RegistryFactory registryFactory,
                                               final TypeDefinitionFactory<Object> modelFactory) {
        this(definitionUtils.getDefinitionManager(),
             registryFactory,
             modelFactory,
             new GraphFactoryImpl(definitionUtils.getDefinitionManager()),
             new EdgeFactoryImpl(definitionUtils.getDefinitionManager()),
             new NodeFactoryImpl(definitionUtils));
    }

    public StunnerTestingBackendFactoryManager(final DefinitionManager definitionManager,
                                               final RegistryFactory registryFactory,
                                               final TypeDefinitionFactory<Object> modelFactory,
                                               final GraphFactory graphFactory,
                                               final EdgeFactory<Object> edgeFactory,
                                               final NodeFactory<Object> nodeFactory) {
        super(definitionManager,
              registryFactory);
        setup(modelFactory,
              graphFactory,
              edgeFactory,
              nodeFactory);
    }

    @SuppressWarnings("unchecked")
    private void setup(final TypeDefinitionFactory<Object> modelFactory,
                       final GraphFactory graphFactory,
                       final EdgeFactory<Object> edgeFactory,
                       final NodeFactory<Object> nodeFactory) {
        registry().register(modelFactory);
        registry().register(graphFactory);
        registry().register(edgeFactory);
        registry().register(nodeFactory);
    }
}
