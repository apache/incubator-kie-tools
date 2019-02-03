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
import com.ait.lienzo.client.core.shape.wires.ILayoutHandler;
import com.ait.lienzo.client.core.shape.wires.WiresShape;
import com.ait.lienzo.test.LienzoMockitoTestRunner;
import com.google.common.collect.Lists;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.kie.workbench.common.stunner.svg.client.shape.view.SVGPrimitiveShape;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;
import static org.mockito.Mockito.atLeast;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.reset;
import static org.mockito.Mockito.spy;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

@RunWith(LienzoMockitoTestRunner.class)
public class CaseManagementShapeViewTest {

    @Mock
    private ILayoutHandler layoutHandler;

    private CaseManagementShapeView shape;

    private CaseManagementShapeView createShapeView(String name) {
        final Shape primitiveShapes = new MockShape();
        primitiveShapes.setID(UUID.randomUUID().toString());

        final CaseManagementShapeView view = new CaseManagementShapeView(name,
                                                                         new SVGPrimitiveShape(primitiveShapes),
                                                                         0d,
                                                                         0d,
                                                                         false);
        view.setUUID(UUID.randomUUID().toString());

        return spy(view);
    }

    @Before
    public void setup() {
        this.shape = createShapeView("shape");
        this.shape.setLayoutHandler(layoutHandler);
    }

    @Test
    public void checkLogicalReplacementWithOneChild() {
        final CaseManagementShapeView child = createShapeView("child");
        final CaseManagementShapeView replacement = createShapeView("replacement");

        shape.add(child);

        verify(layoutHandler,
               times(1)).requestLayout(shape);

        assertEquals(1,
                     shape.getChildShapes().size());
        assertEquals(child,
                     shape.getChildShapes().get(0));
        assertEquals(shape,
                     child.getParent());

        shape.logicallyReplace(child,
                               replacement);

        assertEquals(1,
                     shape.getChildShapes().size());
        assertEquals(replacement,
                     shape.getChildShapes().get(0));
        assertEquals(shape,
                     replacement.getParent());
        assertNull(child.getParent());

        verify(layoutHandler,
               times(2)).requestLayout(shape);
    }

    @Test
    public void checkLogicalReplacementWithMultipleChildren() {
        final CaseManagementShapeView child1 = createShapeView("child1");
        final CaseManagementShapeView child2 = createShapeView("child2");
        final CaseManagementShapeView replacement = createShapeView("replacement");

        shape.add(child1);
        shape.add(child2);

        verify(layoutHandler,
               times(2)).requestLayout(shape);

        assertEquals(2,
                     shape.getChildShapes().size());
        assertEquals(child1,
                     shape.getChildShapes().get(0));
        assertEquals(child2,
                     shape.getChildShapes().get(1));
        assertEquals(shape,
                     child1.getParent());
        assertEquals(shape,
                     child2.getParent());

        shape.logicallyReplace(child1,
                               replacement);

        assertEquals(2,
                     shape.getChildShapes().size());
        assertEquals(replacement,
                     shape.getChildShapes().get(0));
        assertEquals(child2,
                     shape.getChildShapes().get(1));
        assertEquals(shape,
                     replacement.getParent());
        assertNull(child1.getParent());

        verify(layoutHandler,
               times(3)).requestLayout(shape);
    }

    @Test
    public void checkAddShapeAtIndex0WithNoExistingChildren() {
        final CaseManagementShapeView child = createShapeView("child");

        shape.addShape(child,
                       0);

        verify(layoutHandler,
               times(1)).requestLayout(shape);

        assertEquals(1,
                     shape.getChildShapes().size());
        assertEquals(child,
                     shape.getChildShapes().get(0));
    }

    @Test
    public void checkAddShapeAtIndex1WithNoExistingChildren() {
        final CaseManagementShapeView child = createShapeView("child");

        shape.addShape(child,
                       1);

        verify(layoutHandler,
               never()).requestLayout(shape);

        assertEquals(0,
                     shape.getChildShapes().size());
    }

    @Test
    public void checkAddShapeAtNegativeIndexWithNoExistingChildren() {
        final CaseManagementShapeView child = createShapeView("child");

        shape.addShape(child,
                       -1);

        verify(layoutHandler,
               never()).requestLayout(shape);

        assertEquals(0,
                     shape.getChildShapes().size());
    }

    @Test
    public void testGetIndex() throws Exception {
        final CaseManagementShapeView child1 = createShapeView("child1");
        final CaseManagementShapeView child2 = createShapeView("child2");

        shape.add(child1);
        shape.add(child2);

        assertEquals(shape.getIndex(child1),
                     0);
        assertEquals(shape.getIndex(child2),
                     1);
    }

    @Test
    public void testGetGhost() throws Exception {
        final CaseManagementShapeView child = createShapeView("child");
        shape.add(child);

        final CaseManagementShapeView ghost = shape.getGhost();

        assertTrue(ILayoutHandler.NONE.equals(ghost.getLayoutHandler()));
        assertEquals(ghost.getUUID(),
                     shape.getUUID());

        assertTrue(ghost.getChildShapes().size() == 1);
        CaseManagementShapeView ghostChild = (CaseManagementShapeView) ghost.getChildShapes().toList().get(0);
        assertEquals(ghostChild.getUUID(),
                     child.getUUID());
    }

    @Test
    public void testAddShape() throws Exception {
        final CaseManagementShapeView child1 = createShapeView("child1");
        final CaseManagementShapeView child2 = createShapeView("child2");
        final CaseManagementShapeView child3 = createShapeView("child2");

        {
            shape.addShape(child1,
                           0);

            verify(shape,
                   times(1)).add(child1);

            verify(layoutHandler,
                   times(1)).requestLayout(shape);

            assertEquals(1,
                         shape.getChildShapes().size());
        }

        {
            reset(shape);
            reset(layoutHandler);

            shape.addShape(child2,
                           0);

            final ArgumentCaptor<WiresShape> childShapes = ArgumentCaptor.forClass(WiresShape.class);

            verify(shape,
                   times(2)).add(childShapes.capture());

            assertTrue(childShapes.getAllValues().containsAll(Lists.newArrayList(child1,
                                                                                 child2)));

            verify(layoutHandler,
                   atLeast(2)).requestLayout(shape);

            assertEquals(2,
                         shape.getChildShapes().size());
        }

        {
            reset(shape);
            reset(layoutHandler);

            shape.addShape(child3,
                           1);

            final ArgumentCaptor<WiresShape> childShapes = ArgumentCaptor.forClass(WiresShape.class);

            verify(shape,
                   times(3)).add(childShapes.capture());

            assertTrue(childShapes.getAllValues().containsAll(Lists.newArrayList(child1,
                                                                                 child2,
                                                                                 child3)));

            verify(layoutHandler,
                   atLeast(3)).requestLayout(shape);

            assertEquals(3,
                         shape.getChildShapes().size());
        }

        assertEquals(shape.getIndex(child1),
                     2);
        assertEquals(shape.getIndex(child2),
                     0);
        assertEquals(shape.getIndex(child3),
                     1);
    }

    private static class MockShape extends Rectangle {

        public MockShape() {
            super(0d, 0d);
        }

        @Override
        public Rectangle copy() {
            return this;
        }
    }
}
