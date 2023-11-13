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


package org.kie.workbench.common.stunner.bpmn.client.forms.fields.notificationsEditor.validation;

import java.util.ArrayList;
import java.util.List;

import javax.validation.ConstraintValidatorContext;

import com.google.gwtmockito.GwtMockitoTestRunner;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.kie.workbench.common.stunner.bpmn.client.forms.fields.model.NotificationRow;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

@RunWith(GwtMockitoTestRunner.class)
public class NotificationValueValidatorTest {

    private NotificationValueValidator validator = new NotificationValueValidator();

    private ConstraintValidatorContext context;

    private final List<String> errorMessages = new ArrayList<>();

    @Before
    public void setUp() throws Exception {
        validator = new NotificationValueValidator();
        context = new ConstraintValidatorContext() {
            @Override
            public void disableDefaultConstraintViolation() {
            }

            @Override
            public String getDefaultConstraintMessageTemplate() {
                return null;
            }

            @Override
            public ConstraintViolationBuilder buildConstraintViolationWithTemplate(String message) {
                errorMessages.add(message);
                return new ConstraintViolationBuilder() {
                    @Override
                    public NodeBuilderDefinedContext addNode(String name) {
                        return null;
                    }

                    @Override
                    public ConstraintValidatorContext addConstraintViolation() {
                        return context;
                    }
                };
            }
        };
    }

    @Test
    public void testEmptyNotificationRow() {
        boolean result = validator.isValid(new NotificationRow(), context);
        assertFalse(result);
        assertFalse(errorMessages.isEmpty());
        assertEquals(NotificationValueValidator.WRONG_EXPIRES_AT_EXPRESSION, errorMessages.get(0));
    }

    @Test
    public void testISO8601DataTimeRepeatableValue() {
        NotificationRow notification = new NotificationRow();
        notification.setExpiresAt("R/2019-07-14T13:34-02/PT33M");
        boolean result = validator.isValid(notification, context);
        assertTrue(result);
        assertTrue(errorMessages.isEmpty());
    }

    @Test
    public void testISO8601WithTZ02ZRepeatable1AndPeriodValue() {
        NotificationRow notification = new NotificationRow();
        notification.setExpiresAt("R1/2019-07-14T13:34-02/PT33M");
        boolean result = validator.isValid(notification, context);
        assertTrue(result);
        assertTrue(errorMessages.isEmpty());
    }

    @Test
    public void testISO8601WithTZ02ZRepeatable1AndPeriodTooBigValue() {
        NotificationRow notification = new NotificationRow();
        notification.setExpiresAt("R1/2019-07-14T13:34-02/PT3333333333333333333M");
        boolean result = new NotificationValueValidator().isValid(notification, context);
        assertFalse(result);
        assertEquals(NotificationValueValidator.WRONG_EXPIRES_AT_EXPRESSION, errorMessages.get(0));
    }

    @Test
    public void testISO8601WithTZ02ZRepeatableTooBigValue() {
        NotificationRow notification = new NotificationRow();
        notification.setExpiresAt("R13333333333333333/2019-07-14T13:34-02/PT33M");
        boolean result = validator.isValid(notification, context);
        assertFalse(result);
        assertEquals(NotificationValueValidator.WRONG_EXPIRES_AT_EXPRESSION, errorMessages.get(0));
    }

    @Test
    public void testISO8601WithTZ02Repeatable1AndPeriodAndWrongYearValue() {
        NotificationRow notification = new NotificationRow();
        notification.setExpiresAt("R1/1919-07-14T13:34-02/PT33M");
        boolean result = validator.isValid(notification, context);
        assertFalse(result);
        assertEquals(NotificationValueValidator.WRONG_EXPIRES_AT_EXPRESSION, errorMessages.get(0));
    }

    @Test
    public void testISO8601WithTZ00ZRepeatable1AndPeriodAndWrongTZ002Value() {
        NotificationRow notification = new NotificationRow();
        notification.setExpiresAt("R1/1919-07-14T13:34-002/P33Y");
        boolean result = validator.isValid(notification, context);
        assertFalse(result);
        assertEquals(NotificationValueValidator.WRONG_EXPIRES_AT_EXPRESSION, errorMessages.get(0));
    }

    @Test
    public void testISO8601WithTZ00ZRepeatable1AndPeriodAndTZ02Value() {
        NotificationRow notification = new NotificationRow();
        notification.setExpiresAt("R1/2019-07-14T13:34-02/P33D");
        boolean result = validator.isValid(notification, context);
        assertTrue(result);
        assertTrue(errorMessages.isEmpty());
    }

    @Test
    public void testISO8601WithTZ00ZRepeatable10AndPeriodAndTZ0230Value() {
        NotificationRow notification = new NotificationRow();
        notification.setExpiresAt("R10/2019-07-14T13:34+02:30/P33M");
        boolean result = validator.isValid(notification, context);
        assertTrue(result);
        assertTrue(errorMessages.isEmpty());
    }

    @Test
    public void testPasdfasdkValue() {
        NotificationRow notification = new NotificationRow();
        notification.setExpiresAt("Pasdfa;sdk");
        boolean result = validator.isValid(notification, context);
        assertFalse(result);
        assertEquals(NotificationValueValidator.WRONG_EXPIRES_AT_EXPRESSION, errorMessages.get(0));
    }

    @Test
    public void testISO8601WithTZ00ZRepeatable10AndPeriodValue() {
        NotificationRow notification = new NotificationRow();
        notification.setExpiresAt("R10/2019-07-14T13:34:00Z/P33Y");
        boolean result = validator.isValid(notification, context);
        assertTrue(result);
        assertTrue(errorMessages.isEmpty());
    }

    @Test
    public void testISO8601WithTZ00ZRepeatableUntilStateChangesAndPeriodValue() {
        NotificationRow notification = new NotificationRow();
        notification.setExpiresAt("R/2019-07-14T13:34:00Z/P33M");
        boolean result = validator.isValid(notification, context);
        assertTrue(result);
        assertTrue(errorMessages.isEmpty());
    }

    @Test
    public void testISO8601WithTZ00ZRepeatableUntilStateChangesValue() {
        NotificationRow notification = new NotificationRow();
        notification.setExpiresAt("R/2019-07-14T13:34:00Z/PT33M");
        boolean result = validator.isValid(notification, context);
        assertTrue(result);
        assertTrue(errorMessages.isEmpty());
    }

    @Test
    public void testISO8601WrongWithTZ00ZRepeatableUntilStateChangesValue() {
        NotificationRow notification = new NotificationRow();
        notification.setExpiresAt("R/1819-07-14T13:34:00Z/PT33M");
        boolean result = validator.isValid(notification, context);
        assertFalse(result);
        assertEquals(NotificationValueValidator.WRONG_EXPIRES_AT_EXPRESSION, errorMessages.get(0));
    }

    @Test
    public void testISO8601WrongWithTZ00ZRepeatableUntilStateChangesAndPeriodZeroValue() {
        NotificationRow notification = new NotificationRow();
        notification.setExpiresAt("R/2019-07-14T13:34:00Z/PT0Z");
        boolean result = validator.isValid(notification, context);
        assertFalse(result);
        assertEquals(NotificationValueValidator.WRONG_EXPIRES_AT_EXPRESSION, errorMessages.get(0));
    }

    @Test
    public void testISO8601WrongWithTZ00ZRepeatableZeroUntilStateChangesAndPeriodZeroValue() {
        NotificationRow notification = new NotificationRow();
        notification.setExpiresAt("R0/2019-07-14T13:34:00Z/PT1M");
        boolean result = validator.isValid(notification, context);
        assertFalse(result);
        assertEquals(NotificationValueValidator.WRONG_EXPIRES_AT_EXPRESSION, errorMessages.get(0));
    }

    @Test
    public void testISO8601WrongWithTZ00ZRepeatable10UntilStateChangesAndPeriodZeroValue() {
        NotificationRow notification = new NotificationRow();
        notification.setExpiresAt("R10/2019-07-14T13:34:00Z/PT0M");
        boolean result = validator.isValid(notification, context);
        assertFalse(result);
        assertEquals(NotificationValueValidator.WRONG_EXPIRES_AT_EXPRESSION, errorMessages.get(0));
    }

    @Test
    public void testISO8601WrongWithTZ00ZRepeatableZeroUntilStateChangesValue() {
        NotificationRow notification = new NotificationRow();
        notification.setExpiresAt("R0/2019-07-14T13:34:00Z/PT22M");
        boolean result = validator.isValid(notification, context);
        assertFalse(result);
        assertEquals(NotificationValueValidator.WRONG_EXPIRES_AT_EXPRESSION, errorMessages.get(0));
    }

    @Test
    public void testISO8601WithTZ02RepeatableValue() {
        NotificationRow notification = new NotificationRow();
        notification.setExpiresAt("2019-07-14T13:34-02");
        boolean result = validator.isValid(notification, context);
        assertTrue(result);
        assertTrue(errorMessages.isEmpty());
    }

    @Test
    public void testISO8601DataTimeValue() {
        NotificationRow notification = new NotificationRow();
        notification.setExpiresAt("2019-07-14T13:34-02");
        boolean result = validator.isValid(notification, context);
        assertTrue(result);
        assertTrue(errorMessages.isEmpty());
    }

    @Test
    public void testISO8601WithWrongDelimiterRepeatableValue() {
        NotificationRow notification = new NotificationRow();
        notification.setExpiresAt("2019-07-14W13:34-02");
        boolean result = validator.isValid(notification, context);
        assertFalse(result);
        assertEquals(NotificationValueValidator.WRONG_EXPIRES_AT_EXPRESSION, errorMessages.get(0));
    }

    @Test
    public void testISO8601WithWrongTZRepeatableValue() {
        NotificationRow notification = new NotificationRow();
        notification.setExpiresAt("2019-07-14T13:34-022");
        boolean result = validator.isValid(notification, context);
        assertFalse(result);
        assertEquals(NotificationValueValidator.WRONG_EXPIRES_AT_EXPRESSION, errorMessages.get(0));
    }

    @Test
    public void testISO8601WithTZ00ZRepeatableValue() {
        NotificationRow notification = new NotificationRow();
        notification.setExpiresAt("2019-07-14T13:34:00Z");
        boolean result = validator.isValid(notification, context);
        assertTrue(result);
        assertTrue(errorMessages.isEmpty());
    }

    @Test
    public void testISO8601WithTZ0245RepeatableValue() {
        NotificationRow notification = new NotificationRow();
        notification.setExpiresAt("2019-07-14T13:34-02:45");
        boolean result = validator.isValid(notification, context);
        assertTrue(result);
        assertTrue(errorMessages.isEmpty());
    }

    @Test
    public void testISO8601RepeatableValue() {
        NotificationRow notification = new NotificationRow();
        notification.setExpiresAt("R/PT33M");
        boolean result = validator.isValid(notification, context);
        assertTrue(result);
        assertTrue(errorMessages.isEmpty());
    }

    @Test
    public void testMonthRepeatableUntilStateChangesNotification() {
        NotificationRow notification = new NotificationRow();
        notification.setExpiresAt("R/P33M");
        boolean result = validator.isValid(notification, context);
        assertTrue(result);
        assertTrue(errorMessages.isEmpty());
    }

    @Test
    public void testDayRepeatableUntilStateChangesNotification() {
        NotificationRow notification = new NotificationRow();
        notification.setExpiresAt("R/P33D");
        boolean result = validator.isValid(notification, context);
        assertTrue(result);
        assertTrue(errorMessages.isEmpty());
    }

    @Test
    public void testYearRepeatableUntilStateChangesNotification() {
        NotificationRow notification = new NotificationRow();
        notification.setExpiresAt("R/P33Y");
        boolean result = validator.isValid(notification, context);
        assertTrue(result);
        assertTrue(errorMessages.isEmpty());
    }

    @Test
    public void testHourRepeatableUntilStateChangesNotification() {
        NotificationRow notification = new NotificationRow();
        notification.setExpiresAt("R/PT33H");
        boolean result = validator.isValid(notification, context);
        assertTrue(result);
        assertTrue(errorMessages.isEmpty());
    }

    @Test
    public void testWrongDayRepeatableUntilNotification() {
        NotificationRow notification = new NotificationRow();
        notification.setExpiresAt("R/P33D");
        boolean result = validator.isValid(notification, context);
        assertTrue(result);
        assertTrue(errorMessages.isEmpty());
    }

    @Test
    public void testWrongYearRepeatableUntilNotification() {
        NotificationRow notification = new NotificationRow();
        notification.setExpiresAt("R/P33Y");
        boolean result = validator.isValid(notification, context);
        assertTrue(result);
        assertTrue(errorMessages.isEmpty());
    }

    @Test
    public void testWrongHourRepeatableUntilNotification() {
        NotificationRow notification = new NotificationRow();
        notification.setExpiresAt("R/P33H");
        boolean result = validator.isValid(notification, context);
        assertFalse(result);
        assertEquals(NotificationValueValidator.WRONG_EXPIRES_AT_EXPRESSION, errorMessages.get(0));
    }

    @Test
    public void testWrongTimesZeroRepeatableUntilNotification() {
        NotificationRow notification = new NotificationRow();
        notification.setExpiresAt("R/PT0H");
        boolean result = validator.isValid(notification, context);
        assertFalse(result);
        assertEquals(NotificationValueValidator.WRONG_EXPIRES_AT_EXPRESSION, errorMessages.get(0));
    }

    @Test
    public void testWrongTimesZeroRepeatable1UntilNotification() {
        NotificationRow notification = new NotificationRow();
        notification.setExpiresAt("R1/PT0H");
        boolean result = validator.isValid(notification, context);
        assertFalse(result);
        assertEquals(NotificationValueValidator.WRONG_EXPIRES_AT_EXPRESSION, errorMessages.get(0));
    }

    @Test
    public void testWrongPeriodNotification() {
        NotificationRow notification = new NotificationRow();
        notification.setExpiresAt("PT0H");
        boolean result = validator.isValid(notification, context);
        assertFalse(result);
        assertEquals(NotificationValueValidator.WRONG_EXPIRES_AT_EXPRESSION, errorMessages.get(0));
    }

    @Test
    public void testWrongPeriod100HNotification() {
        NotificationRow notification = new NotificationRow();
        notification.setExpiresAt("PT100H");
        boolean result = validator.isValid(notification, context);
        assertTrue(result);
        assertTrue(errorMessages.isEmpty());
    }

    @Test
    public void testWrongTimesZeroRepeatableZeroNotification() {
        NotificationRow notification = new NotificationRow();
        notification.setExpiresAt("R0/PT0H");
        boolean result = validator.isValid(notification, context);
        assertFalse(result);
        assertEquals(NotificationValueValidator.WRONG_EXPIRES_AT_EXPRESSION, errorMessages.get(0));
    }

    @Test
    public void testR0PT1H() {
        NotificationRow notification = new NotificationRow();
        notification.setExpiresAt("R0/PT1H");
        boolean result = validator.isValid(notification, context);
        assertFalse(result);
        assertEquals(NotificationValueValidator.WRONG_EXPIRES_AT_EXPRESSION, errorMessages.get(0));
    }

    @Test
    public void testR1PT0H() {
        NotificationRow notification = new NotificationRow();
        notification.setExpiresAt("R1/PT0H");
        boolean result = validator.isValid(notification, context);
        assertFalse(result);
        assertEquals(NotificationValueValidator.WRONG_EXPIRES_AT_EXPRESSION, errorMessages.get(0));
    }

    @Test
    public void testR1PT1H0S() {
        NotificationRow notification = new NotificationRow();
        notification.setExpiresAt("R1/PT1H0S");
        boolean result = validator.isValid(notification, context);
        assertFalse(result);
        assertEquals(NotificationValueValidator.WRONG_EXPIRES_AT_EXPRESSION, errorMessages.get(0));
    }

    @Test
    public void testR2PT4H20190527T130000Z() {
        NotificationRow notification = new NotificationRow();
        notification.setExpiresAt("R2/PT4H/2019-05-27T13:00:00Z");
        boolean result = validator.isValid(notification, context);
        assertTrue(result);
        assertTrue(errorMessages.isEmpty());
    }

    @Test
    public void testR2PT4H20190527T133300Z() {
        NotificationRow notification = new NotificationRow();
        notification.setExpiresAt("R2/PT4H/2019-05-27T13:33:00Z");
        boolean result = validator.isValid(notification, context);
        assertTrue(result);
        assertTrue(errorMessages.isEmpty());
    }

    @Test
    public void testPT4HOrPT6H() {
        NotificationRow notification = new NotificationRow();
        notification.setExpiresAt("PT4H,PT6H");
        boolean result = validator.isValid(notification, context);
        assertTrue(result);
        assertTrue(errorMessages.isEmpty());
    }

    @Test
    public void testPT4HOrPT6HWrong() {
        NotificationRow notification = new NotificationRow();
        notification.setExpiresAt("PT4H,ZPT6H");
        boolean result = validator.isValid(notification, context);
        assertFalse(result);
        assertEquals(NotificationValueValidator.WRONG_EXPIRES_AT_EXPRESSION, errorMessages.get(0));
    }

    @Test
    public void testWrongDayRepeatableNotification() {
        NotificationRow notification = new NotificationRow();
        notification.setExpiresAt("  R44/PT33D");
        boolean result = validator.isValid(notification, context);
        assertFalse(result);
        assertEquals(NotificationValueValidator.WRONG_EXPIRES_AT_EXPRESSION, errorMessages.get(0));
    }

    @Test
    public void testWrongYearRepeatableNotification() {
        NotificationRow notification = new NotificationRow();
        notification.setExpiresAt("   R44/PT33Y   ");
        boolean result = validator.isValid(notification, context);
        assertFalse(result);
        assertEquals(NotificationValueValidator.WRONG_EXPIRES_AT_EXPRESSION, errorMessages.get(0));
    }

    @Test
    public void testWrongHourRepeatableNotification() {
        NotificationRow notification = new NotificationRow();
        notification.setExpiresAt("R44/P33H");
        boolean result = validator.isValid(notification, context);
        assertFalse(result);
        assertEquals(NotificationValueValidator.WRONG_EXPIRES_AT_EXPRESSION, errorMessages.get(0));
    }

    @Test
    public void testWrongHourRepeatableZeroNotification() {
        NotificationRow notification = new NotificationRow();
        notification.setExpiresAt("R0/P33H");
        boolean result = validator.isValid(notification, context);
        assertFalse(result);
        assertEquals(NotificationValueValidator.WRONG_EXPIRES_AT_EXPRESSION, errorMessages.get(0));
    }

    @Test
    public void testWrongYearNotificationAndTZ02() {
        NotificationRow notification = new NotificationRow();
        notification.setExpiresAt("2019-07-14T13:34-02");
        boolean result = validator.isValid(notification, context);
        assertTrue(result);
        assertTrue(errorMessages.isEmpty());
    }

    @Test
    public void testWrongNotificationAndTZ022() {
        NotificationRow notification = new NotificationRow();
        notification.setExpiresAt("2019-07-14T13:34-022");
        boolean result = validator.isValid(notification, context);
        assertFalse(result);
        assertEquals(NotificationValueValidator.WRONG_EXPIRES_AT_EXPRESSION, errorMessages.get(0));
    }

    @Test
    public void testNotificationAndTZ0245() {
        NotificationRow notification = new NotificationRow();
        notification.setExpiresAt("2019-07-14T13:34-02:45");
        boolean result = validator.isValid(notification, context);
        assertTrue(result);
        assertTrue(errorMessages.isEmpty());
    }

    @Test
    public void testNotificationAndTZ002() {
        NotificationRow notification = new NotificationRow();
        notification.setExpiresAt("2019-07-14T13:34:00Z");
        boolean result = validator.isValid(notification, context);
        assertTrue(result);
        assertTrue(errorMessages.isEmpty());
    }

    @Test
    public void testNegativeExpiresAtNotificationRow() {
        NotificationRow value = new NotificationRow();
        value.setExpiresAt("-1d");
        boolean result = validator.isValid(value, context);
        assertFalse(result);
        assertEquals(NotificationValueValidator.WRONG_EXPIRES_AT_EXPRESSION, errorMessages.get(0));
    }

    @Test
    public void test1DigExpiresAtNotificationRow() {
        NotificationRow value = new NotificationRow();
        value.setExpiresAt("1d");
        boolean result = validator.isValid(value, context);
        assertTrue(result);
        assertTrue(errorMessages.isEmpty());
    }

    @Test
    public void test2DigExpiresAtNotificationRow() {
        NotificationRow value = new NotificationRow();
        value.setExpiresAt("11d");
        boolean result = validator.isValid(value, context);
        assertTrue(result);
        assertTrue(errorMessages.isEmpty());
    }

    @Test
    public void test3DigExpiresAtNotificationRow() {
        NotificationRow value = new NotificationRow();
        value.setExpiresAt("111d");
        boolean result = validator.isValid(value, context);
        assertTrue(result);
        assertTrue(errorMessages.isEmpty());
    }

    @Test
    public void test4DigExpiresAtNotificationRow() {
        NotificationRow value = new NotificationRow();
        value.setExpiresAt("1111d");
        boolean result = validator.isValid(value, context);
        assertTrue(result);
        assertTrue(errorMessages.isEmpty());
    }

    @Test
    public void test5DigExpiresAtNotificationRow() {
        NotificationRow value = new NotificationRow();
        value.setExpiresAt("11111d");
        boolean result = validator.isValid(value, context);
        assertTrue(result);
        assertTrue(errorMessages.isEmpty());
    }

    @Test
    public void test10DigExpiresAtNotificationRow() {
        NotificationRow value = new NotificationRow();
        value.setExpiresAt("1111111111d");
        boolean result = validator.isValid(value, context);
        assertTrue(result);
        assertTrue(errorMessages.isEmpty());
    }

    @Test
    public void testZAnd10DigExpiresAtNotificationRow() {
        NotificationRow value = new NotificationRow();
        value.setExpiresAt("Z1111111111d");
        boolean result = validator.isValid(value, context);
        assertFalse(result);
        assertEquals(NotificationValueValidator.WRONG_EXPIRES_AT_EXPRESSION, errorMessages.get(0));
    }

    @Test
    public void testIntMaxExpiresAtNotificationRow() {
        NotificationRow value = new NotificationRow();
        value.setExpiresAt("2147483647d");
        boolean result = validator.isValid(value, context);
        assertTrue(result);
        assertTrue(errorMessages.isEmpty());
    }

    @Test
    public void testZeroExpiresAtNotificationRow() {
        NotificationRow value = new NotificationRow();
        value.setExpiresAt("0d");
        boolean result = validator.isValid(value, context);
        assertFalse(result);
        assertEquals(NotificationValueValidator.WRONG_EXPIRES_AT_EXPRESSION, errorMessages.get(0));
    }

    @Test
    public void testOldTimeFormat() {
        NotificationRow value = new NotificationRow();
        value.setExpiresAt("2d6H48m32s12mS");
        boolean result = validator.isValid(value, context);
        assertTrue(result);
        assertTrue(errorMessages.isEmpty());
    }

    @Test
    public void testP2M2D() {
        NotificationRow value = new NotificationRow();
        value.setExpiresAt("P2M2D");
        boolean result = validator.isValid(value, context);
        assertTrue(result);
        assertTrue(errorMessages.isEmpty());
    }

    @Test
    public void testP2M2DT2S() {
        NotificationRow value = new NotificationRow();
        value.setExpiresAt("P2M2DT2S");
        boolean result = validator.isValid(value, context);
        assertTrue(result);
        assertTrue(errorMessages.isEmpty());
    }

    @Test
    public void testExampleFromTheTip() {
        NotificationRow value = new NotificationRow();
        value.setExpiresAt("2d6H48m32s12mS");
        boolean result = validator.isValid(value, context);
        assertTrue(result);
        assertTrue(errorMessages.isEmpty());
    }

    @Test
    public void testDurations() {
        NotificationRow value = new NotificationRow();
        value.setExpiresAt("P1Y2M5DT4H5M8S6MS");
        boolean result = validator.isValid(value, context);
        assertTrue(result);
        assertTrue(errorMessages.isEmpty());
    }

    @Test
    public void testDurationsY() {
        NotificationRow value = new NotificationRow();
        value.setExpiresAt("P1Y445DT20H13M");
        boolean result = validator.isValid(value, context);
        assertTrue(result);
        assertTrue(errorMessages.isEmpty());
    }

    @Test
    public void testBounded5S() {
        NotificationRow value = new NotificationRow();
        value.setExpiresAt("R5/PT2S");
        boolean result = validator.isValid(value, context);
        assertTrue(result);
        assertTrue(errorMessages.isEmpty());
    }

    @Test
    public void testBounded5SWrong() {
        NotificationRow value = new NotificationRow();
        value.setExpiresAt("R5/P2S");
        boolean result = validator.isValid(value, context);
        assertFalse(result);
        assertEquals(NotificationValueValidator.WRONG_EXPIRES_AT_EXPRESSION, errorMessages.get(0));
    }

    @Test
    public void testUnBounded2D() {
        NotificationRow value = new NotificationRow();
        value.setExpiresAt("R/P2D");
        boolean result = validator.isValid(value, context);
        assertTrue(result);
        assertTrue(errorMessages.isEmpty());
    }

    @Test
    public void testUnBounded2DWrong() {
        NotificationRow value = new NotificationRow();
        value.setExpiresAt("R/PT2D");
        boolean result = validator.isValid(value, context);
        assertFalse(result);
        assertEquals(NotificationValueValidator.WRONG_EXPIRES_AT_EXPRESSION, errorMessages.get(0));
    }

    @Test
    public void testDurationsD() {
        NotificationRow value = new NotificationRow();
        value.setExpiresAt("P1DT20H13M7MS");
        boolean result = validator.isValid(value, context);
        assertTrue(result);
        assertTrue(errorMessages.isEmpty());
    }

    @Test
    public void testDurationsW() {
        NotificationRow value = new NotificationRow();
        value.setExpiresAt("P1WT20H13M");
        boolean result = validator.isValid(value, context);
        assertTrue(result);
        assertTrue(errorMessages.isEmpty());
    }

    @Test
    public void testRepeatingIntervals() {
        NotificationRow value = new NotificationRow();
        value.setExpiresAt("R5/P1Y2M5DT4H5M8S6MS");
        boolean result = validator.isValid(value, context);
        assertTrue(result);
        assertTrue(errorMessages.isEmpty());
    }

    @Test
    public void testRepeatingIntervalsY() {
        NotificationRow value = new NotificationRow();
        value.setExpiresAt("R2/P1YT20H13M");
        boolean result = validator.isValid(value, context);
        assertTrue(result);
        assertTrue(errorMessages.isEmpty());
    }

    @Test
    public void testRepeatingIntervalsD() {
        NotificationRow value = new NotificationRow();
        value.setExpiresAt("R2/P1DT20H13M");
        boolean result = validator.isValid(value, context);
        assertTrue(result);
        assertTrue(errorMessages.isEmpty());
    }

    @Test
    public void testRepeatingIntervalsW() {
        NotificationRow value = new NotificationRow();
        value.setExpiresAt("R2/P1WT20H13M");
        boolean result = validator.isValid(value, context);
        assertTrue(result);
        assertTrue(errorMessages.isEmpty());
    }

    @Test
    public void testR2StartEnd() {
        NotificationRow value = new NotificationRow();
        value.setExpiresAt("R2/2019-05-27T13:00:00Z/2019-05-27T17:00:00Z");
        boolean result = validator.isValid(value, context);
        assertTrue(result);
        assertTrue(errorMessages.isEmpty());
    }

    @Test
    public void test6d() {
        NotificationRow value = new NotificationRow();
        value.setExpiresAt("6d");
        boolean result = validator.isValid(value, context);
        assertTrue(result);
        assertTrue(errorMessages.isEmpty());
    }

    @Test
    public void test7h() {
        NotificationRow value = new NotificationRow();
        value.setExpiresAt("7h");
        boolean result = validator.isValid(value, context);
        assertTrue(result);
        assertTrue(errorMessages.isEmpty());
    }
    @Test
    public void test9m() {
        NotificationRow value = new NotificationRow();
        value.setExpiresAt("9m");
        boolean result = validator.isValid(value, context);
        assertTrue(result);
        assertTrue(errorMessages.isEmpty());
    }
    @Test
    public void test2s() {
        NotificationRow value = new NotificationRow();
        value.setExpiresAt("2s");
        boolean result = validator.isValid(value, context);
        assertTrue(result);
        assertTrue(errorMessages.isEmpty());
    }

    @Test
    public void test15ms() {
        NotificationRow value = new NotificationRow();
        value.setExpiresAt("15ms");
        boolean result = validator.isValid(value, context);
        assertTrue(result);
        assertTrue(errorMessages.isEmpty());
    }

    @Test
    public void testR3PT4H() {
        NotificationRow value = new NotificationRow();
        value.setExpiresAt("R3/PT4H");
        boolean result = validator.isValid(value, context);
        assertTrue(result);
        assertTrue(errorMessages.isEmpty());
    }

    @Test
    public void testPT4HPT6H() {
        NotificationRow value = new NotificationRow();
        value.setExpiresAt("PT4H,PT6H");
        boolean result = validator.isValid(value, context);
        assertTrue(result);
        assertTrue(errorMessages.isEmpty());
    }

    @Test
    public void testPT4H() {
        NotificationRow value = new NotificationRow();
        value.setExpiresAt("PT4H");
        boolean result = validator.isValid(value, context);
        assertTrue(result);
        assertTrue(errorMessages.isEmpty());
    }

    @Test
    public void testR4T4HR4T6H() {
        NotificationRow value = new NotificationRow();
        value.setExpiresAt("R4/PT4H,R4/PT6H");
        boolean result = validator.isValid(value, context);
        assertTrue(result);
        assertTrue(errorMessages.isEmpty());
    }

    @Test
    public void testR3PT4HWrong() {
        NotificationRow value = new NotificationRow();
        value.setExpiresAt("R3/PT4HZ");
        boolean result = validator.isValid(value, context);
        assertFalse(result);
        assertEquals(NotificationValueValidator.WRONG_EXPIRES_AT_EXPRESSION, errorMessages.get(0));
    }

    @Test
    public void testR2PT4H() {
        NotificationRow value = new NotificationRow();
        value.setExpiresAt("R2/PT4H");
        boolean result = validator.isValid(value, context);
        assertTrue(result);
        assertTrue(errorMessages.isEmpty());
    }

    @Test
    public void testRPT4H() {
        NotificationRow value = new NotificationRow();
        value.setExpiresAt("R/PT4H");
        boolean result = validator.isValid(value, context);
        assertTrue(result);
        assertTrue(errorMessages.isEmpty());
    }

    @Test
    public void testR220190527T130000ZPT4H() {
        NotificationRow value = new NotificationRow();
        value.setExpiresAt("R2/2019-05-27T13:00:00Z/PT4H");
        boolean result = validator.isValid(value, context);
        assertTrue(result);
        assertTrue(errorMessages.isEmpty());
    }

    @Test
    public void testR2PT4H20190527T130000ZWrong() {
        NotificationRow value = new NotificationRow();
        value.setExpiresAt("R2/PT4H/2019-05-27T13:00:00ZZ");
        boolean result = validator.isValid(value, context);
        assertFalse(result);
        assertEquals(NotificationValueValidator.WRONG_EXPIRES_AT_EXPRESSION, errorMessages.get(0));
    }

    @Test
    public void testVariableName() {
        NotificationRow value = new NotificationRow();
        value.setExpiresAt("#{variable_name}");
        boolean result = validator.isValid(value, context);
        assertTrue(result);
        assertTrue(errorMessages.isEmpty());
    }

    @Test
    public void test50s500ms() {
        NotificationRow value = new NotificationRow();
        value.setExpiresAt("50s500ms");
        boolean result = validator.isValid(value, context);
        assertTrue(result);
        assertTrue(errorMessages.isEmpty());
    }

    @Test
    public void test2d50s500ms() {
        NotificationRow value = new NotificationRow();
        value.setExpiresAt("2d50s500ms");
        boolean result = validator.isValid(value, context);
        assertTrue(result);
        assertTrue(errorMessages.isEmpty());
    }

    @Test
    public void testVariableNameWrong() {
        NotificationRow value = new NotificationRow();
        value.setExpiresAt("#{final}");
        boolean result = validator.isValid(value, context);
        assertFalse(result);
        assertEquals(NotificationValueValidator.WRONG_EXPIRES_AT_EXPRESSION, errorMessages.get(0));
    }

    @Test
    public void testVariableNameWrongClass() {
        NotificationRow value = new NotificationRow();
        value.setExpiresAt("#{class}");
        boolean result = validator.isValid(value, context);
        assertFalse(result);
        assertEquals(NotificationValueValidator.WRONG_EXPIRES_AT_EXPRESSION, errorMessages.get(0));
    }

    @Test
    public void testRandomChars() {
        NotificationRow value = new NotificationRow();
        value.setExpiresAt("P~!@#$%^&*()_+=-{}][:\"';<>?/.,");
        boolean result = validator.isValid(value, context);
        assertFalse(result);
        assertEquals(NotificationValueValidator.WRONG_EXPIRES_AT_EXPRESSION, errorMessages.get(0));
    }

    @Test
    public void testRandomChars2() {
        NotificationRow value = new NotificationRow();
        value.setExpiresAt("Pтыфц");
        boolean result = validator.isValid(value, context);
        assertFalse(result);
        assertEquals(NotificationValueValidator.WRONG_EXPIRES_AT_EXPRESSION, errorMessages.get(0));
    }

    @Test
    public void testRandomChars3() {
        NotificationRow value = new NotificationRow();
        value.setExpiresAt("P12345");
        boolean result = validator.isValid(value, context);
        assertFalse(result);
        assertEquals(NotificationValueValidator.WRONG_EXPIRES_AT_EXPRESSION, errorMessages.get(0));
    }

    @Test
    public void testP2y10M14dT20h13m() {
        NotificationRow value = new NotificationRow();
        value.setExpiresAt("P2y10M14dT20h13m");
        boolean result = validator.isValid(value, context);
        assertTrue(result);
        assertTrue(errorMessages.isEmpty());
    }

    @Test
    public void testR120011201T133402PT3S() {
        NotificationRow value = new NotificationRow();

        value.setExpiresAt("R1/2001-12-01T13:34-02/PT3S");
        boolean result = validator.isValid(value, context);
        assertTrue(result);
        assertTrue(errorMessages.isEmpty());
    }

    @Test
    public void testR120011201T133402P1M1D() {
        NotificationRow value = new NotificationRow();
        value.setExpiresAt("R1/2001-12-01T13:34+02/P1M1D");
        boolean result = validator.isValid(value, context);
        assertTrue(result);
        assertTrue(errorMessages.isEmpty());
    }

    @Test
    public void testR0120011201T133402P1M1D() {
        NotificationRow value = new NotificationRow();
        value.setExpiresAt("R0/2001-12-01T13:34+02/P1M1D");
        boolean result = validator.isValid(value, context);
        assertFalse(result);
        assertEquals(NotificationValueValidator.WRONG_EXPIRES_AT_EXPRESSION, errorMessages.get(0));
    }

    @Test
    public void testR20011201T133402P1DT1H() {
        NotificationRow value = new NotificationRow();
        value.setExpiresAt("R1/2001-12-01T13:34-02/P1DT1H");
        boolean result = validator.isValid(value, context);
        assertTrue(result);
        assertTrue(errorMessages.isEmpty());
    }

    @Test
    public void testR20011201T133402RP1DT3H() {
        NotificationRow value = new NotificationRow();
        value.setExpiresAt("R/2001-12-01T13:34-02/P1DT1H");
        boolean result = validator.isValid(value, context);
        assertTrue(result);
        assertTrue(errorMessages.isEmpty());
    }

    @Test
    public void testR120011201T133402RP1DT3H() {
        NotificationRow value = new NotificationRow();
        value.setExpiresAt("R1/2001-12-01T13:34-02/P1DT1H");
        boolean result = validator.isValid(value, context);
        assertTrue(result);
        assertTrue(errorMessages.isEmpty());
    }

    @Test
    public void testR1P1M14d() {
        NotificationRow value = new NotificationRow();
        value.setExpiresAt("R1/P1M14D");
        boolean result = validator.isValid(value, context);
        assertTrue(result);
        assertTrue(errorMessages.isEmpty());
    }

    @Test
    public void testRP1M14d() {
        NotificationRow value = new NotificationRow();
        value.setExpiresAt("R/P1M14D");
        boolean result = validator.isValid(value, context);
        assertTrue(result);
        assertTrue(errorMessages.isEmpty());
    }

    @Test
    public void testR1P1m14d() {
        NotificationRow value = new NotificationRow();
        value.setExpiresAt("R1/P1m14D");
        boolean result = validator.isValid(value, context);
        assertTrue(result);
        assertTrue(errorMessages.isEmpty());
    }

    @Test
    public void testRP1m14d() {
        NotificationRow value = new NotificationRow();
        value.setExpiresAt("R/P1m14D");
        boolean result = validator.isValid(value, context);
        assertTrue(result);
        assertTrue(errorMessages.isEmpty());
    }

    @Test
    public void testRP1DT3H() {
        NotificationRow value = new NotificationRow();
        value.setExpiresAt("R/P1DT3H");
        boolean result = validator.isValid(value, context);
        assertTrue(result);
        assertTrue(errorMessages.isEmpty());
    }

    @Test
    public void testRP1M14D() {
        NotificationRow value = new NotificationRow();
        value.setExpiresAt("R/P1M14D");
        boolean result = validator.isValid(value, context);
        assertTrue(result);
        assertTrue(errorMessages.isEmpty());
    }

    @Test
    public void testR1P1M14D() {
        NotificationRow value = new NotificationRow();
        value.setExpiresAt("R1/P1M14D");
        boolean result = validator.isValid(value, context);
        assertTrue(result);
        assertTrue(errorMessages.isEmpty());
    }

    @Test
    public void testR1P1M14DT2H1S() {
        NotificationRow value = new NotificationRow();
        value.setExpiresAt("R1/P1M14DT2H1S");
        boolean result = validator.isValid(value, context);
        assertTrue(result);
        assertTrue(errorMessages.isEmpty());
    }

    @Test
    public void testRPT5m30s() {
        NotificationRow value = new NotificationRow();
        value.setExpiresAt("R/PT5m30s");
        boolean result = validator.isValid(value, context);
        assertTrue(result);
        assertTrue(errorMessages.isEmpty());
    }

    @Test
    public void testR10PT5m30s() {
        NotificationRow value = new NotificationRow();
        value.setExpiresAt("R10/PT5m30s");
        boolean result = validator.isValid(value, context);
        assertTrue(result);
        assertTrue(errorMessages.isEmpty());
    }
}
