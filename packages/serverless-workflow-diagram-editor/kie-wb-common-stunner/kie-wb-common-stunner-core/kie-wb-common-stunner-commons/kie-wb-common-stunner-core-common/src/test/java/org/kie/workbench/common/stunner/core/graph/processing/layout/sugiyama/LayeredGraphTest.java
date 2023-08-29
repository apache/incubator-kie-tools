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


package org.kie.workbench.common.stunner.core.graph.processing.layout.sugiyama;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.junit.MockitoJUnitRunner;

import static org.junit.Assert.assertEquals;
import static org.kie.workbench.common.stunner.core.graph.processing.layout.sugiyama.step04.VertexPositioning.DEFAULT_VERTEX_HEIGHT;
import static org.kie.workbench.common.stunner.core.graph.processing.layout.sugiyama.step04.VertexPositioning.DEFAULT_VERTEX_WIDTH;

@RunWith(MockitoJUnitRunner.class)
public class LayeredGraphTest {

    private LayeredGraph tested;

    @Before
    public void setup() {
        tested = new LayeredGraph();
    }

    @Test
    public void testGetVertexHeight() {

        final int width = 10;
        final int height = 20;
        final String id = "1";
        int actual = tested.getVertexHeight(id);
        assertEquals(DEFAULT_VERTEX_HEIGHT, actual);

        tested.setVertexSize(id, width, height);
        actual = tested.getVertexHeight(id);
        assertEquals(height, actual);
    }

    @Test
    public void testGetVertexWidth() {

        final int width = 10;
        final int height = 20;
        final String id = "1";
        int actual = tested.getVertexWidth(id);
        assertEquals(DEFAULT_VERTEX_WIDTH, actual);

        tested.setVertexSize(id, width, height);
        actual = tested.getVertexWidth(id);
        assertEquals(width, actual);
    }

    @Test
    public void testSetVertexSize() {

        final int width = 15;
        final int height = 17;
        final String id = "1";

        tested.setVertexSize(id, width, height);

        final int actualWidth = tested.getVertexWidth(id);
        final int actualHeight = tested.getVertexHeight(id);

        assertEquals(width, actualWidth);
        assertEquals(height, actualHeight);
    }
}