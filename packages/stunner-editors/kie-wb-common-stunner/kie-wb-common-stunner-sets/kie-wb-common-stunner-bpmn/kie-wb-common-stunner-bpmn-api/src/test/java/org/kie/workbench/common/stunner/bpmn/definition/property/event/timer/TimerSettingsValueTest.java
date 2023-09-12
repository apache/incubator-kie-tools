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


package org.kie.workbench.common.stunner.bpmn.definition.property.event.timer;

import org.junit.Before;
import org.junit.Test;
import org.kie.workbench.common.stunner.bpmn.util.EqualsAndHashCodeTestUtils;

public class TimerSettingsValueTest {

    private static final String TIME_DATE = "TIME_DATE";
    private static final String TIME_DURATION = "TIME_DURATION";
    private static final String TIME_CYCLE = "TIME_CYCLE";
    private static final String TIME_CYCLE_LANGUAGE = "TIME_CYCLE_LANGUAGE";

    private TimerSettingsValue value;

    @Before
    public void setUp() {
        value = new TimerSettingsValue(TIME_DATE,
                                       TIME_DURATION,
                                       TIME_CYCLE,
                                       TIME_CYCLE_LANGUAGE);
    }

    @Test
    public void testEqualsAndHashCode() {

        TimerSettingsValue otherValue = new TimerSettingsValue(TIME_DATE,
                                                               TIME_DURATION,
                                                               TIME_CYCLE,
                                                               TIME_CYCLE_LANGUAGE);
        EqualsAndHashCodeTestUtils.TestCaseBuilder.newTestCase()
                .addTrueCase(new TimerSettingsValue(),
                             new TimerSettingsValue())
                .addTrueCase(value,
                             otherValue)
                .addFalseCase(value,
                              null)
                .addFalseCase(value,
                              new TimerSettingsValue())
                .addFalseCase(value,
                              new TimerSettingsValue("a",
                                                     "b",
                                                     "c",
                                                     "d"))
                .test();
    }
}
