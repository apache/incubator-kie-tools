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

package org.uberfire.backend.vfs;

import java.util.HashMap;
import java.util.Map;

import org.jboss.errai.common.client.api.annotations.Portable;

import static org.uberfire.commons.validation.PortablePreconditions.*;

public final class PathFactory {

    public static String VERSION_PROPERTY = "hasVersionSupport";

    private PathFactory() {
    }

    public static Path newPath( final String fileName,
                                final String uri ) {
        return new PathImpl( checkNotEmpty( "fileName", fileName ), checkNotEmpty( "uri", uri ) );
    }

    public static Path newPathBasedOn( final String fileName,
                                       final String uri,
                                       final Path path ) {
        return new PathImpl( checkNotEmpty( "fileName", fileName ), checkNotEmpty( "uri", uri ), checkNotNull( "path", path ) );
    }

    public static Path newPath( final String fileName,
                                final String uri,
                                final Map<String, Object> attrs ) {
        return new PathImpl( checkNotEmpty( "fileName", fileName ), checkNotEmpty( "uri", uri ), attrs );
    }

    @Portable
    public static class PathImpl implements Path,
                                            IsVersioned {

        private String uri = null;
        private String fileName = null;
        private HashMap<String, Object> attributes = null;
        private boolean hasVersionSupport = false;

        public PathImpl() {
        }

        private PathImpl( final String fileName,
                          final String uri ) {
            this( fileName, uri, (Map<String, Object>) null );
        }

        private PathImpl( final String fileName,
                          final String uri,
                          final Map<String, Object> attrs ) {
            this.fileName = fileName;
            this.uri = uri;
            if ( attrs == null ) {
                this.attributes = new HashMap<String, Object>();
            } else {
                if ( attrs.containsKey( VERSION_PROPERTY ) ) {
                    hasVersionSupport = (Boolean) attrs.remove( VERSION_PROPERTY );
                }
                if ( attrs.size() > 0 ) {
                    this.attributes = new HashMap<String, Object>( attrs );
                } else {
                    this.attributes = new HashMap<String, Object>();
                }
            }
        }

        private PathImpl( final String fileName,
                          final String uri,
                          final Path path ) {
            this.fileName = fileName;
            this.uri = uri;
            if ( path instanceof PathImpl ) {
                this.hasVersionSupport = ( (PathImpl) path ).hasVersionSupport;
            }
        }

        @Override
        public String getFileName() {
            return fileName;
        }

        @Override
        public String toURI() {
            return uri;
        }

        public Map<String, Object> getAttributes() {
            return attributes;
        }

        @Override
        public int compareTo( final Path another ) {
            return this.uri.compareTo( another.toURI() );
        }

        @Override
        public boolean equals( Object o ) {
            if ( this == o ) {
                return true;
            }
            if ( !( o instanceof Path ) ) {
                return false;
            }

            final Path path = (Path) o;

            return uri.equals( path.toURI() );
        }

        @Override
        public boolean hasVersionSupport() {
            return hasVersionSupport;
        }

        @Override
        public int hashCode() {
            return uri.hashCode();
        }

        @Override
        public String toString() {
            return "PathImpl{" +
                    "uri='" + uri + '\'' +
                    ", fileName='" + fileName + '\'' +
                    ", attrs=" + attributes +
                    '}';
        }
    }
}
