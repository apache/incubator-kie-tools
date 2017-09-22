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

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import com.ait.lienzo.client.core.shape.MultiPath;
import com.ait.lienzo.client.core.shape.wires.WiresShape;

import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.spy;

public abstract class BaseLayoutManagerTest {

    protected static final double PADDING = 5.0;

    protected AbstractCaseManagementShape container;

    protected List<WiresShape> shapes;

    protected AbstractNestedLayoutHandler handler;

    public void setup() {
        this.shapes = new ArrayList<>();
        this.handler = getLayoutHandler();
        this.container = spy(new MockCaseManagementShape());
        this.container.setLayoutHandler(handler);

        //Shapes are at (0,15), (0,30) and (0,45) by default. Set by LayoutManager.
        for (int i = 0; i < 3; i++) {
            final int index = i;
            final WiresShape shape = new WiresShape(new MultiPath()) {
                @Override
                public String toString() {
                    return "WiresShape:" + index;
                }
            };
            container.add(shape);
            shapes.add(shape);
        }
    }

    protected abstract AbstractNestedLayoutHandler getLayoutHandler();

    protected void assertChildrenOrder(final Check... checks) {
        Arrays.asList(checks).stream().forEach(check -> assertEquals(shapes.get(check.shapeIndex),
                                                                     container.getChildShapes().toList().get(check.containerShapeIndex)));
    }

    protected class Check {

        private int shapeIndex;

        private int containerShapeIndex;

        public Check(final int shapeIndex,
                     final int containerShapeIndex) {
            this.shapeIndex = shapeIndex;
            this.containerShapeIndex = containerShapeIndex;
        }
    }
}
