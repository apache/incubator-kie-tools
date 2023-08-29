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


package org.kie.workbench.common.stunner.bpmn.forms.validation.timerEditor;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import javax.validation.ConstraintValidatorContext;

import com.google.gwt.junit.client.GWTTestCase;
import org.junit.Test;
import org.kie.workbench.common.stunner.bpmn.definition.property.event.timer.TimerSettingsValue;

public class TimerSettingsValueValidatorTest
        extends GWTTestCase {

    private TimerSettingsValueValidator validator;

    private ConstraintValidatorContext context;

    private TimerSettingsValue value;

    private List<String> errorMessages = new ArrayList<>();

    private List<TestElement> testElements = new ArrayList<>();

    private static final String[] VALID_TIME_DURATIONS = {
            "P6D",
            "P6DT1H",
            "P6DT1H8M",
            "P6DT1H8M15S",
            "PT1H",
            "PT1H8M",
            "PT1H8M5S",
            "PT8M",
            "PT3S",
            "P1Y2M3DT2H30M2S",
            "P4Y",
            "P4Y2M",
            "P4Y2M6D",
            "P4Y2M6DT1H",
            "P4Y2M6DT1H8M",
            "P4Y2M6DT1H8M15S",
            "P5Y1M4DT13H30M0,4S",
            "P5Y1M4DT13H30M0.4S"
    };

    private static final String[] INVALID_TIME_DURATIONS = {
            "P4G",
            "P4M2Y",
            "P4Y2W6M",
            "P4Y2M6D6W",
            "P4Y2M6DT1Y",
            "P4Y2M6DT1M8H",
            "P4Y2M6DT1H8S15M",
            "P5Y1M4DT1,5H30,2M0,4S",
            "",
            "PPP",
            "23EE",
            "etc",
    };

    private static final String[] VALID_ISO_TIME_CYCLE_DURATIONS = {
            "R/P6D",
            "R/P6DT1H",
            "R/P6DT1H8M",
            "R/P6DT1H8M15S",
            "R/PT1H",
            "R/PT1H8M",
            "R/PT1H8M5S",
            "R/PT8M",
            "R/PT3S",
            "R2/P6D",
            "R2/P6DT1H",
            "R2/P6DT1H8M",
            "R2/P6DT1H8M15S",
            "R2/PT1H",
            "R2/PT1H8M",
            "R2/PT1H8M5S",
            "R2/PT8M",
            "R2/PT3S",
            "R5/2020-04-30T17:55-04:00/PT1S",
            "R5/2020-04-30T17:55-04:00/PT1M",
            "R5/2020-04-30T17:55-04:00/PT1H",
            "R5/2020-04-30T17:55-04:00/P1D",
            "R5/2020-04-30T17:55-04:00/P1Y",
            "R5/2020-02-07T15:36:00Z/PT5S",
            "R5/2020-02-07T15:36:00Z/PT5M",
            "R5/2020-02-07T15:36:00Z/PT5H",
            "R5/2020-02-07T15:36:00Z/P5D",
            "R5/2020-02-07T15:36:00Z/P5Y",
            "R1/2020-05-12T18:25:00+02:00/PT5S",
            "R5/2021-04-14T08:15:00Z/P1Y2M3DT2H30M2S",
            "R5/2030-08-20T22:14-00:30/PT5S",
            "R5/2020-12-01T23:50:01Z/PT5S",
            "R5/2021-04-14T08:15:00Z/2021-04-15T08:15:00Z",
            "R5/2030-08-20T17:59Z/PT5S",
            "R2/PT3S/2021-04-15T08:15:00Z",
            "R5/2020-04-30T17:55-04:00/P5Y1M4DT13H30M0,4S",
            "R5/2020-04-30T17:55-04:00/P5Y1M4DT13H30M0.4S",
            "R5/2020-02-29T08:15:00Z/P1Y2M3DT2H30M2S",
            "R5/2024-02-29T08:15:00Z/P1Y2M3DT2H30M2S",
            "R5/2020-02-29T08:15:00Z/2024-02-29T08:15:00Z"
    };

    private static final String[] INVALID_ISO_TIME_CYCLE_DURATIONS = {
            "R/P4S",
            "R/P4Y2H",
            "R/P4M2M",
            "R/P4Y2M6DT1W",
            "R/P4Y2M6DT1H8K",
            "R/P4Y2M6D1H8M15S",
            "R2/P4S",
            "R2/P4Y2H",
            "R2/P4M2M",
            "R2/P4Y2M6DT1W",
            "R2/P4Y2M6DT1H8K",
            "R2/P4Y2M6D1H8M15S",
            "",
            "R/",
            "R/4Y2M",
            "A",
            "/P4Y2M6D",
            "R2PT1H",
            "R5/2020-04-30T17:55-4:00/PT1S",
            "R5/2020-04-30T17:55-04/PT1M",
            "R5/2020-04-30T17:55-4/PT1H",
            "R5/2020-04-30T17:55-04:00",
            "R5/2020-04-30T17:5504:00/PT1Y",
            "R5/2020-02-07T15:36:00Z",
            "2020-02-07T15:36:00Z/PT5M",
            "R1/2020-05-12T18:25:00Z+02:00/PT5S",
            "R5/2021-04-14T08:15:00Z/P1Y2M3DT2H30S2M",
            "R5/2030-08-20T25:14-00:30/PT5S",
            "R5/2020-12-01T23:50:01/PT5S",
            "R5/2021-04-14T08:15:00Z/2021-13-15T08:15:00-04:00",
            "R5/2030-08-20T17:60Z/PT5S",
            "R2/PT3S/2021-04-32T08:15:00Z",
            "R5/2020-04-30T17:55-04:00/P5Y1M4DT13H30,5M0,4S",
            "R5/2020-04-30T17:55-04:00/P5Y1M4DT13H30.5M0.4S",
            "R2/PT3S/2021-02-29T08:15:00Z",
            "R2/PT3S/2021-06-31T08:15:00Z",
            "R5/2021-02-29T15:36:00Z/PT5S",
            "R5/2021-02-30T15:36:00Z/PT5S",
            "etc"
    };

    private static final String[] VALID_CRON_TIME_CYCLE_DURATIONS = {
            "1",
            "11",
            "2d",
            "2d5m",
            "2d5m30s",
            "2d5m30s40ms",
            "5m30s",
            "5m30s40ms",
            "30s",
            "30s40ms",
            "40ms",
            "2d 5m",
            "2d 5m30s",
            "2d 5m 30s 40ms",
            "5m 30s",
            "5m 30s 40ms",
            "30s",
            "30s 40ms",
            "40ms"
    };

    private static final String[] INVALID_CRON_TIME_CYCLE_DURATIONS = {
            "",
            "d",
            "d1",
            "5m2d",
            "5h8d",
            "8ms5s",
            "etc"
    };

    private static final String[] VALID_TIME_DATES = {
            "2013-10-24T20:15:00.000+00:00",
            "2030-08-20T17:59Z",
            "2020-05-01T15:45:00Z",
            "2030-08-20T22:14-00:30",
            "2013-10-24T20:15:00+02:00",
            "2020-02-29T22:14-00:30",
            "2024-02-29T22:14-00:30"
    };

    private static final String[] INVALID_TIME_DATES = {
            "201AA3-12-24T20:15:00.000+00:00",
            "2013-13-24T20:15:00+02:05",
            "2013-10-40T20:15:00+02:05",
            "2013-10-24T25:15:00+02:05",
            "2013-10-24T20:75:47+00:00",
            "2013-10-24T20:15:75+00:00",
            "2021-02-29T22:14-00:30",
            "2025-02-29T22:14-00:30",
            "2025-06-31T22:14-00:30",
            "etc"
    };

    private static final String[] VALID_EXPRESSIONS = {
            "#{something}"
    };

    private static final String[] INVALID_EXPRESSIONS = {
            "#",
            "{",
            "#{",
            "#{}",
            "#}",
            "}",
            "etc"
    };

    private static final String[] VALID_QUARTZ_CRON_TIME_CYCLE_DURATIONS = {
            "0 15 10 * * ? 2005",
            "0 0 0 1 * ?",
            "19 15 10 4 Apr ?",
            "0 43 9 ? * 5L",
    };

    private static final String[] INVALID_QUARTZ_CRON_TIME_CYCLE_DURATIONS = {
            "* * * * Foo ?",
            "* * * * Jan-Foo ?",
            "0 0 * * * *",
            "0 0 * 4 * *",
            "0 0 * * * 4",
            "0 43 9 1,5,29,L * ?",
            "0 43 9 ? * SAT,SUN,L",
            "0 43 9 ? * 6,7,L",
    };

    @Override
    protected void gwtSetUp() throws Exception {
        super.gwtSetUp();
        validator = new TimerSettingsValueValidator();
        value = new TimerSettingsValue();
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

    @Override
    public String getModuleName() {
        return "org.kie.workbench.common.stunner.bpmn.forms.validation.TimerSettingsValueValidatorTest";
    }

    @Test
    public void testValidateTimeDuration() {
        clear();
        loadValidTestElements(VALID_TIME_DURATIONS);
        loadValidTestElements(VALID_EXPRESSIONS);
        loadInvalidTestElements(TimerSettingsValueValidator.TimeDurationInvalid,
                                INVALID_TIME_DURATIONS);
        loadInvalidTestElements(TimerSettingsValueValidator.TimeDurationInvalid,
                                INVALID_EXPRESSIONS);
        testElements.add(new TestElement(null,
                                         false,
                                         TimerSettingsValueValidator.NoValueHasBeenProvided));
        testElements.forEach(testElement -> {
            value.setTimeDuration(testElement.getValue());
            testElement.setResult(validator.isValid(value,
                                                    context));
        });
        verifyTestResults();
    }

    @Test
    public void testValidateISOTimeCycle() {
        clear();
        loadValidTestElements(VALID_ISO_TIME_CYCLE_DURATIONS);
        loadValidTestElements(VALID_EXPRESSIONS);
        loadInvalidTestElements(TimerSettingsValueValidator.ISOTimeCycleInvalid,
                                INVALID_ISO_TIME_CYCLE_DURATIONS);
        loadInvalidTestElements(TimerSettingsValueValidator.ISOTimeCycleInvalid,
                                INVALID_EXPRESSIONS);
        testElements.forEach(testElement -> {
            value.setTimeCycleLanguage(TimerSettingsValueValidator.ISO);
            value.setTimeCycle(testElement.getValue());
            testElement.setResult(validator.isValid(value,
                                                    context));
        });
        testElements.add(new TestElement(null,
                                         false,
                                         TimerSettingsValueValidator.NoValueHasBeenProvided));
        value.setTimeCycleLanguage(null);
        value.setTimeCycle(null);
        testElements.get(testElements.size() - 1).setResult(validator.isValid(value,
                                                                              context));
        verifyTestResults();
    }

    @Test
    public void testValidateCronTimeCycle() {
        clear();
        loadValidTestElements(VALID_CRON_TIME_CYCLE_DURATIONS);
        loadValidTestElements(VALID_QUARTZ_CRON_TIME_CYCLE_DURATIONS);
        loadValidTestElements(VALID_EXPRESSIONS);
        loadInvalidTestElements(TimerSettingsValueValidator.CronTimeCycleInvalid,
                                INVALID_CRON_TIME_CYCLE_DURATIONS);
        loadInvalidTestElements(TimerSettingsValueValidator.CronTimeCycleInvalid,
                                INVALID_QUARTZ_CRON_TIME_CYCLE_DURATIONS);
        loadInvalidTestElements(TimerSettingsValueValidator.CronTimeCycleInvalid,
                                INVALID_EXPRESSIONS);
        testElements.forEach(testElement -> {
            value.setTimeCycleLanguage(TimerSettingsValueValidator.CRON);
            value.setTimeCycle(testElement.getValue());
            testElement.setResult(validator.isValid(value,
                                                    context));
        });
        testElements.add(new TestElement(null,
                                         false,
                                         TimerSettingsValueValidator.NoValueHasBeenProvided));
        value.setTimeCycleLanguage(null);
        value.setTimeCycle(null);
        testElements.get(testElements.size() - 1).setResult(validator.isValid(value,
                                                                              context));
        verifyTestResults();
    }

    @Test
    public void testValidateTimeDate() {
        clear();
        loadValidTestElements(VALID_TIME_DATES);
        loadValidTestElements(VALID_EXPRESSIONS);
        loadInvalidTestElements(TimerSettingsValueValidator.TimeDateInvalid,
                                INVALID_TIME_DATES);
        loadInvalidTestElements(TimerSettingsValueValidator.TimeDateInvalid,
                                INVALID_EXPRESSIONS);
        testElements.add(new TestElement(null,
                                         false,
                                         TimerSettingsValueValidator.NoValueHasBeenProvided));
        testElements.forEach(testElement -> {
            value.setTimeDate(testElement.getValue());
            testElement.setResult(validator.isValid(value,
                                                    context));
        });
        verifyTestResults();
    }

    private void loadValidTestElements(String... values) {
        Arrays.stream(values).forEach(value -> testElements.add(new TestElement(value,
                                                                                true)));
    }

    private void loadInvalidTestElements(String errorMessage,
                                         String... values) {
        Arrays.stream(values).forEach(value -> testElements.add(new TestElement(value,
                                                                                false,
                                                                                errorMessage)));
    }

    private void verifyTestResults() {
        int error = 0;
        for (int i = 0; i < testElements.size(); i++) {
            TestElement testElement = testElements.get(i);
            assertEquals("Invalid validation for item: " + testElement.toString(),
                         testElement.getExpectedResult(),
                         testElement.getResult());

            if (!testElement.getExpectedResult()) {
                assertEquals("Invalid validation: " + testElement.toString(),
                             testElement.getExpectedError(),
                             errorMessages.get(error));
                error++;
            }
        }
    }

    private void clear() {
        testElements.clear();
        errorMessages.clear();
    }

    private class TestElement {

        private String value = null;
        private boolean expectedResult;
        private String expectedError = null;
        private boolean result;

        public TestElement(String value,
                           boolean expectedResult) {
            this.value = value;
            this.expectedResult = expectedResult;
        }

        public TestElement(String value,
                           boolean expectedResult,
                           String expectedError) {
            this.value = value;
            this.expectedResult = expectedResult;
            this.expectedError = expectedError;
        }

        public String getValue() {
            return value;
        }

        public boolean getExpectedResult() {
            return expectedResult;
        }

        public String getExpectedError() {
            return expectedError;
        }

        public boolean getResult() {
            return result;
        }

        public void setResult(boolean result) {
            this.result = result;
        }

        @Override
        public String toString() {
            return "TestElement{" +
                    "value='" + value + '\'' +
                    ", expectedResult=" + expectedResult +
                    ", expectedError='" + expectedError + '\'' +
                    ", result=" + result +
                    '}';
        }
    }
}
