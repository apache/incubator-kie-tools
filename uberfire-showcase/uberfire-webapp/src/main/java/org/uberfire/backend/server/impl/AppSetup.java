/*
 * Copyright 2015 JBoss, by Red Hat, Inc
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

package org.uberfire.backend.server.impl;

import java.net.URI;
import java.util.HashMap;
import javax.annotation.PostConstruct;
import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;
import javax.inject.Named;

import org.uberfire.commons.services.cdi.Startup;
import org.uberfire.io.IOService;
import org.uberfire.java.nio.file.FileSystemAlreadyExistsException;

@ApplicationScoped
@Startup
public class AppSetup {

    private static final String PLAYGROUND_ORIGIN = "https://github.com/guvnorngtestuser1/guvnorng-playground.git";
    private static final String PLAYGROUND_UID = "guvnorngtestuser1";

    @Inject
    @Named("ioStrategy")
    private IOService ioService;

    @PostConstruct
    public void assertPlayground() {
        try {
            ioService.newFileSystem( URI.create( "default://uf-playground" ), new HashMap<String, Object>() {{
                put( "origin", PLAYGROUND_ORIGIN );
                put( "username", PLAYGROUND_UID );
            }} );
        } catch ( final FileSystemAlreadyExistsException ignore ) {

        }
    }

}
