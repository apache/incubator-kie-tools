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

package org.kie.workbench.common.screens.examples.backend.server;

import java.util.Map;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.kie.workbench.common.screens.examples.service.ExamplesService;

import static org.junit.Assert.*;

public class ExamplesPreferencesLoaderTest {

    private String oldPropertyValue;

    private ExamplesPreferencesLoader loader;

    @Before
    public void setup() {
        oldPropertyValue = System.getProperty( ExamplesService.EXAMPLES_SYSTEM_PROPERTY );
        loader = new ExamplesPreferencesLoader();
    }

    @After
    public void reset() {
        if ( oldPropertyValue != null ) {
            System.setProperty( ExamplesService.EXAMPLES_SYSTEM_PROPERTY,
                                oldPropertyValue );
        } else {
            System.clearProperty( ExamplesService.EXAMPLES_SYSTEM_PROPERTY );
        }
    }

    @Test
    public void testWithoutSystemProperty() {
        final Map<String, String> results = loader.load();
        assertNotNull( results );
        assertEquals( 1,
                      results.size() );
        assertTrue( Boolean.parseBoolean( results.get( ExamplesService.EXAMPLES_SYSTEM_PROPERTY ) ) );
    }

    @Test
    public void testWithSystemPropertyTrue() {
        System.setProperty( ExamplesService.EXAMPLES_SYSTEM_PROPERTY,
                            "true" );
        final Map<String, String> results = loader.load();
        assertNotNull( results );
        assertEquals( 1,
                      results.size() );
        assertTrue( Boolean.parseBoolean( results.get( ExamplesService.EXAMPLES_SYSTEM_PROPERTY ) ) );
    }

    @Test
    public void testWithSystemPropertyFalse() {
        System.setProperty( ExamplesService.EXAMPLES_SYSTEM_PROPERTY,
                            "false" );
        final Map<String, String> results = loader.load();
        assertNotNull( results );
        assertEquals( 1,
                      results.size() );
        assertFalse( Boolean.parseBoolean( results.get( ExamplesService.EXAMPLES_SYSTEM_PROPERTY ) ) );
    }

    @Test
    public void testWithSystemPropertyDuffValue() {
        System.setProperty( ExamplesService.EXAMPLES_SYSTEM_PROPERTY,
                            "cheese" );
        final Map<String, String> results = loader.load();
        assertNotNull( results );
        assertEquals( 1,
                      results.size() );
        assertFalse( Boolean.parseBoolean( results.get( ExamplesService.EXAMPLES_SYSTEM_PROPERTY ) ) );
    }

}
