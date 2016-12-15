/*
 * Copyright 2016 Red Hat, Inc. and/or its affiliates.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *   http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.kie.workbench.common.stunner.backend;

import org.kie.workbench.common.stunner.core.api.AbstractDefinitionManager;
import org.kie.workbench.common.stunner.core.definition.DefinitionSetProxy;
import org.kie.workbench.common.stunner.core.definition.adapter.*;
import org.kie.workbench.common.stunner.core.registry.RegistryFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.annotation.PostConstruct;
import javax.enterprise.context.ApplicationScoped;
import javax.enterprise.inject.Instance;
import javax.inject.Inject;

@ApplicationScoped
public class ApplicationDefinitionManager extends AbstractDefinitionManager {

    private static final Logger LOG = LoggerFactory.getLogger( ApplicationDefinitionManager.class );

    Instance<DefinitionSetProxy<?>> definitionSetsInstances;
    Instance<DefinitionSetAdapter<?>> definitionSetAdapterInstances;
    Instance<DefinitionSetRuleAdapter<?>> definitionSetRuleAdapterInstances;
    Instance<DefinitionAdapter<?>> definitionAdapterInstances;
    Instance<PropertySetAdapter<?>> propertySetAdapterInstances;
    Instance<PropertyAdapter<?, ?>> propertyAdapterInstances;
    Instance<MorphAdapter<?>> morphAdapterInstances;

    protected ApplicationDefinitionManager() {
        super();
    }

    @Inject
    public ApplicationDefinitionManager( RegistryFactory registryFactory,
                                         AdapterManager adapterManager,
                                         Instance<DefinitionSetProxy<?>> definitionSetsInstances,
                                         Instance<DefinitionSetAdapter<?>> definitionSetAdapterInstances,
                                         Instance<DefinitionSetRuleAdapter<?>> definitionSetRuleAdapterInstances,
                                         Instance<DefinitionAdapter<?>> definitionAdapterInstances,
                                         Instance<PropertySetAdapter<?>> propertySetAdapterInstances,
                                         Instance<PropertyAdapter<?, ?>> propertyAdapterInstances,
                                         Instance<MorphAdapter<?>> morphAdapterInstances ) {
        super( registryFactory, adapterManager );
        this.definitionSetsInstances = definitionSetsInstances;
        this.definitionSetAdapterInstances = definitionSetAdapterInstances;
        this.definitionSetRuleAdapterInstances = definitionSetRuleAdapterInstances;
        this.definitionAdapterInstances = definitionAdapterInstances;
        this.propertySetAdapterInstances = propertySetAdapterInstances;
        this.propertyAdapterInstances = propertyAdapterInstances;
        this.morphAdapterInstances = morphAdapterInstances;
    }

    @PostConstruct
    public void init() {
        initAdapters();
        initMorphAdapters();
        // Once adapters present, add the Definition Sets found on current context.
        initDefSets();
    }

    private void initDefSets() {
        for ( DefinitionSetProxy definitionSet : definitionSetsInstances ) {
            addDefinitionSet( definitionSet.getDefinitionSet() );
        }
    }

    private void initAdapters() {
        for ( DefinitionSetAdapter definitionSetAdapter : definitionSetAdapterInstances ) {
            addAdapter( definitionSetAdapter );
        }
        for ( DefinitionSetRuleAdapter definitionSetRuleAdapter : definitionSetRuleAdapterInstances ) {
            addAdapter( definitionSetRuleAdapter );
        }
        for ( DefinitionAdapter definitionAdapter : definitionAdapterInstances ) {
            addAdapter( definitionAdapter );
        }
        for ( PropertySetAdapter propertySetAdapter : propertySetAdapterInstances ) {
            addAdapter( propertySetAdapter );
        }
        for ( PropertyAdapter propertyAdapter : propertyAdapterInstances ) {
            addAdapter( propertyAdapter );
        }
    }

    private void initMorphAdapters() {
        for ( MorphAdapter morphAdapter : morphAdapterInstances ) {
            addAdapter( morphAdapter );
        }
    }

}

