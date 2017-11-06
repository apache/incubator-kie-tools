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

package org.kie.workbench.common.stunner.bpmn.backend.marshall.json.builder;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;

import org.kie.workbench.common.stunner.bpmn.backend.marshall.json.oryx.OryxManager;
import org.kie.workbench.common.stunner.core.api.DefinitionManager;
import org.kie.workbench.common.stunner.core.backend.definition.adapter.annotation.RuntimeDefinitionAdapter;
import org.kie.workbench.common.stunner.core.definition.adapter.BindableMorphAdapter;
import org.kie.workbench.common.stunner.core.definition.adapter.MorphAdapter;
import org.kie.workbench.common.stunner.core.definition.exception.DefinitionNotFoundException;
import org.kie.workbench.common.stunner.core.definition.morph.BindablePropertyMorphDefinition;
import org.kie.workbench.common.stunner.core.definition.morph.MorphDefinition;
import org.kie.workbench.common.stunner.core.factory.graph.EdgeFactory;
import org.kie.workbench.common.stunner.core.factory.graph.ElementFactory;
import org.kie.workbench.common.stunner.core.factory.graph.NodeFactory;

@ApplicationScoped
public class BPMNGraphObjectBuilderFactory implements GraphObjectBuilderFactory {

    DefinitionManager definitionManager;
    OryxManager oryxManager;

    @Inject
    public BPMNGraphObjectBuilderFactory(final DefinitionManager definitionManager,
                                         final OryxManager oryxManager) {
        this.definitionManager = definitionManager;
        this.oryxManager = oryxManager;
    }

    public BPMNGraphObjectBuilderFactory() {
    }

    @Override
    public GraphObjectBuilder<?, ?> bootstrapBuilder() {
        return new BootstrapObjectBuilder(this);
    }

    @Override
    @SuppressWarnings("unchecked")
    public GraphObjectBuilder<?, ?> builderFor(final String oryxId) {
        if (oryxId == null) {
            throw new NullPointerException();
        }
        Class<?> defClass = oryxManager.getMappingsManager().getDefinition(oryxId);
        if (null != defClass) {
            MorphAdapter<Object> morphAdapter = definitionManager.adapters().registry().getMorphAdapter(defClass);
            BindablePropertyMorphDefinition propertyMorphDefinition = null;
            if (morphAdapter != null) {
                final Iterable<MorphDefinition> morphDefinitions =
                        ((BindableMorphAdapter<Object>) morphAdapter).getMorphDefinitionsForType(defClass);
                if (null != morphDefinitions && morphDefinitions.iterator().hasNext()) {
                    for (MorphDefinition morphDefinition : morphDefinitions) {
                        if (morphDefinition instanceof BindablePropertyMorphDefinition) {
                            propertyMorphDefinition = (BindablePropertyMorphDefinition) morphDefinition;
                            break;
                        }
                    }
                }
            }
            if (null != propertyMorphDefinition) {
                // Specific handle for morphing based on class inheritance.
                return new NodePropertyMorphBuilderImpl(defClass,
                                                        propertyMorphDefinition);
            } else {
                Class<? extends ElementFactory> elementFactory = RuntimeDefinitionAdapter.getGraphFactory(defClass);
                if (isNodeFactory(elementFactory)) {
                    return new NodeBuilderImpl(defClass);
                } else if (isEdgeFactory(elementFactory)) {
                    return new EdgeBuilderImpl(defClass);
                } else {
                    throw new RuntimeException("No graph element found for definition with class [" + defClass.getName() + "]");
                }
            }
        }
        throw new DefinitionNotFoundException("No definition found for oryx stencil with id [" + oryxId + "]", oryxId);
    }

    private static boolean isNodeFactory(final Class<? extends ElementFactory> elementFactory) {
        return elementFactory.isAssignableFrom(NodeFactory.class);
    }

    private static boolean isEdgeFactory(final Class<? extends ElementFactory> elementFactory) {
        return elementFactory.isAssignableFrom(EdgeFactory.class);
    }
}
