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
import javax.inject.Inject;

import com.ait.lienzo.client.core.shape.Group;
import org.kie.workbench.common.stunner.client.lienzo.components.glyph.LienzoGlyphRenderer;
import org.kie.workbench.common.stunner.client.lienzo.components.glyph.LienzoGlyphRenderers;
import org.kie.workbench.common.stunner.core.client.api.ShapeManager;
import org.kie.workbench.common.stunner.core.client.canvas.AbstractCanvasHandler;
import org.kie.workbench.common.stunner.core.client.canvas.controls.builder.EdgeBuilderControl;
import org.kie.workbench.common.stunner.core.client.canvas.controls.toolbox.command.builder.NewConnectorCommand;
import org.kie.workbench.common.stunner.core.client.components.drag.ConnectorDragProxy;
import org.kie.workbench.common.stunner.core.client.components.views.CanvasDefinitionTooltip;
import org.kie.workbench.common.stunner.core.client.service.ClientFactoryService;
import org.kie.workbench.common.stunner.core.definition.shape.Glyph;
import org.kie.workbench.common.stunner.core.graph.processing.index.bounds.GraphBoundsIndexer;

@Dependent
public class LienzoNewConnectorCommand extends NewConnectorCommand<Group> {

    private final LienzoGlyphRenderer<Glyph> glyphLienzoGlyphRenderer;

    @Inject
    public LienzoNewConnectorCommand(final ClientFactoryService clientFactoryServices,
                                     final ShapeManager shapeManager,
                                     final CanvasDefinitionTooltip definitionTooltip,
                                     final GraphBoundsIndexer graphBoundsIndexer,
                                     final ConnectorDragProxy<AbstractCanvasHandler> connectorDragProxyFactory,
                                     final EdgeBuilderControl<AbstractCanvasHandler> edgeBuilderControl,
                                     final LienzoGlyphRenderers glyphLienzoGlyphRenderer) {
        super(clientFactoryServices,
              shapeManager,
              definitionTooltip,
              graphBoundsIndexer,
              connectorDragProxyFactory,
              edgeBuilderControl);
        this.glyphLienzoGlyphRenderer = glyphLienzoGlyphRenderer;
    }

    @PostConstruct
    public void init() {
        getDefinitionTooltip().setPrefix("Click and move to connect using a ");
    }

    @Override
    protected Group getGlyphIcon(final Glyph glyph,
                                 final double width,
                                 final double height) {
        return glyphLienzoGlyphRenderer.render(glyph,
                                               width,
                                               height);
    }
}
