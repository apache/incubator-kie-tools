/*
 * Copyright 2016 Red Hat, Inc. and/or its affiliates.
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

package org.kie.workbench.common.screens.server.management.backend.storage;

import java.math.BigInteger;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.List;

import javax.annotation.PostConstruct;
import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;
import javax.inject.Named;

import com.thoughtworks.xstream.XStream;
import org.kie.server.controller.api.model.spec.ServerTemplate;
import org.kie.server.controller.api.model.spec.ServerTemplateKey;
import org.kie.server.controller.api.storage.KieServerTemplateStorage;
import org.kie.soup.commons.xstream.XStreamUtils;
import org.kie.workbench.common.screens.server.management.backend.storage.migration.ServerTemplateMigration;
import org.kie.workbench.common.screens.server.management.backend.utils.EmbeddedController;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.uberfire.io.IOService;
import org.uberfire.java.nio.file.FileSystem;
import org.uberfire.java.nio.file.NotDirectoryException;
import org.uberfire.java.nio.file.Path;

@ApplicationScoped
@EmbeddedController
public class ServerTemplateVFSStorage implements KieServerTemplateStorage {

    private static final Logger logger = LoggerFactory.getLogger(ServerTemplateVFSStorage.class);

    private IOService ioService;
    private FileSystem fileSystem;

    private XStream xs;

    //enable proxy
    public ServerTemplateVFSStorage() {
        xs = XStreamUtils.createTrustingXStream();
    }

    @Inject
    public ServerTemplateVFSStorage( @Named("configIO") final IOService ioService, @Named("systemFS") final FileSystem fileSystem ) {
        this();
        this.ioService = ioService;
        this.fileSystem = fileSystem;
    }

    @PostConstruct
    public void init() {
        ServerTemplateMigration.migrate(buildPath(null), ioService, xs, this);
    }

    @Override
    public ServerTemplate store( final ServerTemplate serverTemplate ) {
        logger.debug("About to store server template {}", serverTemplate);
        final Path path = buildPath( serverTemplate.getId() );
        if (!ioService.exists(path)) {
            try {
                ioService.startBatch(path.getFileSystem());
                ioService.write(path, xs.toXML(serverTemplate));
            } finally {
                ioService.endBatch();
            }
            logger.debug("Server template {} stored successfully", serverTemplate.getId());
            return serverTemplate;
        } else {
            throw new IllegalArgumentException("Server template with id " + serverTemplate.getId() + " is already stored");
        }
    }

    @Override
    public List<ServerTemplateKey> loadKeys() {
        logger.debug("About to load all available server templates (as keys only)...");
        final List<ServerTemplateKey> result = new ArrayList<ServerTemplateKey>();
        final Path dir = buildPath( null );

        try {
            ioService.startBatch( dir.getFileSystem() );
            for ( final Path registeredServer : ioService.newDirectoryStream( dir ) ) {
                try {
                    ServerTemplate serverTemplate = readServerTemplate(registeredServer);
                    logger.debug("Found server template {}, taking its short key version...");
                    result.add( new ServerTemplateKey(serverTemplate.getId(), serverTemplate.getName()) );
                } catch ( final Exception ignore ) {
                    ioService.delete( registeredServer );
                }
            }
            logger.debug("All found server template keys {}", result);
            return result;
        } catch ( final NotDirectoryException ignore ) {
            logger.debug("No directory found {}, returning empty result", dir);
            return result;
        } finally {
            ioService.endBatch();
        }
    }

    @Override
    public List<ServerTemplate> load() {
        logger.debug("About to load all available server templates...");
        final List<ServerTemplate> result = new ArrayList<ServerTemplate>();
        final Path dir = buildPath( null );

        try {
            ioService.startBatch( dir.getFileSystem() );
            for ( final Path registeredServer : ioService.newDirectoryStream( dir ) ) {
                try {
                    ServerTemplate serverTemplate = readServerTemplate(registeredServer);
                    logger.debug("Found server template {}", serverTemplate);
                    result.add( serverTemplate );
                } catch ( final Exception ignore ) {
                    ioService.delete( registeredServer );
                }
            }
            logger.debug("All found server templates {}", result);
            return result;
        } catch ( final NotDirectoryException ignore ) {
            logger.debug("No directory found {}, returning empty result", dir);
            return result;
        } finally {
            ioService.endBatch();
        }
    }

    @Override
    public ServerTemplate load( final String identifier ) {
        logger.debug("About to load server template for {}", identifier);
        final Path path = buildPath( identifier );

        ServerTemplate serverTemplate = readServerTemplate(path);
        logger.debug("Server template loaded {}", serverTemplate);
        return serverTemplate;
    }

    @Override
    public boolean exists( final String identifier ) {

        boolean serverTemplateExists = ioService.exists( buildPath( identifier));
        logger.debug("Server with id {} exists = {}", identifier, serverTemplateExists);
        return serverTemplateExists;
    }

    @Override
    public ServerTemplate update( final ServerTemplate serverTemplate ) {
        logger.debug("About to update server template {}", serverTemplate);
        final Path path = buildPath( serverTemplate.getId() );
        try {
            ioService.startBatch(path.getFileSystem());
            ioService.write(path, xs.toXML(serverTemplate));
        } finally {
            ioService.endBatch();
        }
        logger.debug("Server template {} updated successfully", serverTemplate);
        return serverTemplate;
    }

    @Override
    public ServerTemplate delete( final String identifier ) {
        logger.debug("About to remove server template with id {}", identifier);
        final Path path = buildPath( identifier );

        ServerTemplate serverTemplate = null;
        try {
            ioService.startBatch( path.getFileSystem() );
            serverTemplate = readServerTemplate(path);
            ioService.delete( path );
        } finally {
            ioService.endBatch();
        }
        logger.debug("Server template with id {}, removed successfully", identifier);
        return serverTemplate;
    }

    /*
     * helper methods
     */

    protected ServerTemplate readServerTemplate(final Path registeredServer) {
        try {
            if (ioService.exists(registeredServer)) {
                final ServerTemplate serverTemplate = (ServerTemplate) xs.fromXML(ioService.readAllString(registeredServer));
                return serverTemplate;
            }
        } catch ( Exception ex ) {
            logger.error("Error reading KieServerInstance definition from path {}", registeredServer, ex);
        }
        return null;
    }

    protected Path buildPath( final String identifier ) {
        if ( identifier != null ) {
            return fileSystem.getPath( "servers", "remote", toHex( identifier ) + "-template.xml" );
        } else {
            return fileSystem.getPath( "servers", "remote" );
        }
    }

    protected String toHex( final String arg ) {
        if ( isHex( arg ) ) {
            return arg;
        }
        return String.format( "%x", new BigInteger( 1, arg.toLowerCase().getBytes( Charset.forName("UTF-8") ) ) );
    }

    protected boolean isHex( final String endpoint ) {
        try {
            new BigInteger( endpoint, 16 );
            return true;
        } catch ( NumberFormatException ex ) {
            return false;
        }
    }
}
