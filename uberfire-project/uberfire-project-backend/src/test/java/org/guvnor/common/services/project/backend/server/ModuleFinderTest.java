/*
 * Copyright 2017 Red Hat, Inc. and/or its affiliates.
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
package org.guvnor.common.services.project.backend.server;

import java.net.URISyntaxException;
import java.util.Set;
import javax.inject.Inject;
import javax.inject.Named;

import org.guvnor.common.services.project.model.GAV;
import org.guvnor.common.services.project.model.Module;
import org.guvnor.common.services.project.model.POM;
import org.guvnor.common.services.project.service.POMService;
import org.guvnor.structure.repositories.Branch;
import org.guvnor.test.WeldJUnitRunner;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.uberfire.backend.server.util.Paths;
import org.uberfire.io.IOService;
import org.uberfire.java.nio.base.NotImplementedException;
import org.uberfire.java.nio.file.Path;

import static org.junit.Assert.*;

@RunWith(WeldJUnitRunner.class)
public class ModuleFinderTest {

    @Inject
    @Named("ioStrategy")
    IOService ioService;

    @Inject
    POMService pomService;

    private ModuleFinder finder;

    @Before
    public void setUp() throws Exception {

        finder = new ModuleFinder(ioService,
                                  pomService);
    }

    private ResourceResolver getResourceResolver() throws URISyntaxException {

        final Path project1FolderPath = ioService.get(this.getClass().getResource("/LegacyRepositoryStructure/Project1/").toURI());
        final Path project1FilePath = ioService.get(this.getClass().getResource("/LegacyRepositoryStructure/Project1/pom.xml").toURI());

        final Path project2FolderPath = ioService.get(this.getClass().getResource("/LegacyRepositoryStructure/Project2/").toURI());
        final Path project2FilePath = ioService.get(this.getClass().getResource("/LegacyRepositoryStructure/Project2/pom.xml").toURI());

        return new ResourceResolver() {
            @Override
            public Module resolveModule(org.uberfire.backend.vfs.Path resource, boolean loadPOM) {

                final String uri = resource.toURI();

                if (uri.endsWith("Project1/pom.xml")) {
                    return new Module(Paths.convert(project1FolderPath),
                                      Paths.convert(project1FilePath),
                                      new POM(new GAV("test",
                                                      "project1",
                                                      "1.0")));
                } else if (uri.endsWith("Project2/pom.xml")) {
                    return new Module(Paths.convert(project2FolderPath),
                                      Paths.convert(project2FilePath),
                                      new POM(new GAV("test",
                                                      "project2",
                                                      "1.0")));
                } else {
                    return null;
                }
            }

            @Override
            public Module simpleModuleInstance(Path nioModuleRootPath) {
                throw new NotImplementedException();
            }
        };
    }

    @Test
    public void pomIsInPathRoot() throws Exception {
        final Path folderPath = ioService.get(this.getClass().getResource("/LegacyRepositoryStructure/Project1/").toURI());

        final Set<Module> modules = finder.find(getResourceResolver(),
                                                new Branch("master",
                                                           Paths.convert(folderPath)));

        assertFalse(modules.isEmpty());
    }

    @Test
    public void modulesAreInFolders() throws Exception {
        final Path folderPath = ioService.get(this.getClass().getResource("/LegacyRepositoryStructure/").toURI());

        final Set<Module> modules = finder.find(getResourceResolver(),
                                                new Branch("master",
                                                           Paths.convert(folderPath)));

        assertEquals(2,
                     modules.size());
    }
}