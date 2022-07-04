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

package org.kie.workbench.common.stunner.sw.client.resources;

import org.kie.workbench.common.stunner.svg.annotation.SVGSource;
import org.kie.workbench.common.stunner.svg.annotation.SVGViewFactory;
import org.kie.workbench.common.stunner.svg.client.shape.view.SVGShapeViewResource;

import static org.kie.workbench.common.stunner.sw.client.resources.ShapeViewFactory.PATH_CSS;

@SVGViewFactory(cssPath = PATH_CSS)
public interface ShapeViewFactory {

    String PATH_CSS = "images/shapes/shapes.css";
    String PATH_STATE = "images/shapes/state.svg";
    String PATH_START = "images/shapes/start.svg";
    String PATH_EVENT = "images/shapes/event.svg";
    String PATH_EVENT_TIMEOUT = "images/shapes/event-timeout.svg";
    String PATH_ACTION = "images/shapes/action.svg";
    String PATH_CONTAINER = "images/shapes/container.svg";
    String PATH_END = "images/shapes/end.svg";

    @SVGSource(PATH_STATE)
    SVGShapeViewResource injectState();

    @SVGSource(PATH_STATE)
    SVGShapeViewResource switchState();

    @SVGSource(PATH_STATE)
    SVGShapeViewResource operationState();

    @SVGSource(PATH_STATE)
    SVGShapeViewResource eventState();

    @SVGSource(PATH_START)
    SVGShapeViewResource startState();

    @SVGSource(PATH_END)
    SVGShapeViewResource endState();

    @SVGSource(PATH_EVENT)
    SVGShapeViewResource event();

    @SVGSource(PATH_EVENT_TIMEOUT)
    SVGShapeViewResource eventTimeout();

    @SVGSource(PATH_ACTION)
    SVGShapeViewResource action();

    @SVGSource(PATH_CONTAINER)
    SVGShapeViewResource container();

    @SVGSource(PATH_STATE)
    SVGShapeViewResource callbackState();

    @SVGSource(PATH_STATE)
    SVGShapeViewResource forEachState();

    @SVGSource(PATH_STATE)
    SVGShapeViewResource parallelState();

    @SVGSource(PATH_STATE)
    SVGShapeViewResource sleepState();
}
