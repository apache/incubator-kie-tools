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
