/*
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 * 
 *  http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied.  See the License for the
 * specific language governing permissions and limitations
 * under the License. 
 */


package org.kie.workbench.common.stunner.client.lienzo.components.glyph;

import java.util.function.BiConsumer;
import java.util.function.Consumer;

import javax.enterprise.context.Dependent;
import javax.inject.Inject;

import com.ait.lienzo.client.core.shape.Picture;
import org.kie.workbench.common.stunner.core.client.shape.SvgDataUriGlyph;
import org.kie.workbench.common.stunner.core.client.util.SvgDataUriGenerator;

@Dependent
public class LienzoSvgDataUriGlyphRenderer extends AbstractLienzoShapeGlyphRenderer<SvgDataUriGlyph, Picture> {

    private final SvgDataUriGenerator svgDataUriUtil;
    private final BiConsumer<String, Consumer<Picture>> pictureBuilder;

    protected LienzoSvgDataUriGlyphRenderer() {
        this(null,
             null);
    }

    @Inject
    public LienzoSvgDataUriGlyphRenderer(final SvgDataUriGenerator svgDataUriUtil) {
        this(svgDataUriUtil,
             (uri, consumer) -> new Picture(uri,
                                            consumer::accept));
    }

    LienzoSvgDataUriGlyphRenderer(final SvgDataUriGenerator svgDataUriUtil,
                                  final BiConsumer<String, Consumer<Picture>> pictureBuilder) {
        this.svgDataUriUtil = svgDataUriUtil;
        this.pictureBuilder = pictureBuilder;
    }

    @Override
    public Class<SvgDataUriGlyph> getGlyphType() {
        return SvgDataUriGlyph.class;
    }

    @Override
    protected void getShape(final SvgDataUriGlyph glyph,
                            final double width,
                            final double height,
                            final Consumer<Picture> shapeConsumer) {
        final String content = svgDataUriUtil
                .generate(glyph.getSvg(),
                          glyph.getDefs(),
                          glyph.getValidUseRefIds());
        final String encoded = SvgDataUriGenerator.encodeBase64(content);
        pictureBuilder.accept(encoded,
                              shapeConsumer::accept);
    }
}
