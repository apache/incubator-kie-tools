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


package org.kie.workbench.common.stunner.bpmn.forms.model;

import org.junit.Test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotEquals;

public class MetaDataEditorFieldDefinitionTest {

    @Test
    public void testGetFieldTypeTest() {
        assertEquals("MetaDataEditor", new MetaDataEditorFieldDefinition().getFieldType().getTypeName());
    }

    @Test
    public void testDoCopyFromTest() {
        MetaDataEditorFieldDefinition def = new MetaDataEditorFieldDefinition();
        MetaDataEditorFieldDefinition def2 = new MetaDataEditorFieldDefinition();
        def.doCopyFrom(def2);
        assertEquals(def2, def);
    }

    @Test
    public void testDoCopyFromTestFail() {
        MetaDataEditorFieldDefinition def = new MetaDataEditorFieldDefinition();
        ImportsFieldDefinition def2 = new ImportsFieldDefinition();
        def.doCopyFrom(def2);
        assertNotEquals(def2, def);
    }

    @Test
    public void testSetDefaultValueTest() {
        MetaDataEditorFieldDefinition def = new MetaDataEditorFieldDefinition();
        def.setDefaultValue("test");
        assertEquals("test", def.getDefaultValue());
    }

    @Test
    public void testEquals() {
        MetaDataEditorFieldDefinition def = new MetaDataEditorFieldDefinition();
        MetaDataEditorFieldDefinition def2 = new MetaDataEditorFieldDefinition();
        ImportsFieldDefinition def3 = new ImportsFieldDefinition();

        def.setDefaultValue("test");
        def2.setDefaultValue("test");

        assertEquals(def, def2);

        def.setDefaultValue("anyValue");
        assertFalse(def.equals(def2));

        assertFalse(def.equals(def3));
    }

    @Test
    public void testHashCode() {
        MetaDataEditorFieldDefinition def = new MetaDataEditorFieldDefinition();
        MetaDataEditorFieldDefinition def2 = new MetaDataEditorFieldDefinition();

        assertEquals(def.hashCode(), def2.hashCode());
    }
}
