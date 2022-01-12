/*
 * Copyright 2017 Red Hat, Inc. and/or its affiliates.
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

package org.kie.workbench.common.stunner.bpmn.definition.property.task;

import java.util.Set;

import javax.validation.ConstraintViolation;
import javax.validation.Validation;
import javax.validation.Validator;
import javax.validation.ValidatorFactory;

import org.junit.Before;
import org.junit.Test;
import org.kie.workbench.common.stunner.bpmn.definition.ExclusiveGateway;
import org.kie.workbench.common.stunner.bpmn.definition.property.gateway.GatewayExecutionSet;
import org.kie.workbench.common.stunner.bpmn.definition.property.general.Name;

import static org.junit.Assert.assertTrue;

public class ExclusiveGatewayTest {

    private Validator validator;

    private static final String NAME_VALID = "Gateway";
    private static final String DEFAULT_ROUTE_VALID = "Straight outta gateway";

    @Before
    public void init() {
        ValidatorFactory vf = Validation.buildDefaultValidatorFactory();
        this.validator = vf.getValidator();
    }

    @Test
    public void testExclusiveDatabasedGatewayNameValid() {
        ExclusiveGateway exclusiveGateway = new ExclusiveGateway();
        exclusiveGateway.getGeneral().setName(new Name(NAME_VALID));
        Set<ConstraintViolation<ExclusiveGateway>> violations = this.validator.validate(exclusiveGateway);
        assertTrue(violations.isEmpty());
    }

    @Test
    public void testExclusiveDatabasedGatewayNameEmpty() {
        ExclusiveGateway exclusiveGateway = new ExclusiveGateway();
        exclusiveGateway.getGeneral().setName(new Name(""));
        Set<ConstraintViolation<ExclusiveGateway>> violations = this.validator.validate(exclusiveGateway);
        assertTrue(violations.isEmpty());
    }

    @Test
    public void testExclusiveDatabasedGatewayExecutionSet() {
        ExclusiveGateway exclusiveGateway = new ExclusiveGateway();
        exclusiveGateway.setExecutionSet(new GatewayExecutionSet());
        Set<ConstraintViolation<ExclusiveGateway>> violations = this.validator.validate(exclusiveGateway);
        assertTrue(violations.isEmpty());
    }

    @Test
    public void testExclusiveDatabasedGatewayExecutionSetWithDefaultRoute() {
        ExclusiveGateway exclusiveGateway = new ExclusiveGateway();
        exclusiveGateway.setExecutionSet(new GatewayExecutionSet(DEFAULT_ROUTE_VALID));
        Set<ConstraintViolation<ExclusiveGateway>> violations = this.validator.validate(exclusiveGateway);
        assertTrue(violations.isEmpty());
    }
}
