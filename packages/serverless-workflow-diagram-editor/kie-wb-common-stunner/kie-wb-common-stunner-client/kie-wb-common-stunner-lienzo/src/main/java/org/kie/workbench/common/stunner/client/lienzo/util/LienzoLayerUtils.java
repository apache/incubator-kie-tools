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

package org.kie.workbench.common.stunner.client.lienzo.util;

import com.ait.lienzo.client.core.shape.Shape;
import com.ait.lienzo.client.core.types.BoundingBox;
import com.ait.lienzo.client.core.util.ScratchPad;
import com.ait.lienzo.client.widget.panel.Bounds;
import com.ait.lienzo.client.widget.panel.impl.BoundsProviderFactory;
import com.ait.lienzo.shared.core.types.DataURLType;
import org.kie.workbench.common.stunner.client.lienzo.canvas.LienzoLayer;
import org.kie.workbench.common.stunner.client.lienzo.canvas.wires.WiresUtils;

public class LienzoLayerUtils {

    public static String layerToDataURL(final LienzoLayer lienzoLayer,
                                        final DataURLType dataURLType,
                                        final int x,
                                        final int y,
                                        final int width,
                                        final int height,
                                        final String bgColor) {
        final com.ait.lienzo.client.core.shape.Layer layer = lienzoLayer.getLienzoLayer();
        final ScratchPad scratchPad = layer.getScratchPad();
        scratchPad.setPixelSize(width, height);
        if (null != bgColor) {
            scratchPad.getContext().setFillColor(bgColor);
            scratchPad.getContext().fillRect(0,
                                             0,
                                             width,
                                             height);
        }
        layer.drawWithTransforms(scratchPad.getContext(),
                                 1,
                                 BoundingBox.fromDoubles(x,
                                                         y,
                                                         width,
                                                         height));
        final String data = scratchPad.toDataURL(dataURLType,
                                                 1);
        scratchPad.clear();
        return data;
    }

    private static final BoundsProviderFactory.WiresBoundsProvider WIRES_BOUNDS_PROVIDER = new BoundsProviderFactory.WiresBoundsProvider();

    public static Bounds computeBounds(final LienzoLayer layer) {
        return WIRES_BOUNDS_PROVIDER.get(layer.getLienzoLayer());
    }

    public static String getUUID_At(final LienzoLayer lienzoLayer,
                                    final double x,
                                    final double y) {
        int sx = (int) x;
        int sy = (int) y;
        final Shape<?> shape = lienzoLayer.getLienzoLayer().getLayer().findShapeAtPoint(sx,
                                                                                        sy);
        final String viewUUID = getShapeUUID(shape);
        return viewUUID;
    }

    private static String getShapeUUID(final Shape<?> lienzoShape) {
        if (null != lienzoShape) {
            if (hasUUID(lienzoShape)) {
                return getNodeViewUUID(lienzoShape);
            }
            com.ait.lienzo.client.core.shape.Node<?> parent = lienzoShape.getParent();
            while (null != parent && !hasUUID(parent)) {
                parent = parent.getParent();
            }
            if (null != parent) {
                return getNodeViewUUID(parent);
            }
        }
        return null;
    }

    private static boolean hasUUID(final com.ait.lienzo.client.core.shape.Node<?> node) {
        return WiresUtils.getShapeUUID(node) != null;
    }

    private static String getNodeViewUUID(final com.ait.lienzo.client.core.shape.Node<?> node) {
        return WiresUtils.getShapeUUID(node);
    }
}
