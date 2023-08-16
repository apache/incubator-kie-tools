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


package org.kie.workbench.common.stunner.bpmn.client.util;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotEquals;

public class EqualsAndHashCodeTestUtils {

    public static class HashCodeAndEqualityTestCase {

        private Object a;
        private Object b;
        private boolean expectedResult;

        public HashCodeAndEqualityTestCase(Object a,
                                           Object b,
                                           boolean expectedResult) {
            this.a = a;
            this.b = b;
            this.expectedResult = expectedResult;
        }

        public Object getA() {
            return a;
        }

        public Object getB() {
            return b;
        }

        public boolean isExpectedResult() {
            return expectedResult;
        }
    }

    public static class TestCaseBuilder {

        private List<HashCodeAndEqualityTestCase> testCases = new ArrayList<>();

        private TestCaseBuilder() {
        }

        public static TestCaseBuilder newTestCase() {
            return new TestCaseBuilder();
        }

        public TestCaseBuilder addTrueCase(Object a,
                                           Object b) {
            testCases.add(new HashCodeAndEqualityTestCase(a,
                                                          b,
                                                          true));
            return this;
        }

        public TestCaseBuilder addFalseCase(Object a,
                                            Object b) {
            testCases.add(new HashCodeAndEqualityTestCase(a,
                                                          b,
                                                          false));
            return this;
        }

        public void test() {
            testHashCodeAndEquality(testCases);
        }
    }

    public static void testHashCodeAndEquality(Collection<HashCodeAndEqualityTestCase> testCases) {
        int index = 0;
        for (HashCodeAndEqualityTestCase testCase : testCases) {
            if (testCase.isExpectedResult()) {
                assertEquals("Equality check failed for test case element: " + index + " expected result is: " + testCase.isExpectedResult(),
                             testCase.getA(),
                             testCase.getB());
                assertEquals("HashCode check failed for test case element: " + index + " expected result is: " + testCase.isExpectedResult(),
                             hashCode(testCase.getA()),
                             hashCode(testCase.getB()));
            } else {
                assertNotEquals("Equality check failed for test case element: " + index + " expected result is: " + testCase.isExpectedResult(),
                                testCase.getA(),
                                testCase.getB());
                assertNotEquals("HashCode check failed for test case element: " + index + " expected result is: " + testCase.isExpectedResult(),
                                hashCode(testCase.getA()),
                                hashCode(testCase.getB()));
            }
            index++;
        }
    }

    private static Integer hashCode(Object value) {
        return value != null ? value.hashCode() : null;
    }
}