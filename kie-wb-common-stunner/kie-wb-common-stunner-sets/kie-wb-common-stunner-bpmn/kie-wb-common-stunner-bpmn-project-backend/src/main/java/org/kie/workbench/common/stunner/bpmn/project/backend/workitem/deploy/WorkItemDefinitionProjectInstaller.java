/*
 * Copyright 2018 Red Hat, Inc. and/or its affiliates.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.kie.workbench.common.stunner.bpmn.project.backend.workitem.deploy;

import java.util.Collection;
import java.util.Set;
import java.util.stream.Collectors;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;

import org.guvnor.common.services.project.model.Dependencies;
import org.guvnor.common.services.project.model.Dependency;
import org.guvnor.common.services.project.model.Module;
import org.guvnor.common.services.project.model.POM;
import org.guvnor.common.services.project.service.POMService;
import org.guvnor.common.services.shared.metadata.MetadataService;
import org.kie.workbench.common.services.shared.project.KieModuleService;
import org.kie.workbench.common.stunner.bpmn.workitem.WorkItemDefinition;
import org.kie.workbench.common.stunner.core.diagram.Metadata;
import org.uberfire.backend.vfs.Path;

/**
 * It adds the work item definitions dependencies into the KIE runtime model.
 */
@ApplicationScoped
public class WorkItemDefinitionProjectInstaller {

    private static final String INSALL_MESSAGE = "Updated project dependencies from work item definitions";

    private final POMService pomService;
    private final MetadataService metadataService;
    private final KieModuleService moduleService;

    protected WorkItemDefinitionProjectInstaller() {
        this.pomService = null;
        this.metadataService = null;
        this.moduleService = null;
    }

    @Inject
    public WorkItemDefinitionProjectInstaller(final POMService pomService,
                                              final MetadataService metadataService,
                                              final KieModuleService moduleService) {
        this.pomService = pomService;
        this.metadataService = metadataService;
        this.moduleService = moduleService;
    }

    @SuppressWarnings("all")
    public void install(final Collection<WorkItemDefinition> items,
                        final Metadata metadata) {
        final Module module = moduleService.resolveModule(metadata.getRoot());
        final Path pomXMLPath = module.getPomXMLPath();
        final POM projectPOM = pomService.load(pomXMLPath);
        if (projectPOM != null) {
            final Dependencies projectDependencies = projectPOM.getDependencies();
            final Set<Dependency> widDependencies = items.stream()
                    .flatMap(wid -> wid.getDependencies().stream())
                    .filter(d -> !projectDependencies.contains(d))
                    .collect(Collectors.toSet());
            projectDependencies.addAll(widDependencies);
            pomService.save(pomXMLPath,
                            projectPOM,
                            metadataService.getMetadata(pomXMLPath),
                            INSALL_MESSAGE,
                            false);
        }
    }
}
