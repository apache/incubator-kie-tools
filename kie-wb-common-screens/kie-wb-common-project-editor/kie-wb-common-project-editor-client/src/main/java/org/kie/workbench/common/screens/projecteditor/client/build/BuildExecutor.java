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

import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import javax.enterprise.event.Event;
import javax.inject.Inject;

import com.google.common.collect.FluentIterable;
import com.google.common.collect.Maps;
import org.guvnor.common.services.project.builder.model.BuildResults;
import org.guvnor.common.services.project.builder.service.BuildService;
import org.guvnor.common.services.project.client.repositories.ConflictingRepositoriesPopup;
import org.guvnor.common.services.project.context.ProjectContext;
import org.guvnor.common.services.project.model.GAV;
import org.guvnor.common.services.project.service.DeploymentMode;
import org.guvnor.common.services.project.service.GAVAlreadyExistsException;
import org.jboss.errai.bus.client.api.messaging.Message;
import org.jboss.errai.common.client.api.Caller;
import org.jboss.errai.common.client.api.RemoteCallback;
import org.kie.server.api.model.KieContainerStatus;
import org.kie.server.api.model.ReleaseId;
import org.kie.server.controller.api.model.spec.ContainerSpec;
import org.kie.server.controller.api.model.spec.ServerTemplate;
import org.kie.server.controller.api.model.spec.ServerTemplateKey;
import org.kie.workbench.common.screens.projecteditor.client.editor.DeploymentScreenPopupViewImpl;
import org.kie.workbench.common.screens.projecteditor.client.resources.ProjectEditorResources;
import org.kie.workbench.common.screens.server.management.service.SpecManagementService;
import org.kie.workbench.common.widgets.client.callbacks.CommandWithThrowableDrivenErrorCallback;
import org.uberfire.ext.widgets.common.client.common.HasBusyIndicator;
import org.uberfire.mvp.Command;
import org.uberfire.workbench.events.NotificationEvent;

public class BuildExecutor {

    public interface View extends HasBusyIndicator {

        void showABuildIsAlreadyRunning();
    }

    private DeploymentScreenPopupViewImpl deploymentScreenPopupView;

    private Caller<SpecManagementService> specManagementService;

    private Caller<BuildService> buildServiceCaller;

    private Event<BuildResults> buildResultsEvent;

    private Event<NotificationEvent> notificationEvent;

    private ConflictingRepositoriesPopup conflictingRepositoriesPopup;

    private ProjectContext projectContext;

    private View view;

    private boolean building = false;

    @Inject
    public BuildExecutor(DeploymentScreenPopupViewImpl deploymentScreenPopupView,
                         Caller<SpecManagementService> specManagementService,
                         Caller<BuildService> buildServiceCaller,
                         Event<BuildResults> buildResultsEvent,
                         Event<NotificationEvent> notificationEvent,
                         ConflictingRepositoriesPopup conflictingRepositoriesPopup,
                         ProjectContext projectContext) {
        this.deploymentScreenPopupView = deploymentScreenPopupView;
        this.specManagementService = specManagementService;
        this.buildServiceCaller = buildServiceCaller;
        this.buildResultsEvent = buildResultsEvent;
        this.notificationEvent = notificationEvent;
        this.conflictingRepositoriesPopup = conflictingRepositoriesPopup;
        this.projectContext = projectContext;
    }

    public void init(final View view) {
        this.view = view;
    }

    private Map<Class<? extends Throwable>, CommandWithThrowableDrivenErrorCallback.CommandWithThrowable> onBuildAndDeployGavExistsHandler = new HashMap<Class<? extends Throwable>, CommandWithThrowableDrivenErrorCallback.CommandWithThrowable>() {{
        put(GAVAlreadyExistsException.class,
            new CommandWithThrowableDrivenErrorCallback.CommandWithThrowable() {
                @Override
                public void execute(final Throwable parameter) {
                    view.hideBusyIndicator();
                    conflictingRepositoriesPopup.setContent(projectContext.getActiveProject().getPom().getGav(),
                                                            ((GAVAlreadyExistsException) parameter).getRepositories(),
                                                            () -> {
                                                                conflictingRepositoriesPopup.hide();
                                                                getBuildDeployCommand(DeploymentMode.FORCED).execute();
                                                            });
                    conflictingRepositoriesPopup.show();
                }
            });
    }};

    public void triggerBuild() {
        getSafeExecutedCommand(getBuildCommand()).execute();
    }

    public void triggerBuildAndDeploy() {
        specManagementService.call(new RemoteCallback<Collection<ServerTemplate>>() {
            @Override
            public void callback(final Collection<ServerTemplate> serverTemplates) {
                final String defaultContainerId = projectContext.getActiveProject().getPom().getGav().getArtifactId() + "_" + projectContext.getActiveProject().getPom().getGav().getVersion();
                final String defaultContainerAlias = projectContext.getActiveProject().getPom().getGav().getArtifactId();
                final boolean defaultStartContainer = true;
                if (serverTemplates.isEmpty()) {
                    getSafeExecutedCommand(getBuildDeployCommand(DeploymentMode.VALIDATED)).execute();
                } else if (serverTemplates.size() == 1) {
                    buildDeployWithOneServerTemplateAvailable(serverTemplates,
                                                              defaultContainerId,
                                                              defaultContainerAlias,
                                                              defaultStartContainer);
                } else {
                    buildDeployWithMultipleServerTemplatesAvailable(serverTemplates,
                                                                    defaultContainerId,
                                                                    defaultContainerAlias,
                                                                    defaultStartContainer);
                }
            }
        }).listServerTemplates();
    }

    private void buildDeployWithOneServerTemplateAvailable(Collection<ServerTemplate> serverTemplates,
                                                           String defaultContainerId,
                                                           String defaultContainerAlias,
                                                           boolean defaultStartContainer) {
        final ServerTemplate serverTemplate = serverTemplates.iterator().next();
        final Set<String> existingContainers = FluentIterable.from(serverTemplate.getContainersSpec()).transform(s -> s.getId()).toSet();
        if (existingContainers.contains(defaultContainerId) == false) {
            getSafeExecutedCommand(getBuildDeployProvisionCommand(DeploymentMode.VALIDATED,
                                                                  defaultContainerId,
                                                                  defaultContainerAlias,
                                                                  serverTemplate.getId(),
                                                                  defaultStartContainer)).execute();
        } else {
            deploymentScreenPopupView.setValidateExistingContainerCallback(containerName -> existingContainers.contains(containerName));
            deploymentScreenPopupView.setContainerId(defaultContainerId);
            deploymentScreenPopupView.setContainerAlias(defaultContainerAlias);
            deploymentScreenPopupView.setStartContainer(defaultStartContainer);
            deploymentScreenPopupView.configure(() -> {
                final String containerId = deploymentScreenPopupView.getContainerId();
                final String containerAlias = deploymentScreenPopupView.getContainerAlias();
                final boolean startContainer = deploymentScreenPopupView.getStartContainer();

                getSafeExecutedCommand(getBuildDeployProvisionCommand(DeploymentMode.VALIDATED,
                                                                      containerId,
                                                                      containerAlias,
                                                                      serverTemplate.getId(),
                                                                      startContainer)).execute();

                deploymentScreenPopupView.hide();
            });
            deploymentScreenPopupView.show();
        }
    }

    private void buildDeployWithMultipleServerTemplatesAvailable(Collection<ServerTemplate> serverTemplates,
                                                                 String defaultContainerId,
                                                                 String defaultContainerAlias,
                                                                 boolean defaultStartContainer) {
        final Map<String, ServerTemplate> serverTemplatesIds = Maps.uniqueIndex(serverTemplates,
                                                                                s -> s.getId());
        final Map<String, Set<String>> containerNames = Maps.transformEntries(serverTemplatesIds,
                                                                              (id, server) ->
                                                                                      FluentIterable.from(server.getContainersSpec()).transform(c -> c.getContainerName()).toSet()
        );
        deploymentScreenPopupView.addServerTemplates(FluentIterable.from(serverTemplatesIds.keySet()).toSortedSet(String.CASE_INSENSITIVE_ORDER));
        deploymentScreenPopupView.setValidateExistingContainerCallback(containerName -> FluentIterable.from(containerNames.get(deploymentScreenPopupView.getServerTemplate())).contains(containerName));
        deploymentScreenPopupView.setContainerId(defaultContainerId);
        deploymentScreenPopupView.setContainerAlias(defaultContainerAlias);
        deploymentScreenPopupView.setStartContainer(defaultStartContainer);
        deploymentScreenPopupView.configure(() -> {
            final String containerId = deploymentScreenPopupView.getContainerId();
            final String containerAlias = deploymentScreenPopupView.getContainerAlias();
            final String serverTemplate = deploymentScreenPopupView.getServerTemplate();
            final boolean startContainer = deploymentScreenPopupView.getStartContainer();

            getSafeExecutedCommand(getBuildDeployProvisionCommand(DeploymentMode.VALIDATED,
                                                                  containerId,
                                                                  containerAlias,
                                                                  serverTemplate,
                                                                  startContainer)).execute();

            deploymentScreenPopupView.hide();
        });
        deploymentScreenPopupView.show();
    }

    private Command getSafeExecutedCommand(final Command command) {
        return () -> {
            if (building) {
                view.showABuildIsAlreadyRunning();
            } else {
                command.execute();
            }
        };
    }

    private Command getBuildCommand() {
        return () -> {
            view.showBusyIndicator(ProjectEditorResources.CONSTANTS.Building());
            build();
        };
    }

    private void build() {
        building = true;
        buildServiceCaller.call(getBuildSuccessCallback(),
                                new BuildFailureErrorCallback(view,
                                                              Collections.EMPTY_MAP)).build(projectContext.getActiveProject());
    }

    private RemoteCallback getBuildSuccessCallback() {
        return new RemoteCallback<BuildResults>() {
            @Override
            public void callback(final BuildResults result) {
                if (result.getErrorMessages().isEmpty()) {
                    notificationEvent.fire(new NotificationEvent(ProjectEditorResources.CONSTANTS.BuildSuccessful(),
                                                                 NotificationEvent.NotificationType.SUCCESS));
                } else {
                    notificationEvent.fire(new NotificationEvent(ProjectEditorResources.CONSTANTS.BuildFailed(),
                                                                 NotificationEvent.NotificationType.ERROR));
                }
                buildResultsEvent.fire(result);
                view.hideBusyIndicator();

                building = false;
            }
        };
    }

    private class BuildFailureErrorCallback
            extends CommandWithThrowableDrivenErrorCallback {

        public BuildFailureErrorCallback(final HasBusyIndicator view,
                                         final Map<Class<? extends Throwable>, CommandWithThrowable> commands) {
            super(view,
                  commands);
        }

        @Override
        public boolean error(final Message message,
                             final Throwable throwable) {
            building = false;
            return super.error(message,
                               throwable);
        }
    }

    private Command getBuildDeployCommand(final DeploymentMode mode) {
        return () -> {
            view.showBusyIndicator(ProjectEditorResources.CONSTANTS.Building());
            buildAndDeploy(mode);
        };
    }

    private void buildAndDeploy(final DeploymentMode mode) {
        building = true;
        buildServiceCaller.call(getBuildSuccessCallback(),
                                new BuildFailureErrorCallback(view,
                                                              onBuildAndDeployGavExistsHandler)).buildAndDeploy(projectContext.getActiveProject(),
                                                                                                                mode);
    }

    private Command getBuildDeployProvisionCommand(final DeploymentMode mode,
                                                   final String containerId,
                                                   final String containerAlias,
                                                   final String serverTemplate,
                                                   final boolean startContainer) {
        return () -> {
            view.showBusyIndicator(ProjectEditorResources.CONSTANTS.Building());
            buildAndDeployAndProvision(mode,
                                       containerId,
                                       containerAlias,
                                       serverTemplate,
                                       startContainer);
        };
    }

    private void buildAndDeployAndProvision(final DeploymentMode mode,
                                            final String containerId,
                                            final String containerAlias,
                                            final String serverTemplate,
                                            final boolean startContainer) {

        building = true;
        buildServiceCaller.call(getBuildDeployProvisionSuccessCallback(containerId,
                                                                       containerAlias,
                                                                       serverTemplate,
                                                                       startContainer),
                                new BuildFailureErrorCallback(view,
                                                              getOnBuildAndDeployAndProvisionGavExistsHandler(containerId,
                                                                                                              containerAlias,
                                                                                                              serverTemplate,
                                                                                                              startContainer))).buildAndDeploy(projectContext.getActiveProject(),
                                                                                                                                               mode);
    }

    private RemoteCallback getBuildDeployProvisionSuccessCallback(final String containerId,
                                                                  final String containerAlias,
                                                                  final String serverTemplate,
                                                                  final boolean startContainer) {
        return new RemoteCallback<BuildResults>() {
            @Override
            public void callback(final BuildResults result) {
                if (result.getErrorMessages().isEmpty()) {
                    notificationEvent.fire(new NotificationEvent(ProjectEditorResources.CONSTANTS.BuildSuccessful(),
                                                                 NotificationEvent.NotificationType.SUCCESS));
                    if (containerId != null && serverTemplate != null) {
                        GAV gav = projectContext.getActiveProject().getPom().getGav();
                        ReleaseId releaseId = new ReleaseId(gav.getGroupId(),
                                                            gav.getArtifactId(),
                                                            gav.getVersion());
                        ContainerSpec containerSpec = new ContainerSpec(containerId,
                                                                        containerAlias,
                                                                        new ServerTemplateKey(serverTemplate,
                                                                                              serverTemplate),
                                                                        releaseId,
                                                                        KieContainerStatus.STOPPED,
                                                                        new HashMap<>());

                        specManagementService.call(aVoid -> {
                            notificationEvent.fire(new NotificationEvent(ProjectEditorResources.CONSTANTS.DeploySuccessful(),
                                                                         NotificationEvent.NotificationType.SUCCESS));

                            if (startContainer) {
                                specManagementService.call(aVoid1 -> {
                                                           }
                                ).startContainer(containerSpec);
                            }
                        }).saveContainerSpec(serverTemplate,
                                             containerSpec);
                    }
                } else {
                    notificationEvent.fire(new NotificationEvent(ProjectEditorResources.CONSTANTS.BuildFailed(),
                                                                 NotificationEvent.NotificationType.ERROR));
                }
                buildResultsEvent.fire(result);
                view.hideBusyIndicator();

                building = false;
            }
        };
    }

    private Map<Class<? extends Throwable>, CommandWithThrowableDrivenErrorCallback.CommandWithThrowable> getOnBuildAndDeployAndProvisionGavExistsHandler(final String containerId,
                                                                                                                                                          final String containerAlias,
                                                                                                                                                          final String serverTemplate,
                                                                                                                                                          final boolean startContainer) {
        return new HashMap<Class<? extends Throwable>, CommandWithThrowableDrivenErrorCallback.CommandWithThrowable>() {{
            put(GAVAlreadyExistsException.class,
                parameter -> {
                    view.hideBusyIndicator();
                    conflictingRepositoriesPopup.setContent(projectContext.getActiveProject().getPom().getGav(),
                                                            ((GAVAlreadyExistsException) parameter).getRepositories(),
                                                            () -> {
                                                                conflictingRepositoriesPopup.hide();
                                                                getBuildDeployProvisionCommand(DeploymentMode.FORCED,
                                                                                               containerId,
                                                                                               containerAlias,
                                                                                               serverTemplate,
                                                                                               startContainer).execute();
                                                            });
                    conflictingRepositoriesPopup.show();
                });
        }};
    }
}
