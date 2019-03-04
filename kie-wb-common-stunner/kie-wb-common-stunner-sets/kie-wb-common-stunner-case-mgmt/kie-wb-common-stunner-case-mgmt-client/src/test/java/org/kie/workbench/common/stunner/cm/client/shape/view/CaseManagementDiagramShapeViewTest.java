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

import java.util.Optional;
import java.util.UUID;

import com.ait.lienzo.client.core.shape.MultiPath;
import com.ait.lienzo.client.core.shape.Rectangle;
import com.ait.lienzo.client.core.shape.Shape;
import com.ait.lienzo.client.widget.panel.Bounds;
import com.ait.lienzo.test.LienzoMockitoTestRunner;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.kie.workbench.common.stunner.cm.client.canvas.CaseManagementCanvas;
import org.kie.workbench.common.stunner.cm.client.wires.HorizontalStackLayoutManager;
import org.kie.workbench.common.stunner.svg.client.shape.view.SVGPrimitiveShape;
import org.mockito.Mock;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotEquals;
import static org.junit.Assert.assertTrue;
import static org.mockito.Mockito.when;

@RunWith(LienzoMockitoTestRunner.class)
public class CaseManagementDiagramShapeViewTest {

    @Mock
    private CaseManagementCanvas canvas;

    private CaseManagementDiagramShapeView tested;

    @Before
    public void setUp() throws Exception {
        Shape primitiveShapes = new MockShape();
        primitiveShapes.setID(UUID.randomUUID().toString());

        tested = new CaseManagementDiagramShapeView("",
                                                    new SVGPrimitiveShape(primitiveShapes),
                                                    10d,
                                                    10d,
                                                    false);
        tested.setUUID(UUID.randomUUID().toString());

        when(canvas.getPanelBounds()).thenReturn(Optional.empty());
        tested.setCanvas(canvas);
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

    @Test
    public void testGetDropZone() throws Exception {
        Optional<MultiPath> dropZone1 = tested.getDropZone();

        assertTrue(dropZone1.isPresent());

        when(canvas.getPanelBounds()).thenReturn(Optional.of(Bounds.build(0.0, 0.0, 2000.0, 1000.0)));

        Optional<MultiPath> dropZone2 = tested.getDropZone();

        assertTrue(dropZone2.isPresent());
        assertNotEquals(dropZone1, dropZone2);
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