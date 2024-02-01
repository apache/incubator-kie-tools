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

package org.kie.workbench.common.stunner.sw.client.shapes.icons;

import com.ait.lienzo.client.core.shape.Group;
import com.ait.lienzo.client.core.shape.Layer;
import com.ait.lienzo.client.core.shape.MultiPath;
import com.ait.lienzo.client.core.shape.Rectangle;
import com.ait.lienzo.client.core.shape.toolbox.items.tooltip.PrimitiveTextTooltip;
import com.ait.lienzo.client.core.types.Point2D;
import com.ait.lienzo.tools.client.event.HandlerRegistration;
import org.kie.workbench.common.stunner.core.client.theme.StunnerTheme;
import org.kie.workbench.common.stunner.sw.client.theme.ColorTheme;

public class CornerIcon extends Group {

    private final HandlerRegistration mouseEnterHandler;
    private final HandlerRegistration mouseExitHandler;
    private final HandlerRegistration mouseClickHandler;
    PrimitiveTextTooltip tooltipElement;
    private final String tooltipText;

    private final Rectangle border = new Rectangle(20, 20)
            .setFillColor("white")
            .setFillAlpha(0.001)
            .setStrokeAlpha(0.001)
            .setStrokeColor("white")
            .setCornerRadius(9)
            .setListening(true);

    public CornerIcon(String icon, Point2D position, final String tooltip) {
        setLocation(position);
        setListening(true);
        add(border);
        this.tooltipText = tooltip;

        MultiPath clockIcon = new MultiPath(icon)
                .setScale(2)
                .setStrokeWidth(0)
                .setFillColor(((ColorTheme) StunnerTheme.getTheme()).getCornerIconFillColor())
                .setListening(false);
        add(clockIcon);

        mouseEnterHandler = border.addNodeMouseEnterHandler(event -> {
            createToolTip();
            clockIcon.setFillColor(((ColorTheme) StunnerTheme.getTheme()).getCornerIconHoverFillColor());
            border.getLayer().batch();
        });
        mouseExitHandler = border.addNodeMouseExitHandler(event -> {
            tooltipElement.destroy();
            tooltipElement = null;
            clockIcon.setFillColor(((ColorTheme) StunnerTheme.getTheme()).getCornerIconFillColor());
            border.getLayer().batch();
        });
        mouseClickHandler = border.addNodeMouseClickHandler(
                event -> {
                    this.getParent().asGroup().getChildren().get(0).fireEvent(event);
                    if (null != tooltipElement) {
                        tooltipElement.asPrimitive().moveToTop();
                    }
                }
        );
    }

    private void createToolTip() {
        tooltipElement = PrimitiveTextTooltip.Builder.build(tooltipText);
        tooltipElement.withText(t -> {
            t.setText(tooltipText);
            t.setFontSize(12);
            t.setFillColor(((ColorTheme) StunnerTheme.getTheme()).getTooltipTextColor());
        });
        final Layer topLayer = getLayer().getScene().getTopLayer();
        topLayer.add(tooltipElement.asPrimitive());
        tooltipElement.offset(CornerIcon.this::getComputedLocation);
        tooltipElement.forComputedBoundingBox(border::getBoundingBox);
        tooltipElement.show();
    }

    @Override
    public void destroy() {
        super.destroy();
        if (tooltipElement != null) {
            tooltipElement.destroy();
            tooltipElement = null;
        }
        mouseEnterHandler.removeHandler();
        mouseExitHandler.removeHandler();
        mouseClickHandler.removeHandler();
    }
}
