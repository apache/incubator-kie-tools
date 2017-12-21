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
import org.kie.workbench.common.stunner.svg.client.shape.view.SVGPrimitive;
import org.kie.workbench.common.stunner.svg.client.shape.view.SVGShapeView;

public class ViewHandlerHelper {

    static final double DASH = 5d;

    public static void setCircleDashed(final SVGShapeView<?> view,
                                       final String circleId,
                                       final boolean dashed) {
        final SVGPrimitive<?> svgPrimitive = view.getChildren()
                .stream()
                .filter(prim -> prim.getId().equals(circleId))
                .findFirst()
                .get();
        final Circle circle = (Circle) svgPrimitive.get();
        final double dash = dashed ? DASH : 0d;
        circle.setDashArray(dash,
                            dash,
                            dash);
    }
}
