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

import java.lang.annotation.Annotation;
import java.util.Collection;
import java.util.Set;
import java.util.function.Predicate;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import javax.annotation.PreDestroy;
import javax.enterprise.context.Dependent;
import javax.enterprise.inject.Any;
import javax.inject.Inject;

import org.jboss.errai.ioc.client.api.ManagedInstance;
import org.kie.workbench.common.stunner.core.client.canvas.AbstractCanvasHandler;
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
    private final ToolboxDomainLookups toolboxDomainLookups;
    private final DomainProfileManager profileManager;
    private final ManagedInstance<CreateConnectorToolboxAction> createConnectorActions;
    private final ManagedInstance<CreateNodeToolboxAction> createNodeActions;
    private final ManagedInstance<ActionsToolboxView> views;

    @Inject
    public FlowActionsToolboxFactory(final DefinitionUtils definitionUtils,
                                     final ToolboxDomainLookups toolboxDomainLookups,
                                     final DomainProfileManager profileManager,
                                     final @Any ManagedInstance<CreateConnectorToolboxAction> createConnectorActions,
                                     final @Any @FlowActionsToolbox ManagedInstance<CreateNodeToolboxAction> createNodeActions,
                                     final @Any @FlowActionsToolbox ManagedInstance<ActionsToolboxView> views) {
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
        final Diagram diagram = canvasHandler.getDiagram();
        final Metadata metadata = diagram.getMetadata();
        final String defSetId = metadata.getDefinitionSetId();
        final Annotation qualifier = definitionUtils.getQualifier(defSetId);
        final Node<Definition<Object>, Edge> node = (Node<Definition<Object>, Edge>) element;
        // Look for the default connector type and create a button for it.
        final CommonDomainLookups lookup = toolboxDomainLookups.get(defSetId);
        final Set<String> targetConnectors = lookup.lookupTargetConnectors(node);
        return Stream.concat(targetConnectors.stream()
                                     .map(connectorDefId -> newCreateConnectorToolboxAction(qualifier).setEdgeId(connectorDefId)),
                             targetConnectors.stream()
                                     .flatMap(defaultConnectorId -> {
                                         final Predicate<String> definitionsAllowedFilter = profileManager.isDefinitionIdAllowed(metadata);
                                         final Set<String> targets = lookup.lookupTargetNodes(diagram.getGraph(),
                                                                                              node,
                                                                                              defaultConnectorId,
                                                                                              definitionsAllowedFilter);
                                         final Set<String> morphTargets = lookup.lookupMorphBaseDefinitions(targets);
                                         return morphTargets.stream().map(defId -> newCreateNodeToolboxAction(qualifier)
                                                 .setEdgeId(defaultConnectorId)
                                                 .setNodeId(defId));
                                     }))
                .collect(Collectors.toList());
    }

    private CreateConnectorToolboxAction newCreateConnectorToolboxAction(final Annotation qualifier) {
        return lookup(createConnectorActions, qualifier);
    }

    private CreateNodeToolboxAction newCreateNodeToolboxAction(final Annotation qualifier) {
        return lookup(createNodeActions, qualifier);
    }

    @PreDestroy
    public void destroy() {
        createConnectorActions.destroyAll();
        createNodeActions.destroyAll();
        views.destroyAll();
    }
}
