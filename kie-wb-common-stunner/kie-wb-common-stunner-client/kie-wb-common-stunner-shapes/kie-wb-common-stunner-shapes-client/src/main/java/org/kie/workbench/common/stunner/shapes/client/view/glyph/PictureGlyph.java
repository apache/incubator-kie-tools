/*
 * Copyright 2016 Red Hat, Inc. and/or its affiliates.
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *  
 *     http://www.apache.org/licenses/LICENSE-2.0
 *  
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.kie.workbench.common.stunner.shapes.client.view.glyph;

import com.ait.lienzo.client.core.shape.Group;
import com.ait.lienzo.client.core.shape.Picture;
import com.ait.lienzo.client.core.shape.Rectangle;
import com.ait.lienzo.shared.core.types.ColorName;
import com.google.gwt.core.client.Scheduler;
import org.kie.workbench.common.stunner.client.lienzo.shape.view.glyph.AbstractLienzoShapeGlyph;
import org.kie.workbench.common.stunner.shapes.client.view.PictureUtils;

import static org.kie.workbench.common.stunner.client.lienzo.util.LienzoShapeUtils.scalePicture;

public final class PictureGlyph extends AbstractLienzoShapeGlyph {

    private Picture picture;

    public PictureGlyph(final String uri,
                        final double width,
                        final double height) {
        super(new Group(),
              width,
              height);
        build(uri,
              width,
              height);
    }

    @Override
    public void destroy() {
        PictureUtils.tryDestroy(getPicture(),
                                (p) -> Scheduler.get().scheduleFixedDelay(() -> !PictureUtils.retryDestroy(p),
                                                                          200));
    }

    //package-protected method to support overriding Picture in Unit Tests
    Picture getPicture() {
        return this.picture;
    }

    private void build(final String uri,
                       final double width,
                       final double height) {
        final Rectangle decorator = new Rectangle(width,
                                                  height)
                .setCornerRadius(5)
                .setFillColor(ColorName.LIGHTGREY)
                .setFillAlpha(0.2d);
        picture = new Picture(uri,
                              picture -> {
                                  scalePicture(picture,
                                               width,
                                               height);
                                  group.remove(decorator);
                                  group.add(picture);
                              });
        group.add(decorator);
    }
}
