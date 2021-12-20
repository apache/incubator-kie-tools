/*
 * Copyright 2018 Red Hat, Inc. and/or its affiliates.
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

package org.kie.workbench.common.stunner.client.widgets.palette;

import java.lang.annotation.Annotation;

import javax.annotation.PreDestroy;
import javax.enterprise.context.Dependent;
import javax.enterprise.event.Event;
import javax.enterprise.inject.Any;
import javax.inject.Inject;

import org.jboss.errai.ioc.client.api.ManagedInstance;
import org.kie.workbench.common.stunner.core.api.DefinitionManager;
import org.kie.workbench.common.stunner.core.client.canvas.AbstractCanvasHandler;
import org.kie.workbench.common.stunner.core.client.canvas.controls.event.BuildCanvasShapeEvent;
import org.kie.workbench.common.stunner.core.client.canvas.controls.event.CanvasShapeDragStartEvent;
import org.kie.workbench.common.stunner.core.client.canvas.controls.event.CanvasShapeDragUpdateEvent;
import org.kie.workbench.common.stunner.core.client.components.palette.DefaultPaletteDefinition;
import org.kie.workbench.common.stunner.core.client.components.palette.PaletteDefinitionBuilder;
import org.kie.workbench.common.stunner.core.client.components.palette.PaletteFactory;
import org.kie.workbench.common.stunner.core.util.DefinitionUtils;

@Dependent
public class DefaultPaletteFactory<H extends AbstractCanvasHandler>
        implements PaletteFactory<DefaultPaletteWidget, H> {

    private final DefinitionUtils definitionUtils;
    private final ManagedInstance<PaletteDefinitionBuilder<H, DefaultPaletteDefinition>> paletteDefinitionBuilders;
    private final ManagedInstance<DefaultPaletteWidget> palettes;
    private final Event<BuildCanvasShapeEvent> buildCanvasShapeEvent;
    private final Event<CanvasShapeDragStartEvent> canvasShapeDragStartEvent;
    private final Event<CanvasShapeDragUpdateEvent> canvasShapeDragUpdateEvent;

    @Inject
    public DefaultPaletteFactory(final DefinitionUtils definitionUtils,
                                 final @Any ManagedInstance<PaletteDefinitionBuilder<H, DefaultPaletteDefinition>> paletteDefinitionBuilders,
                                 final @Any ManagedInstance<DefaultPaletteWidget> palettes,
                                 final Event<BuildCanvasShapeEvent> buildCanvasShapeEvent,
                                 final Event<CanvasShapeDragStartEvent> canvasShapeDragStartEvent,
                                 final Event<CanvasShapeDragUpdateEvent> canvasShapeDragUpdateEvent) {
        this.definitionUtils = definitionUtils;
        this.paletteDefinitionBuilders = paletteDefinitionBuilders;
        this.palettes = palettes;
        this.buildCanvasShapeEvent = buildCanvasShapeEvent;
        this.canvasShapeDragStartEvent = canvasShapeDragStartEvent;
        this.canvasShapeDragUpdateEvent = canvasShapeDragUpdateEvent;
    }

    @Override
    public DefaultPaletteWidget newPalette(final H canvasHandler) {
        final DefaultPaletteWidget palette = getPaletteWidget(canvasHandler);
        getPaletteDefinitionBuilder(canvasHandler)
                .build(canvasHandler,
                       palette::bind);
        return palette;
    }

    @PreDestroy
    public void destroy() {
        paletteDefinitionBuilders.destroyAll();
        palettes.destroyAll();
    }

    private PaletteDefinitionBuilder<H, DefaultPaletteDefinition> getPaletteDefinitionBuilder(final H canvasHandler) {
        final String definitionSetId = canvasHandler.getDiagram().getMetadata().getDefinitionSetId();
        final Annotation qualifier = definitionUtils.getQualifier(definitionSetId);
        final ManagedInstance<PaletteDefinitionBuilder<H, DefaultPaletteDefinition>> custom =
                paletteDefinitionBuilders.select(qualifier);
        if (custom.isUnsatisfied()) {
            return paletteDefinitionBuilders.select(DefinitionManager.DEFAULT_QUALIFIER).get();
        }
        return custom.get();
    }

    private DefaultPaletteWidget getPaletteWidget(final H canvasHandler) {
        final DefaultPaletteWidget palette = palettes.get();
        palette.onItemDrop(event -> buildCanvasShapeEvent.fire(new BuildCanvasShapeEvent(canvasHandler,
                                                                                         event.getDefinition(),
                                                                                         event.getFactory(),
                                                                                         event.getX(),
                                                                                         event.getY())));
        palette.onItemDragStart(event -> canvasShapeDragStartEvent.fire(new CanvasShapeDragStartEvent(canvasHandler,
                                                                                                      event.getDefinition(),
                                                                                                      event.getFactory(),
                                                                                                      event.getX(),
                                                                                                      event.getY())));
        palette.onItemDragUpdate(event -> canvasShapeDragUpdateEvent.fire(new CanvasShapeDragUpdateEvent(canvasHandler,
                                                                                                         event.getDefinition(),
                                                                                                         event.getFactory(),
                                                                                                         event.getX(),
                                                                                                         event.getY())));
        return palette;
    }
}
