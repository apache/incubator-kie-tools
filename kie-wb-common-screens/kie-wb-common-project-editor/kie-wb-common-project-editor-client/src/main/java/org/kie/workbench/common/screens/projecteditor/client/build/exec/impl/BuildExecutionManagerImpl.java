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

package org.kie.workbench.common.screens.projecteditor.client.build.exec.impl;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

import javax.annotation.PostConstruct;
import javax.enterprise.context.Dependent;
import javax.enterprise.event.Event;
import javax.inject.Inject;

import org.guvnor.common.services.project.builder.model.BuildResults;
import org.guvnor.common.services.project.builder.service.BuildService;
import org.guvnor.common.services.project.client.repositories.ConflictingRepositoriesPopup;
import org.jboss.errai.common.client.api.Caller;
import org.kie.workbench.common.screens.projecteditor.build.exec.SnapshotDeploymentSettings;
import org.kie.workbench.common.screens.projecteditor.client.build.exec.BuildExecutionContext;
import org.kie.workbench.common.screens.projecteditor.client.build.exec.BuildExecutionManager;
import org.kie.workbench.common.screens.projecteditor.client.build.exec.BuildType;
import org.kie.workbench.common.screens.projecteditor.client.build.exec.dialog.BuildDialog;
import org.kie.workbench.common.screens.projecteditor.client.build.exec.impl.executors.Executor;
import org.kie.workbench.common.screens.projecteditor.client.build.exec.impl.executors.build.BuildExecutor;
import org.kie.workbench.common.screens.projecteditor.client.build.exec.impl.executors.deploy.DefaultBuildAndDeployExecutor;
import org.kie.workbench.common.screens.projecteditor.client.build.exec.impl.executors.deploy.SnapshotBuildAndDeployExecutor;
import org.kie.workbench.common.screens.projecteditor.client.build.exec.impl.executors.install.DefaultInstallExecutor;
import org.kie.workbench.common.screens.projecteditor.client.build.exec.impl.executors.install.SnapshotInstallExecutor;
import org.kie.workbench.common.screens.projecteditor.client.build.exec.impl.executors.redeploy.SnapshotRedeployExecutor;
import org.kie.workbench.common.screens.projecteditor.client.build.exec.impl.executors.validators.DefaultContextValidator;
import org.kie.workbench.common.screens.projecteditor.client.build.exec.impl.executors.validators.SnapshotContextValidator;
import org.kie.workbench.common.screens.projecteditor.client.editor.DeploymentPopup;
import org.kie.workbench.common.screens.server.management.service.SpecManagementService;
import org.uberfire.workbench.events.NotificationEvent;

@Dependent
public class BuildExecutionManagerImpl implements BuildExecutionManager {

    private final Caller<BuildService> buildServiceCaller;
    private final Event<BuildResults> buildResultsEvent;
    private final Event<NotificationEvent> notificationEvent;
    private final BuildDialog buildDialog;
    private final DeploymentPopup deploymentPopup;
    private final Caller<SpecManagementService> specManagementService;
    private final ConflictingRepositoriesPopup conflictingRepositoriesPopup;
    private final SnapshotDeploymentSettings settings;

    protected Map<BuildType, Executor> defaultRunners;
    protected Map<BuildType, Executor> snapshotRunners;

    BuildExecutionManagerImpl() {
        // Constructor for testing purposes
        this(null, null, null, null, null, null, null, null);
    }

    @Inject
    public BuildExecutionManagerImpl(final Caller<BuildService> buildServiceCaller,
                                     final Event<BuildResults> buildResultsEvent,
                                     final Event<NotificationEvent> notificationEvent,
                                     final BuildDialog buildDialog,
                                     final DeploymentPopup deploymentPopup,
                                     final Caller<SpecManagementService> specManagementService,
                                     final ConflictingRepositoriesPopup conflictingRepositoriesPopup,
                                     final SnapshotDeploymentSettings settings) {
        this.buildServiceCaller = buildServiceCaller;
        this.buildResultsEvent = buildResultsEvent;
        this.notificationEvent = notificationEvent;
        this.buildDialog = buildDialog;
        this.deploymentPopup = deploymentPopup;
        this.specManagementService = specManagementService;
        this.conflictingRepositoriesPopup = conflictingRepositoriesPopup;
        this.settings = settings;
    }

    @PostConstruct
    protected void init() {
        defaultRunners = new HashMap<>();
        defaultRunners.put(BuildType.BUILD, new BuildExecutor(buildServiceCaller, buildResultsEvent, notificationEvent, buildDialog, new DefaultContextValidator()));
        defaultRunners.put(BuildType.DEPLOY, new DefaultBuildAndDeployExecutor(buildServiceCaller, buildResultsEvent, notificationEvent, buildDialog, deploymentPopup, specManagementService, conflictingRepositoriesPopup));
        defaultRunners.put(BuildType.INSTALL, new DefaultInstallExecutor(buildServiceCaller, buildResultsEvent, notificationEvent, buildDialog, conflictingRepositoriesPopup));

        settings.load();

        snapshotRunners = new HashMap<>();
        snapshotRunners.put(BuildType.BUILD, new BuildExecutor(buildServiceCaller, buildResultsEvent, notificationEvent, buildDialog, new SnapshotContextValidator()));
        snapshotRunners.put(BuildType.DEPLOY, new SnapshotBuildAndDeployExecutor(buildServiceCaller, buildResultsEvent, notificationEvent, buildDialog, deploymentPopup, specManagementService, settings));
        snapshotRunners.put(BuildType.INSTALL, new SnapshotInstallExecutor(buildServiceCaller, buildResultsEvent, notificationEvent, buildDialog));
        snapshotRunners.put(BuildType.REDEPLOY, new SnapshotRedeployExecutor(buildServiceCaller, buildResultsEvent, notificationEvent, buildDialog, deploymentPopup, specManagementService, settings));
    }

    @Override
    public void execute(final BuildType buildType, final BuildExecutionContext context) {
        Map<BuildType, Executor> runners = context.getModule().getPom().getGav().isSnapshot() ? snapshotRunners : defaultRunners;
        execute(runners, buildType, context);
    }

    private void execute(final Map<BuildType, Executor> executionMap, final BuildType buildType, final BuildExecutionContext context) {
        Optional<Executor> optional = Optional.ofNullable(executionMap.get(buildType));

        if (optional.isPresent()) {
            optional.get().run(context);
        } else {
            throw new IllegalArgumentException("Cannot run " + buildType + " for module " + context.getModule().getPom().getGav().toString());
        }
    }
}
