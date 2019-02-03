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

package org.kie.workbench.common.stunner.cm.client.shape.view;

import java.util.UUID;

import com.ait.lienzo.client.core.shape.Rectangle;
import com.ait.lienzo.client.core.shape.Shape;
import com.ait.lienzo.test.LienzoMockitoTestRunner;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.kie.workbench.common.stunner.cm.client.wires.HorizontalStackLayoutManager;
import org.kie.workbench.common.stunner.svg.client.shape.view.SVGPrimitiveShape;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

@RunWith(LienzoMockitoTestRunner.class)
public class CaseManagementDiagramShapeViewTest {

    private CaseManagementDiagramShapeView tested;

    @Before
    public void setUp() throws Exception {
        Shape primitiveShapes = new MockShape();
        primitiveShapes.setID(UUID.randomUUID().toString());

        tested = new CaseManagementDiagramShapeView("",
                                                    new SVGPrimitiveShape(primitiveShapes),
                                                    0d,
                                                    0d,
                                                    false);
        tested.setUUID(UUID.randomUUID().toString());
    }

    @Test
    public void testGetGhost() throws Exception {
        Shape childPrimitiveShapes = new MockShape();
        childPrimitiveShapes.setID(UUID.randomUUID().toString());
        CaseManagementStageShapeView child = new CaseManagementStageShapeView("child",
                                                                              new SVGPrimitiveShape(childPrimitiveShapes),
                                                                              0d,
                                                                              0d,
                                                                              false);
        child.setUUID(UUID.randomUUID().toString());
        tested.add(child);

        Shape grandchildPrimitiveShapes = new MockShape();
        grandchildPrimitiveShapes.setID(UUID.randomUUID().toString());
        CaseManagementShapeView grandchild = new CaseManagementShapeView("grandchild",
                                                                         new SVGPrimitiveShape(grandchildPrimitiveShapes),
                                                                         0d,
                                                                         0d,
                                                                         false);
        grandchild.setUUID(UUID.randomUUID().toString());
        child.add(grandchild);

        CaseManagementShapeView ghost = tested.getGhost();

        assertTrue(ghost instanceof CaseManagementDiagramShapeView);

        assertTrue(ghost.getLayoutHandler() instanceof HorizontalStackLayoutManager);
        assertEquals(tested.getUUID(), ghost.getUUID());

        assertTrue(ghost.getChildShapes().size() == 1);
        CaseManagementShapeView ghostChild = (CaseManagementShapeView) ghost.getChildShapes().toList().get(0);
        assertTrue(ghostChild instanceof CaseManagementStageShapeView);
        assertEquals(ghostChild.getUUID(), child.getUUID());

        assertTrue(ghostChild.getChildShapes().size() == 1);
        CaseManagementShapeView ghostGrandchild = (CaseManagementShapeView) ghostChild.getChildShapes().toList().get(0);
        assertEquals(ghostGrandchild.getUUID(), grandchild.getUUID());
    }

    private static class MockShape extends Rectangle {

        public MockShape() {
            super(10d, 10d);
        }

        @Override
        public Rectangle copy() {
            return this;
        }
    }
}