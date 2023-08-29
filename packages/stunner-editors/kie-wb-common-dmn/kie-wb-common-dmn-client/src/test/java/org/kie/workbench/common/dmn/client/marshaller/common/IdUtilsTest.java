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

package org.kie.workbench.common.dmn.client.marshaller.common;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.junit.Test;
import org.kie.workbench.common.dmn.webapp.kogito.marshaller.js.model.dmndi12.JSIDMNDiagram;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotEquals;
import static org.kie.workbench.common.dmn.client.marshaller.common.IdUtils.AUTO_SOURCE_CONNECTION;
import static org.kie.workbench.common.dmn.client.marshaller.common.IdUtils.AUTO_TARGET_CONNECTION;
import static org.kie.workbench.common.dmn.client.marshaller.common.IdUtils.getComposedId;
import static org.kie.workbench.common.dmn.client.marshaller.common.IdUtils.getEdgeId;
import static org.kie.workbench.common.dmn.client.marshaller.common.IdUtils.getPrefixedId;
import static org.kie.workbench.common.dmn.client.marshaller.common.IdUtils.getRawId;
import static org.kie.workbench.common.dmn.client.marshaller.common.IdUtils.getShapeId;
import static org.kie.workbench.common.dmn.client.marshaller.common.IdUtils.uniqueId;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

public class IdUtilsTest {

    @Test
    public void testGetPrefixedId() {
        assertEquals("1111#2222", getPrefixedId("1111", "2222"));
        assertEquals("2222", getPrefixedId("", "2222"));
        assertEquals("2222", getPrefixedId(null, "2222"));
    }

    @Test
    public void testGetRawId() {
        assertEquals("2222", getRawId("0000#1111#2222"));
        assertEquals("2222", getRawId("1111#2222"));
        assertEquals("2222", getRawId("#2222"));
        assertEquals("2222", getRawId("2222"));
    }

    @Test
    public void testUniqueId() {
        assertNotEquals(uniqueId(), uniqueId());
    }

    @Test
    public void testGetComposedId() {
        assertEquals("dmnshape-page-1-_1111-2222", getComposedId("dmnshape", "page 1", "_1111-2222"));
        assertEquals("dmnshape-page-1-_1111-2222", getComposedId("dmnshape", "page   1   ", "_1111-2222"));
        assertEquals("dmnshape-_1111-2222", getComposedId("dmnshape", "", "_1111-2222"));
        assertEquals("dmnshape-_1111-2222", getComposedId("dmnshape", "_1111-2222"));
    }

    @Test
    public void testGetShapeId() {
        final JSIDMNDiagram diagram = mock(JSIDMNDiagram.class);
        when(diagram.getName()).thenReturn("DRG");
        assertEquals("dmnshape-drg-_1111-2222", getShapeId(diagram, list(), "_1111-2222"));
        assertEquals("dmnshape-drg-2-_1111-2222", getShapeId(diagram, list("dmnshape-drg-_1111-2222"), "_1111-2222"));
        assertEquals("dmnshape-drg-3-_1111-2222", getShapeId(diagram, list("dmnshape-drg-_1111-2222", "dmnshape-drg-2-_1111-2222"), "_1111-2222"));
    }

    @Test
    public void testGetEdgeId() {
        String dmnElementId = "_1111-2222";
        String uniqueId = "dmnedge-drg-" + dmnElementId;

        final JSIDMNDiagram diagram = mock(JSIDMNDiagram.class);
        when(diagram.getName()).thenReturn("DRG");

        final String result1 = getEdgeId(diagram, list(),
                                         dmnElementId,
                                         false,
                                         false);
        String expected1 = uniqueId;
        assertEquals(expected1, result1);

        final String result2 = getEdgeId(diagram, list(),
                                         dmnElementId,
                                         true,
                                         false);
        String expected2 = uniqueId + AUTO_SOURCE_CONNECTION;
        assertEquals(expected2, result2);

        final String result3 = getEdgeId(diagram, list(),
                                         dmnElementId,
                                         false,
                                         true);
        String expected3 = uniqueId + AUTO_TARGET_CONNECTION;
        assertEquals(expected3, result3);

        final String result4 = getEdgeId(diagram, list(),
                                         dmnElementId,
                                         true,
                                         true);
        String expected4 = uniqueId + AUTO_SOURCE_CONNECTION + AUTO_TARGET_CONNECTION;
        assertEquals(expected4, result4);
    }

    @Test
    public void testGetShapeIdWhenDiagramNameIsNull() {
        final JSIDMNDiagram diagram = mock(JSIDMNDiagram.class);
        assertEquals("dmnshape-_1111-2222", getShapeId(diagram, list(), "_1111-2222"));
    }

    @Test
    public void testGetEdgeIdWhenDiagramNameIsNull() {
        final JSIDMNDiagram diagram = mock(JSIDMNDiagram.class);
        assertEquals("dmnedge-_1111-2222", getEdgeId(diagram, list(), "_1111-2222", false, false));
    }

    private List<String> list(final String... items) {
        return new ArrayList<>(Arrays.asList(items));
    }
}
