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

package org.kie.workbench.common.stunner.cm.client.wires;

import com.ait.lienzo.client.core.shape.wires.WiresShape;
import com.ait.lienzo.client.core.types.Point2D;
import com.ait.lienzo.test.LienzoMockitoTestRunner;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;

import static org.junit.Assert.assertEquals;

@RunWith(LienzoMockitoTestRunner.class)
public class VerticalStackLayoutManagerTest extends BaseLayoutManagerTest {

    @Before
    public void setup() {
        super.setup();
    }

    @Override
    protected AbstractNestedLayoutHandler getLayoutHandler() {
        return new VerticalStackLayoutManager();
    }

    @Test
    public void checkLayout() {
        assertEquals(VerticalStackLayoutManager.PADDING_Y,
                     shapes.get(0).getY(),
                     0.0);
        assertEquals(VerticalStackLayoutManager.PADDING_Y * 2,
                     shapes.get(1).getY(),
                     0.0);
        assertEquals(VerticalStackLayoutManager.PADDING_Y * 3,
                     shapes.get(2).getY(),
                     0.0);
    }

    @Test
    public void orderChildrenInsertShape0At0() {
        //Mouse Y needs to be before target index Y as insertion happens when Shape is dragged before target
        addShapeAtIndex(shapes.get(0),
                        shapes.get(0).getY() - PADDING);
        assertChildrenOrder(new Check(0,
                                      0),
                            new Check(1,
                                      1),
                            new Check(2,
                                      2));
    }

    @Test
    public void orderChildrenInsertShape0At1() {
        //Mouse Y needs to be after target index Y as insertion happens when Shape is dragged after target
        addShapeAtIndex(shapes.get(0),
                        shapes.get(1).getY() + PADDING);
        assertChildrenOrder(new Check(0,
                                      1),
                            new Check(1,
                                      0),
                            new Check(2,
                                      2));
    }

    @Test
    public void orderChildrenInsertShape0At2() {
        //Mouse Y needs to be after target index Y as insertion happens when Shape is dragged after target
        addShapeAtIndex(shapes.get(0),
                        shapes.get(2).getY() + PADDING);
        assertChildrenOrder(new Check(0,
                                      2),
                            new Check(1,
                                      0),
                            new Check(2,
                                      1));
    }

    @Test
    public void orderChildrenInsertShape1At0() {
        //Mouse Y needs to be before target index Y as insertion happens when Shape is dragged before target
        addShapeAtIndex(shapes.get(1),
                        shapes.get(0).getY() - PADDING);
        assertChildrenOrder(new Check(0,
                                      1),
                            new Check(1,
                                      0),
                            new Check(2,
                                      2));
    }

    @Test
    public void orderChildrenInsertShape1At1() {
        //Mouse Y needs to be after target index Y as insertion happens when Shape is dragged after target
        addShapeAtIndex(shapes.get(1),
                        shapes.get(1).getY() + PADDING);
        assertChildrenOrder(new Check(0,
                                      0),
                            new Check(1,
                                      1),
                            new Check(2,
                                      2));
    }

    @Test
    public void orderChildrenInsertShape1At2() {
        //Mouse Y needs to be after target index Y as insertion happens when Shape is dragged after target
        addShapeAtIndex(shapes.get(1),
                        shapes.get(2).getY() + PADDING);
        assertChildrenOrder(new Check(0,
                                      0),
                            new Check(1,
                                      2),
                            new Check(2,
                                      1));
    }

    @Test
    public void orderChildrenInsertShape2At0() {
        //Mouse Y needs to be before target index Y as insertion happens when Shape is dragged before target
        addShapeAtIndex(shapes.get(2),
                        shapes.get(0).getY() - PADDING);
        assertChildrenOrder(new Check(0,
                                      1),
                            new Check(1,
                                      2),
                            new Check(2,
                                      0));
    }

    @Test
    public void orderChildrenInsertShape2At1() {
        //Mouse Y needs to be before target index Y as insertion happens when Shape is dragged before target
        addShapeAtIndex(shapes.get(2),
                        shapes.get(1).getY() - PADDING);
        assertChildrenOrder(new Check(0,
                                      0),
                            new Check(1,
                                      2),
                            new Check(2,
                                      1));
    }

    @Test
    public void orderChildrenInsertShape2At2() {
        //Mouse Y needs to be after target index Y as insertion happens when Shape is dragged after target
        addShapeAtIndex(shapes.get(2),
                        shapes.get(2).getY() + PADDING);
        assertChildrenOrder(new Check(0,
                                      0),
                            new Check(1,
                                      1),
                            new Check(2,
                                      2));
    }

    private void addShapeAtIndex(final WiresShape shape,
                                 final double mouseY) {
        final Point2D mouseLoc = new Point2D(0,
                                             mouseY);

        handler.orderChildren(shape,
                              container,
                              mouseLoc);
    }
}
