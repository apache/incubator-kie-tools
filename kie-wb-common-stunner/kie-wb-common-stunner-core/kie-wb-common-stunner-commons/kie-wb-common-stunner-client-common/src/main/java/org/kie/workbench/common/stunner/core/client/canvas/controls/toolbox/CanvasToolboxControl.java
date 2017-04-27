/*
 * Copyright 2017 Red Hat, Inc. and/or its affiliates.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *   http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.kie.workbench.common.stunner.core.client.canvas.controls.toolbox;

import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import javax.enterprise.event.Observes;
import javax.inject.Inject;

import com.google.gwt.user.client.ui.IsWidget;
import com.google.gwt.user.client.ui.Widget;
import org.jboss.errai.ioc.client.api.ManagedInstance;
import org.kie.workbench.common.stunner.core.client.canvas.AbstractCanvasHandler;
import org.kie.workbench.common.stunner.core.client.canvas.controls.AbstractCanvasHandlerRegistrationControl;
import org.kie.workbench.common.stunner.core.client.canvas.controls.toolbox.command.AbstractContext;
import org.kie.workbench.common.stunner.core.client.canvas.controls.toolbox.command.Context;
import org.kie.workbench.common.stunner.core.client.canvas.controls.toolbox.command.ToolboxCommand;
import org.kie.workbench.common.stunner.core.client.canvas.event.selection.CanvasClearSelectionEvent;
import org.kie.workbench.common.stunner.core.client.canvas.event.selection.CanvasElementSelectedEvent;
import org.kie.workbench.common.stunner.core.client.command.CanvasCommandManager;
import org.kie.workbench.common.stunner.core.client.components.toolbox.Toolbox;
import org.kie.workbench.common.stunner.core.client.components.toolbox.ToolboxButton;
import org.kie.workbench.common.stunner.core.client.components.toolbox.ToolboxButtonGrid;
import org.kie.workbench.common.stunner.core.client.components.toolbox.ToolboxFactory;
import org.kie.workbench.common.stunner.core.client.components.toolbox.builder.ToolboxBuilder;
import org.kie.workbench.common.stunner.core.client.components.toolbox.builder.ToolboxButtonBuilder;
import org.kie.workbench.common.stunner.core.client.components.toolbox.event.ToolboxButtonEvent;
import org.kie.workbench.common.stunner.core.client.shape.NodeShape;
import org.kie.workbench.common.stunner.core.client.shape.Shape;
import org.kie.workbench.common.stunner.core.client.shape.view.HasEventHandlers;
import org.kie.workbench.common.stunner.core.client.shape.view.event.DragEvent;
import org.kie.workbench.common.stunner.core.client.shape.view.event.DragHandler;
import org.kie.workbench.common.stunner.core.client.shape.view.event.ViewEventType;
import org.kie.workbench.common.stunner.core.graph.Element;

import static org.uberfire.commons.validation.PortablePreconditions.checkNotNull;

// TODO: Update / rebuild toolbox/buttons after the source element has been updated.
// TODO: Destroy toolbox command instances as well once removing the toolbox for a given element (missing call to ToolboxCommand::destroy method).

public class CanvasToolboxControl extends AbstractCanvasHandlerRegistrationControl<AbstractCanvasHandler>
        implements ToolboxControl<AbstractCanvasHandler, Element>,
                   IsWidget {

    public interface View extends IsWidget {

        View addWidget(final IsWidget widget);

        View clear();
    }

    private final ManagedInstance<ToolboxControlProvider> controlProviders;
    private final ToolboxFactory toolboxFactory;
    private final View view;

    private final Map<String, List<Toolbox>> toolboxMap = new HashMap<>();
    private String currentToolboxUUID;
    private CommandManagerProvider<AbstractCanvasHandler> commandManagerProvider;

    @Inject
    public CanvasToolboxControl(final ManagedInstance<ToolboxControlProvider> controlProviders,
                                final ToolboxFactory toolboxFactory,
                                final View view) {
        this.controlProviders = controlProviders;
        this.toolboxFactory = toolboxFactory;
        this.view = view;
        this.currentToolboxUUID = null;
    }

    @Override
    public void setCommandManagerProvider(final CommandManagerProvider<AbstractCanvasHandler> provider) {
        this.commandManagerProvider = provider;
    }

    @Override
    public void enable(final AbstractCanvasHandler canvasHandler) {
        super.enable(canvasHandler);
        // Add the control view widget into the canvas.
        canvasHandler.getAbstractCanvas().addControl(CanvasToolboxControl.this.asWidget());
    }

    @Override
    @SuppressWarnings("unchecked")
    public void register(final Element element) {
        if (checkNotRegistered(element) && hasToolboxControls(element)) {
            // Register the element's identifier for further toolbox lazy loading.
            toolboxMap.put(element.getUUID(),
                           new LinkedList<>());
            // If shape view can be drag, hide the shape's toolbox/es when drag starts.
            final Shape shape = canvasHandler.getCanvas().getShape(element.getUUID());
            if (shape instanceof NodeShape) {
                final HasEventHandlers hasEventHandlers = (HasEventHandlers) shape.getShapeView();
                if (hasEventHandlers.supports(ViewEventType.DRAG)) {
                    final DragHandler handler = new DragHandler() {

                        @Override
                        public void handle(final DragEvent event) {
                        }

                        @Override
                        public void start(final DragEvent event) {
                            hideToolboxes(element);
                        }

                        @Override
                        public void end(final DragEvent event) {
                        }
                    };
                    hasEventHandlers.addHandler(ViewEventType.DRAG,
                                                handler);
                    registerHandler(element.getUUID(),
                                    handler);
                }
            }
        }
    }

    /**
     * Once an element has been updated, the toolbox/es should be re-built, as
     * rule evaluations have to be evaluated against latest status
     * and latest graph structure.
     * <p/>
     * TODO:
     * - bug -> applies the new toolbox buttons after any further op with the node, but not the 1st time.
     * - improve by not recreating instances, just adding/removing buttons.
     */
    @Override
    public void update(Element element) {
        super.update(element);
        this.deregister(element);
        this.register(element);
        canvasHandler.getCanvas().draw();
    }

    protected boolean hasToolboxControls(final Element element) {
        final List<ToolboxControlProvider<AbstractCanvasHandler, Element>> toolboxControlProviders = getToolboxProviders(element);
        return null != toolboxControlProviders && !toolboxControlProviders.isEmpty();
    }

    protected List<Toolbox> lazyLoad(final String uuid) {
        final List<Toolbox> t = toolboxMap.get(uuid);
        if (null != t && t.isEmpty()) {
            // Lazy loading.
            load(canvasHandler.getGraphIndex().get(uuid));
        }
        return toolboxMap.get(uuid);
    }

    @SuppressWarnings("unchecked")
    protected void load(final Element element) {
        final Shape shape = canvasHandler.getCanvas().getShape(element.getUUID());
        if (shape instanceof NodeShape) {
            final List<ToolboxControlProvider<AbstractCanvasHandler, Element>> toolboxControlProviders = getToolboxProviders(element);
            if (null != toolboxControlProviders && !toolboxControlProviders.isEmpty()) {
                for (final ToolboxControlProvider<AbstractCanvasHandler, Element> toolboxControlProvider : toolboxControlProviders) {
                    final List<ToolboxCommand<AbstractCanvasHandler, ?>> commands = toolboxControlProvider.getCommands(canvasHandler,
                                                                                                                       element);
                    if (null != commands && !commands.isEmpty()) {
                        final ToolboxBuilder<?, ToolboxButtonGrid, ?> toolboxBuilder = (ToolboxBuilder<?, ToolboxButtonGrid, ?>) toolboxFactory.toolboxBuilder();
                        final ToolboxButtonGrid grid = toolboxControlProvider.getGrid(canvasHandler,
                                                                                      element);
                        toolboxBuilder.forLayer(canvasHandler.getCanvas().getLayer());
                        toolboxBuilder.forView(shape.getShapeView());
                        toolboxBuilder.direction(toolboxControlProvider.getOn(),
                                                 toolboxControlProvider.getTowards());
                        toolboxBuilder.grid(grid);
                        final ToolboxButtonBuilder<Object> buttonBuilder = (ToolboxButtonBuilder<Object>) toolboxFactory.toolboxButtonBuilder();
                        for (final ToolboxCommand<AbstractCanvasHandler, ?> command : commands) {
                            // TODO: Use command title (tooltip).
                            final ToolboxButton button = buttonBuilder.setIcon(command.getIcon(canvasHandler,
                                                                                               grid.getButtonSize(),
                                                                                               grid.getButtonSize()))
                                    .setClickHandler(event -> fireCommandExecutionAndHideToolbox(element,
                                                                                                 command,
                                                                                                 event,
                                                                                                 Context.EventType.CLICK))
                                    .setMouseEnterHandler(event -> fireCommandExecution(element,
                                                                                        command,
                                                                                        event,
                                                                                        Context.EventType.MOUSE_ENTER))
                                    .setMouseExitHandler(event -> fireCommandExecution(element,
                                                                                       command,
                                                                                       event,
                                                                                       Context.EventType.MOUSE_EXIT))
                                    .setMouseDownHandler(event -> fireCommandExecutionAndHideToolbox(element,
                                                                                                     command,
                                                                                                     event,
                                                                                                     Context.EventType.MOUSE_DOWN))
                                    .build();
                            toolboxBuilder.add(button);
                        }
                        final Toolbox toolbox = toolboxBuilder.build();
                        addToolbox(element.getUUID(),
                                   toolbox);
                    }
                }
            }
        }
    }

    @SuppressWarnings("unchecked")
    protected List<ToolboxControlProvider<AbstractCanvasHandler, Element>> getToolboxProviders(final Element element) {
        if (element.getContent() instanceof org.kie.workbench.common.stunner.core.graph.content.view.View) {
            final org.kie.workbench.common.stunner.core.graph.content.view.View viewContent = (org.kie.workbench.common.stunner.core.graph.content.view.View) element.getContent();
            final Object definition = viewContent.getDefinition();
            final List<ToolboxControlProvider<AbstractCanvasHandler, Element>> result = new LinkedList<>();
            // Create a command provider instance for each type available and load the provided commands.
            controlProviders.forEach(c -> {
                if (c.supports(definition)) {
                    result.add(c);
                }
            });
            return result;
        }
        return null;
    }

    private void addToolbox(final String uuid,
                            final Toolbox toolbox) {
        if (null != uuid && null != toolbox) {
            List<Toolbox> toolboxes = toolboxMap.get(uuid);
            if (null == toolboxes) {
                toolboxes = new LinkedList<>();
                toolboxMap.put(uuid,
                               toolboxes);
            }
            toolboxes.add(toolbox);
        }
    }

    @SuppressWarnings("unchecked")
    private void fireCommandExecution(final Element element,
                                      final ToolboxCommand command,
                                      final ToolboxButtonEvent event,
                                      final Context.EventType eventTypeType) {
        final Context _context = new AbstractContext(canvasHandler,
                                                     eventTypeType,
                                                     event.getX(),
                                                     event.getY(),
                                                     event.getAbsoluteX(),
                                                     event.getAbsoluteY(),
                                                     event.getClientX(),
                                                     event.getClientY()) {
            @Override
            public CanvasCommandManager getCommandManager() {
                return CanvasToolboxControl.this.commandManagerProvider.getCommandManager();
            }
        };
        setCommandView(command).execute(_context,
                                        element);
    }

    private void fireCommandExecutionAndHideToolbox(final Element element,
                                                    final ToolboxCommand command,
                                                    final ToolboxButtonEvent event,
                                                    final Context.EventType eventTypeType) {
        fireCommandExecution(element,
                             command,
                             event,
                             eventTypeType);
        hideToolboxes(element);
    }

    @Override
    protected void doDisable() {
        super.doDisable();
        // Delete the control view.
        canvasHandler.getAbstractCanvas().deleteControl(CanvasToolboxControl.this.asWidget());
    }

    @Override
    public void deregisterAll() {
        super.deregisterAll();
        new HashSet<>(toolboxMap.keySet())
                .stream()
                .forEach(this::deregister);
        toolboxMap.clear();
    }

    @Override
    public void deregister(final String uuid) {
        super.deregister(uuid);
        final List<Toolbox> toolboxes = getToolboxes(uuid);
        if (null != toolboxes) {
            toolboxes
                    .stream()
                    .forEach(Toolbox::remove);
            toolboxMap.remove(uuid);
        }
    }

    private ToolboxCommand setCommandView(final ToolboxCommand command) {
        view.clear();
        if (command instanceof IsWidget) {
            view.addWidget(((IsWidget) command).asWidget());
        }
        return command;
    }

    @Override
    public Widget asWidget() {
        return view.asWidget();
    }

    void onCanvasElementSelectedEvent(final @Observes CanvasElementSelectedEvent event) {
        checkNotNull("event",
                     event);
        if (checkEventContext(event)) {
            final String uuid = event.getElementUUID();
            if (null != uuid) {
                switchVisibility(uuid);
            }
        }
    }

    void CanvasClearSelectionEvent(final @Observes CanvasClearSelectionEvent event) {
        checkNotNull("event",
                     event);
        if (null != this.currentToolboxUUID) {
            setVisible(this.currentToolboxUUID,
                       false);
            this.currentToolboxUUID = null;
        }
    }

    private boolean isVisible(final String uuid) {
        return currentToolboxUUID != null && currentToolboxUUID.equals(uuid);
    }

    private void switchVisibility(final String uuid) {
        if (isVisible(uuid)) {
            setVisible(this.currentToolboxUUID,
                       false);
            this.currentToolboxUUID = null;
        } else {
            if (null != this.currentToolboxUUID) {
                setVisible(this.currentToolboxUUID,
                           false);
            }
            setVisible(uuid,
                       true);
            this.currentToolboxUUID = uuid;
        }
    }

    private void setVisible(final String uuid,
                            final boolean visible) {
        final List<Toolbox> toolboxes = loadToolboxes(uuid);
        if (null != toolboxes) {
            for (final Toolbox toolbox : toolboxes) {
                if (visible) {
                    toolbox.show();
                } else {
                    toolbox.hide();
                }
            }
        }
    }

    private void hideToolboxes(final Element<?> element) {
        final List<Toolbox> toolboxes = getToolboxes(element);
        if (null != toolboxes) {
            toolboxes.stream().forEach(Toolbox::hide);
        }
    }

    private List<Toolbox> getToolboxes(final Element<?> element) {
        final String uuid = null != element ? element.getUUID() : null;
        return null != uuid ? getToolboxes(uuid) : null;
    }

    private List<Toolbox> loadToolboxes(final String uuid) {
        return lazyLoad(uuid);
    }

    private List<Toolbox> getToolboxes(final String uuid) {
        return toolboxMap.get(uuid);
    }
}
