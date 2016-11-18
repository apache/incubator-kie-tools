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

import org.kie.workbench.common.stunner.bpmn.definition.EndTerminateEvent;
import org.kie.workbench.common.stunner.core.client.shape.HasChildren;
import org.kie.workbench.common.stunner.core.client.shape.view.HasTitle;
import org.kie.workbench.common.stunner.core.definition.shape.AbstractShapeDef;
import org.kie.workbench.common.stunner.core.definition.shape.GlyphDef;
import org.kie.workbench.common.stunner.core.definition.shape.ShapeDef;
import org.kie.workbench.common.stunner.shapes.def.BasicShapeWithTitleDef;
import org.kie.workbench.common.stunner.shapes.def.CircleShapeDef;
import org.kie.workbench.common.stunner.shapes.def.HasChildShapeDefs;
import org.kie.workbench.common.stunner.shapes.def.WrappedBasicNamedShapeDef;

import java.util.HashMap;
import java.util.Map;

public final class EndTerminateEventShapeDef
        extends AbstractShapeDef<EndTerminateEvent>
        implements CircleShapeDef<EndTerminateEvent>,
        HasChildShapeDefs<EndTerminateEvent> {

    @Override
    public double getRadius( final EndTerminateEvent element ) {
        return element.getDimensionsSet().getRadius().getValue();
    }

    @Override
    public String getBackgroundColor( final EndTerminateEvent element ) {
        return element.getBackgroundSet().getBgColor().getValue();
    }

    @Override
    public double getBackgroundAlpha( final EndTerminateEvent element ) {
        return 1;
    }

    @Override
    public String getBorderColor( final EndTerminateEvent element ) {
        return element.getBackgroundSet().getBorderColor().getValue();
    }

    @Override
    public double getBorderSize( final EndTerminateEvent element ) {
        return element.getBackgroundSet().getBorderSize().getValue();
    }

    @Override
    public double getBorderAlpha( final EndTerminateEvent element ) {
        return 1;
    }

    @Override
    public String getFontFamily( final EndTerminateEvent element ) {
        return element.getFontSet().getFontFamily().getValue();
    }

    @Override
    public String getFontColor( final EndTerminateEvent element ) {
        return element.getFontSet().getFontColor().getValue();
    }

    @Override
    public double getFontSize( final EndTerminateEvent element ) {
        return element.getFontSet().getFontSize().getValue();
    }

    @Override
    public String getNamePropertyValue( final EndTerminateEvent element ) {
        return element.getGeneral().getName().getValue();
    }

    @Override
    public double getFontBorderSize( final EndTerminateEvent element ) {
        return element.getFontSet().getFontBorderSize().getValue();
    }

    @Override
    public HasTitle.Position getFontPosition( final EndTerminateEvent element ) {
        return HasTitle.Position.BOTTOM;
    }

    @Override
    public double getFontRotation( final EndTerminateEvent element ) {
        return 0;
    }

    @Override
    public GlyphDef<EndTerminateEvent> getGlyphDef() {
        // TODO: Missing icon picture?
        return super.getGlyphDef();
    }

    @Override
    public Map<ShapeDef<EndTerminateEvent>, HasChildren.Layout> getChildShapeDefs() {
        return new HashMap<ShapeDef<EndTerminateEvent>, HasChildren.Layout>() {{
            put( new InnerCircleShapeDef( EndTerminateEventShapeDef.this ), HasChildren.Layout.CENTER );
        }};
    }

    // The inner circle shape (child).
    public final class InnerCircleShapeDef extends WrappedBasicNamedShapeDef<EndTerminateEvent>
            implements CircleShapeDef<EndTerminateEvent> {

        private static final String BG_COLOR = "#000000";

        public InnerCircleShapeDef( final BasicShapeWithTitleDef<EndTerminateEvent> parent ) {
            super( parent );
        }

        @Override
        public double getRadius( final EndTerminateEvent element ) {
            return EndTerminateEventShapeDef.this.getRadius( element ) * 0.6;
        }

        @Override
        public String getBackgroundColor( EndTerminateEvent element ) {
            return BG_COLOR;
        }

        @Override
        public double getBorderSize( final EndTerminateEvent element ) {
            return 1;
        }

        @Override
        public HasTitle.Position getFontPosition( final EndTerminateEvent element ) {
            return HasTitle.Position.CENTER;
        }

        @Override
        public double getFontRotation( final EndTerminateEvent element ) {
            return 0;
        }
    }

}
