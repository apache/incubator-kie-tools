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

package org.kie.workbench.common.stunner.bpmn.client.shape.def;

import org.kie.workbench.common.stunner.bpmn.client.resources.BPMNGlyphFactory;
import org.kie.workbench.common.stunner.bpmn.definition.Association;
import org.kie.workbench.common.stunner.core.client.shape.common.DashArray;
import org.kie.workbench.common.stunner.core.client.shape.view.ShapeView;
import org.kie.workbench.common.stunner.core.client.shape.view.handler.FontHandler;
import org.kie.workbench.common.stunner.core.definition.shape.Glyph;
import org.kie.workbench.common.stunner.shapes.def.ConnectorShapeDef;

public class AssociationConnectorDef
        implements BPMNShapeDef<Association, ShapeView>,
                   ConnectorShapeDef<Association, ShapeView> {

    private static final DashArray DASH_ARRAY = DashArray.create(2, 6);

    @Override
    public FontHandler<Association, ShapeView> newFontHandler() {
        return newFontHandlerBuilder()
                .fontFamily(c -> FONT_FAMILY)
                .fontSize(c -> FONT_SIZE)
                .fontColor(c -> FONT_COLOR)
                .strokeColor(c -> FONT_STROKE_COLOR)
                .strokeSize(c -> STROKE_SIZE)
                .build();
    }

    @Override
    public Glyph getGlyph(final Class type,
                          final String defId) {
        return BPMNGlyphFactory.ASSOCIATION;
    }

    @Override
    public DashArray getDashArray(final Association element) {
        return DASH_ARRAY;
    }
}