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

import java.lang.annotation.Annotation;
import java.util.Collection;
import java.util.HashMap;
import java.util.Optional;
import java.util.Set;
import java.util.function.Predicate;

import jakarta.annotation.PreDestroy;
import jakarta.enterprise.context.Dependent;
import jakarta.enterprise.inject.Any;
import jakarta.inject.Inject;
import org.kie.j2cl.tools.di.core.ManagedInstance;
import org.kie.workbench.common.stunner.core.client.canvas.AbstractCanvasHandler;
import org.kie.workbench.common.stunner.core.client.components.toolbox.Toolbox;
import org.kie.workbench.common.stunner.core.diagram.Diagram;
import org.kie.workbench.common.stunner.core.diagram.Metadata;
import org.kie.workbench.common.stunner.core.graph.Edge;
import org.kie.workbench.common.stunner.core.graph.Element;
import org.kie.workbench.common.stunner.core.graph.Node;
import org.kie.workbench.common.stunner.core.graph.content.definition.Definition;
import org.kie.workbench.common.stunner.core.lookup.domain.CommonDomainLookups;
import org.kie.workbench.common.stunner.core.profile.DomainProfileManager;
import org.kie.workbench.common.stunner.core.util.DefinitionUtils;

import static org.kie.workbench.common.stunner.core.client.session.impl.InstanceUtils.lookup;

@Dependent
@GroupActionsToolbox
public class GroupActionsToolboxFactory
        extends AbstractActionsToolboxFactory {

    private final DefinitionUtils definitionUtils;
    private final ToolboxDomainLookups toolboxDomainLookups;
    private final DomainProfileManager profileManager;
    private final ManagedInstance<CreateConnectorToolboxAction> createConnectorActions;
    private final ManagedInstance<CreateNodeToolboxAction> createNodeActions;
    private final ManagedInstance<ActionsToolboxView> views;

    @Inject
    public GroupActionsToolboxFactory(final DefinitionUtils definitionUtils,
                                      final ToolboxDomainLookups toolboxDomainLookups,
                                      final DomainProfileManager profileManager,
                                      final @Any ManagedInstance<CreateConnectorToolboxAction> createConnectorActions,
                                      final @Any @GroupActionsToolbox ManagedInstance<CreateNodeToolboxAction> createNodeActions,
                                      final @Any @GroupActionsToolbox ManagedInstance<ActionsToolboxView> views) {
        this.definitionUtils = definitionUtils;
        this.toolboxDomainLookups = toolboxDomainLookups;
        this.profileManager = profileManager;
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
        return null;
    }

    public HashMap<ToolboxAction<AbstractCanvasHandler>, String> getConnectorActions(final AbstractCanvasHandler canvasHandler,
                                                                                     final Element<?> element) {
        final Diagram diagram = canvasHandler.getDiagram();
        final Metadata metadata = diagram.getMetadata();
        final String defSetId = metadata.getDefinitionSetId();
        final Annotation qualifier = definitionUtils.getQualifier(defSetId);
        final Node<Definition<Object>, Edge> node = (Node<Definition<Object>, Edge>) element;
        // Look for the default connector type and create a button for it.
        final CommonDomainLookups lookup = toolboxDomainLookups.get(defSetId);
        final Set<String> targetConnectors = lookup.lookupTargetConnectors(node);

        HashMap<ToolboxAction<AbstractCanvasHandler>, String> connectorActionsMap = new HashMap<>();

        for (String connectorDefId : targetConnectors) {
            connectorActionsMap.put(newCreateConnectorToolboxAction(qualifier).setEdgeId(connectorDefId), connectorDefId);
        }

        return connectorActionsMap;
    }

    public HashMap<ToolboxAction<AbstractCanvasHandler>, String> getNodeActions(final AbstractCanvasHandler canvasHandler,
                                                                                final Element<?> element) {
        final Diagram diagram = canvasHandler.getDiagram();
        final Metadata metadata = diagram.getMetadata();
        final String defSetId = metadata.getDefinitionSetId();
        final Annotation qualifier = definitionUtils.getQualifier(defSetId);
        final Node<Definition<Object>, Edge> node = (Node<Definition<Object>, Edge>) element;
        // Look for the default connector type and create a button for it.
        final CommonDomainLookups lookup = toolboxDomainLookups.get(defSetId);
        final Set<String> targetConnectors = lookup.lookupTargetConnectors(node);

        HashMap<ToolboxAction<AbstractCanvasHandler>, String> connectorNodeActionsMap = new HashMap<>();

        for (String connectorDefId : targetConnectors) {
            final Predicate<String> definitionsAllowedFilter = profileManager.isDefinitionIdAllowed(metadata);
            Set<String> targets = lookup.lookupTargetNodes(diagram.getGraph(),
                                                           node,
                                                           connectorDefId,
                                                           definitionsAllowedFilter);

            for (String defId : targets) {
                CreateNodeToolboxAction nodeAction = newCreateNodeToolboxAction(qualifier).setEdgeId(connectorDefId).setNodeId(defId);
                connectorNodeActionsMap.put(nodeAction, connectorDefId);
            }
        }

        return connectorNodeActionsMap;
    }

    private CreateConnectorToolboxAction newCreateConnectorToolboxAction(final Annotation qualifier) {
        return lookup(createConnectorActions, qualifier);
    }

    private CreateNodeToolboxAction newCreateNodeToolboxAction(final Annotation qualifier) {
        return lookup(createNodeActions, qualifier);
    }

    @Override
    public Optional<Toolbox<?>> build(final AbstractCanvasHandler canvasHandler,
                                      final Element element) {

        HashMap<ToolboxAction<AbstractCanvasHandler>, String> connectorActions = getConnectorActions(canvasHandler, element);
        HashMap<ToolboxAction<AbstractCanvasHandler>, String> nodeActions = getNodeActions(canvasHandler, element);

        if (!nodeActions.isEmpty()) {
            final GroupedActionsToolbox<?> toolbox =
                    new GroupedActionsToolbox<>(() -> canvasHandler,
                                                element,
                                                newViewInstance());
            // create hierarchy
            toolbox.setConnectorActions(connectorActions);
            toolbox.setNodeActions(nodeActions);

            nodeActions.forEach((key, value) -> toolbox.add(key));

            return Optional.of(toolbox.init());
        }
        return Optional.empty();
    }

    @PreDestroy
    public void destroy() {
        createConnectorActions.destroyAll();
        createNodeActions.destroyAll();
        views.destroyAll();
    }
}
