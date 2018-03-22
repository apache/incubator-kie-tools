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

package org.kie.workbench.common.stunner.cm.client.shape.def;

import org.kie.workbench.common.stunner.bpmn.client.resources.BPMNImageResources;
import org.kie.workbench.common.stunner.bpmn.client.shape.def.BPMNShapeDef;
import org.kie.workbench.common.stunner.bpmn.definition.BPMNViewDefinition;
import org.kie.workbench.common.stunner.core.client.shape.ImageDataUriGlyph;
import org.kie.workbench.common.stunner.core.client.shape.view.ShapeView;
import org.kie.workbench.common.stunner.core.client.shape.view.handler.SizeHandler;
import org.kie.workbench.common.stunner.core.definition.shape.Glyph;

public interface CaseManagementShapeDef<W extends BPMNViewDefinition, V extends ShapeView>
        extends BPMNShapeDef<W, V> {

    ImageDataUriGlyph GLYPH_OOME_HACK = ImageDataUriGlyph.create(BPMNImageResources.INSTANCE.glyphOOMEHack().getSafeUri());

    default SizeHandler.Builder<W, V> newSizeHandlerBuilder() {
        return new SizeHandler.Builder<>();
    }

    @Override
    default Glyph getGlyph(final Class<? extends W> type,
                           final String defId) {
        return GLYPH_OOME_HACK;
    }
}
