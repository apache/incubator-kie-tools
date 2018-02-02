/*
 * Copyright 2017 Red Hat, Inc. and/or its affiliates.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.kie.workbench.common.services.backend.builder.core;

import java.util.Set;
import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;

import org.guvnor.common.services.project.model.GAV;
import org.guvnor.common.services.project.model.MavenRepositoryMetadata;
import org.guvnor.common.services.project.model.Module;
import org.guvnor.common.services.project.model.ModuleRepositories;
import org.guvnor.common.services.project.service.DeploymentMode;
import org.guvnor.common.services.project.service.GAVAlreadyExistsException;
import org.guvnor.common.services.project.service.ModuleRepositoriesService;
import org.guvnor.common.services.project.service.ModuleRepositoryResolver;
import org.kie.workbench.common.services.shared.project.KieModule;

/**
 * Helper class for verifying if a given module fulfils the required conditions for performing the deployment.
 */
@ApplicationScoped
public class DeploymentVerifier {

    private ModuleRepositoryResolver repositoryResolver;

    private ModuleRepositoriesService moduleRepositoriesService;

    public DeploymentVerifier() {
        //Empty constructor for Weld proxying
    }

    @Inject
    public DeploymentVerifier(final ModuleRepositoryResolver repositoryResolver,
                              final ModuleRepositoriesService moduleRepositoriesService) {
        this.repositoryResolver = repositoryResolver;
        this.moduleRepositoriesService = moduleRepositoriesService;
    }

    /**
     * Verifies if a given module can be deployed in current server for the given deployment mode.
     * An example of when a module can't be deployed is when the deployment mode is DeploymentMode.VALIDATED and the
     * module was already deployed. This case will raise a GAVAlreadyExistsException.
     * @param module the module to verify.
     * @param deploymentMode the deployment mode for doing the verification.
     */
    public void verifyWithException(final Module module,
                                    DeploymentMode deploymentMode) {
        if (DeploymentMode.VALIDATED.equals(deploymentMode)) {
            // Check is the POM's GAV resolves to any pre-existing artifacts.
            final GAV gav = module.getPom().getGav();
            if (gav.isSnapshot()) {
                return;
            }
            final ModuleRepositories projectRepositories = moduleRepositoriesService.load(((KieModule) module).getRepositoriesPath());
            final Set<MavenRepositoryMetadata> repositories = repositoryResolver.getRepositoriesResolvingArtifact(gav,
                                                                                                                  module,
                                                                                                                  projectRepositories.filterByIncluded());
            if (repositories.size() > 0) {
                throw new GAVAlreadyExistsException(gav,
                                                    repositories);
            }
        }
    }
}