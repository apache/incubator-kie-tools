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

package org.kie.workbench.common.dmn.backend.definition.v1_1.dd;

public class PointUtils {

    private PointUtils() {
        // util class.
    }

    public static org.kie.dmn.model.api.dmndi.Point point2dToDMNDIPoint(org.kie.workbench.common.stunner.core.graph.content.view.Point2D point2d) {
        org.kie.dmn.model.api.dmndi.Point result = new org.kie.dmn.model.v1_2.dmndi.Point();
        result.setX(point2d.getX());
        result.setY(point2d.getY());
        return result;
    }

    public static org.kie.workbench.common.stunner.core.graph.content.view.Point2D dmndiPointToPoint2D(org.kie.dmn.model.api.dmndi.Point dmndiPoint) {
        org.kie.workbench.common.stunner.core.graph.content.view.Point2D result = new org.kie.workbench.common.stunner.core.graph.content.view.Point2D(dmndiPoint.getX(), dmndiPoint.getY());
        return result;
    }
}
