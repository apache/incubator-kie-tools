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

package org.kie.workbench.common.stunner.lienzo.core.shape.wires;

import com.ait.lienzo.client.core.shape.Group;
import com.ait.lienzo.client.core.shape.wires.WiresContainer;
import com.ait.lienzo.client.core.types.BoundingBox;
import com.ait.lienzo.client.core.types.DragBounds;
import com.ait.lienzo.client.core.types.Point2D;
import com.ait.lienzo.client.widget.DragConstraintEnforcer;
import com.ait.lienzo.client.widget.DragContext;
import com.ait.lienzo.test.LienzoMockitoTestRunner;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import static org.mockito.Matchers.any;
import static org.mockito.Matchers.eq;
import static org.mockito.Matchers.isNull;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.spy;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@RunWith(LienzoMockitoTestRunner.class)
public class WiresDragConstraintEnforcerTest {

    @Mock
    private WiresContainer container;

    @Mock
    private BoundingBox boundingBox;

    @Mock
    private DragBounds dragBounds;

    @Mock
    private DragContext dragContext;

    @Mock
    private Group group;

    @Mock
    private DragConstraintEnforcer enforcer;

    private WiresDragConstraintEnforcer tested;

    @Before
    public void setUp() {
        when(container.getGroup()).thenReturn(group);
        when(group.getDragConstraints()).thenReturn(enforcer);
        when(enforcer.adjust(any(Point2D.class))).thenReturn(false);
        tested = WiresDragConstraintEnforcer.enforce(container,
                                                     dragBounds);
    }

    @Test
    public void testEnforce() {
        Group group1 = spy(new Group()).setX(10).setY(10);
        when(container.getGroup()).thenReturn(group1);
        when(group1.getDragConstraints()).thenReturn(enforcer);
        when(group1.getBoundingBox()).thenReturn(boundingBox);
        when(boundingBox.getX()).thenReturn(10d);
        when(boundingBox.getY()).thenReturn(10d);
        when(boundingBox.getWidth()).thenReturn(100d);
        when(boundingBox.getHeight()).thenReturn(100d);
        when(dragBounds.getX1()).thenReturn(0d);
        when(dragBounds.getY1()).thenReturn(0d);
        when(dragBounds.getX2()).thenReturn(300d);
        when(dragBounds.getY2()).thenReturn(300d);
        tested = WiresDragConstraintEnforcer.enforce(container,
                                                     dragBounds);
        tested.startDrag(dragContext);
        // Adjust allowed.
        final Point2D point1 = new Point2D(0,
                                           0);
        final boolean adjusted1 = tested.adjust(point1);
        assertFalse(adjusted1);
        verify(enforcer,
               times(1)).adjust(eq(point1));
        verify(enforcer,
               times(1)).startDrag(eq(dragContext));
        // Adjust failed - X axis value exceeded.
        final Point2D point2 = new Point2D(301,
                                           20);
        final boolean adjusted2 = tested.adjust(point2);
        assertTrue(adjusted2);
        verify(enforcer,
               never()).adjust(eq(point2));
        // Adjust failed - Y axis value exceeded.
        final Point2D point3 = new Point2D(20,
                                           301);
        final boolean adjusted3 = tested.adjust(point3);
        assertTrue(adjusted3);
        verify(enforcer,
               never()).adjust(eq(point3));
        // Adjust failed - X + width value exceeded.
        final Point2D point4 = new Point2D(270,
                                           20);
        final boolean adjusted4 = tested.adjust(point4);
        assertTrue(adjusted4);
        verify(enforcer,
               never()).adjust(eq(point4));
        // Adjust failed - Y + height value exceeded.
        final Point2D point5 = new Point2D(20,
                                           270);
        final boolean adjusted5 = tested.adjust(point5);
        assertTrue(adjusted5);
        verify(enforcer,
               never()).adjust(eq(point5));
    }

    @Test
    public void testRemove() {
        tested.remove();
        verify(group,
               times(1)).setDragConstraints(eq(enforcer));
    }

    @Test
    public void testRemoveNoDelegate() {
        when(group.getDragConstraints()).thenReturn(null);
        tested = WiresDragConstraintEnforcer.enforce(container,
                                                     dragBounds);
        tested.remove();
        verify(group,
               times(1)).setDragConstraints(isNull(DragConstraintEnforcer.class));
    }

    @Test(expected = IllegalStateException.class)
    public void testCheckBounds() {
        tested = WiresDragConstraintEnforcer.enforce(container,
                                                     null);
        tested.startDrag(dragContext);
    }
}
