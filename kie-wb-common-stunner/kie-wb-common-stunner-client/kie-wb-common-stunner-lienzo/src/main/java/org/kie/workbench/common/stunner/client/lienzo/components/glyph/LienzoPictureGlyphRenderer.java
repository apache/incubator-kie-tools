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

import javax.enterprise.context.Dependent;

import com.ait.lienzo.client.core.image.PictureLoadedHandler;
import com.ait.lienzo.client.core.shape.Group;
import com.ait.lienzo.client.core.shape.Picture;
import com.ait.lienzo.client.core.shape.Rectangle;
import com.ait.lienzo.shared.core.types.ColorName;
import org.kie.workbench.common.stunner.core.client.shape.ImageDataUriGlyph;

import static org.kie.workbench.common.stunner.client.lienzo.util.LienzoShapeUtils.scalePicture;

@Dependent
public class LienzoPictureGlyphRenderer implements LienzoGlyphRenderer<ImageDataUriGlyph> {

    private final BiConsumer<String, Consumer<Picture>> pictureBuilder;

    public LienzoPictureGlyphRenderer() {
        this.pictureBuilder = (uri,
                               consumer) -> new DestroyablePicture(uri,
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
        final DestroyablePictureGroup group = new DestroyablePictureGroup();
        final Rectangle decorator =
                new Rectangle(width,
                              height)
                        .setCornerRadius(5)
                        .setFillColor(ColorName.LIGHTGREY)
                        .setFillAlpha(0.7d);
        pictureBuilder.accept(data,
                              picture -> {
                                  group.forPicture(picture);
                                  scalePicture(picture,
                                               width,
                                               height);
                                  group.remove(decorator);
                                  group.add(picture);
                              });
        group.add(decorator);
        return group;
    }

    public static class DestroyablePictureGroup extends Group {

        private Picture picture;

        public void forPicture(final Picture picture) {
            this.picture = picture;
        }

        @Override
        public boolean removeFromParent() {
            if (null != picture) {
                if (picture instanceof DestroyablePicture) {
                    ((DestroyablePicture) picture).destroy();
                }
                picture = null;
            }
            return super.removeFromParent();
        }
    }

    public static class DestroyablePicture extends Picture {

        public DestroyablePicture(final String url,
                                  final PictureLoadedHandler loadedHandler) {
            super(url, loadedHandler);
        }

        public void destroy() {
            getImageProxy().destroy();
            removeFromParent();
        }
    }
}
