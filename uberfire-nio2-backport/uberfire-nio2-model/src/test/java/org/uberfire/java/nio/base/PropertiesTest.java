/*
 * Copyright 2015 JBoss, by Red Hat, Inc
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

package org.uberfire.java.nio.base;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import org.junit.Test;

import static org.junit.Assert.*;

/**
 *
 */
public class PropertiesTest {

    @Test
    public void testState() throws IOException {
        final File fcontent = File.createTempFile( "foo", "bar" );
        final Properties properties = new Properties();
        final Date dt = new Date();

        properties.put( "int", 10453 );
        properties.put( "long", 1000000L );
        properties.put( "date", dt );

        final OutputStream out = new FileOutputStream( fcontent );
        properties.store( out );

        final Properties loadProperties = new Properties();

        final InputStream in = new FileInputStream( fcontent );
        loadProperties.load( in );

        assertNotNull( properties.get( "int" ) );
        assertNotNull( properties.get( "long" ) );
        assertNotNull( properties.get( "date" ) );

        assertEquals( 10453, properties.get( "int" ) );
        assertEquals( 1000000L, properties.get( "long" ) );
        assertEquals( dt, properties.get( "date" ) );

    }

    @Test
    public void testEmptyState() throws IOException {
        final File fcontent = File.createTempFile( "foo2", "bar" );
        final Properties loadProperties = new Properties();

        final InputStream in = new FileInputStream( fcontent );
        loadProperties.load( in );

        assertEquals( 0, loadProperties.size() );
    }

    @Test
    public void testNullValues() throws IOException {
        final Map<String, Object> original = new HashMap<String, Object>();
        original.put( "key1",
                      "value1" );
        original.put( "key2",
                      null );

        final Properties properties = new Properties( original );

        assertEquals( 1,
                      properties.size() );
        assertTrue( properties.containsKey( "key1" ) );
        assertEquals( "value1",
                      properties.get( "key1" ) );
        assertFalse( properties.containsKey( "key2" ) );
    }

}
