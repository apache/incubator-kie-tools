/*
 * Copyright 2018 Red Hat, Inc. and/or its affiliates.
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

package org.kie.workbench.common.stunner.cm.client.resources;

import org.kie.workbench.common.stunner.svg.annotation.SVGSource;
import org.kie.workbench.common.stunner.svg.annotation.SVGViewFactory;
import org.kie.workbench.common.stunner.svg.client.shape.view.SVGShapeViewResource;

import static org.kie.workbench.common.stunner.cm.client.resources.CaseManagementSVGViewFactory.PATH_CSS;

@SVGViewFactory(cssPath = PATH_CSS, builder = CaseManagementSVGViewBuilder.class)
public interface CaseManagementSVGViewFactory {

    String PATH_CSS = "images/shapes/cm-shapes.css";
    String PATH_STAGE = "images/shapes/stage.svg";
    String PATH_TASK = "images/shapes/user_task.svg";
    String PATH_SUBPROCESS = "images/shapes/subprocess.svg";
    String PATH_SUBCASE = "images/shapes/subcase.svg";
    String PATH_RECTANGLE = "images/shapes/rectangle.svg";

    @SVGSource(PATH_STAGE)
    SVGShapeViewResource stage();

    @SVGSource(PATH_TASK)
    SVGShapeViewResource task();

    @SVGSource(PATH_SUBPROCESS)
    SVGShapeViewResource subprocess();

    @SVGSource(PATH_SUBCASE)
    SVGShapeViewResource subcase();

    @SVGSource(PATH_RECTANGLE)
    SVGShapeViewResource rectangle();
}