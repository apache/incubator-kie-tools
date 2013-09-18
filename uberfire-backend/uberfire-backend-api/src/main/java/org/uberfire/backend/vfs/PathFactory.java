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

import static org.kie.commons.validation.PortablePreconditions.*;

public final class PathFactory {

    private PathFactory() {
    }

    public static Path newPath( final FileSystem fs,
                                final String fileName,
                                final String uri ) {
        return new PathImpl( checkNotNull( "fs", fs ), checkNotEmpty( "fileName", fileName ), checkNotEmpty( "uri", uri ) );
    }

    public static Path newPath( final FileSystem fs,
                                final String fileName,
                                final String uri,
                                final Map<String, Object> attrs ) {
        return new PathImpl( checkNotNull( "fs", fs ), checkNotEmpty( "fileName", fileName ), checkNotEmpty( "uri", uri ), attrs );
    }

    @Portable
    public static class PathImpl implements Path {

        private FileSystem fs = null;
        private String uri = null;
        private String fileName = null;
        private HashMap<String, Object> attributes = null;

        public PathImpl() {
        }

        private PathImpl( final FileSystem fs,
                          final String fileName,
                          final String uri ) {
            this( fs, fileName, uri, null );
        }

        private PathImpl( final FileSystem fs,
                          final String fileName,
                          final String uri,
                          final Map<String, Object> attrs ) {
            this.fs = fs;
            this.fileName = fileName;
            this.uri = uri;
            if ( attrs == null ) {
                this.attributes = new HashMap<String, Object>();
            } else {
                this.attributes = new HashMap<String, Object>( attrs );
            }
        }

        @Override
        public FileSystem getFileSystem() {
            return fs;
        }

        @Override
        public String getFileName() {
            return fileName;
        }
        
        @Override
        public String getFileNameWithoutExtension() {
            return removeExtension(fileName);
        }
        
		public static String removeExtension(final String filename) {
			if (filename == null) {
				return null;
			}
			final int index = indexOfExtension(filename);
			if (index == -1) {
				return filename;
			} else {
				return filename.substring(0, index);
			}
		}

		public static int indexOfExtension(final String filename) {
			if (filename == null) {
				return -1;
			}
			final int extensionPos = filename.lastIndexOf(".");
			return extensionPos;
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
