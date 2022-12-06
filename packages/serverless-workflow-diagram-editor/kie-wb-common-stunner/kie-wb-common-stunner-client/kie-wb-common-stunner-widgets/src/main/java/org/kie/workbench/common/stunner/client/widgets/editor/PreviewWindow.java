/*
 * Copyright 2022 Red Hat, Inc. and/or its affiliates.
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

package org.kie.workbench.common.stunner.client.widgets.editor;

import javax.enterprise.context.ApplicationScoped;
import javax.enterprise.event.Observes;
import javax.inject.Inject;

import com.google.gwt.dom.client.Element;
import com.google.gwt.dom.client.Style;
import com.google.gwt.user.client.Timer;
import com.google.gwt.user.client.ui.FlowPanel;
import com.google.gwt.user.client.ui.RootPanel;
import com.google.gwt.user.client.ui.Widget;
import org.gwtbootstrap3.extras.animate.client.ui.Animate;
import org.gwtbootstrap3.extras.animate.client.ui.constants.Animation;
import org.jboss.errai.ioc.client.api.ManagedInstance;
import org.kie.workbench.common.stunner.client.lienzo.components.mediators.preview.TogglePreviewEvent;
import org.kie.workbench.common.stunner.client.widgets.canvas.PreviewLienzoPanel;
import org.kie.workbench.common.stunner.client.widgets.presenters.session.SessionDiagramPreview;
import org.kie.workbench.common.stunner.client.widgets.presenters.session.SessionViewer;
import org.kie.workbench.common.stunner.client.widgets.presenters.session.impl.SessionPreviewImpl;
import org.kie.workbench.common.stunner.core.client.api.SessionManager;
import org.kie.workbench.common.stunner.core.client.canvas.AbstractCanvas;
import org.kie.workbench.common.stunner.core.client.canvas.AbstractCanvasHandler;
import org.kie.workbench.common.stunner.core.client.canvas.CanvasPanel;
import org.kie.workbench.common.stunner.core.client.service.ClientRuntimeError;
import org.kie.workbench.common.stunner.core.client.session.ClientSession;
import org.kie.workbench.common.stunner.core.client.session.impl.AbstractSession;
import org.kie.workbench.common.stunner.core.diagram.Diagram;

@ApplicationScoped
public class PreviewWindow {

    @Inject
    private SessionManager sessionManager;

    @Inject
    private ManagedInstance<SessionDiagramPreview<AbstractSession>> sessionPreviews;

    private SessionDiagramPreview<AbstractSession> previewWidget;

    private static final int DURATION = 300;
    public static final int MARGIN = 25;

    private FlowPanel previewRoot;
    private boolean closing;

    private void addWidget() {
        deleteWidget();

        previewRoot = new FlowPanel();
        RootPanel.get().add(previewRoot);
        previewRoot.add(previewWidget.getView());

        AbstractCanvasHandler abstractCanvasHandler = previewWidget.getHandler();
        AbstractCanvas abstractCanvas = abstractCanvasHandler.getAbstractCanvas();
        AbstractCanvas.CanvasView canvasView = abstractCanvas.getView();
        CanvasPanel canvasPanel = canvasView.getPanel();
        Widget widget = canvasPanel.asWidget();
        Element element = widget.getElement();

        Style style = element.getStyle();
        style.setBorderWidth(1, Style.Unit.PX);
        style.setBorderStyle(Style.BorderStyle.SOLID);
        style.setBorderColor("#808080");
    }

    private void close() {
        if (null != previewRoot && previewRoot.isVisible() && !closing) {
            closing = true;
            Animate.animate(previewRoot,
                            Animation.FADE_OUT,
                            1,
                            DURATION);
            final Timer hideTimer = new Timer() {
                @Override
                public void run() {
                    deleteWidget();
                    if (null != previewWidget) {
                        previewWidget.destroy();
                        sessionPreviews.destroy(previewWidget);
                        previewWidget = null;
                        closing = false;
                    }
                }
            };
            hideTimer.schedule(DURATION);
        }
    }

    private void deleteWidget() {
        if (null != previewRoot) {
            previewRoot.removeFromParent();
            previewRoot = null;
        }
    }

    private void show(final double x,
                      final double y,
                      final double width,
                      final double height) {
        ClientSession session = sessionManager.getCurrentSession();
        if (session instanceof AbstractSession) {
            if (null != previewWidget) {
                close();
            }
            previewWidget = sessionPreviews.get();
            previewWidget.open((AbstractSession) session,
                               new SessionViewer.SessionViewerCallback<Diagram>() {
                                   @Override
                                   public void afterCanvasInitialized() {
                                       addWidget();
                                       translate(x, y, width, height);
                                       Animate.animate(previewRoot,
                                                       Animation.FADE_IN,
                                                       1,
                                                       DURATION);
                                   }

                                   @Override
                                   public void onError(final ClientRuntimeError error) {
                                       showError(error);
                                   }
                               });
        }
    }

    private void showError(final ClientRuntimeError error) {
        final String s = error.toString();
        previewWidget.getAlertsControl().addError(s);
    }

    private void translate(final double clientLeft,
                           final double clientTop,
                           final double clientWidth,
                           final double clientHeight) {
        SessionPreviewImpl sessionPreview = (SessionPreviewImpl) previewWidget;
        PreviewLienzoPanel previewLienzoPanel = (PreviewLienzoPanel) sessionPreview.getCanvas().getView().getPanel();

        int width = previewLienzoPanel.getWidthPx();
        int height = previewLienzoPanel.getHeightPx();

        final double previewX = clientLeft + clientWidth - width - MARGIN;
        final double previewY = clientTop + clientHeight - height - MARGIN;

        Style style = previewRoot.getElement().getStyle();
        style.setPosition(Style.Position.ABSOLUTE);
        style.setLeft(previewX, Style.Unit.PX);
        style.setTop(previewY, Style.Unit.PX);
        style.setBorderWidth(1, Style.Unit.PX);
        style.setBorderStyle(Style.BorderStyle.SOLID);
        style.setBorderColor("#808080");
    }

    void onTogglePreviewEvent(@Observes TogglePreviewEvent event) {
        if (null != previewRoot && previewRoot.isVisible()) {
            switch (event.getEventType()) {
                case TOGGLE:
                case HIDE:
                case RESIZE:
                    close();
                    break;
                case SHOW:
                    break;
            }
        } else {
            switch (event.getEventType()) {
                case HIDE:
                    close();
                    break;
                case SHOW:
                case TOGGLE:
                    show(event.getX(),
                         event.getY(),
                         event.getWidth(),
                         event.getHeight());
                    break;
            }
        }
    }
}