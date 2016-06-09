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

package org.kie.workbench.common.services.backend.validation;

import static org.junit.Assert.assertEquals;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;
import org.junit.runners.Parameterized.Parameter;
import org.junit.runners.Parameterized.Parameters;

@RunWith(Parameterized.class)
public class PackageNameValidatorTest {

    private static final PackageNameValidator VALIDATOR = new PackageNameValidator();

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

                              {"a z", false},
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
                              {"a-z", false},

                              {"123", false},
                              {"int", false},

                              {"a", true},
                              {"a.z", true},
                              {"under_score", true},
                              {"_int.x123", true}};
    }

    @Parameter(0)
    public String input;
    @Parameter(1)
    public boolean valid;

    @Test
    public void isValid() {
        assertEquals(valid, VALIDATOR.isValid(input));
    }

}