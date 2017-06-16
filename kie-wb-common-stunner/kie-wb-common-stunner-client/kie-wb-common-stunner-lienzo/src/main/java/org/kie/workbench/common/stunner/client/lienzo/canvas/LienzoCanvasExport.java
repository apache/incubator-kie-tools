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

package org.kie.workbench.common.stunner.client.lienzo.canvas;

import javax.enterprise.context.ApplicationScoped;

import com.ait.lienzo.client.core.types.BoundingBox;
import com.ait.lienzo.client.core.util.ScratchPad;
import com.ait.lienzo.shared.core.types.DataURLType;
import org.kie.workbench.common.stunner.core.client.canvas.AbstractCanvasHandler;
import org.kie.workbench.common.stunner.core.client.canvas.CanvasExport;
import org.kie.workbench.common.stunner.core.client.canvas.Layer;

@ApplicationScoped
public class LienzoCanvasExport implements CanvasExport<AbstractCanvasHandler> {

    static final String BG_COLOR = "#FFFFFF";

    @Override
    public String toImageData(final AbstractCanvasHandler canvasHandler,
                              final Layer.URLDataType urlDataType) {
        final LienzoLayer layer = getLayer(canvasHandler);
        final com.ait.lienzo.client.core.shape.Layer lienzoLayer = layer.getLienzoLayer();
        return layerToDataURL(lienzoLayer,
                              getDataType(urlDataType),
                              0,
                              0,
                              lienzoLayer.getWidth(),
                              lienzoLayer.getHeight(),
                              BG_COLOR);
    }

    @Override
    public String toImageData(final AbstractCanvasHandler canvasHandler,
                              final Layer.URLDataType urlDataType,
                              final int x,
                              final int y,
                              final int width,
                              final int height) {
        final LienzoLayer layer = getLayer(canvasHandler);
        final com.ait.lienzo.client.core.shape.Layer lienzoLayer = layer.getLienzoLayer();

        return layerToDataURL(lienzoLayer,
                              getDataType(urlDataType),
                              x,
                              y,
                              width,
                              height,
                              BG_COLOR);
    }

    private static String layerToDataURL(final com.ait.lienzo.client.core.shape.Layer layer,
                                         final DataURLType dataURLType,
                                         final int x,
                                         final int y,
                                         final int width,
                                         final int height,
                                         final String bgColor) {
        ScratchPad scratchPad = layer.getScratchPad();
        if (null != bgColor) {
            scratchPad.getContext().setFillColor(bgColor);
            scratchPad.getContext().fillRect(x,
                                             y,
                                             width,
                                             height);
        }
        layer.drawWithTransforms(scratchPad.getContext(),
                                 1,
                                 new BoundingBox(x,
                                                 y,
                                                 width,
                                                 height));
        final String data = scratchPad.toDataURL(dataURLType,
                                                 1);
        scratchPad.clear();
        return data;
    }

    private static DataURLType getDataType(final org.kie.workbench.common.stunner.core.client.canvas.Layer.URLDataType type) {
        switch (type) {
            case JPG:
                return DataURLType.JPG;
            case PNG:
                return DataURLType.PNG;
        }
        throw new UnsupportedOperationException("Export data type [" + type.name() + "] not supported ");
    }

    private static LienzoLayer getLayer(final AbstractCanvasHandler canvasHandler) {
        return (LienzoLayer) canvasHandler.getCanvas().getLayer();
    }
}
