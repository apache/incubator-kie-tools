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

package com.ait.lienzo.client.core.shape.wires;

import com.ait.lienzo.client.core.shape.MultiPath;
import com.ait.lienzo.client.core.shape.MultiPathDecorator;
import com.ait.lienzo.client.core.shape.PolyLine;
import com.ait.lienzo.client.core.types.Point2DArray;
import com.ait.lienzo.test.LienzoMockitoTestRunner;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;

import static org.junit.Assert.assertNull;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

@RunWith(LienzoMockitoTestRunner.class)
public class WiresConnectorTest {

    @Mock
    private MultiPathDecorator headDecorator;

    @Mock
    private MultiPathDecorator tailDecorator;

    private PolyLine line;
    private static MultiPath headPath = new MultiPath().circle(10);
    private static MultiPath tailPath = new MultiPath().circle(10);
    private WiresConnector tested;

    @Before
    public void setup() {
        when(headDecorator.getPath()).thenReturn(headPath);
        when(tailDecorator.getPath()).thenReturn(tailPath);
        Point2DArray points = new Point2DArray(10, 20);
        line = new PolyLine(points);
        tested = new WiresConnector(line,
                                    headDecorator,
                                    tailDecorator);
    }

    // Asset than it handles no magnets set as well, it must not
    // throw any NullPointerException, as magnets are not mandatory to be present.
    @Test
    public void testGetMagnetsHandlesNulls() {
        WiresShape headShape = mock(WiresShape.class);
        WiresShape tailShape = mock(WiresShape.class);
        WiresMagnet[] magnets = tested.getMagnets(headShape, 1, tailShape, 3);
        assertNull(magnets[0]);
        assertNull(magnets[1]);
    }
}
