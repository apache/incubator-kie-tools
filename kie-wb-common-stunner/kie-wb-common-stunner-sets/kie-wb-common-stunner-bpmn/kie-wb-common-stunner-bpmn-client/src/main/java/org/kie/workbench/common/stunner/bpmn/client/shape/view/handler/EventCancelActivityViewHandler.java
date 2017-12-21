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

import org.kie.workbench.common.stunner.bpmn.definition.BaseCatchingIntermediateEvent;
import org.kie.workbench.common.stunner.bpmn.definition.IntermediateMessageEventCatching;
import org.kie.workbench.common.stunner.bpmn.definition.IntermediateSignalEventCatching;
import org.kie.workbench.common.stunner.bpmn.definition.IntermediateTimerEvent;
import org.kie.workbench.common.stunner.core.client.shape.view.ShapeViewHandler;
import org.kie.workbench.common.stunner.svg.client.shape.view.SVGShapeView;

import static org.kie.workbench.common.stunner.bpmn.client.shape.view.handler.ViewHandlerHelper.setCircleDashed;

public class EventCancelActivityViewHandler
        implements ShapeViewHandler<BaseCatchingIntermediateEvent, SVGShapeView<?>> {

    // The id for the circle to change in the SVG file.
    static final String INTERMEDIATE_CIRCLE_ID = "eventAll_interm";

    @Override
    public void handle(final BaseCatchingIntermediateEvent bean,
                       final SVGShapeView<?> view) {
        if (bean instanceof IntermediateSignalEventCatching) {
            final boolean isCancelActivity = ((IntermediateSignalEventCatching) bean).getExecutionSet().getCancelActivity().getValue();
            setCircleDashed(view,
                            INTERMEDIATE_CIRCLE_ID,
                            !isCancelActivity);
        } else if (bean instanceof IntermediateTimerEvent) {
            final boolean isCancelActivity = ((IntermediateTimerEvent) bean).getExecutionSet().getCancelActivity().getValue();
            setCircleDashed(view,
                            INTERMEDIATE_CIRCLE_ID,
                            !isCancelActivity);
        } else if (bean instanceof IntermediateMessageEventCatching) {
            final boolean isCancelActivity = ((IntermediateMessageEventCatching) bean).getExecutionSet().getCancelActivity().getValue();
            setCircleDashed(view,
                            INTERMEDIATE_CIRCLE_ID,
                            !isCancelActivity);
        }
    }
}

