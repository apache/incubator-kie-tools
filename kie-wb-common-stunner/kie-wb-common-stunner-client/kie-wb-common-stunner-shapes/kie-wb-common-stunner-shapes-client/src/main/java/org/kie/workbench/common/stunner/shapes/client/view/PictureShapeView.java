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
package org.kie.workbench.common.stunner.shapes.client.view;

import com.ait.lienzo.client.core.shape.MultiPath;
import com.ait.lienzo.client.core.shape.Picture;
import com.google.gwt.core.client.Scheduler;
import org.kie.workbench.common.stunner.client.lienzo.shape.util.LienzoPictureUtils;
import org.kie.workbench.common.stunner.client.lienzo.shape.view.wires.WiresContainerShapeView;
import org.kie.workbench.common.stunner.core.client.shape.view.event.ShapeViewSupportedEvents;

import static org.kie.workbench.common.stunner.client.lienzo.util.LienzoShapeUtils.scalePicture;

/**
 * The lienzo view implementation for the Picture shape.
 * Note that this view impl does not support resize.
 */
public class PictureShapeView<T extends PictureShapeView>
        extends WiresContainerShapeView<T> {

    private Picture picture;

    public PictureShapeView(final String uri,
                            final double width,
                            final double height) {
        super(ShapeViewSupportedEvents.DESKTOP_NO_RESIZE_EVENT_TYPES,
              new MultiPath()
                      .rect(0,
                            0,
                            width,
                            height)
                      .setStrokeAlpha(0)
                      .setFillAlpha(0));
        this.picture = new Picture(uri,
                                   picture -> {
                                       scalePicture(picture,
                                                    width,
                                                    height);
                                       addChild(picture);
                                       refresh();
                                   });
        super.setResizable(false);
    }

    public PictureShapeView(final MultiPath path) {
        super(ShapeViewSupportedEvents.DESKTOP_NO_RESIZE_EVENT_TYPES,
              path);
    }

    @Override
    public void destroy() {
        super.destroy();
        LienzoPictureUtils.tryDestroy(getPicture(),
                                      (p) -> Scheduler.get().scheduleFixedDelay(() -> !LienzoPictureUtils.retryDestroy(p),
                                                                                200));
    }

    //package-protected method to support overriding Picture in Unit Tests
    Picture getPicture() {
        return this.picture;
    }
}
