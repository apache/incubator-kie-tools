/*
 * Copyright 2017 JBoss, by Red Hat, Inc
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *       http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.dashbuilder.navigation;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;

import java.util.Arrays;
import java.util.Collection;

import static org.junit.Assert.assertEquals;
import static org.junit.runners.Parameterized.Parameter;
import static org.junit.runners.Parameterized.Parameters;

@RunWith(Parameterized.class)
public class NavItemContextInclusionTest {

    @Parameters
    public static Collection<Object[]> data() {
        return Arrays.asList(new Object[][]{
                {"", "", true},
                {"a=1", "a=1", true}, //identical ctxts
                {"a=1;b=2", "a=1;b=2", true},
                {"a=1;b=2", "b=2;a=1", true}, // order should not matter
                {"a=1;b=2", "a=1", true}, // extra property
                {"a=1;b=2;c=3", "b=2", true}, // extra property in different place

                {"a=1;b=2", "c=3", false}, // property not included
                {"a=1", "a=2", false}, // different value of property
                {"a=1;b=2", "a=1;c=2", false}, // same amount of props, but different names
                {"A=1", "a=1", false}, // property names are case sensitive
        });
    }

    @Parameter(0)
    public String ctx1;

    @Parameter(1)
    public String ctx2;

    @Parameter(2)
    public boolean includesPropertiesOf;

    @Test
    public void testMatch() {
        NavItemContext
                c1 = NavItemContext.get(ctx1),
                c2 = NavItemContext.get(ctx2);

        String msg = String.format("NavItemContext %s should%s include properties of  %s", ctx1, (includesPropertiesOf ? "" : " not"), ctx2);

        assertEquals(msg, includesPropertiesOf, c1.includesPropertiesOf(c2));
    }
}
