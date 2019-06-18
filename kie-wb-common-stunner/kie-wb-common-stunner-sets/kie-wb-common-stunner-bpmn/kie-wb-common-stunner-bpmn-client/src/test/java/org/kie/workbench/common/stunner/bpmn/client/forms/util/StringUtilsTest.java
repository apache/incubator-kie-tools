/*
 * Copyright 2016 Red Hat, Inc. and/or its affiliates.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.kie.workbench.common.stunner.bpmn.client.forms.util;

import org.junit.Test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

public class StringUtilsTest {

    @Test
    public void testCreateDataTypeDisplayName() {
        assertEquals("Chairs [com.test]",
                     StringUtils.createDataTypeDisplayName("com.test.Chairs"));
    }

    @Test
    public void testRegexSequence() {

        String test1 = "123Test";
        assertTrue(test1.matches(StringUtils.ALPHA_NUM_REGEXP));

        String test2 = "123Test ";
        assertFalse(test2.matches(StringUtils.ALPHA_NUM_REGEXP));

        String test3 = "123Test #";
        assertFalse(test3.matches(StringUtils.ALPHA_NUM_REGEXP));

        String test4 = "123Test";
        assertTrue(test4.matches(StringUtils.ALPHA_NUM_SPACE_REGEXP));

        String test5 = "123Test ";
        assertTrue(test5.matches(StringUtils.ALPHA_NUM_SPACE_REGEXP));

        String test6 = "123Test #";
        assertFalse(test6.matches(StringUtils.ALPHA_NUM_SPACE_REGEXP));
    }
}
