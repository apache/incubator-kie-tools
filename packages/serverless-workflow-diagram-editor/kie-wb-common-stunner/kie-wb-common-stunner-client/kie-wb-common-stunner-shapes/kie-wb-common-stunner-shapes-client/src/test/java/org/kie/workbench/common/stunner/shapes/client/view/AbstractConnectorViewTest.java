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

import com.ait.lienzo.client.core.shape.AbstractDirectionalMultiPointShape;
import com.ait.lienzo.client.core.shape.MultiPathDecorator;
import com.ait.lienzo.client.core.types.BoundingBox;
import com.ait.lienzo.client.core.types.Point2DArray;
import com.ait.lienzo.test.LienzoMockitoTestRunner;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.kie.workbench.common.stunner.shapes.client.factory.LineConnectorFactory;
import org.kie.workbench.common.stunner.shapes.def.ConnectorShapeDef;
import org.mockito.Mock;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

@RunWith(LienzoMockitoTestRunner.class)
public class AbstractConnectorViewTest {

    @Mock
    private LineConnectorFactory lineFactory;

    @Mock
    private AbstractDirectionalMultiPointShape line;

    @Before
    public void setUp() throws Exception {
        when(lineFactory.createLine(any(Point2DArray.class))).thenReturn(line);
    }

    @Test
    public void createLineOne() {
        Object[] line = AbstractConnectorView.createLine(lineFactory, ConnectorShapeDef.Direction.ONE, 0d, 1d);
        MultiPathDecorator headDecorator = (MultiPathDecorator) line[1];
        MultiPathDecorator tailDecorator = (MultiPathDecorator) line[2];
        assertEquals(headDecorator.getPath().getBoundingBox(), emptyBoundingBox());
        assertNotEquals(tailDecorator.getPath().getBoundingBox(), emptyBoundingBox());
    }

    @Test
    public void createLineBoth() {
        Object[] line = AbstractConnectorView.createLine(lineFactory, ConnectorShapeDef.Direction.BOTH, 0d, 1d);
        MultiPathDecorator headDecorator = (MultiPathDecorator) line[1];
        MultiPathDecorator tailDecorator = (MultiPathDecorator) line[2];
        assertNotEquals(headDecorator.getPath().getBoundingBox(), emptyBoundingBox());
        assertNotEquals(tailDecorator.getPath().getBoundingBox(), emptyBoundingBox());
    }

    @Test
    public void createLineNone() {
        Object[] line = AbstractConnectorView.createLine(lineFactory, ConnectorShapeDef.Direction.NONE, 0d, 1d);
        MultiPathDecorator headDecorator = (MultiPathDecorator) line[1];
        MultiPathDecorator tailDecorator = (MultiPathDecorator) line[2];
        assertEquals(headDecorator.getPath().getBoundingBox(), emptyBoundingBox());
        assertEquals(tailDecorator.getPath().getBoundingBox(), emptyBoundingBox());
    }

    @Test
    public void createLineNull() {
        Object[] line = AbstractConnectorView.createLine(lineFactory, null, 0d, 1d);
        MultiPathDecorator headDecorator = (MultiPathDecorator) line[1];
        MultiPathDecorator tailDecorator = (MultiPathDecorator) line[2];
        assertEquals(headDecorator.getPath().getBoundingBox(), emptyBoundingBox());
        assertEquals(tailDecorator.getPath().getBoundingBox(), emptyBoundingBox());
    }

    private BoundingBox emptyBoundingBox() {
        return BoundingBox.fromDoubles(0, 0, 0, 0);
    }
}
