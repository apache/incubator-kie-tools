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

import java.util.Arrays;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

import javax.enterprise.event.Event;
import javax.inject.Inject;
import javax.validation.UnexpectedTypeException;

import org.guvnor.common.services.project.builder.model.BuildResults;
import org.guvnor.common.services.project.builder.service.BuildService;
import org.guvnor.common.services.project.client.context.WorkspaceProjectContext;
import org.guvnor.common.services.project.client.repositories.ConflictingRepositoriesPopup;
import org.guvnor.common.services.project.model.GAV;
import org.guvnor.common.services.project.model.MavenRepositoryMetadata;
import org.guvnor.common.services.project.model.Module;
import org.guvnor.common.services.project.service.DeploymentMode;
import org.guvnor.common.services.project.service.GAVAlreadyExistsException;
import org.jboss.errai.bus.client.api.messaging.Message;
import org.jboss.errai.common.client.api.Caller;
import org.jboss.errai.common.client.api.RemoteCallback;
import org.kie.server.api.model.KieContainerStatus;
import org.kie.server.api.model.KieScannerStatus;
import org.kie.server.api.model.ReleaseId;
import org.kie.server.controller.api.model.spec.Capability;
import org.kie.server.controller.api.model.spec.ContainerConfig;
import org.kie.server.controller.api.model.spec.ContainerSpec;
import org.kie.server.controller.api.model.spec.ContainerSpecKey;
import org.kie.server.controller.api.model.spec.ProcessConfig;
import org.kie.server.controller.api.model.spec.RuleConfig;
import org.kie.server.controller.api.model.spec.ServerTemplate;
import org.kie.server.controller.api.model.spec.ServerTemplateKey;
import org.kie.server.controller.api.model.spec.ServerTemplateList;
import org.kie.workbench.common.screens.projecteditor.client.editor.DeploymentScreenPopupViewImpl;
import org.kie.workbench.common.screens.server.management.model.MergeMode;
import org.kie.workbench.common.screens.server.management.model.RuntimeStrategy;
import org.kie.workbench.common.screens.server.management.service.SpecManagementService;
import org.kie.workbench.common.widgets.client.callbacks.CommandWithThrowableDrivenErrorCallback;
import org.kie.workbench.common.widgets.client.callbacks.CommandWithThrowableDrivenErrorCallback.CommandWithThrowable;
import org.uberfire.ext.widgets.common.client.common.HasBusyIndicator;
import org.uberfire.mvp.Command;
import org.uberfire.workbench.events.NotificationEvent;

import static org.kie.workbench.common.screens.projecteditor.client.resources.ProjectEditorResources.CONSTANTS;
import static org.uberfire.workbench.events.NotificationEvent.NotificationType.ERROR;
import static org.uberfire.workbench.events.NotificationEvent.NotificationType.SUCCESS;
import static org.uberfire.workbench.events.NotificationEvent.NotificationType.WARNING;

public class BuildExecutor {

    private DeploymentScreenPopupViewImpl deploymentScreenPopupView;

    private Caller<SpecManagementService> specManagementService;

    private Caller<BuildService> buildServiceCaller;

    private Event<BuildResults> buildResultsEvent;

    private Event<NotificationEvent> notificationEvent;

    private ConflictingRepositoriesPopup conflictingRepositoriesPopup;

    private WorkspaceProjectContext projectContext;

    private View view;

    private boolean building = false;

    @Inject
    public BuildExecutor(final DeploymentScreenPopupViewImpl deploymentScreenPopupView,
                         final Caller<SpecManagementService> specManagementService,
                         final Caller<BuildService> buildServiceCaller,
                         final Event<BuildResults> buildResultsEvent,
                         final Event<NotificationEvent> notificationEvent,
                         final ConflictingRepositoriesPopup conflictingRepositoriesPopup,
                         final WorkspaceProjectContext projectContext) {

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

    public void triggerBuild() {
        safeExecuted(buildCommand()).execute();
    }

    public void triggerBuildAndDeploy() {
        specManagementService.call((ServerTemplateList serverTemplates) -> {
            final int length = serverTemplates == null || serverTemplates.getServerTemplates() == null ? 0 : serverTemplates.getServerTemplates().length;
            switch (length) {
                case 0:
                    safeExecuted(buildDeployWithoutServerTemplateCommand(DeploymentMode.VALIDATED)).execute();
                    break;
                case 1:
                    buildDeployWithOneServerTemplate(serverTemplates.getServerTemplates()[0]);
                    break;
                default:
                    buildDeployWithMultipleServerTemplates(Arrays.asList(serverTemplates.getServerTemplates()));
                    break;
            }
        }).listServerTemplates();
    }

    private void buildDeployWithOneServerTemplate(ServerTemplate serverTemplate) {
        final Set<String> existingContainers = existingContainers(serverTemplate);

        if (!existingContainers.contains(defaultContainerId())) {
            safeExecuted(buildDeployProvision(DeploymentMode.VALIDATED,
                                              defaultContainerId(),
                                              defaultContainerAlias(),
                                              serverTemplate,
                                              true)).execute();
        } else {
            deploymentPopupBuilder()
                    .buildDeployWithOneServerTemplate(
                            serverTemplate,
                            view -> {
                                safeExecuted(buildDeployProvision(DeploymentMode.VALIDATED,
                                                                  view.getContainerId(),
                                                                  view.getContainerAlias(),
                                                                  serverTemplate,
                                                                  view.getStartContainer())).execute();
                            })
                    .show();
        }
    }

    private DeploymentPopupBuilder deploymentPopupBuilder() {
        return new DeploymentPopupBuilder(this);
    }

    Set<String> existingContainers(final ServerTemplate serverTemplate) {
        final Collection<ContainerSpec> containersSpec = serverTemplate.getContainersSpec();

        return containersSpec
                .stream()
                .map(ContainerSpecKey::getId)
                .collect(Collectors.toSet());
    }

    String defaultContainerAlias() {
        return projectGAV().getArtifactId();
    }

    String defaultContainerId() {
        return projectGAV().getArtifactId() + "_" + projectGAV().getVersion();
    }

    private void buildDeployWithMultipleServerTemplates(Collection<ServerTemplate> serverTemplates) {
        deploymentPopupBuilder()
                .buildDeployWithMultipleServerTemplates(
                        serverTemplates,
                        view -> {
                            final String serverTemplateId = view.getServerTemplate();
                            final ServerTemplate serverTemplate = serverTemplateById(serverTemplates,
                                                                                     serverTemplateId);

                            safeExecuted(buildDeployProvision(DeploymentMode.VALIDATED,
                                                              view.getContainerId(),
                                                              view.getContainerAlias(),
                                                              serverTemplate,
                                                              view.getStartContainer())).execute();
                        })
                .show();
    }

    private ServerTemplate serverTemplateById(final Collection<ServerTemplate> serverTemplates,
                                              final String serverTemplateId) {
        return serverTemplates
                .stream()
                .filter(s -> s.getId().equals(serverTemplateId))
                .findFirst()
                .orElseThrow(UnexpectedTypeException::new);
    }

    private Command safeExecuted(final Command command) {
        return () -> {
            if (building) {
                view.showABuildIsAlreadyRunning();
            } else {
                command.execute();
            }
        };
    }

    private Command buildCommand() {
        return () -> {
            view.showBusyIndicator(CONSTANTS.Building());
            building = true;

            buildServiceCaller.call((RemoteCallback<BuildResults>) result -> {

                if (result.getErrorMessages().isEmpty()) {
                    notificationEvent.fire(new NotificationEvent(CONSTANTS.BuildSuccessful(), SUCCESS));
                } else {
                    notificationEvent.fire(new NotificationEvent(CONSTANTS.BuildFailed(), ERROR));
                }

                buildResultsEvent.fire(result);
                view.hideBusyIndicator();
                building = false;
            }, new BuildFailureErrorCallback(view, new HashMap<>())).build(activeModule());
        };
    }

    private Command buildDeployWithoutServerTemplateCommand(final DeploymentMode mode) {
        return () -> {
            view.showBusyIndicator(CONSTANTS.Building());
            building = true;

            buildServiceCaller.call((RemoteCallback<BuildResults>) result -> {

                if (result.getErrorMessages().isEmpty()) {
                    notificationEvent.fire(new NotificationEvent(CONSTANTS.BuildSuccessful(), SUCCESS));
                    notificationEvent.fire(new NotificationEvent(CONSTANTS.DeploymentSkippedDueToNoServerTemplateConfigured(), WARNING));
                } else {
                    notificationEvent.fire(new NotificationEvent(CONSTANTS.BuildFailed(), ERROR));
                }

                buildResultsEvent.fire(result);
                view.hideBusyIndicator();
                building = false;
            }, new BuildFailureErrorCallback(view, onBuildAndDeployGavExistsHandler())).buildAndDeploy(activeModule(), mode);
        };
    }

    private Command buildDeployProvision(final DeploymentMode mode,
                                         final String containerId,
                                         final String containerAlias,
                                         final ServerTemplate serverTemplate,
                                         final Boolean startContainer) {
        return () -> {
            view.showBusyIndicator(CONSTANTS.Building());
            building = true;

            buildServiceCaller.call(onBuildDeployProvisionSuccess(containerId,
                                                                  containerAlias,
                                                                  serverTemplate,
                                                                  startContainer),
                                    new BuildFailureErrorCallback(view,
                                                                  getOnBuildAndDeployAndProvisionGavExistsHandler(containerId,
                                                                                                                  containerAlias,
                                                                                                                  serverTemplate,
                                                                                                                  startContainer))).buildAndDeploy(activeModule(),
                                                                                                                                                   mode);
        };
    }

    private RemoteCallback onBuildDeployProvisionSuccess(final String containerId,
                                                         final String containerAlias,
                                                         final ServerTemplate serverTemplate,
                                                         final boolean startContainer) {
        return (RemoteCallback<BuildResults>) result -> {

            if (result.getErrorMessages().isEmpty()) {
                notificationEvent.fire(new NotificationEvent(CONSTANTS.BuildSuccessful(), SUCCESS));

                if (containerId != null && serverTemplate != null && serverTemplate.getId() != null) {
                    saveContainerSpecAndMaybeStartContainer(containerId,
                                                            containerAlias,
                                                            serverTemplate,
                                                            startContainer,
                                                            result.getParameters());
                }
            } else {
                notificationEvent.fire(new NotificationEvent(CONSTANTS.BuildFailed(), ERROR));
            }

            buildResultsEvent.fire(result);
            view.hideBusyIndicator();
            building = false;
        };
    }

    private void saveContainerSpecAndMaybeStartContainer(final String containerId,
                                                         final String containerAlias,
                                                         final ServerTemplate serverTemplate,
                                                         final Boolean startContainer,
                                                         final Map<String, String> parameters) {

        final ContainerSpec containerSpec = makeContainerSpec(containerId,
                                                              containerAlias,
                                                              serverTemplate,
                                                              parameters);

        specManagementService.call(ignore -> {

            if (!startContainer) {
                notificationEvent.fire(new NotificationEvent(CONSTANTS.DeploySuccessful(), SUCCESS));
                return;
            }

            specManagementService.call(ignore2 -> {
                notificationEvent.fire(new NotificationEvent(CONSTANTS.DeploySuccessfulAndContainerStarted(), SUCCESS));
            }, (o, throwable) -> {
                notificationEvent.fire(new NotificationEvent(CONSTANTS.DeploySuccessfulButContainerFailedToStart(), WARNING));
                return false;
            }).startContainer(containerSpec);

        }, (o, throwable) -> {
            notificationEvent.fire(new NotificationEvent(CONSTANTS.DeployFailed(), ERROR));
            return false;
        }).saveContainerSpec(serverTemplate.getId(), containerSpec);
    }

    private ContainerSpec makeContainerSpec(final String containerId,
                                            final String containerAlias,
                                            final ServerTemplate serverTemplate,
                                            final Map<String, String> parameters) {

        final ReleaseId releaseId = makeReleaseId();
        final KieContainerStatus status = KieContainerStatus.STOPPED;
        final ServerTemplateKey serverTemplateKey = new ServerTemplateKey(serverTemplate.getId(),
                                                                          serverTemplate.getId());

        return new ContainerSpec(containerId,
                                 containerAlias,
                                 serverTemplateKey,
                                 releaseId,
                                 status,
                                 makeConfigs(serverTemplate,
                                             parameters));
    }

    private ReleaseId makeReleaseId() {
        final GAV gav = projectGAV();

        return new ReleaseId(gav.getGroupId(),
                             gav.getArtifactId(),
                             gav.getVersion());
    }

    Map<Capability, ContainerConfig> makeConfigs(final ServerTemplate serverTemplate,
                                                 final Map<String, String> parameters) {
        final Map<Capability, ContainerConfig> configs = new HashMap<>();

        if (hasProcessCapability(serverTemplate)) {
            configs.put(Capability.PROCESS,
                        makeProcessConfig(parameters));
        }

        configs.put(Capability.RULE,
                    makeRuleConfig());

        return configs;
    }

    RuleConfig makeRuleConfig() {
        return new RuleConfig(null,
                              KieScannerStatus.STOPPED);
    }

    ProcessConfig makeProcessConfig(final Map<String, String> parameters) {
        String strategy = parameters.getOrDefault("RuntimeStrategy", RuntimeStrategy.SINGLETON.name());

        return new ProcessConfig(strategy,
                                 "",
                                 "",
                                 MergeMode.MERGE_COLLECTIONS.name());
    }

    boolean hasProcessCapability(final ServerTemplate serverTemplate) {
        final List<String> capabilities = serverTemplate.getCapabilities();
        final String process = Capability.PROCESS.name();

        return capabilities.contains(process);
    }

    private Map<Class<? extends Throwable>, CommandWithThrowable> getOnBuildAndDeployAndProvisionGavExistsHandler(final String containerId,
                                                                                                                  final String containerAlias,
                                                                                                                  final ServerTemplate serverTemplate,
                                                                                                                  final boolean startContainer) {
        return new HashMap<Class<? extends Throwable>, CommandWithThrowable>() {{
            put(GAVAlreadyExistsException.class, e -> showConflictingRepositoriesPopup((GAVAlreadyExistsException) e, () -> {
                conflictingRepositoriesPopup.hide();

                buildDeployProvision(DeploymentMode.FORCED,
                                     containerId,
                                     containerAlias,
                                     serverTemplate,
                                     startContainer).execute();
            }));
        }};
    }

    private HashMap<Class<? extends Throwable>, CommandWithThrowable> onBuildAndDeployGavExistsHandler() {
        return new HashMap<Class<? extends Throwable>, CommandWithThrowable>() {{
            put(GAVAlreadyExistsException.class, e -> showConflictingRepositoriesPopup((GAVAlreadyExistsException) e, () -> {
                conflictingRepositoriesPopup.hide();
                buildDeployWithoutServerTemplateCommand(DeploymentMode.FORCED).execute();
            }));
        }};
    }

    private void showConflictingRepositoriesPopup(final GAVAlreadyExistsException parameter,
                                                  final Command command) {
        final Set<MavenRepositoryMetadata> repositories = parameter.getRepositories();

        view.hideBusyIndicator();

        conflictingRepositoriesPopup.setContent(projectGAV(),
                                                repositories,
                                                command);
        conflictingRepositoriesPopup.show();
    }

    private Module activeModule() {
        return projectContext.getActiveModule()
                             .orElseThrow(() -> new IllegalStateException("Cannot perform build without active module."));
    }

    private GAV projectGAV() {
        return activeModule().getPom().getGav();
    }

    DeploymentScreenPopupViewImpl getDeploymentScreenPopupViewImpl() {
        return deploymentScreenPopupView;
    }

    public interface View extends HasBusyIndicator {

        void showABuildIsAlreadyRunning();
    }

    private class BuildFailureErrorCallback
            extends CommandWithThrowableDrivenErrorCallback {

        BuildFailureErrorCallback(final HasBusyIndicator view,
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
}
