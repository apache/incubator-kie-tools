/*
 * Copyright 2018 Red Hat, Inc. and/or its affiliates.
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

package org.kie.workbench.common.stunner.client.lienzo;

import javax.annotation.PostConstruct;
import javax.enterprise.context.ApplicationScoped;

import com.ait.lienzo.client.core.config.LienzoCore;
import com.ait.lienzo.client.core.shape.wires.WiresAutoMagnetSelector;
import com.ait.lienzo.shared.core.types.ImageSelectionMode;
import org.jboss.errai.ioc.client.api.EntryPoint;
import org.kie.workbench.common.stunner.core.graph.content.Bounds;
import org.kie.workbench.common.stunner.core.graph.content.view.MagnetConnection;
import org.kie.workbench.common.stunner.core.graph.content.view.Point2D;
import org.kie.workbench.common.stunner.core.graph.content.view.View;
import org.kie.workbench.common.stunner.core.graph.util.GraphUtils;

@ApplicationScoped
@EntryPoint
public class StunnerLienzoCore {

    @PostConstruct
    public void init() {
        setDefaultImageSelectionMode();
        setAutoMagnetsDefaultBehavior();
    }

    /**
     * It's really important to set the <code>ImageSelectionMode</code> to the
     * value <code>SELECT_BOUNDS</code> due to performance reasons (image rendering on different browsers).
     * Stunner does not use image filters neither requires the image to be drawn in the
     * selection context2D, so it uses the value <code>SELECT_BOUNDS</code> as default.
     * Also it's being used due to huge differences on the resulting performance when
     * rendering the images into the contexts.
     */
    private static void setDefaultImageSelectionMode() {
        LienzoCore.get().setDefaultImageSelectionMode(ImageSelectionMode.SELECT_BOUNDS);
    }

    private static void setAutoMagnetsDefaultBehavior() {
        MagnetConnection.sourceAutoMagnet = (source, target) -> {
            double[] sourceBounds = viewBoundsToDoubles(source);
            double[] targetBounds = centerBoundsToDoubles(target);
            int magnet = WiresAutoMagnetSelector.getHeadMagnetNonOverlappedShapes(sourceBounds, targetBounds);
            if (magnet == -1) {
                //DomGlobal.console.log("HEAD IS CENTER");
            }
            return magnet;
        };
        MagnetConnection.targetAutoMagnet = (source, target) -> {
            double[] sourceBounds = centerBoundsToDoubles(source);
            double[] targetBounds = viewBoundsToDoubles(target);
            int magnet = WiresAutoMagnetSelector.getTailMagnetNonOverlappedShapes(sourceBounds, targetBounds);
            if (magnet == -1) {
                //DomGlobal.console.log("TAIL IS CENTER");
            }
            return magnet;
        };
    }

    private static double[] viewBoundsToDoubles(View shape) {
        Point2D pos = GraphUtils.getPosition(shape);
        Bounds bounds = shape.getBounds();
        double[] doubles = {pos.getX(), pos.getY(), pos.getX() + bounds.getWidth(), pos.getY() + bounds.getHeight()};
        return doubles;
    }

    private static double[] centerBoundsToDoubles(View shape) {
        Point2D pos = GraphUtils.getPosition(shape);
        Bounds bounds = shape.getBounds();
        double rw = bounds.getWidth() / 2;
        double rh = bounds.getHeight() / 2;
        double x = pos.getX() + rw;
        double y = pos.getY() + rh;
        double[] doubles = {x, y, x, y};
        return doubles;
    }
}
