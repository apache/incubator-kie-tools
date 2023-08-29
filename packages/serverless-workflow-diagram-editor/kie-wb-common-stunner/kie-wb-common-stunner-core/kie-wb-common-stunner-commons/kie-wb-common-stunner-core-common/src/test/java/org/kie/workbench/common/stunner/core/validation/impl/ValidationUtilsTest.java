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


package org.kie.workbench.common.stunner.core.validation.impl;

import java.util.ArrayList;
import java.util.Collection;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.kie.workbench.common.stunner.core.validation.Violation;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;

import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.when;

@RunWith(MockitoJUnitRunner.class)
public class ValidationUtilsTest {

    @Mock
    Violation violation1;

    @Mock
    Violation violation2;

    @Mock
    Violation violation3;

    private Collection<? extends Violation> violations;

    @Before
    public void setup() {
        violations = new ArrayList<Violation>() {{
            add(violation1);
            add(violation2);
            add(violation3);
        }};
    }

    @Test
    @SuppressWarnings("unchecked")
    public void testGetMaxSeverity() {
        when(violation1.getViolationType()).thenReturn(Violation.Type.INFO);
        when(violation2.getViolationType()).thenReturn(Violation.Type.WARNING);
        when(violation3.getViolationType()).thenReturn(Violation.Type.ERROR);
        assertEquals(Violation.Type.ERROR,
                     ValidationUtils.getMaxSeverity(violations));
        when(violation3.getViolationType()).thenReturn(Violation.Type.INFO);
        assertEquals(Violation.Type.WARNING,
                     ValidationUtils.getMaxSeverity(violations));
        when(violation1.getViolationType()).thenReturn(Violation.Type.ERROR);
        assertEquals(Violation.Type.ERROR,
                     ValidationUtils.getMaxSeverity(violations));
    }
}
