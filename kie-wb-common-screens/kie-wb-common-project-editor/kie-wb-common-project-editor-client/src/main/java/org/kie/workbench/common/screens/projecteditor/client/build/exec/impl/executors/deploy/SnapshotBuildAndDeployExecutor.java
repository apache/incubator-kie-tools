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

import java.util.List;
import java.util.Optional;
import java.util.Set;

import javax.enterprise.event.Event;

import org.guvnor.common.services.project.builder.model.BuildResults;
import org.guvnor.common.services.project.builder.service.BuildService;
import org.guvnor.common.services.project.model.GAV;
import org.guvnor.common.services.project.service.DeploymentMode;
import org.jboss.errai.bus.client.api.messaging.Message;
import org.jboss.errai.common.client.api.Caller;
import org.jboss.errai.common.client.api.ErrorCallback;
import org.jboss.errai.common.client.api.RemoteCallback;
import org.kie.server.api.model.KieServerMode;
import org.kie.server.controller.api.model.spec.ContainerSpec;
import org.kie.server.controller.api.model.spec.ServerTemplate;
import org.kie.workbench.common.screens.projecteditor.build.exec.SnapshotDeployment;
import org.kie.workbench.common.screens.projecteditor.build.exec.SnapshotDeploymentSettings;
import org.kie.workbench.common.screens.projecteditor.client.build.exec.BuildExecutionContext;
import org.kie.workbench.common.screens.projecteditor.client.build.exec.dialog.BuildDialog;
import org.kie.workbench.common.screens.projecteditor.client.build.exec.impl.executors.utils.BuildUtils;
import org.kie.workbench.common.screens.projecteditor.client.build.exec.impl.executors.validators.SnapshotContextValidator;
import org.kie.workbench.common.screens.projecteditor.client.editor.DefaultDeploymentPopupDriver;
import org.kie.workbench.common.screens.projecteditor.client.editor.DeploymentPopup;
import org.kie.workbench.common.screens.server.management.service.SpecManagementService;
import org.uberfire.workbench.events.NotificationEvent;

import static org.kie.workbench.common.screens.projecteditor.client.resources.ProjectEditorResources.CONSTANTS;
import static org.uberfire.workbench.events.NotificationEvent.NotificationType.ERROR;
import static org.uberfire.workbench.events.NotificationEvent.NotificationType.SUCCESS;

public class SnapshotBuildAndDeployExecutor extends AbstractBuildAndDeployExecutor {

    private final SnapshotDeploymentSettings settings;

    public SnapshotBuildAndDeployExecutor(final Caller<BuildService> buildServiceCaller,
                                          final Event<BuildResults> buildResultsEvent,
                                          final Event<NotificationEvent> notificationEvent,
                                          final BuildDialog buildDialog,
                                          final DeploymentPopup deploymentPopup,
                                          final Caller<SpecManagementService> specManagementService,
                                          final SnapshotDeploymentSettings settings) {
        super(buildServiceCaller, buildResultsEvent, notificationEvent, buildDialog, new SnapshotContextValidator(), deploymentPopup, specManagementService, KieServerMode.DEVELOPMENT);
        this.settings = settings;
    }

    @Override
    DeploymentMode getPreferredDeploymentMode() {
        return DeploymentMode.FORCED;
    }

    @Override
    void buildDeployWithOneServerTemplate(final BuildExecutionContext context, final ServerTemplate serverTemplate) {
        showBuildMessage();

        context.setServerTemplate(serverTemplate);
        Set<String> containers = BuildUtils.extractExistingContainers(serverTemplate);

        buildServiceCaller.call((RemoteCallback<BuildResults>) result -> {
            if (result.getErrorMessages().isEmpty()) {
                notificationEvent.fire(new NotificationEvent(CONSTANTS.BuildSuccessful(), SUCCESS));

                if (serverTemplate != null && serverTemplate.getId() != null) {
                    GAV gav = context.getModule().getPom().getGav();

                    settings.addDeployment(gav.getGroupId(), gav.getArtifactId(), serverTemplate.getId());
                    settings.save();

                    ContainerSpec containerSpec = BuildUtils.makeContainerSpec(context, result.getParameters());

                    if (containers.contains(context.getContainerId())) {
                        Optional<ContainerSpec> originalSpec = Optional.ofNullable(context.getServerTemplate().getContainerSpec(containerSpec.getId()));
                        originalSpec.ifPresent(original -> containerSpec.setStatus(original.getStatus()));

                        updateContainerSpec(context, containerSpec);
                    } else {
                        saveContainerSpecAndMaybeStartContainer(context, containerSpec);
                    }
                }
            } else {
                notificationEvent.fire(new NotificationEvent(CONSTANTS.BuildFailed(), ERROR));
                finish();
            }

            buildResultsEvent.fire(result);
        }, (ErrorCallback<Message>) (message, e) -> {
            finish();
            return false;
        }).buildAndDeploy(context.getModule(), DeploymentMode.FORCED);
    }

    @Override
    void buildDeployWithMultipleServerTemplates(BuildExecutionContext context, List<ServerTemplate> serverTemplates) {
        GAV gav = context.getModule().getPom().getGav();

        Optional<SnapshotDeployment> optional = settings.getDeployment(gav.getGroupId(), gav.getArtifactId());

        if (optional.isPresent()) {

            SnapshotDeployment previousDeployment = optional.get();

            Optional<ServerTemplate> serverOptional = serverTemplates
                    .stream()
                    .filter(s -> s.getId().equals(previousDeployment.getServer()))
                    .findFirst();

            if (serverOptional.isPresent()) {

                buildDeployWithOneServerTemplate(context, serverOptional.get());

                return;
            }
        }

        context.setServerTemplate(serverTemplates.get(0));

        deploymentPopup.show(new DefaultDeploymentPopupDriver(context,
                                                              DeploymentPopup.Mode.MULTIPLE_SERVER_FORCED,
                                                              () -> serverTemplates,
                                                              () -> buildDeployWithOneServerTemplate(context, context.getServerTemplate()),
                                                              () -> finish()));
    }

    protected void updateContainerSpec(final BuildExecutionContext context, final ContainerSpec containerSpec) {

        specManagementService.call(ignore -> {
            notifyUpdateSuccess();
        }, (o, throwable) -> {
            notifyUpdateError();
            return false;
        }).updateContainerSpec(context.getServerTemplate().getId(), containerSpec);
    }

    protected void notifyUpdateSuccess() {
        notificationEvent.fire(new NotificationEvent(CONSTANTS.DeploySuccessfulAndContainerUpdated(), NotificationEvent.NotificationType.SUCCESS));
        finish();
    }

    protected void notifyUpdateError() {
        notificationEvent.fire(new NotificationEvent(CONSTANTS.DeployFailed(), NotificationEvent.NotificationType.ERROR));
        finish();
    }
}
