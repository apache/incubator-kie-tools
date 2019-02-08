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

package org.kie.workbench.common.screens.projecteditor.client.build;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;

import org.guvnor.common.services.project.client.context.WorkspaceProjectContext;
import org.guvnor.common.services.project.model.GAV;
import org.guvnor.common.services.project.model.Module;
import org.kie.workbench.common.screens.projecteditor.client.build.exec.BuildExecutionContext;
import org.kie.workbench.common.screens.projecteditor.client.build.exec.BuildExecutionManager;
import org.kie.workbench.common.screens.projecteditor.client.build.exec.BuildType;

import static org.kie.workbench.common.screens.projecteditor.client.build.exec.BuildType.BUILD;
import static org.kie.workbench.common.screens.projecteditor.client.build.exec.BuildType.DEPLOY;
import static org.kie.workbench.common.screens.projecteditor.client.build.exec.BuildType.INSTALL;
import static org.kie.workbench.common.screens.projecteditor.client.build.exec.BuildType.REDEPLOY;

@ApplicationScoped
public class BuildExecutor {

    private final WorkspaceProjectContext projectContext;

    private BuildExecutionManager buildExecutionManager;

    @Inject
    public BuildExecutor(final WorkspaceProjectContext projectContext,
                         final BuildExecutionManager buildExecutionManager) {
        this.projectContext = projectContext;
        this.buildExecutionManager = buildExecutionManager;
    }

    public void triggerBuild() {
        trigger(BUILD);
    }

    public void triggerBuildAndInstall() {
        trigger(INSTALL);
    }

    public void triggerBuildAndDeploy() {
        trigger(DEPLOY);
    }

    public void triggerRedeploy() {
        trigger(REDEPLOY);
    }

    private void trigger(final BuildType buildType) {
        if (projectContext.getActiveModule().isPresent()) {
            Module module = projectContext.getActiveModule().get();

            buildExecutionManager.execute(buildType, new BuildExecutionContext(defaultContainerId(), defaultContainerAlias(), module));
        }
    }

    public String defaultContainerAlias() {
        return projectGAV().getArtifactId();
    }

    public String defaultContainerId() {
        return projectGAV().getArtifactId() + "_" + projectGAV().getVersion();
    }

    private Module activeModule() {
        return projectContext.getActiveModule()
                .orElseThrow(() -> new IllegalStateException("Cannot perform build without active module."));
    }

    private GAV projectGAV() {
        return activeModule().getPom().getGav();
    }
}
