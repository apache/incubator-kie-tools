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

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import org.uberfire.java.nio.IOException;
import org.uberfire.java.nio.file.Path;
import org.uberfire.java.nio.file.attribute.BasicFileAttributeView;
import org.uberfire.java.nio.file.attribute.BasicFileAttributes;
import org.uberfire.java.nio.file.attribute.FileTime;

import static org.uberfire.commons.validation.Preconditions.*;

public abstract class AbstractBasicFileAttributeView<P extends Path>
        implements BasicFileAttributeView,
                   ExtendedAttributeView {

    static final String IS_REGULAR_FILE    = "isRegularFile";
    static final String IS_DIRECTORY       = "isDirectory";
    static final String IS_SYMBOLIC_LINK   = "isSymbolicLink";
    static final String IS_OTHER           = "isOther";
    static final String SIZE               = "size";
    static final String FILE_KEY           = "fileKey";
    static final String LAST_MODIFIED_TIME = "lastModifiedTime";
    static final String LAST_ACCESS_TIME   = "lastAccessTime";
    static final String CREATION_TIME      = "creationTime";

    private static final Set<String> PROPERTIES = new HashSet<String>() {{
        add( IS_REGULAR_FILE );
        add( IS_DIRECTORY );
        add( IS_SYMBOLIC_LINK );
        add( IS_OTHER );
        add( SIZE );
        add( FILE_KEY );
        add( LAST_MODIFIED_TIME );
        add( LAST_ACCESS_TIME );
        add( CREATION_TIME );
    }};

    protected final P path;

    public AbstractBasicFileAttributeView( final P path ) {
        this.path = checkNotNull( "path", path );
    }

    @Override
    public String name() {
        return "basic";
    }

    @Override
    public void setTimes( final FileTime lastModifiedTime,
                          final FileTime lastAccessTime,
                          final FileTime createTime )
            throws IOException {
        throw new NotImplementedException();
    }

    @Override
    public Map<String, Object> readAllAttributes() {
        return readAttributes( "*" );
    }

    @Override
    public Map<String, Object> readAttributes( final String... attributes ) {
        final BasicFileAttributes attrs = readAttributes();

        return new HashMap<String, Object>() {{
            for ( final String attribute : attributes ) {
                checkNotEmpty( "attribute", attribute );
                if ( attribute.equals( "*" ) || attribute.equals( IS_REGULAR_FILE ) ) {
                    put( IS_REGULAR_FILE, attrs.isRegularFile() );
                }
                if ( attribute.equals( "*" ) || attribute.equals( IS_DIRECTORY ) ) {
                    put( IS_DIRECTORY, attrs.isDirectory() );
                }
                if ( attribute.equals( "*" ) || attribute.equals( IS_SYMBOLIC_LINK ) ) {
                    put( IS_SYMBOLIC_LINK, attrs.isSymbolicLink() );
                }
                if ( attribute.equals( "*" ) || attribute.equals( IS_OTHER ) ) {
                    put( IS_OTHER, attrs.isOther() );
                }
                if ( attribute.equals( "*" ) || attribute.equals( SIZE ) ) {
                    put( SIZE, new Long( attrs.size() ) );
                }
                if ( attribute.equals( "*" ) || attribute.equals( FILE_KEY ) ) {
                    put( FILE_KEY, attrs.fileKey() );
                }
                if ( attribute.equals( "*" ) || attribute.equals( LAST_MODIFIED_TIME ) ) {
                    put( LAST_MODIFIED_TIME, attrs.lastModifiedTime() );
                }
                if ( attribute.equals( "*" ) || attribute.equals( LAST_ACCESS_TIME ) ) {
                    put( LAST_ACCESS_TIME, attrs.lastAccessTime() );
                }
                if ( attribute.equals( "*" ) || attribute.equals( CREATION_TIME ) ) {
                    put( CREATION_TIME, attrs.creationTime() );
                }
                if ( attribute.equals( "*" ) ) {
                    break;
                }
            }
        }};
    }

    @Override
    public void setAttribute( final String attribute,
                              final Object value ) throws IOException {
        checkNotEmpty( "attribute", attribute );
        checkCondition( "invalid attribute", PROPERTIES.contains( attribute ) );

        throw new NotImplementedException();
    }

    @Override
    public boolean isSerializable() {
        return false;
    }

}
