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

package org.uberfire.io;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.util.Collections;
import java.util.Map;

import org.uberfire.java.nio.file.Path;

public class SimpleFSIOServiceDotFileTest extends CommonIOExceptionsServiceDotFileTest {

    protected static final Map<String, Object> EMPTY_ENV = Collections.emptyMap();

    @Override
    protected int testFileAttrSize4() {
        return 7;
    }

    @Override
    protected int testFileAttrSize3() {
        return 10;
    }

    @Override
    protected int testFileAttrSize2() {
        return 10;
    }

    @Override
    protected int testFileAttrSize1() {
        return 10;
    }

    @Override
    protected int testDirectoryAttrSize4() {
        return 7;
    }

    @Override
    protected int testDirectoryAttrSize3() {
        return 10;
    }

    @Override
    protected int testDirectoryAttrSize2() {
        return 11;
    }

    @Override
    protected int testDirectoryAttrSize1() {
        return 10;
    }

    @Override
    protected int createDirectoriesAttrSize() {
        return 8;
    }

    @Override
    protected int testNewByteChannelAttrSize() {
        return 8;
    }

    @Override
    public Path getFilePath() {
        try {
            final File dir = createTempDirectory();
            return ioService().get( dir.toURI() ).resolve( "myTempFile.txt" );
        } catch ( IOException e ) {
            throw new RuntimeException( e );
        }
    }

    @Override
    public Path getTargetPath() {
        try {
            final File dir = createTempDirectory();
            return ioService().get( dir.toURI() ).resolve( "myTargetFile.txt" );
        } catch ( IOException e ) {
            throw new RuntimeException( e );
        }
    }

    @Override
    public Path getDirectoryPath() {
        try {
            final File dir = createTempDirectory();
            return ioService().get( dir.toURI() ).resolve( "myDir" );
        } catch ( IOException e ) {
            throw new RuntimeException( e );
        }
    }

    @Override
    public Path getComposedDirectoryPath() {
        try {
            final File dir = createTempDirectory();
            return ioService().get( dir.toURI() ).resolve( "path/to/myDir" );
        } catch ( IOException e ) {
            throw new RuntimeException( e );
        }
    }

    public File tempFile( final String content ) throws IOException {
        final File file = File.createTempFile( "bar", "foo" );
        tempFiles.add( file );
        final OutputStream out = new FileOutputStream( file );

        if ( content != null && !content.isEmpty() ) {
            out.write( content.getBytes() );
            out.flush();
        }

        out.close();

        return file;
    }
}
