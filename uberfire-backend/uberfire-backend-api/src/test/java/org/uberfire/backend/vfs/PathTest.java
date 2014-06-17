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
