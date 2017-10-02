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

import java.util.Collections;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.logging.Logger;

import org.kie.workbench.common.stunner.core.client.api.ClientDefinitionManager;
import org.kie.workbench.common.stunner.core.client.canvas.AbstractCanvasHandler;
import org.kie.workbench.common.stunner.core.client.canvas.controls.AbstractCanvasHandlerControl;
import org.kie.workbench.common.stunner.core.client.canvas.controls.builder.ElementBuilderControl;
import org.kie.workbench.common.stunner.core.client.canvas.controls.builder.request.ElementBuildRequest;
import org.kie.workbench.common.stunner.core.client.canvas.util.CanvasLayoutUtils;
import org.kie.workbench.common.stunner.core.client.command.CanvasCommandFactory;
import org.kie.workbench.common.stunner.core.client.command.CanvasCommandManager;
import org.kie.workbench.common.stunner.core.client.command.CanvasViolation;
import org.kie.workbench.common.stunner.core.client.command.RequiresCommandManager;
import org.kie.workbench.common.stunner.core.client.service.ClientFactoryService;
import org.kie.workbench.common.stunner.core.client.service.ClientRuntimeError;
import org.kie.workbench.common.stunner.core.client.service.ServiceCallback;
import org.kie.workbench.common.stunner.core.command.Command;
import org.kie.workbench.common.stunner.core.command.impl.CompositeCommandImpl;
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
import org.kie.workbench.common.stunner.core.rule.context.impl.RuleContextBuilder;
import org.kie.workbench.common.stunner.core.rule.violations.DefaultRuleViolations;
import org.kie.workbench.common.stunner.core.util.UUID;

public abstract class AbstractElementBuilderControl extends AbstractCanvasHandlerControl<AbstractCanvasHandler>
        implements ElementBuilderControl<AbstractCanvasHandler> {

    private static Logger LOGGER = Logger.getLogger(AbstractElementBuilderControl.class.getName());

    private final ClientDefinitionManager clientDefinitionManager;
    private final ClientFactoryService clientFactoryServices;
    private final CanvasCommandFactory<AbstractCanvasHandler> canvasCommandFactory;
    private final GraphUtils graphUtils;
    private final RuleManager ruleManager;
    private final GraphBoundsIndexer graphBoundsIndexer;
    private final CanvasLayoutUtils canvasLayoutUtils;
    private RequiresCommandManager.CommandManagerProvider<AbstractCanvasHandler> commandManagerProvider;

    public AbstractElementBuilderControl(final ClientDefinitionManager clientDefinitionManager,
                                         final ClientFactoryService clientFactoryServices,
                                         final GraphUtils graphUtils,
                                         final RuleManager ruleManager,
                                         final CanvasCommandFactory<AbstractCanvasHandler> canvasCommandFactory,
                                         final GraphBoundsIndexer graphBoundsIndexer,
                                         final CanvasLayoutUtils canvasLayoutUtils) {
        this.clientDefinitionManager = clientDefinitionManager;
        this.clientFactoryServices = clientFactoryServices;
        this.graphUtils = graphUtils;
        this.ruleManager = ruleManager;
        this.canvasCommandFactory = canvasCommandFactory;
        this.graphBoundsIndexer = graphBoundsIndexer;
        this.canvasLayoutUtils = canvasLayoutUtils;
    }

    @Override
    public void setCommandManagerProvider(final RequiresCommandManager.CommandManagerProvider<AbstractCanvasHandler> provider) {
        this.commandManagerProvider = provider;
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
        if (null != parent) {
            final Object parentDef = parent.getContent().getDefinition();
            final Set<String> parentLabels = clientDefinitionManager.adapters().forDefinition().getLabels(parentDef);
            final RuleViolations containmentViolations =
                    ruleManager.evaluate(ruleSet,
                                         RuleContextBuilder.DomainContexts.containment(parentLabels,
                                                                                       labels));
            if (!isValid(containmentViolations)) {
                return false;
            }
        }
        // Check cardinality rules.
        final Map<String, Integer> graphLabelCount = GraphUtils.getLabelsCount(canvasHandler.getDiagram().getGraph(),
                                                                               labels);
        final DefaultRuleViolations cardinalityViolations = new DefaultRuleViolations();
        labels.forEach(role -> {
            final Integer roleCount = Optional.ofNullable(graphLabelCount.get(role)).orElse(0);
            final RuleViolations violations =
                    ruleManager.evaluate(ruleSet,
                                         RuleContextBuilder.DomainContexts.cardinality(Collections.singleton(role),
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
        double x = 0;
        double y = 0;

        if ((x>=0) && (y>=0)){
            x = request.getX();
            y = request.getY();
        } else {
            throw new IllegalArgumentException("Coordinates locations cannot be negative");
        }
        final Object definition = request.getDefinition();
        // Notify processing starts.
        fireProcessingStarted();
        final Node<View<?>, Edge> parent = getParent(x,
                                                     y);
        final Point2D childCoordinates = getChildCoordinates(parent,
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
                            getCommandManager().execute(canvasHandler,
                                                        new CompositeCommandImpl.CompositeCommandBuilder()
                                                                .addCommands(commands)
                                                                .build());
                            buildCallback.onSuccess(uuid);
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
    protected void doDisable() {
        graphBoundsIndexer.destroy();
        commandManagerProvider = null;
    }

    public void getCommands(final Object definition,
                            final Node<View<?>, Edge> parent,
                            final double x,
                            final double y,
                            final CommandsCallback commandsCallback) {
        final String defId = clientDefinitionManager.adapters().forDefinition().getId(definition);
        final String uuid = UUID.uuid();
        clientFactoryServices.newElement(uuid,
                                         defId,
                                         new ServiceCallback<Element>() {
                                             @Override
                                             public void onSuccess(final Element element) {
                                                 getElementCommands(element,
                                                                    parent,
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

    @SuppressWarnings("unchecked")
    public void getElementCommands(final Element element,
                                   final Node<View<?>, Edge> parent,
                                   final double x,
                                   final double y,
                                   final CommandsCallback commandsCallback) {
        Command<AbstractCanvasHandler, CanvasViolation> command = null;
        if (element instanceof Node) {
            if (null != parent) {
                command = canvasCommandFactory.addChildNode(parent,
                                                            (Node) element,
                                                            getShapeSetId());
            } else {
                command = canvasCommandFactory.addNode((Node) element,
                                                       getShapeSetId());
            }
        } else if (element instanceof Edge && null != parent) {
            command = canvasCommandFactory.addConnector(parent,
                                                        (Edge) element,
                                                        MagnetConnection.Builder.forElement(parent),
                                                        getShapeSetId());
        } else {
            throw new RuntimeException("Unrecognized element type for " + element);
        }
        // Execute both add element and move commands in batch, so undo will be done in batch as well.
        Command<AbstractCanvasHandler, CanvasViolation> moveCanvasElementCommand = canvasCommandFactory.updatePosition((Node<View<?>, Edge>) element,
                                                                                                                       x,
                                                                                                                       y);
        final List<Command<AbstractCanvasHandler, CanvasViolation>> commandList = new LinkedList<Command<AbstractCanvasHandler, CanvasViolation>>();
        commandList.add(command);
        commandList.add(moveCanvasElementCommand);
        commandsCallback.onComplete(element.getUUID(),
                                    commandList);
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

    public Point2D getChildCoordinates(final Node<View<?>, Edge> parent,
                                       final double _x,
                                       final double _y) {
        if (null != parent) {
            final Point2D parentCoords = GraphUtils.getPosition(parent.getContent());
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
