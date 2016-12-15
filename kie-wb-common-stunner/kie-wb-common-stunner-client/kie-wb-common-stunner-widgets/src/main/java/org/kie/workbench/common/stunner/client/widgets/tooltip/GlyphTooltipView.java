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

package org.kie.workbench.common.stunner.client.widgets.tooltip;

import com.ait.lienzo.client.core.shape.IPrimitive;
import com.google.gwt.user.client.ui.FlowPanel;
import org.kie.workbench.common.stunner.core.client.components.glyph.GlyphTooltip;
import org.kie.workbench.common.stunner.lienzo.primitive.PrimitiveTooltip;

public class GlyphTooltipView extends FlowPanel implements GlyphTooltipImpl.View {

    GlyphTooltipImpl presenter;
    private PrimitiveTooltip tooltip;

    @Override
    public void init( final GlyphTooltipImpl presenter ) {
        this.presenter = presenter;
        this.tooltip = new PrimitiveTooltip();
    }

    @Override
    public GlyphTooltipImpl.View show( final String text,
                                       final double x,
                                       final double y,
                                       final GlyphTooltip.Direction direction ) {
        tooltip.show( null, text, 0, 0, x, y, getDirection( direction ) );
        return this;

    }

    @Override
    public GlyphTooltipImpl.View show( final IPrimitive<?> _glyph,
                                       final String text,
                                       final double x,
                                       final double y,
                                       final double width,
                                       final double height,
                                       final GlyphTooltip.Direction direction ) {
        tooltip.show( _glyph, text, width, height, x, y, getDirection( direction ) );
        return this;
    }

    @Override
    public GlyphTooltipImpl.View hide() {
        tooltip.hide();
        return this;
    }

    @Override
    public GlyphTooltipImpl.View remove() {
        tooltip.remove();
        return this;
    }

    protected PrimitiveTooltip.Direction getDirection( final GlyphTooltip.Direction direction ) {
        return GlyphTooltip.Direction.WEST.equals( direction ) ? PrimitiveTooltip.Direction.WEST : PrimitiveTooltip.Direction.NORTH;

    }

}
