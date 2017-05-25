/*
 * Copyright 2016 Red Hat, Inc. and/or its affiliates.
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

package org.kie.workbench.common.stunner.core.client.canvas.controls.toolbox.command.builder;

import javax.enterprise.event.Observes;
import javax.inject.Inject;

import org.kie.workbench.common.stunner.core.client.api.ShapeManager;
import org.kie.workbench.common.stunner.core.client.canvas.AbstractCanvasHandler;
import org.kie.workbench.common.stunner.core.client.canvas.Transform;
import org.kie.workbench.common.stunner.core.client.canvas.controls.toolbox.command.Context;
import org.kie.workbench.common.stunner.core.client.components.drag.DragProxyCallback;
import org.kie.workbench.common.stunner.core.client.components.glyph.DefinitionGlyphTooltip;
import org.kie.workbench.common.stunner.core.client.components.glyph.GlyphTooltip;
import org.kie.workbench.common.stunner.core.client.event.keyboard.KeyDownEvent;
import org.kie.workbench.common.stunner.core.client.event.keyboard.KeyboardEvent;
import org.kie.workbench.common.stunner.core.client.service.ClientFactoryService;
import org.kie.workbench.common.stunner.core.client.shape.factory.ShapeFactory;
import org.kie.workbench.common.stunner.core.client.shape.view.glyph.Glyph;
import org.kie.workbench.common.stunner.core.graph.Element;
import org.kie.workbench.common.stunner.core.graph.content.view.Point2D;
import org.kie.workbench.common.stunner.core.graph.processing.index.bounds.GraphBoundsIndexer;

import static org.uberfire.commons.validation.PortablePreconditions.checkNotNull;

public abstract class AbstractElementBuilderCommand<I> extends AbstractBuilderCommand<I> {

    private final ShapeManager shapeManager;
    private final DefinitionGlyphTooltip<?> glyphTooltip;
    private I iconView;
    private ShapeFactory factory;

    @Inject
    public AbstractElementBuilderCommand(final ClientFactoryService clientFactoryServices,
                                         final ShapeManager shapeManager,
                                         final DefinitionGlyphTooltip<?> glyphTooltip,
                                         final GraphBoundsIndexer graphBoundsIndexer) {
        super(clientFactoryServices,
              graphBoundsIndexer);
        this.shapeManager = shapeManager;
        this.glyphTooltip = glyphTooltip;
    }

    protected abstract String getGlyphDefinitionId();

    @Override
    public void destroy() {
        super.destroy();
        this.factory = null;
        this.iconView = null;
    }

    @Override
    @SuppressWarnings("unchecked")
    public I getIcon(final AbstractCanvasHandler context,
                     final double width,
                     final double height) {
        if (null == iconView) {
            final ShapeFactory factory = getFactory(context);
            // TODO: Review why glyphs result smaller than expected. Adding some padding pixels here.
            final Glyph<I> glyph = factory.glyph(getGlyphDefinitionId(),
                                                 width + 6,
                                                 height + 6);
            this.iconView = glyph.getGroup();
        }
        return iconView;
    }

    @Override
    public void mouseEnter(final Context<AbstractCanvasHandler> context,
                           final Element element) {
        super.mouseEnter(context,
                         element);
        if (null != getFactory(context.getCanvasHandler())) {
            final Transform transform = context.getCanvasHandler().getCanvas().getLayer().getTransform();
            final double ax = context.getCanvasHandler().getAbstractCanvas().getView().getAbsoluteX();
            final double ay = context.getCanvasHandler().getAbstractCanvas().getView().getAbsoluteY();
            // As tooltip is a floating view (not part of the canvas), need to transform the cartesian coordinates
            // using current transform attributes to obtain the right absolute position on the screen.
            final Point2D t = transform.transform(context.getX(),
                                                  context.getY());
            glyphTooltip.showTooltip(getGlyphDefinitionId(),
                                     ax + t.getX() + 20,
                                     ay + t.getY(),
                                     GlyphTooltip.Direction.WEST);
        }
    }

    @Override
    public void mouseExit(final Context<AbstractCanvasHandler> context,
                          final Element element) {
        super.mouseExit(context,
                        element);
        glyphTooltip.hide();
    }

    protected ShapeFactory getFactory(final AbstractCanvasHandler context) {
        if (null == factory) {
            final String ssid = context.getDiagram().getMetadata().getShapeSetId();
            factory = shapeManager.getShapeSet(ssid)
                    .getShapeFactory();
        }
        return factory;
    }

    @Override
    protected DragProxyCallback getDragProxyCallback(final Context<AbstractCanvasHandler> context,
                                                     final Element element,
                                                     final Element item) {
        return new DragProxyCallback() {
            @Override
            public void onStart(final int x1,
                                final int y1) {
                AbstractElementBuilderCommand.this.onStart(context,
                                                           element,
                                                           item,
                                                           x1,
                                                           y1);
            }

            @Override
            public void onMove(final int x1,
                               final int y1) {
                AbstractElementBuilderCommand.this.onMove(context,
                                                          element,
                                                          item,
                                                          x1,
                                                          y1);
            }

            @Override
            public void onComplete(final int x1,
                                   final int y1) {
                AbstractElementBuilderCommand.this.onComplete(context,
                                                              element,
                                                              item,
                                                              x1,
                                                              y1);
            }
        };
    }

    @Override
    protected void onItemBuilt(final Context<AbstractCanvasHandler> context,
                               final String uuid) {
        super.onItemBuilt(context,
                          uuid);
        glyphTooltip.hide();
    }

    protected void clearDragProxy() {
        getDragProxyFactory().clear();
    }

    ShapeManager getShapeManager() {
        return shapeManager;
    }

    DefinitionGlyphTooltip<?> getGlyphTooltip() {
        return glyphTooltip;
    }

    /**
     * Listens for <code>ESC</code> key pressed - cancels the current drag/build operation.
     */
    void onKeyDownEvent(final @Observes KeyDownEvent keyDownEvent) {
        checkNotNull("keyDownEvent",
                     keyDownEvent);
        final KeyboardEvent.Key key = keyDownEvent.getKey();
        if (null != key && KeyboardEvent.Key.ESC.equals(key)) {
            clearDragProxy();
        }
    }
}
