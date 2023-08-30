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


package org.kie.workbench.common.stunner.bpmn.client.marshall.converters.tostunner.properties;

import java.util.ArrayList;
import java.util.Arrays;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.kie.workbench.common.stunner.bpmn.client.marshall.converters.customproperties.ParsedReassignmentsInfos;
import org.kie.workbench.common.stunner.bpmn.client.marshall.converters.fromstunner.associations.AssociationType;
import org.kie.workbench.common.stunner.bpmn.definition.property.reassignment.ReassignmentValue;
import org.mockito.junit.MockitoJUnitRunner;

import static org.junit.Assert.assertEquals;

@RunWith(MockitoJUnitRunner.class)
public class ParsedReassignmentsInfosTest {

    @Test
    public void testReassignment() {
        String body = "[users:Forms,Reviewer,manager|groups:director,john]@[33y]";
        ReassignmentValue actual = ParsedReassignmentsInfos.of(AssociationType.NOT_COMPLETED_REASSIGN.getName(), body);
        ReassignmentValue expected = new ReassignmentValue();
        expected.setType(AssociationType.NOT_COMPLETED_REASSIGN.getName());
        expected.setDuration("33y");
        expected.setGroups(new ArrayList<>(Arrays.asList("director", "john")));
        expected.setUsers(new ArrayList<>(Arrays.asList("Forms", "Reviewer", "manager")));

        assertEquals(expected, actual);
    }

    @Test
    public void testReassignmentPartial() {
        String body = "[users:|groups:]@[33h]";
        ReassignmentValue actual = ParsedReassignmentsInfos.of(AssociationType.NOT_COMPLETED_REASSIGN.getName(), body);
        ReassignmentValue expected = new ReassignmentValue();
        expected.setType(AssociationType.NOT_COMPLETED_REASSIGN.getName());
        expected.setDuration("33h");

        assertEquals(expected.toString(), actual.toString());
        assertEquals(expected.toCDATAFormat(), actual.toCDATAFormat());
        assertEquals(expected, actual);
    }

    @Test
    public void testNotificationEmpty() {
        ReassignmentValue value = ParsedReassignmentsInfos.of(AssociationType.NOT_COMPLETED_REASSIGN.getName(), "");
        ReassignmentValue valid = new ReassignmentValue();
        valid.setType(AssociationType.NOT_COMPLETED_REASSIGN.getName());
        assertEquals(valid, value);
    }
}
