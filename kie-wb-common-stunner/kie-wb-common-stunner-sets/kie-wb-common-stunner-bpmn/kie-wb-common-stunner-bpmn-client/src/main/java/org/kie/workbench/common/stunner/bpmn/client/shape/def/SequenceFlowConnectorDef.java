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

package org.kie.workbench.common.stunner.bpmn.client.shape.def;

import org.kie.workbench.common.stunner.bpmn.client.resources.BPMNImageResources;
import org.kie.workbench.common.stunner.bpmn.definition.SequenceFlow;
import org.kie.workbench.common.stunner.core.client.shape.SvgDataUriGlyph;
import org.kie.workbench.common.stunner.core.client.shape.view.HasTitle;
import org.kie.workbench.common.stunner.core.client.shape.view.ShapeView;
import org.kie.workbench.common.stunner.core.client.shape.view.handler.FontHandler;
import org.kie.workbench.common.stunner.core.definition.shape.Glyph;
import org.kie.workbench.common.stunner.shapes.def.ConnectorShapeDef;

public final class SequenceFlowConnectorDef
        implements BPMNShapeDef<SequenceFlow, ShapeView>,
                   ConnectorShapeDef<SequenceFlow, ShapeView> {

    @Override
    public FontHandler<SequenceFlow, ShapeView> newFontHandler() {
        return newFontHandlerBuilder()
                .positon(c -> HasTitle.Position.BOTTOM)
                .build();
    }

    @Override
    public Glyph getGlyph(final Class type,
                          final String defId) {
        return SvgDataUriGlyph.Builder.build(BPMNImageResources.INSTANCE.sequenceFlow().getSafeUri());
    }
}
