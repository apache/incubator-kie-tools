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

package org.kie.workbench.common.stunner.client.lienzo.components.glyph;

import java.util.function.BiConsumer;
import java.util.function.Consumer;

import javax.enterprise.context.ApplicationScoped;

import com.ait.lienzo.client.core.shape.Group;
import com.ait.lienzo.client.core.shape.Picture;
import com.ait.lienzo.client.core.shape.Rectangle;
import com.ait.lienzo.shared.core.types.ColorName;
import com.google.gwt.core.client.Scheduler;
import org.kie.workbench.common.stunner.client.lienzo.shape.util.LienzoPictureUtils;
import org.kie.workbench.common.stunner.core.client.shape.ImageDataUriGlyph;

import static org.kie.workbench.common.stunner.client.lienzo.util.LienzoShapeUtils.scalePicture;

@ApplicationScoped
public class LienzoPictureGlyphRenderer implements LienzoGlyphRenderer<ImageDataUriGlyph> {

    private final BiConsumer<String, Consumer<Picture>> pictureBuilder;

    public LienzoPictureGlyphRenderer() {
        this.pictureBuilder = (uri,
                               consumer) -> new Picture(uri,
                                                        consumer::accept);
    }

    LienzoPictureGlyphRenderer(final BiConsumer<String, Consumer<Picture>> pictureBuilder) {
        this.pictureBuilder = pictureBuilder;
    }

    @Override
    public Class<ImageDataUriGlyph> getGlyphType() {
        return ImageDataUriGlyph.class;
    }

    @Override
    public Group render(final ImageDataUriGlyph glyph,
                        final double width,
                        final double height) {
        return this.render(glyph.getUri().asString(),
                           width,
                           height);
    }

    public Group render(final String data,
                        final double width,
                        final double height) {
        final DestroyableGroup group = new DestroyableGroup();
        final Rectangle decorator =
                new Rectangle(width,
                              height)
                        .setCornerRadius(5)
                        .setFillColor(ColorName.LIGHTGREY)
                        .setFillAlpha(0.7d);
        pictureBuilder.accept(data,
                              picture -> {
                                  group.picture = picture;
                                  scalePicture(picture,
                                               width,
                                               height);
                                  group.remove(decorator);
                                  group.add(picture);
                              });
        group.add(decorator);
        return group;
    }

    private static class DestroyableGroup extends Group {

        private Picture picture;

        @Override
        public boolean removeFromParent() {
            LienzoPictureUtils.tryDestroy(picture,
                                          (p) -> Scheduler.get()
                                                  .scheduleFixedDelay(() -> !LienzoPictureUtils.retryDestroy(p),
                                                                      200));
            return super.removeFromParent();
        }
    }
}
