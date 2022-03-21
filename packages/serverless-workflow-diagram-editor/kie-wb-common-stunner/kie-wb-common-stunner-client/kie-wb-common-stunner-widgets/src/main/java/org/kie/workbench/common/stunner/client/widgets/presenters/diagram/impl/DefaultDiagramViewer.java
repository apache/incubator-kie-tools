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

package org.kie.workbench.common.stunner.client.widgets.presenters.diagram.impl;

import java.lang.annotation.Annotation;
import java.util.Collections;

import javax.enterprise.context.Dependent;
import javax.enterprise.inject.Any;
import javax.inject.Inject;

import org.jboss.errai.ioc.client.api.ManagedInstance;
import org.kie.workbench.common.stunner.client.lienzo.canvas.LienzoCanvas;
import org.kie.workbench.common.stunner.client.lienzo.canvas.LienzoCanvasView;
import org.kie.workbench.common.stunner.client.widgets.views.WidgetWrapperView;
import org.kie.workbench.common.stunner.core.client.canvas.AbstractCanvas;
import org.kie.workbench.common.stunner.core.client.canvas.AbstractCanvasHandler;
import org.kie.workbench.common.stunner.core.client.canvas.CanvasPanel;
import org.kie.workbench.common.stunner.core.client.canvas.controls.MediatorsControl;
import org.kie.workbench.common.stunner.core.client.canvas.controls.SelectionControl;
import org.kie.workbench.common.stunner.core.client.canvas.controls.select.SingleSelection;
import org.kie.workbench.common.stunner.core.client.canvas.listener.CanvasElementListener;
import org.kie.workbench.common.stunner.core.client.canvas.listener.CanvasShapeListener;
import org.kie.workbench.common.stunner.core.client.preferences.StunnerPreferencesRegistries;
import org.kie.workbench.common.stunner.core.client.session.impl.DefaultCanvasElementListener;
import org.kie.workbench.common.stunner.core.client.session.impl.DefaultCanvasShapeListener;
import org.kie.workbench.common.stunner.core.client.session.impl.InstanceUtils;
import org.kie.workbench.common.stunner.core.diagram.Diagram;
import org.kie.workbench.common.stunner.core.graph.Element;
import org.kie.workbench.common.stunner.core.util.DefinitionUtils;

/**
 * A generic DiagramViewer implementation.
 * It opens a diagram instance in a new canvas and handler instances for read-only purposes.,
 * It provides a zoom and selection control that third parties can interacting with, but it does not provide
 * any controls that allow the diagram's authoring.
 */
@Dependent
public class DefaultDiagramViewer
        extends AbstractDiagramViewer<Diagram, AbstractCanvasHandler> {

    private final DefinitionUtils definitionUtils;
    private final ManagedInstance<AbstractCanvas> canvasInstances;
    private final ManagedInstance<CanvasPanel> canvasPanelInstances;
    private final ManagedInstance<AbstractCanvasHandler> canvasHandlerInstances;
    private final ManagedInstance<MediatorsControl<AbstractCanvas>> mediatorsControlInstances;
    private final ManagedInstance<SelectionControl<AbstractCanvasHandler, Element>> selectionControlInstances;
    private final StunnerPreferencesRegistries preferencesRegistries;

    private AbstractCanvas canvas;
    private CanvasShapeListener shapeListener;
    private CanvasElementListener elementListener;
    private CanvasPanel canvasPanel;
    private AbstractCanvasHandler canvasHandler;
    private MediatorsControl<AbstractCanvas> mediatorsControl;
    private SelectionControl<AbstractCanvasHandler, Element> selectionControl;

    @Inject
    public DefaultDiagramViewer(final DefinitionUtils definitionUtils,
                                final @Any ManagedInstance<AbstractCanvas> canvasInstances,
                                final @Any ManagedInstance<CanvasPanel> canvasPanelInstances,
                                final @Any ManagedInstance<AbstractCanvasHandler> canvasHandlerInstances,
                                final @Any ManagedInstance<MediatorsControl<AbstractCanvas>> mediatorsControlInstances,
                                final @Any @SingleSelection ManagedInstance<SelectionControl<AbstractCanvasHandler, Element>> selectionControlInstances,
                                final WidgetWrapperView view,
                                final StunnerPreferencesRegistries preferencesRegistries) {
        super(view);
        this.definitionUtils = definitionUtils;
        this.canvasInstances = canvasInstances;
        this.canvasPanelInstances = canvasPanelInstances;
        this.canvasHandlerInstances = canvasHandlerInstances;
        this.mediatorsControlInstances = mediatorsControlInstances;
        this.selectionControlInstances = selectionControlInstances;
        this.preferencesRegistries = preferencesRegistries;
    }

    @Override
    protected void onOpen(final Diagram diagram) {
        final Annotation qualifier =
                definitionUtils.getQualifier(diagram.getMetadata().getDefinitionSetId());
        canvasPanel = InstanceUtils.lookup(canvasPanelInstances, qualifier);
        canvas = InstanceUtils.lookup(canvasInstances, qualifier);
        canvasHandler = InstanceUtils.lookup(canvasHandlerInstances, qualifier);
        mediatorsControl = InstanceUtils.lookup(mediatorsControlInstances, qualifier);
        selectionControl = InstanceUtils.lookup(selectionControlInstances, qualifier);
        shapeListener = new DefaultCanvasShapeListener(Collections.singletonList(mediatorsControl));
        canvas.addRegistrationListener(shapeListener);
        elementListener = new DefaultCanvasElementListener(Collections.singletonList(selectionControl));
        canvasHandler.addRegistrationListener(elementListener);
    }

    @Override
    protected void enableControls() {
        mediatorsControl.init(getCanvas());
        selectionControl.init(getHandler());
    }

    @Override
    protected void destroyControls() {
        mediatorsControl.destroy();
        selectionControl.destroy();
        mediatorsControlInstances.destroy(mediatorsControl);
        mediatorsControlInstances.destroyAll();
        selectionControlInstances.destroy(selectionControl);
        selectionControlInstances.destroyAll();
        mediatorsControl = null;
        selectionControl = null;
    }

    @Override
    protected void destroyInstances() {
        super.destroyInstances();
        canvasInstances.destroy(canvas);
        canvasInstances.destroyAll();
        canvasPanelInstances.destroyAll();
        canvasHandlerInstances.destroy(canvasHandler);
        canvasHandlerInstances.destroyAll();
        canvas = null;
        canvasPanel = null;
        canvasHandler = null;
        shapeListener = null;
        elementListener = null;
    }

    @Override
    public AbstractCanvasHandler getHandler() {
        return canvasHandler;
    }

    @Override
    protected void scalePanel(final int width,
                              final int height) {
        getLienzoCanvas().getView().setPixelSize(width,
                                                 height);
    }

    @SuppressWarnings("unchecked")
    private LienzoCanvas<LienzoCanvasView> getLienzoCanvas() {
        return (LienzoCanvas) getCanvas();
    }

    @Override
    public AbstractCanvas getCanvas() {
        return canvas;
    }

    @Override
    public CanvasPanel getCanvasPanel() {
        return canvasPanel;
    }

    @Override
    protected StunnerPreferencesRegistries getPreferencesRegistry() {
        return preferencesRegistries;
    }

    @Override
    @SuppressWarnings("unchecked")
    public MediatorsControl<AbstractCanvas> getMediatorsControl() {
        return mediatorsControl;
    }

    @Override
    public SelectionControl<AbstractCanvasHandler, Element> getSelectionControl() {
        return selectionControl;
    }
}
