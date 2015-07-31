/*
 * Copyright 2015 JBoss Inc
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
*/

package org.kie.workbench.common.screens.server.management.backend;

import java.math.BigInteger;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;
import javax.inject.Named;

import com.thoughtworks.xstream.XStream;
import org.kie.server.api.model.KieContainerResource;
import org.kie.server.api.model.KieContainerStatus;
import org.kie.server.api.model.KieScannerResource;
import org.kie.server.api.model.KieScannerStatus;
import org.kie.server.api.model.ReleaseId;
import org.kie.server.controller.api.model.KieServerInstance;
import org.kie.server.controller.api.model.KieServerInstanceInfo;
import org.kie.server.controller.api.model.KieServerSetup;
import org.kie.server.controller.api.model.KieServerStatus;
import org.kie.server.controller.api.storage.KieServerControllerStorage;
import org.kie.workbench.common.screens.server.management.model.ContainerRef;
import org.kie.workbench.common.screens.server.management.model.ContainerStatus;
import org.kie.workbench.common.screens.server.management.model.ScannerStatus;
import org.kie.workbench.common.screens.server.management.model.ServerRef;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.uberfire.io.IOService;
import org.uberfire.java.nio.file.FileSystem;
import org.uberfire.java.nio.file.NotDirectoryException;
import org.uberfire.java.nio.file.Path;

@ApplicationScoped
public class VFSKieServerControllerStorage implements KieServerControllerStorage {

    private static final Logger logger = LoggerFactory.getLogger(VFSKieServerControllerStorage.class);

    private IOService ioService;
    private FileSystem fileSystem;

    private final XStream xs = new XStream();

    //enable proxy
    public VFSKieServerControllerStorage() {
    }

    @Inject
    public VFSKieServerControllerStorage( @Named("configIO") final IOService ioService,
            @Named("systemFS") final FileSystem fileSystem ) {
        this.ioService = ioService;
        this.fileSystem = fileSystem;
    }

    @Override
    public KieServerInstance store(KieServerInstance kieServerInstance) {
        final Path path = buildPath( kieServerInstance );
        if (!ioService.exists(path)) {
            try {
                ioService.startBatch(path.getFileSystem());
                ioService.write(path, xs.toXML(kieServerInstance));
            } finally {
                ioService.endBatch();
            }

            return kieServerInstance;
        } else {
            throw new IllegalArgumentException("KieServerInstance with id " + kieServerInstance.getIdentifier() + " is already stored");
        }

    }

    @Override
    public List<KieServerInstance> load() {
        final List<KieServerInstance> result = new ArrayList<KieServerInstance>();
        final Path dir = buildPath( (String) null );

        try {
            ioService.startBatch( dir.getFileSystem() );
            for ( final Path registeredServer : ioService.newDirectoryStream( dir ) ) {
                try {
                    migrate(registeredServer);
                    result.add( readKieServerInstance(registeredServer) );
                } catch ( final Exception ignore ) {
                    ioService.delete( registeredServer );
                }
            }
            return result;
        } catch ( final NotDirectoryException ignore ) {
            return result;
        } finally {
            ioService.endBatch();
        }
    }

    @Override
    public KieServerInstance load(String identifier) {
        final Path path = buildPath( identifier );
        migrate(path);
        KieServerInstance serverInstance = readKieServerInstance( path );

        return serverInstance;
    }

    @Override
    public KieServerInstance update(KieServerInstance kieServerInstance) {
        final Path path = buildPath( kieServerInstance );
        try {
            ioService.startBatch(path.getFileSystem());
            ioService.write(path, xs.toXML(kieServerInstance));
        } finally {
            ioService.endBatch();
        }

        return kieServerInstance;
    }

    @Override
    public KieServerInstance delete(String identifier) {
        final Path path = buildPath( identifier );

        KieServerInstance serverInstance = null;
        try {
            ioService.startBatch( path.getFileSystem() );
            serverInstance = readKieServerInstance( path );
            ioService.delete( path );
        } finally {
            ioService.endBatch();
        }

        return serverInstance;
    }

    // utility methods

    private KieServerInstance readKieServerInstance( final Path registeredServer ) {
        try {
            if (ioService.exists(registeredServer)) {
                final KieServerInstance serverInstance = (KieServerInstance) xs.fromXML(ioService.readAllString(registeredServer));
                return serverInstance;
            }
        } catch ( Exception ex ) {
            logger.error("Error reading KieServerInstance definition from path {}", registeredServer, ex);
        }
        return null;
    }

    Path buildPath( final KieServerInstance kieServerInstance ) {
        if ( kieServerInstance == null ) {
            return buildPath( (String) null );
        }
        return buildPath( kieServerInstance.getIdentifier() );
    }

    Path buildPath( final String identifier ) {
        if ( identifier != null ) {
            return fileSystem.getPath( "servers", "remote", toHex( identifier ) + "-info.xml" );
        } else {
            return fileSystem.getPath( "servers", "remote" );
        }
    }

    public String toHex( final String arg ) {
        if ( isHex( arg ) ) {
            return arg;
        }
        return String.format( "%x", new BigInteger( 1, arg.toLowerCase().getBytes( Charset.forName("UTF-8") ) ) );
    }

    private boolean isHex( final String endpoint ) {
        try {
            new BigInteger( endpoint, 16 );
            return true;
        } catch ( NumberFormatException ex ) {
            return false;
        }
    }

    protected void migrate(Path path) {
        if (!path.toString().endsWith("-info.xml")) {
            try {
                final ServerRef serverRef = (ServerRef) xs.fromXML(ioService.readAllString(path));


                KieServerInstance kieServerInstance = new KieServerInstance();
                kieServerInstance.setIdentifier(serverRef.getId());
                kieServerInstance.setVersion(serverRef.getProperties().get("version"));
                kieServerInstance.setName(serverRef.getName());
                kieServerInstance.setStatus(serverRef.getStatus().equals(ContainerStatus.STARTED)? KieServerStatus.UP:KieServerStatus.DOWN);

                KieServerSetup kieServerSetup = new KieServerSetup();
                Set<KieContainerResource> containerResources = new HashSet<KieContainerResource>();

                Collection<ContainerRef> containersRef = serverRef.getContainersRef();
                if (containersRef != null) {
                    for (ContainerRef containerRef : containersRef) {

                        KieContainerResource containerResource = new KieContainerResource();
                        containerResource.setContainerId(containerRef.getId());
                        containerResource.setReleaseId(new ReleaseId(containerRef.getReleasedId().getGroupId(), containerRef.getReleasedId().getArtifactId(), containerRef.getReleasedId().getVersion()));
                        containerResource.setStatus(serverRef.getStatus().equals(ContainerStatus.STARTED)? KieContainerStatus.STARTED:KieContainerStatus.STOPPED);

                        Long scannerInterval = containerRef.getPollInterval();
                        ScannerStatus scannerStatus = containerRef.getScannerStatus();

                        if (scannerInterval != null && scannerStatus != null) {
                            KieScannerResource scannerResource = new KieScannerResource();
                            scannerResource.setPollInterval(scannerInterval);
                            scannerResource.setStatus(KieScannerStatus.valueOf(scannerStatus.toString()));

                            containerResource.setScanner(scannerResource);
                        }

                        containerResources.add(containerResource);
                    }
                }

                kieServerSetup.setContainers(containerResources);


                KieServerInstanceInfo instanceInfo = new KieServerInstanceInfo();
                instanceInfo.setLocation(serverRef.getUrl());
                instanceInfo.setStatus(serverRef.getStatus().equals(ContainerStatus.STARTED)? KieServerStatus.UP:KieServerStatus.DOWN);

                kieServerInstance.getManagedInstances().add(instanceInfo);
                // store migrated information
                store(kieServerInstance);

                // delete old to do not attempt second time migration
                try {
                    ioService.startBatch( path.getFileSystem() );
                    ioService.delete( path );
                } finally {
                    ioService.endBatch();
                }

            } catch (Exception ex) {
                logger.error("Error wile migrating old version of kie server ref from path {}", path, ex);
            }
        }

    }
}
