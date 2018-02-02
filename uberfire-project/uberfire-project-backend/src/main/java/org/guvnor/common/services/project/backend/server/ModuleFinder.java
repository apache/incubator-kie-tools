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

import java.util.HashSet;
import java.util.Set;
import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;
import javax.inject.Named;

import org.guvnor.common.services.project.model.Module;
import org.guvnor.common.services.project.service.POMService;
import org.guvnor.structure.repositories.Branch;
import org.uberfire.backend.server.util.Paths;
import org.uberfire.io.IOService;
import org.uberfire.java.nio.file.DirectoryStream;
import org.uberfire.java.nio.file.Files;
import org.uberfire.java.nio.file.Path;

import static org.guvnor.common.services.project.utils.ModuleResourcePaths.POM_PATH;
import static org.kie.soup.commons.validation.PortablePreconditions.checkNotNull;

@ApplicationScoped
public class ModuleFinder {

    private POMService pomService;
    private IOService ioService;

    private ResourceResolver resourceResolver;

    public ModuleFinder() {
    }

    @Inject
    public ModuleFinder(final @Named("ioStrategy") IOService ioService,
                        final POMService pomService) {
        this.ioService = ioService;
        this.pomService = pomService;
    }

    public Set<Module> find(final ResourceResolver resourceResolver,
                            final Branch branch) {

        this.resourceResolver = checkNotNull("resourceResolver",
                                             resourceResolver);

        return new Finder(checkNotNull("branch",
                                       branch)).find();
    }

    private class Finder {

        private final Branch branch;

        private final Set<Module> modules = new HashSet<Module>();

        public Finder(final Branch branch) {
            this.branch = branch;
        }

        public Set<Module> find() {
            if (branch == null) {
                return modules;
            }

            findModule(Paths.convert(branch.getPath()),
                       true);

            return modules;
        }

        private void findModule(final org.uberfire.java.nio.file.Path folderPath,
                                final boolean checkModulesFromFolders) {
            final org.uberfire.java.nio.file.Path pomPath = folderPath.resolve(POM_PATH);

            if (Files.exists(pomPath)) {
                final Module module = resourceResolver.resolveModule(Paths.convert(pomPath));
                if (module != null) {
                    addModule(module);
                } else if (checkModulesFromFolders) {
                    lookForModulesFromFolders(folderPath);
                }
            } else if (checkModulesFromFolders) {
                lookForModulesFromFolders(folderPath);
            }
        }

        private void lookForModulesFromFolders(final Path folderPath) {
            final DirectoryStream<Path> nioRepositoryPaths = ioService.newDirectoryStream(folderPath);
            try {
                for (final Path nioRepositoryPath : nioRepositoryPaths) {

                    if (Files.isDirectory(nioRepositoryPath)) {
                        findModule(nioRepositoryPath,
                                   false);
                    }
                }
            } finally {
                nioRepositoryPaths.close();
            }
        }

        private void addModule(final Module module) {
            module.setPom(pomService.load(module.getPomXMLPath()));
            modules.add(module);
        }
    }
}
