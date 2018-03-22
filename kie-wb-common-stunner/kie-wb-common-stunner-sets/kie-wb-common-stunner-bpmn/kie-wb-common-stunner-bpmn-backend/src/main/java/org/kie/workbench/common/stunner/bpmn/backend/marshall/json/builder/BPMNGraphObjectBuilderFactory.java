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

import java.util.function.Supplier;

import javax.enterprise.context.ApplicationScoped;
import javax.enterprise.inject.Instance;
import javax.inject.Inject;

import org.kie.workbench.common.stunner.bpmn.backend.marshall.json.oryx.OryxManager;
import org.kie.workbench.common.stunner.bpmn.workitem.ServiceTask;
import org.kie.workbench.common.stunner.bpmn.workitem.WorkItemDefinitionRegistry;
import org.kie.workbench.common.stunner.core.api.DefinitionManager;
import org.kie.workbench.common.stunner.core.backend.definition.adapter.reflect.BackendDefinitionAdapter;
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

    private final DefinitionManager definitionManager;
    private final OryxManager oryxManager;
    private final Supplier<WorkItemDefinitionRegistry> workItemDefinitionRegistries;

    public BPMNGraphObjectBuilderFactory() {
        this.definitionManager = null;
        this.oryxManager = null;
        this.workItemDefinitionRegistries = null;
    }

    @Inject
    public BPMNGraphObjectBuilderFactory(final DefinitionManager definitionManager,
                                         final OryxManager oryxManager,
                                         final Instance<WorkItemDefinitionRegistry> workItemDefinitionRegistries) {
        this.definitionManager = definitionManager;
        this.oryxManager = oryxManager;
        this.workItemDefinitionRegistries = workItemDefinitionRegistries::get;
    }

    public BPMNGraphObjectBuilderFactory(final DefinitionManager definitionManager,
                                         final OryxManager oryxManager,
                                         final Supplier<WorkItemDefinitionRegistry> workItemDefinitionRegistries) {
        this.definitionManager = definitionManager;
        this.oryxManager = oryxManager;
        this.workItemDefinitionRegistries = workItemDefinitionRegistries;
    }

    @Override
    public GraphObjectBuilder<?, ?> bootstrapBuilder() {
        return new BootstrapObjectBuilder(this);
    }

    @Override
    @SuppressWarnings("all")
    public GraphObjectBuilder<?, ?> builderFor(final String oryxId) {
        if (oryxId == null) {
            throw new NullPointerException();
        }
        final Class<?> defClass = oryxManager.getMappingsManager().getDefinition(oryxId);
        if (null != defClass) {
            final String defId = oryxManager.getMappingsManager().getDefinitionId(oryxId);
            if (ServiceTask.class.equals(defClass)) {
                return new ServiceTaskNodeBuilder(defId,
                                                  workItemDefinitionRegistries);
            }
            MorphAdapter<Object> morphAdapter = definitionManager.adapters().registry().getMorphAdapter(defClass);
            BindablePropertyMorphDefinition propertyMorphDefinition = null;
            if (null != morphAdapter) {
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
                Class<? extends ElementFactory> elementFactory = BackendDefinitionAdapter.getGraphFactory(defClass);
                if (isNodeFactory(elementFactory)) {
                    return new NodeBuilderImpl(defClass,
                                               defId);
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
