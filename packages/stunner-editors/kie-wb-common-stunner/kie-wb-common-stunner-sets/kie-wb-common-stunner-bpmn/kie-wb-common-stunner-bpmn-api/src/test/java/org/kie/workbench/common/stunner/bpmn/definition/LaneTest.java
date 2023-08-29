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


package org.kie.workbench.common.stunner.bpmn.definition;

import java.util.List;
import java.util.Set;

import org.junit.Test;
import org.kie.workbench.common.stunner.core.backend.definition.adapter.ReflectionAdapterUtils;
import org.kie.workbench.common.stunner.core.definition.annotation.definition.Labels;
import org.kie.workbench.common.stunner.core.rule.annotation.CanContain;

import static java.util.Arrays.asList;
import static java.util.Collections.singletonList;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;

public class LaneTest {

    @Test
    public void testLaneCanContain() {

        final Class<Lane> laneClass = Lane.class;
        final CanContain canContain = laneClass.getAnnotation(CanContain.class);

        final List<String> expectedRoles = singletonList("lane_child");
        final List<String> actualRoles = asList(canContain.roles());

        assertEquals(expectedRoles, actualRoles);
    }

    @Test
    public void testLaneCannotContainAnotherLane() throws Exception {

        final Lane lane = new Lane();
        final Set<String> labels = ReflectionAdapterUtils.getAnnotatedFieldValue(lane, Labels.class);

        assertNotNull(labels);
        assertFalse(labels.contains("lane_child"));
    }
}
