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

package org.kie.workbench.common.stunner.bpmn.definition.property.task;

import java.util.Arrays;
import java.util.Collections;

import org.junit.Test;
import org.kie.workbench.common.stunner.bpmn.definition.property.reassignment.ReassignmentValue;
import org.kie.workbench.common.stunner.bpmn.util.EqualsAndHashCodeTestUtils;

public class ReassignmentValueTest {

    @Test
    public void testEqualsAndHashCode() {
        EqualsAndHashCodeTestUtils.TestCaseBuilder.newTestCase()
                .addTrueCase(new ReassignmentValue(),
                             new ReassignmentValue())
                .addTrueCase(new ReassignmentValue("AAA",
                                                   "1h",
                                                   Collections.EMPTY_LIST,
                                                   Collections.EMPTY_LIST),
                             new ReassignmentValue("AAA",
                                                   "1h",
                                                   Collections.EMPTY_LIST,
                                                   Collections.EMPTY_LIST))

                .addTrueCase(new ReassignmentValue("AAA",
                                                   "1h",
                                                   Collections.EMPTY_LIST,
                                                   Arrays.asList(new String[]{"foo", "bar", "baz"})),
                             new ReassignmentValue("AAA",
                                                   "1h",
                                                   Collections.EMPTY_LIST,
                                                   Arrays.asList(new String[]{"foo", "bar", "baz"})))
                .addTrueCase(new ReassignmentValue("AAA",
                                                   "1h",
                                                   Arrays.asList(new String[]{"foo", "bar", "baz"}),
                                                   Arrays.asList(new String[]{"foo", "bar", "baz"})),
                             new ReassignmentValue("AAA",
                                                   "1h",
                                                   Arrays.asList(new String[]{"foo", "bar", "baz"}),
                                                   Arrays.asList(new String[]{"foo", "bar", "baz"})))

                .addFalseCase(new ReassignmentValue("AAA1",
                                                    "1h1",
                                                    Collections.EMPTY_LIST,
                                                    Collections.EMPTY_LIST),
                              new ReassignmentValue("AAA",
                                                    "1h",
                                                    Collections.EMPTY_LIST,
                                                    Collections.EMPTY_LIST))

                .addFalseCase(new ReassignmentValue("AAA",
                                                    "1h",
                                                    Collections.EMPTY_LIST,
                                                    Arrays.asList(new String[]{"foo1", "bar", "baz"})),
                              new ReassignmentValue("AAA",
                                                    "1h",
                                                    Collections.EMPTY_LIST,
                                                    Arrays.asList(new String[]{"foo", "bar", "baz"})))
                .addFalseCase(new ReassignmentValue("AAA",
                                                    "1h",
                                                    Arrays.asList(new String[]{"foo", "bar", "baz"}),
                                                    Arrays.asList(new String[]{"foo1", "bar", "baz"})),
                              new ReassignmentValue("AAA",
                                                    "1h",
                                                    Arrays.asList(new String[]{"foo", "bar", "baz"}),
                                                    Arrays.asList(new String[]{"foo", "bar", "baz"})))
                .test();
    }
}
