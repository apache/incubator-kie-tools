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

package org.kie.workbench.common.stunner.client.lienzo.util;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;

import com.ait.lienzo.client.core.shape.Group;
import com.ait.lienzo.client.core.shape.Layer;
import com.ait.lienzo.client.widget.LienzoPanel;
import org.kie.workbench.common.stunner.client.lienzo.components.glyph.LienzoGlyphRenderer;
import org.kie.workbench.common.stunner.client.lienzo.components.glyph.LienzoGlyphRenderers;
import org.kie.workbench.common.stunner.core.definition.shape.Glyph;

@ApplicationScoped
public class LienzoPanelUtils {

    private final LienzoGlyphRenderer<Glyph> glyphLienzoGlyphRenderer;

    @Inject
    public LienzoPanelUtils(final LienzoGlyphRenderers glyphLienzoGlyphRenderer) {
        this.glyphLienzoGlyphRenderer = glyphLienzoGlyphRenderer;
    }

    public LienzoPanel newPanel(final Glyph glyph,
                                final int width,
                                final int height) {
        final Group glyphGroup = glyphLienzoGlyphRenderer.render(glyph,
                                                                 width,
                                                                 height);
        final com.ait.lienzo.client.widget.LienzoPanel panel = new LienzoPanel(width,
                                                                               height);
        final Layer layer = new Layer();
        panel.add(layer.setTransformable(true));
        layer.add(glyphGroup);
        return panel;
    }
}
