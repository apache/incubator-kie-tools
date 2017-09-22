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

package org.kie.workbench.common.stunner.client.lienzo.shape.view.wires;

import java.util.List;

import com.ait.lienzo.client.core.shape.Group;
import com.ait.lienzo.client.core.shape.MultiPath;
import com.ait.lienzo.client.core.shape.wires.LayoutContainer;
import com.ait.lienzo.client.core.types.Point2D;
import com.ait.lienzo.client.widget.DragConstraintEnforcer;
import com.ait.lienzo.test.LienzoMockitoTestRunner;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.kie.workbench.common.stunner.client.lienzo.canvas.wires.WiresUtils;
import org.kie.workbench.common.stunner.lienzo.core.shape.wires.WiresDragConstraintEnforcer;
import org.mockito.Mock;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;
import static org.mockito.Matchers.any;
import static org.mockito.Matchers.anyDouble;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@RunWith(LienzoMockitoTestRunner.class)
public class WiresShapeViewTest {

    private final static MultiPath PATH = new MultiPath();

    @Mock
    private DragConstraintEnforcer enforcer;

    private WiresShapeView tested;

    @Before
    public void setup() throws Exception {
        this.tested = new WiresShapeView(PATH);
        this.tested.getGroup().setDragConstraints(enforcer);
        assertEquals(PATH,
                     tested.getShape());
    }

    @Test
    public void testUUID() {
        final String uuid = "uuid";
        tested.setUUID(uuid);
        assertTrue(tested.getContainer().getUserData() instanceof WiresUtils.UserData);
        assertEquals(uuid,
                     tested.getUUID());
        assertEquals(uuid,
                     ((WiresUtils.UserData) tested.getContainer().getUserData()).getUuid());
    }

    @Test
    public void testCoordinates() {
        tested.setShapeX(50.5);
        tested.setShapeY(321.65);
        assertEquals(50.5,
                     tested.getShapeX(),
                     0d);
        assertEquals(321.65,
                     tested.getShapeY(),
                     0d);
    }

    @Test
    public void testAlpha() {
        tested.setAlpha(0.53);
        assertEquals(0.53,
                     tested.getAlpha(),
                     0d);
    }

    @Test
    public void testFillAttributes() {
        tested.setFillColor("color1");
        tested.setFillAlpha(0.53);
        assertEquals("color1",
                     tested.getFillColor());
        assertEquals(0.53,
                     tested.getFillAlpha(),
                     0d);
    }

    @Test
    public void testStrokeAttributes() {
        tested.setStrokeColor("color1");
        tested.setStrokeWidth(3.89);
        tested.setStrokeAlpha(0.53);
        assertEquals("color1",
                     tested.getStrokeColor());
        assertEquals(3.89,
                     tested.getStrokeWidth(),
                     0d);
        assertEquals(0.53,
                     tested.getStrokeAlpha(),
                     0d);
    }

    @Test
    public void testSetDragBounds() {
        tested.setDragBounds(1.5,
                             6.4,
                             564.78,
                             543.84);
        final WiresDragConstraintEnforcer dragEnforcer = tested.getDragEnforcer();
        assertNotNull(dragEnforcer);
        assertTrue(dragEnforcer.getDelegate().isPresent());
        assertEquals(dragEnforcer,
                     tested.getGroup().getDragConstraints());
        assertEquals(enforcer,
                     dragEnforcer.getDelegate().get());
    }

    @Test
    public void testUnSetDragBounds() {
        tested.setDragBounds(1.5,
                             6.4,
                             564.78,
                             543.84);
        tested.unsetDragBounds();
        assertNull(tested.getDragEnforcer());
        assertEquals(enforcer,
                     tested.getGroup().getDragConstraints());
    }

    @Test
    public void testMove() {
        final LayoutContainer container = mock(LayoutContainer.class);
        final Group group = mock(Group.class);
        when(container.getGroup()).thenReturn(group);
        when(container.refresh()).thenReturn(container);
        when(container.execute()).thenReturn(container);
        when(container.setOffset(any(Point2D.class))).thenReturn(container);
        when(container.setSize(anyDouble(),
                               anyDouble())).thenReturn(container);
        this.tested = new WiresShapeView(PATH,
                                         container);
        tested.moveToTop();
        tested.moveToBottom();
        tested.moveUp();
        tested.moveDown();
        verify(group,
               times(1)).moveToTop();
        verify(group,
               times(1)).moveToBottom();
        verify(group,
               times(1)).moveUp();
        verify(group,
               times(1)).moveDown();
    }

    @Test
    public void testDecorators() {
        final List decorators = tested.getDecorators();
        assertNotNull(decorators);
        assertEquals(1,
                     decorators.size());
        assertEquals(PATH,
                     decorators.get(0));
    }

    @Test
    public void testDestroy() {
        tested.destroy();
        assertNull(tested.getParent());
        assertNull(tested.getDragEnforcer());
    }
}
