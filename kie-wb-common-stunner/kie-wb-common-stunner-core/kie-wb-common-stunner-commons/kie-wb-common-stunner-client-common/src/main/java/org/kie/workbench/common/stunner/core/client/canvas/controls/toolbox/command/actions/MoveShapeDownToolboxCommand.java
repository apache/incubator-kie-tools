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

import java.util.logging.Level;
import java.util.logging.Logger;
import javax.enterprise.context.Dependent;
import javax.inject.Inject;

import org.kie.workbench.common.stunner.core.client.canvas.AbstractCanvasHandler;
import org.kie.workbench.common.stunner.core.client.canvas.controls.toolbox.command.Context;
import org.kie.workbench.common.stunner.core.client.components.glyph.DefinitionGlyphTooltip;
import org.kie.workbench.common.stunner.core.client.shape.Shape;
import org.kie.workbench.common.stunner.core.graph.Element;

@Dependent
public class MoveShapeDownToolboxCommand<I> extends AbstractActionToolboxCommand<I> {

    private static Logger LOGGER = Logger.getLogger(MoveShapeDownToolboxCommand.class.getName());

    protected MoveShapeDownToolboxCommand() {
        this(null);
    }

    @Inject
    public MoveShapeDownToolboxCommand(final DefinitionGlyphTooltip<?> glyphTooltip) {
        super(glyphTooltip);
    }

    // TODO: i18n.
    @Override
    public String getTitle() {
        return "Move Down";
    }

    @Override
    public void click(final Context<AbstractCanvasHandler> context,
                      final Element element) {
        super.click(context,
                    element);
        final String uuid = element.getUUID();
        final Shape<?> shape = context.getCanvasHandler().getCanvas().getShape(uuid);
        if (null != shape) {
            shape.getShapeView().setZIndex(shape.getShapeView().getZIndex() - 1);
            shape.getShapeView().moveDown();
        } else {
            LOGGER.log(Level.WARNING,
                       "Shape not found for UUID [" + uuid + "]");
        }
    }

    @Override
    public void destroy() {
    }
}
