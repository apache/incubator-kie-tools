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

package org.kie.workbench.common.screens.projecteditor.client.build.exec.impl.executors.deploy;

import java.util.Collections;
import java.util.List;
import java.util.Set;

import javax.enterprise.event.Event;

import org.guvnor.common.services.project.builder.model.BuildResults;
import org.guvnor.common.services.project.builder.service.BuildService;
import org.guvnor.common.services.project.client.repositories.ConflictingRepositoriesPopup;
import org.guvnor.common.services.project.model.MavenRepositoryMetadata;
import org.guvnor.common.services.project.service.DeploymentMode;
import org.guvnor.common.services.project.service.GAVAlreadyExistsException;
import org.jboss.errai.bus.client.api.messaging.Message;
import org.jboss.errai.common.client.api.Caller;
import org.jboss.errai.common.client.api.ErrorCallback;
import org.jboss.errai.common.client.api.RemoteCallback;
import org.kie.server.api.model.KieServerMode;
import org.kie.server.controller.api.model.spec.ServerTemplate;
import org.kie.workbench.common.screens.projecteditor.client.build.exec.BuildExecutionContext;
import org.kie.workbench.common.screens.projecteditor.client.build.exec.dialog.BuildDialog;
import org.kie.workbench.common.screens.projecteditor.client.build.exec.impl.executors.utils.BuildUtils;
import org.kie.workbench.common.screens.projecteditor.client.build.exec.impl.executors.validators.DefaultContextValidator;
import org.kie.workbench.common.screens.projecteditor.client.editor.DefaultDeploymentPopupDriver;
import org.kie.workbench.common.screens.projecteditor.client.editor.DeploymentPopup;
import org.kie.workbench.common.screens.server.management.service.SpecManagementService;
import org.uberfire.mvp.Command;
import org.uberfire.workbench.events.NotificationEvent;

public class ProductionBuildAndDeployExecutor extends AbstractBuildAndDeployExecutor {

    private final ConflictingRepositoriesPopup conflictingRepositoriesPopup;

    public ProductionBuildAndDeployExecutor(final Caller<BuildService> buildServiceCaller,
                                            final Event<BuildResults> buildResultsEvent,
                                            final Event<NotificationEvent> notificationEvent,
                                            final BuildDialog buildDialog,
                                            final DeploymentPopup deploymentPopup,
                                            final Caller<SpecManagementService> specManagementService,
                                            final ConflictingRepositoriesPopup conflictingRepositoriesPopup) {

        super(buildServiceCaller, buildResultsEvent, notificationEvent, buildDialog, new DefaultContextValidator(), deploymentPopup, specManagementService, KieServerMode.PRODUCTION);
        this.conflictingRepositoriesPopup = conflictingRepositoriesPopup;
    }

    @Override
    DeploymentMode getPreferredDeploymentMode() {
        return DeploymentMode.VALIDATED;
    }

    @Override
    void buildDeployWithOneServerTemplate(final BuildExecutionContext context, final ServerTemplate serverTemplate) {
        final Set<String> existingContainers = BuildUtils.extractExistingContainers(serverTemplate);

        context.setServerTemplate(serverTemplate);

        if (!existingContainers.contains(context.getContainerId())) {
            buildAndDeploy(context, DeploymentMode.VALIDATED);
        } else {
            deploymentPopup.show(new DefaultDeploymentPopupDriver(context,
                                                                  DeploymentPopup.Mode.SINGLE_SERVER,
                                                                  () -> Collections.EMPTY_LIST,
                                                                  () -> buildAndDeploy(context, DeploymentMode.VALIDATED),
                                                                  () -> finish()));
        }
    }

    private void buildAndDeploy(final BuildExecutionContext context, final DeploymentMode deploymentMode) {
        showBuildMessage();
        buildServiceCaller.call((RemoteCallback<BuildResults>) buildResults -> onBuildDeploySuccess(context, buildResults),
                                (ErrorCallback<Message>) (message, throwable) -> {
                                    onBuildDeployError(context, throwable);
                                    return false;
                                }).buildAndDeploy(context.getModule(), deploymentMode);
    }

    private void showConflictingRepositoriesPopup(final BuildExecutionContext context,
                                                  final GAVAlreadyExistsException parameter,
                                                  final Command command) {
        final Set<MavenRepositoryMetadata> repositories = parameter.getRepositories();

        buildDialog.hideBusyIndicator();

        conflictingRepositoriesPopup.setContent(context.getModule().getPom().getGav(),
                                                repositories,
                                                () -> finish(),
                                                command);
        conflictingRepositoriesPopup.show();
    }

    private void onBuildDeployError(final BuildExecutionContext context, Throwable throwable) {
        if (throwable instanceof GAVAlreadyExistsException) {
            showConflictingRepositoriesPopup(context,
                                             (GAVAlreadyExistsException) throwable,
                                             () -> buildAndDeploy(context, DeploymentMode.FORCED));
        } else {
            finish();
        }
    }

    @Override
    void buildDeployWithMultipleServerTemplates(final BuildExecutionContext context, final List<ServerTemplate> serverTemplates) {
        context.setServerTemplate(serverTemplates.get(0));

        deploymentPopup.show(new DefaultDeploymentPopupDriver(context,
                                                              DeploymentPopup.Mode.MULTIPLE_SERVER,
                                                              () -> serverTemplates,
                                                              () -> buildAndDeploy(context, DeploymentMode.VALIDATED),
                                                              () -> finish()));
    }
}
