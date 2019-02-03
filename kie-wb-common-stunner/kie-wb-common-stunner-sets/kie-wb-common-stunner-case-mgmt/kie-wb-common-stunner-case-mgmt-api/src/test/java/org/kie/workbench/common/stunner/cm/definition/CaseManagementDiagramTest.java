/*
 * Copyright 2018 Red Hat, Inc. and/or its affiliates.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.kie.workbench.common.stunner.cm.definition;

import java.util.Set;

import javax.validation.ConstraintViolation;
import javax.validation.Validation;
import javax.validation.Validator;
import javax.validation.ValidatorFactory;

import org.junit.Before;
import org.junit.Test;
import org.kie.workbench.common.stunner.bpmn.definition.property.diagram.Id;
import org.kie.workbench.common.stunner.bpmn.definition.property.general.Name;
import org.kie.workbench.common.stunner.cm.definition.property.diagram.DiagramSet;
import org.kie.workbench.common.stunner.cm.definition.property.diagram.Package;
import org.kie.workbench.common.stunner.cm.definition.property.diagram.Version;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

public class CaseManagementDiagramTest {

    private static final String NAME_VALID = "My New CM";
    private static final String NAME_INVALID = "";
    private static final String ID_VALID = "Project1.MyNewCM";
    private static final String ID_INVALID = "";
    private static final String PACKAGE_VALID = "myorg.project1";
    private static final String PACKAGE_INVALID = "";
    private static final String VERSION_VALID = "1.0";
    private static final String VERSION_INVALID = "";
    private Validator validator;
    private CaseManagementDiagram tested;

    @Before
    public void init() {
        ValidatorFactory vf = Validation.buildDefaultValidatorFactory();
        this.validator = vf.getValidator();
    }

    @Before
    public void setup() {
        tested = new CaseManagementDiagram();
        DiagramSet diagramSet = tested.getDiagramSet();
        diagramSet.setName(new Name(NAME_VALID));
        diagramSet.setId(new Id(ID_VALID));
        diagramSet.setPackageProperty(new Package(PACKAGE_VALID));
        diagramSet.setVersion(new Version(VERSION_VALID));
    }

    @Test
    public void testAllValid() {
        Set<ConstraintViolation<CaseManagementDiagram>> violations = this.validator.validate(tested);
        assertTrue(violations.isEmpty());
    }

    @Test
    public void testNameInvalid() {
        tested.getDiagramSet().setName(new Name(NAME_INVALID));
        Set<ConstraintViolation<CaseManagementDiagram>> violations = this.validator.validate(tested);
        assertEquals(1, violations.size());
    }

    @Test
    public void testIDInvalid() {
        tested.getDiagramSet().setId(new Id(ID_INVALID));
        Set<ConstraintViolation<CaseManagementDiagram>> violations = this.validator.validate(tested);
        assertEquals(1, violations.size());
    }

    @Test
    public void testPackageInvalid() {
        tested.getDiagramSet().setPackageProperty(new Package(PACKAGE_INVALID));
        Set<ConstraintViolation<CaseManagementDiagram>> violations = this.validator.validate(tested);
        assertEquals(1, violations.size());
    }

    @Test
    public void testVersionInvalid() {
        tested.getDiagramSet().setVersion(new Version(VERSION_INVALID));
        Set<ConstraintViolation<CaseManagementDiagram>> violations = this.validator.validate(tested);
        assertEquals(1, violations.size());
    }
}
