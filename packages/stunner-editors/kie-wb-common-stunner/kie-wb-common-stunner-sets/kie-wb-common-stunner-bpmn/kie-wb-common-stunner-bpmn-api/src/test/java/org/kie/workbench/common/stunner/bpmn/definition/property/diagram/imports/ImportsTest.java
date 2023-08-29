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

public class ImportsTest {

    private ImportsValue importsValue;

    @Before
    public void setUp() {
        final String CLASS_NAME = "ClassName";
        final String LOCATION = "Location";
        final String NAMESPACE = "Namespace";

        final int DEFAULT_IMPORTS_QTY = 10;
        final int WSDL_IMPORTS_QTY = 11;

        final List<DefaultImport> defaultImports = new ArrayList<>();
        for (int i = 0; i < DEFAULT_IMPORTS_QTY; i++) {
            defaultImports.add(new DefaultImport(CLASS_NAME + i));
        }

        final List<WSDLImport> wsdlImports = new ArrayList<>();
        for (int i = 0; i < WSDL_IMPORTS_QTY; i++) {
            wsdlImports.add(new WSDLImport(LOCATION + i, NAMESPACE + i));
        }

        importsValue = new ImportsValue(defaultImports, wsdlImports);
    }

    @Test
    public void getType() {
        Imports tested = new Imports();
        assertEquals(Imports.type, tested.getType());
    }

    @Test
    public void getValue() {
        Imports tested = new Imports(importsValue);
        assertEquals(importsValue, tested.getValue());
    }

    @Test
    public void setValue() {
        Imports tested = new Imports();
        tested.setValue(importsValue);
        assertEquals(importsValue, tested.getValue());
    }

    @Test
    public void testEquals() {
        Imports tested1 = new Imports();
        Imports tested2 = new Imports();
        assertEquals(tested1, tested2);

        Imports tested3 = new Imports(importsValue);
        Imports tested4 = new Imports(importsValue);
        assertEquals(tested3, tested4);
    }

    @Test
    public void testHashCode() {
        Imports tested1 = new Imports();
        Imports tested2 = new Imports();
        assertEquals(tested1.hashCode(), tested2.hashCode());

        Imports tested3 = new Imports(importsValue);
        Imports tested4 = new Imports(importsValue);
        assertEquals(tested3.hashCode(), tested4.hashCode());
    }
}