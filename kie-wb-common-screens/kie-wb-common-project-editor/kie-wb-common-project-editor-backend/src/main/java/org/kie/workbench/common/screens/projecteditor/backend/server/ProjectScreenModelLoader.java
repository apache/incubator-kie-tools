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

package org.kie.workbench.common.screens.projecteditor.backend.server;

import javax.inject.Inject;

import org.guvnor.common.services.project.service.ModuleRepositoriesService;
import org.guvnor.common.services.project.service.POMService;
import org.guvnor.common.services.shared.metadata.MetadataService;
import org.guvnor.common.services.shared.metadata.model.Metadata;
import org.kie.workbench.common.screens.projecteditor.model.ProjectScreenModel;
import org.kie.workbench.common.services.shared.kmodule.KModuleService;
import org.kie.workbench.common.services.shared.project.KieModule;
import org.kie.workbench.common.services.shared.project.KieModuleService;
import org.kie.workbench.common.services.shared.project.ProjectImportsService;
import org.kie.workbench.common.services.shared.whitelist.PackageNameWhiteListService;
import org.uberfire.backend.server.util.Paths;
import org.uberfire.backend.vfs.Path;

public class ProjectScreenModelLoader {

    private KieModuleService moduleService;
    private POMService pomService;
    private MetadataService metadataService;
    private KModuleService kModuleService;
    private ProjectImportsService importsService;
    private ModuleRepositoriesService repositoriesService;
    private PackageNameWhiteListService whiteListService;

    public ProjectScreenModelLoader() {
    }

    @Inject
    public ProjectScreenModelLoader(final KieModuleService moduleService,
                                    final POMService pomService,
                                    final MetadataService metadataService,
                                    final KModuleService kModuleService,
                                    final ProjectImportsService importsService,
                                    final ModuleRepositoriesService repositoriesService,
                                    final PackageNameWhiteListService whiteListService) {
        this.moduleService = moduleService;
        this.pomService = pomService;
        this.metadataService = metadataService;
        this.kModuleService = kModuleService;
        this.importsService = importsService;
        this.repositoriesService = repositoriesService;
        this.whiteListService = whiteListService;
    }

    public ProjectScreenModel load(final Path pathToPom) {
        return new Loader(pathToPom).load();
    }

    protected boolean fileExists(final Path path) {
        return org.uberfire.java.nio.file.Files.exists(Paths.convert(path));
    }

    class Loader {

        private final ProjectScreenModel model = new ProjectScreenModel();
        private final Path pathToPom;
        private final KieModule project;

        public Loader(final Path pathToPom) {
            this.pathToPom = pathToPom;
            project = moduleService.resolveModule(pathToPom);
        }

        public ProjectScreenModel load() {

            loadPOM();
            loadKModule();
            loadGitURL();
            loadImports();
            loadWhiteList();
            loadRepositories();

            return model;
        }

        private void loadPOM() {
            model.setPOM(pomService.load(pathToPom));
            model.setPOMMetaData(getMetadata(pathToPom));
            model.setPathToPOM(pathToPom);
        }

        private void loadKModule() {
            model.setKModule(kModuleService.load(project.getKModuleXMLPath()));
            model.setKModuleMetaData(getMetadata(project.getKModuleXMLPath()));
            model.setPathToKModule(project.getKModuleXMLPath());
        }

        private void loadGitURL() {
            String value = "";
            try {
                value = Paths.convert(project.getRootPath()).getFileSystem().toString();
            } catch (final Exception ignore) {
                //this is basically for tests that don't use git file system
            }

            model.setGitUrl(value.replace("\n", " | "));
        }

        private void loadImports() {
            model.setProjectImports(importsService.load(project.getImportsPath()));
            model.setProjectImportsMetaData(getMetadata(project.getImportsPath()));
            model.setPathToImports(project.getImportsPath());
        }

        private void loadRepositories() {
            model.setRepositories(repositoriesService.load(project.getRepositoriesPath()));
            model.setPathToRepositories(project.getRepositoriesPath());
        }

        private void loadWhiteList() {
            model.setWhiteList(whiteListService.load(project.getPackageNamesWhiteListPath()));
            model.setWhiteListMetaData(getMetadata(project.getPackageNamesWhiteListPath()));
            model.setPathToWhiteList(project.getPackageNamesWhiteListPath());
        }

        private Metadata getMetadata(final Path path) {
            if (fileExists(path)) {
                return metadataService.getMetadata(path);
            } else {
                return new Metadata();
            }
        }
    }
}
