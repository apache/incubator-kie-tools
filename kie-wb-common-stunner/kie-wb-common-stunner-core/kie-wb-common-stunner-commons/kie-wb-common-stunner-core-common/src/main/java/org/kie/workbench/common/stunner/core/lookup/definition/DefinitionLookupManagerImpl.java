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

package org.kie.workbench.common.stunner.core.lookup.definition;

import java.util.LinkedList;
import java.util.List;
import java.util.Set;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;

import org.kie.workbench.common.stunner.core.api.DefinitionManager;
import org.kie.workbench.common.stunner.core.api.FactoryManager;
import org.kie.workbench.common.stunner.core.definition.adapter.DefinitionAdapter;
import org.kie.workbench.common.stunner.core.factory.graph.ElementFactory;
import org.kie.workbench.common.stunner.core.lookup.criteria.AbstractCriteriaLookupManager;
import org.kie.workbench.common.stunner.core.registry.RegistryFactory;
import org.kie.workbench.common.stunner.core.registry.definition.DefinitionRegistry;
import org.kie.workbench.common.stunner.core.util.DefinitionUtils;

@ApplicationScoped
public class DefinitionLookupManagerImpl
        extends AbstractCriteriaLookupManager<String, DefinitionRepresentation, DefinitionLookupRequest>
        implements DefinitionLookupManager {

    DefinitionManager definitionManager;
    FactoryManager factoryManager;
    DefinitionRegistry<Object> registry;

    protected DefinitionLookupManagerImpl() {
    }

    @Inject
    public DefinitionLookupManagerImpl(final DefinitionManager definitionManager,
                                       final FactoryManager factoryManager,
                                       final RegistryFactory registryFactory) {
        this.definitionManager = definitionManager;
        this.factoryManager = factoryManager;
        this.registry = registryFactory.newDefinitionRegistry();
    }

    @Override
    protected List<String> getItems(final DefinitionLookupRequest request) {
        final String defSetId = request.getDefinitionSetId();
        final Object defSet = definitionManager.definitionSets().getDefinitionSetById(defSetId);
        if (null != defSet) {
            final Set<String> defs = definitionManager.adapters().forDefinitionSet().getDefinitions(defSet);
            return new LinkedList<>(defs);
        }
        return null;
    }

    @Override
    protected DefinitionRepresentation buildResult(final String defId) {
        final Object def = getDomainObject(defId);
        return buildRepresentation(defId,
                                   def);
    }

    @Override
    protected boolean matches(final String key,
                              final String value,
                              final String defId) {
        final Object def = getDomainObject(defId);
        final DefinitionAdapter<Object> definitionAdapter = definitionManager.adapters().registry().getDefinitionAdapter(def.getClass());
        switch (key) {
            case "id":
                return defId.equals(value);
            case "type":
                boolean isNode = isNode(def,
                                        definitionAdapter);
                return "node".equals(value) && isNode;
            case "labels":
                final Set<String> labelSet = toSet(value);
                if (null != labelSet) {
                    Set<String> defLabels = definitionAdapter.getLabels(def);
                    return isIntersect(labelSet,
                                       defLabels);
                }
                return true;
        }
        throw new UnsupportedOperationException("Cannot filter definitions by key [" + key + "]");
    }

    private Object getDomainObject(final String id) {
        Object definition = registry.getDefinitionById(id);
        if (null == definition) {
            definition = factoryManager.newDefinition(id);
            registry.register(definition);
        }
        return definition;
    }

    @SuppressWarnings("unchecked")
    private DefinitionRepresentation buildRepresentation(final String id,
                                                         final Object def) {
        final DefinitionAdapter<Object> definitionAdapter = definitionManager.adapters().registry().getDefinitionAdapter(def.getClass());
        final Set<String> labels = definitionAdapter.getLabels(def);
        boolean isNode = isNode(def,
                                definitionAdapter);
        return new DefinitionRepresentation(id,
                                            isNode,
                                            labels);
    }

    private boolean isNode(final Object def,
                           final DefinitionAdapter<Object> definitionAdapter) {
        final Class<? extends ElementFactory> elemFactoryType = definitionAdapter.getGraphFactoryType(def);
        return DefinitionUtils.isNodeFactory(elemFactoryType,
                                             factoryManager.registry());
    }
}
