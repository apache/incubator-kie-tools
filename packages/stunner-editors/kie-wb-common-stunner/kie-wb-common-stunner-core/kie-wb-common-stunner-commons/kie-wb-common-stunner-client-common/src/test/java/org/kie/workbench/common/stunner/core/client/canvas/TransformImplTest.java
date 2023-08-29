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

package org.kie.workbench.common.stunner.core.client.canvas;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.kie.workbench.common.stunner.core.graph.content.view.Point2D;
import org.mockito.junit.MockitoJUnitRunner;

import static org.junit.Assert.assertEquals;

@RunWith(MockitoJUnitRunner.class)
public class TransformImplTest {

    private static final Point2D translate = new Point2D(10,
                                                         20);
    private static final Point2D scale = new Point2D(2,
                                                     5);

    private TransformImpl tested;

    @Before
    public void setup() throws Exception {
        this.tested = new TransformImpl(translate,
                                        scale);
    }

    @Test
    public void testGetTranslate() {
        final Point2D t = tested.getTranslate();
        assertEquals(translate,
                     t);
    }

    @Test
    public void testGetScale() {
        final Point2D s = tested.getScale();
        assertEquals(scale,
                     s);
    }

    @Test
    public void testTransform() {
        final Point2D t = tested.transform(1,
                                           1);
        assertEquals(12,
                     t.getX(),
                     0);
        assertEquals(25,
                     t.getY(),
                     0);
        final Point2D t1 = tested.transform(2,
                                            2);
        assertEquals(14,
                     t1.getX(),
                     0);
        assertEquals(30,
                     t1.getY(),
                     0);
    }
}
