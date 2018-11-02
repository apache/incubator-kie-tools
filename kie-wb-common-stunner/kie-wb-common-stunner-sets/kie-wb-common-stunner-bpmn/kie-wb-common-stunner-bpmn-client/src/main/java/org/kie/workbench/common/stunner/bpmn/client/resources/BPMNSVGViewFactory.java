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

package org.kie.workbench.common.stunner.bpmn.client.resources;

import org.kie.workbench.common.stunner.svg.annotation.SVGSource;
import org.kie.workbench.common.stunner.svg.annotation.SVGViewFactory;
import org.kie.workbench.common.stunner.svg.client.shape.view.SVGShapeViewResource;

import static org.kie.workbench.common.stunner.bpmn.client.resources.BPMNSVGViewFactory.PATH_CSS;

@SVGViewFactory(cssPath = PATH_CSS)
public interface BPMNSVGViewFactory {

    /**
     * The file paths for the SVG shape views.
     */
    String PATH_CSS = "images/shapes/bpmn-shapes.css";
    String PATH_TASK = "images/shapes/task.svg";
    String PATH_GATEWAY = "images/shapes/gateway.svg";
    String PATH_EVENT_ALL = "images/shapes/event-all.svg";
    String PATH_LANE = "images/shapes/lane.svg";
    String PATH_SUBPROCESS = "images/shapes/subprocess.svg";
    String PATH_SUBPROCESS_ADHOC = "images/shapes/subprocess-adhoc.svg";
    String PATH_SUBPROCESS_EVENT = "images/shapes/subprocess-event.svg";
    String PATH_SUBPROCESS_MULTIPLE_INSTANCE = "images/shapes/subprocess-multiple-instance.svg";
    String PATH_RECTANGLE = "images/shapes/rectangle.svg";

    @SVGSource(PATH_TASK)
    SVGShapeViewResource noneTask();

    @SVGSource(PATH_TASK)
    SVGShapeViewResource userTask();

    @SVGSource(PATH_TASK)
    SVGShapeViewResource scriptTask();

    @SVGSource(PATH_TASK)
    SVGShapeViewResource businessRuleTask();

    @SVGSource(PATH_TASK)
    SVGShapeViewResource serviceTask();

    @SVGSource(PATH_GATEWAY)
    SVGShapeViewResource parallelMultipleGateway();

    @SVGSource(PATH_GATEWAY)
    SVGShapeViewResource exclusiveGateway();

    @SVGSource(PATH_GATEWAY)
    SVGShapeViewResource inclusiveGateway();

    @SVGSource(PATH_EVENT_ALL)
    SVGShapeViewResource startNoneEvent();

    @SVGSource(PATH_EVENT_ALL)
    SVGShapeViewResource startSignalEvent();

    @SVGSource(PATH_EVENT_ALL)
    SVGShapeViewResource startMessageEvent();

    @SVGSource(PATH_EVENT_ALL)
    SVGShapeViewResource startTimerEvent();

    @SVGSource(PATH_EVENT_ALL)
    SVGShapeViewResource startErrorEvent();

    @SVGSource(PATH_EVENT_ALL)
    SVGShapeViewResource startConditionalEvent();

    @SVGSource(PATH_EVENT_ALL)
    SVGShapeViewResource startEscalationEvent();

    @SVGSource(PATH_EVENT_ALL)
    SVGShapeViewResource startCompensationEvent();

    @SVGSource(PATH_EVENT_ALL)
    SVGShapeViewResource endNoneEvent();

    @SVGSource(PATH_EVENT_ALL)
    SVGShapeViewResource endSignalEvent();

    @SVGSource(PATH_EVENT_ALL)
    SVGShapeViewResource endMessageEvent();

    @SVGSource(PATH_EVENT_ALL)
    SVGShapeViewResource endTerminateEvent();

    @SVGSource(PATH_EVENT_ALL)
    SVGShapeViewResource endErrorEvent();

    @SVGSource(PATH_EVENT_ALL)
    SVGShapeViewResource endEscalationEvent();

    @SVGSource(PATH_EVENT_ALL)
    SVGShapeViewResource endCompensationEvent();

    @SVGSource(PATH_EVENT_ALL)
    SVGShapeViewResource intermediateNoneEvent();

    @SVGSource(PATH_EVENT_ALL)
    SVGShapeViewResource intermediateSignalCatchingEvent();

    @SVGSource(PATH_EVENT_ALL)
    SVGShapeViewResource intermediateSignalThrowingEvent();

    @SVGSource(PATH_EVENT_ALL)
    SVGShapeViewResource intermediateMessageCatchingEvent();

    @SVGSource(PATH_EVENT_ALL)
    SVGShapeViewResource intermediateEscalationCatchingEvent();

    @SVGSource(PATH_EVENT_ALL)
    SVGShapeViewResource intermediateCompensationCatchingEvent();

    @SVGSource(PATH_EVENT_ALL)
    SVGShapeViewResource intermediateMessageThrowingEvent();

    @SVGSource(PATH_EVENT_ALL)
    SVGShapeViewResource intermediateEscalationThrowingEvent();

    @SVGSource(PATH_EVENT_ALL)
    SVGShapeViewResource intermediateCompensationThrowingEvent();

    @SVGSource(PATH_EVENT_ALL)
    SVGShapeViewResource intermediateTimerEvent();

    @SVGSource(PATH_EVENT_ALL)
    SVGShapeViewResource intermediateConditionalEvent();

    @SVGSource(PATH_EVENT_ALL)
    SVGShapeViewResource intermediateErrorCatchingEvent();

    @SVGSource(PATH_LANE)
    SVGShapeViewResource lane();

    @SVGSource(PATH_SUBPROCESS)
    SVGShapeViewResource reusableSubProcess();

    @SVGSource(PATH_SUBPROCESS_ADHOC)
    SVGShapeViewResource adHocSubProcess();

    @SVGSource(PATH_SUBPROCESS_MULTIPLE_INSTANCE)
    SVGShapeViewResource multipleInstanceSubProcess();

    @SVGSource(PATH_SUBPROCESS_ADHOC)
    SVGShapeViewResource embeddedSubProcess();

    @SVGSource(PATH_SUBPROCESS_EVENT)
    SVGShapeViewResource eventSubProcess();

    @SVGSource(PATH_RECTANGLE)
    SVGShapeViewResource rectangle();
}
