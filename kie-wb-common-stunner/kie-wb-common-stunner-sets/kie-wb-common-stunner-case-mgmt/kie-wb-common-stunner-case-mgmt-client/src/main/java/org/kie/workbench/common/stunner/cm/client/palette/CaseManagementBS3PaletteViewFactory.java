/*
 * Copyright 2016 Red Hat, Inc. and/or its affiliates.
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

package org.kie.workbench.common.stunner.cm.client.palette;

import java.util.HashMap;
import java.util.Map;

import javax.enterprise.context.ApplicationScoped;

import org.gwtbootstrap3.client.ui.constants.IconType;
import org.kie.workbench.common.stunner.client.widgets.components.glyph.BS3IconTypeGlyph;
import org.kie.workbench.common.stunner.client.widgets.palette.factory.AbstractBS3PaletteViewFactory;
import org.kie.workbench.common.stunner.cm.CaseManagementDefinitionSet;
import org.kie.workbench.common.stunner.core.definition.shape.Glyph;

@ApplicationScoped
public class CaseManagementBS3PaletteViewFactory extends AbstractBS3PaletteViewFactory {

    @SuppressWarnings("unchecked")
    private final static Map<String, Glyph> CATEGORY_GLYPHS = new HashMap<String, Glyph>(2) {{
        put(CaseManagementPaletteDefinitionFactory.STAGES,
            BS3IconTypeGlyph.create(IconType.STAR));
        put(CaseManagementPaletteDefinitionFactory.ACTIVITIES,
            BS3IconTypeGlyph.create(IconType.TASKS));
    }};

    @Override
    protected Class<?> getDefinitionSetType() {
        return CaseManagementDefinitionSet.class;
    }

    @Override
    protected Map<String, Glyph> getCategoryGlyphs() {
        return CATEGORY_GLYPHS;
    }
}
