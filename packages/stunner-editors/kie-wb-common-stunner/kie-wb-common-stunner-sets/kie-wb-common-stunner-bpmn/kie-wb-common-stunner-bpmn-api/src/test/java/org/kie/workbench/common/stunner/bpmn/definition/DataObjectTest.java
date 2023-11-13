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


package org.kie.workbench.common.stunner.bpmn.definition;

import org.junit.Test;
import org.kie.workbench.common.stunner.bpmn.definition.property.artifacts.DataObjectType;
import org.kie.workbench.common.stunner.bpmn.definition.property.artifacts.DataObjectTypeValue;
import org.kie.workbench.common.stunner.bpmn.definition.property.background.BackgroundSet;
import org.kie.workbench.common.stunner.bpmn.definition.property.background.BgColor;
import org.kie.workbench.common.stunner.bpmn.definition.property.dimensions.RectangleDimensionsSet;
import org.kie.workbench.common.stunner.bpmn.definition.property.font.FontSet;
import org.kie.workbench.common.stunner.bpmn.definition.property.font.FontSize;
import org.kie.workbench.common.stunner.bpmn.definition.property.general.BPMNGeneralSet;
import org.kie.workbench.common.stunner.bpmn.definition.property.general.Name;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotEquals;
import static org.junit.Assert.assertNotNull;

public class DataObjectTest {

    private DataObject dataObject = new DataObject();

    @Test
    public void getLabels() {
        assertEquals(2, dataObject.getLabels().size());
        assertEquals(true, dataObject.getLabels().contains("all"));
        assertEquals(true, dataObject.getLabels().contains("lane_child"));
    }

    @Test
    public void getGeneral() {
        assertNotNull(dataObject.getGeneral());
    }

    @Test
    public void setGeneral() {
        BPMNGeneralSet general = new BPMNGeneralSet();
        dataObject.setGeneral(general);
        assertEquals(general, dataObject.getGeneral());
    }

    @Test
    public void setName() {
        Name name = new Name(this.getClass().getSimpleName());
        dataObject.setName(name);
        assertEquals(name, dataObject.getName());
    }

    @Test
    public void setType() {
        DataObjectType type = new DataObjectType(new DataObjectTypeValue(this.getClass().getSimpleName()));
        dataObject.setType(type);
        assertEquals(type, dataObject.getType());
    }

    @Test
    public void testHashCode() {
        assertEquals(new DataObject().hashCode(), dataObject.hashCode());
    }

    @Test
    public void testEquals() {
        assertEquals(new DataObject(), dataObject);
        assertNotEquals(new DataObject(), new Object());
    }

    @Test
    public void testNotEquals() {
        DataObject dataObject1 = new DataObject();
        DataObject dataObject2 = new DataObject();
        dataObject1.setName(new Name("dataObject1"));
        dataObject2.setName(new Name("dataObject2"));
        // Test Name
        assertNotEquals(dataObject1, dataObject2);
        // Reset
        dataObject2.setName(new Name("dataObject1"));
        assertEquals(dataObject1, dataObject2);
        // Test Type
        DataObjectType dataObjectType = new DataObjectType();
        dataObjectType.setValue(new DataObjectTypeValue("someType"));
        dataObject2.setType(dataObjectType);
        assertNotEquals(dataObject1, dataObject2);
        // Reset
        dataObject2.setType(new DataObjectType());
        assertEquals(dataObject1, dataObject2);
        // Test General Set
        BPMNGeneralSet generalSet = new BPMNGeneralSet();
        generalSet.setName(new Name("someName"));
        dataObject2.setGeneral(generalSet);
        assertNotEquals(dataObject1, dataObject2);
        // Reset
        dataObject2.setGeneral(new BPMNGeneralSet());
        assertEquals(dataObject1, dataObject2);
        // Test Background Set
        BackgroundSet backgroundSet = new BackgroundSet();
        backgroundSet.setBgColor(new BgColor("Black"));
        dataObject2.setBackgroundSet(backgroundSet);
        assertNotEquals(dataObject1, dataObject2);
        // Reset
        dataObject2.setBackgroundSet(new BackgroundSet());
        assertEquals(dataObject1, dataObject2);
        // Test Font Set
        FontSet fontSet = new FontSet();
        fontSet.setFontSize(new FontSize(11.0));
        dataObject2.setFontSet(fontSet);
        assertNotEquals(dataObject1, dataObject2);
        // Reset
        dataObject2.setFontSet(new FontSet());
        assertEquals(dataObject1, dataObject2);
        // Test Dimension Set
        RectangleDimensionsSet rectangleDimensionsSet = new RectangleDimensionsSet(100.0, 100.0);
        dataObject2.setDimensionsSet(rectangleDimensionsSet);
        assertNotEquals(dataObject1, dataObject2);
        // Reset
        dataObject2.setDimensionsSet(new RectangleDimensionsSet());
        assertEquals(dataObject1, dataObject2);
    }
}