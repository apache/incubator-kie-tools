/*
 * Copyright 2017 Red Hat, Inc. and/or its affiliates.
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

package org.kie.workbench.common.stunner.bpmn.client.resources;

import org.kie.workbench.common.stunner.svg.annotation.SVGSource;
import org.kie.workbench.common.stunner.svg.annotation.SVGViewFactory;
import org.kie.workbench.common.stunner.svg.client.shape.view.SVGShapeView;

@SVGViewFactory
public interface BPMNSVGViewFactory {

    String TASK = "images/task/task.svg";
    String TASK_USER = "images/task/task-user.svg";
    String TASK_SCRIPT = "images/task/task-script.svg";
    String TASK_BUSINESS_RULE = "images/task/task-business-rule.svg";
    String GATEWAY = "images/gateway/gateway.svg";
    String GATEWAY_PARALLEL_MULTIPLE = "images/gateway/parallel_multiple.svg";
    String GATEWAY_EXCLUSIVE = "images/gateway/exclusive.svg";
    String EVENT_START = "images/event/event-start.svg";
    String EVENT_START_SIGNAL = "images/event/event-start-signal.svg";
    String EVENT_START_TIMER = "images/event/event-start-timer.svg";
    String EVENT_END = "images/event/event-end.svg";
    String EVENT_END_NONE = "images/event/event-end-none.svg";
    String EVENT_END_TERMINATE = "images/event/event-end-terminate.svg";
    String EVENT_INTERMEDIATE = "images/event/event-intermediate.svg";
    String LANE = "images/lane/lane.svg";
    String LANE_ICON = "images/lane/lane_icon.svg";
    String SUBPROCESS_REUSABLE = "images/subprocess/sub-process-reusable.svg";
    String SUBPROCESS_EMBEDDED = "images/subprocess/sub-process-embedded.svg";
    String CIRCLE = "images/misc/circle.svg";
    String RECTANGLE = "images/misc/rectangle.svg";

    @SVGSource(TASK)
    SVGShapeView task(final double width,
                      final double height,
                      final boolean resizable);

    @SVGSource(TASK_USER)
    SVGShapeView taskUser(final double width,
                          final double height,
                          final boolean resizable);

    @SVGSource(TASK_SCRIPT)
    SVGShapeView taskScript(final double width,
                            final double height,
                            final boolean resizable);

    @SVGSource(TASK_BUSINESS_RULE)
    SVGShapeView taskBusinessRule(final double width,
                                  final double height,
                                  final boolean resizable);

    @SVGSource(GATEWAY)
    SVGShapeView gateway(final double width,
                         final double height,
                         final boolean resizable);

    @SVGSource(GATEWAY_PARALLEL_MULTIPLE)
    SVGShapeView gwParallelMultiple(final double width,
                                    final double height,
                                    final boolean resizable);

    @SVGSource(GATEWAY_EXCLUSIVE)
    SVGShapeView gwExclusive(final double width,
                             final double height,
                             final boolean resizable);

    @SVGSource(EVENT_START)
    SVGShapeView eventStart(final double width,
                            final double height,
                            final boolean resizable);

    @SVGSource(EVENT_START_SIGNAL)
    SVGShapeView eventStartSignal(final double width,
                                  final double height,
                                  final boolean resizable);

    @SVGSource(EVENT_START_TIMER)
    SVGShapeView eventStartTimer(final double width,
                                 final double height,
                                 final boolean resizable);

    @SVGSource(EVENT_END)
    SVGShapeView eventEnd(final double width,
                          final double height,
                          final boolean resizable);

    @SVGSource(EVENT_END_NONE)
    SVGShapeView eventEndNone(final double width,
                              final double height,
                              final boolean resizable);

    @SVGSource(EVENT_END_TERMINATE)
    SVGShapeView eventEndTerminate(final double width,
                                   final double height,
                                   final boolean resizable);

    @SVGSource(EVENT_INTERMEDIATE)
    SVGShapeView eventIntermediate(final double width,
                                   final double height,
                                   final boolean resizable);

    @SVGSource(LANE)
    SVGShapeView lane(final double width,
                      final double height,
                      final boolean resizable);

    @SVGSource(LANE_ICON)
    SVGShapeView laneIcon(final double width,
                          final double height,
                          final boolean resizable);

    @SVGSource(SUBPROCESS_REUSABLE)
    SVGShapeView subprocessReusable(final double width,
                                    final double height,
                                    final boolean resizable);

    @SVGSource(SUBPROCESS_EMBEDDED)
    SVGShapeView subprocessEmbedded(final double width,
                                    final double height,
                                    final boolean resizable);

    @SVGSource(CIRCLE)
    SVGShapeView circle(final double width,
                        final double height,
                        final boolean resizable);

    @SVGSource(RECTANGLE)
    SVGShapeView rectangle(final double width,
                           final double height,
                           final boolean resizable);
}
