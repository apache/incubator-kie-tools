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

import java.util.LinkedHashMap;
import java.util.Map;

import org.kie.workbench.common.stunner.basicset.definition.PolygonWithIcon;
import org.kie.workbench.common.stunner.core.client.shape.HasChildren;
import org.kie.workbench.common.stunner.core.client.shape.view.HasTitle;
import org.kie.workbench.common.stunner.core.definition.shape.AbstractShapeDef;
import org.kie.workbench.common.stunner.core.definition.shape.GlyphDef;
import org.kie.workbench.common.stunner.core.definition.shape.GlyphDefinitions;
import org.kie.workbench.common.stunner.core.definition.shape.ShapeDef;
import org.kie.workbench.common.stunner.shapes.def.HasChildShapeDefs;
import org.kie.workbench.common.stunner.shapes.def.PolygonShapeDef;
import org.kie.workbench.common.stunner.shapes.def.icon.dynamics.DynamicIconShapeDef;
import org.kie.workbench.common.stunner.shapes.def.icon.dynamics.Icons;

public final class PolygonWithIconShapeDefImpl
        extends AbstractShapeDef<PolygonWithIcon>
        implements
        PolygonShapeDef<PolygonWithIcon>,
        HasChildShapeDefs<PolygonWithIcon> {

    @Override
    public double getRadius( final PolygonWithIcon element ) {
        return element.getRadius().getValue();
    }

    @Override
    public String getNamePropertyValue( final PolygonWithIcon element ) {
        return element.getName().getValue();
    }

    @Override
    public String getBackgroundColor( final PolygonWithIcon element ) {
        return element.getBackgroundSet().getBgColor().getValue();
    }

    @Override
    public double getBackgroundAlpha( final PolygonWithIcon element ) {
        return 1;
    }

    @Override
    public String getBorderColor( final PolygonWithIcon element ) {
        return element.getBackgroundSet().getBorderColor().getValue();
    }

    @Override
    public double getBorderSize( final PolygonWithIcon element ) {
        return element.getBackgroundSet().getBorderSize().getValue();
    }

    @Override
    public double getBorderAlpha( final PolygonWithIcon element ) {
        return 1;
    }

    @Override
    public String getFontFamily( final PolygonWithIcon element ) {
        return element.getFontSet().getFontFamily().getValue();
    }

    @Override
    public String getFontColor( final PolygonWithIcon element ) {
        return element.getFontSet().getFontColor().getValue();
    }

    @Override
    public double getFontSize( final PolygonWithIcon element ) {
        return element.getFontSet().getFontSize().getValue();
    }

    @Override
    public double getFontBorderSize( final PolygonWithIcon element ) {
        return element.getFontSet().getFontBorderSize().getValue();
    }

    @Override
    public HasTitle.Position getFontPosition( final PolygonWithIcon element ) {
        return HasTitle.Position.BOTTOM;
    }

    @Override
    public double getFontRotation( final PolygonWithIcon element ) {
        return 0;
    }

    @Override
    public GlyphDef<PolygonWithIcon> getGlyphDef() {
        return GlyphDefinitions.GLYPH_SHAPE();
    }

    @Override
    public Map<ShapeDef<PolygonWithIcon>, HasChildren.Layout> getChildShapeDefs() {
        return new LinkedHashMap<ShapeDef<PolygonWithIcon>, HasChildren.Layout>() {{
            put( new IconProxy(),
                 HasChildren.Layout.CENTER );
        }};
    }

    private final class IconProxy
            extends AbstractShapeDef<PolygonWithIcon>
            implements DynamicIconShapeDef<PolygonWithIcon> {

        private static final String COLOR = "#000000";

        @Override
        public Icons getIcon( final PolygonWithIcon definition ) {
            return definition.getIconType().getValue();
        }

        @Override
        public double getWidth( final PolygonWithIcon element ) {
            return element.getRadius().getValue() / 2;
        }

        @Override
        public double getHeight( final PolygonWithIcon element ) {
            return element.getRadius().getValue() / 2;
        }

        @Override
        public String getBackgroundColor( final PolygonWithIcon element ) {
            return COLOR;
        }

        @Override
        public double getBackgroundAlpha( final PolygonWithIcon element ) {
            return 1;
        }

        @Override
        public String getBorderColor( final PolygonWithIcon element ) {
            return COLOR;
        }

        @Override
        public double getBorderSize( final PolygonWithIcon element ) {
            return 5;
        }

        @Override
        public double getBorderAlpha( final PolygonWithIcon element ) {
            return 1;
        }
    }
}
