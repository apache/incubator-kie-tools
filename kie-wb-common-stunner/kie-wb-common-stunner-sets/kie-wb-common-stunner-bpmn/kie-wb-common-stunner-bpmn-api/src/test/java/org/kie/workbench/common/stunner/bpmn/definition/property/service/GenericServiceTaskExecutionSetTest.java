/*
 * Copyright 2019 Red Hat, Inc. and/or its affiliates.
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

package org.kie.workbench.common.stunner.bpmn.definition.property.service;

import org.junit.Before;
import org.junit.Test;
import org.kie.workbench.common.stunner.bpmn.definition.property.general.SLADueDate;
import org.mockito.MockitoAnnotations;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotEquals;

public class GenericServiceTaskExecutionSetTest {

    private final static String SLA_DUE_DATE_1 = "02/17/2013";
    private final static String SLA_DUE_DATE_2 = "07/12/2017";

    @Before
    public void setUp() {
        MockitoAnnotations.initMocks(this);
    }

    @Test
    public void testHashCode() {
        GenericServiceTaskExecutionSet a = new GenericServiceTaskExecutionSet();
        GenericServiceTaskExecutionSet b = new GenericServiceTaskExecutionSet();
        assertEquals(a.hashCode(),
                     b.hashCode());

        GenericServiceTaskExecutionSet c = new GenericServiceTaskExecutionSet(new GenericServiceTaskInfo(),
                                                                              new SLADueDate(SLA_DUE_DATE_1));
        GenericServiceTaskExecutionSet d = new GenericServiceTaskExecutionSet(new GenericServiceTaskInfo(),
                                                                              new SLADueDate(SLA_DUE_DATE_1));
        assertEquals(c.hashCode(),
                     d.hashCode());
    }

    @Test
    public void testEquals() {
        GenericServiceTaskExecutionSet a = new GenericServiceTaskExecutionSet();
        GenericServiceTaskExecutionSet b = new GenericServiceTaskExecutionSet();
        assertEquals(a, b);

        GenericServiceTaskExecutionSet c = new GenericServiceTaskExecutionSet(new GenericServiceTaskInfo(),
                                                                              new SLADueDate(SLA_DUE_DATE_1));
        GenericServiceTaskExecutionSet d = new GenericServiceTaskExecutionSet(new GenericServiceTaskInfo(),
                                                                              new SLADueDate(SLA_DUE_DATE_1));
        assertEquals(c, d);
    }

    @Test
    public void testEqualFalse() {
        GenericServiceTaskExecutionSet a = new GenericServiceTaskExecutionSet();
        GenericServiceTaskExecutionSet b = new GenericServiceTaskExecutionSet();
        a.setGenericServiceTaskInfo(null);

        assertNotEquals(a, b);

        GenericServiceTaskExecutionSet c = new GenericServiceTaskExecutionSet(new GenericServiceTaskInfo(),
                                                                              new SLADueDate(SLA_DUE_DATE_1));
        GenericServiceTaskExecutionSet d = new GenericServiceTaskExecutionSet(new GenericServiceTaskInfo(),
                                                                              new SLADueDate(SLA_DUE_DATE_2));
        assertNotEquals(c, d);
    }

    @Test
    public void testEqualTrue() {
        GenericServiceTaskExecutionSet a = new GenericServiceTaskExecutionSet();
        GenericServiceTaskExecutionSet b = new GenericServiceTaskExecutionSet();
        a.setGenericServiceTaskInfo(new GenericServiceTaskInfo());
        assertEquals(a, b);

        GenericServiceTaskExecutionSet c = new GenericServiceTaskExecutionSet(new GenericServiceTaskInfo(),
                                                                              new SLADueDate(SLA_DUE_DATE_1));
        GenericServiceTaskExecutionSet d = new GenericServiceTaskExecutionSet(new GenericServiceTaskInfo(),
                                                                              new SLADueDate(SLA_DUE_DATE_1));
        assertEquals(c, d);
    }

    @Test
    public void testGetGenericServiceTaskInfo() {
        GenericServiceTaskExecutionSet a = new GenericServiceTaskExecutionSet();
        assertEquals(new GenericServiceTaskInfo(), a.getGenericServiceTaskInfo());
    }

    @Test
    public void testGetSlaDueDate() {
        GenericServiceTaskExecutionSet a = new GenericServiceTaskExecutionSet(
                new GenericServiceTaskInfo(),
                new SLADueDate(SLA_DUE_DATE_1));
        assertEquals(SLA_DUE_DATE_1, a.getSlaDueDate().getValue());
    }
}
