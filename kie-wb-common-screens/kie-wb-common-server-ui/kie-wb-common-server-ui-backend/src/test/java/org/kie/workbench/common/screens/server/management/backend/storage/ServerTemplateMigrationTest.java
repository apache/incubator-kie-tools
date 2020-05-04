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

import java.io.IOException;
import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

import com.thoughtworks.xstream.XStream;
import org.apache.commons.io.IOUtils;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.kie.server.api.model.KieContainerStatus;
import org.kie.server.api.model.ReleaseId;
import org.kie.server.controller.api.model.runtime.ServerInstanceKey;
import org.kie.server.controller.api.model.spec.ContainerSpec;
import org.kie.server.controller.api.model.spec.ServerTemplate;
import org.kie.soup.xstream.XStreamUtils;
import org.kie.workbench.common.screens.server.management.backend.storage.migration.ServerTemplateMigration;
import org.uberfire.io.IOService;
import org.uberfire.java.nio.file.FileSystem;
import org.uberfire.java.nio.file.Path;
import org.uberfire.mocks.FileSystemTestingUtils;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

public class ServerTemplateMigrationTest {

    private FileSystemTestingUtils fileSystemTestingUtils = new FileSystemTestingUtils();
    private ServerTemplateVFSStorage templateStorage;

    private IOService ioService;
    private FileSystem fileSystem;
    private XStream xstream;

    @Before
    public void setup() throws IOException {
        xstream = XStreamUtils.createTrustingXStream();

        fileSystemTestingUtils.setup();

        ioService = fileSystemTestingUtils.getIoService();
        fileSystem = fileSystemTestingUtils.getFileSystem();

        templateStorage = new ServerTemplateVFSStorage(ioService, fileSystem);
    }

    @After
    public void cleanup() {
        fileSystemTestingUtils.cleanup();
    }

    @Test
    public void testMigrationOfOldServerTemplate() throws Exception {
        String serverTemplateId = "kie_server";
        String oldServerTemplateContent = IOUtils.toString(this.getClass().getResourceAsStream("/kie-server-6.3-info.xml"));
        assertNotNull(oldServerTemplateContent);

        Path path = buildPath(serverTemplateId);
        assertNotNull(path);
        // let's store it in the old way -info.xml file
        ioService.write(path, oldServerTemplateContent);

        ServerTemplateMigration templateMigration = new ServerTemplateMigration();

        templateMigration.migrate(path.getParent(), ioService, xstream, templateStorage);

        boolean exists = templateStorage.exists(serverTemplateId);
        assertTrue(exists);

        ServerTemplate fromStorage = templateStorage.load(serverTemplateId);
        assertNotNull(fromStorage);
        // verify server template
        assertEquals(serverTemplateId, fromStorage.getId());
        assertEquals("kie server name", fromStorage.getName());
        Collection<String> capabilities = fromStorage.getCapabilities();
        assertNotNull(capabilities);
        assertTrue(capabilities.contains("KieServer"));
        assertTrue(capabilities.contains("BRM"));
        assertTrue(capabilities.contains("BPM"));
        // verify server instances (previously known as managedInstances)
        Collection<ServerInstanceKey> instances = fromStorage.getServerInstanceKeys();
        assertNotNull(instances);
        assertEquals(1, instances.size());

        ServerInstanceKey serverInstanceKey = instances.iterator().next();
        assertNotNull(serverInstanceKey);
        assertEquals(serverTemplateId, serverInstanceKey.getServerTemplateId());
        assertEquals("kie_server@localhost:8180", serverInstanceKey.getServerName());
        assertEquals("kie_server@localhost:8180", serverInstanceKey.getServerInstanceId());
        assertEquals("http://localhost:8180/kie-server/services/rest/server", serverInstanceKey.getUrl());

        // verify containers
        Collection<ContainerSpec> containerSpecs = fromStorage.getContainersSpec();
        assertNotNull(containerSpecs);
        assertEquals(3, containerSpecs.size());

        Map<String, ContainerSpec> containersById = mapContainers(containerSpecs);

        assertTrue(containersById.containsKey("project-1"));
        assertTrue(containersById.containsKey("project-2"));
        assertTrue(containersById.containsKey("project-3"));


        // first container spec...
        ContainerSpec spec = containersById.get("project-2");
        assertNotNull(spec);

        assertEquals("project-2", spec.getId());
        assertEquals(new ReleaseId("org.kie.server", "project-2", "1.0.0"), spec.getReleasedId());
        assertEquals(serverTemplateId, spec.getServerTemplateKey().getId());
        assertEquals("kie server name", spec.getServerTemplateKey().getName());
        assertEquals(KieContainerStatus.STARTED, spec.getStatus());
        assertEquals(0, spec.getConfigs().size());

        // second container spec
        spec = containersById.get("project-3");
        assertNotNull(spec);

        assertEquals("project-3", spec.getId());
        assertEquals(new ReleaseId("org.kie.server", "project-3", "1.0.0"), spec.getReleasedId());
        assertEquals(serverTemplateId, spec.getServerTemplateKey().getId());
        assertEquals("kie server name", spec.getServerTemplateKey().getName());
        assertEquals(KieContainerStatus.STOPPED, spec.getStatus());
        assertEquals(0, spec.getConfigs().size());

        // third container spec
        spec = containersById.get("project-1");
        assertNotNull(spec);

        assertEquals("project-1", spec.getId());
        assertEquals(new ReleaseId("org.kie.server", "project-1", "1.0.0"), spec.getReleasedId());
        assertEquals(serverTemplateId, spec.getServerTemplateKey().getId());
        assertEquals("kie server name", spec.getServerTemplateKey().getName());
        assertEquals(KieContainerStatus.STARTED, spec.getStatus());
        assertEquals(0, spec.getConfigs().size());
    }

    @Test
    public void testMigrationOfServerTemplateOnly() throws Exception {
        String serverTemplateId = "kie_server";
        String oldServerTemplateContent = IOUtils.toString(this.getClass().getResourceAsStream("/kie-server-6.3-info-just-server.xml"));
        assertNotNull(oldServerTemplateContent);

        Path path = buildPath(serverTemplateId);
        assertNotNull(path);
        // let's store it in the old way -info.xml file
        ioService.write(path, oldServerTemplateContent);

        ServerTemplateMigration templateMigration = new ServerTemplateMigration();

        templateMigration.migrate(path.getParent(), ioService, xstream, templateStorage);

        boolean exists = templateStorage.exists(serverTemplateId);
        assertTrue(exists);

        ServerTemplate fromStorage = templateStorage.load(serverTemplateId);
        assertNotNull(fromStorage);
        // verify server template
        assertEquals(serverTemplateId, fromStorage.getId());
        assertEquals("kie server name", fromStorage.getName());
        Collection<String> capabilities = fromStorage.getCapabilities();
        assertNotNull(capabilities);
        assertEquals(0, capabilities.size());
        // verify server instances (previously known as managedInstances)
        Collection<ServerInstanceKey> instances = fromStorage.getServerInstanceKeys();
        assertNotNull(instances);
        assertEquals(0, instances.size());

        // verify containers
        Collection<ContainerSpec> containerSpecs = fromStorage.getContainersSpec();
        assertNotNull(containerSpecs);
        assertEquals(0, containerSpecs.size());

    }

    @Test
    public void testMigrationOfOldServerTemplateWithContainers() throws Exception {
        String serverTemplateId = "kie_server";
        String oldServerTemplateContent = IOUtils.toString(this.getClass().getResourceAsStream("/kie-server-6.3-info-with-containers.xml"));
        assertNotNull(oldServerTemplateContent);

        Path path = buildPath(serverTemplateId);
        assertNotNull(path);
        // let's store it in the old way -info.xml file
        ioService.write(path, oldServerTemplateContent);

        ServerTemplateMigration templateMigration = new ServerTemplateMigration();

        templateMigration.migrate(path.getParent(), ioService, xstream, templateStorage);

        boolean exists = templateStorage.exists(serverTemplateId);
        assertTrue(exists);

        ServerTemplate fromStorage = templateStorage.load(serverTemplateId);
        assertNotNull(fromStorage);
        // verify server template
        assertEquals(serverTemplateId, fromStorage.getId());
        assertEquals("kie server name", fromStorage.getName());
        Collection<String> capabilities = fromStorage.getCapabilities();
        assertNotNull(capabilities);
        assertEquals(0, capabilities.size());
        // verify server instances (previously known as managedInstances)
        Collection<ServerInstanceKey> instances = fromStorage.getServerInstanceKeys();
        assertNotNull(instances);
        assertEquals(0, instances.size());

        // verify containers
        Collection<ContainerSpec> containerSpecs = fromStorage.getContainersSpec();
        assertNotNull(containerSpecs);
        assertEquals(1, containerSpecs.size());

        Iterator<ContainerSpec> iterator = containerSpecs.iterator();

        // first container spec...
        ContainerSpec spec = iterator.next();
        assertNotNull(spec);

        assertEquals("project-1", spec.getId());
        assertEquals(new ReleaseId("org.kie.server", "project-1", "1.0.0"), spec.getReleasedId());
        assertEquals(serverTemplateId, spec.getServerTemplateKey().getId());
        assertEquals("kie server name", spec.getServerTemplateKey().getName());
        assertEquals(KieContainerStatus.STARTED, spec.getStatus());
        assertEquals(0, spec.getConfigs().size());
    }

    @Test
    public void testMigrationOfOldServerTemplateWithInstances() throws Exception {
        String serverTemplateId = "kie_server";
        String oldServerTemplateContent = IOUtils.toString(this.getClass().getResourceAsStream("/kie-server-6.3-info-with-instances.xml"));
        assertNotNull(oldServerTemplateContent);

        Path path = buildPath(serverTemplateId);
        assertNotNull(path);
        // let's store it in the old way -info.xml file
        ioService.write(path, oldServerTemplateContent);

        ServerTemplateMigration templateMigration = new ServerTemplateMigration();

        templateMigration.migrate(path.getParent(), ioService, xstream, templateStorage);

        boolean exists = templateStorage.exists(serverTemplateId);
        assertTrue(exists);

        ServerTemplate fromStorage = templateStorage.load(serverTemplateId);
        assertNotNull(fromStorage);
        // verify server template
        assertEquals(serverTemplateId, fromStorage.getId());
        assertEquals("kie server name", fromStorage.getName());
        Collection<String> capabilities = fromStorage.getCapabilities();
        assertNotNull(capabilities);
        assertTrue(capabilities.contains("KieServer"));
        assertTrue(capabilities.contains("BRM"));
        assertTrue(capabilities.contains("BPM"));
        // verify server instances (previously known as managedInstances)
        Collection<ServerInstanceKey> instances = fromStorage.getServerInstanceKeys();
        assertNotNull(instances);
        assertEquals(1, instances.size());

        ServerInstanceKey serverInstanceKey = instances.iterator().next();
        assertNotNull(serverInstanceKey);
        assertEquals(serverTemplateId, serverInstanceKey.getServerTemplateId());
        assertEquals("kie_server@localhost:8180", serverInstanceKey.getServerName());
        assertEquals("kie_server@localhost:8180", serverInstanceKey.getServerInstanceId());
        assertEquals("http://localhost:8180/kie-server/services/rest/server", serverInstanceKey.getUrl());

        // verify containers
        Collection<ContainerSpec> containerSpecs = fromStorage.getContainersSpec();
        assertNotNull(containerSpecs);
        assertEquals(0, containerSpecs.size());

    }

    private Path buildPath( final String identifier ) {
        if ( identifier != null ) {
            return fileSystem.getPath( "servers", "remote", templateStorage.toHex(identifier ) + "-info.xml" );
        } else {
            return fileSystem.getPath( "servers", "remote" );
        }
    }

    private Map<String, ContainerSpec> mapContainers(Collection<ContainerSpec> containerSpecs) {
        Map<String, ContainerSpec> containersById = new HashMap<String, ContainerSpec>();
        for (ContainerSpec cs : containerSpecs) {
            containersById.put(cs.getId(), cs);
        }

        return containersById;
    }
}
