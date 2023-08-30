/*
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 * 
 *  http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied.  See the License for the
 * specific language governing permissions and limitations
 * under the License. 
 */


package org.kie.workbench.common.stunner.bpmn.client.shape.def;

import java.util.function.Supplier;

import org.kie.workbench.common.stunner.bpmn.client.resources.BPMNGlyphFactory;
import org.kie.workbench.common.stunner.bpmn.definition.SequenceFlow;
import org.kie.workbench.common.stunner.core.client.shape.view.ShapeView;
import org.kie.workbench.common.stunner.core.client.shape.view.handler.FontHandler;
import org.kie.workbench.common.stunner.core.definition.shape.Glyph;
import org.kie.workbench.common.stunner.shapes.def.ConnectorShapeDef;

public final class SequenceFlowConnectorDef
        implements BPMNShapeDef<SequenceFlow, ShapeView>,
                   ConnectorShapeDef<SequenceFlow, ShapeView> {

    private static final String FONT_FAMILY = "Open Sans";
    private static final String FONT_COLOR = "#000000";
    private static final String FONT_STROKE_COLOR = "#393f44";
    private static final double FONT_SIZE = 10d;
    private static final double STROKE_SIZE = 0.5d;
    private final Supplier<FontHandler.Builder<SequenceFlow, ShapeView>> fontHandlerBuilder;

    public SequenceFlowConnectorDef(final Supplier<FontHandler.Builder<SequenceFlow, ShapeView>> fontHandlerBuilder) {
        this.fontHandlerBuilder = fontHandlerBuilder;
    }

    public SequenceFlowConnectorDef() {
        this.fontHandlerBuilder = () -> newFontHandlerBuilder()
                .fontFamily(c -> FONT_FAMILY)
                .fontSize(c -> FONT_SIZE)
                .fontColor(c -> FONT_COLOR)
                .strokeColor(c -> FONT_STROKE_COLOR)
                .strokeSize(c -> STROKE_SIZE);
    }

    @Override
    public FontHandler<SequenceFlow, ShapeView> newFontHandler() {
        return fontHandlerBuilder.get().build();
    }

    @Override
    public Glyph getGlyph(final Class type,
                          final String defId) {
        return BPMNGlyphFactory.SEQUENCE_FLOW;
    }
}
