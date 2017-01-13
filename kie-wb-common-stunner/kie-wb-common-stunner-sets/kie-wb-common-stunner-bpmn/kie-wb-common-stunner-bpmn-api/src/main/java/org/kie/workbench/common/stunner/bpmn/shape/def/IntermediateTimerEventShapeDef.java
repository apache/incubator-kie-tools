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

package org.kie.workbench.common.stunner.bpmn.shape.def;

import java.util.HashMap;
import java.util.Map;

import org.kie.workbench.common.stunner.bpmn.definition.IntermediateTimerEvent;
import org.kie.workbench.common.stunner.core.client.shape.HasChildren;
import org.kie.workbench.common.stunner.core.client.shape.view.HasTitle;
import org.kie.workbench.common.stunner.core.definition.shape.AbstractShapeDef;
import org.kie.workbench.common.stunner.core.definition.shape.GlyphDef;
import org.kie.workbench.common.stunner.core.definition.shape.ShapeDef;
import org.kie.workbench.common.stunner.shapes.def.BasicShapeWithTitleDef;
import org.kie.workbench.common.stunner.shapes.def.CircleShapeDef;
import org.kie.workbench.common.stunner.shapes.def.HasChildShapeDefs;
import org.kie.workbench.common.stunner.shapes.def.WrappedBasicNamedShapeDef;
import org.kie.workbench.common.stunner.shapes.def.icon.statics.IconShapeDef;
import org.kie.workbench.common.stunner.shapes.def.icon.statics.Icons;
import org.kie.workbench.common.stunner.shapes.def.picture.PictureGlyphDef;

public final class IntermediateTimerEventShapeDef
        extends AbstractShapeDef<IntermediateTimerEvent>
        implements
        CircleShapeDef<IntermediateTimerEvent>,
        HasChildShapeDefs<IntermediateTimerEvent> {

    private static final String WHITE = "#FFFFFF";

    @Override
    public double getRadius(final IntermediateTimerEvent element) {
        return element.getDimensionsSet().getRadius().getValue();
    }

    @Override
    public String getBackgroundColor(final IntermediateTimerEvent element) {
        return element.getBackgroundSet().getBgColor().getValue();
    }

    @Override
    public double getBackgroundAlpha(final IntermediateTimerEvent element) {
        return 1;
    }

    @Override
    public String getBorderColor(final IntermediateTimerEvent element) {
        return element.getBackgroundSet().getBorderColor().getValue();
    }

    @Override
    public double getBorderSize(final IntermediateTimerEvent element) {
        return element.getBackgroundSet().getBorderSize().getValue();
    }

    @Override
    public double getBorderAlpha(final IntermediateTimerEvent element) {
        return 1;
    }

    @Override
    public String getFontFamily(final IntermediateTimerEvent element) {
        return element.getFontSet().getFontFamily().getValue();
    }

    @Override
    public String getFontColor(final IntermediateTimerEvent element) {
        return element.getFontSet().getFontColor().getValue();
    }

    @Override
    public double getFontSize(final IntermediateTimerEvent element) {
        return element.getFontSet().getFontSize().getValue();
    }

    @Override
    public String getNamePropertyValue(final IntermediateTimerEvent element) {
        return element.getGeneral().getName().getValue();
    }

    @Override
    public double getFontBorderSize(final IntermediateTimerEvent element) {
        return element.getFontSet().getFontBorderSize().getValue();
    }

    @Override
    public HasTitle.Position getFontPosition(final IntermediateTimerEvent element) {
        return HasTitle.Position.BOTTOM;
    }

    @Override
    public double getFontRotation(final IntermediateTimerEvent element) {
        return 0;
    }

    @Override
    public Map<ShapeDef<IntermediateTimerEvent>, HasChildren.Layout> getChildShapeDefs() {
        return new HashMap<ShapeDef<IntermediateTimerEvent>, HasChildren.Layout>() {{
            put(new Circle1Proxy(IntermediateTimerEventShapeDef.this),
                HasChildren.Layout.CENTER);
            put(new Circle2Proxy(IntermediateTimerEventShapeDef.this),
                HasChildren.Layout.CENTER);
            put(new Circle3Proxy(IntermediateTimerEventShapeDef.this),
                HasChildren.Layout.CENTER);
            put(new Circle4Proxy(IntermediateTimerEventShapeDef.this),
                HasChildren.Layout.CENTER);
            put(new Circle5Proxy(IntermediateTimerEventShapeDef.this),
                HasChildren.Layout.CENTER);
            put(new TimerIconProxy(),
                HasChildren.Layout.CENTER);
        }};
    }

    private static final PictureGlyphDef<IntermediateTimerEvent, BPMNPictures> PICTURE_GLYPH_DEF =
            new PictureGlyphDef<IntermediateTimerEvent, BPMNPictures>() {

                @Override
                public String getGlyphDescription(final IntermediateTimerEvent element) {
                    return element.getDescription();
                }

                @Override
                public BPMNPictures getSource(final Class<?> type) {
                    return BPMNPictures.CLOCK_O;
                }
            };

    @Override
    public GlyphDef<IntermediateTimerEvent> getGlyphDef() {
        return PICTURE_GLYPH_DEF;
    }

    // The timer icon.
    public final class TimerIconProxy
            extends AbstractShapeDef<IntermediateTimerEvent>
            implements IconShapeDef<IntermediateTimerEvent> {

        @Override
        public Icons getIcon(final IntermediateTimerEvent element) {
            return Icons.TIMER;
        }
    }

    // Outer circle #1.
    public final class Circle1Proxy extends WrappedBasicNamedShapeDef<IntermediateTimerEvent>
            implements CircleShapeDef<IntermediateTimerEvent> {

        public Circle1Proxy(final BasicShapeWithTitleDef<IntermediateTimerEvent> parent) {
            super(parent);
        }

        @Override
        public double getRadius(final IntermediateTimerEvent element) {
            return percent(element,
                           1);
        }

        @Override
        public double getBorderSize(final IntermediateTimerEvent element) {
            return 1;
        }

        @Override
        public HasTitle.Position getFontPosition(final IntermediateTimerEvent element) {
            return HasTitle.Position.BOTTOM;
        }

        @Override
        public double getFontRotation(final IntermediateTimerEvent element) {
            return 0;
        }
    }

    // Outer circle #2.
    public final class Circle2Proxy extends WrappedBasicNamedShapeDef<IntermediateTimerEvent>
            implements CircleShapeDef<IntermediateTimerEvent> {

        public Circle2Proxy(final BasicShapeWithTitleDef<IntermediateTimerEvent> parent) {
            super(parent);
        }

        @Override
        public double getRadius(final IntermediateTimerEvent element) {
            return percent(element,
                           0.8);
        }

        @Override
        public double getBorderSize(final IntermediateTimerEvent element) {
            return 1;
        }

        @Override
        public HasTitle.Position getFontPosition(final IntermediateTimerEvent element) {
            return HasTitle.Position.BOTTOM;
        }

        @Override
        public double getFontRotation(final IntermediateTimerEvent element) {
            return 0;
        }
    }

    // Outer circle #3.
    public final class Circle3Proxy extends WrappedBasicNamedShapeDef<IntermediateTimerEvent>
            implements CircleShapeDef<IntermediateTimerEvent> {

        public Circle3Proxy(final BasicShapeWithTitleDef<IntermediateTimerEvent> parent) {
            super(parent);
        }

        @Override
        public double getRadius(final IntermediateTimerEvent element) {
            return percent(element,
                           1);
        }

        @Override
        public double getBorderSize(final IntermediateTimerEvent element) {
            return 1;
        }

        @Override
        public HasTitle.Position getFontPosition(final IntermediateTimerEvent element) {
            return HasTitle.Position.BOTTOM;
        }

        @Override
        public double getFontRotation(final IntermediateTimerEvent element) {
            return 0;
        }
    }

    // Outer circle #4.
    public final class Circle4Proxy extends WrappedBasicNamedShapeDef<IntermediateTimerEvent>
            implements CircleShapeDef<IntermediateTimerEvent> {

        public Circle4Proxy(final BasicShapeWithTitleDef<IntermediateTimerEvent> parent) {
            super(parent);
        }

        @Override
        public double getRadius(final IntermediateTimerEvent element) {
            return percent(element,
                           0.8);
        }

        @Override
        public double getBorderSize(final IntermediateTimerEvent element) {
            return 1;
        }

        @Override
        public HasTitle.Position getFontPosition(final IntermediateTimerEvent element) {
            return HasTitle.Position.BOTTOM;
        }

        @Override
        public double getFontRotation(final IntermediateTimerEvent element) {
            return 0;
        }
    }

    // Outer circle #5.
    public final class Circle5Proxy extends WrappedBasicNamedShapeDef<IntermediateTimerEvent>
            implements CircleShapeDef<IntermediateTimerEvent> {

        public Circle5Proxy(final BasicShapeWithTitleDef<IntermediateTimerEvent> parent) {
            super(parent);
        }

        @Override
        public double getRadius(final IntermediateTimerEvent element) {
            return percent(element,
                           0.67);
        }

        @Override
        public double getBorderSize(final IntermediateTimerEvent element) {
            return 1;
        }

        @Override
        public HasTitle.Position getFontPosition(final IntermediateTimerEvent element) {
            return HasTitle.Position.BOTTOM;
        }

        @Override
        public double getFontRotation(final IntermediateTimerEvent element) {
            return 0;
        }
    }

    private double percent(final IntermediateTimerEvent element,
                           final double pct) {
        final double radius = IntermediateTimerEventShapeDef.this.getRadius(element);
        return percent(radius,
                       pct);
    }

    private static double percent(final double value,
                                  final double pct) {
        return (value * pct);
    }
}
