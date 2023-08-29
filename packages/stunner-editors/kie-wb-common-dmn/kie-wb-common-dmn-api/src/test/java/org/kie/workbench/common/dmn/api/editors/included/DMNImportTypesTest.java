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

package org.kie.workbench.common.dmn.api.editors.included;

import java.util.List;

import org.junit.Test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;

public class DMNImportTypesTest {

    @Test
    public void testDMN() {
        assertEquals("dmn", DMNImportTypes.DMN.getFileExtension());

        assertEquals(DMNImportTypes.DMN, DMNImportTypes.determineImportType("http://www.omg.org/spec/DMN/20180521/MODEL/"));
    }

    @Test
    public void testPMML() {
        assertEquals("pmml", DMNImportTypes.PMML.getFileExtension());

        assertEquals(DMNImportTypes.PMML, DMNImportTypes.determineImportType("http://www.dmg.org/PMML-3_0"));
        assertEquals(DMNImportTypes.PMML, DMNImportTypes.determineImportType("http://www.dmg.org/PMML-3_1"));
        assertEquals(DMNImportTypes.PMML, DMNImportTypes.determineImportType("http://www.dmg.org/PMML-3_2"));
        assertEquals(DMNImportTypes.PMML, DMNImportTypes.determineImportType("http://www.dmg.org/PMML-4_0"));
        assertEquals(DMNImportTypes.PMML, DMNImportTypes.determineImportType("http://www.dmg.org/PMML-4_1"));
        assertEquals(DMNImportTypes.PMML, DMNImportTypes.determineImportType("http://www.dmg.org/PMML-4_2"));
        assertEquals(DMNImportTypes.PMML, DMNImportTypes.determineImportType("http://www.dmg.org/PMML-4_3"));
    }

    @Test
    public void testUnknown() {
        assertNull(DMNImportTypes.determineImportType("cheese"));
    }

    @Test
    public void testGetNamespacesDMN() {
        final List<String> namespaces = DMNImportTypes.DMN.getNamespaces();

        assertEquals(1, namespaces.size());

        assertEquals("http://www.omg.org/spec/DMN/20180521/MODEL/", namespaces.get(0));
    }

    @Test
    public void testGetNamespacesPMML() {
        final List<String> namespaces = DMNImportTypes.PMML.getNamespaces();

        assertEquals(7, namespaces.size());

        assertEquals("http://www.dmg.org/PMML-3_0", namespaces.get(0));
        assertEquals("http://www.dmg.org/PMML-3_1", namespaces.get(1));
        assertEquals("http://www.dmg.org/PMML-3_2", namespaces.get(2));
        assertEquals("http://www.dmg.org/PMML-4_0", namespaces.get(3));
        assertEquals("http://www.dmg.org/PMML-4_1", namespaces.get(4));
        assertEquals("http://www.dmg.org/PMML-4_2", namespaces.get(5));
        assertEquals("http://www.dmg.org/PMML-4_3", namespaces.get(6));
    }

    @Test
    public void testGetDefaultNameSpaceDMN() {
        assertEquals("http://www.omg.org/spec/DMN/20180521/MODEL/", DMNImportTypes.DMN.getDefaultNamespace());
    }

    @Test
    public void testGetDefaultNameSpacePMML() {
        assertEquals("http://www.dmg.org/PMML-4_3", DMNImportTypes.PMML.getDefaultNamespace());
    }
}
