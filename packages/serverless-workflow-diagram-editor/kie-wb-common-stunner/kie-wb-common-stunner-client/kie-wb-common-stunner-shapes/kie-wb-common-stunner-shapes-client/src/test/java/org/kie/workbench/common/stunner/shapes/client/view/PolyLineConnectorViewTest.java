/*
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 *
 *  http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied.  See the License for the
 * specific language governing permissions and limitations
 * under the License.
 */


package org.kie.workbench.common.stunner.shapes.client.view;

import com.ait.lienzo.client.core.shape.MultiPath;
import com.ait.lienzo.client.core.shape.MultiPathDecorator;
import com.ait.lienzo.client.core.types.BoundingBox;
import com.ait.lienzo.test.LienzoMockitoTestRunner;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.kie.workbench.common.stunner.shapes.client.factory.PolyLineConnectorFactory;
import org.kie.workbench.common.stunner.shapes.def.ConnectorShapeDef;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotEquals;

@RunWith(LienzoMockitoTestRunner.class)
public class PolyLineConnectorViewTest {

    @Test
    public void testCreateLine() {

        double[] points = new double[]{0, 0, 10, 10};
        PolyLineConnectorFactory factory = new PolyLineConnectorFactory();
        Object[] line = PolylineConnectorView.createLine(factory, ConnectorShapeDef.Direction.ONE, points);

        MultiPathDecorator head = (MultiPathDecorator) line[1];
        MultiPath headPath = head.getPath();
        BoundingBox headBoundingBox = headPath.getBoundingBox();

        MultiPathDecorator tail = (MultiPathDecorator) line[2];
        MultiPath tailPath = tail.getPath();
        BoundingBox tailBoundingBox = tailPath.getBoundingBox();

        assertEquals(0, headBoundingBox.getWidth(), 0);
        assertEquals(0, headBoundingBox.getHeight(), 0);

        assertNotEquals(0, tailBoundingBox.getWidth(), 0);
        assertNotEquals(0, tailBoundingBox.getHeight(), 0);
    }
}
