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

package org.kie.workbench.common.dmn.client.shape.view.connections;

import java.util.Objects;

import com.ait.lienzo.client.core.shape.PolyLine;
import com.ait.lienzo.test.LienzoMockitoTestRunner;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InOrder;
import org.mockito.Mockito;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import static org.kie.workbench.common.dmn.client.shape.view.connections.Connection.SELECTION_OFFSET;
import static org.mockito.Mockito.spy;
import static org.mockito.Mockito.verify;

@RunWith(LienzoMockitoTestRunner.class)
public class ConnectionTest {

    private Connection connection;
    private double x1 = 1.0d;
    private double y1 = 2.0d;
    private double x2 = 8.0d;
    private double y2 = 9.0d;

    @Before
    public void setup() {
        connection = spy(new Connection(x1,
                                        y1,
                                        x2,
                                        y2));
    }

    @Test
    public void testBuildConnection() {

        final InOrder inorder = Mockito.inOrder(connection);

        connection.buildConnection(x1, y1, x2, y2);

        inorder.verify(connection).createHead();
        inorder.verify(connection).createTail();
        inorder.verify(connection).createLine(x1, y1, x2, y2);

        assertFalse(Objects.equals(connection.getHead(), null));
        assertFalse(Objects.equals(connection.getTail(), null));
        assertFalse(Objects.equals(connection.getLine(), null));
    }

    @Test
    public void testCreateLine() {

        final PolyLine line = connection.createLine(x1, y1, x2, y2);
        final double headBoundingBoxHeight = connection.getHead().getPath().getBoundingBox().getHeight();
        final double tailBoundingBoxHeight = connection.getTail().getPath().getBoundingBox().getHeight();

        verify(connection).setDashArray(line);

        assertTrue(line.isDraggable());
        assertEquals(SELECTION_OFFSET, line.getSelectionStrokeOffset(), 0.01d);
        assertEquals(headBoundingBoxHeight, line.getHeadOffset(), 0.01d);
        assertEquals(tailBoundingBoxHeight, line.getTailOffset(), 0.01d);
    }
}
