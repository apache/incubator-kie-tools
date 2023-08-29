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


package org.kie.workbench.common.stunner.bpmn.definition.property.service;

import org.junit.Before;
import org.junit.Test;
import org.kie.workbench.common.stunner.bpmn.definition.property.dataio.AssignmentsInfo;
import org.kie.workbench.common.stunner.bpmn.definition.property.general.SLADueDate;
import org.kie.workbench.common.stunner.bpmn.definition.property.task.AdHocAutostart;
import org.kie.workbench.common.stunner.bpmn.definition.property.task.IsAsync;
import org.kie.workbench.common.stunner.bpmn.definition.property.task.IsMultipleInstance;
import org.kie.workbench.common.stunner.bpmn.definition.property.task.MultipleInstanceCollectionInput;
import org.kie.workbench.common.stunner.bpmn.definition.property.task.MultipleInstanceCollectionOutput;
import org.kie.workbench.common.stunner.bpmn.definition.property.task.MultipleInstanceCompletionCondition;
import org.kie.workbench.common.stunner.bpmn.definition.property.task.MultipleInstanceDataInput;
import org.kie.workbench.common.stunner.bpmn.definition.property.task.MultipleInstanceDataOutput;
import org.kie.workbench.common.stunner.bpmn.definition.property.task.MultipleInstanceExecutionMode;
import org.kie.workbench.common.stunner.bpmn.definition.property.task.OnEntryAction;
import org.kie.workbench.common.stunner.bpmn.definition.property.task.OnExitAction;
import org.kie.workbench.common.stunner.bpmn.definition.property.task.ScriptTypeListValue;
import org.kie.workbench.common.stunner.bpmn.definition.property.task.ScriptTypeValue;
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

        GenericServiceTaskExecutionSet c = new GenericServiceTaskExecutionSet(new GenericServiceTaskInfo(),new AssignmentsInfo(),
                                                                              new AdHocAutostart(),
                                                                              new IsAsync(),
                                                                              new IsMultipleInstance(false),
                                                                              new MultipleInstanceExecutionMode(false),
                                                                              new MultipleInstanceCollectionInput(),
                                                                              new MultipleInstanceDataInput(),
                                                                              new MultipleInstanceCollectionOutput(),
                                                                              new MultipleInstanceDataOutput(),
                                                                              new MultipleInstanceCompletionCondition(),
                                                                              new OnEntryAction(new ScriptTypeListValue().addValue(new ScriptTypeValue("java",
                                                                                                                                                       ""))),
                                                                              new OnExitAction(new ScriptTypeListValue().addValue(new ScriptTypeValue("java",
                                                                                                                                                      ""))),
                                                                              new SLADueDate(SLA_DUE_DATE_1));
        GenericServiceTaskExecutionSet d = new GenericServiceTaskExecutionSet(new GenericServiceTaskInfo(),
                                                                              new AssignmentsInfo(),
                                                                              new AdHocAutostart(),
                                                                              new IsAsync(),
                                                                              new IsMultipleInstance(false),
                                                                              new MultipleInstanceExecutionMode(false),
                                                                              new MultipleInstanceCollectionInput(),
                                                                              new MultipleInstanceDataInput(),
                                                                              new MultipleInstanceCollectionOutput(),
                                                                              new MultipleInstanceDataOutput(),
                                                                              new MultipleInstanceCompletionCondition(),
                                                                              new OnEntryAction(new ScriptTypeListValue().addValue(new ScriptTypeValue("java",
                                                                                                                                                       ""))),
                                                                              new OnExitAction(new ScriptTypeListValue().addValue(new ScriptTypeValue("java",
                                                                                                                                                      ""))),
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
                                                                              new AssignmentsInfo(),
                                                                              new AdHocAutostart(),
                                                                              new IsAsync(),
                                                                              new IsMultipleInstance(false),
                                                                              new MultipleInstanceExecutionMode(false),
                                                                              new MultipleInstanceCollectionInput(),
                                                                              new MultipleInstanceDataInput(),
                                                                              new MultipleInstanceCollectionOutput(),
                                                                              new MultipleInstanceDataOutput(),
                                                                              new MultipleInstanceCompletionCondition(),
                                                                              new OnEntryAction(new ScriptTypeListValue().addValue(new ScriptTypeValue("java",
                                                                                                                                                       ""))),
                                                                              new OnExitAction(new ScriptTypeListValue().addValue(new ScriptTypeValue("java",
                                                                                                                                                      ""))),
                                                                              new SLADueDate(SLA_DUE_DATE_1));
        GenericServiceTaskExecutionSet d = new GenericServiceTaskExecutionSet(new GenericServiceTaskInfo(),
                                                                              new AssignmentsInfo(),
                                                                              new AdHocAutostart(),
                                                                              new IsAsync(),
                                                                              new IsMultipleInstance(false),
                                                                              new MultipleInstanceExecutionMode(false),
                                                                              new MultipleInstanceCollectionInput(),
                                                                              new MultipleInstanceDataInput(),
                                                                              new MultipleInstanceCollectionOutput(),
                                                                              new MultipleInstanceDataOutput(),
                                                                              new MultipleInstanceCompletionCondition(),
                                                                              new OnEntryAction(new ScriptTypeListValue().addValue(new ScriptTypeValue("java",
                                                                                                                                                       ""))),
                                                                              new OnExitAction(new ScriptTypeListValue().addValue(new ScriptTypeValue("java",
                                                                                                                                                      ""))),
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
                                                                              new AssignmentsInfo(),
                                                                              new AdHocAutostart(),
                                                                              new IsAsync(),
                                                                              new IsMultipleInstance(false),
                                                                              new MultipleInstanceExecutionMode(false),
                                                                              new MultipleInstanceCollectionInput(),
                                                                              new MultipleInstanceDataInput(),
                                                                              new MultipleInstanceCollectionOutput(),
                                                                              new MultipleInstanceDataOutput(),
                                                                              new MultipleInstanceCompletionCondition(),
                                                                              new OnEntryAction(new ScriptTypeListValue().addValue(new ScriptTypeValue("java",
                                                                                                                                                       ""))),
                                                                              new OnExitAction(new ScriptTypeListValue().addValue(new ScriptTypeValue("java",
                                                                                                                                                      ""))),
                                                                              new SLADueDate(SLA_DUE_DATE_1));
        GenericServiceTaskExecutionSet d = new GenericServiceTaskExecutionSet(new GenericServiceTaskInfo(),
                                                                              new AssignmentsInfo(),
                                                                              new AdHocAutostart(),
                                                                              new IsAsync(),
                                                                              new IsMultipleInstance(false),
                                                                              new MultipleInstanceExecutionMode(false),
                                                                              new MultipleInstanceCollectionInput(),
                                                                              new MultipleInstanceDataInput(),
                                                                              new MultipleInstanceCollectionOutput(),
                                                                              new MultipleInstanceDataOutput(),
                                                                              new MultipleInstanceCompletionCondition(),
                                                                              new OnEntryAction(new ScriptTypeListValue().addValue(new ScriptTypeValue("java",
                                                                                                                                                       ""))),
                                                                              new OnExitAction(new ScriptTypeListValue().addValue(new ScriptTypeValue("java",
                                                                                                                                                      ""))),
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
                                                                              new AssignmentsInfo(),
                                                                              new AdHocAutostart(),
                                                                              new IsAsync(),
                                                                              new IsMultipleInstance(false),
                                                                              new MultipleInstanceExecutionMode(false),
                                                                              new MultipleInstanceCollectionInput(),
                                                                              new MultipleInstanceDataInput(),
                                                                              new MultipleInstanceCollectionOutput(),
                                                                              new MultipleInstanceDataOutput(),
                                                                              new MultipleInstanceCompletionCondition(),
                                                                              new OnEntryAction(new ScriptTypeListValue().addValue(new ScriptTypeValue("java",
                                                                                                                                                       ""))),
                                                                              new OnExitAction(new ScriptTypeListValue().addValue(new ScriptTypeValue("java",
                                                                                                                                                      ""))),
                                                                              new SLADueDate(SLA_DUE_DATE_1));
        GenericServiceTaskExecutionSet d = new GenericServiceTaskExecutionSet(new GenericServiceTaskInfo(),
                                                                              new AssignmentsInfo(),
                                                                              new AdHocAutostart(),
                                                                              new IsAsync(),
                                                                              new IsMultipleInstance(false),
                                                                              new MultipleInstanceExecutionMode(false),
                                                                              new MultipleInstanceCollectionInput(),
                                                                              new MultipleInstanceDataInput(),
                                                                              new MultipleInstanceCollectionOutput(),
                                                                              new MultipleInstanceDataOutput(),
                                                                              new MultipleInstanceCompletionCondition(),
                                                                              new OnEntryAction(new ScriptTypeListValue().addValue(new ScriptTypeValue("java",
                                                                                                                                                       ""))),
                                                                              new OnExitAction(new ScriptTypeListValue().addValue(new ScriptTypeValue("java",
                                                                                                                                                      ""))),
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
                new AssignmentsInfo(),
                new AdHocAutostart(),
                new IsAsync(),
                new IsMultipleInstance(false),
                new MultipleInstanceExecutionMode(false),
                new MultipleInstanceCollectionInput(),
                new MultipleInstanceDataInput(),
                new MultipleInstanceCollectionOutput(),
                new MultipleInstanceDataOutput(),
                new MultipleInstanceCompletionCondition(),
                new OnEntryAction(new ScriptTypeListValue().addValue(new ScriptTypeValue("java",
                                                                                         ""))),
                new OnExitAction(new ScriptTypeListValue().addValue(new ScriptTypeValue("java",
                                                                                        ""))),
                new SLADueDate(SLA_DUE_DATE_1));
        assertEquals(SLA_DUE_DATE_1, a.getSlaDueDate().getValue());
    }
}
