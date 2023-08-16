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

import com.google.gwtmockito.GwtMockitoTestRunner;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.kie.workbench.common.stunner.bpmn.client.forms.fields.model.Expiration;

import static org.junit.Assert.assertEquals;

@RunWith(GwtMockitoTestRunner.class)
public class ExpirationTypeOracleTest {

    private ExpirationTypeOracle oracle;

    @Before
    public void setUp() throws Exception {
        oracle = new ExpirationTypeOracle();
    }

    @Test
    public void testEmptyNotificationRow() {
        Expiration result = oracle.guess("1d");
        assertEquals(Expiration.EXPRESSION, result);
    }

    @Test
    public void testISO8601DATETIMERepeatableValue() {
        Expiration result = oracle.guess("R/2019-07-14T13:34-02/PT33M");
        assertEquals(Expiration.DATETIME, result);
    }

    @Test
    public void testISO8601WithTZ02ZRepeatable1AndPeriodValue() {
        Expiration result = oracle.guess("R1/2019-07-14T13:34-02/PT33M");
        assertEquals(Expiration.DATETIME, result);
    }

    @Test
    public void testISO8601WithTZ00ZRepeatable1AndPeriodAndTZ02Value() {
        Expiration result = oracle.guess("R1/2019-07-14T13:34-02/PT33D");
        assertEquals(Expiration.DATETIME, result);
    }

    @Test
    public void testISO8601WithTZ00ZRepeatable10AndPeriodAndTZ0230Value() {
        Expiration result = oracle.guess("R10/2019-07-14T13:34+02:30/P33M");
        assertEquals(Expiration.DATETIME, result);
    }

    @Test
    public void testISO8601WithTZ02RepeatableValue() {
        Expiration result = oracle.guess("2019-07-14T13:34-02");
        assertEquals(Expiration.DATETIME, result);
    }

    @Test
    public void testISO8601DATETIMEValue() {
        Expiration result = oracle.guess("2019-07-14T13:34-02");
        assertEquals(Expiration.DATETIME, result);
    }

    @Test
    public void testISO8601RepeatableValue() {
        Expiration result = oracle.guess("R/PT33M");
        assertEquals(Expiration.TIME_PERIOD, result);
    }

    @Test
    public void testMonthRepeatableUntilStateChangesNotification() {
        Expiration result = oracle.guess("R/P33M");
        assertEquals(Expiration.TIME_PERIOD, result);
    }

    @Test
    public void testNotificationAndTZ0245() {
        Expiration result = oracle.guess("2019-07-14T13:34-02:45");
        assertEquals(Expiration.DATETIME, result);
    }

    @Test
    public void testNotificationAndTZ002() {
        Expiration result = oracle.guess("2019-07-14T13:34:00Z");
        assertEquals(Expiration.DATETIME, result);
    }

    @Test
    public void test1DigExpiresAtNotificationRow() {
        Expiration result = oracle.guess("1d");
        assertEquals(Expiration.EXPRESSION, result);
    }

    @Test
    public void testOldTimeFormat() {
        Expiration result = oracle.guess("2d6H48m32s12mS");
        assertEquals(Expiration.EXPRESSION, result);
    }

    @Test
    public void testExampleFromTheTip() {
        Expiration result = oracle.guess("P2y10M14dT20h13m");
        assertEquals(Expiration.EXPRESSION, result);
    }

    @Test
    public void testDurations() {
        Expiration result = oracle.guess("P1Y2M5DT4H5M8S6MS");
        assertEquals(Expiration.EXPRESSION, result);
    }

    @Test
    public void testDurationsY() {
        Expiration result = oracle.guess("P1YT20H13M");
        assertEquals(Expiration.EXPRESSION, result);
    }

    @Test
    public void testDurationsD() {
        Expiration result = oracle.guess("P1DT20H13M");
        assertEquals(Expiration.EXPRESSION, result);
    }

    @Test
    public void testDurationsW() {
        Expiration result = oracle.guess("P1WT20H13M");
        assertEquals(Expiration.EXPRESSION, result);
    }

    @Test
    public void testRepeatingIntervals() {
        Expiration result = oracle.guess("R5/P1Y2M5DT4H5M8S6MS");
        assertEquals(Expiration.EXPRESSION, result);
    }

    @Test
    public void testRepeatingIntervalsY() {
        Expiration result = oracle.guess("R2/P1YT20H13M");
        assertEquals(Expiration.EXPRESSION, result);
    }

    @Test
    public void testRepeatingIntervalsD() {
        Expiration result = oracle.guess("R2/P1DT20H13M");
        assertEquals(Expiration.EXPRESSION, result);
    }

    @Test
    public void testRepeatingIntervalsW() {
        Expiration result = oracle.guess("R2/P1WT20H13M");
        assertEquals(Expiration.EXPRESSION, result);
    }

    @Test
    public void testR2StartEnd() {
        Expiration result = oracle.guess("R2/2019-05-27T13:00:00Z/2019-05-27T17:00:00Z");
        assertEquals(Expiration.EXPRESSION, result);
    }

    @Test
    public void testRStartEnd() {
        Expiration result = oracle.guess("R/2019-05-27T13:00:00Z/2019-05-27T17:00:00Z");
        assertEquals(Expiration.EXPRESSION, result);
    }

    @Test
    public void testPT0H() {
        Expiration result = oracle.guess("PT0H");
        assertEquals(Expiration.EXPRESSION, result);
    }

    @Test
    public void testR2PT4H20190527T130000Z() {
        Expiration result = oracle.guess("R2/PT4H/2019-05-27T13:00:00Z");
        assertEquals(Expiration.EXPRESSION, result);
    }

    @Test
    public void testR2P1WT20H13M20190527T130000Z() {
        Expiration result = oracle.guess("R2/P1WT20H13M/2019-05-27T13:00:00Z");
        assertEquals(Expiration.EXPRESSION, result);
    }

    @Test
    public void testR120190714T133402P33S() {
        Expiration result = oracle.guess("R1/2019-07-14T13:34-02/P33S");
        assertEquals(Expiration.EXPRESSION, result);
    }

    @Test
    public void testR120190714T133402P33MS() {
        Expiration result = oracle.guess("R1/2019-07-14T13:34-02/P33MS");
        assertEquals(Expiration.EXPRESSION, result);
    }
}
