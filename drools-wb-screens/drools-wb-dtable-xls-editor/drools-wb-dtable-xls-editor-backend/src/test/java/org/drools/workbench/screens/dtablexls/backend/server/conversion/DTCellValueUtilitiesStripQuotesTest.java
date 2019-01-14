/*
 * Copyright 2017 Red Hat, Inc. and/or its affiliates.
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

package org.drools.workbench.screens.dtablexls.backend.server.conversion;

import java.util.HashMap;
import java.util.Map;

import org.junit.BeforeClass;
import org.junit.Test;
import org.kie.workbench.common.services.shared.preferences.ApplicationPreferences;

import static org.junit.Assert.assertEquals;

public class DTCellValueUtilitiesStripQuotesTest {

    private static final String DATE_FORMAT = "dd-MM-yyyy";

    @BeforeClass
    public static void setup() {
        setupPreferences();
    }

    private static void setupPreferences() {
        final Map<String, String> preferences = new HashMap<String, String>() {{
            put(ApplicationPreferences.DATE_FORMAT,
                DATE_FORMAT);
        }};
        ApplicationPreferences.setUp(preferences);
    }

    @Test
    // This test is in it's own class since DTCellValueUtilitiesTest is @Parameterized
    // and any tests therein will execute multiple times for each set of parameters.
    public void checkStripQuotes() {
        assertEquals("a",
                     DTCellValueUtilities.stripQuotes("a"));
        assertEquals("a",
                     DTCellValueUtilities.stripQuotes("\"a\""));
        assertEquals("a",
                     DTCellValueUtilities.stripQuotes("\"a"));
        assertEquals("a",
                     DTCellValueUtilities.stripQuotes("a\""));
    }
}
