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

package org.kie.workbench.common.stunner.client.lienzo.components.palette.view.element;

import com.ait.lienzo.client.core.shape.Group;
import com.ait.lienzo.client.core.shape.IPrimitive;
import com.ait.lienzo.client.core.shape.Text;
import org.kie.workbench.common.stunner.client.lienzo.components.glyph.LienzoGlyphRenderer;
import org.kie.workbench.common.stunner.client.lienzo.components.palette.view.LienzoPaletteView;
import org.kie.workbench.common.stunner.core.client.components.palette.ClientPaletteUtils;
import org.kie.workbench.common.stunner.core.client.components.palette.model.GlyphPaletteItem;
import org.kie.workbench.common.stunner.core.definition.shape.Glyph;

public final class LienzoGlyphPaletteItemViewImpl
        extends AbstractLienzoGlyphPaletteItemView {

    private static final String FONT_FAMILY = "Open Sans";

    private final Group view = new Group();
    private Text text;

    public LienzoGlyphPaletteItemViewImpl(final GlyphPaletteItem item,
                                          final LienzoPaletteView paletteView,
                                          final LienzoGlyphRenderer<Glyph> glyphRenderer,
                                          final Glyph glyph,
                                          final double width,
                                          final double height) {
        super(item,
              paletteView);
        init(glyphRenderer,
             glyph,
             width,
             height);
    }

    public void expand() {
        view.add(this.text);
    }

    public void collapse() {
        view.remove(this.text);
    }

    @Override
    public IPrimitive<?> getView() {
        return view;
    }

    private void init(final LienzoGlyphRenderer<Glyph> glyphRenderer,
                      final Glyph glyph,
                      final double width,
                      final double height) {
        final String title = item.getTitle();
        Group glyphGroup = glyphRenderer.render(glyph,
                                                width,
                                                height);
        final double fontSize = ClientPaletteUtils.computeFontSize(width,
                                                                   height,
                                                                   title.length());
        text = new Text(title)
                .setX(width + 10)
                .setY(width / 2)
                .setFontFamily(FONT_FAMILY)
                .setFontSize(fontSize)
                .setStrokeWidth(1);
        view.add(glyphGroup);
    }
}
