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
import org.kie.workbench.common.stunner.svg.client.shape.view.SVGShapeView;

@SVGViewFactory
public interface BPMNSVGViewFactory {

    /**
     * SVG View names (for referencing, if necessary, from ShapeDef instances)
     * MUST match with each of the SVG element identifier in the source files.
     */
    String VIEW_TASK = "task";
    String VIEW_TASK_USER = "taskUser";
    String VIEW_TASK_SCRIPT = "taskScript";
    String VIEW_TASK_BUSINESS_RULE = "taskBusinessRule";
    String VIEW_GATEWAY = "gateway";
    String VIEW_GATEWAY_PARALLEL_MULTIPLE = "gwParallelMultiple";
    String VIEW_GATEWAY_EXCLUSIVE = "gwExclusive";
    String VIEW_EVENT_START = "eventStart";
    String VIEW_EVENT_SIGNAL = "eventSignal";
    String VIEW_EVENT_TIMER = "eventTimer";
    String VIEW_EVENT_END = "eventEnd";
    String VIEW_EVENT_END_TERMINATE = "eventEndTerminate";
    String VIEW_EVENT_INTERMEDIATE = "eventIntermediate";
    String VIEW_LANE = "lane";
    String VIEW_SUBPROCESS = "subProcess";
    String VIEW_SUBPROCESS_REUSABLE = "subProcessReusable";
    String VIEW_SUBPROCESS_ADHOC = "subProcessAdHoc";
    String VIEW_RECTANGLE = "rectangle";

    /**
     * The file paths for the SVG views.
     */
    String PATH_TASK = "images/task/task.svg";
    String PATH_TASK_USER = "images/task/task-user.svg";
    String PATH_TASK_SCRIPT = "images/task/task-script.svg";
    String PATH_TASK_BUSINESS_RULE = "images/task/task-business-rule.svg";
    String PATH_GATEWAY = "images/gateway/gateway.svg";
    String PATH_GATEWAY_PARALLEL_MULTIPLE = "images/gateway/parallel_multiple.svg";
    String PATH_GATEWAY_EXCLUSIVE = "images/gateway/exclusive.svg";
    String PATH_EVENT_START = "images/event/event-start.svg";
    String PATH_EVENT_SIGNAL = "images/event/event-signal.svg";
    String PATH_EVENT_TIMER = "images/event/event-timer.svg";
    String PATH_EVENT_END = "images/event/event-end.svg";
    String PATH_EVENT_END_TERMINATE = "images/event/event-end-terminate.svg";
    String PATH_EVENT_INTERMEDIATE = "images/event/event-intermediate.svg";
    String PATH_LANE = "images/lane/lane.svg";
    String PATH_SUBPROCESS = "images/subprocess/subprocess.svg";
    String PATH_SUBPROCESS_REUSABLE = "images/subprocess/subprocess-reusable.svg";
    String PATH_SUBPROCESS_ADHOC = "images/subprocess/subprocess-adhoc.svg";
    String PATH_RECTANGLE = "images/misc/rectangle.svg";

    @SVGSource(PATH_TASK)
    SVGShapeView task(final double width,
                      final double height,
                      final boolean resizable);

    @SVGSource(PATH_TASK_USER)
    SVGShapeView taskUser(final double width,
                          final double height,
                          final boolean resizable);

    @SVGSource(PATH_TASK_SCRIPT)
    SVGShapeView taskScript(final double width,
                            final double height,
                            final boolean resizable);

    @SVGSource(PATH_TASK_BUSINESS_RULE)
    SVGShapeView taskBusinessRule(final double width,
                                  final double height,
                                  final boolean resizable);

    @SVGSource(PATH_GATEWAY)
    SVGShapeView gateway(final double width,
                         final double height,
                         final boolean resizable);

    @SVGSource(PATH_GATEWAY_PARALLEL_MULTIPLE)
    SVGShapeView gwParallelMultiple(final double width,
                                    final double height,
                                    final boolean resizable);

    @SVGSource(PATH_GATEWAY_EXCLUSIVE)
    SVGShapeView gwExclusive(final double width,
                             final double height,
                             final boolean resizable);

    @SVGSource(PATH_EVENT_START)
    SVGShapeView eventStart(final double width,
                            final double height,
                            final boolean resizable);

    @SVGSource(PATH_EVENT_SIGNAL)
    SVGShapeView eventSignal(final double width,
                             final double height,
                             final boolean resizable);

    @SVGSource(PATH_EVENT_TIMER)
    SVGShapeView eventTimer(final double width,
                            final double height,
                            final boolean resizable);

    @SVGSource(PATH_EVENT_END)
    SVGShapeView eventEnd(final double width,
                          final double height,
                          final boolean resizable);

    @SVGSource(PATH_EVENT_END_TERMINATE)
    SVGShapeView eventEndTerminate(final double width,
                                   final double height,
                                   final boolean resizable);

    @SVGSource(PATH_EVENT_INTERMEDIATE)
    SVGShapeView eventIntermediate(final double width,
                                   final double height,
                                   final boolean resizable);

    @SVGSource(PATH_LANE)
    SVGShapeView lane(final double width,
                      final double height,
                      final boolean resizable);

    @SVGSource(PATH_SUBPROCESS)
    SVGShapeView subProcess(final double width,
                            final double height,
                            final boolean resizable);

    @SVGSource(PATH_SUBPROCESS_REUSABLE)
    SVGShapeView subProcessReusable(final double width,
                                    final double height,
                                    final boolean resizable);

    @SVGSource(PATH_SUBPROCESS_ADHOC)
    SVGShapeView subProcessAdHoc(final double width,
                                 final double height,
                                 final boolean resizable);

    @SVGSource(PATH_RECTANGLE)
    SVGShapeView rectangle(final double width,
                           final double height,
                           final boolean resizable);
}
