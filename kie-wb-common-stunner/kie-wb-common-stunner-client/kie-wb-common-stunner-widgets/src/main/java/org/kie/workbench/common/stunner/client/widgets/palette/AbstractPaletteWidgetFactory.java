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

package org.kie.workbench.common.stunner.client.widgets.palette;

import javax.enterprise.event.Event;

import org.jboss.errai.ioc.client.api.ManagedInstance;
import org.jboss.errai.ioc.client.container.SyncBeanManager;
import org.kie.workbench.common.stunner.core.client.ShapeManager;
import org.kie.workbench.common.stunner.core.client.canvas.AbstractCanvasHandler;
import org.kie.workbench.common.stunner.core.client.canvas.CanvasHandler;
import org.kie.workbench.common.stunner.core.client.canvas.controls.event.BuildCanvasShapeEvent;
import org.kie.workbench.common.stunner.core.client.components.palette.factory.AbstractPaletteFactory;
import org.kie.workbench.common.stunner.core.client.components.palette.factory.DefaultDefSetPaletteDefinitionFactory;
import org.kie.workbench.common.stunner.core.client.components.palette.model.PaletteDefinition;

public abstract class AbstractPaletteWidgetFactory<I extends PaletteDefinition, P extends PaletteWidget<I, ?>>
        extends AbstractPaletteFactory<I, P>
        implements PaletteWidgetFactory<I, P> {

    protected Event<BuildCanvasShapeEvent> buildCanvasShapeEvent;
    protected CanvasHandler canvasHandler;

    public AbstractPaletteWidgetFactory(final ShapeManager shapeManager,
                                        final SyncBeanManager beanManager,
                                        final ManagedInstance<DefaultDefSetPaletteDefinitionFactory> defaultPaletteDefinitionFactoryInstance,
                                        final P palette,
                                        final Event<BuildCanvasShapeEvent> buildCanvasShapeEvent) {
        super(shapeManager,
              beanManager,
              defaultPaletteDefinitionFactoryInstance,
              palette);
        this.buildCanvasShapeEvent = buildCanvasShapeEvent;
    }

    @Override
    public PaletteWidgetFactory<I, P> forCanvasHandler(final CanvasHandler canvasHandler) {
        this.canvasHandler = canvasHandler;
        return this;
    }

    @Override
    protected void beforeBindPalette(final I paletteDefinition,
                                     final String shapeSetId) {
        super.beforeBindPalette(paletteDefinition,
                                shapeSetId);
        if (null != canvasHandler) {
            palette.onItemDrop((definition,
                                factory,
                                x,
                                y) ->
                                       buildCanvasShapeEvent.fire(new BuildCanvasShapeEvent((AbstractCanvasHandler) canvasHandler,
                                                                                            definition,
                                                                                            factory,
                                                                                            x,
                                                                                            y)));
        }
    }
}
