/*
 * Copyright 2017 Red Hat, Inc. and/or its affiliates.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *   http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.kie.workbench.common.stunner.client.widgets.palette.factory;

import java.util.Map;

import org.kie.workbench.common.stunner.core.definition.adapter.binding.BindableAdapterUtils;
import org.kie.workbench.common.stunner.core.definition.shape.Glyph;

public abstract class AbstractBS3PaletteViewFactory implements BS3PaletteViewFactory {

    protected abstract Class<?> getDefinitionSetType();

    protected abstract Map<String, Glyph> getCategoryGlyphs();

    @Override
    public boolean accepts(final String id) {
        final String dId = getDefinitionSetId(getDefinitionSetType());
        return null != id && id.equals(dId);
    }

    @Override
    public Glyph getCategoryGlyph(String categoryId) {
        return getCategoryGlyphs().get(categoryId);
    }

    private String getDefinitionSetId(final Class<?> type) {
        return BindableAdapterUtils.getDefinitionSetId(type);
    }
}
