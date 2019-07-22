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

import org.kie.workbench.common.stunner.core.StunnerTestingGraphStubAPI;
import org.kie.workbench.common.stunner.core.factory.definition.TypeDefinitionFactory;

import static org.kie.workbench.common.stunner.core.definition.adapter.binding.BindableAdapterUtils.getDefinitionSetId;
import static org.mockito.Mockito.doReturn;

public abstract class StunnerTestingGraphBackendAPI extends StunnerTestingGraphStubAPI {

    public abstract TypeDefinitionFactory<Object> getModelFactory();

    public static StunnerTestingGraphBackendAPI build(final Class<?> definitionSetType) throws IllegalAccessException, InstantiationException {
        return build(definitionSetType, null);
    }

    @SuppressWarnings("unchecked")
    public static StunnerTestingGraphBackendAPI build(final Class<?> definitionSetType,
                                                      final TypeDefinitionFactory<Object> modelFactory) throws IllegalAccessException, InstantiationException {
        final Object definitionSet = definitionSetType.newInstance();
        final TypeDefinitionFactory<Object> factory = null != modelFactory ?
                modelFactory :
                new StunnerTestingModelFactory(definitionSet);
        return new StunnerTestingGraphBackendAPI() {
            @Override
            protected void initAPI() {
                super.initAPI();
                doReturn(definitionSet).when(getDefinitionSetRegistry()).getDefinitionSetByType(definitionSetType);
                doReturn(definitionSet).when(getDefinitionSetRegistry()).getDefinitionSetById(getDefinitionSetId(definitionSetType));
            }

            @Override
            public TypeDefinitionFactory<Object> getModelFactory() {
                return factory;
            }
        };
    }

    @Override
    protected void initAPI() {
        api = new StunnerTestingBackendAPI();
    }

    private StunnerTestingBackendAPI getAPI() {
        return (StunnerTestingBackendAPI) api;
    }

    @Override
    protected void init() {
        super.init();
        api.factoryManager = new StunnerTestingBackendFactoryManager(getAPI().definitionManager,
                                                                     getAPI().registryFactory,
                                                                     getModelFactory(),
                                                                     graphFactory,
                                                                     edgeFactory,
                                                                     nodeFactory);
    }
}
