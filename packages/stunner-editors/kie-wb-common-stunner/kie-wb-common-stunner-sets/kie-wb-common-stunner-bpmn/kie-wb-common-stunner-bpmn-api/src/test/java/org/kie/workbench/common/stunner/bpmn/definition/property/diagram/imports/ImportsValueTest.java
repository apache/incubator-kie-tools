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


package org.kie.workbench.common.stunner.bpmn.definition.property.diagram.imports;

import java.util.ArrayList;
import java.util.List;

import org.junit.Before;
import org.junit.Test;

import static org.junit.Assert.assertEquals;

public class ImportsValueTest {

    private static final String CLASS_NAME = "ClassName";
    private static final String LOCATION = "Location";
    private static final String NAMESPACE = "Namespace";
    private static final String STRING_EMPTY = "";

    private static final int DEFAULT_IMPORTS_QTY = 10;
    private static final int WSDL_IMPORTS_QTY = 11;

    private List<DefaultImport> defaultImports;
    private List<WSDLImport> wsdlImports;

    @Before
    public void setUp() {
        defaultImports = new ArrayList<>();
        for (int i = 0; i < DEFAULT_IMPORTS_QTY; i++) {
            defaultImports.add(new DefaultImport(CLASS_NAME + i));
        }

        wsdlImports = new ArrayList<>();
        for (int i = 0; i < WSDL_IMPORTS_QTY; i++) {
            wsdlImports.add(new WSDLImport(LOCATION + i, NAMESPACE + i));
        }
    }

    @Test
    public void getDefaultImports() {
        ImportsValue tested = new ImportsValue(defaultImports, wsdlImports);
        assertDefaultImports(tested.getDefaultImports(), DEFAULT_IMPORTS_QTY);
    }

    @Test
    public void setDefaultImports() {
        ImportsValue tested = new ImportsValue();
        tested.setDefaultImports(defaultImports);
        assertDefaultImports(tested.getDefaultImports(), DEFAULT_IMPORTS_QTY);
    }

    @Test
    public void getWsdlImports() {
        ImportsValue tested = new ImportsValue(defaultImports, wsdlImports);
        assertWSDLImports(tested.getWSDLImports(), WSDL_IMPORTS_QTY);
    }

    @Test
    public void setWsdlImports() {
        ImportsValue tested = new ImportsValue();
        tested.setWSDLImports(wsdlImports);
        assertWSDLImports(tested.getWSDLImports(), WSDL_IMPORTS_QTY);
    }

    @Test
    public void testAddImport() {
        ImportsValue tested = new ImportsValue();
        for (int i = 0; i < DEFAULT_IMPORTS_QTY; i++) {
            tested.addImport(new DefaultImport(CLASS_NAME + i));
        }
        for (int i = 0; i < WSDL_IMPORTS_QTY; i++) {
            tested.addImport(new WSDLImport(LOCATION + i, NAMESPACE + i));
        }

        assertDefaultImports(tested.getDefaultImports(), DEFAULT_IMPORTS_QTY);
        assertWSDLImports(tested.getWSDLImports(), WSDL_IMPORTS_QTY);
    }

    @Test
    public void testEquals() {
        ImportsValue tested1 = new ImportsValue();
        ImportsValue tested2 = new ImportsValue();
        assertEquals(tested1, tested2);

        ImportsValue tested3 = new ImportsValue(defaultImports, wsdlImports);
        ImportsValue tested4 = new ImportsValue(defaultImports, wsdlImports);
        assertEquals(tested3, tested4);
    }

    @Test
    public void testHashCode() {
        ImportsValue tested1 = new ImportsValue();
        ImportsValue tested2 = new ImportsValue();
        assertEquals(tested1.hashCode(), tested2.hashCode());

        ImportsValue tested3 = new ImportsValue(defaultImports, wsdlImports);
        ImportsValue tested4 = new ImportsValue(defaultImports, wsdlImports);
        assertEquals(tested3.hashCode(), tested4.hashCode());
    }

    @Test
    public void testToString() {
        ImportsValue tested1 = new ImportsValue();
        assertEquals(STRING_EMPTY, tested1.toString());

        ImportsValue tested2 = new ImportsValue(defaultImports, wsdlImports);
        String defaultImpString = buildImportsString(ImportType.DEFAULT, DEFAULT_IMPORTS_QTY);
        String wsdlImpString = buildImportsString(ImportType.WSDL, WSDL_IMPORTS_QTY);
        String expected = defaultImpString + "," + wsdlImpString;

        assertEquals(expected, tested2.toString());
    }

    private String buildDefaultImportString(int index) {
        return DefaultImport.IDENTIFIER + DefaultImport.DELIMITER + CLASS_NAME + index;
    }

    private String buildWSDLImportString(int index) {
        return WSDLImport.IDENTIFIER + WSDLImport.DELIMITER + LOCATION + index + WSDLImport.DELIMITER + NAMESPACE + index;
    }

    private String buildImportsString(ImportType importType, int quantity) {
        String importsString = "";
        for (int i = 0; i < quantity; i++) {
            switch (importType) {
                case DEFAULT:
                    importsString += buildDefaultImportString(i);
                    break;
                case WSDL:
                    importsString += buildWSDLImportString(i);
                    break;
            }
            if (i < quantity - 1) {
                importsString += ",";
            }
        }

        return importsString;
    }

    private void assertDefaultImports(List<DefaultImport> defaultImports, int quantity) {
        assertEquals(quantity, this.defaultImports.size());
        for (int i = 0; i < quantity; i++) {
            assertEquals(CLASS_NAME + i, defaultImports.get(i).getClassName());
        }
    }

    private void assertWSDLImports(List<WSDLImport> wsdlImports, int quantity) {
        assertEquals(quantity, this.wsdlImports.size());
        for (int i = 0; i < quantity; i++) {
            assertEquals(LOCATION + i, wsdlImports.get(i).getLocation());
            assertEquals(NAMESPACE + i, wsdlImports.get(i).getNamespace());
        }
    }

    private enum ImportType {
        DEFAULT,
        WSDL
    }
}