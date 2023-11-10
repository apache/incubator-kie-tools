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


package org.kie.workbench.common.stunner.client.widgets.presenters.session.impl;

import java.lang.annotation.Annotation;
import java.util.Objects;
import java.util.function.Predicate;

import io.crysknife.client.ManagedInstance;
import jakarta.annotation.PostConstruct;
import jakarta.enterprise.context.Dependent;
import jakarta.enterprise.event.Observes;
import jakarta.enterprise.inject.Any;
import jakarta.enterprise.inject.Default;
import jakarta.enterprise.inject.Typed;
import jakarta.inject.Inject;
import org.kie.workbench.common.stunner.client.lienzo.canvas.LienzoCanvasDecoratorFactory;
import org.kie.workbench.common.stunner.client.lienzo.canvas.LienzoCanvasView;
import org.kie.workbench.common.stunner.client.lienzo.canvas.wires.WiresCanvas;
import org.kie.workbench.common.stunner.client.widgets.canvas.PreviewLienzoPanel;
import org.kie.workbench.common.stunner.client.widgets.canvas.ScrollableLienzoPanel;
import org.kie.workbench.common.stunner.client.widgets.presenters.diagram.DiagramViewer;
import org.kie.workbench.common.stunner.client.widgets.presenters.diagram.impl.DiagramPreviewProxy;
import org.kie.workbench.common.stunner.client.widgets.presenters.session.SessionDiagramPreview;
import org.kie.workbench.common.stunner.client.widgets.views.WidgetWrapperView;
import org.kie.workbench.common.stunner.core.client.api.ShapeManager;
import org.kie.workbench.common.stunner.core.client.canvas.AbstractCanvas;
import org.kie.workbench.common.stunner.core.client.canvas.AbstractCanvasHandler;
import org.kie.workbench.common.stunner.core.client.canvas.BaseCanvasHandler;
import org.kie.workbench.common.stunner.core.client.canvas.Canvas;
import org.kie.workbench.common.stunner.core.client.canvas.CanvasPanel;
import org.kie.workbench.common.stunner.core.client.canvas.controls.AlertsControl;
import org.kie.workbench.common.stunner.core.client.canvas.controls.MediatorsControl;
import org.kie.workbench.common.stunner.core.client.canvas.controls.SelectionControl;
import org.kie.workbench.common.stunner.core.client.canvas.controls.actions.TextPropertyProviderFactory;
import org.kie.workbench.common.stunner.core.client.canvas.controls.select.SingleSelection;
import org.kie.workbench.common.stunner.core.client.canvas.event.command.CanvasCommandUndoneEvent;
import org.kie.workbench.common.stunner.core.client.command.CanvasCommandFactory;
import org.kie.workbench.common.stunner.core.client.command.CanvasCommandManager;
import org.kie.workbench.common.stunner.core.client.command.CanvasViolation;
import org.kie.workbench.common.stunner.core.client.command.CommandRequestLifecycle;
import org.kie.workbench.common.stunner.core.client.command.MouseRequestLifecycle;
import org.kie.workbench.common.stunner.core.client.preferences.StunnerPreferencesRegistries;
import org.kie.workbench.common.stunner.core.client.session.impl.AbstractSession;
import org.kie.workbench.common.stunner.core.client.session.impl.InstanceUtils;
import org.kie.workbench.common.stunner.core.command.Command;
import org.kie.workbench.common.stunner.core.command.CommandResult;
import org.kie.workbench.common.stunner.core.command.util.CommandUtils;
import org.kie.workbench.common.stunner.core.diagram.Diagram;
import org.kie.workbench.common.stunner.core.graph.Element;
import org.kie.workbench.common.stunner.core.graph.util.GraphUtils;
import org.kie.workbench.common.stunner.core.util.DefinitionUtils;

/**
 * A generic session's preview instance for subtypes of <code>AbstractClientSession</code>.
 * It aggregates a custom diagram preview type which provides binds the editors's diagram instance
 * with the diagram and controls for the given session. It also scales the view to the given
 * size for the preview.
 */
@Dependent
@Typed(SessionDiagramPreview.class)
@Default
public class SessionPreviewImpl<S extends AbstractSession>
        extends AbstractSessionViewer<S>
        implements SessionDiagramPreview<S>,
                   CommandRequestLifecycle {

    private final DefinitionUtils definitionUtils;
    private final GraphUtils graphUtils;
    private final ShapeManager shapeManager;
    private final TextPropertyProviderFactory textPropertyProviderFactory;
    private final MouseRequestLifecycle requestLifecycle;
    private final ManagedInstance<WiresCanvas> canvases;
    private final ManagedInstance<PreviewLienzoPanel> canvasPanels;
    private final ManagedInstance<BaseCanvasHandler> canvasHandlers;
    private final ManagedInstance<MediatorsControl<AbstractCanvas>> mediatorControls;
    private final ManagedInstance<AlertsControl<AbstractCanvas>> alertControls;
    private final ManagedInstance<SelectionControl<AbstractCanvasHandler, Element>> selectionControls;
    private final ManagedInstance<CanvasCommandFactory> canvasCommandFactories;
    private final ManagedInstance<CanvasCommandManager<AbstractCanvasHandler>> canvasCommandManagers;

    private WiresCanvas canvas;
    private PreviewLienzoPanel canvasPanel;
    private SessionPreviewCanvasHandlerProxy canvasHandler;
    private MediatorsControl<AbstractCanvas> mediatorsControl;
    private AlertsControl<AbstractCanvas> alertsControl;
    private SelectionControl<AbstractCanvasHandler, Element> selectionControl;
    private CanvasCommandFactory commandFactory;
    private CanvasCommandManager<AbstractCanvasHandler> commandManager;
    private DiagramPreviewProxy<Diagram> diagramPreview;
    private Predicate<Command<AbstractCanvasHandler, CanvasViolation>> isCommandAllowed;

    @Inject
    @SuppressWarnings("unchecked")
    public SessionPreviewImpl(final DefinitionUtils definitionUtils,
                              final GraphUtils graphUtils,
                              final ShapeManager shapeManager,
                              final TextPropertyProviderFactory textPropertyProviderFactory,
                              final MouseRequestLifecycle requestLifecycle,
                              final @Any ManagedInstance<WiresCanvas> canvases,
                              final @Any ManagedInstance<PreviewLienzoPanel> canvasPanels,
                              final @Any ManagedInstance<BaseCanvasHandler> canvasHandlers,
                              final @Any ManagedInstance<MediatorsControl<AbstractCanvas>> mediatorControls,
                              final @Any ManagedInstance<AlertsControl<AbstractCanvas>> alertControls,
                              final @Any @SingleSelection ManagedInstance<SelectionControl<AbstractCanvasHandler, Element>> selectionControls,
                              final @Any ManagedInstance<CanvasCommandFactory> canvasCommandFactories,
                              final @Any ManagedInstance<CanvasCommandManager<AbstractCanvasHandler>> canvasCommandManagers,
                              final WidgetWrapperView view,
                              final StunnerPreferencesRegistries preferencesRegistries) {
        this.definitionUtils = definitionUtils;
        this.graphUtils = graphUtils;
        this.shapeManager = shapeManager;
        this.textPropertyProviderFactory = textPropertyProviderFactory;
        this.requestLifecycle = requestLifecycle;
        this.canvases = canvases;
        this.canvasPanels = canvasPanels;
        this.canvasHandlers = canvasHandlers;
        this.mediatorControls = mediatorControls;
        this.alertControls = alertControls;
        this.selectionControls = selectionControls;
        this.canvasCommandFactories = canvasCommandFactories;
        this.canvasCommandManagers = canvasCommandManagers;
        this.isCommandAllowed = c -> true;
        this.diagramPreview =
                new DiagramPreviewProxy<Diagram>(view,
                                                 preferencesRegistries) {
                    @Override
                    public SelectionControl<AbstractCanvasHandler, Element> getSelectionControl() {
                        return selectionControl;
                    }

                    @Override
                    public <C extends Canvas> MediatorsControl<C> getMediatorsControl() {
                        return (MediatorsControl<C>) mediatorsControl;
                    }

                    @Override
                    public <C extends Canvas> AlertsControl<C> getAlertsControl() {
                        return (AlertsControl<C>) alertsControl;
                    }

                    @Override
                    protected CanvasPanel getCanvasPanel() {
                        return canvasPanel;
                    }

                    @Override
                    protected void onOpen(final Diagram diagram) {
                        SessionPreviewImpl.this.onOpen(diagram);
                    }

                    @Override
                    protected AbstractCanvas getCanvas() {
                        return canvas;
                    }

                    @Override
                    protected CanvasCommandFactory getCanvasCommandFactory() {
                        return commandFactory;
                    }

                    @Override
                    protected BaseCanvasHandler<Diagram, ?> getCanvasHandler() {
                        return canvasHandler;
                    }

                    @Override
                    protected void enableControls() {
                        // Do not allow manual scaling on the preview presenter, so do not init by default the zoom control.
                    }

                    @Override
                    protected void destroyControls() {
                        // No control actually enabled.
                    }

                    @Override
                    protected void destroyInstances() {
                        SessionPreviewImpl.this.destroyInstances();
                    }
                };
        this.canvas = null;
        this.mediatorsControl = null;
    }

    @PostConstruct
    public void init() {
        requestLifecycle.listen(() -> this);
    }

    @Override
    public void start() {
        getCanvas().getView().getLayer().setSkipDraw(true);
    }

    @Override
    public void rollback() {
    }

    @Override
    public void complete() {
        getCanvas().getView().getLayer().setSkipDraw(false);
    }

    public SessionPreviewImpl setCommandAllowed(final Predicate<Command<AbstractCanvasHandler, CanvasViolation>> isCommandAllowed) {
        this.isCommandAllowed = isCommandAllowed;
        return this;
    }

    @SuppressWarnings("unchecked")
    private void onOpen(final Diagram diagram) {
        final Annotation qualifier = definitionUtils.getQualifier(diagram.getMetadata().getDefinitionSetId());
        final BaseCanvasHandler delegate = InstanceUtils.lookup(canvasHandlers, qualifier);
        canvas = InstanceUtils.lookup(canvases, qualifier);
        canvasPanel = InstanceUtils.lookup(canvasPanels, qualifier);
        canvasHandler = new SessionPreviewCanvasHandlerProxy(delegate,
                                                             definitionUtils.getDefinitionManager(),
                                                             graphUtils,
                                                             shapeManager,
                                                             textPropertyProviderFactory);
        mediatorsControl = InstanceUtils.lookup(mediatorControls, qualifier);
        alertsControl = InstanceUtils.lookup(alertControls, qualifier);
        selectionControl = InstanceUtils.lookup(selectionControls, qualifier);
        commandFactory = InstanceUtils.lookup(canvasCommandFactories, qualifier);
        commandManager = InstanceUtils.lookup(canvasCommandManagers, qualifier);

        // Allow preview to scale as the diagram grows horizontally/vertically
        mediatorsControl.init(canvas);
        alertsControl.init(canvas);
    }

    @SuppressWarnings("unchecked")
    @Override
    protected void onAfterCanvasInitialized() {
        super.onAfterCanvasInitialized();
        canvas.getView().setDecoratorFactory(LienzoCanvasDecoratorFactory.PREVIEW);
    }

    @Override
    protected void onOpenSuccess() {
        super.onOpenSuccess();
        final LienzoCanvasView view = (LienzoCanvasView) getSessionHandler().getAbstractCanvas().getView();
        canvasPanel.observe((ScrollableLienzoPanel) view.getLienzoPanel());
    }

    private void destroyInstances() {
        canvases.destroy(canvas);
        canvases.destroyAll();
        canvasPanels.destroyAll();
        canvasHandlers.destroy(canvasHandler.getWrapped());
        canvasHandlers.destroyAll();
        mediatorControls.destroy(mediatorsControl);
        mediatorControls.destroyAll();
        selectionControls.destroy(selectionControl);
        selectionControls.destroyAll();
        canvasCommandFactories.destroy(commandFactory);
        canvasCommandFactories.destroyAll();
        canvasCommandManagers.destroy(commandManager);
        canvasCommandManagers.destroyAll();
        canvas = null;
        canvasPanel = null;
        canvasHandler = null;
        mediatorsControl = null;
        selectionControl = null;
        commandFactory = null;
        commandManager = null;
        diagramPreview = null;
        isCommandAllowed = null;
    }

    public CanvasCommandManager<AbstractCanvasHandler> getCommandManager() {
        return commandManager;
    }

    public SessionPreviewCanvasHandlerProxy getCanvasHandler() {
        return canvasHandler;
    }

    @Override
    public WiresCanvas getCanvas() {
        return canvas;
    }

    public CanvasCommandFactory getCommandFactory() {
        return commandFactory;
    }

    @Override
    protected DiagramViewer<Diagram, AbstractCanvasHandler> getDiagramViewer() {
        return diagramPreview;
    }

    @Override
    protected Diagram getDiagram() {
        return null != getSessionHandler() ? getSessionHandler().getDiagram() : null;
    }

    @SuppressWarnings("unchecked")
    void commandUndoExecutedFired(@Observes CanvasCommandUndoneEvent commandUndoExecutedEvent) {
        checkNotNull("commandUndoExecutedEvent",
                     commandUndoExecutedEvent);
        final Command<AbstractCanvasHandler, CanvasViolation> command = commandUndoExecutedEvent.getCommand();
        if (isCommandAllowed.test(command)) {
            final AbstractCanvasHandler context = (AbstractCanvasHandler) commandUndoExecutedEvent.getCanvasHandler();
            final CommandResult<CanvasViolation> result = commandUndoExecutedEvent.getResult();
            onUndo(context,
                   command,
                   result);
        }
    }

    private static <T> T checkNotNull(String objName, T obj) {
        return Objects.requireNonNull(obj, "Parameter named '" + objName + "' should be not null!");
    }

    private void onExecute(final AbstractCanvasHandler context,
                           final Command<AbstractCanvasHandler, CanvasViolation> command,
                           final CommandResult<CanvasViolation> result) {
        if (isOperationAllowed(context,
                               result)) {
            getCommandManager().execute(getDiagramViewer().getHandler(),
                                        command);
        }
    }

    private void onUndo(final AbstractCanvasHandler context,
                        final Command<AbstractCanvasHandler, CanvasViolation> command,
                        final CommandResult<CanvasViolation> result) {
        if (isOperationAllowed(context,
                               result)) {
            getCommandManager().undo(getDiagramViewer().getHandler(),
                                     command);
        }
    }

    private boolean isOperationAllowed(final AbstractCanvasHandler sessionHandlerContext,
                                       final CommandResult<CanvasViolation> result) {
        return isSameContext(sessionHandlerContext) && !CommandUtils.isError(result);
    }

    private boolean isSameContext(final AbstractCanvasHandler sessionHandlerContext) {
        return null != getSessionHandler() &&
                getSessionHandler().equals(sessionHandlerContext);
    }

    @Override
    @SuppressWarnings("all")
    public MediatorsControl<AbstractCanvas> getMediatorsControl() {
        return mediatorsControl;
    }

    @Override
    @SuppressWarnings("all")
    public AlertsControl<AbstractCanvas> getAlertsControl() {
        return alertsControl;
    }
}
