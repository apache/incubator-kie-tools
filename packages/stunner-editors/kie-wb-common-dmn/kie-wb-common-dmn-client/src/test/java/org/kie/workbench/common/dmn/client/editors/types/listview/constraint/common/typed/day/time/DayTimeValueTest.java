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

package org.kie.workbench.common.dmn.client.editors.types.listview.constraint.common.typed.day.time;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.junit.MockitoJUnitRunner;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import static org.kie.workbench.common.dmn.client.editors.types.listview.constraint.common.typed.day.time.DayTimeValue.NONE;

@RunWith(MockitoJUnitRunner.class)
public class DayTimeValueTest {

    @Test
    public void testIsEmptyWithoutValues() {
        assertTrue(new DayTimeValue().isEmpty());
    }

    @Test
    public void testIsEmptyWithDays() {
        assertFalse(new DayTimeValue(1, NONE, NONE, NONE).isEmpty());
    }

    @Test
    public void testIsEmptyWithHours() {
        assertFalse(new DayTimeValue(NONE, 1, NONE, NONE).isEmpty());
    }

    @Test
    public void testIsEmptyWithMinutes() {
        assertFalse(new DayTimeValue(NONE, NONE, 1, NONE).isEmpty());
    }

    @Test
    public void testIsEmptyWithSeconds() {
        assertFalse(new DayTimeValue(NONE, NONE, NONE, 1).isEmpty());
    }
}
