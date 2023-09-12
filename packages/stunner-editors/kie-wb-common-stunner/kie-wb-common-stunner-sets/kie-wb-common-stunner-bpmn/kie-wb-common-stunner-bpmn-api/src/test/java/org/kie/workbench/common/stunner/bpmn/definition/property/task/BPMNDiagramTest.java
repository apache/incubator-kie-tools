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


package org.kie.workbench.common.stunner.bpmn.definition.property.task;

import java.util.Set;

import javax.validation.ConstraintViolation;
import javax.validation.Validation;
import javax.validation.Validator;
import javax.validation.ValidatorFactory;

import org.junit.Before;
import org.junit.Test;
import org.kie.workbench.common.stunner.bpmn.definition.BPMNDiagramImpl;
import org.kie.workbench.common.stunner.bpmn.definition.property.diagram.DiagramSet;
import org.kie.workbench.common.stunner.bpmn.definition.property.diagram.GlobalVariables;
import org.kie.workbench.common.stunner.bpmn.definition.property.diagram.Id;
import org.kie.workbench.common.stunner.bpmn.definition.property.diagram.MetaDataAttributes;
import org.kie.workbench.common.stunner.bpmn.definition.property.diagram.Package;
import org.kie.workbench.common.stunner.bpmn.definition.property.diagram.Version;
import org.kie.workbench.common.stunner.bpmn.definition.property.dimensions.RectangleDimensionsSet;
import org.kie.workbench.common.stunner.bpmn.definition.property.general.Name;
import org.kie.workbench.common.stunner.bpmn.definition.property.variables.ProcessData;
import org.kie.workbench.common.stunner.bpmn.definition.property.variables.RootProcessAdvancedData;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotEquals;
import static org.junit.Assert.assertTrue;

public class BPMNDiagramTest {

    private Validator validator;

    private static final String NAME_VALID = "My New BP";
    private static final String NAME_INVALID = "";

    private static final String ID_VALID = "Project1.MyNewBP";
    private static final String ID_INVALID = "";

    private static final String PACKAGE_VALID = "myorg.project1";
    private static final String PACKAGE_INVALID = "";

    private static final String VERSION_VALID = "1.0";
    private static final String VERSION_INVALID = "";
    private final String GLOBAL_VARIABLES = "GV1:Boolean, GV2:Boolean, GV3:Integer";
    private final String METADATA = "securityRolesß<![CDATA[employees,managers]]>Ø securityRoles2ß<![CDATA[admin,managers]]>";

    @Before
    public void init() {
        ValidatorFactory vf = Validation.buildDefaultValidatorFactory();
        this.validator = vf.getValidator();
    }

    public BPMNDiagramImpl createValidBpmnDiagram() {
        BPMNDiagramImpl BPMNDiagramImpl = new BPMNDiagramImpl();
        DiagramSet diagramSet = BPMNDiagramImpl.getDiagramSet();
        diagramSet.setName(new Name(NAME_VALID));
        diagramSet.setId(new Id(ID_VALID));
        diagramSet.setPackageProperty(new Package(PACKAGE_VALID));
        diagramSet.setVersion(new Version(VERSION_VALID));

        return BPMNDiagramImpl;
    }

    @Test
    public void testAllValid() {
        BPMNDiagramImpl BPMNDiagramImpl = createValidBpmnDiagram();
        Set<ConstraintViolation<BPMNDiagramImpl>> violations = this.validator.validate(BPMNDiagramImpl);
        assertTrue(violations.isEmpty());
    }

    @Test
    public void testNameInvalid() {
        BPMNDiagramImpl BPMNDiagramImpl = createValidBpmnDiagram();
        BPMNDiagramImpl.getDiagramSet().setName(new Name(NAME_INVALID));
        Set<ConstraintViolation<BPMNDiagramImpl>> violations = this.validator.validate(BPMNDiagramImpl);
        assertEquals(1,
                     violations.size());
    }

    @Test
    public void testIDInvalid() {
        BPMNDiagramImpl BPMNDiagramImpl = createValidBpmnDiagram();
        BPMNDiagramImpl.getDiagramSet().setId(new Id(ID_INVALID));
        Set<ConstraintViolation<BPMNDiagramImpl>> violations = this.validator.validate(BPMNDiagramImpl);
        assertEquals(1,
                     violations.size());
    }

    @Test
    public void testPackageInvalid() {
        BPMNDiagramImpl BPMNDiagramImpl = createValidBpmnDiagram();
        BPMNDiagramImpl.getDiagramSet().setPackageProperty(new Package(PACKAGE_INVALID));
        Set<ConstraintViolation<BPMNDiagramImpl>> violations = this.validator.validate(BPMNDiagramImpl);
        assertEquals(1,
                     violations.size());
    }

    @Test
    public void testVersionInvalid() {
        BPMNDiagramImpl BPMNDiagramImpl = createValidBpmnDiagram();
        BPMNDiagramImpl.getDiagramSet().setVersion(new Version(VERSION_INVALID));
        Set<ConstraintViolation<BPMNDiagramImpl>> violations = this.validator.validate(BPMNDiagramImpl);
        assertEquals(1,
                     violations.size());
    }

    @Test
    public void testSetGlobalVariables() {
        RootProcessAdvancedData advancedData = new RootProcessAdvancedData();
        assertEquals(advancedData.getGlobalVariables(), new GlobalVariables());

        advancedData.setGlobalVariables(new GlobalVariables(GLOBAL_VARIABLES));
        assertEquals(advancedData.getGlobalVariables(), new GlobalVariables(GLOBAL_VARIABLES));
    }

    @Test
    public void testSetMetaDataAttributes() {
        RootProcessAdvancedData advancedData = new RootProcessAdvancedData();
        assertEquals(advancedData.getMetaDataAttributes(), new MetaDataAttributes());

        advancedData.setMetaDataAttributes(new MetaDataAttributes(METADATA));
        assertEquals(advancedData.getMetaDataAttributes(), new MetaDataAttributes(METADATA));
    }

    @Test
    public void testSetAdvancedData() {
        BPMNDiagramImpl BPMNDiagramImpl = createValidBpmnDiagram();
        BPMNDiagramImpl.setAdvancedData(new RootProcessAdvancedData(new GlobalVariables(GLOBAL_VARIABLES),
                                                                    new MetaDataAttributes(METADATA)));
        RootProcessAdvancedData advancedData = new RootProcessAdvancedData(new GlobalVariables(GLOBAL_VARIABLES),
                                                                           new MetaDataAttributes(METADATA));

        assertEquals(advancedData, BPMNDiagramImpl.getAdvancedData());
        assertEquals(advancedData.getGlobalVariables(), BPMNDiagramImpl.getAdvancedData().getGlobalVariables());
        assertEquals(advancedData.getMetaDataAttributes(), BPMNDiagramImpl.getAdvancedData().getMetaDataAttributes());
    }

    @Test
    public void testAdvancedDataConstructors() {
        RootProcessAdvancedData advancedData = new RootProcessAdvancedData(new GlobalVariables(GLOBAL_VARIABLES),
                                                                           new MetaDataAttributes(METADATA));
        RootProcessAdvancedData advancedData2 = new RootProcessAdvancedData(GLOBAL_VARIABLES, METADATA);

        assertEquals(advancedData, advancedData2);
    }

    @Test
    public void testNotAdvancedData() {
        ProcessData processData = new ProcessData(GLOBAL_VARIABLES);
        RootProcessAdvancedData advancedData = new RootProcessAdvancedData(new GlobalVariables(GLOBAL_VARIABLES),
                                                                           new MetaDataAttributes(METADATA));

        assertNotEquals(advancedData, processData);
    }

    @Test
    public void testNotEqualsAdvancedData() {
        BPMNDiagramImpl BPMNDiagramImpl = createValidBpmnDiagram();
        BPMNDiagramImpl.setAdvancedData(new RootProcessAdvancedData(new GlobalVariables(GLOBAL_VARIABLES),
                                                                    new MetaDataAttributes(METADATA)));
        RootProcessAdvancedData advancedData = new RootProcessAdvancedData(new GlobalVariables(), new MetaDataAttributes());

        assertNotEquals(advancedData, BPMNDiagramImpl.getAdvancedData());

        assertNotEquals(advancedData.getGlobalVariables(), BPMNDiagramImpl.getAdvancedData().getGlobalVariables());
        assertNotEquals(advancedData.getMetaDataAttributes(), BPMNDiagramImpl.getAdvancedData().getMetaDataAttributes());
    }

    @Test
    public void testBPMNDiagramEquals() {
        BPMNDiagramImpl BPMNDiagramImpl = createValidBpmnDiagram();
        BPMNDiagramImpl.setAdvancedData(new RootProcessAdvancedData(new GlobalVariables(GLOBAL_VARIABLES),
                                                                    new MetaDataAttributes(METADATA)));
        BPMNDiagramImpl BPMNDiagramImpl2 = createValidBpmnDiagram();
        BPMNDiagramImpl2.setAdvancedData(new RootProcessAdvancedData(new GlobalVariables(GLOBAL_VARIABLES),
                                                                     new MetaDataAttributes(METADATA)));

        assertEquals(BPMNDiagramImpl, BPMNDiagramImpl2);

        BPMNDiagramImpl.setAdvancedData(new RootProcessAdvancedData(new GlobalVariables("id:"),
                                                                    new MetaDataAttributes("securityRoles3ß<![CDATA[employees,clients]]>")));
        assertNotEquals(BPMNDiagramImpl, BPMNDiagramImpl2);

        BPMNDiagramImpl.setAdvancedData(new RootProcessAdvancedData(new GlobalVariables(GLOBAL_VARIABLES),
                                                                    new MetaDataAttributes("securityRoles3ß<![CDATA[employees,clients]]>")));
        assertNotEquals(BPMNDiagramImpl, BPMNDiagramImpl2);

        BPMNDiagramImpl.setAdvancedData(new RootProcessAdvancedData(new GlobalVariables("id:"),
                                                                    new MetaDataAttributes(METADATA)));
        assertNotEquals(BPMNDiagramImpl, BPMNDiagramImpl2);

        BPMNDiagramImpl.setAdvancedData(new RootProcessAdvancedData(new GlobalVariables(GLOBAL_VARIABLES),
                                                                    new MetaDataAttributes(METADATA)));
        assertEquals(BPMNDiagramImpl, BPMNDiagramImpl2);

        BPMNDiagramImpl.setDimensionsSet(new RectangleDimensionsSet(10d, 10d));
        BPMNDiagramImpl2.setDimensionsSet(new RectangleDimensionsSet(20d, 20d));
        assertNotEquals(BPMNDiagramImpl, BPMNDiagramImpl2);

        BPMNDiagramImpl2.setDimensionsSet(new RectangleDimensionsSet(10d, 10d));
        assertEquals(BPMNDiagramImpl, BPMNDiagramImpl2);
    }
}