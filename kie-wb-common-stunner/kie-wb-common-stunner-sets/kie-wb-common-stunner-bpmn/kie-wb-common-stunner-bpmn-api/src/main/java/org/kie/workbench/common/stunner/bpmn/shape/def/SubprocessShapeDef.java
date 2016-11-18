/*
 * Copyright 2016 Red Hat, Inc. and/or its affiliates.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * 	http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.kie.workbench.common.stunner.bpmn.shape.def;

import org.kie.workbench.common.stunner.bpmn.definition.ReusableSubprocess;
import org.kie.workbench.common.stunner.core.client.shape.HasChildren;
import org.kie.workbench.common.stunner.core.client.shape.view.HasTitle;
import org.kie.workbench.common.stunner.core.definition.shape.AbstractShapeDef;
import org.kie.workbench.common.stunner.core.definition.shape.GlyphDef;
import org.kie.workbench.common.stunner.core.definition.shape.ShapeDef;
import org.kie.workbench.common.stunner.shapes.def.HasChildShapeDefs;
import org.kie.workbench.common.stunner.shapes.def.RectangleShapeDef;
import org.kie.workbench.common.stunner.shapes.def.icon.dynamics.DynamicIconShapeDef;
import org.kie.workbench.common.stunner.shapes.def.icon.dynamics.Icons;
import org.kie.workbench.common.stunner.shapes.def.picture.PictureGlyphDef;

import java.util.HashMap;
import java.util.Map;

public final class SubprocessShapeDef
        extends AbstractShapeDef<ReusableSubprocess>
        implements
        RectangleShapeDef<ReusableSubprocess>,
        HasChildShapeDefs<ReusableSubprocess> {

    @Override
    public String getBackgroundColor( final ReusableSubprocess element ) {
        return element.getBackgroundSet().getBgColor().getValue();
    }

    @Override
    public double getBackgroundAlpha( final ReusableSubprocess element ) {
        return 1;
    }

    @Override
    public String getBorderColor( final ReusableSubprocess element ) {
        return element.getBackgroundSet().getBorderColor().getValue();
    }

    @Override
    public double getBorderSize( final ReusableSubprocess element ) {
        return element.getBackgroundSet().getBorderSize().getValue();
    }

    @Override
    public double getBorderAlpha( final ReusableSubprocess element ) {
        return 1;
    }

    @Override
    public String getFontFamily( final ReusableSubprocess element ) {
        return element.getFontSet().getFontFamily().getValue();
    }

    @Override
    public String getFontColor( final ReusableSubprocess element ) {
        return element.getFontSet().getFontColor().getValue();
    }

    @Override
    public double getFontSize( final ReusableSubprocess element ) {
        return element.getFontSet().getFontSize().getValue();
    }

    @Override
    public String getNamePropertyValue( final ReusableSubprocess element ) {
        return element.getGeneral().getName().getValue();
    }

    @Override
    public double getFontBorderSize( final ReusableSubprocess element ) {
        return element.getFontSet().getFontBorderSize().getValue();
    }

    @Override
    public HasTitle.Position getFontPosition( final ReusableSubprocess element ) {
        return HasTitle.Position.BOTTOM;
    }

    @Override
    public double getFontRotation( final ReusableSubprocess element ) {
        return 0;
    }

    @Override
    public Map<ShapeDef<ReusableSubprocess>, HasChildren.Layout> getChildShapeDefs() {
        return new HashMap<ShapeDef<ReusableSubprocess>, HasChildren.Layout>() {{
            put( new ProcessIconProxy(), HasChildren.Layout.TOP );

        }};
    }

    @Override
    public double getWidth( final ReusableSubprocess element ) {
        return element.getDimensionsSet().getWidth().getValue();
    }

    @Override
    public double getHeight( final ReusableSubprocess element ) {
        return element.getDimensionsSet().getHeight().getValue();
    }

    @Override
    public double getCornerRadius( final ReusableSubprocess element ) {
        return 0;
    }

    private static final PictureGlyphDef<ReusableSubprocess, BPMNPictures> SUBPROCESS_GLYPH_DEF =
            new PictureGlyphDef<ReusableSubprocess, BPMNPictures>() {

        @Override
        public String getGlyphDescription( ReusableSubprocess element ) {
            return element.getDescription();
        }

        @Override
        public BPMNPictures getSource( final Class<?> type ) {
            return BPMNPictures.SUB_PROCESS;
        }
    };

    @Override
    public GlyphDef<ReusableSubprocess> getGlyphDef() {
        return SUBPROCESS_GLYPH_DEF;
    }

    public final class ProcessIconProxy
            extends AbstractShapeDef<ReusableSubprocess>
            implements DynamicIconShapeDef<ReusableSubprocess> {

        private static final String BLACK = "#000000";

        @Override
        public double getWidth( final ReusableSubprocess element ) {
            return element.getDimensionsSet().getWidth().getValue() / 2;
        }

        @Override
        public double getHeight( final ReusableSubprocess element ) {
            return element.getDimensionsSet().getHeight().getValue() / 2;
        }

        @Override
        public String getBackgroundColor( final ReusableSubprocess element ) {
            return BLACK;
        }

        @Override
        public double getBackgroundAlpha( final ReusableSubprocess element ) {
            return 1;
        }

        @Override
        public String getBorderColor( final ReusableSubprocess element ) {
            return BLACK;
        }

        @Override
        public double getBorderSize( final ReusableSubprocess element ) {
            return 0;
        }

        @Override
        public double getBorderAlpha( final ReusableSubprocess element ) {
            return 1;
        }

        @Override
        public Icons getIcon( final ReusableSubprocess definition ) {
            return Icons.PLUS;
        }

    }

}
