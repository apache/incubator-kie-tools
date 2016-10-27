/*
 * Copyright 2016 Red Hat, Inc. and/or its affiliates.
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *  
 *     http://www.apache.org/licenses/LICENSE-2.0
 *  
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.kie.workbench.common.stunner.core.backend.definition.adapter.binding;

import org.kie.workbench.common.stunner.core.api.FactoryManager;
import org.kie.workbench.common.stunner.core.definition.adapter.BindableMorphAdapter;
import org.kie.workbench.common.stunner.core.definition.morph.MorphDefinition;
import org.kie.workbench.common.stunner.core.definition.morph.MorphDefinitionProvider;
import org.kie.workbench.common.stunner.core.definition.util.DefinitionUtils;

import javax.annotation.PostConstruct;
import javax.enterprise.context.Dependent;
import javax.enterprise.inject.Instance;
import javax.inject.Inject;
import java.util.Collection;

@Dependent
public class RuntimeBindableMorphAdapter<S> extends BindableMorphAdapter<S> {

    Instance<MorphDefinitionProvider> morphDefinitionInstances;

    @Inject
    public RuntimeBindableMorphAdapter( DefinitionUtils definitionUtils,
                                        FactoryManager factoryManager,
                                        Instance<MorphDefinitionProvider> morphDefinitionInstances ) {
        super( definitionUtils, factoryManager );
        this.morphDefinitionInstances = morphDefinitionInstances;
    }

    public RuntimeBindableMorphAdapter( DefinitionUtils definitionUtils,
                                        FactoryManager factoryManager,
                                        Collection<MorphDefinition> morphDefinitions1 ) {
        super( definitionUtils, factoryManager );
        morphDefinitions.addAll( morphDefinitions1 );
    }

    @PostConstruct
    public void init() {
        initMorphDefinitions();
    }

    private void initMorphDefinitions() {
        if ( null != morphDefinitionInstances ) {
            for ( MorphDefinitionProvider morphDefinitionProvider : morphDefinitionInstances ) {
                morphDefinitions.addAll( morphDefinitionProvider.getMorphDefinitions() );
            }
        }
    }

    @Override
    protected <T> T doMerge( final S source,
                             final T result ) {
        // TODO: Merge beans in server side.
        //       See current logic on client side at ClientBindingUtils#merge
        //       For now the morphing operations are only performed on client side.
        return result;

    }

    @Override
    public boolean accepts( Class<?> type ) {
        return true;
    }

}
