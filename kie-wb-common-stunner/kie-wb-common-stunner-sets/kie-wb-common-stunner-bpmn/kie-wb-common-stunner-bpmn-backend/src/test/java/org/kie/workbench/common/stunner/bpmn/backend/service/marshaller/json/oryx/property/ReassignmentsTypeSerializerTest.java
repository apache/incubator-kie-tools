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

package org.kie.workbench.common.stunner.bpmn.backend.service.marshaller.json.oryx.property;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.junit.Before;
import org.junit.Test;
import org.kie.workbench.common.stunner.bpmn.backend.marshall.json.oryx.property.ReassignmentsTypeSerializer;
import org.kie.workbench.common.stunner.bpmn.definition.property.reassignment.ReassignmentTypeListValue;
import org.kie.workbench.common.stunner.bpmn.definition.property.reassignment.ReassignmentValue;

import static org.junit.Assert.assertEquals;

public class ReassignmentsTypeSerializerTest {

    private static final String SERIALIZED = "AAA|1h|foo,bar,baz|foo,bar,baz";
    private static final String DELIMITER = "^";
    private ReassignmentsTypeSerializer serializer;

    @Before
    public void setUp() {
        serializer = new ReassignmentsTypeSerializer();
    }

    @Test
    public void testParseEmpty() {
        ReassignmentTypeListValue reassignmentTypeListValue = new ReassignmentTypeListValue();

        String result = serializer.serialize(new Object(), reassignmentTypeListValue);

        assertEquals(result,
                     "");
    }

    @Test
    public void testParseContainsOne() {
        ReassignmentTypeListValue reassignmentTypeListValue = new ReassignmentTypeListValue(getReassignmentValues());
        String result = serializer.serialize(new Object(), reassignmentTypeListValue);

        assertEquals(result, SERIALIZED);

        assertEquals(reassignmentTypeListValue, serializer.parse(result));
    }

    private List<ReassignmentValue> getReassignmentValues() {
        List<ReassignmentValue> result = new ArrayList<>();
        result.add(getReassignmentValue());
        return result;
    }

    private ReassignmentValue getReassignmentValue() {
        return new ReassignmentValue("AAA",
                                     "1h",
                                     Arrays.asList(new String[]{"foo", "bar", "baz"}),
                                     Arrays.asList(new String[]{"foo", "bar", "baz"}));
    }

    @Test
    public void testParseContainsFew() {
        ReassignmentTypeListValue reassignmentTypeListValue = new ReassignmentTypeListValue();
        reassignmentTypeListValue.addValue(getReassignmentValue());
        reassignmentTypeListValue.addValue(getReassignmentValue());
        reassignmentTypeListValue.addValue(getReassignmentValue());
        String result = serializer.serialize(new Object(), reassignmentTypeListValue);

        assertEquals(result, SERIALIZED + DELIMITER + SERIALIZED + DELIMITER + SERIALIZED);
        assertEquals(reassignmentTypeListValue, serializer.parse(result));
    }
}
