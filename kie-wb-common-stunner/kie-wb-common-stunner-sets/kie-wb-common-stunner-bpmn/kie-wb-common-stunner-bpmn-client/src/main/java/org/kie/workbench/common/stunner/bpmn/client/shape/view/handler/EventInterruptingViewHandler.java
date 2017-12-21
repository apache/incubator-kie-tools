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

import org.kie.workbench.common.stunner.bpmn.definition.BaseStartEvent;
import org.kie.workbench.common.stunner.bpmn.definition.StartMessageEvent;
import org.kie.workbench.common.stunner.bpmn.definition.StartTimerEvent;
import org.kie.workbench.common.stunner.core.client.shape.view.ShapeViewHandler;
import org.kie.workbench.common.stunner.svg.client.shape.view.SVGShapeView;

import static org.kie.workbench.common.stunner.bpmn.client.shape.view.handler.ViewHandlerHelper.setCircleDashed;

public class EventInterruptingViewHandler
        implements ShapeViewHandler<BaseStartEvent, SVGShapeView<?>> {

    // The id for the circle to change in the SVG file.
    static final String INTERMEDIATE_CIRCLE_ID = "eventAll_interm";

    @Override
    public void handle(final BaseStartEvent bean,
                       final SVGShapeView<?> view) {
        if (bean instanceof StartMessageEvent) {
            final boolean isInterrupting = ((StartMessageEvent) bean).getExecutionSet().getIsInterrupting().getValue();
            setCircleDashed(view,
                            INTERMEDIATE_CIRCLE_ID,
                            !isInterrupting);
        } else if (bean instanceof StartTimerEvent) {
            final boolean isInterrupting = ((StartTimerEvent) bean).getExecutionSet().getIsInterrupting().getValue();
            setCircleDashed(view,
                            INTERMEDIATE_CIRCLE_ID,
                            !isInterrupting);
        }
    }
}
