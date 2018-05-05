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

package org.kie.workbench.common.dmn.client.canvas.controls.toolbox;

import java.util.Collection;
import java.util.LinkedHashSet;
import java.util.Set;

import javax.annotation.PreDestroy;
import javax.enterprise.context.Dependent;
import javax.enterprise.inject.Any;
import javax.inject.Inject;

import org.jboss.errai.ioc.client.api.ManagedInstance;
import org.kie.workbench.common.stunner.core.api.DefinitionManager;
import org.kie.workbench.common.stunner.core.client.canvas.AbstractCanvasHandler;
import org.kie.workbench.common.stunner.core.client.components.toolbox.actions.AbstractActionsToolboxFactory;
import org.kie.workbench.common.stunner.core.client.components.toolbox.actions.ActionsToolboxView;
import org.kie.workbench.common.stunner.core.client.components.toolbox.actions.CreateConnectorAction;
import org.kie.workbench.common.stunner.core.client.components.toolbox.actions.CreateNodeAction;
import org.kie.workbench.common.stunner.core.client.components.toolbox.actions.FlowActionsToolbox;
import org.kie.workbench.common.stunner.core.client.components.toolbox.actions.ToolboxAction;
import org.kie.workbench.common.stunner.core.diagram.Diagram;
import org.kie.workbench.common.stunner.core.graph.Edge;
import org.kie.workbench.common.stunner.core.graph.Element;
import org.kie.workbench.common.stunner.core.graph.Node;
import org.kie.workbench.common.stunner.core.graph.content.definition.Definition;
import org.kie.workbench.common.stunner.core.lookup.util.CommonLookups;
import org.kie.workbench.common.stunner.core.util.DefinitionUtils;

@Dependent
@DMNFlowActionsToolbox
public class DMNFlowActionsToolboxFactory
        extends AbstractActionsToolboxFactory {

    private final DefinitionUtils definitionUtils;
    private final CommonLookups commonLookups;
    private final ManagedInstance<CreateConnectorAction> createConnectorActions;
    private final ManagedInstance<CreateNodeAction> createNodeActions;
    private final ManagedInstance<ActionsToolboxView> views;

    @Inject
    public DMNFlowActionsToolboxFactory(final DefinitionUtils definitionUtils,
                                        final CommonLookups commonLookups,
                                        final @Any ManagedInstance<CreateConnectorAction> createConnectorActions,
                                        final @Any ManagedInstance<CreateNodeAction> createNodeActions,
                                        final @Any @FlowActionsToolbox ManagedInstance<ActionsToolboxView> views) {
        this.definitionUtils = definitionUtils;
        this.commonLookups = commonLookups;
        this.createConnectorActions = createConnectorActions;
        this.createNodeActions = createNodeActions;
        this.views = views;
    }

    @Override
    protected ActionsToolboxView<?> newViewInstance() {
        return views.get();
    }

    @Override
    @SuppressWarnings("unchecked")
    public Collection<ToolboxAction<AbstractCanvasHandler>> getActions(final AbstractCanvasHandler canvasHandler,
                                                                       final Element<?> element) {
        final Set<ToolboxAction<AbstractCanvasHandler>> actions = new LinkedHashSet<>();
        final Node<Definition<Object>, Edge> node = (Node<Definition<Object>, Edge>) element;
        final Diagram diagram = canvasHandler.getDiagram();
        final String defSetId = diagram.getMetadata().getDefinitionSetId();

        // Look for the allowed connectors present in the Definition Set.
        final Set<String> allowedConnectorIds = commonLookups.getAllowedConnectors(defSetId,
                                                                                   node,
                                                                                   0,
                                                                                   10);
        for (final String allowedConnectorId : allowedConnectorIds) {
            // Append a new action for each connector.
            actions.add(createConnectorActions.get()
                                .setEdgeId(allowedConnectorId));
            // Append a new action for each candidate target node (as from the current connector).
            commonLookups
                    .getAllowedTargetDefinitions(defSetId,
                                                 diagram.getGraph(),
                                                 node,
                                                 allowedConnectorId,
                                                 0,
                                                 10)
                    .forEach(def -> actions.add(createNodeActions.get()
                                                        .setEdgeId(allowedConnectorId)
                                                        .setNodeId(getDefinitionId(def))));
        }
        return actions;
    }

    @PreDestroy
    public void destroy() {
        createConnectorActions.destroyAll();
        createNodeActions.destroyAll();
        views.destroyAll();
    }

    private String getDefinitionId(final Object def) {
        return getDefinitionManager().adapters().forDefinition().getId(def);
    }

    private DefinitionManager getDefinitionManager() {
        return definitionUtils.getDefinitionManager();
    }
}
