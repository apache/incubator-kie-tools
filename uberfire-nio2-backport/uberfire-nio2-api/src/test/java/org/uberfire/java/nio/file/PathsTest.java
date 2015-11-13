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

package org.uberfire.java.nio.file;

import java.net.URI;

import org.junit.Test;

import static org.fest.assertions.api.Assertions.*;

public class PathsTest {

    @Test
    public void simpleGet() {
        final Path path = Paths.get( "/path/to/file.txt" );

        assertThat( path ).isNotNull();
        assertThat( path.isAbsolute() ).isTrue();
        assertThat( path.toString() ).isEqualTo( "/path/to/file.txt" );
    }

    @Test
    public void simplePartitionedGet() {
        final Path path = Paths.get( "/path", "to", "file.txt" );

        assertThat( path ).isNotNull();
        assertThat( path.isAbsolute() ).isTrue();
        assertThat( path.toString() ).isEqualTo( "/path/to/file.txt" );

        assertThat( path ).isEqualTo( Paths.get( "/path/to/file.txt" ) );
    }

    @Test
    public void simpleWindowsGet() {
        final Path path = Paths.get( "c:\\path\\to\\file.txt" );

        assertThat( path ).isNotNull();
        assertThat( path.isAbsolute() ).isTrue();
        assertThat( path.toString() ).isEqualTo( "c:\\path\\to\\file.txt" );
    }

    @Test
    public void simplePartitionedWindowsGet() {
        final Path path = Paths.get( "c:\\path", "to", "file.txt" );

        assertThat( path ).isNotNull();
        assertThat( path.isAbsolute() ).isTrue();

        assertThat( path.toString() ).isEqualTo( "c:\\path" + separator() + "to" + separator() + "file.txt" );
    }

    @Test
    public void simpleGetButUsingURIAsString() {
        final Path path = Paths.get( "file:///path/to/file.txt" );

        assertThat( path ).isNotNull();
        assertThat( path.isAbsolute() ).isTrue();
        assertThat( path.toString() ).isEqualTo( "/path/to/file.txt" );

        assertThat( path ).isEqualTo( Paths.get( "/path/to/file.txt" ) );
    }

    @Test
    public void simpleGetButUsingURIAsStringAndDefaultScheme() {
        final Path path = Paths.get( "default:///path/to/file.txt" );

        assertThat( path ).isNotNull();
        assertThat( path.isAbsolute() ).isTrue();
        assertThat( path.toString() ).isEqualTo( "/path/to/file.txt" );

        assertThat( path ).isEqualTo( Paths.get( "/path/to/file.txt" ) );
    }

    @Test
    public void simpleGetURI() {
        final Path path = Paths.get( URI.create( "file:///path/to/file.txt" ) );

        assertThat( path ).isNotNull();
        assertThat( path.isAbsolute() ).isTrue();
        assertThat( path.toString() ).isEqualTo( "/path/to/file.txt" );

        assertThat( path ).isEqualTo( Paths.get( "/path/to/file.txt" ) );
    }

    @Test
    public void simpleGetEmpty() {
        final Path path = Paths.get( "" );

        assertThat( path ).isNotNull();
        assertThat( path.isAbsolute() ).isFalse();
        assertThat( path.toString() ).isEqualTo( "" );
    }

    @Test(expected = IllegalArgumentException.class)
    public void simpleGetNull1() {
        Paths.get( (String) null );
    }

    @Test(expected = IllegalArgumentException.class)
    public void simpleGetNull2() {
        Paths.get( (URI) null );
    }

    private String separator() {
        return System.getProperty( "file.separator", "/" );
    }

}
