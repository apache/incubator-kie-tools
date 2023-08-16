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


package org.kie.workbench.common.stunner.bpmn.definition.property.diagram;

import java.util.Set;

import javax.validation.ConstraintViolation;
import javax.validation.Validation;
import javax.validation.Validator;
import javax.validation.ValidatorFactory;

import org.junit.Before;
import org.junit.Test;
import org.kie.workbench.common.stunner.bpmn.definition.property.diagram.imports.DefaultImport;
import org.kie.workbench.common.stunner.bpmn.definition.property.diagram.imports.Imports;
import org.kie.workbench.common.stunner.bpmn.definition.property.diagram.imports.ImportsValue;
import org.kie.workbench.common.stunner.bpmn.definition.property.diagram.imports.WSDLImport;
import org.kie.workbench.common.stunner.bpmn.definition.property.general.Name;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotEquals;
import static org.junit.Assert.assertTrue;

public class DiagramSetTest {

    private static final String NAME_VALID = "My New Name";
    private static final String NAME_INVALID = "";
    private static final String ID_VALID = "My_New_Id";
    private static final String ID_INVALID = "";
    private static final String PACKAGE_VALID = "My New Package";
    private static final String PACKAGE_INVALID = "";
    private static final String VERSION_VALID = "5.0";
    private static final String VERSION_INVALID = "";
    private Validator validator;
    private DiagramSet tested;

    @Before
    public void setUp() throws Exception {
        ValidatorFactory vf = Validation.buildDefaultValidatorFactory();
        this.validator = vf.getValidator();

        tested = new DiagramSet();
        tested.setName(new Name((NAME_VALID)));
        tested.setId(new Id(ID_VALID));
        tested.setPackageProperty(new Package(PACKAGE_VALID));
        tested.setVersion(new Version(VERSION_VALID));
        tested.setProcessType(new ProcessType());
    }

    @Test
    public void testAllValid() {
        Set<ConstraintViolation<DiagramSet>> violations = this.validator.validate(tested);
        assertTrue(violations.isEmpty());
    }

    @Test
    public void testNameInvalid() {
        tested.setName(new Name(NAME_INVALID));
        Set<ConstraintViolation<DiagramSet>> violations = this.validator.validate(tested);
        assertEquals(1, violations.size());
    }

    @Test
    public void testIDInvalid() {
        tested.setId(new Id(ID_INVALID));
        Set<ConstraintViolation<DiagramSet>> violations = this.validator.validate(tested);
        assertEquals(1, violations.size());
    }

    @Test
    public void testPackageInvalid() {
        tested.setPackageProperty(new Package(PACKAGE_INVALID));
        Set<ConstraintViolation<DiagramSet>> violations = this.validator.validate(tested);
        assertEquals(1, violations.size());
    }

    @Test
    public void testVersionInvalid() {
        tested.setVersion(new Version(VERSION_INVALID));
        Set<ConstraintViolation<DiagramSet>> violations = this.validator.validate(tested);
        assertEquals(1, violations.size());
    }

    @Test
    public void testProcessType() {
        ProcessType test = new ProcessType();
        test.setValue("Private");
        tested.setProcessType(test);
        assertEquals("Private", tested.getProcessType().getValue());
    }

    @Test
    public void testGetImports() {
        DiagramSet diagramSet = new DiagramSet();
        assertEquals(new Imports(), diagramSet.getImports());
    }

    @Test
    public void testSetImports() {
        DefaultImport defaultImport = new DefaultImport("className");
        WSDLImport wsdlImport = new WSDLImport("location", "namespace");

        ImportsValue importsValue = new ImportsValue();
        importsValue.addImport(defaultImport);
        importsValue.addImport(wsdlImport);

        Imports imports = new Imports(importsValue);

        DiagramSet diagramSet = new DiagramSet();
        diagramSet.setImports(imports);

        assertEquals(imports, diagramSet.getImports());
    }

    @Test
    public void testHashCode() {
        DiagramSet a = new DiagramSet();
        DiagramSet b = new DiagramSet();
        assertEquals(a.hashCode(), b.hashCode());

        DefaultImport defaultImport = new DefaultImport("className");
        WSDLImport wsdlImport = new WSDLImport("location", "namespace");

        ImportsValue importsValue = new ImportsValue();
        importsValue.addImport(defaultImport);
        importsValue.addImport(wsdlImport);

        DiagramSet c = new DiagramSet();
        c.setImports(new Imports(importsValue));
        DiagramSet d = new DiagramSet();

        assertNotEquals(c.hashCode(), d.hashCode());
    }

    @Test
    public void testEquals() {
        DiagramSet a = new DiagramSet();
        DiagramSet b = new DiagramSet();
        assertEquals(a, b);

        DefaultImport defaultImport = new DefaultImport("className");
        WSDLImport wsdlImport = new WSDLImport("location", "namespace");

        ImportsValue importsValue = new ImportsValue();
        importsValue.addImport(defaultImport);
        importsValue.addImport(wsdlImport);

        DiagramSet c = new DiagramSet();
        c.setImports(new Imports(importsValue));
        DiagramSet d = new DiagramSet();

        assertNotEquals(c, d);
    }
}
