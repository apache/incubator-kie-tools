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
import org.kie.workbench.common.stunner.bpmn.definition.ParallelGateway;
import org.kie.workbench.common.stunner.bpmn.definition.property.general.Name;

import static org.junit.Assert.assertTrue;

public class ParallelGatewayTest {

    private Validator validator;

    private static final String NAME_VALID = "Gateway";

    @Before
    public void init() {
        ValidatorFactory vf = Validation.buildDefaultValidatorFactory();
        this.validator = vf.getValidator();
    }

    @Test
    public void testParallelDatabasedGatewayNameValid() {
        ParallelGateway parallelGateway = new ParallelGateway();
        parallelGateway.getGeneral().setName(new Name(NAME_VALID));
        Set<ConstraintViolation<ParallelGateway>> violations = this.validator.validate(parallelGateway);
        assertTrue(violations.isEmpty());
    }

    @Test
    public void testParallelDatabasedGatewayNameEmpty() {
        ParallelGateway parallelGateway = new ParallelGateway();
        parallelGateway.getGeneral().setName(new Name(""));
        Set<ConstraintViolation<ParallelGateway>> violations = this.validator.validate(parallelGateway);
        assertTrue(violations.isEmpty());
    }
}