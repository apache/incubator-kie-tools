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
import org.kie.workbench.common.stunner.bpmn.definition.InclusiveGateway;
import org.kie.workbench.common.stunner.bpmn.definition.property.gateway.GatewayExecutionSet;
import org.kie.workbench.common.stunner.bpmn.definition.property.general.Name;

import static org.junit.Assert.assertTrue;

public class InclusiveGatewayTest {

    private Validator validator;

    private static final String NAME_VALID = "Gateway";
    private static final String DEFAULT_ROUTE_VALID = "Straight outta gateway";

    @Before
    public void init() {
        ValidatorFactory vf = Validation.buildDefaultValidatorFactory();
        this.validator = vf.getValidator();
    }

    @Test
    public void testInclusiveDatabasedGatewayNameValid() {
        InclusiveGateway inclusiveGateway = new InclusiveGateway();
        inclusiveGateway.getGeneral().setName(new Name(NAME_VALID));
        Set<ConstraintViolation<InclusiveGateway>> violations = this.validator.validate(inclusiveGateway);
        assertTrue(violations.isEmpty());
    }

    @Test
    public void testInclusiveDatabasedGatewayNameEmpty() {
        InclusiveGateway inclusiveGateway = new InclusiveGateway();
        inclusiveGateway.getGeneral().setName(new Name(NAME_VALID));
        Set<ConstraintViolation<InclusiveGateway>> violations = this.validator.validate(inclusiveGateway);
        assertTrue(violations.isEmpty());
    }

    @Test
    public void testInclusiveDatabasedGatewayExecutionSet() {
        InclusiveGateway inclusiveGateway = new InclusiveGateway();
        inclusiveGateway.setExecutionSet(new GatewayExecutionSet());
        Set<ConstraintViolation<InclusiveGateway>> violations = this.validator.validate(inclusiveGateway);
        assertTrue(violations.isEmpty());
    }

    @Test
    public void testInclusiveDatabasedGatewayExecutionSetWithDefaultRoute() {
        InclusiveGateway inclusiveGateway = new InclusiveGateway();
        inclusiveGateway.setExecutionSet(new GatewayExecutionSet(DEFAULT_ROUTE_VALID));
        Set<ConstraintViolation<InclusiveGateway>> violations = this.validator.validate(inclusiveGateway);
        assertTrue(violations.isEmpty());

        inclusiveGateway.setExecutionSet(null);
        violations = this.validator.validate(inclusiveGateway);
        assertTrue(violations.isEmpty());
    }
}