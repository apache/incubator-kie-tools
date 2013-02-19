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

import java.util.Iterator;
import javax.annotation.PostConstruct;
import javax.enterprise.context.ApplicationScoped;
import javax.enterprise.context.SessionScoped;
import javax.inject.Inject;
import javax.inject.Named;

import org.jboss.errai.bus.server.annotations.Service;
import org.kie.commons.io.IOService;
import org.kie.commons.java.nio.file.FileSystem;
import org.kie.commons.java.nio.file.Path;
import org.uberfire.client.workbench.services.UserServices;
import org.uberfire.security.Identity;

import static org.kie.commons.io.FileSystemType.Bootstrap.*;

/**
 * Workbench services
 */
@Service
@ApplicationScoped
public class UserServicesImpl
        implements
        UserServices {

    @Inject
    @Named("ioStrategy")
    private IOService ioService;

    @Inject
    @SessionScoped
    private Identity identity;

    private Path bootstrapRoot = null;

    @PostConstruct
    public void init() {
        final Iterator<FileSystem> fsIterator = ioService.getFileSystems( BOOTSTRAP_INSTANCE ).iterator();
        if ( fsIterator.hasNext() ) {
            final FileSystem bootstrap = fsIterator.next();
            final Iterator<Path> rootIterator = bootstrap.getRootDirectories().iterator();
            if ( rootIterator.hasNext() ) {
                this.bootstrapRoot = rootIterator.next();
            }
        }
    }

    @Override
    public Path buildPath( final String serviceType,
                           final String relativePath ) {
        if ( relativePath != null && !"".equals( relativePath ) ) {
            return bootstrapRoot.resolve( "/.metadata/.users/" + identity.getName() + "/." + serviceType + "/." + relativePath );
        } else {
            return bootstrapRoot.resolve( "/.metadata/.users/" + identity.getName() + "/." + serviceType );
        }
    }
}
