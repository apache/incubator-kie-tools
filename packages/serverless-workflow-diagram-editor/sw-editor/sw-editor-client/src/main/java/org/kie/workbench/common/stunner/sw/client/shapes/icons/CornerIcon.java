/*
 * Copyright 2023 Red Hat, Inc. and/or its affiliates.
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
package org.kie.workbench.common.stunner.sw.client.shapes.icons;

import com.ait.lienzo.client.core.shape.Group;
import com.ait.lienzo.client.core.shape.MultiPath;
import com.ait.lienzo.client.core.shape.Rectangle;
import com.ait.lienzo.client.core.shape.toolbox.items.tooltip.PrimitiveTextTooltip;
import com.ait.lienzo.client.core.types.Point2D;
import com.ait.lienzo.shared.core.types.EventPropagationMode;
import com.ait.lienzo.tools.client.event.HandlerRegistration;

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
            .setEventPropagationMode(EventPropagationMode.NO_ANCESTORS)
            .setListening(true);

    public CornerIcon(String icon, Point2D position, String tooltip) {
        setLocation(position);
        setListening(true);
        add(border);
        this.tooltipText = tooltip;

        MultiPath clockIcon = new MultiPath(icon)
                .setScale(2)
                .setStrokeWidth(0)
                .setFillColor("#CCC")
                .setListening(false);
        add(clockIcon);

        mouseEnterHandler = border.addNodeMouseEnterHandler(event -> {
            this.getParent().moveToTop();
            this.moveToTop();
            createToolTip();
            clockIcon.setFillColor("#4F5255");
        });
        mouseExitHandler = border.addNodeMouseExitHandler(event -> {
            tooltipElement.hide();
            remove(tooltipElement.asPrimitive());
            tooltipElement.destroy();
            tooltipElement = null;
            clockIcon.setFillColor("#CCC");
            border.getLayer().batch();
        });
        mouseClickHandler = border.addNodeMouseClickHandler(
                event -> this.getParent().asGroup().getChildren().get(0).fireEvent(event)
        );
    }

    private void createToolTip() {
        tooltipElement = PrimitiveTextTooltip.Builder.build(tooltipText);
        tooltipElement.withText(t -> {
            t.setText(tooltipText);
            t.setFontSize(12);
        });
        add(tooltipElement.asPrimitive());
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
