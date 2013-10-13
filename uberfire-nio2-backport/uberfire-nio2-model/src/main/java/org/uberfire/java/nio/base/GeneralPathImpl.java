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

package org.uberfire.java.nio.base;

import java.io.File;
import java.util.regex.Matcher;

import org.uberfire.java.nio.file.FileSystem;
import org.uberfire.java.nio.file.Path;

import static org.uberfire.commons.validation.Preconditions.*;

public class GeneralPathImpl
        extends AbstractPath<FileSystem>
        implements SegmentedPath {

    private GeneralPathImpl( final FileSystem fs,
                             final File file ) {
        super( fs, file );
    }

    private GeneralPathImpl( final FileSystem fs,
                             final String path,
                             boolean isRoot,
                             boolean isRealPath,
                             boolean isNormalized ) {
        super( fs, path, "", isRoot, isRealPath, isNormalized );
    }

    @Override
    protected RootInfo setupRoot( final FileSystem fs,
                                  final String path,
                                  final String host,
                                  final boolean isRoot ) {

        final boolean isRooted = isRoot ? true : path.startsWith( "/" );
        final Matcher hasWindowsDrive = WINDOWS_DRIVER.matcher( path );

        final boolean isAbsolute;
        if ( isRooted || hasWindowsDrive.matches() ) {
            isAbsolute = true;
        } else {
            isAbsolute = false;
        }

        int lastOffset = isAbsolute ? 1 : 0;
        int windowsDriveEndsAt = -1;
        if ( isAbsolute && hasWindowsDrive.matches() ) {
            windowsDriveEndsAt = hasWindowsDrive.toMatchResult().end( 1 ) + 1;
            lastOffset = windowsDriveEndsAt;
        }

        final boolean isFinalRoot;
        if ( path.length() == 1 && lastOffset == 1 ) {
            isFinalRoot = true;
        } else if ( hasWindowsDrive.matches() && path.length() == windowsDriveEndsAt ) {
            isFinalRoot = true;
        } else {
            isFinalRoot = isRoot;
        }

        return new RootInfo( lastOffset, isAbsolute, isFinalRoot, path.getBytes() );
    }

    @Override
    protected String defaultDirectory() {
        if ( usesWindowsFormat ) {
            final String result = new File( "" ).getAbsolutePath().replaceAll( "/", "\\\\" ) + "\\";

            if ( !hasWindowsDriver( result ) ) {
                return DEFAULT_WINDOWS_DRIVER + result;
            }
            return result;
        }
        return new File( "" ).getAbsolutePath() + "/";
    }

    private boolean hasWindowsDriver( final String text ) {
        checkNotEmpty( "text", text );
        return WINDOWS_DRIVER.matcher( text ).matches();
    }

    public static GeneralPathImpl newFromFile( final FileSystem fs,
                                               final File file ) {
        checkNotNull( "fs", fs );
        checkNotNull( "file", file );

        return new GeneralPathImpl( fs, file );
    }

    public static GeneralPathImpl create( final FileSystem fs,
                                          final String path,
                                          boolean isRealPath ) {
        return create( fs, path, isRealPath, false );
    }

    public static GeneralPathImpl createRoot( final FileSystem fs,
                                              final String path,
                                              boolean isRealPath ) {
        return new GeneralPathImpl( fs, path, true, isRealPath, true );
    }

    public static GeneralPathImpl create( final FileSystem fs,
                                          final String path,
                                          boolean isRealPath,
                                          boolean isNormalized ) {
        checkNotNull( "fs", fs );
        checkNotNull( "path", path );

        return new GeneralPathImpl( fs, path, false, isRealPath, isNormalized );
    }

    @Override
    protected Path newRoot( FileSystem fs,
                            String substring,
                            String host,
                            boolean realPath ) {
        return new GeneralPathImpl( fs, substring, true, realPath, true );
    }

    @Override
    protected Path newPath( final FileSystem fs,
                            final String substring,
                            String host,
                            final boolean isRealPath,
                            final boolean isNormalized ) {
        return new GeneralPathImpl( fs, substring, false, isRealPath, isNormalized );
    }

    @Override
    public File toFile()
            throws UnsupportedOperationException {
        if ( file == null ) {
            synchronized ( this ) {
                file = new File( toString() );
            }
        }
        return file;
    }

    @Override
    public String getSegmentId() {
        return "/";
    }
}
