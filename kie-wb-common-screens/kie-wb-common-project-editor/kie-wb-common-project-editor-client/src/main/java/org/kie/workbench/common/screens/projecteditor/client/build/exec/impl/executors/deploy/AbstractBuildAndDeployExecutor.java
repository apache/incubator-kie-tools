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
import java.util.Optional;
import java.util.function.Predicate;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import javax.enterprise.event.Event;

import org.guvnor.common.services.project.builder.model.BuildResults;
import org.guvnor.common.services.project.builder.service.BuildService;
import org.guvnor.common.services.project.service.DeploymentMode;
import org.jboss.errai.bus.client.api.messaging.Message;
import org.jboss.errai.common.client.api.Caller;
import org.jboss.errai.common.client.api.ErrorCallback;
import org.jboss.errai.common.client.api.RemoteCallback;
import org.kie.server.api.model.KieServerMode;
import org.kie.server.controller.api.model.spec.ContainerSpec;
import org.kie.server.controller.api.model.spec.ServerTemplate;
import org.kie.server.controller.api.model.spec.ServerTemplateList;
import org.kie.workbench.common.screens.projecteditor.client.build.exec.BuildExecutionContext;
import org.kie.workbench.common.screens.projecteditor.client.build.exec.dialog.BuildDialog;
import org.kie.workbench.common.screens.projecteditor.client.build.exec.impl.executors.AbstractExecutor;
import org.kie.workbench.common.screens.projecteditor.client.build.exec.impl.executors.ContextValidator;
import org.kie.workbench.common.screens.projecteditor.client.build.exec.impl.executors.utils.BuildUtils;
import org.kie.workbench.common.screens.projecteditor.client.editor.DeploymentPopup;
import org.kie.workbench.common.screens.server.management.service.SpecManagementService;
import org.uberfire.mvp.impl.DefaultPlaceRequest;
import org.uberfire.workbench.events.NotificationEvent;

import static org.kie.workbench.common.workbench.client.PerspectiveIds.SERVER_MANAGEMENT;
import static org.kie.workbench.common.screens.projecteditor.client.resources.ProjectEditorResources.CONSTANTS;
import static org.uberfire.workbench.events.NotificationEvent.NotificationType.ERROR;
import static org.uberfire.workbench.events.NotificationEvent.NotificationType.SUCCESS;

public abstract class AbstractBuildAndDeployExecutor extends AbstractExecutor {

    protected final DeploymentPopup deploymentPopup;
    protected final Caller<SpecManagementService> specManagementService;
    protected final KieServerMode preferedKieServerMode;
    protected Predicate<ServerTemplate> templateFilter = template -> true;

    public AbstractBuildAndDeployExecutor(final Caller<BuildService> buildServiceCaller,
                                          final Event<BuildResults> buildResultsEvent,
                                          final Event<NotificationEvent> notificationEvent,
                                          final BuildDialog buildDialog,
                                          final ContextValidator validator,
                                          final DeploymentPopup deploymentPopup,
                                          final Caller<SpecManagementService> specManagementService,
                                          final KieServerMode preferedKieServerMode) {
        super(buildServiceCaller, buildResultsEvent, notificationEvent, buildDialog, validator);
        this.deploymentPopup = deploymentPopup;
        this.specManagementService = specManagementService;
        this.preferedKieServerMode = preferedKieServerMode;
    }

    @Override
    protected void start(final BuildExecutionContext context) {
        specManagementService.call((ServerTemplateList serverTemplates) -> {
            List<ServerTemplate> templates = getServerTemplates(serverTemplates);
            switch (templates.size()) {
                case 0:
                    buildDeployWithoutServerTemplate(context, getPreferredDeploymentMode());
                    break;
                case 1:
                    buildDeployWithOneServerTemplate(context, templates.get(0));
                    break;
                default:
                    buildDeployWithMultipleServerTemplates(context, templates);
                    break;
            }
        }).listServerTemplates();
    }

    List<ServerTemplate> getServerTemplates(ServerTemplateList templateList) {

        if (templateList != null) {
            return Stream.of(templateList.getServerTemplates())
                    .filter(templateFilter)
                    .collect(Collectors.toList());
        }

        return Collections.emptyList();
    }

    abstract DeploymentMode getPreferredDeploymentMode();

    abstract void buildDeployWithOneServerTemplate(BuildExecutionContext context, ServerTemplate serverTemplate);

    abstract void buildDeployWithMultipleServerTemplates(BuildExecutionContext context, List<ServerTemplate> serverTemplates);

    private void buildDeployWithoutServerTemplate(final BuildExecutionContext context, final DeploymentMode mode) {
        showBuildMessage();
        buildServiceCaller.call((RemoteCallback<BuildResults>) result -> {
            if (result.getErrorMessages().isEmpty()) {
                notificationEvent.fire(new NotificationEvent(CONSTANTS.BuildSuccessful(), NotificationEvent.NotificationType.SUCCESS));
                notificationEvent.fire(
                        new NotificationEvent(
                                CONSTANTS.DeploymentSkippedDueToNoServerTemplateConfiguredForMode(
                                        preferedKieServerMode.name().toLowerCase()),
                                NotificationEvent.NotificationType.WARNING)
                        .setAutoHide(false)
                        .setNavigation(CONSTANTS.ViewDeploymentDetails(),
                                       new DefaultPlaceRequest(SERVER_MANAGEMENT)));
            } else {
                notificationEvent.fire(new NotificationEvent(CONSTANTS.BuildFailed(), NotificationEvent.NotificationType.ERROR));
            }

            buildResultsEvent.fire(result);
            finish();
        }, (ErrorCallback<Message>) (message, throwable) -> {
            buildDeployWithoutServerTemplate(context, DeploymentMode.FORCED);
            return false;
        }).buildAndDeploy(context.getModule(), mode);
    }

    protected void onBuildDeploySuccess(final BuildExecutionContext context, final BuildResults result) {
        if (result.getErrorMessages().isEmpty()) {
            notificationEvent.fire(new NotificationEvent(CONSTANTS.BuildSuccessful(), SUCCESS));

            ServerTemplate serverTemplate = context.getServerTemplate();

            if (serverTemplate != null && serverTemplate.getId() != null) {

                ContainerSpec containerSpec = BuildUtils.makeContainerSpec(context, result.getParameters());

                Optional<ContainerSpec> optional = Optional.ofNullable(context.getServerTemplate().getContainerSpec(containerSpec.getId()));

                if (optional.isPresent()) {
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
    }

    protected void updateContainerSpec(final BuildExecutionContext context, final ContainerSpec containerSpec) {
        ServerTemplate serverTemplate = context.getServerTemplate();

        if (serverTemplate.getMode().equals(KieServerMode.DEVELOPMENT)) {
            containerSpec.setStatus(serverTemplate.getContainerSpec(containerSpec.getId()).getStatus());
            specManagementService.call(ignore -> {
                notifyUpdateSuccess();
            }, (o, throwable) -> {
                notifyUpdateError();
                return false;
            }).updateContainerSpec(context.getServerTemplate().getId(), containerSpec);
        } else {
            notificationEvent.fire(
                    new NotificationEvent(
                            CONSTANTS.DeploymentSkippedCannotUpdateDeploymentsOnProduction(),
                            ERROR)
                    .setAutoHide(false)
                    .setNavigation(CONSTANTS.ViewDeploymentDetails(),
                                   new DefaultPlaceRequest(SERVER_MANAGEMENT)));
            finish();
        }
    }

    protected void notifyUpdateSuccess() {
        notificationEvent.fire(
                new NotificationEvent(
                        CONSTANTS.DeploySuccessfulAndContainerUpdated(),
                        NotificationEvent.NotificationType.SUCCESS)
                .setAutoHide(false)
                .setNavigation(CONSTANTS.ViewDeploymentDetails(),
                               new DefaultPlaceRequest(SERVER_MANAGEMENT)));
        finish();
    }

    protected void notifyUpdateError() {
        notificationEvent.fire(
                new NotificationEvent(
                        CONSTANTS.DeployFailed(),
                        NotificationEvent.NotificationType.ERROR)
                .setAutoHide(false)
                .setNavigation(CONSTANTS.ViewDeploymentDetails(),
                               new DefaultPlaceRequest(SERVER_MANAGEMENT)));
        finish();
    }

    protected void saveContainerSpecAndMaybeStartContainer(final BuildExecutionContext context, final ContainerSpec containerSpec) {
        specManagementService.call(ignore -> {
            if (!context.isStartContainer()) {
                notificationEvent.fire(
                        new NotificationEvent(
                                CONSTANTS.DeploySuccessful(),
                                NotificationEvent.NotificationType.SUCCESS)
                        .setAutoHide(false)
                        .setNavigation(CONSTANTS.ViewDeploymentDetails(),
                                       new DefaultPlaceRequest(SERVER_MANAGEMENT)));
                finish();
                return;
            }

            specManagementService.call(ignore2 -> {
                notificationEvent.fire(
                        new NotificationEvent(
                                CONSTANTS.DeploySuccessfulAndContainerStarted(),
                                NotificationEvent.NotificationType.SUCCESS)
                        .setAutoHide(false)
                        .setNavigation(CONSTANTS.ViewDeploymentDetails(),
                                       new DefaultPlaceRequest(SERVER_MANAGEMENT)));
                finish();
            }, (o, throwable) -> {
                notificationEvent.fire(
                        new NotificationEvent(
                                CONSTANTS.DeploySuccessfulButContainerFailedToStart(),
                                NotificationEvent.NotificationType.WARNING)
                        .setAutoHide(false)
                        .setNavigation(CONSTANTS.ViewDeploymentDetails(),
                                       new DefaultPlaceRequest(SERVER_MANAGEMENT)));
                finish();
                return false;
            }).startContainer(containerSpec);
        }, (o, throwable) -> {
            notificationEvent.fire(
                    new NotificationEvent(
                            CONSTANTS.DeployFailed(),
                            NotificationEvent.NotificationType.ERROR)
                    .setAutoHide(false)
                    .setNavigation(CONSTANTS.ViewDeploymentDetails(),
                                   new DefaultPlaceRequest(SERVER_MANAGEMENT)));
            finish();
            return false;
        }).saveContainerSpec(context.getServerTemplate().getId(), containerSpec);
    }
}
