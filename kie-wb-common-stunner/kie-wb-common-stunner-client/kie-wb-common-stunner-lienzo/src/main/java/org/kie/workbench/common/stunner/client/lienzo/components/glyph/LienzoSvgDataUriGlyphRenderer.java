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

package org.kie.workbench.common.stunner.client.lienzo.components.glyph;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;

import com.ait.lienzo.client.core.shape.Group;
import org.kie.workbench.common.stunner.core.client.shape.ImageDataUriGlyph;
import org.kie.workbench.common.stunner.core.client.shape.SvgDataUriGlyph;

@ApplicationScoped
public class LienzoSvgDataUriGlyphRenderer implements LienzoGlyphRenderer<SvgDataUriGlyph> {

    private final LienzoPictureGlyphRenderer pictureGlyphRenderer;

    protected LienzoSvgDataUriGlyphRenderer() {
        this(null);
    }

    @Inject
    public LienzoSvgDataUriGlyphRenderer(final LienzoPictureGlyphRenderer pictureGlyphRenderer) {
        this.pictureGlyphRenderer = pictureGlyphRenderer;
    }

    @Override
    public Class<SvgDataUriGlyph> getGlyphType() {
        return SvgDataUriGlyph.class;
    }

    @Override
    public Group render(final SvgDataUriGlyph glyph,
                        final double width,
                        final double height) {
        final ImageDataUriGlyph imageGlyph = ImageDataUriGlyph.create(glyph.getUri());
        return pictureGlyphRenderer.render(imageGlyph,
                                           width,
                                           height);
    }
}
