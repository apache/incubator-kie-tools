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


package org.kie.workbench.common.stunner.svg.client.shape.view;

import com.ait.lienzo.client.core.shape.Rectangle;
import com.ait.lienzo.client.core.shape.wires.LayoutContainer;
import com.ait.lienzo.test.LienzoMockitoTestRunner;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

@RunWith(LienzoMockitoTestRunner.class)
public class SVGPrimitiveTest {

    private SVGPrimitive tested;
    private Rectangle rectangle;

    @Before
    @SuppressWarnings("unchecked")
    public void setup() throws Exception {
        rectangle = new Rectangle(10d, 10d).setID("rect1");
        tested = new SVGPrimitive(rectangle,
                                  true,
                                  LayoutContainer.Layout.BOTTOM);
    }

    @Test
    public void testGetters() {
        assertEquals("rect1", tested.getId());
        assertEquals(LayoutContainer.Layout.BOTTOM, tested.getLayout());
        assertEquals(rectangle, tested.get());
        assertTrue(tested.isScalable());
    }
}
