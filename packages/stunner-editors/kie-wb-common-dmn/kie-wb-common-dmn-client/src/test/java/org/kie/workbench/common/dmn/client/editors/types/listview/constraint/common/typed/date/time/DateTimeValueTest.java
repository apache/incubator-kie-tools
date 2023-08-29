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

package org.kie.workbench.common.dmn.client.editors.types.listview.constraint.common.typed.date.time;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.junit.MockitoJUnitRunner;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

@RunWith(MockitoJUnitRunner.class)
public class DateTimeValueTest {

    @Test
    public void testHasTimeWhenTimeIsNotSet() {

        final DateTimeValue value = new DateTimeValue();

        final boolean actual = value.hasTime();

        assertFalse(actual);
    }

    @Test
    public void testHasTime() {
        final DateTimeValue value = new DateTimeValue();
        value.setTime("time");

        final boolean actual = value.hasTime();

        assertTrue(actual);
    }

    @Test
    public void testHasDateWhenDateIsNotSet() {

        final DateTimeValue value = new DateTimeValue();

        final boolean actual = value.hasDate();

        assertFalse(actual);
    }

    @Test
    public void testHasDate() {

        final DateTimeValue value = new DateTimeValue();
        value.setDate("date");

        final boolean actual = value.hasDate();

        assertTrue(actual);
    }
}