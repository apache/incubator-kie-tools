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

import java.util.Optional;

import javax.annotation.PostConstruct;
import javax.enterprise.context.Dependent;
import javax.inject.Inject;

import elemental2.dom.HTMLElement;
import org.guvnor.common.services.project.client.context.WorkspaceProjectContext;
import org.guvnor.common.services.project.model.Module;
import org.guvnor.common.services.shared.test.TestRunnerService;
import org.jboss.errai.common.client.api.Caller;
import org.jboss.errai.common.client.api.RemoteCallback;
import org.jboss.errai.common.client.api.elemental2.IsElement;
import org.jboss.errai.security.shared.api.identity.User;
import org.jboss.errai.ui.client.local.spi.TranslationService;
import org.kie.workbench.common.screens.library.client.resources.i18n.LibraryConstants;
import org.kie.workbench.common.screens.projecteditor.client.build.BuildExecutor;
import org.uberfire.client.mvp.PlaceManager;
import org.uberfire.mvp.impl.DefaultPlaceRequest;

@Dependent
public class ProjectMainActions implements ProjectMainActionsView.Presenter,
                                           IsElement {

    private final BuildExecutor buildExecutor;
    private final ProjectMainActionsView view;
    private final Caller<TestRunnerService> testRunnerService;
    private final User user;
    private TranslationService ts;
    private PlaceManager placeManager;
    private WorkspaceProjectContext workspaceProjectContext;

    private boolean buildEnabled;
    private boolean deployEnabled;
    private boolean redeployEnabled;

    @Inject
    public ProjectMainActions(final BuildExecutor buildExecutor,
                              final ProjectMainActionsView view,
                              final Caller<TestRunnerService> testRunnerService,
                              final WorkspaceProjectContext workspaceProjectContext,
                              final PlaceManager placeManager,
                              final TranslationService ts,
                              final User user) {
        this.buildExecutor = buildExecutor;
        this.view = view;
        this.testRunnerService = testRunnerService;
        this.workspaceProjectContext = workspaceProjectContext;
        this.placeManager = placeManager;
        this.ts = ts;
        this.user = user;
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
    public void onRunTest() {
        final Optional<Module> activeModule = workspaceProjectContext.getActiveModule();

        if (activeModule.isPresent()) {
            view.showBusyIndicator(ts.getTranslation(LibraryConstants.Testing));

            placeManager.goTo(new DefaultPlaceRequest("org.kie.guvnor.TestResults"));

            testRunnerService.call(
                    (RemoteCallback<Void>) response -> view.hideBusyIndicator()
            ).runAllTests(user.getIdentifier(),
                          activeModule.get().getRootPath());
        }
    }

    @Override
    public HTMLElement getElement() {
        return view.getElement();
    }
}
