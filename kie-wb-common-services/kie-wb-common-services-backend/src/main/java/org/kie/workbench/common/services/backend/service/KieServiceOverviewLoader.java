/*
 * Copyright 2018 Red Hat, Inc. and/or its affiliates.
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

package org.kie.workbench.common.services.backend.service;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;

import org.guvnor.common.services.backend.metadata.MetadataServerSideService;
import org.guvnor.common.services.project.service.WorkspaceProjectService;
import org.guvnor.common.services.shared.metadata.model.Overview;
import org.kie.workbench.common.services.shared.project.KieModule;
import org.kie.workbench.common.services.shared.project.KieModuleService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.uberfire.backend.vfs.Path;

@ApplicationScoped
public class KieServiceOverviewLoader {

    protected static Logger logger = LoggerFactory.getLogger(KieServiceOverviewLoader.class);

    private MetadataServerSideService metadataService;

    private KieModuleService moduleService;

    private WorkspaceProjectService projectService;

    public KieServiceOverviewLoader() {
        //CDI proxy
    }

    @Inject
    public KieServiceOverviewLoader(final MetadataServerSideService metadataService,
                                    final KieModuleService moduleService,
                                    final WorkspaceProjectService projectService) {
        this.metadataService = metadataService;
        this.moduleService = moduleService;
        this.projectService = projectService;
    }

    public Overview loadOverview(final Path path) {
        final Overview overview = new Overview();

        try {
            // Some older versions in our example do not have metadata. This should be impossible in any kie-wb version
            overview.setMetadata(metadataService.getMetadata(path));
        } catch (Exception e) {
            logger.warn("No metadata found for file: " + path.getFileName() + ", full path [" + path.toString() + "]");
        }

        //Some resources are not within a Module (e.g. categories.xml) so don't assume we can set the module name
        final KieModule module = moduleService.resolveModule(path);
        if (module == null) {
            logger.info("File: " + path.getFileName() + ", full path [" + path.toString() + "] was not within a Module. Module Name cannot be set.");
        } else {
            try {
                overview.setProjectName(projectService.resolveProject(module.getRootPath()).getName());
            } catch (Throwable t) {
                logger.debug("File: " + path.getFileName() + ", full path [" + path.toString() + "] was not within a Project. Project name cannot be set.", t);
            }
        }

        return overview;
    }
}
