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


package org.kie.workbench.common.stunner.core.client.command;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.kie.workbench.common.stunner.core.rule.RuleViolation;
import org.kie.workbench.common.stunner.core.rule.violations.RuleViolationImpl;
import org.mockito.junit.MockitoJUnitRunner;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

@RunWith(MockitoJUnitRunner.class)
public class CanvasViolationImplTest {

    private static final String MESSAGE = "m1";
    private static final String UUID = "uuid1";

    private RuleViolationImpl ruleViolation;

    @Before
    public void setup() throws Exception {
        ruleViolation = new RuleViolationImpl(MESSAGE);
        ruleViolation.setUUID(UUID);
    }

    @Test
    @SuppressWarnings("unchecked")
    public void testBuild() {
        final CanvasViolation canvasViolation = CanvasViolationImpl.Builder.build(ruleViolation);
        assertNotNull(canvasViolation);
        assertEquals(RuleViolation.Type.ERROR,
                     canvasViolation.getViolationType());
        assertEquals(MESSAGE,
                     canvasViolation.getArguments().get()[0]);
        assertEquals(UUID,
                     canvasViolation.getUUID());
    }
}
