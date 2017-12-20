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

package org.kie.workbench.common.stunner.bpmn.client.shape.view.handler;

import com.ait.lienzo.client.core.shape.Circle;
import org.kie.workbench.common.stunner.bpmn.definition.BaseCatchingIntermediateEvent;
import org.kie.workbench.common.stunner.bpmn.definition.IntermediateMessageEventCatching;
import org.kie.workbench.common.stunner.bpmn.definition.IntermediateSignalEventCatching;
import org.kie.workbench.common.stunner.core.client.shape.view.ShapeViewHandler;
import org.kie.workbench.common.stunner.svg.client.shape.view.SVGPrimitive;
import org.kie.workbench.common.stunner.svg.client.shape.view.SVGShapeView;

public class EventCancelActivityViewHandler
        implements ShapeViewHandler<BaseCatchingIntermediateEvent, SVGShapeView<?>> {

    // The id for the circle to change in the SVG file.
    static final String INTERMEDIATE_CIRCLE_ID = "eventAll_interm";
    static final double DASH = 5d;

    @Override
    public void handle(final BaseCatchingIntermediateEvent bean,
                       final SVGShapeView<?> view) {
        boolean isCancelActivity = false;
        boolean changeIntermediateCircleStyle = false;
        if (bean instanceof IntermediateSignalEventCatching) {
            isCancelActivity = ((IntermediateSignalEventCatching) bean).getExecutionSet().getCancelActivity().getValue();
            changeIntermediateCircleStyle = true;
        } else if (bean instanceof IntermediateMessageEventCatching) {
            isCancelActivity = ((IntermediateMessageEventCatching) bean).getExecutionSet().getCancelActivity().getValue();
            changeIntermediateCircleStyle = true;
        }
        if (changeIntermediateCircleStyle) {
            final SVGPrimitive<?> svgPrimitive = view.getChildren()
                    .stream()
                    .filter(prim -> prim.getId().equals(INTERMEDIATE_CIRCLE_ID))
                    .findFirst()
                    .get();
            final Circle circle = (Circle) svgPrimitive.get();
            final double dash = isCancelActivity ? 0d : DASH;
            circle.setDashArray(dash, dash, dash);
        }
    }
}

