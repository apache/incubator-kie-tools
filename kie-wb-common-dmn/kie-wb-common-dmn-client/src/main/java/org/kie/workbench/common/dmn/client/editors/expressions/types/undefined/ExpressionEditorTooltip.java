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

package org.kie.workbench.common.dmn.client.editors.expressions.types.undefined;

import com.ait.lienzo.client.core.shape.IPrimitive;
import com.ait.lienzo.client.core.shape.Rectangle;
import com.ait.lienzo.client.core.types.BoundingBox;
import com.ait.lienzo.shared.core.types.Direction;
import org.kie.workbench.common.dmn.api.definition.v1_1.Expression;
import org.kie.workbench.common.dmn.client.editors.expressions.types.ExpressionEditorDefinition;
import org.kie.workbench.common.stunner.lienzo.toolbox.items.tooltip.PrimitiveTextTooltip;

public class ExpressionEditorTooltip {

    static final ExpressionEditorTooltip INSTANCE = new ExpressionEditorTooltip();

    private PrimitiveTextTooltip tooltip;

    private ExpressionEditorTooltip() {
        tooltip = PrimitiveTextTooltip.Builder.build("")
                .forComputedBoundingBox(BoundingBox::new)
                .withText(text -> text.setFontSize(10.0))
                .towards(Direction.SOUTH)
                .at(Direction.SOUTH);
    }

    void show(final ExpressionEditorDefinition<Expression> definition,
              final double absoluteCellX,
              final double absoluteCellY,
              final Rectangle rectangle) {
        tooltip.withText((text) -> text.setText(definition.getName()))
                .forComputedBoundingBox(() -> getBoundingBox(absoluteCellX, absoluteCellY, rectangle))
                .show();
    }

    private BoundingBox getBoundingBox(final double absoluteCellX,
                                       final double absoluteCellY,
                                       final Rectangle r) {
        final double minX = absoluteCellX + r.getX() - 25;
        final double maxX = absoluteCellX + r.getX() + r.getWidth() + 25;
        final double minY = absoluteCellY + r.getY();
        final double maxY = absoluteCellY + r.getY() + r.getHeight();
        return new BoundingBox(minX, minY, maxX, maxY);
    }

    void hide() {
        tooltip.hide();
    }

    IPrimitive<?> asPrimitive() {
        return tooltip.asPrimitive();
    }
}
