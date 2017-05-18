/*
 * Copyright 2016 Red Hat, Inc. and/or its affiliates.
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *  
 *     http://www.apache.org/licenses/LICENSE-2.0
 *  
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.kie.workbench.common.stunner.core.client.canvas.controls.toolbox.command.actions;

import org.kie.workbench.common.stunner.core.client.canvas.AbstractCanvasHandler;
import org.kie.workbench.common.stunner.core.client.canvas.Transform;
import org.kie.workbench.common.stunner.core.client.canvas.controls.toolbox.command.AbstractToolboxCommand;
import org.kie.workbench.common.stunner.core.client.canvas.controls.toolbox.command.Context;
import org.kie.workbench.common.stunner.core.client.components.glyph.DefinitionGlyphTooltip;
import org.kie.workbench.common.stunner.core.client.components.glyph.GlyphTooltip;
import org.kie.workbench.common.stunner.core.graph.Element;
import org.kie.workbench.common.stunner.core.graph.content.view.Point2D;

public abstract class AbstractActionToolboxCommand<I> extends AbstractToolboxCommand<I> {

    private final DefinitionGlyphTooltip<?> glyphTooltip;
    private I icon;

    protected AbstractActionToolboxCommand(final DefinitionGlyphTooltip<?> glyphTooltip) {
        this.glyphTooltip = glyphTooltip;
    }

    public AbstractActionToolboxCommand<I> setIcon(final I icon) {
        this.icon = icon;
        return this;
    }

    @Override
    public I getIcon(final AbstractCanvasHandler context,
                     final double width,
                     final double height) {
        return icon;
    }

    @Override
    public void mouseEnter(final Context<AbstractCanvasHandler> context,
                           final Element element) {
        super.mouseEnter(context,
                         element);
        final Transform transform = context.getCanvasHandler().getCanvas().getLayer().getTransform();
        final double ax = context.getCanvasHandler().getAbstractCanvas().getView().getAbsoluteX();
        final double ay = context.getCanvasHandler().getAbstractCanvas().getView().getAbsoluteY();
        // As tooltip is a floating view (not part of the canvas), need to transform the cartesian coordinates
        // using current transform attributes to obtain the right absolute position on the screen.
        final Point2D t = transform.transform(context.getX(),
                                              context.getY());
        glyphTooltip.show(getTitle(),
                          ax + t.getX() + 20,
                          ay + t.getY(),
                          GlyphTooltip.Direction.WEST);
    }

    @Override
    public void mouseExit(final Context<AbstractCanvasHandler> context,
                          final Element element) {
        super.mouseExit(context,
                        element);
        glyphTooltip.hide();
    }
}
