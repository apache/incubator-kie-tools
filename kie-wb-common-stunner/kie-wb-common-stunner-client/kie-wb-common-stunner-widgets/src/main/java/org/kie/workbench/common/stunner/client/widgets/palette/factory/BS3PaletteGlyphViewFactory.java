/*
 * Copyright 2016 Red Hat, Inc. and/or its affiliates.
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

import java.util.HashSet;
import java.util.Set;

import com.google.gwt.user.client.ui.IsWidget;
import com.google.gwt.validation.client.impl.Group;
import org.kie.workbench.common.stunner.client.lienzo.util.LienzoPanelUtils;
import org.kie.workbench.common.stunner.client.widgets.palette.factory.icons.IconRenderer;
import org.kie.workbench.common.stunner.client.widgets.palette.factory.icons.IconResource;
import org.kie.workbench.common.stunner.client.widgets.palette.factory.icons.PaletteIconSettings;
import org.kie.workbench.common.stunner.client.widgets.palette.factory.icons.lienzo.LienzoPanelIconRenderer;
import org.kie.workbench.common.stunner.core.client.api.ShapeManager;
import org.kie.workbench.common.stunner.core.client.shape.view.glyph.Glyph;

class BS3PaletteGlyphViewFactory implements BS3PaletteViewFactory {

    public static final int DEFAULT_DEFINITION_GLYPH_SIZE = 15;

    private final ShapeManager shapeManager;

    private final Set<Glyph<Group>> glyphs = new HashSet<>();

    BS3PaletteGlyphViewFactory(final ShapeManager shapeManager) {
        this.shapeManager = shapeManager;
    }

    @Override
    public boolean accepts(final String id) {
        return true;
    }

    @Override
    public PaletteIconSettings<? extends IconRenderer, ? extends IconResource> getCategoryIconSettings(String categoryId) {
        return null;
    }

    @Override
    @SuppressWarnings("unchecked")
    public PaletteIconSettings getDefinitionIconSettings(String defSetId,
                                                         String itemId) {
        IsWidget panel = getDefinitionView(defSetId,
                                           itemId);

        return new PaletteIconSettings(LienzoPanelIconRenderer.class,
                                       new IconResource<>(panel));
    }

    @Override
    public void destroy() {
        glyphs.forEach(Glyph::destroy);
        glyphs.clear();
    }

    protected IsWidget getDefinitionView(final String defSetId,
                                         final String defId) {
        final Glyph<Group> glyph = getGlyph(defSetId,
                                            defId,
                                            DEFAULT_DEFINITION_GLYPH_SIZE,
                                            DEFAULT_DEFINITION_GLYPH_SIZE);
        return LienzoPanelUtils.newPanel(glyph,
                                         DEFAULT_DEFINITION_GLYPH_SIZE,
                                         DEFAULT_DEFINITION_GLYPH_SIZE);
    }

    @SuppressWarnings("unchecked")
    private Glyph<Group> getGlyph(final String defSetId,
                                  final String id,
                                  final int width,
                                  final int height) {
        final Glyph<Group> glyph = shapeManager.getDefaultShapeSet(defSetId).getShapeFactory().glyph(id,
                                                                                                     width,
                                                                                                     height);
        glyphs.add(glyph);
        return glyph;
    }
}
