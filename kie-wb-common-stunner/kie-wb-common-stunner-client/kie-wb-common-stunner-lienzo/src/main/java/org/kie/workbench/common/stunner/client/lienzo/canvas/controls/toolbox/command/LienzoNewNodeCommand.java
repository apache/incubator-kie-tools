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

package org.kie.workbench.common.stunner.client.lienzo.canvas.controls.toolbox.command;

import javax.annotation.PostConstruct;
import javax.enterprise.context.Dependent;
import javax.enterprise.event.Event;
import javax.inject.Inject;

import com.ait.lienzo.client.core.shape.Group;
import org.kie.workbench.common.stunner.client.lienzo.components.glyph.LienzoGlyphRenderer;
import org.kie.workbench.common.stunner.client.lienzo.components.glyph.LienzoGlyphRenderers;
import org.kie.workbench.common.stunner.core.client.api.ShapeManager;
import org.kie.workbench.common.stunner.core.client.canvas.AbstractCanvasHandler;
import org.kie.workbench.common.stunner.core.client.canvas.controls.builder.NodeBuilderControl;
import org.kie.workbench.common.stunner.core.client.canvas.controls.toolbox.command.builder.NewNodeCommand;
import org.kie.workbench.common.stunner.core.client.canvas.event.selection.CanvasElementSelectedEvent;
import org.kie.workbench.common.stunner.core.client.canvas.util.CanvasLayoutUtils;
import org.kie.workbench.common.stunner.core.client.components.drag.NodeDragProxy;
import org.kie.workbench.common.stunner.core.client.components.views.CanvasDefinitionTooltip;
import org.kie.workbench.common.stunner.core.client.service.ClientFactoryService;
import org.kie.workbench.common.stunner.core.definition.shape.Glyph;
import org.kie.workbench.common.stunner.core.graph.processing.index.bounds.GraphBoundsIndexer;
import org.kie.workbench.common.stunner.core.util.DefinitionUtils;

@Dependent
public class LienzoNewNodeCommand extends NewNodeCommand<Group> {

    private final LienzoGlyphRenderer<Glyph> glyphRenderer;

    @Inject
    public LienzoNewNodeCommand(final ClientFactoryService clientFactoryServices,
                                final ShapeManager shapeManager,
                                final CanvasDefinitionTooltip definitionTooltip,
                                final GraphBoundsIndexer graphBoundsIndexer,
                                final NodeDragProxy<AbstractCanvasHandler> nodeDragProxyFactory,
                                final NodeBuilderControl<AbstractCanvasHandler> nodeBuilderControl,
                                final DefinitionUtils definitionUtils,
                                final CanvasLayoutUtils canvasLayoutUtils,
                                final Event<CanvasElementSelectedEvent> elementSelectedEvent,
                                final LienzoGlyphRenderers glyphRenderer) {
        super(clientFactoryServices,
              shapeManager,
              definitionTooltip,
              graphBoundsIndexer,
              nodeDragProxyFactory,
              nodeBuilderControl,
              definitionUtils,
              canvasLayoutUtils,
              elementSelectedEvent);
        this.glyphRenderer = glyphRenderer;
    }

    @PostConstruct
    public void init() {
        getDefinitionTooltip().setPrefix("Click to create a ");
    }

    @Override
    protected Group getGlyphIcon(final Glyph glyph,
                                 final double width,
                                 final double height) {
        return glyphRenderer.render(glyph,
                                    width,
                                    height);
    }
}
