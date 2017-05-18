/*
 * Copyright 2016 Red Hat, Inc. and/or its affiliates.
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

package org.kie.workbench.common.stunner.client.lienzo.util;

import com.ait.lienzo.client.core.shape.IPrimitive;
import com.ait.lienzo.client.core.shape.Picture;
import com.ait.lienzo.client.core.shape.wires.MagnetManager;
import com.ait.lienzo.client.core.shape.wires.WiresLayoutContainer;
import com.ait.lienzo.client.core.shape.wires.WiresShape;
import com.ait.lienzo.client.core.types.BoundingBox;
import com.ait.lienzo.client.core.types.LinearGradient;
import org.kie.workbench.common.stunner.core.client.shape.HasChildren;
import org.kie.workbench.common.stunner.core.client.shape.Shape;
import org.kie.workbench.common.stunner.core.client.shape.view.ShapeView;
import org.kie.workbench.common.stunner.core.client.util.ShapeUtils;
import org.kie.workbench.common.stunner.core.graph.content.view.Magnet;
import org.kie.workbench.common.stunner.core.graph.content.view.MagnetImpl;

public class LienzoShapeUtils {

    public static final int DEFAULT_SOURCE_MAGNET = 3;
    public static final int DEFAULT_TARGET_MAGNET = 7;


    public static void scalePicture(final Picture picture,
                                    final double width,
                                    final double height) {
        final BoundingBox bb = picture.getBoundingBox();
        final double[] scale = LienzoUtils.getScaleFactor(bb.getWidth(),
                                                          bb.getHeight(),
                                                          width,
                                                          height);
        picture.setScale(scale[0],
                         scale[1]);
    }

    public static WiresLayoutContainer.Layout getWiresLayout(final HasChildren.Layout layout) {
        switch (layout) {
            case CENTER:
                return WiresLayoutContainer.Layout.CENTER;
            case LEFT:
                return WiresLayoutContainer.Layout.LEFT;
            case RIGHT:
                return WiresLayoutContainer.Layout.RIGHT;
            case TOP:
                return WiresLayoutContainer.Layout.TOP;
            case BOTTOM:
                return WiresLayoutContainer.Layout.BOTTOM;
        }
        throw new UnsupportedOperationException("Unsupported layout [" + layout.name() + "]");
    }

    public static LinearGradient getLinearGradient(final String startColor,
                                                   final String endColor,
                                                   final Double width,
                                                   final Double height) {
        final LinearGradient linearGradient = new LinearGradient(0,
                                                                 width,
                                                                 0,
                                                                 -height / 2);
        linearGradient.addColorStop(1,
                                    endColor);
        linearGradient.addColorStop(0,
                                    startColor);
        return linearGradient;
    }

    /**
     * Obtain the magnet indexes on the source and target shapes.
     * The resulting index values are on the min distance between the magnets, which is calculated
     * by basic trigonometry on the cartesian coordinates area.
     * To avoid using the magnets on shape's corners, only some concrete magnets will be allowed as result. As
     * arc's and line's magnet points are based cartesian cardinality and it is quite similar
     * on both cases ( see https://www.lienzo-core.com/lienzo-ks/#CARDINAL_INTERSECT ),
     * this implementation will consider only the odd magnet index values as results.
     * TODO: This is enough for the current line/arc support in stunner's shapes, but consider
     * improving this behavior on the future.
     */
    public static Magnet[] getDefaultMagnets(final WiresShape sourceShape,
                                             final WiresShape targetShape) {
        final MagnetManager.Magnets sourceMagnets = sourceShape.getMagnets();
        final MagnetManager.Magnets targetMagnets = targetShape.getMagnets();
        int iSourceMagnet = 0;
        int iTargetMagnet = 0;
        double dist = Double.MAX_VALUE;
        for (int x = 0; x < sourceMagnets.size(); x++) {
            if (isOddNumber(x)) {
                final IPrimitive<?> sourceControl = sourceMagnets.getMagnet(x).getControl();
                final double sX = sourceControl.getX();
                final double sY = sourceControl.getY();
                for (int y = 0; y < targetMagnets.size(); y++) {
                    if (isOddNumber(y)) {
                        final IPrimitive<?> targetControl = targetMagnets.getMagnet(y).getControl();
                        final double tX = targetControl.getX();
                        final double tY = targetControl.getY();
                        final double _d = ShapeUtils.dist(sX,
                                                          sY,
                                                          tX,
                                                          tY);
                        if (_d < dist) {
                            dist = _d;
                            iSourceMagnet = x;
                            iTargetMagnet = y;
                        }
                    }
                }
            }
        }
        Magnet sMagnet = MagnetImpl.Builder.build(sourceMagnets.getMagnet(iSourceMagnet).getX(),
                                                  sourceMagnets.getMagnet(iSourceMagnet).getY());
        Magnet tMagnet = MagnetImpl.Builder.build(targetMagnets.getMagnet(iTargetMagnet).getX(),
                                                  targetMagnets.getMagnet(iTargetMagnet).getY());
        return new Magnet[]{sMagnet, tMagnet};
    }

    public static Magnet[] getDefaultMagnets(final Shape sourceShape,
                                             final Shape targetShape) {
        final ShapeView<?> sourceView = sourceShape.getShapeView();
        final ShapeView<?> targetView = targetShape.getShapeView();
        if (sourceView instanceof WiresShape && targetView instanceof WiresShape) {
            return getDefaultMagnets((WiresShape) sourceView,
                                     (WiresShape) targetView);
        }
        return new Magnet[]{MagnetImpl.Builder.build(Magnet.MagnetType.OUTGOING), MagnetImpl.Builder.build(Magnet.MagnetType.INCOMING)};
    }

    private static boolean isOddNumber(final int i) {
        return i % 2 > 0;
    }
}
