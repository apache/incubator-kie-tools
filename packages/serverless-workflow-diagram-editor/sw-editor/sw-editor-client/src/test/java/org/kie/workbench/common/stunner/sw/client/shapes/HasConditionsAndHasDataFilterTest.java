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


package org.kie.workbench.common.stunner.sw.client.shapes;

import org.junit.Test;
import org.kie.workbench.common.stunner.sw.definition.DataConditionTransition;
import org.kie.workbench.common.stunner.sw.definition.EventConditionTransition;

import static org.kie.workbench.common.stunner.sw.resources.i18n.SWConstants.CONDITION_IS_NULL;
import static org.kie.workbench.common.stunner.sw.resources.i18n.SWConstants.DATA_CONDITION_TRANSITION;
import static org.kie.workbench.common.stunner.sw.resources.i18n.SWConstants.DATA_CONDITION_TRANSITION_NAME;
import static org.kie.workbench.common.stunner.sw.resources.i18n.SWConstants.DATA_CONDITION_TRANSITION_VALUE;
import static org.kie.workbench.common.stunner.sw.resources.i18n.SWConstants.EVENT_CONDITION_TRANSITION;
import static org.kie.workbench.common.stunner.sw.resources.i18n.SWConstants.EVENT_CONDITION_TRANSITION_NAME;
import static org.kie.workbench.common.stunner.sw.resources.i18n.SWConstants.EVENT_CONDITION_TRANSITION_VALUE;

public class HasConditionsAndHasDataFilterTest extends HasTranslationGeneralTest {

    private final HasConditionsAndHasDataFilter hasConditions = HasConditionsAndHasDataFilterTest.super::getTranslation;

    @Test
    public void testGetConditionsStringNullInputs() {
        assertTranslations(TEST_STRING,
                           hasConditions.getConditionsString(null, null),
                           CONDITION_IS_NULL);
    }

    @Test
    public void testGetConditionsStringNullAndEmptyInputs() {
        assertTranslations(TEST_STRING,
                           hasConditions.getConditionsString(new DataConditionTransition[0], null),
                           CONDITION_IS_NULL);
    }

    @Test
    public void testGetConditionsStringEmptyAndNullInputs() {
        assertTranslations(TEST_STRING,
                           hasConditions.getConditionsString(null, new EventConditionTransition[0]),
                           CONDITION_IS_NULL);
    }

    @Test
    public void testGetConditionsStringEmptyInputs() {
        assertTranslations(TEST_STRING,
                           hasConditions.getConditionsString(new DataConditionTransition[0], new EventConditionTransition[0]),
                           CONDITION_IS_NULL);
    }

    @Test
    public void testGetConditionsStringDataOnly() {
        DataConditionTransition dataTransition1 = new DataConditionTransition();
        dataTransition1.setName("data1");
        dataTransition1.setCondition("value1");
        DataConditionTransition dataTransition2 = new DataConditionTransition();
        dataTransition2.setCondition("value2");
        DataConditionTransition dataTransition3 = new DataConditionTransition();
        dataTransition3.setName("data3");
        DataConditionTransition[] data = {
                dataTransition1,
                dataTransition2,
                dataTransition3
        };
        String expected = TEST_STRING + ":\r\n" +
                TEST_STRING + ": data1\r\n" +
                TEST_STRING + ": value2\r\n" +
                TEST_STRING + ": data3\r\n";

        assertTranslations(expected,
                           hasConditions.getConditionsString(data, new EventConditionTransition[0]),
                           DATA_CONDITION_TRANSITION,
                           DATA_CONDITION_TRANSITION_NAME,
                           DATA_CONDITION_TRANSITION_VALUE,
                           DATA_CONDITION_TRANSITION_NAME);
    }

    @Test
    public void testGetConditionsStringEventsOnly() {
        EventConditionTransition eventTransition1 = new EventConditionTransition();
        eventTransition1.setName("event1");
        eventTransition1.setEventRef("eventRef1");
        EventConditionTransition eventTransition2 = new EventConditionTransition();
        eventTransition2.setEventRef("eventRef2");
        EventConditionTransition eventTransition3 = new EventConditionTransition();
        eventTransition3.setName("event3");
        EventConditionTransition[] events = {
                eventTransition1,
                eventTransition2,
                eventTransition3
        };
        String expected = TEST_STRING + ":\r\n" +
                TEST_STRING + ": event1\r\n" +
                TEST_STRING + ": eventRef2\r\n" +
                TEST_STRING + ": event3\r\n";
        assertTranslations(expected,
                           hasConditions.getConditionsString(new DataConditionTransition[0], events),
                           EVENT_CONDITION_TRANSITION,
                           EVENT_CONDITION_TRANSITION_NAME,
                           EVENT_CONDITION_TRANSITION_VALUE,
                           EVENT_CONDITION_TRANSITION_NAME);
    }

    @Test
    public void testGetConditionsStringMixedInputs() {
        DataConditionTransition dataTransition1 = new DataConditionTransition();
        dataTransition1.setName("data1");
        dataTransition1.setCondition("value1");
        DataConditionTransition dataTransition2 = new DataConditionTransition();
        dataTransition2.setCondition("value2");
        DataConditionTransition dataTransition3 = new DataConditionTransition();
        dataTransition3.setName("data3");
        dataTransition3.setCondition("value3");
        DataConditionTransition[] data = {
                dataTransition1,
                dataTransition2,
                dataTransition3
        };
        EventConditionTransition eventTransition1 = new EventConditionTransition();
        eventTransition1.setName("event1");
        eventTransition1.setEventRef("eventRef1");
        EventConditionTransition eventTransition2 = new EventConditionTransition();
        eventTransition2.setEventRef("eventRef2");
        EventConditionTransition eventTransition3 = new EventConditionTransition();
        eventTransition3.setName("event3");
        eventTransition3.setEventRef("eventRef3");
        EventConditionTransition[] events = {
                eventTransition1,
                eventTransition2,
                eventTransition3
        };
        String expected = TEST_STRING + ":\r\n" +
                TEST_STRING + ": data1\r\n" +
                TEST_STRING + ": value2\r\n" +
                TEST_STRING + ": data3\r\n";

        assertTranslations(expected,
                           hasConditions.getConditionsString(data, events),
                           DATA_CONDITION_TRANSITION,
                           DATA_CONDITION_TRANSITION_NAME,
                           DATA_CONDITION_TRANSITION_VALUE,
                           DATA_CONDITION_TRANSITION_NAME);
    }
}
