/*
 * Copyright 2019 Red Hat, Inc. and/or its affiliates.
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

package org.kie.workbench.common.dmn.client.editors.types.listview.constraint.common.typed.common;

import org.junit.Test;

import static org.junit.Assert.assertEquals;
import static org.kie.workbench.common.dmn.client.editors.types.listview.constraint.common.typed.common.DurationHelper.addFunctionCall;
import static org.kie.workbench.common.dmn.client.editors.types.listview.constraint.common.typed.common.DurationHelper.getFunctionParameter;

public class DurationHelperTest {

    @Test
    public void testAddFunctionCall() {

        final String actual = addFunctionCall("<VALUE>");
        final String expected = "duration(\"<VALUE>\")";

        assertEquals(expected, actual);
    }

    @Test
    public void testGetFunctionParameter() {

        final String actual = getFunctionParameter("duration(\"<VALUE>\")");
        final String expected = "<VALUE>";

        assertEquals(expected, actual);
    }

    @Test
    public void testGetFunctionParameterWithSpacesBeforeOpenBracket() {

        final String actual = getFunctionParameter("duration        (\"<VALUE>\")");
        final String expected = "<VALUE>";

        assertEquals(expected, actual);
    }

    @Test
    public void testGetFunctionParameterWithSpacesInsideBrackets() {

        final String actual = getFunctionParameter("duration(     \"<VALUE>\"     )");
        final String expected = "<VALUE>";

        assertEquals(expected, actual);
    }
}
