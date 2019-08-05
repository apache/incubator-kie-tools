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

package org.kie.workbench.common.stunner.bpmn.backend.converters.customproperties;

import java.util.Locale;

import org.eclipse.bpmn2.BaseElement;
import org.junit.Test;
import org.kie.workbench.common.stunner.bpmn.definition.property.diagram.Package;
import org.kie.workbench.common.stunner.core.graph.content.view.Point2D;

import static org.junit.Assert.assertEquals;
import static org.kie.workbench.common.stunner.bpmn.backend.converters.fromstunner.Factories.bpmn2;

public class CustomAttributeTest {

    private static final double POINT2D_X_COORDINATE = 1.0;
    private static final double POINT2D_Y_COORDINATE = 2.0;

    @Test
    public void packageNameSetEmpty() {
        BaseElement baseElement = createBaseElement();
        CustomAttribute<String> packageName =
                CustomAttribute.packageName.of(baseElement);
        packageName.set("");
        assertEquals(Package.DEFAULT_PACKAGE, packageName.get());
    }

    @Test
    public void packageNameSetNull() {
        BaseElement baseElement = createBaseElement();
        CustomAttribute<String> packageName =
                CustomAttribute.packageName.of(baseElement);
        packageName.set(null);
        assertEquals(null, packageName.get());
    }

    @Test
    public void dockerInfoSet() {
        CustomAttribute<Point2D> dockerInfo = createDockerInfoAttribute();
        Point2D point2D = new Point2D(POINT2D_X_COORDINATE, POINT2D_Y_COORDINATE);
        dockerInfo.set(point2D);
        assertEquals(point2D, dockerInfo.get());
    }

    @Test
    public void dockerInfoSetLocalization() {
        Locale defaultLocale = Locale.getDefault();
        Locale.setDefault(new Locale("cs", "CZ"));

        CustomAttribute<Point2D> dockerInfo = createDockerInfoAttribute();
        Point2D point2D = new Point2D(POINT2D_X_COORDINATE, POINT2D_Y_COORDINATE);
        dockerInfo.set(point2D);
        assertEquals(point2D, dockerInfo.get());

        Locale.setDefault(defaultLocale);
    }

    private CustomAttribute<Point2D> createDockerInfoAttribute() {
        BaseElement baseElement = bpmn2.createBoundaryEvent();
        return CustomAttribute.dockerInfo.of(baseElement);
    }

    private BaseElement createBaseElement() {
        return bpmn2.createTask();
    }
}