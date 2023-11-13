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


package org.kie.workbench.common.stunner.bpmn.client.marshall.converters.fromstunner.properties;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;

import org.apache.commons.lang3.reflect.FieldUtils;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.kie.workbench.common.stunner.bpmn.client.marshall.converters.customproperties.CustomInput;
import org.kie.workbench.common.stunner.bpmn.client.marshall.converters.fromstunner.associations.AssociationType;
import org.kie.workbench.common.stunner.bpmn.definition.property.reassignment.ReassignmentValue;
import org.kie.workbench.common.stunner.bpmn.definition.property.reassignment.ReassignmentsInfo;

import static org.kie.workbench.common.stunner.bpmn.client.marshall.converters.fromstunner.Factories.bpmn2;

public class ReassignmentPropertyWriterTest {

    private FlatVariableScope variableScope;

    @Before
    public void before() {
        this.variableScope = new FlatVariableScope();
    }

    @Test
    public void addOneNotStartedReassignmentValue() {
        UserTaskPropertyWriter userTaskPropertyWriter = new UserTaskPropertyWriter(bpmn2.createUserTask(), variableScope, new HashSet<>());

        ReassignmentsInfo reassignmentsInfo = new ReassignmentsInfo();
        reassignmentsInfo.getValue().addValue(getReassignmentValue(AssociationType.NOT_STARTED_REASSIGN.getName()));
        userTaskPropertyWriter.setReassignments(reassignmentsInfo);

        CustomInput<String> notStartedNotify = getFieldValue(UserTaskPropertyWriter.class, userTaskPropertyWriter, "notStartedReassign");
        Assert.assertEquals(asCDATA(getReassignmentValue(AssociationType.NOT_STARTED_REASSIGN.getName()).toCDATAFormat()), notStartedNotify.get());
    }

    @Test
    public void addTwoNotStartedReassignmentValue() {
        UserTaskPropertyWriter userTaskPropertyWriter = new UserTaskPropertyWriter(bpmn2.createUserTask(), variableScope, new HashSet<>());

        ReassignmentsInfo reassignmentsInfo = new ReassignmentsInfo();
        reassignmentsInfo.getValue().addValue(getReassignmentValue(AssociationType.NOT_COMPLETED_REASSIGN.getName()));
        reassignmentsInfo.getValue().addValue(getReassignmentValue(AssociationType.NOT_COMPLETED_REASSIGN.getName()));
        userTaskPropertyWriter.setReassignments(reassignmentsInfo);

        String cdata = getReassignmentValue(AssociationType.NOT_COMPLETED_REASSIGN.getName()).toCDATAFormat();

        CustomInput<String> notStartedNotify = getFieldValue(UserTaskPropertyWriter.class, userTaskPropertyWriter, "notCompletedReassign");
        Assert.assertEquals(asCDATA(cdata + "^" + cdata), notStartedNotify.get());
    }

    @Test
    public void addOneNotCompletedReassignmentValue() {
        UserTaskPropertyWriter userTaskPropertyWriter = new UserTaskPropertyWriter(bpmn2.createUserTask(), variableScope, new HashSet<>());

        ReassignmentsInfo reassignmentsInfo = new ReassignmentsInfo();
        reassignmentsInfo.getValue().addValue(getReassignmentValue(AssociationType.NOT_COMPLETED_REASSIGN.getName()));
        userTaskPropertyWriter.setReassignments(reassignmentsInfo);

        CustomInput<String> notStartedNotify = getFieldValue(UserTaskPropertyWriter.class, userTaskPropertyWriter, "notCompletedReassign");
        Assert.assertEquals(asCDATA(getReassignmentValue(AssociationType.NOT_COMPLETED_REASSIGN.getName()).toCDATAFormat()), notStartedNotify.get());
    }

    @Test
    public void addTwoNotCompletedReassignmentValue() {
        UserTaskPropertyWriter userTaskPropertyWriter = new UserTaskPropertyWriter(bpmn2.createUserTask(), variableScope, new HashSet<>());

        ReassignmentsInfo reassignmentsInfo = new ReassignmentsInfo();
        reassignmentsInfo.getValue().addValue(getReassignmentValue(AssociationType.NOT_COMPLETED_REASSIGN.getName()));
        reassignmentsInfo.getValue().addValue(getReassignmentValue(AssociationType.NOT_COMPLETED_REASSIGN.getName()));
        userTaskPropertyWriter.setReassignments(reassignmentsInfo);

        String cdata = getReassignmentValue(AssociationType.NOT_COMPLETED_REASSIGN.getName()).toCDATAFormat();

        CustomInput<String> notStartedNotify = getFieldValue(UserTaskPropertyWriter.class, userTaskPropertyWriter, "notCompletedReassign");
        Assert.assertEquals(asCDATA(cdata + "^" + cdata), notStartedNotify.get());
    }

    protected <T> T getFieldValue(Class parent, Object instance, String fieldName) {
        Field inputField = FieldUtils.getField(parent, fieldName, true);
        try {
            return (T) inputField.get(instance);
        } catch (IllegalAccessException e) {
            throw new Error(e);
        }
    }

    private String asCDATA(String value) {
        return "<![CDATA[" + value + "]]>";
    }

    private ReassignmentValue getReassignmentValue(String type) {
        List<String> users = new ArrayList<>();
        users.add("AAA");
        users.add("BBB");

        List<String> groups = new ArrayList<>();
        users.add("G1");
        users.add("G2");

        ReassignmentValue value = new ReassignmentValue();
        value.setType(type);
        value.setUsers(users);
        value.setGroups(groups);
        value.setDuration("1d");

        return value;
    }
}
