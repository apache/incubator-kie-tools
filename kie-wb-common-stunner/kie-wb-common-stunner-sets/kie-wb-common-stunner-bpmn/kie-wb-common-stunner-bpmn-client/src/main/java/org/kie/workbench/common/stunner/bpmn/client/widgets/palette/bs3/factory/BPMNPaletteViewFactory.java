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

package org.kie.workbench.common.stunner.bpmn.client.widgets.palette.bs3.factory;

import java.util.HashMap;
import java.util.Map;

import javax.enterprise.context.ApplicationScoped;

import org.kie.workbench.common.stunner.bpmn.BPMNDefinitionSet;
import org.kie.workbench.common.stunner.bpmn.client.resources.BPMNImageResources;
import org.kie.workbench.common.stunner.bpmn.definition.Categories;
import org.kie.workbench.common.stunner.client.widgets.palette.factory.AbstractBS3PaletteViewFactory;
import org.kie.workbench.common.stunner.core.client.shape.SvgDataUriGlyph;
import org.kie.workbench.common.stunner.core.definition.shape.Glyph;

@ApplicationScoped
public class BPMNPaletteViewFactory extends AbstractBS3PaletteViewFactory {

    private static final Map<String, Glyph> CATEGORY_GLYPHS = new HashMap<String, Glyph>(5) {{
        put(Categories.ACTIVITIES,
            SvgDataUriGlyph.Builder.build(BPMNImageResources.INSTANCE.categoryActivity().getSafeUri()));
        put(Categories.CONTAINERS,
            SvgDataUriGlyph.Builder.build((BPMNImageResources.INSTANCE.categoryContainer().getSafeUri())));
        put(Categories.GATEWAYS,
            SvgDataUriGlyph.Builder.build((BPMNImageResources.INSTANCE.categoryGateway().getSafeUri())));
        put(Categories.EVENTS,
            SvgDataUriGlyph.Builder.build(BPMNImageResources.INSTANCE.cagetoryEvents().getSafeUri()));
        put(Categories.CONNECTING_OBJECTS,
            SvgDataUriGlyph.Builder.build((BPMNImageResources.INSTANCE.categorySequence().getSafeUri())));
    }};

    @Override
    protected Class<?> getDefinitionSetType() {
        return BPMNDefinitionSet.class;
    }

    @Override
    protected Map<String, Glyph> getCategoryGlyphs() {
        return CATEGORY_GLYPHS;
    }
}
