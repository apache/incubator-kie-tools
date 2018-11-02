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
import java.util.UUID;

import com.ait.lienzo.client.core.shape.Rectangle;
import com.ait.lienzo.client.core.shape.wires.WiresShape;
import org.kie.workbench.common.stunner.cm.client.shape.view.CaseManagementShapeView;
import org.kie.workbench.common.stunner.svg.client.shape.view.SVGPrimitiveShape;

import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.spy;

public abstract class BaseLayoutManagerTest {

    protected static final double PADDING = 5.0;

    protected CaseManagementShapeView container;

    protected List<WiresShape> shapes;

    protected AbstractNestedLayoutHandler handler;

    public void setup() {
        this.shapes = new ArrayList<>();
        this.handler = getLayoutHandler();
        this.container = spy(new CaseManagementShapeView("mockCaseMgmtShapeView",
                                                         new SVGPrimitiveShape(new Rectangle(0d, 0d)),
                                                         0d,
                                                         0d,
                                                         false));
        this.container.setUUID(UUID.randomUUID().toString());
        this.container.setLayoutHandler(handler);

        //Shapes are at (0,15), (0,30) and (0,45) by default. Set by LayoutManager.
        for (int i = 0; i < 3; i++) {
            final CaseManagementShapeView shape = new CaseManagementShapeView("mockChildCaseMgmtShapeView" + i,
                                                                              new SVGPrimitiveShape(new Rectangle(0d, 0d)),
                                                                              0d,
                                                                              0d,
                                                                              false);
            shape.setUUID(UUID.randomUUID().toString());
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
