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

package org.kie.workbench.common.stunner.client.widgets.palette.factory;

import java.util.Collection;
import java.util.LinkedList;
import java.util.List;

import javax.annotation.PostConstruct;
import javax.enterprise.context.Dependent;
import javax.enterprise.event.Event;
import javax.inject.Inject;

import org.jboss.errai.ioc.client.api.ManagedInstance;
import org.jboss.errai.ioc.client.container.SyncBeanDef;
import org.jboss.errai.ioc.client.container.SyncBeanManager;
import org.kie.workbench.common.stunner.client.widgets.palette.BS3PaletteWidget;
import org.kie.workbench.common.stunner.core.api.DefinitionManager;
import org.kie.workbench.common.stunner.core.client.api.ShapeManager;
import org.kie.workbench.common.stunner.core.client.canvas.AbstractCanvasHandler;
import org.kie.workbench.common.stunner.core.client.canvas.CanvasHandler;
import org.kie.workbench.common.stunner.core.client.canvas.controls.event.BuildCanvasShapeEvent;
import org.kie.workbench.common.stunner.core.client.canvas.controls.event.CanvasShapeDragStartEvent;
import org.kie.workbench.common.stunner.core.client.canvas.controls.event.CanvasShapeDragUpdateEvent;
import org.kie.workbench.common.stunner.core.client.components.palette.factory.AbstractPaletteFactory;
import org.kie.workbench.common.stunner.core.client.components.palette.factory.DefaultDefSetPaletteDefinitionFactory;
import org.kie.workbench.common.stunner.core.client.components.palette.model.PaletteDefinition;

@Dependent
public class BS3PaletteFactoryImpl extends AbstractPaletteFactory<PaletteDefinition, BS3PaletteWidget<PaletteDefinition>>
        implements BS3PaletteFactory {

    private final Event<BuildCanvasShapeEvent> buildCanvasShapeEvent;
    private final Event<CanvasShapeDragStartEvent> canvasShapeDragStartEvent;
    private final Event<CanvasShapeDragUpdateEvent> canvasShapeDragUpdateEvent;
    private final List<BS3PaletteViewFactory> viewFactories = new LinkedList<>();

    @Inject
    public BS3PaletteFactoryImpl(final ShapeManager shapeManager,
                                 final SyncBeanManager beanManager,
                                 final DefinitionManager definitionManager,
                                 final ManagedInstance<DefaultDefSetPaletteDefinitionFactory> defaultPaletteDefinitionFactoryInstance,
                                 final Event<BuildCanvasShapeEvent> buildCanvasShapeEvent,
                                 final Event<CanvasShapeDragStartEvent> canvasShapeDragStartEvent,
                                 final Event<CanvasShapeDragUpdateEvent> canvasShapeDragUpdateEvent) {
        super(shapeManager,
              beanManager,
              definitionManager,
              defaultPaletteDefinitionFactoryInstance);
        this.buildCanvasShapeEvent = buildCanvasShapeEvent;
        this.canvasShapeDragStartEvent = canvasShapeDragStartEvent;
        this.canvasShapeDragUpdateEvent = canvasShapeDragUpdateEvent;
    }

    @PostConstruct
    @SuppressWarnings("unchecked")
    public void init() {
        super.init();
        Collection<SyncBeanDef<BS3PaletteViewFactory>> beanDefSets = beanManager.lookupBeans(BS3PaletteViewFactory.class);
        for (SyncBeanDef<BS3PaletteViewFactory> defSet : beanDefSets) {
            BS3PaletteViewFactory factory = defSet.getInstance();
            viewFactories.add(factory);
        }
    }

    @Override
    public BS3PaletteWidget<PaletteDefinition> newPalette(final String shapeSetId,
                                                          final CanvasHandler canvasHandler) {

        final BS3PaletteWidget<PaletteDefinition> palette = super.newPalette(shapeSetId);
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
            palette.onItemDragStart((definition, factory, x, y) ->
                                            canvasShapeDragStartEvent.fire(new CanvasShapeDragStartEvent((AbstractCanvasHandler) canvasHandler,
                                                                                                         definition,
                                                                                                         factory,
                                                                                                         x,
                                                                                                         y)));
            palette.onItemDragUpdate((definition, factory, x, y) ->
                                             canvasShapeDragUpdateEvent.fire(new CanvasShapeDragUpdateEvent((AbstractCanvasHandler) canvasHandler,
                                                                                                            definition,
                                                                                                            factory,
                                                                                                            x,
                                                                                                            y)));
        }

        return palette;
    }

    @Override
    protected void beforeBindPalette(final PaletteDefinition paletteDefinition,
                                     final BS3PaletteWidget<PaletteDefinition> palette,
                                     final String shapeSetId) {
        super.beforeBindPalette(paletteDefinition,
                                palette,
                                shapeSetId);
        final String defSetId = paletteDefinition.getDefinitionSetId();
        BS3PaletteViewFactory viewFactory = getViewFactory(defSetId);
        palette.setViewFactory(viewFactory);
    }

    BS3PaletteViewFactory getViewFactory(final String defSetId) {
        for (final BS3PaletteViewFactory factory : viewFactories) {
            if (factory.accepts(defSetId)) {
                return factory;
            }
        }
        return null;
    }
}
