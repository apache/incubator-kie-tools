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

import com.ait.lienzo.client.core.shape.Rectangle;
import com.ait.lienzo.test.LienzoMockitoTestRunner;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.kie.workbench.common.stunner.cm.client.shape.view.CaseManagementDiagramShapeView;
import org.kie.workbench.common.stunner.cm.client.shape.view.CaseManagementShapeView;
import org.kie.workbench.common.stunner.cm.client.shape.view.CaseManagementStageShapeView;
import org.kie.workbench.common.stunner.svg.client.shape.view.SVGPrimitiveShape;
import org.kie.workbench.common.stunner.svg.client.shape.view.SVGShapeView;

import static org.junit.Assert.assertTrue;

@RunWith(LienzoMockitoTestRunner.class)
public class CaseManagementSVGViewBuilderTest {

    private CaseManagementSVGViewBuilder tested = new CaseManagementSVGViewBuilder();

    @Test
    public void testBuild_rectangle() throws Exception {
        final SVGShapeView shapeView = tested.build("rectangle",
                                                    new SVGPrimitiveShape(new Rectangle(0d, 0d)),
                                                    10d,
                                                    10d,
                                                    false);

        assertTrue(shapeView instanceof CaseManagementDiagramShapeView);
    }

    @Test
    public void testBuild_stage() throws Exception {
        final SVGShapeView shapeView = tested.build("stage",
                                                    new SVGPrimitiveShape(new Rectangle(0d, 0d)),
                                                    10d,
                                                    10d,
                                                    false);

        assertTrue(shapeView instanceof CaseManagementStageShapeView);
    }

    @Test
    public void testBuild_subcase() throws Exception {
        final SVGShapeView shapeView = tested.build("subcase",
                                                    new SVGPrimitiveShape(new Rectangle(0d, 0d)),
                                                    10d,
                                                    10d,
                                                    false);

        assertTrue(shapeView instanceof CaseManagementShapeView);
    }

    @Test
    public void testBuild_subprocess() throws Exception {
        final SVGShapeView shapeView = tested.build("subprocess",
                                                    new SVGPrimitiveShape(new Rectangle(0d, 0d)),
                                                    10d,
                                                    10d,
                                                    false);

        assertTrue(shapeView instanceof CaseManagementShapeView);
    }

    @Test
    public void testBuild_task() throws Exception {
        final SVGShapeView shapeView = tested.build("task",
                                                    new SVGPrimitiveShape(new Rectangle(0d, 0d)),
                                                    10d,
                                                    10d,
                                                    false);

        assertTrue(shapeView instanceof CaseManagementShapeView);
    }
}