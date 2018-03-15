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
import org.kie.workbench.common.stunner.bpmn.definition.StartSignalEvent;
import org.kie.workbench.common.stunner.bpmn.definition.StartTimerEvent;
import org.kie.workbench.common.stunner.core.client.shape.view.ShapeViewHandler;
import org.kie.workbench.common.stunner.svg.client.shape.view.SVGShapeView;
import org.kie.workbench.common.stunner.svg.client.shape.view.SVGViewUtils;

public class EventInterruptingViewHandler
        implements ShapeViewHandler<BaseStartEvent, SVGShapeView<?>> {

    // The id for the circle to change in the SVG file.
    static final String ID_START = "start";
    static final String ID_START_NON_INTERRUPTING = "start-noninterrupting";

    @Override
    public void handle(final BaseStartEvent bean,
                       final SVGShapeView<?> view) {
        Boolean isInterrupting = null;
        if (bean instanceof StartMessageEvent) {
            isInterrupting = ((StartMessageEvent) bean).getExecutionSet().getIsInterrupting().getValue();
        } else if (bean instanceof StartTimerEvent) {
            isInterrupting = ((StartTimerEvent) bean).getExecutionSet().getIsInterrupting().getValue();
        } else if (bean instanceof StartSignalEvent) {
            isInterrupting = ((StartSignalEvent) bean).getExecutionSet().getIsInterrupting().getValue();
        }
        if (null != isInterrupting) {
            final String visibleId = isInterrupting ? ID_START : ID_START_NON_INTERRUPTING;
            final String nonVisibleId = !isInterrupting ? ID_START : ID_START_NON_INTERRUPTING;
            SVGViewUtils.switchVisibility(view,
                                          visibleId,
                                          nonVisibleId);
        }
    }
}
