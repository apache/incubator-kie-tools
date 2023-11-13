/*
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 *
 *  http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied.  See the License for the
 * specific language governing permissions and limitations
 * under the License.
 */


package org.kie.workbench.common.stunner.core.client.components.toolbox.actions;

import java.util.Collection;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.Map;
import java.util.Set;
import java.util.function.Predicate;
import java.util.function.Supplier;

import io.crysknife.client.ManagedInstance;
import jakarta.annotation.PreDestroy;
import jakarta.enterprise.context.Dependent;
import jakarta.enterprise.inject.Any;
import jakarta.inject.Inject;
import org.kie.workbench.common.stunner.core.api.DefinitionManager;
import org.kie.workbench.common.stunner.core.client.canvas.AbstractCanvasHandler;
import org.kie.workbench.common.stunner.core.definition.adapter.MorphAdapter;
import org.kie.workbench.common.stunner.core.definition.morph.MorphDefinition;
import org.kie.workbench.common.stunner.core.diagram.Metadata;
import org.kie.workbench.common.stunner.core.graph.Element;
import org.kie.workbench.common.stunner.core.graph.content.definition.Definition;
import org.kie.workbench.common.stunner.core.profile.DomainProfileManager;
import org.kie.workbench.common.stunner.core.util.DefinitionUtils;
import org.uberfire.mvp.Command;

/**
 * This factory builds a toolbox with a button for each target morph definition available
 * for the toolbox' related node.
 */
@Dependent
@MorphActionsToolbox
public class MorphActionsToolboxFactory
        extends AbstractActionsToolboxFactory {

    private final DefinitionUtils definitionUtils;
    private final DomainProfileManager profileManager;
    private final Supplier<MorphNodeToolboxAction> morphNodeActions;
    private final Command morphNodeActionsDestroyer;
    private final Supplier<ActionsToolboxView> views;
    private final Command viewsDestroyer;

    @Inject
    public MorphActionsToolboxFactory(final DefinitionUtils definitionUtils,
                                      final DomainProfileManager profileManager,
                                      final @Any ManagedInstance<MorphNodeToolboxAction> morphNodeActions,
                                      final @Any @MorphActionsToolbox ManagedInstance<ActionsToolboxView> views) {
        this(definitionUtils,
             profileManager,
             morphNodeActions::get,
             morphNodeActions::destroyAll,
             views::get,
             views::destroyAll);
    }

    MorphActionsToolboxFactory(final DefinitionUtils definitionUtils,
                               final DomainProfileManager profileManager,
                               final Supplier<MorphNodeToolboxAction> morphNodeActions,
                               final Command morphNodeActionsDestroyer,
                               final Supplier<ActionsToolboxView> views,
                               final Command viewsDestroyer) {
        this.definitionUtils = definitionUtils;
        this.profileManager = profileManager;
        this.morphNodeActions = morphNodeActions;
        this.morphNodeActionsDestroyer = morphNodeActionsDestroyer;
        this.views = views;
        this.viewsDestroyer = viewsDestroyer;
    }

    private DefinitionManager getDefinitionManager() {
        return definitionUtils.getDefinitionManager();
    }

    @Override
    protected ActionsToolboxView<?> newViewInstance() {
        return views.get();
    }

    @Override
    @SuppressWarnings("unchecked")
    public Collection<ToolboxAction<AbstractCanvasHandler>> getActions(final AbstractCanvasHandler canvasHandler,
                                                                       final Element<?> e) {
        final Set<ToolboxAction<AbstractCanvasHandler>> actions = new LinkedHashSet<>();
        if (null != e.asNode()) {
            final Element<? extends Definition<?>> element = (Element<? extends Definition<?>>) e;
            final Object definition = element.getContent().getDefinition();
            if (definitionUtils.hasMorphTargets(definition)) {
                final String id = getDefinitionManager().adapters().forDefinition().getId(definition).value();
                final MorphAdapter<Object> morphAdapter = getDefinitionManager().adapters().registry().getMorphAdapter(definition.getClass());
                final Iterable<MorphDefinition> morphDefinitions = morphAdapter.getMorphDefinitions(definition);
                if (null != morphDefinitions && morphDefinitions.iterator().hasNext()) {
                    final Metadata metadata = canvasHandler.getDiagram().getMetadata();
                    final Predicate<String> definitionsAllowedFilter = profileManager.isDefinitionIdAllowed(metadata);
                    final Map<String, MorphDefinition> definitionMap = new LinkedHashMap<>();
                    for (final MorphDefinition morphDefinition : morphDefinitions) {

                        final Iterable<String> morphTargets = morphAdapter.getTargets(definition,
                                                                                      morphDefinition);
                        if (null != morphTargets && morphTargets.iterator().hasNext()) {
                            for (final String morphTarget : morphTargets) {
                                if (!id.equals(morphTarget)
                                        && definitionsAllowedFilter.test(morphTarget)) {
                                    definitionMap.put(morphTarget,
                                                      morphDefinition);
                                }
                            }
                        }
                    }
                    // Create a morph toolbox action for each target morph candidate.
                    definitionMap.forEach((targetMorphId, morphDefinition) -> actions.add(morphNodeActions.get()
                                                                                                  .setMorphDefinition(morphDefinition)
                                                                                                  .setTargetDefinitionId(targetMorphId)));
                }
            }
        }
        return actions;
    }

    @PreDestroy
    public void destroy() {
        morphNodeActionsDestroyer.execute();
        viewsDestroyer.execute();
    }
}
