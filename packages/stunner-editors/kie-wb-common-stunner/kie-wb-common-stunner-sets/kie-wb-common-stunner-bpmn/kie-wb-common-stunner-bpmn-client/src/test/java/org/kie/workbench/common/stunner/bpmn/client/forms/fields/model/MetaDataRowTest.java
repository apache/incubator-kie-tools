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


package org.kie.workbench.common.stunner.bpmn.client.forms.fields.model;

import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotEquals;
import static org.mockito.MockitoAnnotations.initMocks;

public class MetaDataRowTest {

    @Mock
    private MetaDataAttribute metaDataAttribute;

    private MetaDataRow metaDataRow;

    @Before
    public void setUp() {
        initMocks(this);
        metaDataRow = new MetaDataRow(metaDataAttribute);
    }

    @Test
    public void testSetId() {
        metaDataRow.setId(1000L);
        assertEquals(1000L, metaDataRow.getId());
    }

    @Test
    public void testSetAttribute() {
        metaDataRow.setAttribute("Test");
        assertEquals("Test", metaDataRow.getAttribute());
    }

    @Test
    public void testSetValue() {
        metaDataRow.setValue("Val1");
        assertEquals("Val1", metaDataRow.getValue());
    }

    @Test
    public void testEquals() {
        MetaDataRow metaDataRow1 = new MetaDataRow(new MetaDataAttribute("att1"));
        MetaDataRow metaDataRow2 = new MetaDataRow(new MetaDataAttribute("att1", "val1"));
        MetaDataRow metaDataRow3 = new MetaDataRow("att1", "val1");
        VariableRow variableRow = new VariableRow();

        assertNotEquals(metaDataRow1, variableRow);
        assertNotEquals(metaDataRow2, metaDataRow3);

        metaDataRow1.setId(1L);
        metaDataRow2.setId(1L);
        metaDataRow3.setId(1L);
        assertEquals(metaDataRow2, metaDataRow3);

        metaDataRow2.setAttribute("att2");
        assertNotEquals(metaDataRow2, metaDataRow3);

        metaDataRow1.setAttribute("att1");
        metaDataRow1.setValue("val2");
        assertNotEquals(metaDataRow2, metaDataRow3);

        metaDataRow2.setAttribute("att1");
        metaDataRow2.setValue("val2");
        assertNotEquals(metaDataRow2, metaDataRow3);
    }

    @Test
    public void testHashCode() {
        MetaDataRow metaDataRow1 = new MetaDataRow("att1", "val1");
        MetaDataRow metaDataRow2 = new MetaDataRow("att1", "val1");

        assertNotEquals(metaDataRow1.hashCode(), metaDataRow2.hashCode());

        metaDataRow1.setId(1L);
        metaDataRow2.setId(1L);

        assertEquals(metaDataRow1.hashCode(), metaDataRow2.hashCode());
    }
}
