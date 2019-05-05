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

package org.kie.workbench.common.stunner.core.client.canvas.controls.builder.impl;

import java.util.Arrays;
import java.util.Collections;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.Set;

import org.kie.workbench.common.stunner.core.client.api.ClientDefinitionManager;
import org.kie.workbench.common.stunner.core.client.canvas.AbstractCanvasHandler;
import org.kie.workbench.common.stunner.core.client.canvas.controls.AbstractCanvasHandlerControl;
import org.kie.workbench.common.stunner.core.client.canvas.controls.builder.ElementBuilderControl;
import org.kie.workbench.common.stunner.core.client.canvas.controls.builder.request.ElementBuildRequest;
import org.kie.workbench.common.stunner.core.client.command.CanvasCommand;
import org.kie.workbench.common.stunner.core.client.command.CanvasCommandFactory;
import org.kie.workbench.common.stunner.core.client.command.CanvasCommandManager;
import org.kie.workbench.common.stunner.core.client.command.CanvasViolation;
import org.kie.workbench.common.stunner.core.client.command.RequiresCommandManager;
import org.kie.workbench.common.stunner.core.client.i18n.ClientTranslationMessages;
import org.kie.workbench.common.stunner.core.client.service.ClientFactoryService;
import org.kie.workbench.common.stunner.core.client.service.ClientRuntimeError;
import org.kie.workbench.common.stunner.core.client.service.ServiceCallback;
import org.kie.workbench.common.stunner.core.command.Command;
import org.kie.workbench.common.stunner.core.command.CommandResult;
import org.kie.workbench.common.stunner.core.command.impl.CompositeCommand;
import org.kie.workbench.common.stunner.core.command.util.CommandUtils;
import org.kie.workbench.common.stunner.core.graph.Edge;
import org.kie.workbench.common.stunner.core.graph.Element;
import org.kie.workbench.common.stunner.core.graph.Node;
import org.kie.workbench.common.stunner.core.graph.content.view.MagnetConnection;
import org.kie.workbench.common.stunner.core.graph.content.view.Point2D;
import org.kie.workbench.common.stunner.core.graph.content.view.View;
import org.kie.workbench.common.stunner.core.graph.processing.index.bounds.GraphBoundsIndexer;
import org.kie.workbench.common.stunner.core.graph.util.GraphUtils;
import org.kie.workbench.common.stunner.core.rule.RuleManager;
import org.kie.workbench.common.stunner.core.rule.RuleSet;
import org.kie.workbench.common.stunner.core.rule.RuleViolation;
import org.kie.workbench.common.stunner.core.rule.RuleViolations;
import org.kie.workbench.common.stunner.core.rule.context.CardinalityContext;
import org.kie.workbench.common.stunner.core.rule.context.impl.RuleEvaluationContextBuilder;
import org.kie.workbench.common.stunner.core.rule.violations.DefaultRuleViolations;
import org.kie.workbench.common.stunner.core.util.UUID;

public abstract class AbstractElementBuilderControl extends AbstractCanvasHandlerControl<AbstractCanvasHandler>
        implements ElementBuilderControl<AbstractCanvasHandler> {

    enum ParentAssignment {
        DOCKING,
        CONTAINMENT,
        NONE
    }

    private final ClientDefinitionManager clientDefinitionManager;
    private final ClientFactoryService clientFactoryServices;
    private final CanvasCommandFactory<AbstractCanvasHandler> canvasCommandFactory;
    private final ClientTranslationMessages translationMessages;
    private final RuleManager ruleManager;
    private final GraphBoundsIndexer graphBoundsIndexer;
    private RequiresCommandManager.CommandManagerProvider<AbstractCanvasHandler> commandManagerProvider;

    public AbstractElementBuilderControl(final ClientDefinitionManager clientDefinitionManager,
                                         final ClientFactoryService clientFactoryServices,
                                         final RuleManager ruleManager,
                                         final CanvasCommandFactory<AbstractCanvasHandler> canvasCommandFactory,
                                         final ClientTranslationMessages translationMessages,
                                         final GraphBoundsIndexer graphBoundsIndexer) {
        this.clientDefinitionManager = clientDefinitionManager;
        this.clientFactoryServices = clientFactoryServices;
        this.ruleManager = ruleManager;
        this.canvasCommandFactory = canvasCommandFactory;
        this.translationMessages = translationMessages;
        this.graphBoundsIndexer = graphBoundsIndexer;
    }

    @Override
    public void setCommandManagerProvider(final RequiresCommandManager.CommandManagerProvider<AbstractCanvasHandler> provider) {
        this.commandManagerProvider = provider;
    }

    protected ParentAssignment getParentAssignment(final Node<View<?>, Edge> parent, final Object definition) {

        Objects.requireNonNull(definition);
        final Set<String> labels = clientDefinitionManager.adapters().forDefinition().getLabels(definition);
        final RuleSet ruleSet = canvasHandler.getRuleSet();

        // Check containment rules.
        if (null != parent) {
            final Set<String> parentLabels = parent.getLabels();

            final RuleViolations dockingViolations =
                    ruleManager.evaluate(ruleSet, RuleEvaluationContextBuilder.DomainContexts.docking(parentLabels, labels));
            if (isValid(dockingViolations)) {
                return ParentAssignment.DOCKING;
            }

            final RuleViolations containmentViolations =
                    ruleManager.evaluate(ruleSet, RuleEvaluationContextBuilder.DomainContexts.containment(parentLabels, labels));
            if (isValid(containmentViolations)) {
                return ParentAssignment.CONTAINMENT;
            }
        }
        return ParentAssignment.NONE;
    }

    @Override
    @SuppressWarnings("unchecked")
    public boolean allows(final ElementBuildRequest<AbstractCanvasHandler> request) {
        final double x = request.getX();
        final double y = request.getY();
        final Object definition = request.getDefinition();
        final Node<View<?>, Edge> parent = getParent(x,
                                                     y);
        final Set<String> labels = clientDefinitionManager.adapters().forDefinition().getLabels(definition);
        final RuleSet ruleSet = canvasHandler.getRuleSet();

        // Check containment rules.
        if (Objects.nonNull(parent) && ParentAssignment.NONE.equals(getParentAssignment(parent, definition))) {
            return false;
        }

        // Check cardinality rules.
        final Map<String, Integer> graphLabelCount = GraphUtils.getLabelsCount(canvasHandler.getDiagram().getGraph(),
                                                                               labels);
        final DefaultRuleViolations cardinalityViolations = new DefaultRuleViolations();
        labels.forEach(role -> {
            final Integer roleCount = Optional.ofNullable(graphLabelCount.get(role)).orElse(0);
            final RuleViolations violations =
                    ruleManager.evaluate(ruleSet,
                                         RuleEvaluationContextBuilder.DomainContexts.cardinality(Collections.singleton(role),
                                                                                                 roleCount,
                                                                                                 Optional.of(CardinalityContext.Operation.ADD)));
            cardinalityViolations.addViolations(violations);
        });
        labels.stream().forEach(role -> {

        });
        return isValid(cardinalityViolations);
    }

    @Override
    @SuppressWarnings("unchecked")
    public void build(final ElementBuildRequest<AbstractCanvasHandler> request,
                      final BuildCallback buildCallback) {
        if (null == canvasHandler) {
            buildCallback.onSuccess(null);
            return;
        }

        double x = request.getX();
        double y = request.getY();

        final Object definition = request.getDefinition();
        // Notify processing starts.
        fireProcessingStarted();

        final Node<View<?>, Edge> parent = getParent(x,
                                                     y);
        final Point2D childCoordinates = getComputedChildCoordinates(parent,
                                                                     x,
                                                                     y);
        getCommands(definition,
                    parent,
                    childCoordinates.getX(),
                    childCoordinates.getY(),
                    new CommandsCallback() {

                        @Override
                        public void onComplete(final String uuid,
                                               final List<Command<AbstractCanvasHandler, CanvasViolation>> commands) {
                            final CommandResult<CanvasViolation> result =
                                    getCommandManager().execute(canvasHandler,
                                                                new CompositeCommand.Builder()
                                                                        .addCommands(commands)
                                                                        .build());
                            if (!CommandUtils.isError(result)) {
                                buildCallback.onSuccess(uuid);
                            } else {
                                final String message =
                                        translationMessages.getCanvasCommandValidationsErrorMessage(result.getViolations());
                                buildCallback.onError(new ClientRuntimeError(message));
                            }
                            // Notify processing ends.
                            fireProcessingCompleted();
                        }

                        @Override
                        public void onError(final ClientRuntimeError error) {
                            buildCallback.onError(error);
                            // Notify processing ends.
                            fireProcessingCompleted();
                        }
                    });
    }

    @Override
    protected void doInit() {
    }

    @Override
    protected void doDestroy() {
        graphBoundsIndexer.destroy();
        commandManagerProvider = null;
    }

    public void getCommands(final Object definition,
                            final Node<View<?>, Edge> parent,
                            final double x,
                            final double y,
                            final CommandsCallback commandsCallback) {
        final String defId = clientDefinitionManager.adapters().forDefinition().getId(definition).value();
        final String uuid = UUID.uuid();
        clientFactoryServices.newElement(uuid,
                                         defId,
                                         new ServiceCallback<Element>() {
                                             @Override
                                             public void onSuccess(final Element element) {
                                                 updateElementFromDefinition(element, definition);
                                                 getElementCommands(element,
                                                                    parent,
                                                                    getParentAssignment(parent, definition),
                                                                    x,
                                                                    y,
                                                                    new CommandsCallback() {
                                                                        @Override
                                                                        public void onComplete(final String uuid,
                                                                                               final List<Command<AbstractCanvasHandler, CanvasViolation>> commands) {
                                                                            commandsCallback.onComplete(uuid,
                                                                                                        commands);
                                                                        }

                                                                        @Override
                                                                        public void onError(final ClientRuntimeError error) {
                                                                            commandsCallback.onError(error);
                                                                        }
                                                                    });
                                             }

                                             @Override
                                             public void onError(final ClientRuntimeError error) {
                                                 commandsCallback.onError(error);
                                             }
                                         });
    }

    protected void updateElementFromDefinition(final Element element, final Object definition) {
        // Nothing for now
    }

    @SuppressWarnings("unchecked")
    public void getElementCommands(final Element element,
                                   final Node<View<?>, Edge> parent,
                                   final ParentAssignment parentAssignment,
                                   final double x,
                                   final double y,
                                   final CommandsCallback commandsCallback) {
        final List<Command<AbstractCanvasHandler, CanvasViolation>> commandList = new LinkedList<>();
        if (element instanceof Node) {
            final Node<View<?>, Edge> node = (Node<View<?>, Edge>) element;
            commandList.addAll(getNodeBuildCommands(node, parent, parentAssignment, x, y));
        } else if (element instanceof Edge && null != parent) {
            commandList.add(canvasCommandFactory.addConnector(parent,
                                                              (Edge) element,
                                                              MagnetConnection.Builder.atCenter(parent),
                                                              getShapeSetId()));
        } else {
            throw new IllegalStateException("Unrecognized element type for " + element);
        }

        commandsCallback.onComplete(element.getUUID(), commandList);
    }

    private CanvasCommand<AbstractCanvasHandler> getUpdatePositionCommand(double x, double y, Node<View<?>, Edge> node) {
        return canvasCommandFactory.updatePosition(node, new Point2D(x, y));
    }

    private List<CanvasCommand<AbstractCanvasHandler>> getNodeBuildCommands(Node<View<?>, Edge> node, Node<View<?>, Edge> parent, ParentAssignment parentAssignment, double x, double y) {
        final List<CanvasCommand<AbstractCanvasHandler>> commandList = new LinkedList<>();

        if (Objects.isNull(parent)) {
            return getNoParentCommands(node, x, y);
        }

        switch (parentAssignment) {
            case DOCKING: {
                Node<View<?>, Edge> grandParent = (Node<View<?>, Edge>) GraphUtils.getParent(parent);

                commandList.add(canvasCommandFactory.addChildNode(grandParent, node, getShapeSetId()));
                commandList.add(canvasCommandFactory.updatePosition(node, new Point2D(x, y)));
                commandList.add(canvasCommandFactory.updateDockNode(parent, node, true));
                return commandList;
            }
            case CONTAINMENT: {
                commandList.add(canvasCommandFactory.addChildNode(parent, node, getShapeSetId()));
                commandList.add(getUpdatePositionCommand(x, y, node));
                return commandList;
            }
            case NONE:
            default: {
                return getNoParentCommands(node, x, y);
            }
        }
    }

    private List<CanvasCommand<AbstractCanvasHandler>> getNoParentCommands(Node<View<?>, Edge> node, double x, double y) {
        return Arrays.asList(canvasCommandFactory.addNode(node, getShapeSetId()), getUpdatePositionCommand(x, y, node));
    }

    @SuppressWarnings("unchecked")
    public Node<View<?>, Edge> getParent(final double _x,
                                         final double _y) {
        if (_x > -1 && _y > -1) {
            final String rootUUID = canvasHandler.getDiagram().getMetadata().getCanvasRootUUID();
            graphBoundsIndexer.setRootUUID(rootUUID).build(canvasHandler.getDiagram().getGraph());
            final Node<View<?>, Edge> r = graphBoundsIndexer.getAt(_x,
                                                                   _y);
            return r;
        }
        return null;
    }

    public Point2D getComputedChildCoordinates(final Node<View<?>, Edge> parent,
                                               final double _x,
                                               final double _y) {
        if (null != parent) {
            final Point2D parentCoords = GraphUtils.getComputedPosition(parent);
            final double x = _x - parentCoords.getX();
            final double y = _y - parentCoords.getY();
            return new Point2D(x,
                               y);
        }
        return new Point2D(_x,
                           _y);
    }

    protected void fireProcessingStarted() {
        // Nothing to for now.
    }

    protected void fireProcessingCompleted() {
        // Nothing to for now.
    }

    protected boolean isValid(final RuleViolations violations) {
        return !violations.violations(RuleViolation.Type.ERROR).iterator().hasNext();
    }

    protected String getShapeSetId() {
        return canvasHandler.getDiagram().getMetadata().getShapeSetId();
    }

    CanvasCommandManager<AbstractCanvasHandler> getCommandManager() {
        return commandManagerProvider.getCommandManager();
    }

    public interface CommandsCallback {

        void onComplete(final String uuid,
                        final List<Command<AbstractCanvasHandler, CanvasViolation>> commands);

        void onError(final ClientRuntimeError error);
    }
}
