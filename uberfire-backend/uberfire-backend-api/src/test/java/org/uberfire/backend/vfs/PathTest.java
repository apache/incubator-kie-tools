/*
 * Copyright 2015 Red Hat, Inc. and/or its affiliates.
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

package org.uberfire.backend.vfs;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.junit.Test;

import static org.fest.assertions.api.Assertions.*;
import static org.uberfire.backend.vfs.PathFactory.*;

/**
 *
 */
public class PathTest {

    final FileSystem fs = new FileSystem() {

        @Override
        public List<Path> getRootDirectories() {
            return null;
        }

        @Override
        public Set<String> supportedFileAttributeViews() {
            return null;
        }
    };

    @Test
    public void generalState() {
        {
            final Path path = newPath( "resource", "scheme://path/to/some/resource" );
            assertThat( path.equals( path ) ).isTrue();
            assertThat( path.equals( newPath( "resource", "scheme://path/to/some/resource" ) ) ).isTrue();
            assertThat( path.hashCode() ).isEqualTo( newPath( "resource", "scheme://path/to/some/resource" ).hashCode() );
            assertThat( path.hashCode() ).isEqualTo( path.hashCode() );
        }

        {
            final Path path = newPath( "resource", "scheme://different/path/to/some/resource" );
            assertThat( path.equals( newPath( "resource", "scheme://path/to/some/resource" ) ) ).isFalse();
            assertThat( path.hashCode() ).isNotEqualTo( newPath( "resource", "scheme://path/to/some/resource" ).hashCode() );
        }

        {
            final Path path = newPath( "resource", "scheme://different/path/to/some/resource" );
            assertThat( path.equals( "something" ) ).isFalse();
            assertThat( path.equals( null ) ).isFalse();
        }
    }

    @Test
    public void checkNPE() {
        final Map<Path, String> hashMap = new HashMap<Path, String>();
        final Path path = newPath( "defaultPackage", "default://guvnor-jcr2vfs-migration/defaultPackage/" );
        hashMap.put( path, "content" );
        assertThat( hashMap.get( path ) ).isEqualTo( "content" );

        assertThat( hashMap.get( newPath( "defaultPackage", "default://guvnor-jcr2vfs-migration/defaultPackage/" ) ) ).isEqualTo( "content" );
    }
}
