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


package org.kie.workbench.common.stunner.bpmn.documentation.model.general;

import java.util.ArrayList;

import org.junit.Test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

public class ImportsTest {

    private static final String EMPTY_VALUE = "";
    private static final String HIDDEN = "hidden";

    private static final String CLASS_NAME = "Class Name";
    private static final String LOCATION = "Location";
    private static final String NAMESPACE = "Namespace";

    @Test
    public void createImportsSuccess() {
        final ArrayList<Imports.DefaultImport> defaultImports = new ArrayList<>();
        final ArrayList<Imports.WSDLImport> wsdlImports = new ArrayList<>();

        final Object imports = Imports.create(defaultImports, wsdlImports);

        assertNotNull(imports);
        assertTrue(imports instanceof Imports);
    }

    @Test(expected = NullPointerException.class)
    public void createImportsFail() {
        final Imports imports = Imports.create(null, null);
    }

    @Test
    public void createDefaultImport() {
        final Imports.DefaultImport defaultImports = Imports.DefaultImport.create(CLASS_NAME);

        assertNotNull(defaultImports);
        assertEquals(CLASS_NAME, defaultImports.getClassName());
        assertTrue(defaultImports instanceof Imports.DefaultImport);
    }

    @Test
    public void createWSDLImport() {
        final Imports.WSDLImport wsdlImport = Imports.WSDLImport.create(LOCATION, NAMESPACE);

        assertNotNull(wsdlImport);
        assertEquals(LOCATION, wsdlImport.getLocation());
        assertEquals(NAMESPACE, wsdlImport.getNamespace());
        assertTrue(wsdlImport instanceof Imports.WSDLImport);
    }

    @Test
    public void getDefaultImportsHidden() {
        final ArrayList<Imports.DefaultImport> emptyDefaultImports = new ArrayList<>();
        final ArrayList<Imports.WSDLImport> emptyWSDLImports = new ArrayList<>();

        final ArrayList<Imports.DefaultImport> defaultImports = new ArrayList<>();
        defaultImports.add(Imports.DefaultImport.create(CLASS_NAME));

        final Imports imports1 = Imports.create(emptyDefaultImports, emptyWSDLImports);
        final Imports imports2 = Imports.create(defaultImports, emptyWSDLImports);

        assertEquals(HIDDEN, imports1.getDefaultImportsHidden());
        assertEquals(EMPTY_VALUE, imports2.getDefaultImportsHidden());
    }

    @Test
    public void getWSDLImportsHidden() {
        final ArrayList<Imports.DefaultImport> emptyDefaultImports = new ArrayList<>();
        final ArrayList<Imports.WSDLImport> emptyWSDLImports = new ArrayList<>();

        final ArrayList<Imports.WSDLImport> wsdlImports = new ArrayList<>();
        wsdlImports.add(Imports.WSDLImport.create(LOCATION, NAMESPACE));

        final Imports imports1 = Imports.create(emptyDefaultImports, emptyWSDLImports);
        final Imports imports2 = Imports.create(emptyDefaultImports, wsdlImports);

        assertEquals(HIDDEN, imports1.getWSDLImportsHidden());
        assertEquals(EMPTY_VALUE, imports2.getWSDLImportsHidden());
    }

    @Test
    public void getImportsTableHidden() {
        final ArrayList<Imports.DefaultImport> emptyDefaultImports = new ArrayList<>();
        final ArrayList<Imports.WSDLImport> emptyWSDLImports = new ArrayList<>();

        final ArrayList<Imports.DefaultImport> defaultImports = new ArrayList<>();
        defaultImports.add(Imports.DefaultImport.create(CLASS_NAME));

        final ArrayList<Imports.WSDLImport> wsdlImports = new ArrayList<>();
        wsdlImports.add(Imports.WSDLImport.create(LOCATION, NAMESPACE));

        final Imports imports1 = Imports.create(emptyDefaultImports, emptyWSDLImports);
        final Imports imports2 = Imports.create(defaultImports, wsdlImports);

        assertEquals(HIDDEN, imports1.getImportsTableHidden());
        assertEquals(EMPTY_VALUE, imports2.getImportsTableHidden());
    }

    @Test
    public void getNoImportsHidden() {
        final ArrayList<Imports.DefaultImport> emptyDefaultImports = new ArrayList<>();
        final ArrayList<Imports.WSDLImport> emptyWSDLImports = new ArrayList<>();

        final ArrayList<Imports.DefaultImport> defaultImports = new ArrayList<>();
        defaultImports.add(Imports.DefaultImport.create(CLASS_NAME));

        final ArrayList<Imports.WSDLImport> wsdlImports = new ArrayList<>();
        wsdlImports.add(Imports.WSDLImport.create(LOCATION, NAMESPACE));

        final Imports imports1 = Imports.create(emptyDefaultImports, emptyWSDLImports);
        final Imports imports2 = Imports.create(defaultImports, wsdlImports);

        assertEquals(EMPTY_VALUE, imports1.getNoImportsHidden());
        assertEquals(HIDDEN, imports2.getNoImportsHidden());
    }

    @Test
    public void getTotalDefaultImports() {
        final ArrayList<Imports.WSDLImport> emptyWSDLImports = new ArrayList<>();

        final ArrayList<Imports.DefaultImport> defaultImports = new ArrayList<>();
        defaultImports.add(Imports.DefaultImport.create(CLASS_NAME + 1));
        defaultImports.add(Imports.DefaultImport.create(CLASS_NAME + 2));
        defaultImports.add(Imports.DefaultImport.create(CLASS_NAME + 3));

        final Imports imports = Imports.create(defaultImports, emptyWSDLImports);

        assertEquals(3, imports.getTotalDefaultImports(), 0);
    }

    @Test
    public void getTotalWSDLImports() {
        final ArrayList<Imports.DefaultImport> emptyDefaultImports = new ArrayList<>();

        final ArrayList<Imports.WSDLImport> wsdlImports = new ArrayList<>();
        wsdlImports.add(Imports.WSDLImport.create(LOCATION + 1, NAMESPACE + 1));
        wsdlImports.add(Imports.WSDLImport.create(LOCATION + 2, NAMESPACE + 2));
        wsdlImports.add(Imports.WSDLImport.create(LOCATION + 3, NAMESPACE + 3));

        final Imports imports = Imports.create(emptyDefaultImports, wsdlImports);

        assertEquals(3, imports.getTotalWSDLImports(), 0);
    }

    @Test
    public void getDefaultImports() {
        final int quantity = 5;

        final ArrayList<Imports.WSDLImport> emptyWSDLImports = new ArrayList<>();

        final ArrayList<Imports.DefaultImport> defaultImports = new ArrayList<>();
        for (int i = 0; i < quantity; i++) {
            defaultImports.add(Imports.DefaultImport.create(CLASS_NAME + i));
        }

        final Imports imports = Imports.create(defaultImports, emptyWSDLImports);

        assertEquals(quantity, imports.getDefaultImports().length, 0);
        for (int i = 0; i < quantity; i++) {
            assertEquals(CLASS_NAME + i, imports.getDefaultImports()[i].getClassName());
        }
    }

    @Test
    public void getWSDLImports() {
        final int quantity = 5;

        final ArrayList<Imports.DefaultImport> emptyDefaultImports = new ArrayList<>();

        final ArrayList<Imports.WSDLImport> wsdlImports = new ArrayList<>();
        for (int i = 0; i < quantity; i++) {
            wsdlImports.add(Imports.WSDLImport.create(LOCATION + i, NAMESPACE + i));
        }

        final Imports imports = Imports.create(emptyDefaultImports, wsdlImports);

        assertEquals(quantity, imports.getWSDLImports().length, 0);
        for (int i = 0; i < quantity; i++) {
            assertEquals(LOCATION + i, imports.getWSDLImports()[i].getLocation());
            assertEquals(NAMESPACE + i, imports.getWSDLImports()[i].getNamespace());
        }
    }
}