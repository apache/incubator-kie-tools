/*
 * Copyright 2016 Red Hat, Inc. and/or its affiliates.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *       http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.uberfire.ext.wires.core.grids.client.widget.layer.pinning.impl;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Objects;
import java.util.Set;

import com.ait.lienzo.client.core.mediator.IMediator;
import com.ait.lienzo.client.core.mediator.Mediators;
import com.ait.lienzo.client.core.shape.IPrimitive;
import com.ait.lienzo.client.core.shape.Viewport;
import com.ait.lienzo.client.core.types.Transform;
import com.google.gwt.user.client.Command;
import org.uberfire.ext.wires.core.grids.client.widget.grid.GridWidget;
import org.uberfire.ext.wires.core.grids.client.widget.grid.animation.GridWidgetEnterPinnedModeAnimation;
import org.uberfire.ext.wires.core.grids.client.widget.grid.animation.GridWidgetExitPinnedModeAnimation;
import org.uberfire.ext.wires.core.grids.client.widget.layer.GridLayer;
import org.uberfire.ext.wires.core.grids.client.widget.layer.pinning.GridPinnedModeManager;
import org.uberfire.ext.wires.core.grids.client.widget.layer.pinning.TransformMediator;

/**
 * Default implementation of {@link GridPinnedModeManager} that uses animations to enter and/exit "pinned" mode.
 */
public class DefaultPinnedModeManager implements GridPinnedModeManager {

    private final GridLayer gridLayer;

    private final List<Command> onEnterPinnedModeCommands;

    private final List<Command> onExitPinnedModeCommands;

    private PinnedContext context = null;

    public DefaultPinnedModeManager(final GridLayer gridLayer) {
        this.onEnterPinnedModeCommands = new ArrayList<>();
        this.onExitPinnedModeCommands = new ArrayList<>();
        this.gridLayer = Objects.requireNonNull(gridLayer, "gridLayer");
    }

    @Override
    public void enterPinnedMode(final GridWidget gridWidget,
                                final Command onStartCommand) {
        if (context != null) {
            return;
        }
        final Transform transform = gridWidget.getViewport().getTransform();
        final double translateX = transform.getTranslateX();
        final double translateY = transform.getTranslateY();
        final double scaleX = transform.getScaleX();
        final double scaleY = transform.getScaleY();
        final PinnedContext newState = new PinnedContext(gridWidget,
                                                         translateX,
                                                         translateY,
                                                         scaleX,
                                                         scaleY);

        final Set<GridWidget> gridWidgetsToFadeFromView = new HashSet<>(gridLayer.getGridWidgets());
        gridWidgetsToFadeFromView.remove(gridWidget);
        final Set<IPrimitive<?>> gridWidgetConnectorsToFadeFromView = gridLayer.getGridWidgetConnectors();

        doEnterPinnedMode(() -> {
                              context = newState;
                              onStartCommand.execute();
                              enableGridTransformMediator(gridWidget);
                          },
                          gridWidget,
                          gridWidgetsToFadeFromView,
                          gridWidgetConnectorsToFadeFromView);
    }

    protected void doEnterPinnedMode(final Command onStartCommand,
                                     final GridWidget gridWidget,
                                     final Set<GridWidget> gridWidgetsToFadeFromView,
                                     final Set<IPrimitive<?>> gridWidgetConnectorsToFadeFromView) {
        final GridWidgetEnterPinnedModeAnimation enterAnimation = new GridWidgetEnterPinnedModeAnimation(gridWidget,
                                                                                                         gridWidgetsToFadeFromView,
                                                                                                         gridWidgetConnectorsToFadeFromView,
                                                                                                         onStartCommand,
                                                                                                         onEnterPinnedModeCommands);
        enterAnimation.run();
    }

    @Override
    public void exitPinnedMode(final Command onCompleteCommand) {
        if (context == null) {
            return;
        }

        final Set<GridWidget> gridWidgetsToFadeIntoView = new HashSet<>(gridLayer.getGridWidgets());
        gridWidgetsToFadeIntoView.remove(context.getGridWidget());
        final Set<IPrimitive<?>> gridWidgetConnectorsToFadeIntoView = gridLayer.getGridWidgetConnectors();

        doExitPinnedMode(() -> {
                             context = null;
                             onCompleteCommand.execute();
                             enableDefaultTransformMediator();
                         },
                         gridWidgetsToFadeIntoView,
                         gridWidgetConnectorsToFadeIntoView);
    }

    protected void doExitPinnedMode(final Command onCompleteCommand,
                                    final Set<GridWidget> gridWidgetsToFadeIntoView,
                                    final Set<IPrimitive<?>> gridWidgetConnectorsToFadeIntoView) {
        final GridWidgetExitPinnedModeAnimation exitAnimation = new GridWidgetExitPinnedModeAnimation(context,
                                                                                                      gridWidgetsToFadeIntoView,
                                                                                                      gridWidgetConnectorsToFadeIntoView,
                                                                                                      onCompleteCommand,
                                                                                                      onExitPinnedModeCommands);
        exitAnimation.run();
    }

    private void enableGridTransformMediator(final GridWidget gridWidget) {
        for (IMediator mediator : getMediators()) {
            if (mediator instanceof RestrictedMousePanMediator) {
                ((RestrictedMousePanMediator) mediator).setTransformMediator(new GridTransformMediator(gridWidget));
            }
        }
    }

    private void enableDefaultTransformMediator() {
        for (IMediator mediator : getMediators()) {
            if (mediator instanceof RestrictedMousePanMediator) {
                ((RestrictedMousePanMediator) mediator).setTransformMediator(getDefaultTransformMediator());
            }
        }
    }

    private Mediators getMediators() {
        final Viewport viewport = gridLayer.getViewport();
        final Mediators mediators = viewport.getMediators();
        return mediators;
    }

    @Override
    public void updatePinnedContext(final GridWidget gridWidget) throws IllegalStateException {
        if (context == null) {
            throw new IllegalStateException("'pinned' mode has not been entered.");
        }

        for (IMediator mediator : gridLayer.getViewport().getMediators()) {
            if (mediator instanceof RestrictedMousePanMediator) {
                ((RestrictedMousePanMediator) mediator).setTransformMediator(new GridTransformMediator(gridWidget));
            }
        }

        final Transform transform = gridWidget.getViewport().getTransform();
        final double scaleX = context.getScaleX();
        final double scaleY = context.getScaleY();
        final double translateX = transform.getTranslateX() * scaleX;
        final double translateY = transform.getTranslateY() * scaleY;
        context = new PinnedContext(gridWidget,
                                    translateX,
                                    translateY,
                                    context.getScaleX(),
                                    context.getScaleY());
    }

    @Override
    public PinnedContext getPinnedContext() {
        return context;
    }

    @Override
    public boolean isGridPinned() {
        return context != null;
    }

    @Override
    public TransformMediator getDefaultTransformMediator() {
        return gridLayer.getDefaultTransformMediator();
    }

    @Override
    public void addOnEnterPinnedModeCommand(final Command command) {
        onEnterPinnedModeCommands.add(command);
    }

    @Override
    public void addOnExitPinnedModeCommand(final Command command) {
        onExitPinnedModeCommands.add(command);
    }
}
