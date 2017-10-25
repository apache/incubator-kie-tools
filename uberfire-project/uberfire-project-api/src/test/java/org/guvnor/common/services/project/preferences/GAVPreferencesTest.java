/*
 * Copyright 2016 Red Hat, Inc. and/or its affiliates.
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not
 * use this file except in compliance with the License. You may obtain a copy of
 * the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS, WITHOUT
 * WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the
 * License for the specific language governing permissions and limitations under
 * the License.
 */
package org.guvnor.common.services.project.preferences;

import org.junit.After;
import org.junit.Test;

import static org.junit.Assert.*;

public class GAVPreferencesTest {

    @Test
    public void defaultValueTest() {
        GAVPreferences gavPreferences = new GAVPreferences();
        gavPreferences.defaultValue(gavPreferences);

        assertFalse(gavPreferences.isChildGAVEditEnabled());
        assertFalse(gavPreferences.isConflictingGAVCheckDisabled());

        System.setProperty(GAVPreferences.CONFLICTING_GAV_CHECK_DISABLED,
                           "true");
        System.setProperty(GAVPreferences.CHILD_GAV_EDIT_ENABLED,
                           "true");

        gavPreferences.defaultValue(gavPreferences);

        assertTrue(gavPreferences.isChildGAVEditEnabled());
        assertTrue(gavPreferences.isConflictingGAVCheckDisabled());
    }

    @After
    public void clearProperties() {
        System.clearProperty(GAVPreferences.CONFLICTING_GAV_CHECK_DISABLED);
        System.clearProperty(GAVPreferences.CHILD_GAV_EDIT_ENABLED);
    }
}
