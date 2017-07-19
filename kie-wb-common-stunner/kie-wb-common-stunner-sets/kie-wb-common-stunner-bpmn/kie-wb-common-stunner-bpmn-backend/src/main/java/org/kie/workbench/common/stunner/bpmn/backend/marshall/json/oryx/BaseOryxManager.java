/*
 * Copyright 2016 Red Hat, Inc. and/or its affiliates.
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

package org.kie.workbench.common.stunner.bpmn.backend.marshall.json.oryx;

import java.util.LinkedList;
import java.util.List;
import java.util.Set;

import org.kie.workbench.common.stunner.bpmn.backend.marshall.json.oryx.property.Bpmn2OryxPropertyManager;

public abstract class BaseOryxManager implements OryxManager {

    protected OryxIdMappings oryxIdMappings;
    protected Bpmn2OryxPropertyManager oryxPropertyManager;

    protected final List<Class<?>> definitions = new LinkedList<>();

    protected BaseOryxManager() {
    }

    public BaseOryxManager(final OryxIdMappings oryxIdMappings,
                           final Bpmn2OryxPropertyManager oryxPropertyManager) {
        this.oryxIdMappings = oryxIdMappings;
        this.oryxPropertyManager = oryxPropertyManager;
    }

    @Override
    public void init() {
        // Load default & custom mappings for definitions.
        final Set<Class<?>> defClasses = getDefinitionClasses();
        definitions.addAll(defClasses);

        // Initialize the manager for the id mappings.
        oryxIdMappings.init(definitions);
    }

    @Override
    public OryxIdMappings getMappingsManager() {
        return oryxIdMappings;
    }

    @Override
    public Bpmn2OryxPropertyManager getPropertyManager() {
        return oryxPropertyManager;
    }

    @Override
    public List<Class<?>> getDefinitions() {
        return definitions;
    }
}
