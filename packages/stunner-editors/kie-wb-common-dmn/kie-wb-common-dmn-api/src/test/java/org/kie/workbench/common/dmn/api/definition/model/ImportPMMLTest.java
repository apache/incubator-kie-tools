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
package org.kie.workbench.common.dmn.api.definition.model;

import org.junit.Test;
import org.kie.workbench.common.dmn.api.editors.included.DMNImportTypes;
import org.kie.workbench.common.dmn.api.property.dmn.LocationURI;
import org.kie.workbench.common.dmn.api.property.dmn.Name;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

public class ImportPMMLTest {

    @Test
    public void testNameObjectZeroParameterConstructor() {
        final ImportPMML anImport = new ImportPMML();

        doNameObjectTest(anImport);
    }

    @Test
    public void testNameObjectParameterConstructor() {
        final ImportPMML anImport = new ImportPMML("namespace",
                                                   new LocationURI(),
                                                   DMNImportTypes.PMML.getDefaultNamespace());

        assertEquals("namespace", anImport.getName().getValue());

        doNameObjectTest(anImport);
    }

    private void doNameObjectTest(final ImportPMML anImport) {
        anImport.setName(new Name("name"));

        assertEquals("name", anImport.getName().getValue());
        assertEquals("name", anImport.getNamespace());
    }

    @Test
    public void testNameValueZeroParameterConstructor() {
        final ImportPMML anImport = new ImportPMML();

        doNameValueTest(anImport);
    }

    @Test
    public void testNameValueParameterConstructor() {
        final ImportPMML anImport = new ImportPMML("namespace",
                                                   new LocationURI(),
                                                   DMNImportTypes.PMML.getDefaultNamespace());

        assertEquals("namespace", anImport.getName().getValue());

        doNameValueTest(anImport);
    }

    private void doNameValueTest(final ImportPMML anImport) {
        anImport.getName().setValue("name");

        assertEquals("name", anImport.getName().getValue());
        assertEquals("name", anImport.getNamespace());
    }

    @Test
    public void testNamespaceZeroParameterConstructor() {
        final ImportPMML anImport = new ImportPMML();

        doNamespaceTest(anImport);
    }

    @Test
    public void testNamespaceParameterConstructor() {
        final ImportPMML anImport = new ImportPMML("original",
                                                   new LocationURI(),
                                                   DMNImportTypes.PMML.getDefaultNamespace());

        assertEquals("original", anImport.getName().getValue());

        doNamespaceTest(anImport);
    }

    private void doNamespaceTest(final ImportPMML anImport) {
        anImport.setNamespace("namespace");

        assertEquals("namespace", anImport.getName().getValue());
        assertEquals("namespace", anImport.getNamespace());
    }

    @Test
    public void testIdentity() {
        final ImportPMML anImport = new ImportPMML();

        final Name expectedName = anImport.getName();
        anImport.setName(expectedName);

        assertTrue(expectedName == anImport.getName());
    }

    @Test
    public void testEquality() {
        final ImportPMML anImport = new ImportPMML();

        final Name expectedName = anImport.getName();
        anImport.setName(expectedName);

        assertEquals(expectedName, anImport.getName());
    }
}
