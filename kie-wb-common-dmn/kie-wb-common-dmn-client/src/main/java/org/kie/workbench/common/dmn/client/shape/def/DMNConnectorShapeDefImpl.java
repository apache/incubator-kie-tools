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

package org.kie.workbench.common.dmn.client.shape.def;

import java.util.HashMap;
import java.util.Map;
import java.util.function.BiConsumer;

import org.kie.workbench.common.dmn.api.definition.DMNDefinition;
import org.kie.workbench.common.dmn.api.definition.v1_1.Association;
import org.kie.workbench.common.dmn.api.definition.v1_1.AuthorityRequirement;
import org.kie.workbench.common.dmn.api.definition.v1_1.InformationRequirement;
import org.kie.workbench.common.dmn.api.definition.v1_1.KnowledgeRequirement;
import org.kie.workbench.common.dmn.client.resources.DMNSVGGlyphFactory;
import org.kie.workbench.common.dmn.client.shape.view.handlers.DMNViewHandlers;
import org.kie.workbench.common.stunner.core.definition.shape.Glyph;
import org.kie.workbench.common.stunner.core.definition.shape.ShapeGlyph;
import org.kie.workbench.common.stunner.shapes.client.view.AbstractConnectorView;

public class DMNConnectorShapeDefImpl implements DMNConnectorShapeDef<DMNDefinition, AbstractConnectorView> {

    public static final Map<Class<? extends DMNDefinition>, Glyph> GLYPHS =
            new HashMap<Class<? extends DMNDefinition>, Glyph>() {{
                put(Association.class,
                    DMNSVGGlyphFactory.ASSOCIATION_TOOLBOX);
                put(AuthorityRequirement.class,
                    DMNSVGGlyphFactory.AUTHORITY_REQUIREMENT_TOOLBOX);
                put(InformationRequirement.class,
                    DMNSVGGlyphFactory.INFORMATION_REQUIREMENT_TOOLBOX);
                put(KnowledgeRequirement.class,
                    DMNSVGGlyphFactory.KNOWLEDGE_REQUIREMENT_TOOLBOX);
            }};

    @Override
    public BiConsumer<DMNDefinition, AbstractConnectorView> viewHandler() {
        return DMNViewHandlers.CONNECTOR_ATTRIBUTES_HANDLER::handle;
    }

    @Override
    @SuppressWarnings("unchecked")
    public Glyph getGlyph(final Class type,
                          final String defId) {
        return GLYPHS.computeIfAbsent(type, (t) -> ShapeGlyph.create());
    }
}
