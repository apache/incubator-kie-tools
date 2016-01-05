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

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.jboss.errai.common.client.api.annotations.Portable;

import static org.uberfire.commons.validation.PortablePreconditions.*;
import static org.uberfire.backend.vfs.PathFactory.*;

/**
 *
 */
public final class FileSystemFactory {

    private FileSystemFactory() {

    }

    public static FileSystem newFS( final Map<String, String> roots,
                                    final Set<String> supportedViews ) {
        return new FileSystemImpl( roots, supportedViews );
    }

    @Portable
    public static class FileSystemImpl implements FileSystem {

        private List<Path>  rootDirectories = null;
        private Set<String> supportedViews  = null;

        public FileSystemImpl() {
        }

        public FileSystemImpl( final Map<String, String> roots,
                               final Set<String> supportedViews ) {
            checkNotNull( "roots", roots );

            this.rootDirectories = new ArrayList<Path>( roots.size() );

            for ( final Map.Entry<String, String> entry : roots.entrySet() ) {
                this.rootDirectories.add( newPath( entry.getValue(), entry.getKey() ) );
            }
            this.supportedViews = new HashSet<String>( checkNotNull( "supportedViews", supportedViews ) );
        }

        @Override
        public List<Path> getRootDirectories() {
            return rootDirectories;
        }

        @Override
        public Set<String> supportedFileAttributeViews() {
            return supportedViews;
        }

        @Override
        public String toString() {
            final StringBuilder sb = new StringBuilder();
            if ( rootDirectories != null ) {
                for ( final Path rootDirectory : rootDirectories ) {
                    sb.append( rootDirectory.toString() );
                }
            }
            return sb.toString();
        }
    }

}
