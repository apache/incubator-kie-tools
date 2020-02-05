/*
 * Copyright 2018 Red Hat, Inc. and/or its affiliates.
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

package org.kie.workbench.common.dmn.client.canvas.controls.resize;

import java.util.Optional;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.inject.Inject;

import org.kie.workbench.common.dmn.api.definition.model.DecisionService;
import org.kie.workbench.common.dmn.api.qualifiers.DMNEditor;
import org.kie.workbench.common.dmn.client.commands.factory.DefaultCanvasCommandFactory;
import org.kie.workbench.common.dmn.client.shape.view.decisionservice.DecisionServiceSVGShapeView;
import org.kie.workbench.common.stunner.core.client.canvas.AbstractCanvasHandler;
import org.kie.workbench.common.stunner.core.client.canvas.Canvas;
import org.kie.workbench.common.stunner.core.client.canvas.controls.AbstractCanvasHandlerRegistrationControl;
import org.kie.workbench.common.stunner.core.client.command.CanvasCommand;
import org.kie.workbench.common.stunner.core.client.command.CanvasCommandManager;
import org.kie.workbench.common.stunner.core.client.command.CanvasCommandResultBuilder;
import org.kie.workbench.common.stunner.core.client.command.CanvasViolation;
import org.kie.workbench.common.stunner.core.client.command.RequiresCommandManager;
import org.kie.workbench.common.stunner.core.client.shape.Shape;
import org.kie.workbench.common.stunner.core.client.shape.view.ShapeView;
import org.kie.workbench.common.stunner.core.client.shape.view.event.DragEvent;
import org.kie.workbench.common.stunner.core.client.shape.view.event.DragHandler;
import org.kie.workbench.common.stunner.core.client.shape.view.event.ViewHandler;
import org.kie.workbench.common.stunner.core.command.CommandResult;
import org.kie.workbench.common.stunner.core.command.util.CommandUtils;
import org.kie.workbench.common.stunner.core.definition.adapter.DefinitionAdapter;
import org.kie.workbench.common.stunner.core.graph.Element;
import org.kie.workbench.common.stunner.core.graph.content.definition.Definition;
import org.kie.workbench.common.stunner.core.graph.content.view.View;

public class DecisionServiceMoveDividerControl extends AbstractCanvasHandlerRegistrationControl<AbstractCanvasHandler> implements RequiresCommandManager<AbstractCanvasHandler>,
                                                                                                                                  RequiresCommandManager.CommandManagerProvider<AbstractCanvasHandler> {

    static final String DIVIDER_Y_PROPERTY_ID = "dividerLineY";

    private static Logger LOGGER = Logger.getLogger(DecisionServiceMoveDividerControl.class.getName());

    private final DefaultCanvasCommandFactory canvasCommandFactory;
    private RequiresCommandManager.CommandManagerProvider<AbstractCanvasHandler> commandManagerProvider;

    protected DecisionServiceMoveDividerControl() {
        this(null);
    }

    @Inject
    public DecisionServiceMoveDividerControl(final @DMNEditor DefaultCanvasCommandFactory canvasCommandFactory) {
        this.canvasCommandFactory = canvasCommandFactory;
    }

    @Override
    @SuppressWarnings("unchecked")
    public void register(final Element element) {
        if (checkNotRegistered(element)) {
            final Object definition = ((Definition) element.getContent()).getDefinition();
            if (!isDecisionServiceDefinition(definition)) {
                return;
            }

            final Canvas<?> canvas = canvasHandler.getCanvas();
            final Shape<?> shape = canvas.getShape(element.getUUID());
            final ShapeView<?> shapeView = shape.getShapeView();
            if (!isDecisionServiceShapeView(shapeView)) {
                return;
            }

            final DecisionServiceSVGShapeView decisionServiceShapeView = (DecisionServiceSVGShapeView) shapeView;
            final DragHandler dragHandler = new DragHandler() {

                @Override
                public void end(final DragEvent event) {
                    final CommandResult<CanvasViolation> result = doMoveDivider(element, decisionServiceShapeView.getDividerLineY());
                    if (CommandUtils.isError(result)) {
                        LOGGER.log(Level.WARNING,
                                   "Command failed at resize end [result=" + result + "]");
                    }
                }
            };

            decisionServiceShapeView.addDividerDragHandler(dragHandler);
            registerHandler(element.getUUID(), dragHandler);
        }
    }

    private boolean isDecisionServiceDefinition(final Object definition) {
        return definition instanceof DecisionService;
    }

    private boolean isDecisionServiceShapeView(final ShapeView<?> view) {
        return view instanceof DecisionServiceSVGShapeView;
    }

    private CommandResult<CanvasViolation> doMoveDivider(final Element<? extends View<?>> element,
                                                         final double dividerY) {
        final Optional<CanvasCommand<AbstractCanvasHandler>> oCommand = getMoveDividerCommand(element, dividerY);
        if (oCommand.isPresent()) {
            final CanvasCommand<AbstractCanvasHandler> command = oCommand.get();
            return getCommandManager().execute(canvasHandler, command);
        }
        return CanvasCommandResultBuilder.failed();
    }

    private Optional<CanvasCommand<AbstractCanvasHandler>> getMoveDividerCommand(final Element<? extends Definition<?>> element,
                                                                                 final double dividerY) {
        final Definition content = element.getContent();
        final Object definition = content.getDefinition();
        final DefinitionAdapter<Object> adapter = canvasHandler.getDefinitionManager().adapters().registry().getDefinitionAdapter(definition.getClass());
        final Optional<?> dividerYProperty = adapter.getProperty(definition, DIVIDER_Y_PROPERTY_ID);
        if (dividerYProperty.isPresent()) {
            final Object dyp = dividerYProperty.get();
            final String id = canvasHandler.getDefinitionManager().adapters().forProperty().getId(dyp);
            return Optional.of(canvasCommandFactory.updatePropertyValue(element, id, dividerY));
        }
        return Optional.empty();
    }

    @Override
    //Override to increase visibility for Unit Tests
    public void registerHandler(final String uuid,
                                final ViewHandler<?> handler) {
        super.registerHandler(uuid, handler);
    }

    @Override
    protected void doDestroy() {
        super.doDestroy();
        this.commandManagerProvider = null;
    }

    @Override
    public void setCommandManagerProvider(final RequiresCommandManager.CommandManagerProvider<AbstractCanvasHandler> provider) {
        this.commandManagerProvider = provider;
    }

    @Override
    public CanvasCommandManager<AbstractCanvasHandler> getCommandManager() {
        return commandManagerProvider.getCommandManager();
    }
}
