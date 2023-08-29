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


package org.kie.workbench.common.stunner.client.lienzo.util;

import com.ait.lienzo.client.core.shape.wires.OptionalBounds;
import com.ait.lienzo.test.LienzoMockitoTestRunner;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.kie.workbench.common.stunner.core.graph.content.Bounds;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;

@RunWith(LienzoMockitoTestRunner.class)
public class LienzoShapeUtilsTest {

    @Test
    public void testTranslateBounds() {
        Bounds bounds = Bounds.create(1.1d, 2.2d, 3.3d, 4.4d);
        OptionalBounds lienzoBounds = LienzoShapeUtils.translateBounds(bounds);
        assertTrue(lienzoBounds.hasMinX());
        assertEquals(1.1d, lienzoBounds.getMinX(), 0d);
        assertTrue(lienzoBounds.hasMinY());
        assertEquals(2.2d, lienzoBounds.getMinY(), 0d);
        assertTrue(lienzoBounds.hasMaxX());
        assertEquals(3.3d, lienzoBounds.getMaxX(), 0d);
        assertTrue(lienzoBounds.hasMaxY());
        assertEquals(4.4d, lienzoBounds.getMaxY(), 0d);
        Bounds minBounds = Bounds.createMinBounds(0d, 0d);
        OptionalBounds lienzoMinBounds = LienzoShapeUtils.translateBounds(minBounds);
        assertTrue(lienzoMinBounds.hasMinX());
        assertEquals(0d, lienzoMinBounds.getMinX(), 0d);
        assertTrue(lienzoMinBounds.hasMinY());
        assertEquals(0d, lienzoMinBounds.getMinY(), 0d);
        assertFalse(lienzoMinBounds.hasMaxX());
        assertNull(lienzoMinBounds.getMaxX());
        assertFalse(lienzoMinBounds.hasMaxY());
        assertNull(lienzoMinBounds.getMaxY());
    }
}
