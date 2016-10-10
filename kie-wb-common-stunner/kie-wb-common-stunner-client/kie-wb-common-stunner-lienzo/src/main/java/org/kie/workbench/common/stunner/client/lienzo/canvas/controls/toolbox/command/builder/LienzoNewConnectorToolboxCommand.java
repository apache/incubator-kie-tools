/*
 * Copyright 2016 Red Hat, Inc. and/or its affiliates.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * 	http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.kie.workbench.common.stunner.client.lienzo.canvas.controls.toolbox.command.builder;

import com.ait.lienzo.client.core.shape.Shape;
import org.kie.workbench.common.stunner.core.client.ShapeManager;
import org.kie.workbench.common.stunner.core.client.api.ClientDefinitionManager;
import org.kie.workbench.common.stunner.core.client.canvas.AbstractCanvasHandler;
import org.kie.workbench.common.stunner.core.client.canvas.controls.builder.EdgeBuilderControl;
import org.kie.workbench.common.stunner.core.client.canvas.controls.toolbox.command.builder.NewConnectorCommand;
import org.kie.workbench.common.stunner.core.client.components.drag.ConnectorDragProxy;
import org.kie.workbench.common.stunner.core.client.components.glyph.DefinitionGlyphTooltip;
import org.kie.workbench.common.stunner.core.client.service.ClientFactoryServices;
import org.kie.workbench.common.stunner.core.graph.processing.index.bounds.GraphBoundsIndexer;

import javax.annotation.PostConstruct;
import javax.enterprise.context.Dependent;
import javax.inject.Inject;

@Dependent
public class LienzoNewConnectorToolboxCommand extends NewConnectorCommand<Shape<?>> {

    protected LienzoNewConnectorToolboxCommand() {
        this( null, null, null, null, null, null, null );
    }

    @Inject
    public LienzoNewConnectorToolboxCommand( final ClientDefinitionManager clientDefinitionManager,
                                             final ClientFactoryServices clientFactoryServices,
                                             final ShapeManager shapeManager,
                                             final DefinitionGlyphTooltip<?> glyphTooltip,
                                             final GraphBoundsIndexer graphBoundsIndexer,
                                             final ConnectorDragProxy<AbstractCanvasHandler> connectorDragProxyFactory,
                                             final EdgeBuilderControl<AbstractCanvasHandler> edgeBuilderControl ) {
        super( clientDefinitionManager, clientFactoryServices, shapeManager, glyphTooltip,
                graphBoundsIndexer, connectorDragProxyFactory, edgeBuilderControl );
    }

    @PostConstruct
    public void init() {
        glyphTooltip.setPrefix( "Create a new " );
    }

}
