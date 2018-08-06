/*
 * Copyright 2016 Red Hat, Inc. and/or its affiliates.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.uberfire.ext.editor.commons.backend.validation;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;
import org.junit.runners.Parameterized.Parameter;
import org.junit.runners.Parameterized.Parameters;

import static org.junit.Assert.*;

@RunWith(Parameterized.class)
public class ValidationUtils_ParameterizedJavaIdTest {

    @Parameter(0)
    public String input;
    @Parameter(1)
    public boolean valid;

    @Parameters
    public static Object[][] data() {
        return new Object[][]{{null, false},
                {"", false},
                {" ", false},
                {"\n", false},
                {"\\", false},
                {"/", false},
                {"\r", false},
                {"\t", false},
                {"\"", false},
                {"`", false},
                {"?", false},
                {"*", false},
                {"<", false},
                {">", false},
                {"|", false},
                {":", false},

                {".", false},
                {"..", false},
                {". ", false},
                {" .", false},
                {".-.", false},
                {"a.z", false},

                {"a\nz", false},
                {"a\\z", false},
                {"a/z", false},
                {"a\rz", false},
                {"a\tz", false},
                {"a\"z", false},
                {"a`z", false},
                {"a?z", false},
                {"a*z", false},
                {"a<z", false},
                {"a>z", false},
                {"a|z", false},
                {"a:z", false},

                {"a ", false},
                {" z", false},
                {"tchao salut", false},

                {"0one", false},
                {"dash-y", false},
                {"Fire!fire!help", false},
                {"Fire,help", false},
                {"füür", true},
                {"anyone()questionmark", false},

                {"true", false},
                {"==", false},
                {"null", false},
                {"class", false},

                {String.valueOf((char) 7), false},
                {String.valueOf((char) 127), false},

                {"a", true},
                {"classyAndSuperShinyNewCustomThing", true},
                {"Misc2", true},
                {"under_score", true},
                {"背景色", true}};
    }

    @Test
    public void isJavaIdentifier() {
        assertEquals(valid,
                     ValidationUtils.isJavaIdentifier(input));
    }
}
