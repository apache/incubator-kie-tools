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

package org.kie.workbench.common.dmn.client.widgets.panel;

import com.ait.lienzo.client.core.types.Transform;
import com.google.gwt.core.client.Scheduler;
import com.google.gwt.event.dom.client.ContextMenuEvent;
import com.google.gwt.event.dom.client.ContextMenuHandler;
import com.google.gwt.event.dom.client.MouseWheelEvent;
import com.google.gwt.event.dom.client.MouseWheelHandler;
import org.kie.workbench.common.dmn.client.widgets.layer.DMNGridLayer;
import org.uberfire.ext.wires.core.grids.client.widget.dom.HasDOMElementResources;
import org.uberfire.ext.wires.core.grids.client.widget.layer.impl.GridLienzoPanel;
import org.uberfire.ext.wires.core.grids.client.widget.layer.pinning.TransformMediator;
import org.uberfire.ext.wires.core.grids.client.widget.layer.pinning.impl.RestrictedMousePanMediator;

public class DMNGridPanel extends GridLienzoPanel {

    public static final int LIENZO_PANEL_WIDTH = 1000;

    public static final int LIENZO_PANEL_HEIGHT = 450;

    private RestrictedMousePanMediator mousePanMediator;

    public DMNGridPanel(final DMNGridLayer gridLayer,
                        final RestrictedMousePanMediator mousePanMediator,
                        final ContextMenuHandler contextMenuHandler) {
        super(LIENZO_PANEL_WIDTH,
              LIENZO_PANEL_HEIGHT,
              gridLayer);
        this.mousePanMediator = mousePanMediator;

        getDomElementContainer().addDomHandler(destroyDOMElements(),
                                               MouseWheelEvent.getType());
        getDomElementContainer().addDomHandler(contextMenuHandler,
                                               ContextMenuEvent.getType());
    }

    private MouseWheelHandler destroyDOMElements() {
        return (event) -> getDefaultGridLayer()
                .getGridWidgets()
                .forEach(gridWidget -> gridWidget
                        .getModel()
                        .getColumns()
                        .stream()
                        .filter(gridColumn -> gridColumn instanceof HasDOMElementResources)
                        .map(gridColumn -> ((HasDOMElementResources) gridColumn))
                        .forEach(HasDOMElementResources::destroyResources));
    }

    @Override
    public void onResize() {
        doResize(() -> {
            updatePanelSize();
            refreshScrollPosition();

            final TransformMediator restriction = mousePanMediator.getTransformMediator();
            final Transform transform = restriction.adjust(getDefaultGridLayer().getViewport().getTransform(),
                                                           getDefaultGridLayer().getVisibleBounds());
            getDefaultGridLayer().getViewport().setTransform(transform);
            getDefaultGridLayer().batch();
        });
    }

    void doResize(final Scheduler.ScheduledCommand command) {
        Scheduler.get().scheduleDeferred(command);
    }
}