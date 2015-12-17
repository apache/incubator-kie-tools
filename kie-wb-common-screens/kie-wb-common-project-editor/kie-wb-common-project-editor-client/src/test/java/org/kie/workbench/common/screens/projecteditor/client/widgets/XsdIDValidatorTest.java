/*
 * Copyright 2015 Red Hat, Inc. and/or its affiliates.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * 
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
*/

package org.kie.workbench.common.screens.projecteditor.client.widgets;

import org.junit.Test;

import static org.jgroups.util.Util.*;

public class XsdIDValidatorTest {

    @Test
    public void testStartWithLetter() throws Exception {
        assertTrue(XsdIDValidator.validate("test"));
    }

    //  can only contain letters, digits, underscores, hyphens, and periods.

    @Test
    public void testStartsWithUnderScore() throws Exception {
        assertTrue(XsdIDValidator.validate("_test"));
    }

    @Test
    public void testStartsWithNumber() throws Exception {
        assertFalse(XsdIDValidator.validate("1test"));
    }

    @Test
    public void testStartsWithExclamationMark() throws Exception {
        assertFalse(XsdIDValidator.validate("!test"));
    }

    @Test
    public void testContainsExclamationMark() throws Exception {
        assertFalse(XsdIDValidator.validate("test!"));
    }

    @Test
    public void testColons() throws Exception {
        assertFalse(XsdIDValidator.validate("test:"));
    }

    @Test
    public void testSpace() throws Exception {
        assertFalse(XsdIDValidator.validate("test me"));
    }

    @Test
    public void testContainsPeriod() throws Exception {
        assertTrue(XsdIDValidator.validate("test."));
    }

    @Test
    public void testUnderscorePeriod() throws Exception {
        assertTrue(XsdIDValidator.validate("test_me"));
    }

    @Test
    public void testHyphenPeriod() throws Exception {
        assertTrue(XsdIDValidator.validate("test-me"));
    }

}
