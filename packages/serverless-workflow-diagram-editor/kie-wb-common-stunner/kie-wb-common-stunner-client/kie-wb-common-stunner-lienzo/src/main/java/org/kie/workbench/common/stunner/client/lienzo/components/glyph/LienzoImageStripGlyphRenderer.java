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

import java.util.function.BiFunction;

import com.ait.lienzo.client.core.image.Image;
import com.ait.lienzo.client.core.shape.Group;
import jakarta.enterprise.context.Dependent;
import org.kie.workbench.common.stunner.core.client.shape.ImageStripGlyph;
import org.kie.workbench.common.stunner.core.client.shape.ImageStripRegistry;

import static org.kie.workbench.common.stunner.client.lienzo.util.LienzoShapeUtils.scale;

@Dependent
public class LienzoImageStripGlyphRenderer implements LienzoGlyphRenderer<ImageStripGlyph> {

    private final BiFunction<String, Integer, Image> imageBuilder;

    public LienzoImageStripGlyphRenderer() {
        this.imageBuilder = Image::new;
    }

    LienzoImageStripGlyphRenderer(BiFunction<String, Integer, Image> imageBuilder) {
        this.imageBuilder = imageBuilder;
    }

    @Override
    public Class<ImageStripGlyph> getGlyphType() {
        return ImageStripGlyph.class;
    }

    @Override
    public Group render(final ImageStripGlyph glyph,
                        final double width,
                        final double height) {
        final Group group = new Group();
        final Image image = imageBuilder.apply(ImageStripRegistry.getName(glyph.getStripType()),
                                               glyph.getIndex());
        scaleShape(image, width, height);
        group.add(image);
        return group;
    }

    protected void scaleShape(final Image image,
                              final double width,
                              final double height) {
        final double iWidth = image.getWidth();
        final double iHeight = image.getHeight();
        if (width != iWidth || height != iHeight) {
            scale(image, width, height);
        }
    }
}
