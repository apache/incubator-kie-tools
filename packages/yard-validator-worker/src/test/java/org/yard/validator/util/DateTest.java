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
package org.yard.validator.util;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;

import java.util.Arrays;
import java.util.Collection;

import static org.junit.Assert.assertEquals;

@RunWith(Parameterized.class)
public class DateTest {

    private final String givenDateA;
    private final String givenDateB;
    private final int expectedNumber;

    public DateTest(final String givenDateA,
                    final String givenDateB,
                    final int expectedNumber) {

        this.givenDateA = givenDateA;
        this.givenDateB = givenDateB;
        this.expectedNumber = expectedNumber;
    }

    @Parameterized.Parameters
    public static Collection<Object[]> data() {
        return Arrays.asList(new Object[][]{
                {"2000-10-30", "2000-10-30", 0},
                {"2000-10-30", "2000-10-31", -1},
                {"2000-10-31", "2000-10-30", 1},
                {"2000-10-30", "2000-11-30", -1},
                {"2000-11-30", "2000-10-30", 1},
                {"2000-10-30", "2001-10-30", -1},
                {"2001-10-30", "2000-10-30", 1}
        });
    }

    @Test
    public void test() {
        assertEquals(expectedNumber,
                new Date(givenDateA).compareTo(
                        new Date(givenDateB)));

    }
}