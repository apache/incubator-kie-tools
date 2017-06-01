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

package org.kie.workbench.common.stunner.basicset.shape.def;

import org.kie.workbench.common.stunner.basicset.definition.Rectangle;
import org.kie.workbench.common.stunner.core.client.shape.view.HasTitle;
import org.kie.workbench.common.stunner.core.definition.shape.AbstractShapeDef;
import org.kie.workbench.common.stunner.core.definition.shape.GlyphDef;
import org.kie.workbench.common.stunner.core.definition.shape.GlyphDefinitions;
import org.kie.workbench.common.stunner.shapes.def.RectangleShapeDef;

public final class RectangleShapeDefImpl
        extends AbstractShapeDef<Rectangle>
        implements RectangleShapeDef<Rectangle> {

    @Override
    public double getWidth(final Rectangle element) {
        return element.getWidth().getValue();
    }

    @Override
    public double getHeight(final Rectangle element) {
        return element.getHeight().getValue();
    }

    @Override
    public double getCornerRadius(final Rectangle element) {
        return 5;
    }

    @Override
    public double getAlpha(final Rectangle element) {
        return 1d;
    }

    @Override
    public String getBackgroundColor(final Rectangle element) {
        return element.getBackgroundSet().getBgColor().getValue();
    }

    @Override
    public double getBackgroundAlpha(final Rectangle element) {
        return 1;
    }

    @Override
    public String getBorderColor(final Rectangle element) {
        return element.getBackgroundSet().getBorderColor().getValue();
    }

    @Override
    public double getBorderSize(final Rectangle element) {
        return element.getBackgroundSet().getBorderSize().getValue();
    }

    @Override
    public double getBorderAlpha(final Rectangle element) {
        return 1;
    }

    @Override
    public String getFontFamily(final Rectangle element) {
        return element.getFontSet().getFontFamily().getValue();
    }

    @Override
    public String getFontColor(final Rectangle element) {
        return element.getFontSet().getFontColor().getValue();
    }

    @Override
    public String getFontBorderColor(final Rectangle element) {
        return element.getFontSet().getFontBorderColor().getValue();
    }

    @Override
    public double getFontSize(final Rectangle element) {
        return element.getFontSet().getFontSize().getValue();
    }

    @Override
    public double getFontBorderSize(final Rectangle element) {
        return element.getFontSet().getFontBorderSize().getValue();
    }

    @Override
    public HasTitle.Position getFontPosition(final Rectangle element) {
        return HasTitle.Position.CENTER;
    }

    @Override
    public double getFontRotation(final Rectangle element) {
        return 0;
    }

    @Override
    public GlyphDef<Rectangle> getGlyphDef() {
        return GlyphDefinitions.GLYPH_SHAPE();
    }
}
