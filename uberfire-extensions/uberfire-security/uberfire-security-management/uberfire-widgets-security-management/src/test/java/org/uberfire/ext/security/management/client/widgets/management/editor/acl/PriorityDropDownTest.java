/*
 * Copyright 2016 Red Hat, Inc. and/or its affiliates.
 *  
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *  
 *    http://www.apache.org/licenses/LICENSE-2.0
 *  
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.uberfire.ext.security.management.client.widgets.management.editor.acl;

import java.util.Arrays;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;
import org.uberfire.ext.widgets.common.client.dropdown.LiveSearchDropDown;

import static org.junit.Assert.*;

@RunWith(MockitoJUnitRunner.class)
public class PriorityDropDownTest {

    PriorityDropDown priorityDropDown;

    @Mock
    LiveSearchDropDown searchDropDown;

    @Before
    public void setup() {
        priorityDropDown = new PriorityDropDown(searchDropDown);
        priorityDropDown.priorityItemList = Arrays.asList("5",
                                                          "4",
                                                          "3",
                                                          "2",
                                                          "1");
    }

    @Test
    public void testResolvePriority() {
        assertEquals(priorityDropDown.getPriorityName(-10),
                     "1");
        assertEquals(priorityDropDown.getPriorityName(-5),
                     "2");
        assertEquals(priorityDropDown.getPriorityName(-1),
                     "2");
        assertEquals(priorityDropDown.getPriorityName(0),
                     "3");
        assertEquals(priorityDropDown.getPriorityName(1),
                     "4");
        assertEquals(priorityDropDown.getPriorityName(5),
                     "4");
        assertEquals(priorityDropDown.getPriorityName(6),
                     "5");
    }
}
