/*
 * Copyright 2012 JBoss Inc
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

package org.uberfire.java.nio.fs.jgit;

import java.io.IOException;
import java.io.OutputStream;
import java.net.URI;

import org.junit.Test;
import org.uberfire.java.nio.file.DirectoryStream;
import org.uberfire.java.nio.file.FileSystemAlreadyExistsException;
import org.uberfire.java.nio.file.Path;

import static org.fest.assertions.api.Assertions.*;

public class JGitFileSystemProviderGCTest extends AbstractTestInfra {

    @Test
    public void testGC() throws IOException {
        final URI newRepo = URI.create( "git://gc-repo-name" );

        final JGitFileSystem fs = (JGitFileSystem) provider.newFileSystem( newRepo, EMPTY_ENV );

        assertThat( fs ).isNotNull();

        final DirectoryStream<Path> stream = provider.newDirectoryStream( provider.getPath( newRepo ), null );
        assertThat( stream ).isNotNull().hasSize( 0 );

        try {
            provider.newFileSystem( newRepo, EMPTY_ENV );
            failBecauseExceptionWasNotThrown( FileSystemAlreadyExistsException.class );
        } catch ( final Exception ex ) {
        }

        for ( int i = 0; i < 19; i++ ) {
            assertThat( fs.getNumberOfCommitsSinceLastGC() ).isEqualTo( i );

            final Path path = provider.getPath( URI.create( "git://gc-repo-name/path/to/myfile" + i + ".txt" ) );

            final OutputStream outStream = provider.newOutputStream( path );
            assertThat( outStream ).isNotNull();
            outStream.write( ( "my cool" + i + " content" ).getBytes() );
            outStream.close();
        }

        final Path path = provider.getPath( URI.create( "git://gc-repo-name/path/to/myfile.txt" ) );

        final OutputStream outStream = provider.newOutputStream( path );
        assertThat( outStream ).isNotNull();
        outStream.write( "my cool content".getBytes() );
        outStream.close();
        assertThat( fs.getNumberOfCommitsSinceLastGC() ).isEqualTo( 0 );

        final OutputStream outStream2 = provider.newOutputStream( path );
        assertThat( outStream2 ).isNotNull();
        outStream2.write( "my co dwf sdf ol content".getBytes() );
        outStream2.close();
        assertThat( fs.getNumberOfCommitsSinceLastGC() ).isEqualTo( 1 );
    }

}
