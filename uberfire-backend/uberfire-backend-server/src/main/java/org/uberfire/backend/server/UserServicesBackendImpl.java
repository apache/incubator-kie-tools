/*
 * Copyright 2012 JBoss Inc
 * 
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not
 * use this file except in compliance with the License. You may obtain a copy of
 * the License at
 * 
 * http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS, WITHOUT
 * WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the
 * License for the specific language governing permissions and limitations under
 * the License.
 */
package org.uberfire.backend.server;

import java.util.ArrayList;
import java.util.Collection;
import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;
import javax.inject.Named;

import org.uberfire.backend.server.io.SystemFS;
import org.uberfire.backend.server.util.TextUtil;
import org.uberfire.io.IOService;
import org.uberfire.java.nio.file.FileSystem;
import org.uberfire.java.nio.file.Path;

@ApplicationScoped
public class UserServicesBackendImpl {

    @Inject
    @Named("configIO")
    private IOService ioService;

    @Inject
    @SystemFS
    private FileSystem fileSystem;

    public Path buildPath( final String _userName,
                           final String serviceType,
                           final String relativePath ) {

        final String resultUserName = TextUtil.normalizeUserName( _userName );

        if ( relativePath != null && !"".equals( relativePath ) ) {
            return fileSystem.getPath( resultUserName + "-uf-user", serviceType, relativePath );
        } else {
            return fileSystem.getPath( resultUserName + "-uf-user", serviceType );
        }
    }

    public Collection<Path> getAllUsersData( final String serviceType,
                                             final String relativePath ) {
        final Collection<Path> result = new ArrayList<Path>();

        for ( final Path path : fileSystem.getRootDirectories() ) {
            final Path _path;
            if ( relativePath != null && !"".equals( relativePath ) ) {
                _path = path.resolve( serviceType ).resolve( relativePath );
            } else {
                _path = path.resolve( serviceType );
            }
            if ( ioService.exists( _path ) ) {
                result.add( _path );
            }
        }

        return result;
    }

}
