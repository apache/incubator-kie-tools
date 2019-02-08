/*
 * Copyright 2019 Red Hat, Inc. and/or its affiliates.
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

package org.kie.workbench.common.screens.library.client.screens.project.actions;

import javax.annotation.PostConstruct;
import javax.enterprise.context.Dependent;
import javax.inject.Inject;

import elemental2.dom.HTMLElement;
import org.jboss.errai.common.client.api.elemental2.IsElement;
import org.kie.workbench.common.screens.projecteditor.client.build.BuildExecutor;

@Dependent
public class ProjectMainActions implements ProjectMainActionsView.Presenter,
                                           IsElement {

    private final BuildExecutor buildExecutor;
    private final ProjectMainActionsView view;

    private boolean buildEnabled;
    private boolean deployEnabled;
    private boolean redeployEnabled;

    @Inject
    public ProjectMainActions(BuildExecutor buildExecutor, ProjectMainActionsView view) {
        this.buildExecutor = buildExecutor;
        this.view = view;
    }

    @PostConstruct
    public void init() {
        view.init(this);
    }

    public void setBuildEnabled(boolean buildEnabled) {
        this.buildEnabled = buildEnabled;
        view.setBuildDropDownEnabled(buildEnabled);
    }

    public void setDeployEnabled(boolean deployEnabled) {
        this.deployEnabled = deployEnabled;
        view.setBuildAndDeployDropDownEnabled(deployEnabled);
    }

    public void setRedeployEnabled(boolean redeployEnabled) {
        this.redeployEnabled = redeployEnabled;
        view.setRedeployEnabled(redeployEnabled);
    }

    @Override
    public void triggerBuild() {
        if (buildEnabled) {
            this.buildExecutor.triggerBuild();
        }
    }

    @Override
    public void triggerBuildAndInstall() {
        if (buildEnabled) {
            this.buildExecutor.triggerBuildAndInstall();
        }
    }

    @Override
    public void triggerBuildAndDeploy() {
        if (deployEnabled) {
            this.buildExecutor.triggerBuildAndDeploy();
        }
    }

    @Override
    public void triggerRedeploy() {
        if (deployEnabled && redeployEnabled) {
            this.buildExecutor.triggerRedeploy();
        }
    }

    @Override
    public HTMLElement getElement() {
        return view.getElement();
    }
}
