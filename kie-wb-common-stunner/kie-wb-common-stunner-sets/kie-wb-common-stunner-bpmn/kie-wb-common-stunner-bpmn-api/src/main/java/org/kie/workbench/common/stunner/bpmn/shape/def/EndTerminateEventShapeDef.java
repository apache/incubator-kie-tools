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
import org.kie.workbench.common.stunner.core.definition.shape.AbstractShapeDef;
import org.kie.workbench.common.stunner.core.definition.shape.ShapeDef;
import org.kie.workbench.common.stunner.shapes.def.*;

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
    public String getGlyphBackgroundColor( final EndTerminateEvent element ) {
        return EndTerminateEvent.EndTerminateEventBuilder.COLOR;
    }

    @Override
    public String getGlyphDescription( final EndTerminateEvent element ) {
        return EndTerminateEvent.description;
    }

    @Override
    public Map<ShapeDef<EndTerminateEvent>, HasChildren.Layout> getChildShapeDefs() {
        return new HashMap<ShapeDef<EndTerminateEvent>, HasChildren.Layout>() {{
            put( new EndNoneEventRingProxy( EndTerminateEventShapeDef.this ), HasChildren.Layout.CENTER );
        }};
    }

    public final class EndNoneEventRingProxy extends WrappedBasicNamedShapeDef<EndTerminateEvent>
            implements RingShapeDef<EndTerminateEvent> {

        public EndNoneEventRingProxy( final BasicShapeWithTitleDef<EndTerminateEvent> parent ) {
            super( parent );
        }

        @Override
        public double getInnerRadius( final EndTerminateEvent element ) {
            return EndTerminateEventShapeDef.this.getRadius( element ) * 0.7;
        }

        @Override
        public double getOuterRadius( final EndTerminateEvent element ) {
            return EndTerminateEventShapeDef.this.getRadius( element ) * 0.9;
        }

        @Override
        public String getBackgroundColor( final EndTerminateEvent element ) {
            return EndTerminateEvent.EndTerminateEventBuilder.RING_COLOR;
        }

        @Override
        public String getBorderColor( final EndTerminateEvent element ) {
            return EndTerminateEvent.EndTerminateEventBuilder.RING_COLOR;
        }

        @Override
        public double getBorderSize( final EndTerminateEvent element ) {
            return 0;
        }

        @Override
        public String getGlyphBackgroundColor( final EndTerminateEvent element ) {
            return EndTerminateEvent.EndTerminateEventBuilder.RING_COLOR;
        }

    }

}
