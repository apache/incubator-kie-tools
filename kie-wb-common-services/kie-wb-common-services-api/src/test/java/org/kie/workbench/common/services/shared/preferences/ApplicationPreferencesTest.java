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

package org.kie.workbench.common.services.shared.preferences;

import java.util.HashMap;

import org.junit.Test;

import static org.junit.Assert.*;

public class ApplicationPreferencesTest {

    @Test
    public void testBeforeSetup() {
        //Test that use prior to being setup (usually by an Async call) does not throw exceptions
        assertNull( ApplicationPreferences.getStringPref( "any" ) );
        assertNull( ApplicationPreferences.getCurrentDroolsVersion() );
        assertNull( ApplicationPreferences.getDroolsDateFormat() );
        assertNull( ApplicationPreferences.getDroolsDateTimeFormat() );
        assertFalse( ApplicationPreferences.getBooleanPref( "any" ) );
    }

    @Test
    public void testAfterSetup() {
        //Test that use after being setup (usually by an Async call) holds correct values
        assertNull( ApplicationPreferences.getStringPref( "any" ) );
        assertNull( ApplicationPreferences.getCurrentDroolsVersion() );
        assertNull( ApplicationPreferences.getDroolsDateFormat() );
        assertNull( ApplicationPreferences.getDroolsDateTimeFormat() );
        assertFalse( ApplicationPreferences.getBooleanPref( "any" ) );

        ApplicationPreferences.setUp( new HashMap<String, String>() {{
            put( "boolean",
                 "true" );
            put( "string",
                 "string" );
            put( ApplicationPreferences.KIE_VERSION_PROPERTY_NAME,
                 "version" );
            put( ApplicationPreferences.DATE_FORMAT,
                 "dd-MM-yyyy" );
            put( ApplicationPreferences.DATE_TIME_FORMAT,
                 "dd-MM-yyyy hh:mm:ss" );
        }} );

        assertEquals( "string",
                      ApplicationPreferences.getStringPref( "string" ) );
        assertEquals( "version",
                      ApplicationPreferences.getCurrentDroolsVersion() );
        assertEquals( "dd-MM-yyyy",
                      ApplicationPreferences.getDroolsDateFormat() );
        assertEquals( "dd-MM-yyyy hh:mm:ss",
                      ApplicationPreferences.getDroolsDateTimeFormat() );
        assertTrue( ApplicationPreferences.getBooleanPref( "boolean" ) );
    }

}
