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

package org.kie.workbench.common.stunner.bpmn.backend.marshall.json.oryx;

import org.kie.workbench.common.stunner.bpmn.BPMNDefinitionSet;
import org.kie.workbench.common.stunner.bpmn.backend.marshall.json.oryx.property.Bpmn2OryxPropertyManager;
import org.kie.workbench.common.stunner.core.backend.util.BackendBindableDefinitionUtils;

import javax.annotation.PostConstruct;
import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;

@ApplicationScoped
public class Bpmn2OryxManager {

    Bpmn2OryxIdMappings oryxIdMappings;
    Bpmn2OryxPropertyManager oryxPropertyManager;

    private final List<Class<?>> definitions = new LinkedList<>();

    protected Bpmn2OryxManager() {
    }

    @Inject
    public Bpmn2OryxManager( final Bpmn2OryxIdMappings oryxIdMappings,
                             final Bpmn2OryxPropertyManager oryxPropertyManager ) {
        this.oryxIdMappings = oryxIdMappings;
        this.oryxPropertyManager = oryxPropertyManager;
    }

    @PostConstruct
    public void init() {
        final BPMNDefinitionSet set = new BPMNDefinitionSet.BPMNDefinitionSetBuilder().build();
        // Load default & custom mappings for BPMN definitions.
        final Set<Class<?>> defClasses = BackendBindableDefinitionUtils.getDefinitions( set );
        definitions.addAll( defClasses );
        // Initialize the manager for the id mappings.
        oryxIdMappings.init( definitions );

    }

    public Bpmn2OryxIdMappings getMappingsManager() {
        return oryxIdMappings;
    }

    public Bpmn2OryxPropertyManager getPropertyManager() {
        return oryxPropertyManager;
    }

    public List<Class<?>> getDefinitions() {
        return definitions;
    }

}
