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

package org.kie.workbench.common.stunner.core.client.components.toolbox.actions;

import java.util.Collection;
import java.util.LinkedHashSet;
import java.util.Set;
import java.util.function.Supplier;

import javax.annotation.PreDestroy;
import javax.enterprise.context.Dependent;
import javax.enterprise.inject.Any;
import javax.inject.Inject;

import org.jboss.errai.ioc.client.api.ManagedInstance;
import org.kie.workbench.common.stunner.core.client.canvas.AbstractCanvasHandler;
import org.kie.workbench.common.stunner.core.diagram.Diagram;
import org.kie.workbench.common.stunner.core.graph.Edge;
import org.kie.workbench.common.stunner.core.graph.Element;
import org.kie.workbench.common.stunner.core.graph.Node;
import org.kie.workbench.common.stunner.core.graph.content.definition.Definition;
import org.kie.workbench.common.stunner.core.lookup.util.CommonLookups;
import org.kie.workbench.common.stunner.core.util.DefinitionUtils;
import org.uberfire.mvp.Command;

/**
 * This factory builds a toolbox with a button for each candidate connector and target node that
 * can be attached as from the toolbox' related node.
 * <p>
 * This toolbox factory creates actions for:
 * - Each connector that is allowed be created as from the source toolbox' node.
 * - Each target node that is allowed be created as from the source toolbox' node, and by only using
 * the "default" connector for the specified definition set.
 * <p>
 * It also groups the resulting nodes to be created by their morph base type, this way
 * provides an action for creating nodes but only for each common morph base, not for each
 * target node that can be created.
 */
@Dependent
@FlowActionsToolbox
public class FlowActionsToolboxFactory
        extends AbstractActionsToolboxFactory {

    private final DefinitionUtils definitionUtils;
    private final CommonLookups commonLookups;
    private final Supplier<CreateConnectorAction> createConnectorActions;
    private final Command createConnectorActionsDestroyer;
    private final Supplier<CreateNodeAction> createNodeActions;
    private final Command createNodeActionsDestroyer;
    private final Supplier<ActionsToolboxView> views;
    private final Command viewsDestroyer;

    @Inject
    public FlowActionsToolboxFactory(final DefinitionUtils definitionUtils,
                                     final CommonLookups commonLookups,
                                     final @Any ManagedInstance<CreateConnectorAction> createConnectorActions,
                                     final @Any ManagedInstance<CreateNodeAction> createNodeActions,
                                     final @Any @FlowActionsToolbox ManagedInstance<ActionsToolboxView> views) {
        this(definitionUtils,
             commonLookups,
             createConnectorActions::get,
             createConnectorActions::destroyAll,
             createNodeActions::get,
             createNodeActions::destroyAll,
             views::get,
             views::destroyAll);
    }

    FlowActionsToolboxFactory(final DefinitionUtils definitionUtils,
                              final CommonLookups commonLookups,
                              final Supplier<CreateConnectorAction> createConnectorActions,
                              final Command createConnectorActionsDestroyer,
                              final Supplier<CreateNodeAction> createNodeActions,
                              final Command createNodeActionsDestroyer,
                              final Supplier<ActionsToolboxView> views,
                              final Command viewsDestroyer) {
        this.definitionUtils = definitionUtils;
        this.commonLookups = commonLookups;
        this.createConnectorActions = createConnectorActions;
        this.createConnectorActionsDestroyer = createConnectorActionsDestroyer;
        this.createNodeActions = createNodeActions;
        this.createNodeActionsDestroyer = createNodeActionsDestroyer;
        this.views = views;
        this.viewsDestroyer = viewsDestroyer;
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
        // Look for the default connector type and create a button for it.
        commonLookups
                .getAllowedConnectors(defSetId,
                                      node,
                                      0,
                                      10)
                .forEach(connectorDefId -> actions.add(createConnectorActions.get()
                                                               .setEdgeId(connectorDefId)));

        // It uses the default connector's identifier, for the actual definition set,
        // to check the resulting nodes that can be created.
        // It also groups the resuling nodes to be created by it's morph base type, and just
        // create an action for each morph target.
        final String defaultConnectorId = definitionUtils.getDefaultConnectorId(defSetId);
        if (null != defaultConnectorId) {
            commonLookups
                    .getAllowedMorphDefaultDefinitions(defSetId,
                                                       diagram.getGraph(),
                                                       node,
                                                       defaultConnectorId,
                                                       0,
                                                       10)
                    .forEach(defId -> actions.add(createNodeActions.get()
                                                          .setEdgeId(defaultConnectorId)
                                                          .setNodeId(defId.toString())));
        }
        return actions;
    }

    @PreDestroy
    public void destroy() {
        createConnectorActionsDestroyer.execute();
        createNodeActionsDestroyer.execute();
        viewsDestroyer.execute();
    }
}
